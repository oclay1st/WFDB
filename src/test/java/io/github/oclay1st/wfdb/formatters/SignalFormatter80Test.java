package io.github.oclay1st.wfdb.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignalFormatter80Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 80")
    void shouldConvertFromRawDataToFormat80() {
        byte[] source = { 1, 2, 3, 4 };
        SignalFormatter formatter = new SignalFormatter80();
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -127, -126, -125, -124 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 80 to raw data")
    void shouldConvertFromFormat80ToRawData() {
        int[] samples = { -127, -126, -125, -124 };
        SignalFormatter formatter = new SignalFormatter80();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, source);
    }

}
