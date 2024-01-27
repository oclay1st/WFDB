package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RecordTest {

    @Test
    @DisplayName("Shoul parse record")
    void shouldParseRecord() throws ParseException {
        String recordText = "100 2 360 650000 0:0:0 10/01/2001";
        Record record = Record.parse(recordText);
        assertEquals(record.name(), "100");
        assertEquals(record.numberOfSegments(), 0);
        assertEquals(record.numberOfSignals(), 2);
        assertEquals(record.samplingFrequency(), 360f);
        assertEquals(record.counterFrequency(), 360f);
        assertEquals(record.baseCounter(), 0);
        assertEquals(record.numberOfSamples(), 650000);
        assertEquals(record.baseTime(), LocalTime.of(0, 0, 0));
        assertEquals(record.baseDate(), LocalDate.of(2001, 01, 10));
    }

    @Test
    @DisplayName("Shoul parse record with name, number of signals, frequency and number of samples")
    void parseSmallRecordInfo() throws ParseException {
        String recordText = "00001_lr 12 100 1000";
        Record record = Record.parse(recordText);
        assertEquals(record.name(), "00001_lr");
        assertEquals(record.numberOfSignals(), 12);
        assertEquals(record.samplingFrequency(), 100f);
        assertEquals(record.counterFrequency(), 100f);
        assertEquals(record.numberOfSamples(), 1000);
    }

}
