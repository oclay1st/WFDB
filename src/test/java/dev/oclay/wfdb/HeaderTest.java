package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HeaderTest {

    @Test
    @DisplayName("Should parse the header ignoring comments and blank lines")
    void shouldParseHeaderIgnoringBlankLineAndComments() throws IOException, ParseException {
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
        Header header = Header.parse(headerInput);
        assertNotNull(header);
        System.out.println(header);
        assertNotNull(header.record());
        assertNotNull(header.signals());
        assertEquals(header.record().numberOfSignals(), header.signals().length);
        assertEquals(header.record().name(), "000006");
        assertEquals(header.record().samplingFrequency(), 100);
        assertEquals(header.record().numberOfSamples(), 1000);
        assertEquals(header.signals()[0].filename(), "000006.dat");
        assertEquals(header.signals()[0].format(), 16);
        assertEquals(header.signals()[0].units(), "mV");
        assertEquals(header.signals()[0].checksum(), 755);
        assertEquals(header.signals()[0].description(), "ii");
    }

    @Test
    @DisplayName("Should parse the header")
    void shouldParseHeader() throws IOException, ParseException {
        String headerText = """
                16 12 500 5000
                16.dat 16 1244(0)/mV 0 0 -17 3038 0 i""";
        ByteArrayInputStream headerInput = new ByteArrayInputStream(headerText.getBytes());
        Header header = Header.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.signals());
        assertNotNull(header.record());
        assertEquals(header.record().numberOfSignals(), header.signals().length);
        assertEquals(header.record().name(), "16");
        assertEquals(header.record().samplingFrequency(), 500);
        assertEquals(header.record().numberOfSamples(), 5000);
        assertEquals(header.signals()[0].filename(), "16.dat");
        assertEquals(header.signals()[0].format(), 16);
        assertEquals(header.signals()[0].units(), "mV");
        assertEquals(header.signals()[0].checksum(), 3038);
        assertEquals(header.signals()[0].description(), "i");
    }

    @Test
    @DisplayName("Should parse the header from a file")
    void shouldParseHeaderFromFile() throws IOException, ParseException {
        InputStream headerInput = WFDBTest.class.getClassLoader().getResourceAsStream("00001_lr.hea");
        Header header = Header.parse(headerInput);
        assertNotNull(header);
        assertNotNull(header.signals());
        assertNotNull(header.record());
        assertEquals(header.signals().length, 12);
        assertEquals(header.record().name(), "00001_lr");
        assertEquals(header.record().samplingFrequency(), 100);
        assertEquals(header.record().numberOfSamples(), 1000);
    }
}
