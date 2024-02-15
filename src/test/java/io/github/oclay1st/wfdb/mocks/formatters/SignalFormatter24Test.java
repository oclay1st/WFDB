package io.github.oclay1st.wfdb.mocks.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter24;

class SignalFormatter24Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 24")
    void shouldConvertFromRawDataToFormat24() {
        byte[] source = { 1, 2, 3 };
        SignalFormatter formatter = new SignalFormatter24();
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 197121 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 24 to raw data")
    void shouldConvertFromFormat24ToRawData() {
        int[] samples = { 197121 };
        SignalFormatter formatter = new SignalFormatter24();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 1, 2, 3 }, source);
    }

}
