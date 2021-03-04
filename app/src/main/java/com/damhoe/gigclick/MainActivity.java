package com.damhoe.gigclick;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.nfc.FormatException;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CHANNEL_ID = "PLAYER_STATE_CHANNEL";
    private static final int FRAGMENT_TRANSITION_DELAY = 280;

    private MainViewModel viewModel;
    private AudioPlayer player;
    private AppBarConfiguration appBarConfiguration;
    private DrawerLayout drawer;
    private Handler handler;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        drawer = findViewById(R.id.drawer_layout);

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_metronome, R.id.navigation_live, R.id.navigation_library)
                .setOpenableLayout(drawer)
                .build();
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(navView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        handler = new Handler();
        navView.setNavigationItemSelectedListener(this);

        //calling sync state is necessary or else your hamburger icon wont show up
        createNotificationChannel();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getRunStateLD().observe(this, isRunning -> {
            if (isRunning) {
                start();
            } else {
                stop();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(findNavController(), appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private NavController findNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, findNavController())
                || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.setRunningState(false);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void start() {
        startPlayerThread();

        // show a Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_metronome_primary_24dp)
                .setContentTitle("Metronome")
                .setContentText("Player is running...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        int notificationId = 0x00;

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        // manager.notify(notificationId, builder.build());
    }

    private void startPlayerThread() {
        new Thread(new Runnable() {

            private int position = 0;
            private int bar = 0;

            @SuppressWarnings("ConstantConditions")
            public void run() {
                try {
                    player = new AudioPlayer(MainActivity.this);
                    player.prepareAudio();
                    while (viewModel.getRunStateLD().getValue()) {
                        beat();
                        updateUI();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (player != null) {
                        player.release();
                        player = null;
                    }
                }
            }

            /*
              Note, the audio device "blocks" the write request for the time that the buffer is full.
              Therefore we can write continously here in that background thread.
              */
            private void beat() throws IOException, FormatException {
                final Track track = viewModel.getTrack();
                PracticeOptions pOptions = track.getpOptions();

                final ArrayList<Beat> beats = viewModel.getBeats();
                int samples = viewModel.getNumberSamplesPerSplittedBeat();

                int key = beats.get(position).getAccent();

                if (pOptions.isMuted() && pOptions.isIdxMuted(bar)) {
                    key = Beat.ACCENT_OFF;
                }

                viewModel.setFlashLD(position);
                player.play(key, samples);
                position++;
                if (position >= viewModel.getBeats().size()) {
                    position = 0;
                    bar++;
                    if (bar > pOptions.getnBars()) {
                        viewModel.setRunningState(false);
                    }

                    if (pOptions.isSpeed() && pOptions.isSpeedUp(bar)) {
                        viewModel.speedUp(pOptions.getDeltaSpeed());
                    }
                }


            }

            /*
              Note, the audio device plays the sound with a small delay.
              Therefore we should expect that updateUI and playing the playTick are NOT at the exactly same moment.
              We may play around with the audio buffer size.
             */
            private void updateUI() {
                // ignore.
            }
        }).start();
    }

    private void stop() {
        // ignore.
        if (player != null) {
            player.release();
        }
        player = null;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        @IdRes
        int id = item.getItemId();
        NavOptions.Builder optionsBuilder = new NavOptions.Builder();

        switch (id) {
            case R.id.navigation_metronome: {
                // Lets assume for the first menu item navigation is default
                optionsBuilder
                        .setEnterAnim(R.anim.fade_in)
                        .setExitAnim(R.anim.exit_to_bottom)
                        .setPopEnterAnim(R.anim.fade_out)
                        .setPopExitAnim(R.anim.fade_in);
                }
                break;
            case R.id.navigation_live:
            case R.id.navigation_library: {
                // Lets assume for the third menu item navigation is custom
                optionsBuilder
                        .setEnterAnim(R.anim.enter_from_bottom)
                        .setExitAnim(R.anim.fade_out)
                        .setPopEnterAnim(R.anim.fade_in)
                        .setPopExitAnim(R.anim.exit_to_bottom);
                }
                break;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findNavController().navigate(id, null, optionsBuilder.build());
            }
        }, FRAGMENT_TRANSITION_DELAY);

        drawer.closeDrawer(GravityCompat.START);

        return super.onOptionsItemSelected(item);
    }
}
