package io.github.oclay1st.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SingleSegmentHeaderTest {

    @Test
    @DisplayName("Should parse the single segment header ignoring comments and blank lines")
    void shouldIgnoreCommentsAndBlankLines() throws IOException, ParseException {
        String headerText = """
                #<age>: 60
                #<sex>: M
                #<diagnoses>:
                #Rhythm: Sinus rhythm.

                000006 1 100 1000

                000006.dat 16 1322(0)/mV 0 0 -37 755 0 ii

                #Electric axis of the heart: normal.
                #Left atrial hypertrophy.
                #Left ventricular hypertrophy.""";
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        SingleSegmentHeader header = SingleSegmentHeader.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.headerRecord());
        assertNotNull(header.headerSignals());
        assertFalse(header.headerRecord().isMultiSegment());
        assertEquals(header.headerRecord().numberOfSignals(), header.headerSignals().length);
        assertEquals("000006", header.headerRecord().name());
        assertEquals(100, header.headerRecord().samplingFrequency());
        assertEquals(1000, header.headerRecord().numberOfSamplesPerSignal());
        assertEquals("000006.dat", header.headerSignals()[0].filename());
        assertEquals(SignalFormat.FORMAT_16, header.headerSignals()[0].format());
        assertEquals(SignalUnit.MILLIVOLT, header.headerSignals()[0].unit());
        assertEquals(755, header.headerSignals()[0].checksum());
        assertEquals("ii", header.headerSignals()[0].description());
        headerInput.close();
    }

    @Test
    @DisplayName("Should parse the single segment header")
    void shouldParseTheSingleSegmentHeader() throws IOException, ParseException {
        String headerText = """
                16 1 500 5000
                16.dat 16 1244(0)/mV 0 0 -17 3038 0 i""";
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        SingleSegmentHeader header = SingleSegmentHeader.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.headerSignals());
        assertNotNull(header.headerRecord());
        assertFalse(header.headerRecord().isMultiSegment());
        assertEquals(header.headerSignals().length, header.headerRecord().numberOfSignals());
        assertEquals("16", header.headerRecord().name());
        assertEquals(500, header.headerRecord().samplingFrequency());
        assertEquals(5000, header.headerRecord().numberOfSamplesPerSignal());
        assertEquals("16.dat", header.headerSignals()[0].filename());
        assertEquals(SignalFormat.FORMAT_16, header.headerSignals()[0].format());
        assertEquals(SignalUnit.MILLIVOLT, header.headerSignals()[0].unit());
        assertEquals(3038, header.headerSignals()[0].checksum());
        assertEquals("i", header.headerSignals()[0].description());
        headerInput.close();
    }

    @ParameterizedTest(name = ": {0}")
    @ValueSource(strings = {  "**", "---" })
    @DisplayName("Should throw ParseException for input")
    void shouldThrowParseException(String headerText) {
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        assertThrows(ParseException.class, () -> SingleSegmentHeader.parse(headerInput));
    }

}
