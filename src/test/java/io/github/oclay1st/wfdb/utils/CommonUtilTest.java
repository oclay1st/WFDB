package io.github.oclay1st.wfdb.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CommonUtilTest {

    @DisplayName("Should be empty")
    void shouldBeEmpty() {
        assertTrue(CommonUtil.isEmpty(""));
        assertTrue(CommonUtil.isEmpty("  "));
        assertTrue(CommonUtil.isEmpty(null));
    }

    @ParameterizedTest
    @DisplayName("Should not be empty")
    @ValueSource(strings = { "1", "   #   ", "string" })
    void shouldNotBeEmpty(String text) {
        assertFalse(CommonUtil.isEmpty(text));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldNotCreateInstance() {
        Constructor<CommonUtil> constructor = (Constructor<CommonUtil>) CommonUtil.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, () -> constructor.newInstance());
    }

}
