package io.github.oclay1st.wfdb;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.github.oclay1st.wfdb.mocks.MockHeaderSignal;

class SignalFormatTest {

    @Test
    @DisplayName("Should parse signal samples with format 8")
    void shouldParseFormat8() {
        byte[] source = { 0, -128, 127 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_8)
                .initialValue(-2047)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_8.convertToSamples(source, headerSignals);
        assertArrayEquals(new int[] { -2047, -2175, -2048 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 16")
    void shouldParseFormat16() {
        byte[] source = { 1, 2, 3, 4 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_16)
                .initialValue(513)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_16.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 513, 1027 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 24")
    void shouldParseFormat24() {
        byte[] source = { 1, 2, 3 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_24)
                .initialValue(197121)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_24.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 197121 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 32")
    void shouldParseFormat32() {
        byte[] source = { 1, 2, 3, 4 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_32)
                .initialValue(67305985)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_32.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 67305985 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 61")
    void shouldParseFormat61() {
        byte[] source = { 4, 3, 2, 1 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_61)
                .initialValue(127)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_61.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 1027, 513 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 80")
    void shouldParseFormat80() {
        byte[] source = { 1, 2, 3, 4 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_80)
                .initialValue(-127)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_80.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -127, -126, -125, -124 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 160")
    void shouldParseFormat160() {
        byte[] source = { 2, -128, 14, -126 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_160)
                .initialValue(-65534)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_160.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -65534, -65010 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 212")
    void shouldParseFormat212() {
        byte[] source = { 1, 2, 3 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_212)
                .initialValue(513)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_212.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 513, 3 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 310")
    void shouldParseFormat310() {
        byte[] source = { 1, 2, 3, 4 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_310)
                .initialValue(256)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_310.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 256, -511, 0 }, formattedSamples);
    }

    @Test
    @DisplayName("Should parse signal samples with format 311")
    void shouldParseFormat311() {
        byte[] source = { 1, 2, 3, 4 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_311)
                .initialValue(-511)
                .build();
        HeaderSignal[] headerSignals = { signal };
        int[] formattedSamples = SignalFormat.FORMAT_311.convertToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -511, 192, 64 }, formattedSamples);
    }

}
