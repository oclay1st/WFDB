package dev.oclay.wfdb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public record WFDB(Header header, int[][] samplesPerSingal) {

    public static WFDB parse(InputStream headerInput, InputStream samplingInput) throws IOException, ParseException {
        Objects.requireNonNull(headerInput);
        Objects.requireNonNull(samplingInput);
        Header header = Header.parse(headerInput);
        int[][] samples = parseSamplesPerSignal(samplingInput.readAllBytes(), header);
        return new WFDB(header, samples);
    }

    public static WFDB parse(Path headerFilePath, Path samplingFilePath) throws IOException, ParseException {
        try (InputStream headerInput = Files.newInputStream(headerFilePath);
                InputStream samplingInput = Files.newInputStream(samplingFilePath)) {
            return parse(headerInput, samplingInput);
        }
    }

    private static int[][] parseSamplesPerSignal(byte[] data, Header header) throws ParseException {
        if (!header.isSingleFileFormat()) {
            throw new ParseException("Unsupported header with different signal format and files");
        }
        int format = header.signals()[0].format();
        int[] formattedSamples = toFormat(format, data);
        int[][] samplesPerSignal = new int[header.record().numberOfSignals()][header.record().numberOfSamples()];
        int signalIndex = 0;
        for (int i = 0; i < header.record().numberOfSignals(); i++) {
            signalIndex = 0;
            for (int j = i; j < formattedSamples.length; j += header.record().numberOfSignals()) {
                samplesPerSignal[i][signalIndex] = formattedSamples[j];
                signalIndex++;
            }
        }
        return samplesPerSignal;
    }

    private static int[] toFormat(int format, byte[] data) {
        return switch (format) {
            case 8 -> toFormat8(data);
            case 16 -> toFormat16(data);
            case 32 -> toFormat32(data);
            case 80 -> toFormat80(data);
            case 160 -> toFormat160(data);
            case 212 -> toFormat212(data);
            case 310 -> toFormat310(data);
            case 311 -> toFormat311(data);
            default -> throw new IllegalArgumentException("Unsupported bit reference : " + format);
        };
    }

    /**
     * Convert sampling data to format 8
     *
     * Each sample is represented as an 8-bit first difference; i.e., to get the
     * value of sample n, sum the first n bytes of the sample data file together
     * with the initial value from the header file. When format 8 files are created,
     * first differences which cannot be represented in 8 bits are represented
     * instead by the largest difference of the appropriate sign (-128 or +127), and
     * subsequent differences are adjusted such that the correct amplitude is
     * obtained as quickly as possible. Thus there may be loss of information if
     * signals in another of the formats listed below are converted to format 8.
     * Note that the first differences stored in multiplexed format 8 files are
     * always determined by subtraction of successive samples from the same signal
     * (otherwise signals with baselines which differ by 128 units or more could not
     * be represented this way)
     */
    public static int[] toFormat8(byte[] data) {
        int[] values = new int[data.length];
        byte[] samples = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).array();
        for (int i = 0; i < samples.length; i++) {
            values[i] = samples[i];
        }
        return values;
    }

    /**
     * Convert sampling data to format 16
     * 
     * Each sample is represented by a 16-bit two’s complement amplitude stored
     * least significant byte first. Any unused high-order bits are sign-extended
     * from the most significant bit.
     */
    public static int[] toFormat16(byte[] data) {
        int[] values = new int[data.length / 2];
        short[] samples = new short[data.length / 2];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
        for (int i = 0; i < samples.length; i++) {
            values[i] = samples[i];
        }
        return values;
    }

    /**
     * Convert sampling data to format 32
     *
     * Each sample is represented by a 32-bit two’s complement amplitude stored
     * least significant byte first.
     */
    public static int[] toFormat32(byte[] data) {
        int[] samples = new int[data.length / 4];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(samples);
        return samples;
    }

    /**
     * Convert sampling data to format 80
     *
     * Each sample is represented by an 8-bit amplitude in offset binary form (i.e.,
     * 128 must be subtracted from each unsigned byte to obtain a signed 8-bit
     * amplitude).
     */
    public static int[] toFormat80(byte[] data) {
        throw new IllegalStateException("Not implemeted yet");
    }

    /**
     * Convert sampling data to format 160
     *
     * Each sample is represented by a 16-bit amplitude in offset binary form (i.e.,
     * 32,768 must be subtracted from each unsigned byte pair to obtain a signed
     * 16-bit amplitude). As for format 16, the least significant byte of each pair
     * is first.
     */
    public static int[] toFormat160(byte[] data) {
        throw new IllegalStateException("Not implemeted yet");
    }

    /**
     * Convert sampling data to format 212
     *
     * Each sample is represented by a 12-bit two’s complement amplitude. The first
     * sample is obtained from the 12 least significant bits of the first byte pair
     * (stored least significant byte first). The second sample is formed from the 4
     * remaining bits of the first byte pair (which are the 4 high bits of the
     * 12-bit sample) and the next byte (which contains the remaining 8 bits of the
     * second sample). The process is repeated for each successive pair of samples.
     * 
     * Given 3 unsigned bytes : represented as 244 15 78
     * 244 -> 1 1 1 1 0 1 0 0
     * 15 -> 1 1 1 1
     * 78 -> 1 0 0 1 1 1 0
     * Then :
     * First sample: 0 0 0 0 [ 1 1 1 1 | 1 1 1 1 0 1 0 0 ] -> 4084
     * Second sample: 0 0 0 0 | 1 0 0 1 1 1 0 -> 78
     * *
     */
    public static int[] toFormat212(byte[] data) {
        int[] samples = new int[data.length - data.length / 3];
        int index = 0;
        for (int i = 0; i < data.length; i += 3) {
            // first sample
            int firstByteUsigned = data[i] & 0xFF;
            int secondByteUnsigned = data[i + 1] & 0xFF;
            int lastFourBitsOfSecondByte = secondByteUnsigned & 0x0F;
            samples[index] = (lastFourBitsOfSecondByte << 8) + firstByteUsigned;
            // second sample
            int thirdUnsigned = data[i + 2] & 0xFF;
            int firstFourBitsOfSecondByte = (secondByteUnsigned >> 4) & 0x0F;
            samples[index + 1] = (firstFourBitsOfSecondByte << 8) + thirdUnsigned;
            // increment array index
            index += 2;
        }
        return samples;
    }

    /**
     * Convert sampling data to format 310
     *
     * Each sample is represented by a 10-bit two’s-complement amplitude. The first
     * sample is obtained from the 11 least significant bits of the first byte pair
     * (stored least significant byte first), with the low bit discarded. The second
     * sample comes from the 11 least significant bits of the second byte pair, in
     * the same way as the first. The third sample is formed from the 5 most
     * significant bits of each of the first two byte pairs (those from the first
     * byte pair are the least significant bits of the third sample).
     */
    private static int[] toFormat310(byte[] data) {
        throw new IllegalStateException("Not implemeted yet");
    }

    /**
     * Convert sampling data to format 311
     *
     * Each sample is represented by a 10-bit two’s-complement amplitude. Three
     * samples are bit-packed into a 32-bit integer as for format 310, but the
     * layout is different. Each set of four bytes is stored in little-endian order
     * (least significant byte first, most significant byte last). The first sample
     * is obtained from the 10 least significant bits of the 32-bit integer, the
     * second is obtained from the next 10 bits, the third from the next 10 bits,
     * and the two most significant bits are unused. This process is repeated for
     * each successive set of three samples.
     */
    public static int[] toFormat311(byte[] data) {
        throw new IllegalStateException("Not implemeted yet");
    }

    public int time() {
        return (int) (header.record().numberOfSamples() / header.record().samplingFrequency());
    }

}
