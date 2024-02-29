package io.github.oclay1st.wfdb.records;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.github.oclay1st.wfdb.exceptions.ParseException;

/**
 * Represents a single-segment record header.
 *
 * @param headerRecord  the header record
 * @param headerSignals the array of header signals
 */
public record SingleSegmentHeader(HeaderRecord headerRecord, HeaderSignal[] headerSignals) {

    /**
     * Parse the single-segment header from an input form.
     * 
     * <pre>
     * # Example of the header file text:
     * 100 2 360 650000 0:0:0 0/0/0
     * 100.dat 212 200 11 1024 995 -22131 0 MLII
     * 100.dat 212 200 11 1024 1011 20052 0 V5
     * 
     * # 69 M 1085 1629 x1
     * # Aldomet, Inderal
     * </pre>
     *
     * @param input an {@link InputStream} of the header info
     * @return a new {@link SingleSegmentHeader} instance
     * @throws IOException    if the input is invalid
     * @throws ParseException if the text can't be parsed
     * @see SingleSegmentRecord
     */
    public static SingleSegmentHeader parse(InputStream input) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HeaderRecord headerRecord = null;
        HeaderSignal[] headerSignals = null;
        int signalIndex = 0;
        String headerLine;
        boolean headerRecordProcessed = false;
        while ((headerLine = reader.readLine()) != null) {
            String stripedHeaderLine = headerLine.strip();
            if (stripedHeaderLine.isEmpty() || stripedHeaderLine.charAt(0) == '#') {
                continue;
            }
            if (!headerRecordProcessed) {
                headerRecord = HeaderRecord.parse(stripedHeaderLine);
                headerSignals = new HeaderSignal[headerRecord.numberOfSignals()];
                headerRecordProcessed = true;
            } else {
                headerSignals[signalIndex] = HeaderSignal.parse(stripedHeaderLine);
                signalIndex++;
            }
        }
        return new SingleSegmentHeader(headerRecord, headerSignals);
    }

    /**
     * Group the header signals by filename.
     *
     * @return the group of header signals
     */
    public Map<String, List<HeaderSignal>> groupHeaderSignalsByFilename() {
        return Arrays.stream(headerSignals)
                .collect(Collectors.groupingBy(HeaderSignal::filename, LinkedHashMap::new, Collectors.toList()));
    }

    /**
     * Returns the single-segment header representation.
     *
     * @return the text block representation.
     */
    public String toTextBlock() {
        StringBuilder builder = new StringBuilder();
        builder.append(headerRecord.toTextLine());
        for (HeaderSignal headerSignal : headerSignals) {
            builder.append("\n");
            builder.append(headerSignal.toTextLine());
        }
        return builder.toString();
    }

    /**
     * Generate a copy of the header with the checksum of the singals.
     *
     * @param samplesPerSignal the array of samples per singal
     * @return a new {@link SingleSegmentHeader} instance
     */
    public SingleSegmentHeader generateChecksumCopy(int[][] samplesPerSignal) {
        HeaderSignal[] newHeaderSignals = new HeaderSignal[headerSignals.length];
        for (int i = 0; i < headerSignals.length; i++) {
            HeaderSignal headerSignal = headerSignals[i];
            int initialValue = samplesPerSignal[i][0];
            int checksum = HeaderSignal.calculateChecksum(samplesPerSignal[i]);
            newHeaderSignals[i] = new HeaderSignal(headerSignal.filename(), headerSignal.format(),
                    headerSignal.samplesPerFrame(), headerSignal.skew(), headerSignal.bytesOffset(),
                    headerSignal.adcGain(), headerSignal.baseline(), headerSignal.unit(), headerSignal.adcResolution(),
                    headerSignal.adcZero(), initialValue, checksum, headerSignal.blockSize(),
                    headerSignal.description());
        }
        return new SingleSegmentHeader(headerRecord, newHeaderSignals);
    }

    @Override
    public String toString() {
        return "SingleSegmentHeader [headerRecord = " + headerRecord + ", headerSignals = "
                + Arrays.toString(headerSignals) + "]";
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof SingleSegmentHeader instance && headerRecord.equals(instance.headerRecord)
                && Arrays.equals(headerSignals, instance.headerSignals);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(headerRecord);
        return 31 * result + Arrays.hashCode(headerSignals);
    }
}
