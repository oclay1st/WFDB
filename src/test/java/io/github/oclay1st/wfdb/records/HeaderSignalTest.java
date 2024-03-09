package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class HeaderSignalTest {

    @Test
    @DisplayName("Should parse millivolt as default unit if not present")
    void shouldParseMillivoltAsDefaultUnit() throws ParseException {
        String headerSignalText = "d1.7001 8 100 10 0 -69 15626 0 ECG signal 1";
        HeaderSignal headerSignal = HeaderSignal.parse(headerSignalText);
        assertEquals(SignalUnit.MILLIVOLT, headerSignal.unit());
    }

    @Test
    @DisplayName("Should parse the header signal info of 212 bits representation")
    void shouldParseSignal() throws ParseException {
        String headerSignalText = "100.dat 212 200 11 1024 995 -22131 0 II";
        HeaderSignal headerSignal = HeaderSignal.parse(headerSignalText);
        assertEquals("100.dat", headerSignal.filename());
        assertEquals(SignalFormat.FORMAT_212, headerSignal.format());
        assertEquals(200f, headerSignal.adcGain());
        assertEquals(SignalUnit.MILLIVOLT, headerSignal.unit());
        assertEquals(11, headerSignal.adcResolution());
        assertEquals(1024, headerSignal.adcZero());
        assertEquals(995, headerSignal.initialValue());
        assertEquals(-22131, headerSignal.checksum());
        assertEquals(0, headerSignal.blockSize());
        assertEquals("II", headerSignal.description());
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
    void shouldCalculateTheChecksum() {
        int[] samples = { 2, 4, 6, 8 };
        int checksum = HeaderSignal.calculateChecksum(samples);
        assertEquals(20, checksum);
    }

    @ParameterizedTest(name = "in {0}")
    @DisplayName("Should parse and generate the same text of header signal")
    @ValueSource(strings = {
            "signal.1 16x4 1000.0(0)/mV 16 0 10 56654 0 I",
            "signal.2 212x1 1000.0(0)/mV 212 0 -231 32423 0 AVR",
            "signal.3 212x1:3+1 1000.0(0)/mV 212 0 -231 32423 0 AVR",
    })
    void shouldParseAndGenerateTheSameText(String textLine) throws ParseException {
        HeaderSignal headerSignal = HeaderSignal.parse(textLine);
        assertEquals(textLine, headerSignal.toTextLine());
    }

    @ParameterizedTest(name = "in {0}")
    @DisplayName("Should match the checksum")
    @ValueSource(strings = {
            "signal.1 16 1000.0(0)/mV 16 0 10 -1638 0 I",
            "signal.1 16 1000.0(0)/mV 16 0 10 63898 0 I",
    })
    void shouldMatchChecksum(String headerSignalText) throws ParseException {
        HeaderSignal headerSignal = HeaderSignal.parse(headerSignalText);
        int[] samples = { 252123, 41562, 21933, 10424 };
        assertTrue(headerSignal.matchChecksum(samples));
    }

    @ParameterizedTest(name = ": {0}")
    @ValueSource(strings = { "**", "---", "signal mv" })
    @DisplayName("Should throw ParseException for input")
    void shouldThrowParseException(String headerSignalText) {
        assertThrows(ParseException.class, () -> HeaderSignal.parse(headerSignalText));
    }

}
