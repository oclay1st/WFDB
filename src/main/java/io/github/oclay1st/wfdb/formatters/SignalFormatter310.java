package io.github.oclay1st.wfdb.formatters;

import java.util.Arrays;

import io.github.oclay1st.wfdb.HeaderSignal;

/**
 * Represents the signal formatter for format 310.
 * 
 * @see SignalFormatter
 */
public final class SignalFormatter310 implements SignalFormatter {

    /**
     * {@inheritDoc}
     * Each sample is represented by a 10-bit two’s-complement amplitude. The first
     * sample is obtained from the 11 least significant bits of the first byte pair
     * (stored least significant byte first), with the low bit discarded. The second
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
    public int[] convertBytesToSamples(byte[] source, HeaderSignal[] headerSignals) {
        int distribution = 4;
        int samplesPerDistribution = 3;
        int numberOfSamples = source.length - (Math.round(source.length / (float) distribution));
        byte[] data = Arrays.copyOf(source, source.length + (distribution - source.length % distribution));
        int[] samples = new int[numberOfSamples + samplesPerDistribution];
        int index = 0;
        for (int i = 0; i < data.length; i += distribution) {
            // first sample
            int firstByteUnsigned = data[i] & 0xFF;
            int secondByteUnsigned = data[i + 1] & 0xFF;
            int lastThreeBitsOfSecondByte = secondByteUnsigned & 0x07;
            int firstSevenBitsOfFirstByte = firstByteUnsigned >> 1;
            int firstSample = (lastThreeBitsOfSecondByte << 7) + firstSevenBitsOfFirstByte;
            // Convert to two complement amplitude
            samples[index] = firstSample > 511 ? firstSample - 1024 : firstSample;
            // second sample
            int thirdByteUnsigned = data[i + 2] & 0xFF;
            int fourthByteUnsigned = data[i + 3] & 0xFF;
            int lastThreeBitsOfFourthByte = fourthByteUnsigned & 0x07;
            int firstSevenBitsOfThirdByte = thirdByteUnsigned >> 1;
            int secondSample = (lastThreeBitsOfFourthByte << 7) + firstSevenBitsOfThirdByte;
            // Convert to two complement amplitude
            samples[index + 1] = secondSample > 511 ? secondSample - 1024 : secondSample;
            // third sample
            int firstFiveBitsOfFourthByte = fourthByteUnsigned >> 3;
            int firstFiveBitsOfSecondByte = secondByteUnsigned >> 3;
            int thirdSample = (firstFiveBitsOfFourthByte << 5) + firstFiveBitsOfSecondByte;
            samples[index + 2] = thirdSample > 511 ? thirdSample - 1024 : thirdSample;
            // Convert to two complement amplitude
            index += samplesPerDistribution;
        }
        return Arrays.copyOf(samples, numberOfSamples);
    }

    /**
     * {@inheritDoc}
     * Each formatted samples of format 310 will be converted to raw data as bytes.
     */
    @Override
    public byte[] convertSamplesToBytes(int[] samples, HeaderSignal[] headerSignals) {
        // TODO Auto-generated method stub
        return null;
    }

}
