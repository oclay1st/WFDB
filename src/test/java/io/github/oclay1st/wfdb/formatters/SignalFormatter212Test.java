package io.github.oclay1st.wfdb.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SignalFormatter212Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 212")
    void shouldConvertFromRawDataToFormat212() {
        byte[] source = { 1, 2, 3 };
        SignalFormatter formatter = new SignalFormatter212();
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 513, 3 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 212 to raw data")
    void shouldConvertFromFormat212ToRawData() {
        int[] samples = { 513, 3 };
        SignalFormatter formatter = new SignalFormatter212();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 1, 2, 3 }, source);
    }
    
}
