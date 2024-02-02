package com.github.oclay1st.wfdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Represent the info from a multi-segment header
 *
 * @param headerRecord   the header record
 * @param headerSegments the array of header segments
 * @see HeaderRecord
 * @see HeaderSegment
 */
public record MultiSegmentHeader(HeaderRecord headerRecord, HeaderSegment[] headerSegments) {

    /**
     * Parse the multi-segment header from an input form
     * As an example of the header file text:
     * multi/3 2 360 45000
     * 100s 21600
     * null 1800
     * 100s 21600
     *
     * @param input an {@link InputStream} of the header info
     * @return a new {@link MultiSegmentHeader} instance
     * @throws IOException    if the input is invalid
     * @throws ParseException if the text can't be parsed
     * @see MultiSegmentRecord
     */
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
