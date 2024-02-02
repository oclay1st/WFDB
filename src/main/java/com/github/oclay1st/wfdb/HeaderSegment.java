package com.github.oclay1st.wfdb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent the info from header segment
 * 
 * @param name                     the name of the single-segment record
 * @param numberOfSamplesPerSignal the number of samples per signal
 */
public record HeaderSegment(String name, int numberOfSamplesPerSignal) {

    private static final Pattern PATTERN = Pattern.compile("""
            (?<name>[-\\w]+)\
            \\s*(?<numberOfSamplesPerSignal>\\d*)""");

    /**
     * Parse the header segment for a text line
     * As an example the text line may look like:
     * 100s 21600
     *
     * @param text the text that represents the segment info
     * @return a new {@link HeaderSegment} instance
     * @throws ParseException if the text can't be parsed
     */
    public static HeaderSegment parse(String text) throws ParseException {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new ParseException("Unable to parse the header segment");
        }
        String name = matcher.group("name");
        int numberOfSamplesPerSignal = Util.parseOrDefault(matcher.group("numberOfSamplesPerSignal"), 0);
        return new HeaderSegment(name, numberOfSamplesPerSignal);
    }

}
