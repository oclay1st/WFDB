package io.github.oclay1st.wfdb.records;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.oclay1st.wfdb.exceptions.ParseException;

/**
 * Represent the info from a multi-segment header.
 *
 * @param record   the header record
 * @param segments the array of header segments
 * @param comments the comments about the record
 */
public record MultiSegmentHeader(HeaderRecord record, List<HeaderSegment> segments, String comments) { // NOSONAR

    /**
     * Creates an instance of a MultiSegmentHeader class.
     *
     * @param record   the header record. Can't be null.
     * @param segments the array of header segments. Can't be null.
     * @param comments the comments about the record. Can't be null.
     */
    public MultiSegmentHeader {
        Objects.requireNonNull(record);
        Objects.requireNonNull(segments);
        Objects.requireNonNull(comments);
    }

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
        List<HeaderSegment> headerSegments = new ArrayList<>();
        String headerLine;
        StringBuilder commentsBuilder = new StringBuilder();
        boolean headerRecordProcessed = false;
        while ((headerLine = reader.readLine()) != null) {
            String stripedHeaderLine = headerLine.strip();
            if (stripedHeaderLine.isEmpty()) {
                continue;
            }
            if (stripedHeaderLine.charAt(0) == '#') {
                commentsBuilder.append('\n').append(stripedHeaderLine);
            } else if (!headerRecordProcessed) {
                headerRecord = HeaderRecord.parse(stripedHeaderLine);
                headerRecordProcessed = true;
            } else {
                headerSegments.add(HeaderSegment.parse(stripedHeaderLine));
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
        if (!comments.isEmpty()) {
            builder.append('\n').append(comments);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return "MultiSegmentHeader [headerRecord = " + record + ", headerSegments = "
                + segments + ", comments = " + comments + "]";
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof MultiSegmentHeader instance && record.equals(instance.record)
                && segments.equals(instance.segments) && comments.equals(instance.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(record, segments, comments);
    }

}
