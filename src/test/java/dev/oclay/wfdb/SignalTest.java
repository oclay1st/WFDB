package dev.oclay.wfdb;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SignalTest {

    @Test
    @DisplayName("Should parse millivot as default unit if not present")
    void shouldParseMillivoltAsDefaultUnit() throws ParseException {
        String signalText = "d1.7001 8 100 10 0 -69 15626 0 ECG signal 1";
        Signal signal = Signal.parse(signalText);
        assertEquals(signal.units(), "mV");
    }

    @Test
    @DisplayName("Should parse signal info of 212 bits representation")
    void shouldParseSignal() throws ParseException {
        String signalText = "100.dat 212 200 11 1024 995 -22131 0 MLII";
        Signal signal = Signal.parse(signalText);
        assertEquals(signal.filename(), "100.dat");
        assertEquals(signal.format(), 212);
        assertEquals(signal.samplesPerFrame(), 1);
        assertEquals(signal.skew(), 0);
        assertEquals(signal.bytesOffset(), 0);
        assertEquals(signal.adcGain(), 200f);
        assertEquals(signal.baseline(), 1024);
        assertEquals(signal.units(), "mV");
        assertEquals(signal.adcResolution(), 11);
        assertEquals(signal.adcZero(), 1024);
        assertEquals(signal.initialValue(), 995);
        assertEquals(signal.checksum(), -22131);
        assertEquals(signal.blockSize(), 0);
        assertEquals(signal.description(), "MLII");
    }

    @ParameterizedTest(name = "in {0}")
    @ValueSource(strings = {
            "1.dat 16 1000.0(0)/mV 16 0 -26 61447 0 V4",
            "2.dat 212 200 11 1024 1011 20052 0 I",
            "d0.7001 8 100 10 0 -53 -1279 0 ECG signal 0",
    })
    @DisplayName("Should have same value for adc zero and baseline")
    void shouldHaveSameValueForAdcZeroAndBaseline(String signalText) throws ParseException {
        Signal signal = Signal.parse(signalText);
        assertEquals(signal.adcZero(), signal.baseline());
    }

    @Test
    @DisplayName("Should have different value for adc zero and baseline")
    void shouldHaveDifferentValueForAdcZeroAndBaseline() throws ParseException {
        String signalText = "00001_lr.dat 16 1000.0(0)/mV 16 211 -26 61447 0 V4";
        Signal signal = Signal.parse(signalText);
        assertEquals(signal.baseline(), 0);
        assertNotEquals(signal.adcZero(), signal.baseline());
    }

}
