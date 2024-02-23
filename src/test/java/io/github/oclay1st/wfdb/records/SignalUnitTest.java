package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class SignalUnitTest {

    @Test
    @DisplayName("Should parse the picovolt unit")
    void shouldParsePicovolt() throws ParseException {
        SignalUnit unit = SignalUnit.parse("pV");
        assertEquals(SignalUnit.PICOVOLT, unit);
        assertEquals("pV", unit.symbol());
    }

    @Test
    @DisplayName("Should parse the nanovolt unit")
    void shouldParseNanovolt() throws ParseException {
        SignalUnit unit = SignalUnit.parse("nV");
        assertEquals(SignalUnit.NANOVOLT, unit);
        assertEquals("nV", unit.symbol());
    }

    @Test
    @DisplayName("Should parse the microvolt unit")
    void shouldParseMicrovolt() throws ParseException {
        SignalUnit unit = SignalUnit.parse("uV");
        assertEquals(SignalUnit.MICROVOLT, unit);
        assertEquals("uV", unit.symbol());
    }

    @Test
    @DisplayName("Should parse the millivolt unit")
    void shouldParseMillivolt() throws ParseException {
        SignalUnit unit = SignalUnit.parse("mV");
        assertEquals(SignalUnit.MILLIVOLT, unit);
        assertEquals("mV", unit.symbol());
    }

    @Test
    @DisplayName("Should parse the volt unit")
    void shouldParseVolt() throws ParseException {
        SignalUnit unit = SignalUnit.parse("V");
        assertEquals(SignalUnit.VOLT, unit);
        assertEquals("V", unit.symbol());
    }

    @Test
    @DisplayName("Should parse the kilovolt unit")
    void shouldParseKilovolt() throws ParseException {
        SignalUnit unit = SignalUnit.parse("kV");
        assertEquals(SignalUnit.KILOVOLT, unit);
        assertEquals("kV", unit.symbol());
    }

    @Test
    @DisplayName("Should parse the no unit")
    void shouldParseNoUnit() throws ParseException {
        SignalUnit unit = SignalUnit.parse("NU");
        assertEquals(SignalUnit.NO_UNIT, unit);
        assertEquals("NU", unit.symbol());
    }

    @ParameterizedTest(name = "in {0}")
    @DisplayName("Should throw a ParseException")
    @ValueSource(strings = { "", "~", "#", "p", "km", "w" })
    void shouldThrowParseException(String symbol) {
        assertThrows(ParseException.class, () -> SignalUnit.parse(symbol));
    }

}
