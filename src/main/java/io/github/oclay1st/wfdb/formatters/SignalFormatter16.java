package io.github.oclay1st.wfdb.formatters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import io.github.oclay1st.wfdb.utils.CommonUtil;

/**
 * Represents the signal formatter for format 16.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter16 implements SignalFormatter {

    /**
     * {@inheritDoc}
     * Each sample is represented by a 16-bit two’s complement amplitude stored the
     * least significant byte first. Any unused high-order bits are sign-extended
     * from the most significant bit.
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        ShortBuffer buffer = ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        return CommonUtil.convertShortBufferToIntArray(buffer); 
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 16 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        short[] shortSamples = CommonUtil.convertArray(samples);
        ByteBuffer buffer = ByteBuffer.allocate(samples.length * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.asShortBuffer().put(shortSamples);
        return buffer.array();
    }

}
