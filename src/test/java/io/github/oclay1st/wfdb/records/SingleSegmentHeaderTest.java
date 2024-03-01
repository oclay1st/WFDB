package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class SingleSegmentHeaderTest {

    @Test
    @DisplayName("Should parse the single-segment header ignoring comments and blank lines")
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
        assertNotNull(header.record());
        assertNotNull(header.signals());
        assertFalse(header.record().isMultiSegment());
        assertEquals(header.record().numberOfSignals(), header.signals().length);
        assertEquals("000006", header.record().name());
        assertEquals(100, header.record().samplingFrequency());
        assertEquals(1000, header.record().numberOfSamplesPerSignal());
        assertEquals("000006.dat", header.signals()[0].filename());
        assertEquals(SignalFormat.FORMAT_16, header.signals()[0].format());
        assertEquals(SignalUnit.MILLIVOLT, header.signals()[0].unit());
        assertEquals(755, header.signals()[0].checksum());
        assertEquals("ii", header.signals()[0].description());
        headerInput.close();
    }

    @Test
    @DisplayName("Should parse the single-segment header")
    void shouldParseTheSingleSegmentHeader() throws IOException, ParseException {
        String headerText = """
                16 1 500 5000
                16.dat 16 1244(0)/mV 0 0 -17 3038 0 i""";
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        SingleSegmentHeader header = SingleSegmentHeader.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.signals());
        assertNotNull(header.record());
        assertFalse(header.record().isMultiSegment());
        assertEquals(header.signals().length, header.record().numberOfSignals());
        assertEquals("16", header.record().name());
        assertEquals(500, header.record().samplingFrequency());
        assertEquals(5000, header.record().numberOfSamplesPerSignal());
        assertEquals("16.dat", header.signals()[0].filename());
        assertEquals(SignalFormat.FORMAT_16, header.signals()[0].format());
        assertEquals(SignalUnit.MILLIVOLT, header.signals()[0].unit());
        assertEquals(3038, header.signals()[0].checksum());
        assertEquals("i", header.signals()[0].description());
        headerInput.close();
    }

    @ParameterizedTest(name = ": {0}")
    @ValueSource(strings = { "**", "---" })
    @DisplayName("Should throw ParseException for input")
    void shouldThrowParseException(String headerText) {
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        assertThrows(ParseException.class, () -> SingleSegmentHeader.parse(headerInput));
    }

    @ParameterizedTest(name = "in {0}")
    @DisplayName("Should parse and generete the same text block of the header")
    @ValueSource(strings = {
            """
                    sample_0 1 500.0 5000
                    signal.1 16x4 1000.0(0)/mV 16 0 10 56654 0 I""",
            """
                    sample_1 1 100.0 1000 11:20:30 10/10/2001
                    d0.7001 8x1 100.0(0)/mV 10 0 -53 -1279 0 ECG signal 0"""
    })
    void shouldParseAndGenerateTheSameText(String textLine) throws IOException, ParseException {
        InputStream inputStream = new ByteArrayInputStream(textLine.getBytes());
        SingleSegmentHeader header = SingleSegmentHeader.parse(inputStream);
        assertEquals(textLine, header.toTextBlock());
    }

}
