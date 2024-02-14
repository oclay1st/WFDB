package io.github.oclay1st.wfdb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

/**
 * Represents a multi-segment record
 *
 * @param header  the multi-segment header {@link MultiSegmentHeader}
 * @param records the array of single-segment records
 */
public record MultiSegmentRecord(MultiSegmentHeader header, SingleSegmentRecord[] records) {

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
            // Parse the multi segment header file
            MultiSegmentHeader header = MultiSegmentHeader.parse(inputStream);
            int recordIndex = 0;
            SingleSegmentRecord[] singleSegmentRecords = new SingleSegmentRecord[header.headerRecord()
                    .numberOfSamplesPerSignal()];
            for (HeaderSegment segment : header.headerSegments()) {
                Path segmentRecordPath = recordPath.resolveSibling(segment.name());
                SingleSegmentRecord singleSegmentRecord = SingleSegmentRecord.parse(segmentRecordPath);
                singleSegmentRecords[recordIndex] = singleSegmentRecord;
                recordIndex++;
            }
            return new MultiSegmentRecord(header, singleSegmentRecords);
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof MultiSegmentRecord instance) {
            return header.equals(instance.header) && Arrays.equals(records, instance.records);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(header);
        result = 31 * result + Arrays.hashCode(records);
        return result;
    }

}
