package io.github.oclay1st.wfdb.records;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.github.oclay1st.wfdb.exceptions.ParseException;

class HeaderSegementTest {

    @Test
    @DisplayName("Should parse the header segment")
    void shouldParseTheSegment() throws ParseException {
        String segmentText = "test 100";
        HeaderSegment headerSegment = HeaderSegment.parse(segmentText);
        assertNotNull(headerSegment);
        assertEquals("test", headerSegment.name());
        assertEquals(100, headerSegment.numberOfSamplesPerSignal());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", "###"
    })
    @DisplayName("Should throw a ParseException while parsing the input")
    void shouldThrowParseException(String headerText) {
        assertThrows(ParseException.class, () -> HeaderSegment.parse(headerText));
    }

}
