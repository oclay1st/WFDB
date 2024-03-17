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
     * (stored the least significant byte first). The second sample is formed from
     * the 4 remaining bits of the first byte pair (which are the 4 high bits of the
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
            int firstUnsignedByte = data[i] & 0xFF;
            int secondUnsignedByte = data[i + 1] & 0xFF;
            int lastFourBitsOfSecondByte = secondUnsignedByte & 0x0F;
            int firstSample = (lastFourBitsOfSecondByte << 8) + firstUnsignedByte;
            // Convert to two complement amplitude
            samples[sampleIndex] = firstSample > 2047 ? firstSample - 4096 : firstSample;
            // second sample
            int thirdUnsignedByte = data[i + 2] & 0xFF;
            int firstFourBitsOfSecondByte = secondUnsignedByte >> 4;
            int secondSample = (firstFourBitsOfSecondByte << 8) + thirdUnsignedByte;
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
        int numberOfBytes = (int) (Math.ceil(samples.length * DISTRIBUTION / (float) SAMPLES_PER_DISTRIBUTION));
        int numberOfSamples = samples.length + (SAMPLES_PER_DISTRIBUTION - samples.length % SAMPLES_PER_DISTRIBUTION);
        int[] samplesData = Arrays.copyOf(samples, numberOfSamples);
        byte[] source = new byte[numberOfBytes + DISTRIBUTION];
        for (int i = 0; i < samplesData.length; i += SAMPLES_PER_DISTRIBUTION) {
            int firstUnsignedSample = samplesData[i] & 0xFFF;
            int secondUnsignedSample = samplesData[i + 1] & 0xFFF;
            // first byte
            source[sourceIndex] = (byte) (firstUnsignedSample & 0xFF);
            // second byte
            int lastFourBitsOfSecondByte = firstUnsignedSample >> 8;
            int firstFourBitsOfSecondByte = (secondUnsignedSample >> 8) << 4;
            source[sourceIndex + 1] = (byte) ((firstFourBitsOfSecondByte + lastFourBitsOfSecondByte) & 0xFF);
            // third byte
            source[sourceIndex + 2] = (byte) (secondUnsignedSample & 0xFF);
            // increment array index
            sourceIndex += DISTRIBUTION;
        }
        return Arrays.copyOf(source, numberOfBytes);
    }

}
