package io.github.oclay1st.wfdb.mocks.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.HeaderSignal;
import io.github.oclay1st.wfdb.SignalFormat;
import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter80;
import io.github.oclay1st.wfdb.mocks.MockHeaderSignal;

class SignalFormatter80Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 80")
    void shouldConvertFromRawDataToFormat80() {
        byte[] source = { 1, 2, 3, 4 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_80)
                .initialValue(-127)
                .build();
        HeaderSignal[] headerSignals = { signal };
        SignalFormatter formatter = new SignalFormatter80();
        int[] formattedSamples = formatter.convertBytesToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { -127, -126, -125, -124 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 80 to raw data")
    void shouldConvertFromFormat80ToRawData() {
        int[] samples = { -127, -126, -125, -124 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_80)
                .initialValue(-127)
                .build();
        HeaderSignal[] headerSignals = { signal };
        SignalFormatter formatter = new SignalFormatter80();
        byte[] source = formatter.convertSamplesToBytes(samples, headerSignals);
        assertNotNull(source);
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, source);
    }
     
}
