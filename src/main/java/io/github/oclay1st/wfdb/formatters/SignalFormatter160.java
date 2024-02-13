package io.github.oclay1st.wfdb.formatters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import io.github.oclay1st.wfdb.HeaderSignal;

/**
 * Represents the signal formatter for format 160
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter160 implements SignalFormatter {

    /**
     * {@inheritDoc}
     * Each sample is represented by a 16-bit amplitude in offset binary form (i.e.,
     * 32768 must be subtracted from each unsigned byte pair to obtain a signed
     * 16-bit amplitude). As for format 16, the least significant byte of each pair
     * is first.
     */
    @Override
    public int[] convertBytesToSamples(byte[] source, HeaderSignal[] headerSignals) {
        int index = 0;
        int numberOfSamples = source.length / 2;
        int[] samples = new int[numberOfSamples];
        ShortBuffer buffer = ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        while (buffer.hasRemaining()) {
            samples[index] = buffer.get() - 32768;
            index++;
        }
        return samples;
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 160 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples, HeaderSignal[] headerSignals) {
        short[] shortSamples = new short[samples.length];
        for (int i = 0; i < samples.length; i++) {
            shortSamples[i] = (short) (samples[i] + 32768);
        }
        ByteBuffer buffer = ByteBuffer.allocate(samples.length * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.asShortBuffer().put(shortSamples);
        return buffer.array();
    }

}
