package io.github.oclay1st.wfdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Represents a single segment record header
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

    @Override
    public String toString() {
        return "SingleSegmentHeader [headerRecord = " + headerRecord + ", headerSignals = "
                + Arrays.toString(headerSignals) + "]";
    }

    /**
     * Check if all the header signals have the same signal samples file.
     *
     * @return true if all header signal match, otherwise false.
     */
    public boolean hasSingleSamplesSource() {
        return Arrays.stream(headerSignals)
                .allMatch(s -> s.filename().equals(headerSignals[0].filename()));
    }

}
