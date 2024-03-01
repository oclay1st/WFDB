package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
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
        SingleSegmentRecord wfdbRecord = SingleSegmentRecord.parse(recordPath);
        assertNotNull(wfdbRecord);
        assertNotNull(wfdbRecord.header());
        assertNotNull(wfdbRecord.samplesPerSignal());
        assertEquals(12, wfdbRecord.samplesPerSignal().length);
        assertEquals(12, wfdbRecord.header().record().numberOfSignals());
        assertEquals(wfdbRecord.header().signals()[0].initialValue(), wfdbRecord.samplesPerSignal()[0][0]);
        assertEquals(wfdbRecord.header().signals()[1].initialValue(), wfdbRecord.samplesPerSignal()[1][0]);
        assertEquals(wfdbRecord.header().signals()[2].initialValue(), wfdbRecord.samplesPerSignal()[2][0]);
        assertEquals(wfdbRecord.header().signals()[3].initialValue(), wfdbRecord.samplesPerSignal()[3][0]);
        assertEquals(wfdbRecord.header().signals()[4].initialValue(), wfdbRecord.samplesPerSignal()[4][0]);
        assertEquals(wfdbRecord.header().signals()[5].initialValue(), wfdbRecord.samplesPerSignal()[5][0]);
        assertEquals(wfdbRecord.header().signals()[6].initialValue(), wfdbRecord.samplesPerSignal()[6][0]);
        assertEquals(wfdbRecord.header().signals()[7].initialValue(), wfdbRecord.samplesPerSignal()[7][0]);
        assertEquals(wfdbRecord.header().signals()[8].initialValue(), wfdbRecord.samplesPerSignal()[8][0]);
        assertEquals(wfdbRecord.header().signals()[9].initialValue(), wfdbRecord.samplesPerSignal()[9][0]);
        assertEquals(wfdbRecord.header().signals()[10].initialValue(), wfdbRecord.samplesPerSignal()[10][0]);
        assertEquals(wfdbRecord.header().signals()[11].initialValue(), wfdbRecord.samplesPerSignal()[11][0]);
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
        SingleSegmentRecord wfdbRecord = SingleSegmentRecord.parse(recordPath, filter);
        assertNotNull(wfdbRecord);
        assertNotNull(wfdbRecord.header());
        assertNotNull(wfdbRecord.samplesPerSignal());
        assertEquals(3, wfdbRecord.samplesPerSignal().length);
        assertEquals(3, wfdbRecord.header().record().numberOfSignals());
        assertEquals(Duration.ofSeconds(5), wfdbRecord.header().record().durationTime());
    }

    @Test
    @DisplayName("Should parse multi-format record")
    void shouldParseMultiFormatRecord() throws ParseException, IOException {
        Path recordPath = Path.of("src", "test", "resources", "single-segment", "all-formats", "binformats")
                .toAbsolutePath();
        SingleSegmentRecord wfdbRecord = SingleSegmentRecord.parse(recordPath);
        assertNotNull(wfdbRecord);
    }

    @Test
    @DisplayName("Should export the record")
    void shouldExportTheRecord() throws IOException, ParseException {
        Path recordPath = Path.of("src", "test", "resources", "single-segment", "00001", "00001_lr").toAbsolutePath();
        SingleSegmentRecord wfdbRecord = SingleSegmentRecord.parse(recordPath);
        assertNotNull(wfdbRecord);
        Path exportedRecordPath = Files.createTempDirectory("export-record").resolve("00001_lr");
        wfdbRecord.export(exportedRecordPath);
        SingleSegmentRecord exportedRecord = SingleSegmentRecord.parse(exportedRecordPath);
        assertNotNull(exportedRecordPath);
        assertEquals(wfdbRecord, exportedRecord);
    }

}
