package io.github.oclay1st.wfdb.formatters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Represents the signal formatter for format 32.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter32 implements SignalFormatter {

    /**
     * Each sample is represented by a 32-bit two’s complement amplitude stored the
     * least significant byte first.
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        int numberOfSamples = source.length / 4;
        int[] samples = new int[numberOfSamples];
        ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(samples);
        return samples;
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 32 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        ByteBuffer buffer = ByteBuffer.allocate(samples.length * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.asIntBuffer().put(samples);
        return buffer.array();
    }

}
