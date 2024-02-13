package io.github.oclay1st.wfdb.mocks.formatters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.github.oclay1st.wfdb.HeaderSignal;
import io.github.oclay1st.wfdb.SignalFormat;
import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter310;
import io.github.oclay1st.wfdb.mocks.MockHeaderSignal;

class SignalFormatter310Test {

    @Test
    @DisplayName("Should parse signal samples with format 310")
    void shouldParseFormat310() {
        byte[] source = { 1, 2, 3, 4 };
        HeaderSignal signal = new MockHeaderSignal.Builder()
                .format(SignalFormat.FORMAT_310)
                .initialValue(256)
                .build();
        HeaderSignal[] headerSignals = { signal };
        SignalFormatter formatter = new SignalFormatter310();
        int[] formattedSamples = formatter.convertBytesToSamples(source, headerSignals);
        assertNotNull(formattedSamples);
        assertArrayEquals(new int[] { 256, -511, 0 }, formattedSamples);
    }


}