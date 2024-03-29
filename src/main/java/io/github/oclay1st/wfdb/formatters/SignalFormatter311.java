package io.github.oclay1st.wfdb.formatters;

import java.util.Arrays;

/**
 * Represents the signal formatter for format 311.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter311 implements SignalFormatter {

    private static final int DISTRIBUTION = 4; // 4 bytes for each 3 samples

    private static final int SAMPLES_PER_DISTRIBUTION = 3; // 3 samples in 4 bytes

    /**
     * {@inheritDoc}
     * Each sample is represented by a 10-bit two’s-complement amplitude. Three
     * samples are bit-packed into a 32-bit integer as for format 310, but the
     * layout is different. Each set of four bytes is stored in little-endian order
     * (least significant byte first, most significant byte last). The first sample
     * is obtained from the 10 least significant bits of the 32-bit integer, the
     * second is obtained from the next 10 bits, the third from the next 10 bits,
     * and the two most significant bits are unused. This process is repeated for
     * each successive set of three samples.
     *
     * NOTE: In the current implementation three extra values is added to the data
     * array in order to support odd samples of data.
     *
     * Two complement form where values greater than 2^9 - 1 = 511 are negative
     * 10 bits goes from 0 to 1024 for unsigned -512 to 511 for signed.
     */
    @Override
    public int[] convertBytesToSamples(byte[] source) {
        int numberOfSamples = source.length - (Math.round(source.length / (float) DISTRIBUTION));
        byte[] data = Arrays.copyOf(source, source.length + (DISTRIBUTION - source.length % DISTRIBUTION));
        int sampleIndex = 0;
        int[] samples = new int[numberOfSamples + SAMPLES_PER_DISTRIBUTION];
        for (int i = 0; i < data.length; i += DISTRIBUTION) {
            int firstUnsignedByte = data[i] & 0xFF;
            int secondUnsignedByte = data[i + 1] & 0xFF;
            int lastTwoBitsOfSecondByte = secondUnsignedByte & 0x03;
            int firstSample = (lastTwoBitsOfSecondByte << 8) + firstUnsignedByte;
            // Convert to two complement amplitude
            samples[sampleIndex] = firstSample > 511 ? firstSample - 1024 : firstSample;
            // second sample
            int thirdUnsignedByte = data[i + 2] & 0xFF;
            int firstSixBitsOfSecondByte = secondUnsignedByte >> 2;
            int lastFourBitsOfThirdByte = thirdUnsignedByte & 0x0F;
            int secondSample = (lastFourBitsOfThirdByte << 6) + firstSixBitsOfSecondByte;
            // Convert to two complement amplitude
            samples[sampleIndex + 1] = secondSample > 511 ? secondSample - 1024 : secondSample;
            // third sample
            int fourthUnsignedByte = data[i + 3] & 0xFF;
            int lastSevenBitsOfFourthByte = fourthUnsignedByte & 0x7F;
            int firstFourBitsOfThirdByte = thirdUnsignedByte >> 4;
            int thirdSample = (lastSevenBitsOfFourthByte << 4) + firstFourBitsOfThirdByte;
            // Convert to two complement amplitude
            samples[sampleIndex + 2] = thirdSample > 511 ? thirdSample - 1024 : thirdSample;
            sampleIndex += SAMPLES_PER_DISTRIBUTION;
        }
        return Arrays.copyOf(samples, numberOfSamples);
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 311 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples) {
        int numberOfSamples = samples.length + (SAMPLES_PER_DISTRIBUTION - samples.length % SAMPLES_PER_DISTRIBUTION);
        int[] samplesData = Arrays.copyOf(samples, numberOfSamples);
        int numberOfBytes = (int) (Math.ceil(samples.length * DISTRIBUTION / (float) SAMPLES_PER_DISTRIBUTION));
        byte[] source = new byte[numberOfBytes + DISTRIBUTION];
        int sourceIndex = 0;
        for (int i = 0; i < samplesData.length; i += SAMPLES_PER_DISTRIBUTION) {
            int firstUnsignedSample = samplesData[i] & 0x3FF;
            int secondUnsignedSample = samplesData[i + 1] & 0x3FF;
            int thirdUnsignedSample = samplesData[i + 2] & 0x3FF;
            // first byte
            source[sourceIndex] = (byte) (firstUnsignedSample);
            // second byte
            int lastTwoBitsOfSecondByte = firstUnsignedSample >> 8;
            int firstSixBitsOfSecondByte = secondUnsignedSample & 0xF;
            source[sourceIndex + 1] = (byte) (firstSixBitsOfSecondByte + lastTwoBitsOfSecondByte);
            // third byte
            int lastFourBitsOfThirdByte = secondUnsignedSample >> 6;
            int firstFourBitsOfThirdByte = thirdUnsignedSample & 0xF;
            source[sourceIndex + 2] = (byte) (firstFourBitsOfThirdByte + lastFourBitsOfThirdByte);
            // fourth byte
            int lastSixBitsOfFourthByte = thirdUnsignedSample >> 4;
            source[sourceIndex + 3] = (byte) (lastSixBitsOfFourthByte);
            // increment array index
            sourceIndex += DISTRIBUTION;
        }
        return Arrays.copyOf(source, numberOfBytes);
    }

}
