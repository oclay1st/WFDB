package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HeaderRecordTest {

    @Test
    @DisplayName("Shoul parse the header record")
    void shouldParseRecord() throws ParseException {
        String headerRecordText = "100 2 360 650000 0:0:0 10/01/2001";
        HeaderRecord headerRecord = HeaderRecord.parse(headerRecordText);
        assertEquals(headerRecord.name(), "100");
        assertEquals(headerRecord.numberOfSegments(), 0);
        assertEquals(headerRecord.numberOfSignals(), 2);
        assertEquals(headerRecord.samplingFrequency(), 360f);
        assertEquals(headerRecord.counterFrequency(), 360f);
        assertEquals(headerRecord.baseCounter(), 0);
        assertEquals(headerRecord.numberOfSamples(), 650000);
        assertEquals(headerRecord.baseTime(), LocalTime.of(0, 0, 0));
        assertEquals(headerRecord.baseDate(), LocalDate.of(2001, 01, 10));
    }

    @Test
    @DisplayName("Shoul parse the header record with name, number of signals, frequency and number of samples")
    void parseSmallRecordInfo() throws ParseException {
        String headerRecordText = "00001_lr 12 100 1000";
        HeaderRecord headerRecord = HeaderRecord.parse(headerRecordText);
        assertEquals(headerRecord.name(), "00001_lr");
        assertEquals(headerRecord.numberOfSignals(), 12);
        assertEquals(headerRecord.samplingFrequency(), 100f);
        assertEquals(headerRecord.counterFrequency(), 100f);
        assertEquals(headerRecord.numberOfSamples(), 1000);
    }

}
