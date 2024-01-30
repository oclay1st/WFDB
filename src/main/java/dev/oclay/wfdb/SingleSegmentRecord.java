package dev.oclay.wfdb;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record SingleSegmentRecord(SingleSegmentHeader header, int[][] samplesPerSingal) {

    public static SingleSegmentRecord parse(Path recordPath) throws IOException, ParseException {
        Path headerFilePath = recordPath.resolveSibling(recordPath.getFileName() + ".hea");
        try (InputStream inputStream = Files.newInputStream(headerFilePath)) {
            // Parse info from the header file
            SingleSegmentHeader header = SingleSegmentHeader.parse(inputStream);
            int numberOfSignals = header.headerRecord().numberOfSignals();
            int numberOfSamplesPerSignal = header.headerRecord().numberOfSamples();
            int[][] samplesPerSignal = new int[numberOfSignals][numberOfSamplesPerSignal];
            int samplesPerSignalIndex = 0;
            // Group the signals by filename
            Map<String, List<HeaderSignal>> recordFileMap = Arrays.stream(header.headerSignals())
                    .collect(groupingBy(HeaderSignal::filename));
            // Parse the samples files given the signals
            for (Map.Entry<String, List<HeaderSignal>> entry : recordFileMap.entrySet()) {
                Path samplesFilePath = recordPath.resolveSibling(entry.getKey());
                int[][] samples = parseSamples(samplesFilePath, entry.getValue(), numberOfSamplesPerSignal);
                System.arraycopy(samples, 0, samplesPerSignal, samplesPerSignalIndex, samples.length);
                samplesPerSignalIndex += samples.length - 1;
            }
            return new SingleSegmentRecord(header, samplesPerSignal);
        }
    }

    private static int[][] parseSamples(Path samplesFilePath, List<HeaderSignal> signals, int numberOfSamplesPerSignal)
            throws IOException {
        try (InputStream samplesInputStream = Files.newInputStream(samplesFilePath)) {
            byte[] data = samplesInputStream.readAllBytes();
            int format = signals.get(0).format(); // All signals have the same format
            int signalSize = signals.size();
            int[] formattedSamples = toFormat(format, data);
            int[][] samplesPerSignal = new int[signalSize][numberOfSamplesPerSignal];
            int signalIndex = 0;
            for (int i = 0; i < signalSize; i++) {
                signalIndex = 0;
                for (int j = i; j < formattedSamples.length; j += signalSize) {
                    samplesPerSignal[i][signalIndex] = formattedSamples[j];
                    signalIndex++;
                }
            }
            return samplesPerSignal;
        }
    }

    private static int[] toFormat(int format, byte[] data) {
        return switch (format) {
            case 8 -> SignalFormatter.toFormat8(data);
            case 16 -> SignalFormatter.toFormat16(data);
            case 32 -> SignalFormatter.toFormat32(data);
            case 80 -> SignalFormatter.toFormat80(data);
            case 160 -> SignalFormatter.toFormat160(data);
            case 212 -> SignalFormatter.toFormat212(data);
            case 310 -> SignalFormatter.toFormat310(data);
            case 311 -> SignalFormatter.toFormat311(data);
            default -> throw new IllegalArgumentException("Unsupported bit reference : " + format);
        };
    }

    public int time() {
        return (int) (header.headerRecord().numberOfSamples() / header.headerRecord().samplingFrequency());
    }

}
