package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class MultiSegmentRecordTest {

    @Test
    @DisplayName("Should parse multi-segment records")
    void shouldParseMultiSegmentRecord() throws IOException, ParseException {
        Path recordPath = Path.of("src", "test", "resources", "multi-segment", "v102s", "v102s");
        MultiSegmentRecord multiSegmentRecord = MultiSegmentRecord.parse(recordPath);
        assertNotNull(multiSegmentRecord);
        assertEquals(3, multiSegmentRecord.header().headerSegments().length);
    }

}
