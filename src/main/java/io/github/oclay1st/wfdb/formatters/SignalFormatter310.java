package io.github.oclay1st.wfdb.formatters;

import java.util.Arrays;

/**
 * Represents the signal formatter for format 310.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter310 implements SignalFormatter {

    private static final int DISTRIBUTION = 4; // 4 bytes for each 3 samples

    private static final int SAMPLES_PER_DISTRIBUTION = 3; // 3 samples in 4 bytes

    /**
     * {@inheritDoc}
     * Each sample is represented by a 10-bit two’s-complement amplitude. The first
     * sample is obtained from the 11 least significant bits of the first byte pair
     * (stored the least significant byte first), with the low bit discarded. The
     * second
     * sample comes from the 11 least significant bits of the second byte pair, in
     * the same way as the first. The third sample is formed from the 5 most
     * significant bits of each of the first two byte pairs (those from the first
     * byte pair are the least significant bits of the third sample).
     *
     * NOTE: In the current implementation three extra values is added to the data
     * array in order to support odd samples of data.
     * 
     * <pre>
     * Given 3 unsigned bytes: represented as 246 223 0.
     *
     * 246 -> 1 1 1 1 0 1 1 0
     * 223 -> 1 1 0 1 1 1 1 1
     * 0 -> 0 0 0 0 0 0 0 0
     * 248 -> 1 1 1 1 1 0 0 0
     *
     * Then :
     * Fist sample: 1 1 0 1 1 [1 1 1 | 1 1 1 1 0 1 1] 0 -> 1019 - 1024 = -5
     * Second sample: 1 1 1 1 1 [0 0 0 | 0 0 0 0 0 0] 0 -> 0
     * Third sample: [1 1 1 1 1] 0 0 0 | [1 1 0 1 1] 1 1 1 -> 1019 -1024 = -5
     * </pre>
     * 
     * Two complement form where values greater than 2^9 - 1 = 511 are negative
     * 10 bits goes from 0 to 1024 for unsigned -512 to 511 for signed.
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        int sampleIndex = 0;
        int numberOfSamples = source.length - (Math.round(source.length / (float) DISTRIBUTION));
        byte[] data = Arrays.copyOf(source, source.length + (DISTRIBUTION - source.length % DISTRIBUTION));
        int[] samples = new int[numberOfSamples + SAMPLES_PER_DISTRIBUTION];
        for (int i = 0; i < data.length; i += DISTRIBUTION) {
            // first sample
            int firstUnsignedByte = data[i] & 0xFF;
            int secondUnsignedByte = data[i + 1] & 0xFF;
            int lastThreeBitsOfSecondByte = secondUnsignedByte & 0x07;
            int firstSevenBitsOfFirstByte = firstUnsignedByte >> 1;
            int firstSample = (lastThreeBitsOfSecondByte << 7) + firstSevenBitsOfFirstByte;
            // Convert to two complement amplitude
            samples[sampleIndex] = firstSample > 511 ? firstSample - 1024 : firstSample;
            // second sample
            int thirdUnsignedByte = data[i + 2] & 0xFF;
            int fourthUnsignedByte = data[i + 3] & 0xFF;
            int lastThreeBitsOfFourthByte = fourthUnsignedByte & 0x07;
            int firstSevenBitsOfThirdByte = thirdUnsignedByte >> 1;
            int secondSample = (lastThreeBitsOfFourthByte << 7) + firstSevenBitsOfThirdByte;
            // Convert to two complement amplitude
            samples[sampleIndex + 1] = secondSample > 511 ? secondSample - 1024 : secondSample;
            // third sample
            int firstFiveBitsOfFourthByte = fourthUnsignedByte >> 3;
            int firstFiveBitsOfSecondByte = secondUnsignedByte >> 3;
            int thirdSample = (firstFiveBitsOfFourthByte << 5) + firstFiveBitsOfSecondByte;
            samples[sampleIndex + 2] = thirdSample > 511 ? thirdSample - 1024 : thirdSample;
            // Convert to two complement amplitude
            sampleIndex += SAMPLES_PER_DISTRIBUTION;
        }
        return Arrays.copyOf(samples, numberOfSamples);
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 310 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        int sourceIndex = 0;
        int numberOfSamples = samples.length + (SAMPLES_PER_DISTRIBUTION - samples.length % SAMPLES_PER_DISTRIBUTION);
        int numberOfBytes = (int) (Math.ceil(samples.length * DISTRIBUTION / (float) SAMPLES_PER_DISTRIBUTION));
        byte[] source = new byte[numberOfBytes + DISTRIBUTION];
        int[] samplesData = Arrays.copyOf(samples, numberOfSamples);
        for (int i = 0; i < samplesData.length; i += SAMPLES_PER_DISTRIBUTION) {
            int firstUnsignedSample = samplesData[i] & 0x3FF;
            int secondUnsignedSample = samplesData[i + 1] & 0x3FF;
            int thirdUnsignedSample = samplesData[i + 2] & 0x3FF;
            // first byte
            source[sourceIndex] = (byte) ((firstUnsignedSample & 0x7F) << 1);
            // second byte
            int lastThreeBitsOfSecondByte = firstUnsignedSample >> 7;
            int firstFiveBitsOfSecondByte = thirdUnsignedSample & 0x1F;
            source[sourceIndex + 1] = (byte) (firstFiveBitsOfSecondByte + lastThreeBitsOfSecondByte);
            // third byte
            source[sourceIndex + 2] = (byte) ((secondUnsignedSample & 0x7F) << 1);
            // fourth byte
            int lastThreeBitsOfFourthByte = secondUnsignedSample >> 7;
            int firstFiveBitsOfFourthByte = thirdUnsignedSample >> 5;
            source[sourceIndex + 3] = (byte) (firstFiveBitsOfFourthByte + lastThreeBitsOfFourthByte);
            // increment array index
            sourceIndex += DISTRIBUTION;
        }
        return Arrays.copyOf(source, numberOfBytes);
    }

}
