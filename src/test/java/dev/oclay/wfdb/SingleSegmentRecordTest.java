package dev.oclay.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SingleSegmentRecordTest {

    @Test
    @DisplayName("Should parse the waveform file and header")
    void shouldParseWaveformFile() throws IOException, ParseException {
        Path recordPath = Path.of("src", "test", "resources", "00001_lr").toAbsolutePath();
        SingleSegmentRecord record = SingleSegmentRecord.parse(recordPath);
        assertNotNull(record);
        assertNotNull(record.header());
        assertNotNull(record.samplesPerSingal());
        assertEquals(12, record.samplesPerSingal().length);
        assertEquals(12, record.header().headerRecord().numberOfSignals());
        assertEquals(record.header().headerSignals()[0].initialValue(), record.samplesPerSingal()[0][0]);
        assertEquals(record.header().headerSignals()[1].initialValue(), record.samplesPerSingal()[1][0]);
        assertEquals(record.header().headerSignals()[2].initialValue(), record.samplesPerSingal()[2][0]);
        assertEquals(record.header().headerSignals()[3].initialValue(), record.samplesPerSingal()[3][0]);
        assertEquals(record.header().headerSignals()[4].initialValue(), record.samplesPerSingal()[4][0]);
        assertEquals(record.header().headerSignals()[5].initialValue(), record.samplesPerSingal()[5][0]);
        assertEquals(record.header().headerSignals()[6].initialValue(), record.samplesPerSingal()[6][0]);
        assertEquals(record.header().headerSignals()[7].initialValue(), record.samplesPerSingal()[7][0]);
        assertEquals(record.header().headerSignals()[8].initialValue(), record.samplesPerSingal()[8][0]);
        assertEquals(record.header().headerSignals()[9].initialValue(), record.samplesPerSingal()[9][0]);
        assertEquals(record.header().headerSignals()[10].initialValue(), record.samplesPerSingal()[10][0]);
        assertEquals(record.header().headerSignals()[11].initialValue(), record.samplesPerSingal()[11][0]);
    }

    @Test
    @DisplayName("should")
    void shouldParse() throws ParseException, IOException {
        Path path = Path.of("/run/media/oclay/Development/Work/uSinus/Dataset/212 Different Files/als1");
        SingleSegmentRecord record = SingleSegmentRecord.parse(path);
        assertEquals(record.header().headerSignals()[0].initialValue(), record.samplesPerSingal()[0][0]);
    }

    @Test
    @DisplayName("Should parse 32 bit record")
    void shouldParse32BitsRecord() throws ParseException, IOException {
        Path path = Path.of("/run/media/oclay/Development/Work/uSinus/Dataset/32 bits/00689D31-8491-4643-B3C8-45241FBBD47C");
        SingleSegmentRecord record = SingleSegmentRecord.parse(path);
        assertEquals(record.header().headerSignals()[0].initialValue(), record.samplesPerSingal()[0][0]);
    }

    @Test
    @DisplayName("Should parse 80 bit record")
    void shouldParse80BitsRecord() throws ParseException, IOException {
        Path path = Path.of("/run/media/oclay/Development/Work/uSinus/Dataset/80 bits/3544749_0001");
        SingleSegmentRecord record = SingleSegmentRecord.parse(path);
        assertEquals(record.header().headerSignals()[0].initialValue(), record.samplesPerSingal()[0][0]);
    }
}
