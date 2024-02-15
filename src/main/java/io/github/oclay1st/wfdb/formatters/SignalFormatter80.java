package io.github.oclay1st.wfdb.formatters;

/**
 * Represents the signal formatter for format 80.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter80 implements SignalFormatter {

    /**
     * {@inheritDoc}
     * Each sample is represented by an 8-bit amplitude in offset binary form (i.e.,
     * 128 must be subtracted from each unsigned byte to obtain a signed 8-bit
     * amplitude).
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        int[] samples = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            samples[i] = (source[i] & 0xFF) - 128;
        }
        return samples;
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 80 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        byte[] source = new byte[samples.length];
        for (int i = 0; i < samples.length; i++) {
            source[i] = (byte) (samples[i] + 128);
        }
        return source;
    }

}
