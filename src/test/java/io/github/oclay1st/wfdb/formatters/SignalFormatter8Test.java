package io.github.oclay1st.wfdb.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignalFormatter8Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 8")
    void shouldConvertFromRawDataToFormat8() {
        byte[] source = { 0, -128, 127 };
        SignalFormatter8 formatter = new SignalFormatter8();
        formatter.setInitialSignalSamples(new int[] { -2047 });
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertArrayEquals(new int[] { -2047, -2175, -2048 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 8 to raw data")
    void shouldConvertFromFormat8ToRawData() {
        int[] samples = { -2047, -2175, -2048 };
        SignalFormatter8 formatter = new SignalFormatter8();
        formatter.setInitialSignalSamples(new int[] { -2047 });
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertArrayEquals(new byte[] { 0, -128, 127 }, source);
    }

}
