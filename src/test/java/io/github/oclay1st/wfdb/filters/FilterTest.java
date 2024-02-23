package io.github.oclay1st.wfdb.filters;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilterTest {
    
    @Test
    @DisplayName("Should create as default")
    void shouldCreatedAsDefault() {
        Filter filter = new Filter.Builder().build();
        assertTrue(filter.isAsDefault());
    }

    @Test
    @DisplayName("Should create a new filter")
    void shouldCreatedAFilter() {
        Filter filter = new Filter.Builder()
            .startTime(0)
            .endTime(1000)
            .signals(new int[]{0,1,2,3,4,5})
            .build();
        assertNotNull(filter);
        assertEquals(0, filter.startTime());
        assertEquals(1000, filter.endTime());
        assertArrayEquals(new int[]{0,1,2,3,4,5}, filter.signals());
        assertFalse(filter.isAsDefault());
    }

}
