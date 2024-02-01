package dev.oclay.wfdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public record SingleSegmentHeader(HeaderRecord headerRecord, HeaderSignal[] headerSignals) {

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

}
