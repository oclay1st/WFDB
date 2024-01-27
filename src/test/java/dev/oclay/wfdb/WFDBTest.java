package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WFDBTest {

    @Test
    @DisplayName("Should parse the waveform file and header")
    void shouldParseWaveformFile() throws IOException, ParseException {
        var headerInput = WFDBTest.class.getClassLoader().getResourceAsStream("00001_lr.hea");
        var samplingInput = WFDBTest.class.getClassLoader().getResourceAsStream("00001_lr.dat");
        var wfdb = WFDB.parse(headerInput, samplingInput);
        assertNotNull(wfdb);
        assertNotNull(wfdb.header());
        assertNotNull(wfdb.samplesPerSingal());
    }


}
