package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SingleSegmentRecordTest {

    @Test
    @DisplayName("Should parse the waveform file and header")
    void shouldParseWaveformFile() throws IOException, ParseException {
        InputStream headerInput = SingleSegmentRecordTest.class.getClassLoader().getResourceAsStream("00001_lr.hea");
        InputStream samplingInput = SingleSegmentRecordTest.class.getClassLoader().getResourceAsStream("00001_lr.dat");
        SingleSegmentRecord record = SingleSegmentRecord.parse(headerInput, samplingInput);
        assertNotNull(record);
        assertNotNull(record.header());
        assertNotNull(record.samplesPerSingal());
    }


}
