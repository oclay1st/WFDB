package io.github.oclay1st.wfdb;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a single-segment record.
 * 
 * @param header           the single-segment header {@link SingleSegmentHeader}
 * @param samplesPerSignal the array of samples per signal
 */
public record SingleSegmentRecord(SingleSegmentHeader header, int[][] samplesPerSignal) {

    public SingleSegmentRecord {
        for (int i = 0; i < header.headerSignals().length; i++) {
            HeaderSignal headerSignal = header.headerSignals()[i];
            int[] samplesOfSignal = samplesPerSignal[i];
            if (headerSignal.initialValue() != samplesOfSignal[0]) {
                throw new IllegalStateException("Mismatched initial value on signal: " + headerSignal.description());
            }
            if (!headerSignal.matchChecksum(samplesOfSignal)) {
                throw new IllegalStateException("Mismatched checksum on signal: " + headerSignal.description());
            }
        }
    }

    /**
     * Parse a single-segment record given a path.
     *
     * @param recordPath the path where the record will be parsed
     * @return a new {@link SingleSegmentRecord} instance
     * @throws IOException    if the input is invalid
     * @throws ParseException if the text can't be parsed
     */
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
                    .collect(groupingBy(HeaderSignal::filename, LinkedHashMap::new, Collectors.toList()));
            // Parse the signals samples in each file
            for (Map.Entry<String, List<HeaderSignal>> entry : recordFileMap.entrySet()) {
                Path samplesFilePath = recordPath.resolveSibling(entry.getKey());
                HeaderSignal[] headerSignals = entry.getValue().toArray(HeaderSignal[]::new);
                int numberOfSamples = headerSignals.length * numberOfSamplesPerSignal;
                int[][] samples = parseSamples(samplesFilePath, headerSignals, numberOfSamples);
                System.arraycopy(samples, 0, samplesPerSignal, samplesPerSignalIndex, samples.length);
                samplesPerSignalIndex += samples.length;
            }
            return new SingleSegmentRecord(header, samplesPerSignal);
        }
    }

    /**
     * Parse the samples of the signals given a file. It may contain one or many
     * signals samples.
     *
     * @param samplesFilePath the file path of the samples of the signals
     * @param headerSignals   the array of signals
     * @param numberOfSamples the number of samples on the file
     * @return an array of samples for each signal
     * @throws IOException if the input is invalid
     */
    private static int[][] parseSamples(Path samplesFilePath, HeaderSignal[] headerSignals, int numberOfSamples)
            throws IOException {
        try (InputStream samplesInputStream = Files.newInputStream(samplesFilePath)) {
            byte[] source = samplesInputStream.readAllBytes();
            SignalFormat format = headerSignals[0].format(); // All signals have the same format
            int[] formattedSamples = format.convertToSamples(source, headerSignals);
            int[][] samplesPerSignal = new int[headerSignals.length][numberOfSamples / headerSignals.length];
            int signalIndex;
            for (int i = 0; i < headerSignals.length; i++) {
                signalIndex = 0;
                for (int j = i; j < formattedSamples.length; j += headerSignals.length) {
                    samplesPerSignal[i][signalIndex] = formattedSamples[j];
                    signalIndex++;
                }
            }
            return samplesPerSignal;
        }
    }

}
