package io.github.oclay1st.wfdb;

public class Util {

    private Util() {
        throw new IllegalAccessError("Util class");
    }

    public static boolean isEmpty(String text) {
        return text == null || text.isBlank();
    }

    public static int parseOrDefault(String text, int defaultValue) {
        return isEmpty(text) ? defaultValue : Integer.parseInt(text);
    }

    public static float parseOrDefault(String text, float defaultValue) {
        return isEmpty(text) ? defaultValue : Float.parseFloat(text);
    }

    public static int[] castArray(short[] values) {
        int[] newValues = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        return newValues;
    }

    public static short[] castArray(int[] values) {
        short[] newValues = new short[values.length];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = (short) values[i];
        }
        return newValues;
    }

}
