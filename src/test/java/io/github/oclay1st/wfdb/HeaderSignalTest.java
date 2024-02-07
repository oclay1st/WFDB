package io.github.oclay1st.wfdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class HeaderSignalTest {

    @Test
    @DisplayName("Should parse millivot as default unit if not present")
    void shouldParseMillivoltAsDefaultUnit() throws ParseException {
        String headerSignalText = "d1.7001 8 100 10 0 -69 15626 0 ECG signal 1";
        HeaderSignal headerSignal = HeaderSignal.parse(headerSignalText);
        assertEquals("mV", headerSignal.units());
    }

    @Test
    @DisplayName("Should parse the header signal info of 212 bits representation")
    void shouldParseSignal() throws ParseException {
        String headerSignalText = "100.dat 212 200 11 1024 995 -22131 0 MLII";
        HeaderSignal headerSignal = HeaderSignal.parse(headerSignalText);
        assertEquals("100.dat", headerSignal.filename());
        assertEquals(212, headerSignal.format());
        assertEquals(200f, headerSignal.adcGain());
        assertEquals("mV", headerSignal.units());
        assertEquals(11, headerSignal.adcResolution());
        assertEquals(1024, headerSignal.adcZero());
        assertEquals(995, headerSignal.initialValue());
        assertEquals(-22131, headerSignal.checksum());
        assertEquals(0, headerSignal.blockSize());
        assertEquals("MLII", headerSignal.description());
    }

    @ParameterizedTest(name = "in {0}")
    @ValueSource(strings = {
            "1.dat 16 1000.0(0)/mV 16 0 -26 61447 0 V4",
            "2.dat 212 200 11 1024 1011 20052 0 I",
            "d0.7001 8 100 10 0 -53 -1279 0 ECG signal 0",
    })
    @DisplayName("Should have same value for adc zero and baseline")
    void shouldHaveSameValueForAdcZeroAndBaseline(String headerSignalText) throws ParseException {
        HeaderSignal headerSignal = HeaderSignal.parse(headerSignalText);
        assertEquals(headerSignal.adcZero(), headerSignal.baseline());
    }

    @Test
    @DisplayName("Should have different value for adc zero and baseline")
    void shouldHaveDifferentValueForAdcZeroAndBaseline() throws ParseException {
        String headerSignalText = "00001_lr.dat 16 1000.0(0)/mV 16 211 -26 61447 0 V4";
        HeaderSignal headerSignal = HeaderSignal.parse(headerSignalText);
        assertEquals(0, headerSignal.baseline());
        assertNotEquals(headerSignal.adcZero(), headerSignal.baseline());
    }

    @Test
    @DisplayName("Should calculate the checksum of a signal samples")
    void shouldCalcaulateTheChecksum() {
        int[] samples = { 2, 4, 6, 8 };
        int checksum = HeaderSignal.calculateChecksum(samples);
        assertEquals(20, checksum);
    }

}
