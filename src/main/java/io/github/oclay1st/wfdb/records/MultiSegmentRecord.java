package io.github.oclay1st.wfdb.records;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.oclay1st.wfdb.exceptions.ParseException;

/**
 * Represents a multi-segment record.
 *
 * @param header  the multi-segment header {@link MultiSegmentHeader}
 * @param records the list of single-segment records
 */
public record MultiSegmentRecord(MultiSegmentHeader header, List<SingleSegmentRecord> records) {

    /**
     * Creates an instance of a MultiSegmentRecord class.
     *
     * @param header  the multi-segment header. Can't be null;
     * @param records the array of single-segment records. Can't be null.
     */
    public MultiSegmentRecord {
        Objects.requireNonNull(header);
        Objects.requireNonNull(records);
    }

    /**
     * Parse a multi-segment record
     *
     * @param recordPath the path from where the multi-segment record will be parsed
     * @return a new {@link MultiSegmentRecord} instance
     * @throws IOException    if the input is invalid
     * @throws ParseException if the text can't be parsed
     */
    public static MultiSegmentRecord parse(Path recordPath) throws IOException, ParseException {
        Path headerFilePath = recordPath.resolveSibling(recordPath.getFileName() + ".hea");
        try (InputStream inputStream = Files.newInputStream(headerFilePath)) {
            // Parse the multi-segment header file
            MultiSegmentHeader header = MultiSegmentHeader.parse(inputStream);
            List<SingleSegmentRecord> singleSegmentRecords = new ArrayList<>(header.segments().size());
            for (HeaderSegment segment : header.segments()) {
                Path segmentRecordPath = recordPath.resolveSibling(segment.name());
                singleSegmentRecords.add(SingleSegmentRecord.parse(segmentRecordPath));
            }
            return new MultiSegmentRecord(header, singleSegmentRecords);
        }
    }

    /**
     * Export the multi-segment record. Generate the header and signal(s) files.
     * 
     * @param recordPath the path of the record
     * @throws IOException if the record can't be exported.
     */
    public void export(Path recordPath) throws IOException {
        // Create the header file
        Path headerFilePath = recordPath.resolveSibling(recordPath.getFileName() + ".hea");
        Files.createDirectories(recordPath.getParent());
        Files.writeString(headerFilePath, header.toTextBlock());
        // Export the list of single-segment records
        for (SingleSegmentRecord singleSegmentRecord : records) {
            Path singleSegmentRecordPath = recordPath.resolveSibling(singleSegmentRecord.header().record().name());
            singleSegmentRecord.export(singleSegmentRecordPath);
        }
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof MultiSegmentRecord instance && header.equals(instance.header)
                && records.equals(instance.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, records);
    }

    @Override
    public String toString() {
        return "MultiSegmentRecord = [ header=" + header + ", records=" + records + ']';
    }

}
