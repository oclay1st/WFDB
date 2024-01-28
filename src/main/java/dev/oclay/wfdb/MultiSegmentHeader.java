package dev.oclay.wfdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public record MultiSegmentHeader(HeaderRecord headerRecord, HeaderSegment[] headerSegments) {

    public static MultiSegmentHeader parse(InputStream input) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        HeaderRecord headerRecord = null;
        HeaderSegment[] headerSegments = null;
        int segmentIndex = 0;
        String headerLine;
        boolean headerRecordProcessed = false;
        while ((headerLine = reader.readLine()) != null) {
            String stripedHeaderLine = headerLine.strip();
            if (stripedHeaderLine.isEmpty() || stripedHeaderLine.charAt(0) == '#') {
                continue;
            }
            if (!headerRecordProcessed) {
                headerRecord = HeaderRecord.parse(stripedHeaderLine);
                headerSegments = new HeaderSegment[headerRecord.numberOfSegments()];
                headerRecordProcessed = true;
            } else {
                headerSegments[segmentIndex] = HeaderSegment.parse(stripedHeaderLine);
                segmentIndex++;
            }
        }
        return new MultiSegmentHeader(headerRecord, headerSegments);
    }

    @Override
    public String toString() {
        return "MultiSegmentHeader [headerRecord = " + headerRecord + ", headerSegments = "
                + Arrays.toString(headerSegments) + "]";
    }

}
