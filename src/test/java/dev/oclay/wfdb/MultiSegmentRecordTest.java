package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MultiSegmentRecordTest {

    @Test
    @DisplayName("Should parse multi segment records")
    @Disabled
    void shouldParseMultiSegmentRecord() throws IOException, ParseException {
        Path recordPath = Path.of("src", "test", "resources", "multi-segment", "041s", "041s");
        MultiSegmentRecord record = MultiSegmentRecord.parse(recordPath);
        assertNotNull(record);
    }

}
