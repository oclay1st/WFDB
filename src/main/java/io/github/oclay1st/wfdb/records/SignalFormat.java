package io.github.oclay1st.wfdb.records;

import io.github.oclay1st.wfdb.exceptions.ParseException;
import io.github.oclay1st.wfdb.formatters.SignalFormatter;
import io.github.oclay1st.wfdb.formatters.SignalFormatter16;
import io.github.oclay1st.wfdb.formatters.SignalFormatter160;
import io.github.oclay1st.wfdb.formatters.SignalFormatter212;
import io.github.oclay1st.wfdb.formatters.SignalFormatter24;
import io.github.oclay1st.wfdb.formatters.SignalFormatter310;
import io.github.oclay1st.wfdb.formatters.SignalFormatter311;
import io.github.oclay1st.wfdb.formatters.SignalFormatter32;
import io.github.oclay1st.wfdb.formatters.SignalFormatter61;
import io.github.oclay1st.wfdb.formatters.SignalFormatter8;
import io.github.oclay1st.wfdb.formatters.SignalFormatter80;

/**
 * Represents the format of the signal samples
 */
public enum SignalFormat {

    /**
     * Represents the signal format 8
     */
    FORMAT_8("8", 1, 8) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter8();
        }

    },
    /**
     * Represents the signal format 16
     */
    FORMAT_16("16", 2, 16) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter16();
        }

    },
    /**
     * Represents the signal format 24
     */
    FORMAT_24("24", 3, 24) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter24();
        }

    },
    /**
     * Represents the signal format 32
     */
    FORMAT_32("32", 4, 32) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter32();
        }

    },
    /**
     * Represents the signal format 61
     */
    FORMAT_61("61", 2, 16) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter61();
        }

    },
    /**
     * Represents the signal format 80
     */
    FORMAT_80("80", 1, 8) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter80();
        }

    },
    /**
     * Represents the signal format 160
     */
    FORMAT_160("160", 2, 16) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter160();
        }

    },
    /**
     * Represents the signal format 212
     */
    FORMAT_212("212", 1.5f, 12) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter212();
        }

    },
    /**
     * Represents the signal format 310
     */
    FORMAT_310("310", 4 / 3f, 10) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter310();
        }

    },
    /**
     * Represents the signal format 311
     */
    FORMAT_311("311", 4 / 3f, 10) {

        @Override
        public SignalFormatter formatter() {
            return new SignalFormatter311();
        }

    };

    /**
     * Parse the signal format from a text value
     *
     * @param value the text value representation of the format
     * @return the {@link SignalFormat} instance
     * @throws ParseException if the given value is invalid
     */
    public static SignalFormat parse(String value) throws ParseException {
        return switch (value) {
            case "8" -> FORMAT_8;
            case "16" -> FORMAT_16;
            case "24" -> FORMAT_24;
            case "32" -> FORMAT_32;
            case "61" -> FORMAT_61;
            case "80" -> FORMAT_80;
            case "160" -> FORMAT_160;
            case "212" -> FORMAT_212;
            case "310" -> FORMAT_310;
            case "311" -> FORMAT_311;
            default -> throw new ParseException("Invalid signal format");
        };
    }

    private final String text;

    private final float bytesPerSample;

    private final int bitResolution;

    SignalFormat(String text, float bytesPerSample, int bitResolution) {
        this.text = text;
        this.bytesPerSample = bytesPerSample;
        this.bitResolution = bitResolution;
    }

    /**
     * Returns the formatter for the current format
     *
     * @return a {@link SignalFormatter} instance
     */
    public abstract SignalFormatter formatter();

    /**
     * Returns the bits resolution of the format
     *
     * @return the value of the bit resolution
     */
    public int bitResolution() {
        return bitResolution;
    }

    /**
     * Returns a float numbers of the bytes per samples of the format
     *
     * @return the value of the bytes per sample
     */
    public float bytesPerSample() {
        return bytesPerSample;
    }

    /**
     * Returns the text value of the format
     *
     * @return the value of text
     */
    public String text() {
        return text;
    }

}
