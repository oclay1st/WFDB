package dev.oclay.wfdb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a multi-segment record
 *
 * @param header the multi-segment header {@link MultiSegmentHeader}
 * @param record the array of single-segment records {@link SingleSegmentRecord}
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
            SingleSegmentRecord[] records = new SingleSegmentRecord[header.headerRecord().numberOfSamplesPerSignal()];
            for (HeaderSegment segment : header.headerSegments()) {
                Path segmentRecordPath = recordPath.resolveSibling(segment.name());
                SingleSegmentRecord record = SingleSegmentRecord.parse(segmentRecordPath);
                records[recordIndex] = record;
                recordIndex++;
            }
            return new MultiSegmentRecord(header, records);
        }
    }
}
