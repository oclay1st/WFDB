package io.github.oclay1st.wfdb.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignalFormatter16Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 16")
    void shouldConvertFromRawDataToFormat16() {
        byte[] source = { 1, 2, 3, 4 };
        SignalFormatter formatter = new SignalFormatter16();
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 513, 1027 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 16 to raw data")
    void shouldConvertFromFormat16ToRawData() {
        int[] samples = new int[] { 513, 1027 };
        SignalFormatter16 formatter = new SignalFormatter16();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, source);
    }

}
