package io.github.oclay1st.wfdb.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BytesRangeTest {

    @Test
    @DisplayName("Should throw exception if start byte index is greater than end byte index")
    void shouldThrowAnExceptionIfStartIndexGreaterThanEndIndex() {
        assertThrows(IllegalArgumentException.class, () -> new BytesRange(2, 1));
    }

    @Test
    @DisplayName("Should throw exception if start or end byte indices are less than zero")
    void shouldThrowExceptionIfStartOrEndAreLessThanZero() {
        assertThrows(IllegalArgumentException.class, () -> new BytesRange(-1, 1));
        assertThrows(IllegalArgumentException.class, () -> new BytesRange(0, -1));
    }

    @Test
    @DisplayName("Should calulate total bytes length")
    void shouldCalculate() {
        assertEquals(5, new BytesRange(5, 10).total());
        assertEquals(0, new BytesRange(0, 0).total());
    }

}
