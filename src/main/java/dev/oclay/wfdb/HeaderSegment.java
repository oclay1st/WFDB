package dev.oclay.wfdb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record HeaderSegment(String name, int numberOfSamples) {

    private static final Pattern PATTERN = Pattern.compile("""
            (?<name>[-\\w]+) \
            \\s*(?<numberOfSamples>\\d*)""");

    public static HeaderSegment parse(String text) throws ParseException {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new ParseException("Unable to parse the header segment");
        }
        String name = matcher.group("name");
        int numberOfSamples = Util.parseOrDefault(matcher.group("numberOfSamples"), 0);
        return new HeaderSegment(name, numberOfSamples);
    }

}
