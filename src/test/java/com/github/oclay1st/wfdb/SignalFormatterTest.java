package com.github.oclay1st.wfdb;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignalFormatterTest {

    @Test
    @DisplayName("Should parse signal samples with format 8")
    void shouldParseFormat8() {
        byte[] data = { 0, -128, 127 };
        HeaderSignal signal = new HeaderSignal("test", 8, 1, 0, 0, 100, 0, "mV", 0, 0, -2047, 1234, 0, "I");
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormatter.toFormat8(data, 3, headerSignals);
        assertArrayEquals(new int[] { -2047, -2175, -2048 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 16")
    void shouldParseFormat16() {
        byte[] data = { 1, 2, 3, 4 };
        int[] formattedSamples = SignalFormatter.toFormat16(data, 2);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] {513, 1027}, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 24")
    void shouldParseFormat24() {
        byte[] data = { 1, 2, 3 };
        int[] formattedSamples = SignalFormatter.toFormat16(data, 1);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 513 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 32")
    void shouldParseFormat32() {
        byte[] data = { 1, 2, 3, 4 };
        int[] formattedSamples = SignalFormatter.toFormat32(data, 1);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 67305985 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 61")
    void shouldParseFormat61() {
        byte[] data = { 4, 3, 2, 1 };
        int[] formattedSamples = SignalFormatter.toFormat61(data, 2);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 1027, 513 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 80")
    void shouldParseFormat80() {
        byte[] data = { 1, 2, 3, 4 };
        int[] formattedSamples = SignalFormatter.toFormat80(data, 4);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -127, -126, -125, -124 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 160")
    void shouldParseFormat160() {
        byte[] data = { 2, -128, 14, -126 };
        int[] formattedSamples = SignalFormatter.toFormat160(data, 2);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -65534, -65010 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 212")
    void shouldParseFormat212() {
        byte[] data = { 1, 2, 3 };
        int[] formattedSamples = SignalFormatter.toFormat212(data, 2);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 513, 3 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 310")
    void shouldParseFormat310() {
        byte[] data = { 1, 2, 3, 4 };
        int[] formattedSamples = SignalFormatter.toFormat310(data, 3);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 256, -511, 0 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 311")
    void shouldParseFormat311() {
        byte[] data = { 1, 2, 3, 4 };
        int[] formattedSamples = SignalFormatter.toFormat311(data, 3);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -511, 192, 16 }, formattedSamples);
    }

}
