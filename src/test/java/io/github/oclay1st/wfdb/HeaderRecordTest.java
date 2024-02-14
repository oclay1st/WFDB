package io.github.oclay1st.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HeaderRecordTest {

    @Test
    @DisplayName("Should parse the header record")
    void shouldParseRecord() throws ParseException {
        String headerRecordText = "100 2 360 3600 0:0:0 10/01/2001";
        HeaderRecord headerRecord = HeaderRecord.parse(headerRecordText);
        assertEquals("100", headerRecord.name());
        assertEquals(1, headerRecord.numberOfSegments());
        assertEquals(2, headerRecord.numberOfSignals());
        assertEquals(360f, headerRecord.samplingFrequency());
        assertEquals(3600, headerRecord.numberOfSamplesPerSignal());
        assertEquals(LocalTime.of(0, 0, 0), headerRecord.baseTime());
        assertEquals(LocalDate.of(2001, 01, 10), headerRecord.baseDate());
        assertEquals(Duration.ofSeconds(10), headerRecord.durationTime());
    }

    @Test
    @DisplayName("Shoul parse the header record with name, number of signals, frequency and number of samples")
    void parseSmallRecordInfo() throws ParseException {
        String headerRecordText = "00001_lr 12 100 1000";
        HeaderRecord headerRecord = HeaderRecord.parse(headerRecordText);
        assertEquals("00001_lr", headerRecord.name());
        assertEquals(12, headerRecord.numberOfSignals());
        assertEquals(100f, headerRecord.samplingFrequency());
        assertEquals(1000, headerRecord.numberOfSamplesPerSignal());
    }

}
