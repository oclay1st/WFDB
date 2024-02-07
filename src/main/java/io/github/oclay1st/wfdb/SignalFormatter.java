package io.github.oclay1st.wfdb;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

final class SignalFormatter {

    private SignalFormatter() {
        throw new IllegalAccessError("SignalFormatter class");
    }

    /**
     * Convert sampling data to format 8 in where each sample is represented as an
     * 8-bit first difference; i.e., to get the value of sample n, sum the first n
     * bytes of the sample data file together with the initial value from the header
     * file. When format 8 files are created, first differences which cannot be
     * represented in 8 bits are represented instead by the largest difference of
     * the appropriate sign (-128 or +127), and subsequent differences are adjusted
     * such that the correct amplitude is obtained as quickly as possible. Thus
     * there may be loss of information if signals in another of the formats listed
     * below are converted to format 8. Note that the first differences stored in
     * multiplexed format 8 files are always determined by subtraction of successive
     * samples from the same signal (otherwise signals with baselines which differ
     * by 128 units or more could not be represented this way).
     *
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @param headerSignals   the array of signals header
     * @return a formatted array of samples
     */
    public static int[] toFormat8(byte[] source, int numberOfSamples, HeaderSignal[] headerSignals) {
        int[] samples = new int[numberOfSamples];
        samples[0] = source[0];
        for (int i = 1; i < source.length; i++) {
            samples[i] = samples[i - 1] + source[i];
        }
        int signalIndex = 0;
        for (int i = 0; i < samples.length; i++) {
            samples[i] = samples[i] + headerSignals[signalIndex].initialValue();
        }
        return samples;
    }

    /**
     * Convert sampling data to format 16 in where each sample is represented by a
     * 16-bit two’s complement amplitude stored least significant byte first. Any
     * unused high-order bits are sign-extended from the most significant bit.
     *
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat16(byte[] source, int numberOfSamples) {
        int index = 0;
        int[] samples = new int[numberOfSamples];
        ShortBuffer buffer = ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        while (buffer.hasRemaining()) {
            samples[index] = buffer.get();
            index++;
        }
        return samples;
    }

    /**
     * Convert sampling data to format 24 in where each sample is represented by a
     * 24-bit two’s complement amplitude stored least significant byte first.
     *
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat24(byte[] source, int numberOfSamples) {
        int index = 0;
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
     * Convert sampling data to format 32 in where each sample is represented by a
     * 32-bit two’s complement amplitude stored least significant byte first.
     *
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat32(byte[] source, int numberOfSamples) {
        int[] samples = new int[numberOfSamples];
        ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(samples);
        return samples;
    }

    /**
     * Convert sampling data to format 61 in where each sample is represented by a
     * 16-bit two’s complement amplitude stored most significant byte first.
     *
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat61(byte[] source, int numberOfSamples) {
        int index = 0;
        int[] samples = new int[numberOfSamples];
        ShortBuffer buffer = ByteBuffer.wrap(source).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
        while (buffer.hasRemaining()) {
            samples[index] = buffer.get();
            index++;
        }
        return samples;
    }

    /**
     * Convert sampling data to format 80 in where each sample is represented by an
     * 8-bit amplitude in offset binary form (i.e., 128 must be subtracted from each
     * unsigned byte to obtain a signed 8-bit amplitude).
     *
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat80(byte[] source, int numberOfSamples) {
        int[] samples = new int[numberOfSamples];
        for (int i = 0; i < source.length; i++) {
            samples[i] = (source[i] & 0xFF) - 128;
        }
        return samples;
    }

    /**
     * Convert sampling data to format 160 in where each sample is represented by a
     * 16-bit amplitude in offset binary form (i.e., 32768 must be subtracted from
     * each unsigned byte pair to obtain a signed 16-bit amplitude). As for format
     * 16, the least significant byte of each pair is first.
     *
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat160(byte[] source, int numberOfSamples) {
        int index = 0;
        int[] samples = new int[numberOfSamples];
        ShortBuffer buffer = ByteBuffer.wrap(source).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
        while (buffer.hasRemaining()) {
            samples[index] = buffer.get() - 32768;
            index++;
        }
        return samples;
    }

    /**
     * Convert sampling data to format 212 in where each sample is represented by a
     * 12-bit two’s complement amplitude. The first sample is obtained from the 12
     * least significant bits of the first byte pair (stored least significant byte
     * first). The second sample is formed from the 4 remaining bits of the first
     * byte pair (which are the 4 high bits of the 12-bit sample) and the next byte
     * (which contains the remaining 8 bits of the second sample). The process is
     * repeated for each successive pair of samples.
     *
     * NOTE: In the current implementation an extra value is added to the data array
     * in order to support odd samples of data
     *
     * Given 3 unsigned bytes : represented as 244 15 78
     * 244 -> 1 1 1 1 0 1 0 0
     * 15 -> 0 0 0 0 1 1 1 1
     * 78 -> 0 1 0 0 1 1 1 0
     * Then :
     * First sample: 0 0 0 0 [ 1 1 1 1 | 1 1 1 1 0 1 0 0 ] -> 4084 or -12
     * Second sample: 0 0 0 0 | 0 1 0 0 1 1 1 0 -> 78
     *
     * Two complement form where values greater than 2^11 - 1 = 2047 are negative
     * 12 bits goes 0 to 4096 for unsigned and -2048 to 2047 for signed
     *
     * @param source          the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat212(byte[] source, int numberOfSamples) {
        byte[] data = Arrays.copyOf(source, source.length + 1);
        int[] samples = new int[numberOfSamples + 1];
        int index = 0;
        for (int i = 0; i < data.length; i += 3) {
            // first sample
            int firstByteUnsigned = data[i] & 0xFF;
            int secondByteUnsigned = data[i + 1] & 0xFF;
            int lastFourBitsOfSecondByte = secondByteUnsigned & 0x0F;
            int firstSample = (lastFourBitsOfSecondByte << 8) + firstByteUnsigned;
            // Convert to two complement amplitude
            samples[index] = firstSample > 2047 ? firstSample - 4096 : firstSample;
            // second sample
            int thirdByteUnsigned = data[i + 2] & 0xFF;
            int firstFourBitsOfSecondByte = secondByteUnsigned >> 4;
            int secondSample = (firstFourBitsOfSecondByte << 8) + thirdByteUnsigned;
            // Convert to two complement amplitude
            samples[index + 1] = secondSample > 2047 ? secondSample - 4096 : secondSample;
            // increment array index
            index += 2;
        }
        return Arrays.copyOf(samples, numberOfSamples);
    }

    /**
     * Convert sampling data to format 310 in where each sample is represented by a
     * 10-bit two’s-complement amplitude. The first sample is obtained from the 11
     * least significant bits of the first byte pair (stored least significant byte
     * first), with the low bit discarded. The second sample comes from the 11 least
     * significant bits of the second byte pair, in the same way as the first. The
     * third sample is formed from the 5 most significant bits of each of the first
     * two byte pairs (those from the first byte pair are the least significant bits
     * of the third sample).
     *
     * NOTE: In the current implementation two extra values is added to the data
     * array in order to support odd samples of data
     *
     * Given 3 unsigned bytes: represented as 246 223 0
     * 246 -> 1 1 1 1 0 1 1 0
     * 223 -> 1 1 0 1 1 1 1 1
     * 0 -> 0 0 0 0 0 0 0 0
     * 248 -> 1 1 1 1 1 0 0 0
     *
     * Then :
     * Fist sample: 1 1 0 1 1 [1 1 1 | 1 1 1 1 0 1 1] 0 -> 1019 - 1024 = -5
     * Second sample: 1 1 1 1 1 [0 0 0 | 0 0 0 0 0 0] 0 -> 0
     * Third sample: [1 1 1 1 1] 0 0 0 | [1 1 0 1 1] 1 1 1 -> 1019 -1024 = -5
     *
     * Two complement form where values greater than 2^9 - 1 = 511 are negative
     * 10 bits goes from 0 to 1024 for unsigned -512 to 511 for signed
     *
     * @param source          the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat310(byte[] source, int numberOfSamples) {
        byte[] data = Arrays.copyOf(source, source.length + 2);
        int[] samples = new int[numberOfSamples + 2];
        int index = 0;
        for (int i = 0; i < data.length; i += 4) {
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
            index += 3;
        }
        return Arrays.copyOf(samples, numberOfSamples);
    }

    /**
     * Convert sampling data to format 311 in where each sample is represented by a
     * 10-bit two’s-complement amplitude. Three samples are bit-packed into a 32-bit
     * integer as for format 310, but the layout is different. Each set of four
     * bytes is stored in little-endian order (least significant byte first, most
     * significant byte last). The first sample is obtained from the 10 least
     * significant bits of the 32-bit integer, the second is obtained from the next
     * 10 bits, the third from the next 10 bits, and the two most significant bits
     * are unused. This process is repeated for each successive set of three
     * samples.
     *
     * NOTE: In the current implementation two extra values is added to the data
     * array in order to support odd samples of data
     *
     * Two complement form where values greater than 2^9 - 1 = 511 are negative
     * 10 bits goes from 0 to 1024 for unsigned -512 to 511 for signed
     * 
     * @param source            the raw data of the signals samples
     * @param numberOfSamples the number of samples
     * @return a formatted array of samples
     */
    public static int[] toFormat311(byte[] source, int numberOfSamples) {
        byte[] data = Arrays.copyOf(source, source.length + 2);
        int[] samples = new int[numberOfSamples + 2];
        int index = 0;
        for (int i = 0; i < data.length; i += 4) {
            int firstByteUnsigned = data[i] & 0xFF;
            int secondByteUnsigned = data[i + 1] & 0xFF;
            int lastTwoBitsOfSecondByte = secondByteUnsigned & 0x03;
            int firstSample = (lastTwoBitsOfSecondByte << 8) + firstByteUnsigned;
            // Convert to two complement amplitude
            samples[index] = firstSample > 511 ? firstSample - 1024 : firstSample;
            // second sample
            int thirdByteUnsigned = data[i + 2] & 0xFF;
            int firstSixBitsOfSecondByte = secondByteUnsigned >> 2;
            int lastFourBitsOfThirdByte = thirdByteUnsigned & 0x0F;
            int secondSample = (lastFourBitsOfThirdByte << 6) + firstSixBitsOfSecondByte;
            // Convert to two complement amplitude
            samples[index + 1] = secondSample > 511 ? secondSample - 1024 : secondSample;
            // third sample
            int fourthByteUnsigned = data[i + 3] & 0xFF;
            int firstSixBitsOfFourthByte = fourthByteUnsigned & 0x7F;
            int firstFourBitsOfThirdByte = thirdByteUnsigned >> 4;
            int thirdSample = (firstSixBitsOfFourthByte << 4) + firstFourBitsOfThirdByte;
            // Convert to two complement amplitude
            samples[index + 2] = thirdSample > 511 ? thirdSample - 1024 : thirdSample;
            index += 3;
        }
        return Arrays.copyOf(samples, numberOfSamples);
    }

}
