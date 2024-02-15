package io.github.oclay1st.wfdb.formatters;

import java.util.Arrays;

/**
 * Represents the signal formatter for format 212.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter212 implements SignalFormatter {

    private static final int DISTRIBUTION = 3; // 3 bytes for each 2 samples

    private static final int SAMPLES_PER_DISTRIBUTION = 2; // 2 samples in 3 bytes

    /**
     * {@inheritDoc}
     * Each sample is represented by a 12-bit two’s complement amplitude. The first
     * sample is obtained from the 12 least significant bits of the first byte pair
     * (stored the least significant byte first). The second sample is formed from the 4
     * remaining bits of the first byte pair (which are the 4 high bits of the
     * 12-bit sample) and the next byte (which contains the remaining 8 bits of the
     * second sample). The process is repeated for each successive pair of samples.
     *
     * NOTE: In the current implementation two extra values is added to the data
     * array in order to support odd samples of data,
     *
     * <pre>
     * Given 3 unsigned bytes : represented as 244 15 78.
     *
     * 244 -> 1 1 1 1 0 1 0 0
     * 15 -> 0 0 0 0 1 1 1 1
     * 78 -> 0 1 0 0 1 1 1 0
     * Then :
     * First sample: 0 0 0 0 [ 1 1 1 1 | 1 1 1 1 0 1 0 0 ] -> 4084 or -12
     * Second sample: 0 0 0 0 | 0 1 0 0 1 1 1 0 -> 78
     * </pre>
     * 
     * Two complement form where values greater than 2^11 - 1 = 2047 are negative
     * 12 bits goes 0 to 4096 for unsigned and -2048 to 2047 for signed
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        int numberOfSamples = source.length - (Math.round(source.length / (float) DISTRIBUTION));
        byte[] data = Arrays.copyOf(source, source.length + (DISTRIBUTION - source.length % DISTRIBUTION));
        int[] samples = new int[numberOfSamples + SAMPLES_PER_DISTRIBUTION];
        int sampleIndex = 0;
        for (int i = 0; i < data.length; i += DISTRIBUTION) {
            // first sample
            int firstByteUnsigned = data[i] & 0xFF;
            int secondByteUnsigned = data[i + 1] & 0xFF;
            int lastFourBitsOfSecondByte = secondByteUnsigned & 0x0F;
            int firstSample = (lastFourBitsOfSecondByte << 8) + firstByteUnsigned;
            // Convert to two complement amplitude
            samples[sampleIndex] = firstSample > 2047 ? firstSample - 4096 : firstSample;
            // second sample
            int thirdByteUnsigned = data[i + 2] & 0xFF;
            int firstFourBitsOfSecondByte = secondByteUnsigned >> 4;
            int secondSample = (firstFourBitsOfSecondByte << 8) + thirdByteUnsigned;
            // Convert to two complement amplitude
            samples[sampleIndex + 1] = secondSample > 2047 ? secondSample - 4096 : secondSample;
            // increment array index
            sampleIndex += SAMPLES_PER_DISTRIBUTION;
        }
        return Arrays.copyOf(samples, numberOfSamples);
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 212 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        int sourceIndex = 0;
        int numberOfBytes = samples.length + (Math.round(samples.length / (float) DISTRIBUTION));
        byte[] source = new byte[numberOfBytes];
        for (int i = 0; i < samples.length; i += SAMPLES_PER_DISTRIBUTION) {
            int firstSampleUnsigned = samples[i] & 0x7FF;
            int secondSampleUnsigned = samples[i + 1] & 0x7FF;
            // first byte
            source[sourceIndex] = (byte) firstSampleUnsigned;
            // second byte
            int lastFourBitsOfSecondByte = firstSampleUnsigned >> 8;
            int firstFourBitsOfSecondByte = secondSampleUnsigned >> 8;
            source[sourceIndex + 1] = (byte) (firstFourBitsOfSecondByte + lastFourBitsOfSecondByte);
            // third byte
            source[sourceIndex + 2] = (byte) secondSampleUnsigned;
            // increment array index
            sourceIndex += DISTRIBUTION;
        }
        return source;
    }

}
