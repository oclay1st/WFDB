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

import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter8;

/**
 * Represents a single-segment record.
 * 
 * @param header           the single-segment header {@link SingleSegmentHeader}
 * @param samplesPerSignal the array of samples per signal
 */
public record SingleSegmentRecord(SingleSegmentHeader header, int[][] samplesPerSignal) {

    /**
     * Constructs a new instance of SingleSegmentRecord where each signal samples
     * must match the checksum and the initial values from the header info.
     * {@inheritDoc}
     */
    public SingleSegmentRecord {
        for (int i = 0; i < header.headerSignals().length; i++) {
            HeaderSignal headerSignal = header.headerSignals()[i];
            int[] samplesOfSignal = samplesPerSignal[i];
            if (headerSignal.initialValue() != samplesOfSignal[0]) {
                throw new IllegalStateException("Mismatched initial value on signal: " + headerSignal.description());
            }
            if (headerSignal.checksum() != 0 && !headerSignal.matchChecksum(samplesOfSignal)) {
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
            SignalFormatter formatter = resolveSignalFormatter(headerSignals);
            int[] samples = formatter.convertBytesToSamples(source);
            int[][] samplesPerSignal = new int[headerSignals.length][numberOfSamples / headerSignals.length];
            int signalIndex;
            for (int i = 0; i < headerSignals.length; i++) {
                signalIndex = 0;
                for (int j = i; j < samples.length; j += headerSignals.length) {
                    samplesPerSignal[i][signalIndex] = samples[j];
                    signalIndex++;
                }
            }
            return samplesPerSignal;
        }
    }

    /**
     * Resolve the signal formatter to be use.
     *
     * By definition: all signals samples of the same file must have the same format
     * 
     * @param headerSignals the array of header signals
     * @return the {@link SignalFormatter} instance
     */
    private static SignalFormatter resolveSignalFormatter(HeaderSignal[] headerSignals) {
        // takes the signal formatter from the first header signal
        SignalFormatter formatter = headerSignals[0].format().formatter();
        // format 8 is a special case that needs the initial values of header signals
        if (formatter instanceof SignalFormatter8 instance) {
            int[] initialValues = Arrays.stream(headerSignals).mapToInt(HeaderSignal::initialValue).toArray();
            instance.setInitialSignalSamples(initialValues);
        }
        return formatter;
    }

}
