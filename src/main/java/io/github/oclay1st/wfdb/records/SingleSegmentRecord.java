package io.github.oclay1st.wfdb.records;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.oclay1st.wfdb.exceptions.ParseException;
import io.github.oclay1st.wfdb.filters.BytesRange;
import io.github.oclay1st.wfdb.filters.Filter;
import io.github.oclay1st.wfdb.filters.FilterProcessor;
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
     * Parse a single-segment record given a path.
     *
     * @param recordPath the path where the record will be parsed
     * @return a new {@link SingleSegmentRecord} instance
     * @throws IOException    if the input is invalid
     * @throws ParseException if the text can't be parsed
     */
    public static SingleSegmentRecord parse(Path recordPath) throws IOException, ParseException {
        Filter defaultFilter = new Filter.Builder().build();
        return parse(recordPath, defaultFilter);
    }

    /**
     * Parse a single-segment record given a path.
     *
     * @param recordPath the path where the record will be parsed
     * @param filter the filter to apply on the record 
     * @return a new {@link SingleSegmentRecord} instance
     * @throws IOException    if the input is invalid
     * @throws ParseException if the text can't be parsed
     */
    public static SingleSegmentRecord parse(Path recordPath, Filter filter) throws IOException, ParseException {
        SingleSegmentHeader header = parseHeaderFile(recordPath);
        FilterProcessor filterProcessor = FilterProcessor.process(filter, header);
        SingleSegmentHeader filteredHeader = filterProcessor.generateFilteredHeader();
        int numberOfSignals = filteredHeader.headerRecord().numberOfSignals();
        int[][] samplesPerSignal = new int[numberOfSignals][0];
        int samplesPerSignalIndex = 0;
        // Group the signals by filename
        Map<String, List<HeaderSignal>> recordFileMap = filteredHeader.groupByHeaderSignalsByFilename();
        // Parse the signals samples in each file
        for (Map.Entry<String, List<HeaderSignal>> entry : recordFileMap.entrySet()) {
            Path samplesFilePath = recordPath.resolveSibling(entry.getKey());
            HeaderSignal[] headerSignals = entry.getValue().toArray(HeaderSignal[]::new);
            BytesRange bytesRange = filterProcessor.calculateBytesRange(headerSignals);
            byte[] source = parseSamplesFile(samplesFilePath, bytesRange);
            int[][] samples = parseSamplesSource(source, headerSignals);
            System.arraycopy(samples, 0, samplesPerSignal, samplesPerSignalIndex, samples.length);
            samplesPerSignalIndex += samples.length;
        }
        return new SingleSegmentRecord(filteredHeader, samplesPerSignal);
    }

    /**
     * Read the single segment header from file
     *
     * @param recordPath the path where the record will be parsed
     * @return the single segment header
     * @throws IOException    if the input is invalid
     * @throws ParseException if the text can't be parsed
     */
    private static SingleSegmentHeader parseHeaderFile(Path recordPath) throws IOException, ParseException {
        Path headerFilePath = recordPath.resolveSibling(recordPath.getFileName() + ".hea");
        try (InputStream inputStream = Files.newInputStream(headerFilePath)) {
            return SingleSegmentHeader.parse(inputStream);
        }
    }

    /**
     * Read the signal sample file using a range of bytes.
     * 
     * @param samplesFilePath the file path of the samples of the signals
     * @param bytesRange      the byte range to be read from the file
     * @return the value of the source of bytes
     * @throws IOException if the file is invalid to read
     */
    private static byte[] parseSamplesFile(Path samplesFilePath, BytesRange bytesRange) throws IOException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(samplesFilePath.toFile(), "r")) {
            byte[] buffer = new byte[(int) bytesRange.total()];
            randomAccessFile.seek(bytesRange.start());
            randomAccessFile.readFully(buffer);
            return buffer;
        }
    }

    /**
     * Convert the samples source to array of samples per signals
     *
     * @param source        the source of bytes
     * @param headerSignals the array of signals
     * @return an array of samples for each signal
     */
    private static int[][] parseSamplesSource(byte[] source, HeaderSignal[] headerSignals) {
        SignalFormatter formatter = resolveSignalFormatter(headerSignals);
        int[] samples = formatter.convertBytesToSamples(source);
        int[][] samplesPerSignal = new int[headerSignals.length][samples.length / headerSignals.length];
        int localSignalIndex = 0, localSampleIndex = 0;
        for (int sample : samples) {
            samplesPerSignal[localSignalIndex][localSampleIndex] = sample;
            localSignalIndex++;
            if (localSignalIndex == headerSignals.length) {
                localSignalIndex = 0;
                localSampleIndex++;
            }
        }
        return samplesPerSignal;
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

    @Override
    public boolean equals(Object object) {
        return object instanceof SingleSegmentRecord instance && header.equals(instance.header)
                && Arrays.deepEquals(samplesPerSignal, instance.samplesPerSignal);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(header) + Arrays.deepHashCode(samplesPerSignal);
    }

    @Override
    public String toString() {
        return "SingleSegmentRecord [ header = " + header + ", samplesPerSignal = " + Arrays.toString(samplesPerSignal)
                + ']';
    }
}
