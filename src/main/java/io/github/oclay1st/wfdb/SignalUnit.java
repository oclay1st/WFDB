package io.github.oclay1st.wfdb;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the unit of the signal sample
 */
public enum SignalUnit {

    /**
     * Represents the picovolt(pV) unit 10^−12 V
     */
    PICOVOLT("pV"),
    /**
     * Represents the nanovolt(nV) unit 10^−9 V
     */
    NANOVOLT("nV"),
    /**
     * Represents the microvolt(uV) unit 10^−6 V
     */
    MICROVOLT("uV"),
    /**
     * Represents the millivolt(mV) unit 10^−12 V
     */
    MILLIVOLT("mV"),
    /**
     * Represents the volt(V) unit
     */
    VOLT("V"),
    /**
     * Represents the kilovolt(kV) unit 10^−12 V
     */
    KILOVOLT("kV"),
    /**
     * Represents the not unit(NU)
     */
    NO_UNIT("NU");

    private final String symbol;

    SignalUnit(String symbol) {
        this.symbol = symbol;
    }

    static SignalUnit[] VALUES = values();

    /**
     * Parse the signal unit given the symbol
     *
     * @param symbolText the value of the symbol
     * @return the signal unit
     * @throws ParseException if there is no signal unit with the given symbol
     */
    public static SignalUnit parse(String symbolText) throws ParseException {
        Objects.requireNonNull(symbolText);
        return Arrays.stream(VALUES)
                .filter(unit -> unit.symbol().equalsIgnoreCase(symbolText))
                .findFirst()
                .orElseThrow(() -> new ParseException("Unsupported signal unit :" + symbolText));
    }

    /**
     * Returns the symbol of the unit
     * 
     * @return the value of the symbol
     */
    public String symbol() {
        return symbol;
    }

}
