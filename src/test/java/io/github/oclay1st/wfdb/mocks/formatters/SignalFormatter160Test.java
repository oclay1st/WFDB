package io.github.oclay1st.wfdb.mocks.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter160;

class SignalFormatter160Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 160")
    void shouldConvertFromRawDataToFormat160() {
        byte[] source = { 2, -128, 14, -126 };
        SignalFormatter formatter = new SignalFormatter160();
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -65534, -65010 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 160 to raw data")
    void shouldConvertFromFormat160ToRawData() {
        int[] samples = { -65534, -65010 };
        SignalFormatter formatter = new SignalFormatter160();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 2, -128, 14, -126 }, source);
    }

}
