package io.github.oclay1st.wfdb.records;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.oclay1st.wfdb.exceptions.ParseException;
import io.github.oclay1st.wfdb.utils.CommonUtil;

/**
 * Represent the info from header segment
 * 
 * @param name                     the name of the single-segment record
 * @param numberOfSamplesPerSignal the number of samples per signal
 */
public record HeaderSegment(String name, int numberOfSamplesPerSignal) {

    private static final Pattern PATTERN = Pattern.compile("""
            (?<name>[-\\w]*~?)\
            \\s*(?<numberOfSamplesPerSignal>\\d+)""");

    /**
     * Parse the header segment for a text line.
     * 
     * <pre>
     * # As an example the text line may look like:
     * 100s 21600
     * </pre>
     * 
     * @param text the text that represents the segment info
     * @return a new {@link HeaderSegment} instance
     * @throws ParseException if the text can't be parsed
     */
    public static HeaderSegment parse(String text) throws ParseException {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new ParseException("Unable to parse the header segment: " + text);
        }
        String name = matcher.group("name");
        int numberOfSamplesPerSignal = CommonUtil.parseOrDefault(matcher.group("numberOfSamplesPerSignal"), 0);
        return new HeaderSegment(name, numberOfSamplesPerSignal);
    }

    /**
     * Returns the header segment representation
     *
     * @return the text line representation
     */
    public String toTextLine() {
        return name + " " + numberOfSamplesPerSignal;
    }

}
