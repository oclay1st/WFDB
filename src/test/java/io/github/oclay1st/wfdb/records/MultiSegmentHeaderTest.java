package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class MultiSegmentHeaderTest {

    @Test
    @DisplayName("Should parse the multi-segment header")
    void shouldParseTheMultiSegmentHeader() throws IOException, ParseException {
        String headerText = """
                multi/2 4 360 45000
                test 22500
                other 22500""";
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        MultiSegmentHeader header = MultiSegmentHeader.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.record());
        assertTrue(header.record().isMultiSegment());
        assertEquals("multi", header.record().name());
        assertEquals(4, header.record().numberOfSignals());
        assertEquals(360, header.record().samplingFrequency());
        assertEquals(45000, header.record().numberOfSamplesPerSignal());
        assertEquals(2, header.segments().size());
        assertEquals("test", header.segments().get(0).name());
        assertEquals(22500, header.segments().get(0).numberOfSamplesPerSignal());
        assertEquals("other", header.segments().get(1).name());
        assertEquals(22500, header.segments().get(1).numberOfSamplesPerSignal());
    }

    @Test
    @DisplayName("Should parse the multi-segment header ignoring comments and blank lines")
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
        assertNotNull(header.record());
        assertTrue(header.record().isMultiSegment());
        assertEquals(3, header.segments().size());
    }

    @ParameterizedTest(name = "in {0}")
    @DisplayName("Should parse and generate the same text block of the multi-segment header")
    @ValueSource(strings = {
            """
                    sample/2 1 100.0 1000
                    sample_0 500
                    sample_1 500""",
            """
                    multi/3 2 360.0 45000
                    100s 21600
                    null 1800
                    100s 21600"""
    })
    void shouldParseAndGenerateTheSameText(String textBlock) throws IOException, ParseException {
        InputStream inputStream = new ByteArrayInputStream(textBlock.getBytes());
        MultiSegmentHeader header = MultiSegmentHeader.parse(inputStream);
        assertEquals(textBlock, header.toTextBlock());
    }
}
