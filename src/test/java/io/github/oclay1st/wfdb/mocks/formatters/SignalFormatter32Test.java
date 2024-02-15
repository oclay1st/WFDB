package io.github.oclay1st.wfdb.mocks.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter32;

class SignalFormatter32Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 32")
    void shouldConvertFromRawDataToFormat32() {
        byte[] source = { 1, 2, 3, 4 };
        SignalFormatter formatter = new SignalFormatter32();
        int[] formattedSamples = formatter.convertBytesToSamples(source);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 67305985 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 32 to raw data")
    void shouldConvertFromFormat32ToRawData() {
        int[] samples = { 67305985 };
        SignalFormatter formatter = new SignalFormatter32();
        byte[] source = formatter.convertSamplesToBytes(samples);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, source);
    }
}
