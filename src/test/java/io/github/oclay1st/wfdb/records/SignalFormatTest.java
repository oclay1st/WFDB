package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.oclay1st.wfdb.exceptions.ParseException;
import io.github.oclay1st.wfdb.formatters.SignalFormatter16;
import io.github.oclay1st.wfdb.formatters.SignalFormatter160;
import io.github.oclay1st.wfdb.formatters.SignalFormatter212;
import io.github.oclay1st.wfdb.formatters.SignalFormatter24;
import io.github.oclay1st.wfdb.formatters.SignalFormatter310;
import io.github.oclay1st.wfdb.formatters.SignalFormatter311;
import io.github.oclay1st.wfdb.formatters.SignalFormatter32;
import io.github.oclay1st.wfdb.formatters.SignalFormatter61;
import io.github.oclay1st.wfdb.formatters.SignalFormatter8;
import io.github.oclay1st.wfdb.formatters.SignalFormatter80;

class SignalFormatTest {

    @Test
    @DisplayName("Should parse the signal format 8")
    void shouldMatchBitResolutionOfFormat8() throws ParseException {
        SignalFormat format = SignalFormat.parse("8");
        assertEquals(SignalFormat.FORMAT_8, format);
        assertEquals(8, format.bitResolution());
        assertInstanceOf(SignalFormatter8.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 16")
    void shouldMatchBitResolutionOfFormat16() throws ParseException {
        SignalFormat format = SignalFormat.parse("16");
        assertEquals(SignalFormat.FORMAT_16, format);
        assertEquals(16, format.bitResolution());
        assertInstanceOf(SignalFormatter16.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 24")
    void shouldMatchBitResolutionOfFormat24() throws ParseException {
        SignalFormat format = SignalFormat.parse("24");
        assertEquals(SignalFormat.FORMAT_24, format);
        assertEquals(24, format.bitResolution());
        assertInstanceOf(SignalFormatter24.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 32")
    void shouldMatchBitResolutionOfFormat32() throws ParseException {
        SignalFormat format = SignalFormat.parse("32");
        assertEquals(SignalFormat.FORMAT_32, format);
        assertEquals(32, format.bitResolution());
        assertInstanceOf(SignalFormatter32.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 61")
    void shouldMatchBitResolutionOfFormat60() throws ParseException {
        SignalFormat format = SignalFormat.parse("61");
        assertEquals(SignalFormat.FORMAT_61, format);
        assertEquals(16, format.bitResolution());
        assertInstanceOf(SignalFormatter61.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 80")
    void shouldMatchBitResolutionOfFormat80() throws ParseException {
        SignalFormat format = SignalFormat.parse("80");
        assertEquals(SignalFormat.FORMAT_80, format);
        assertEquals(8, format.bitResolution());
        assertInstanceOf(SignalFormatter80.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 160")
    void shouldMatchBitResolutionOfFormat160() throws ParseException {
        SignalFormat format = SignalFormat.parse("160");
        assertEquals(SignalFormat.FORMAT_160, format);
        assertEquals(16, format.bitResolution());
        assertInstanceOf(SignalFormatter160.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 212")
    void shouldMatchBitResolutionOfFormat212() throws ParseException {
        SignalFormat format = SignalFormat.parse("212");
        assertEquals(SignalFormat.FORMAT_212, format);
        assertEquals(12, format.bitResolution());
        assertInstanceOf(SignalFormatter212.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 310")
    void shouldMatchBitResolutionOfFormat310() throws ParseException {
        SignalFormat format = SignalFormat.parse("310");
        assertEquals(SignalFormat.FORMAT_310, format);
        assertEquals(10, format.bitResolution());
        assertInstanceOf(SignalFormatter310.class, format.formatter());
    }

    @Test
    @DisplayName("Should parse the signal format 311")
    void shouldMatchBitResolutionOfFormat311() throws ParseException {
        SignalFormat format = SignalFormat.parse("311");
        assertEquals(SignalFormat.FORMAT_311, format);
        assertEquals(10, format.bitResolution());
        assertInstanceOf(SignalFormatter311.class, format.formatter());
    }

    @ParameterizedTest(name = "in {0}")
    @DisplayName("Should throw ParseException")
    @ValueSource(strings = { "", "  ", "-", "1", "2" })
    void shouldThrowParseException(String input) {
        assertThrows(ParseException.class, () -> SignalFormat.parse(input));
    }

}
