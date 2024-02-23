package io.github.oclay1st.wfdb.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignalFormatter61Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 61")
    void shouldConvertFromRawDataToFormat61() {
        byte[] source = { 4, 3, 2, 1 };
        SignalFormatter formatter = new SignalFormatter61();
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 1027, 513 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 61 to raw data")
    void shouldConvertFromFormat61ToRawData() {
        int[] samples = {1027, 513};
        SignalFormatter formatter = new SignalFormatter61();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 4, 3, 2, 1  }, source);
    }

}
