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
            int numberOfSamplesPerSignal = header.headerRecord().numberOfSamplesPerSignal();
            int[][] samplesPerSignal = new int[numberOfSignals][numberOfSamplesPerSignal];
            int samplesPerSignalIndex = 0;
            // Group the signals by filename
            Map<String, List<HeaderSignal>> recordFileMap = Arrays.stream(header.headerSignals())
                    .collect(groupingBy(HeaderSignal::filename));
            // Parse the signals samples in each file 
            for (Map.Entry<String, List<HeaderSignal>> entry : recordFileMap.entrySet()) {
                Path samplesFilePath = recordPath.resolveSibling(entry.getKey());
                HeaderSignal[] headerSignals = entry.getValue().toArray(HeaderSignal[]::new);
                int numberOfSamples = headerSignals.length * numberOfSamplesPerSignal;
                int[][] samples = parseSamples(samplesFilePath, headerSignals, numberOfSamples);
                System.arraycopy(samples, 0, samplesPerSignal, samplesPerSignalIndex, samples.length);
                samplesPerSignalIndex += samples.length - 1;
            }
            return new SingleSegmentRecord(header, samplesPerSignal);
        }
    }

    private static int[][] parseSamples(Path samplesFilePath, HeaderSignal[] signals, int numberOfSamples)
            throws IOException {
        try (InputStream samplesInputStream = Files.newInputStream(samplesFilePath)) {
            byte[] data = samplesInputStream.readAllBytes();
            int format = signals[0].format(); // All signals have the same format
            int[] formattedSamples = toFormat(format, data, signals, numberOfSamples);
            int[][] samplesPerSignal = new int[signals.length][numberOfSamples / signals.length];
            int signalIndex = 0;
            for (int i = 0; i < signals.length; i++) {
                signalIndex = 0;
                for (int j = i; j < formattedSamples.length; j += signals.length) {
                    samplesPerSignal[i][signalIndex] = formattedSamples[j];
                    signalIndex++;
                }
            }
            return samplesPerSignal;
        }
    }

    private static int[] toFormat(int format, byte[] data, HeaderSignal[] headerSignals, int numberOfSamples) {
        return switch (format) {
            case 8 -> SignalFormatter.toFormat8(data, numberOfSamples, headerSignals);
            case 16 -> SignalFormatter.toFormat16(data, numberOfSamples);
            case 24 -> SignalFormatter.toFormat24(data, numberOfSamples);
            case 32 -> SignalFormatter.toFormat32(data, numberOfSamples);
            case 61 -> SignalFormatter.toFormat61(data, numberOfSamples);
            case 80 -> SignalFormatter.toFormat80(data, numberOfSamples);
            case 160 -> SignalFormatter.toFormat160(data, numberOfSamples);
            case 212 -> SignalFormatter.toFormat212(data, numberOfSamples);
            case 310 -> SignalFormatter.toFormat310(data, numberOfSamples);
            case 311 -> SignalFormatter.toFormat311(data, numberOfSamples);
            default -> throw new IllegalArgumentException("Unsupported bit reference : " + format);
        };
    }

    public int time() {
        return (int) (header.headerRecord().numberOfSamplesPerSignal() / header.headerRecord().samplingFrequency());
    }

}
