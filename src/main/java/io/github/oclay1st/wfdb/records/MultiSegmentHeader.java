package io.github.oclay1st.wfdb.records;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Objects;

import io.github.oclay1st.wfdb.exceptions.ParseException;

/**
 * Represent the info from a multi-segment header
 *
 * @param record   the header record
 * @param segments the array of header segments
 * @see HeaderRecord
 * @see HeaderSegment
 */
public record MultiSegmentHeader(HeaderRecord record, HeaderSegment[] segments, String comments) { // NOSONAR

    /**
     * Parse the multi-segment header from an input form.
     *
     * <pre>
     * # As an example of the header file text:
     * multi/3 2 360 45000
     * 100s 21600
     * null 1800
     * 100s 21600
     * </pre>
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
        StringBuilder commentsBuilder = new StringBuilder();
        while ((headerLine = reader.readLine()) != null) {
            String stripedHeaderLine = headerLine.strip();
            if (stripedHeaderLine.isEmpty()) {
                continue;
            }
            if (stripedHeaderLine.charAt(0) == '#') {
                commentsBuilder.append('\n').append(stripedHeaderLine);
            } else if (!headerRecordProcessed) {
                headerRecord = HeaderRecord.parse(stripedHeaderLine);
                headerSegments = new HeaderSegment[headerRecord.numberOfSegments()];
                headerRecordProcessed = true;
            } else {
                headerSegments[segmentIndex] = HeaderSegment.parse(stripedHeaderLine);
                segmentIndex++;
            }
        }
        return new MultiSegmentHeader(headerRecord, headerSegments, commentsBuilder.toString());
    }

    /**
     * Returns the multi-segment header representation
     *
     * @return the text block representation
     */
    public String toTextBlock() {
        StringBuilder builder = new StringBuilder();
        builder.append(record.toTextLine());
        for (HeaderSegment headerSegment : segments) {
            builder.append('\n').append(headerSegment.toTextLine());
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "MultiSegmentHeader [headerRecord = " + record + ", headerSegments = "
                + Arrays.toString(segments) + "]";
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof MultiSegmentHeader instance && record.equals(instance.record)
                && Arrays.equals(segments, instance.segments);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(record) + Arrays.hashCode(segments);
    }

}
