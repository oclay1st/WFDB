package io.github.oclay1st.wfdb;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class UtilTest {


    @DisplayName("Sould be empty")
    void shouldBeEmpty() {
        assertTrue(Util.isEmpty(""));
        assertTrue(Util.isEmpty("  "));
        assertTrue(Util.isEmpty(null));
    }

    @ParameterizedTest
    @DisplayName("Sould not be empty")
    @ValueSource(strings = { "1", "   #   ", "string" })
    void shouldNotBeEmpty(String text) {
        assertFalse(Util.isEmpty(text));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldNotCreateInstance() {
        Constructor<Util> constructor = (Constructor<Util>) Util.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, () -> constructor.newInstance());
    }

}
