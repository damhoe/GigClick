package com.damhoe.gigclick;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.nfc.FormatException;
import android.util.SparseArray;

import java.io.IOException;

/*
    This class is third party code written by Stephan Hoedtke.

 Use this class
 - to generate sound buffers, meaning sample data byte arrays that can be send to an audio track. These sound buffers will be kept in a cache.
 - to send sound buffers to an audio track to play music
 - use a cache (SparseArray) for sounds indexed by a number
 */
public class AudioGenerator {

    static final int SAMPLES_PER_MINUTE = 60 * 48000;
    private static final int SAMPLES_PER_SECOND = 48000;
    private final Context context;
    private AudioTrack player;
    private final SparseArray<byte[]> cache = new SparseArray<>();

    AudioGenerator(Context context) {
        this.context = context;
    }

    void createSoundBuffer(int newKey, int resourceId) throws IOException, FormatException {
        final byte[] sound =  AudioWaveDecoder.getSamplesFromWaveResource(context, resourceId);
        cache.append(newKey, sound);
    }

    void createJoinedSoundBuffer(int newKey, int key1, int key2) throws IOException, FormatException {
        final byte[] sound1 = cache.get(key1);
        final byte[] sound2 = cache.get(key2);
        final byte[] sound3 = joinSounds(sound1, sound2);
        cache.append(newKey, sound3);
    }

    void createJoinedSoundBuffer(int newKey, int key1, int key2, int key3) throws IOException, FormatException {
        final byte[] sound1 = cache.get(key1);
        final byte[] sound2 = cache.get(key2);
        final byte[] sound3 = cache.get(key3);
        final byte[] sound4 = joinSounds(sound1, sound2);
        final byte[] sound5 = joinSounds(sound4, sound3);
        cache.append(newKey, sound5);
    }

    void createJoinedSoundBuffer(int newKey, int key1, int key2, int key3, int key4) throws IOException, FormatException {
        final byte[] sound1 = cache.get(key1);
        final byte[] sound2 = cache.get(key2);
        final byte[] sound3 = cache.get(key3);
        final byte[] sound4 = cache.get(key4);
        final byte[] sound5 = joinSounds(sound1, sound2);
        final byte[] sound6 = joinSounds(sound5, sound3);
        final byte[] sound7 = joinSounds(sound6, sound4);
        cache.append(newKey, sound7);
    }

    void createJoinedSoundBuffer(int newKey, int key1, int key2, int key3, int key4, int key5) throws IOException, FormatException {
        final byte[] sound1 = cache.get(key1);
        final byte[] sound2 = cache.get(key2);
        final byte[] sound3 = cache.get(key3);
        final byte[] sound4 = cache.get(key4);
        final byte[] sound5 = cache.get(key5);
        final byte[] sound6 = joinSounds(sound1, sound2);
        final byte[] sound7 = joinSounds(sound6, sound3);
        final byte[] sound8 = joinSounds(sound7, sound4);
        final byte[] sound9 = joinSounds(sound8, sound5);
        cache.append(newKey, sound9);
    }

    boolean hasKey(int key) {
        return (cache.get(key) != null);
    }

    /*
      send media samples of the respective sound (stored in the sound map) to media player and return number of samples
     */
    int playSound(int key) {
        final byte[] sound = cache.get(key);
        writeBuffer(sound);
        return sound.length / 2;
    }

    void create() {
        int minSize = AudioTrack.getMinBufferSize(SAMPLES_PER_SECOND, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);
        player = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                SAMPLES_PER_SECOND,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                minSize,
                AudioTrack.MODE_STREAM);
        player.play();
    }

    void release() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    void playSilence(int samples) {
        int sizeInBytes = 2 * samples;
        writeBuffer(createSilenceBuffer(sizeInBytes));
    }

    private void writeBuffer(byte[] buffer) {
        player.write(buffer, 0, buffer.length);
    }

    private byte[] createSilenceBuffer(int sizeInBytes) {
        final byte[] buffer = new byte[sizeInBytes];
        for (int i=0; i < sizeInBytes; i++) {
            buffer[i++] = 0;
        }
        return buffer;
    }

    private byte[] joinSounds(byte[] a, byte[] b) {
        if (a.length <= b.length)
            return joinShorterWithLongerSound(a, b);
        else
            return joinShorterWithLongerSound(b, a);
    }

    /*
        assumes shorter.length <= longer.length
     */
    private byte[] joinShorterWithLongerSound(byte[] shorter, byte[] longer) {
        final int min = shorter.length;
        final int max = longer.length;
        final byte[] buffer = new byte[max];
        for (int i=0, j=1; j < min; i += 2, j += 2) {
            // LITTLE ENDIAN: 0x0345 --> [0x45] [0x03]
            final int x = (short)((0xFF00&(shorter[j] << 8)) | (0x00FF&shorter[i]));
            final int y = (short)(((0xFF00&(longer[j] << 8)) | (0x00FF&longer[i])));
            final int z = Math.min(32000, Math.max(-32000, (int)x + (int)y));
            buffer[i] = (byte)(z & 0x000000FF);
            buffer[j] = (byte)((z & 0x0000FF00) >> 8);
        }
        System.arraycopy(longer, min, buffer, min, max - min);
        return buffer;
    }
}

