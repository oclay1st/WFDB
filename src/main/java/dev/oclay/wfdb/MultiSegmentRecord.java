package dev.oclay.wfdb;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public record MultiSegmentRecord(MultiSegmentHeader header, SingleSegmentRecord[] records) {

    public static MultiSegmentRecord parse(Path recordPath) throws IOException, ParseException {
        Path headerFilePath = recordPath.resolveSibling(recordPath.getFileName() + ".hea");
        try (InputStream inputStream = Files.newInputStream(headerFilePath)) {
            // Parse the multi segment header file
            MultiSegmentHeader header = MultiSegmentHeader.parse(inputStream);
            int recordIndex = 0;
            SingleSegmentRecord[] records = new SingleSegmentRecord[header.headerRecord().numberOfSamples()];
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
