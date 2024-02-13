package io.github.oclay1st.wfdb.formatters;

import io.github.oclay1st.wfdb.HeaderSignal;

/**
 * Represents the signal formatter for format 24.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter24 implements SignalFormatter {

    /**
     * {@inheritDoc}
     * Each sample is represented by a 24-bit two’s complement amplitude stored the
     * least significant byte first.
     */
    @Override
    public int[] convertBytesToSamples(byte[] source, HeaderSignal[] headerSignals) {
        int index = 0;
        int numberOfSamples = source.length / 3;
        int[] samples = new int[numberOfSamples];
        for (int i = 0; i < source.length; i += 3) {
            int firstByteUnsigned = source[i] & 0xFF;
            int secondByteUnsigned = source[i + 1] & 0xFF;
            int thirdByteUnsigned = source[i + 2] & 0xFF;
            int sample = (thirdByteUnsigned << 16) + (secondByteUnsigned << 8) + firstByteUnsigned;
            samples[index] = sample > 8388607 ? sample - 16777216 : sample;
            index++;
        }
        return samples;
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 24 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples, HeaderSignal[] headerSignals) {
        int index = 0;
        byte[] source = new byte[samples.length * 3];
        for (int i = 0; i < samples.length; i++) {
            source[index] = (byte) samples[i];
            source[index + 1] = (byte) (samples[i] >> 8);
            source[index + 2] = (byte) (samples[i] >> 16);
            index += 3;
        }
        return source;
    }

}
