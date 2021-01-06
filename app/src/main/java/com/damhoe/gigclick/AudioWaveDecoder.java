package com.damhoe.gigclick;


import android.content.Context;
import android.nfc.FormatException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/*
* This class is third party code written by Stephan Hoedtke.
*
*/

public class AudioWaveDecoder {

    public static byte[] getSamplesFromWaveResource(Context context, int id) throws IOException, FormatException {
        ByteBuffer buffer = getBytesFromWaveResource(context, id);
        return getWaveDataBuffer(buffer);
    }

    private static ByteBuffer getBytesFromWaveResource(Context context, int id) throws IOException {
        InputStream inputStream = context.getResources().openRawResource(id);
        ByteBuffer buffer = ByteBuffer.allocate(inputStream.available());
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        inputStream.read(buffer.array(), 0, buffer.capacity());
        inputStream.close();return buffer;
    }

    /*
      Confirm that the expected wave parameters are use:
        sample rate = 48000
        16 bit per sample
        1 channel
        PCM encoding
      throw an exception, if not, and return the data length...
     */
    private static byte[] getWaveDataBuffer(ByteBuffer buffer) throws FormatException {

        buffer.position(0);
        int riff = buffer.getInt();
        confirmWaveFormat(riff == 0x46464952, "Invalid audio, missing RIFF header bytes.");

        buffer.position(8);
        int wave = buffer.getInt();
        confirmWaveFormat(wave == 0x45564157, "Invalid audio, missing WAVE header bytes.");

        buffer.position(20);
        int format = buffer.getShort();
        confirmWaveFormat(format == 1, "Unsupported audio encoding: " + format + ", expected: 1(PCM)");

        int channels = buffer.getShort();
        confirmWaveFormat(channels == 1, "Unsupported audio channels: " + channels + ", expected: 1");

        int rate = buffer.getInt();
        confirmWaveFormat(rate == 48000 || rate == 44100, "Unsupported audio sample rate: " + rate + ", expected: 48000");

        buffer.position(buffer.position() + 6);
        int bits = buffer.getShort();
        confirmWaveFormat(bits == 16, "Unsupported bits per sample: " + bits + ", expected: 16");

        int tag = 0;
        while (tag != 0x61746164) {
            tag = buffer.getInt(); // Looking for "DATA"
        }

        int size = buffer.getInt();
        byte[] data = new byte[size];
        buffer.get(data, 0, size);

        return data;
    }

    private static void confirmWaveFormat(boolean condition, String description) throws FormatException {
        if (!condition) {
            throw new FormatException(description);
        }
    }
}
