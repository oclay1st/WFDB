package io.github.oclay1st.wfdb.formatters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import io.github.oclay1st.wfdb.Util;

/**
 * Represents the signal formatter for format 61.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter61 implements SignalFormatter {

    /**
     * {@inheritDoc}
     * Each sample is represented by a 16-bit two’s complement amplitude stored most
     * significant byte first.
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        short[] values = new short[source.length / 2];
        ByteBuffer.wrap(source).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(values);
        return Util.castArray(values);
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 61 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        short[] shortSamples = Util.castArray(samples);
        ByteBuffer buffer = ByteBuffer.allocate(samples.length * 2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asShortBuffer().put(shortSamples);
        return buffer.array();
    }

}
