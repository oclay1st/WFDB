package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class HeaderRecordTest {

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
    @DisplayName("Should parse the header record with name, number of signals, frequency and number of samples")
    void parseSmallRecordInfo() throws ParseException {
        String headerRecordText = "00001_lr 12 100 1000";
        HeaderRecord headerRecord = HeaderRecord.parse(headerRecordText);
        assertEquals("00001_lr", headerRecord.name());
        assertEquals(12, headerRecord.numberOfSignals());
        assertEquals(100f, headerRecord.samplingFrequency());
        assertEquals(1000, headerRecord.numberOfSamplesPerSignal());
    }

    @ParameterizedTest(name = "in {0}")
    @DisplayName("Should parse and generate the same text of the single-header record")
    @ValueSource(strings = {
            "sample_0 2 500.0 5000",
            "sample_1 12 100.0 1000 11:20:30 10/10/2001",
            "sample_2 4 200.0/100.0(2.0) 400"
    })
    void shouldParseAndGenerateTheSameText(String textLine) throws ParseException {
        HeaderRecord headerRecord = HeaderRecord.parse(textLine);
        assertEquals(textLine, headerRecord.toTextLine());
    }

    @ParameterizedTest(name = ": {0}")
    @ValueSource(strings = { "record/f", "record/2 random", "record 12 100 e1000" })
    @DisplayName("Should throw ParseException for input")
    void shouldThrowParseException(String headerRecordText) {
        assertThrows(ParseException.class, () -> HeaderRecord.parse(headerRecordText));
    }

}
