package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WFDBTest {

    @Test
    @DisplayName("Should parse the waveform file and header")
    void shouldParseWaveformFile() throws IOException, ParseException {
        InputStream headerInput = WFDBTest.class.getClassLoader().getResourceAsStream("00001_lr.hea");
        InputStream samplingInput = WFDBTest.class.getClassLoader().getResourceAsStream("00001_lr.dat");
        WFDB wfdb = WFDB.parse(headerInput, samplingInput);
        assertNotNull(wfdb);
        assertNotNull(wfdb.header());
        assertNotNull(wfdb.samplesPerSingal());
    }


}
