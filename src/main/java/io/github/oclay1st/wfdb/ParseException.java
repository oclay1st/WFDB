package io.github.oclay1st.wfdb;

/**
 * Represents an exceptional situation in where is not possible to parse an
 * input data from the WFDB specification.
 */
public class ParseException extends Exception {

    /**
     * Constructs a new instance with a given message
     *
     * @param message the message of the exception
     */
    public ParseException(String message) {
        super(message);
    }

}
