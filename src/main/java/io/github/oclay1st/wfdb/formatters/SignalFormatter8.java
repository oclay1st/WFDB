package io.github.oclay1st.wfdb.formatters;

import java.util.Objects;

/**
 * Represents the signal formatter for format 8.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter8 implements SignalFormatter {

    private int[] initialSignalSamples;

    /**
     * {@inheritDoc}
     * Each sample is represented as an 8-bit first difference; i.e., to get the
     * value of sample n, sum the first n bytes of the sample data file together
     * with the initial value from the header file. When format 8 files are created,
     * first differences which cannot be represented in 8 bits are represented
     * instead by the largest difference of the appropriate sign (-128 or +127), and
     * subsequent differences are adjusted such that the correct amplitude is
     * obtained as quickly as possible. Thus, there may be loss of information if
     * signals in another of the formats listed below are converted to format 8.
     * Note that the first differences stored in multiplexed format 8 files are
     * always determined by subtraction of successive samples from the same signal
     * (otherwise signals with baselines which differ by 128 units or more could not
     * be represented this way).
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        Objects.requireNonNull(initialSignalSamples);
        int[] samples = new int[source.length];
        samples[0] = source[0];
        for (int i = 1; i < source.length; i++) {
            samples[i] = samples[i - 1] + source[i];
        }
        for (int i = 0; i < initialSignalSamples.length; i++) {
            for (int j = i; j < samples.length; j += initialSignalSamples.length) {
                samples[j] = samples[j] + initialSignalSamples[i];
            }
        }
        return samples;
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 8 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        Objects.requireNonNull(initialSignalSamples);
        byte[] source = new byte[samples.length];
        int[] intermediateSamples = new int[samples.length];
        for (int i = 0; i < initialSignalSamples.length; i++) {
            for (int j = i; j < samples.length; j += initialSignalSamples.length) {
                intermediateSamples[j] = samples[j] - initialSignalSamples[i];
            }
        }
        source[0] = (byte) intermediateSamples[0];
        for (int i = intermediateSamples.length - 1; i > 0; i--) {
            source[i] = (byte) (intermediateSamples[i] - intermediateSamples[i - 1]);
        }
        return source;
    }

    /**
     * Set the header signals.
     * 
     * @param initialSignalSamples the new header signals array
     */
    public void setInitialSignalSamples(int[] initialSignalSamples) {
        this.initialSignalSamples = initialSignalSamples;
    }

}
