package io.github.oclay1st.wfdb.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignalFormatter310Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 310")
    void shouldConvertFromRawDataToFormat310() {
        byte[] source = { 0, 2, 2, 4 };
        SignalFormatter formatter = new SignalFormatter310();
        int[] samples = formatter.convertBytesToSamples(source);
        assertNotNull(samples);
        assertArrayEquals(new int[] { 256, -511, 0 }, samples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 310 to raw data")
    void shouldConvertFromFormat310ToRawData() {
        int[] samples = { 256, -511, 0 };
        SignalFormatter formatter = new SignalFormatter310();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 0, 2, 2, 4 }, source);
    }
}
