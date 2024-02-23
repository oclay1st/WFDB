package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class MultiSegmentHeaderTest {

    @Test
    @DisplayName("Should parse the multi segment header")
    void shouldParseTheMultiSegmentHeader() throws IOException, ParseException {
        String headerText = """
                multi/2 4 360 45000
                test 22500
                other 22500""";
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        MultiSegmentHeader header = MultiSegmentHeader.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.headerRecord());
        assertTrue(header.headerRecord().isMultiSegment());
        assertEquals("multi", header.headerRecord().name());
        assertEquals(4, header.headerRecord().numberOfSignals());
        assertEquals(360, header.headerRecord().samplingFrequency());
        assertEquals(45000, header.headerRecord().numberOfSamplesPerSignal());
        assertEquals(2, header.headerSegments().length);
        assertEquals("test", header.headerSegments()[0].name());
        assertEquals(22500, header.headerSegments()[0].numberOfSamplesPerSignal());
        assertEquals("other", header.headerSegments()[1].name());
        assertEquals(22500, header.headerSegments()[1].numberOfSamplesPerSignal());
    }

    @Test
    @DisplayName("Should parse the multi segment header ignorig comments and blank lines")
    void shouldIgnoreCommentsAndBlankLines() throws IOException, ParseException {
        String headerText = """
                multi/3 2 360 45000

                100s 21600

                null 1800
                #Multi segment header
                100s 21600""";
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        MultiSegmentHeader header = MultiSegmentHeader.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.headerRecord());
        assertTrue(header.headerRecord().isMultiSegment());
        assertEquals(3, header.headerSegments().length);
    }

}
