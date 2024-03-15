package io.github.oclay1st.wfdb.utils;

import java.nio.ShortBuffer;

/**
 * Utility reference
 */
public final class CommonUtil {

    private CommonUtil() {
        throw new IllegalAccessError("Util class");
    }

    /**
     * Determine if a given text is empty, blank or null.
     * 
     * @param text the value of the text
     * @return true if is empty, blank or null, otherwise false
     */
    public static boolean isEmpty(String text) {
        return text == null || text.isBlank();
    }

    /**
     * Parse an integer or return a default value if the text is empty
     * 
     * @param text         the value of the text to parse
     * @param defaultValue the default int value
     * @return the parse value or default
     */
    public static int parseOrDefault(String text, int defaultValue) {
        return isEmpty(text) ? defaultValue : Integer.parseInt(text);
    }

    /**
     * Parse a float or return a default value if the text is empty
     * 
     * @param text         the value of the text to parse
     * @param defaultValue the default float value
     * @return the parse value or default
     */
    public static float parseOrDefault(String text, float defaultValue) {
        return isEmpty(text) ? defaultValue : Float.parseFloat(text);
    }

    /**
     * Cast a {@link ShortBuffer} to an int array.
     * 
     * @param buffer the {@link ShortBuffer} instance
     * @return the int array
     */
    public static int[] convertShortBufferToIntArray(ShortBuffer buffer) {
        int index = 0;
        int[] values = new int[buffer.capacity()];
        while (buffer.hasRemaining()) {
            values[index] = buffer.get();
            index++;
        }
        return values;
    }

    /**
     * Cast a int array to an short array.
     * 
     * @param values the int array
     * @return the short array
     */
    public static short[] convertArray(int[] values) {
        short[] newValues = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = (short) values[i];
        }
        return newValues;
    }

}
