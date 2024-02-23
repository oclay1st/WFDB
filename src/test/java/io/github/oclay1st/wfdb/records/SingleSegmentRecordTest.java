package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.exceptions.ParseException;
import io.github.oclay1st.wfdb.filters.Filter;

class SingleSegmentRecordTest {

    @Test
    @DisplayName("Should parse the waveform file and header")
    void shouldParseWaveformFile() throws IOException, ParseException {
        Path recordPath = Path.of("src", "test", "resources", "single-segment", "00001", "00001_lr").toAbsolutePath();
        SingleSegmentRecord record = SingleSegmentRecord.parse(recordPath);
        assertNotNull(record);
        assertNotNull(record.header());
        assertNotNull(record.samplesPerSignal());
        assertEquals(12, record.samplesPerSignal().length);
        assertEquals(12, record.header().headerRecord().numberOfSignals());
        assertEquals(record.header().headerSignals()[0].initialValue(), record.samplesPerSignal()[0][0]);
        assertEquals(record.header().headerSignals()[1].initialValue(), record.samplesPerSignal()[1][0]);
        assertEquals(record.header().headerSignals()[2].initialValue(), record.samplesPerSignal()[2][0]);
        assertEquals(record.header().headerSignals()[3].initialValue(), record.samplesPerSignal()[3][0]);
        assertEquals(record.header().headerSignals()[4].initialValue(), record.samplesPerSignal()[4][0]);
        assertEquals(record.header().headerSignals()[5].initialValue(), record.samplesPerSignal()[5][0]);
        assertEquals(record.header().headerSignals()[6].initialValue(), record.samplesPerSignal()[6][0]);
        assertEquals(record.header().headerSignals()[7].initialValue(), record.samplesPerSignal()[7][0]);
        assertEquals(record.header().headerSignals()[8].initialValue(), record.samplesPerSignal()[8][0]);
        assertEquals(record.header().headerSignals()[9].initialValue(), record.samplesPerSignal()[9][0]);
        assertEquals(record.header().headerSignals()[10].initialValue(), record.samplesPerSignal()[10][0]);
        assertEquals(record.header().headerSignals()[11].initialValue(), record.samplesPerSignal()[11][0]);
    }

    @Test
    @DisplayName("Should parse and filter the waveform file")
    void shouldParseAndFilterFromFile() throws IOException, ParseException {
        Path recordPath = Path.of("src", "test", "resources", "single-segment", "00001", "00001_lr").toAbsolutePath();
        Filter filter = new Filter.Builder()
                .startTime(0)
                .endTime(5000)
                .signals(new int[] { 0, 1, 2 })
                .build();
        SingleSegmentRecord record = SingleSegmentRecord.parse(recordPath, filter);
        assertNotNull(record);
        assertNotNull(record.header());
        assertNotNull(record.samplesPerSignal());
        assertEquals(3, record.samplesPerSignal().length);
        assertEquals(3, record.header().headerRecord().numberOfSignals());
        assertEquals(Duration.ofSeconds(5), record.header().headerRecord().durationTime());
    }

    @Test
    @DisplayName("Should parse multiformat record")
    void shouldParseMultiformatRecord() throws ParseException, IOException {
        Path recordPath = Path.of("src", "test", "resources", "single-segment", "all-formats", "binformats")
                .toAbsolutePath();
        SingleSegmentRecord record = SingleSegmentRecord.parse(recordPath);
        assertNotNull(record);
    }

}
