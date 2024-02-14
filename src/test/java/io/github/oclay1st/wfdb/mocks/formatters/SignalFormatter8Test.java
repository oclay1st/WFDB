package io.github.oclay1st.wfdb.mocks.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.HeaderSignal;
import io.github.oclay1st.wfdb.SignalFormat;
import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter8;
import io.github.oclay1st.wfdb.mocks.MockHeaderSignal;

class SignalFormatter8Test {

    @Test
    @DisplayName("Should convert raw data to signal samples with format 8")
    void shouldConvertFromRawDataToFormat8() {
        byte[] source = { 0, -128, 127 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_8)
                .initialValue(-2047)
                .build();
        HeaderSignal[] headerSignals = { signal };
        SignalFormatter formatter = new SignalFormatter8();
        int[] formattedSamples = formatter.convertBytesToSamples(source, headerSignals);
        assertArrayEquals(new int[] { -2047, -2175, -2048 }, formattedSamples);
    }

    @Test
    @DisplayName("Should convert signal samples with format 8 to raw data")
    void shouldConvertFromFormat8ToRawData() {
        int[] samples = { -2047, -2175, -2048 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_8)
                .initialValue(-2047)
                .build();
        HeaderSignal[] headerSignals = { signal };
        SignalFormatter formatter = new SignalFormatter8();
        byte[] source = formatter.convertSamplesToBytes(samples, headerSignals);
        assertArrayEquals(new byte[] { 0, -128, 127 }, source);
    }

}
