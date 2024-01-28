package dev.oclay.wfdb;

class Util {

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

}
