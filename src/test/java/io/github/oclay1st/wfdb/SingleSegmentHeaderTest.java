package io.github.oclay1st.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
        assertEquals(header.headerRecord().numberOfSignals(), header.headerSignals().length);
        assertEquals("000006", header.headerRecord().name());
        assertEquals(100, header.headerRecord().samplingFrequency());
        assertEquals(1000, header.headerRecord().numberOfSamplesPerSignal());
        assertEquals("000006.dat", header.headerSignals()[0].filename());
        assertEquals(16, header.headerSignals()[0].format());
        assertEquals("mV", header.headerSignals()[0].units());
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
        assertEquals(header.headerSignals().length, header.headerRecord().numberOfSignals());
        assertEquals("16", header.headerRecord().name());
        assertEquals(500, header.headerRecord().samplingFrequency());
        assertEquals(5000, header.headerRecord().numberOfSamplesPerSignal());
        assertEquals("16.dat", header.headerSignals()[0].filename());
        assertEquals(16, header.headerSignals()[0].format());
        assertEquals("mV", header.headerSignals()[0].units());
        assertEquals(3038, header.headerSignals()[0].checksum());
        assertEquals("i", header.headerSignals()[0].description());
        headerInput.close();
    }

}
