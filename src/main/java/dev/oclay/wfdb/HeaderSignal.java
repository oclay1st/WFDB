package dev.oclay.wfdb;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record HeaderSignal(String filename, int format, int samplesPerFrame, int skew, int bytesOffset, float adcGain,
        int baseline, String units, int adcResolution, int adcZero, int initialValue, int checksum, int blockSize,
        String description) {

    // Pattern to match the Signal info intentionally split in multiple lines
    private final static Pattern PATTERN = Pattern.compile("""
            (?<filename>~?[-\\w]*\\.?[\\w]*)
            \\s+(?<format>\\d+)
            x?(?<samplesPerFrame>\\d*)
            :?(?<skew>\\d*)
            \\+?(?<bytesOffset>\\d*)
            \\s*(?<adcGain>-?\\d*\\.?\\d*e?[+-]?\\d*)
            \\(?(?<baseline>-?\\d*)\\)?
            /?(?<units>[\\w^\\-?%/]*)
            \\s*(?<adcResolution>\\d*)
            \\s*(?<adcZero>-?\\d*)
            \\s*(?<initialValue>-?\\d*)
            \\s*(?<checksum>-?\\d*)
            \\s*(?<blockSize>\\d*)
            \\s*(?<description>[\\S]?[^\\t\\n\\r\\f\\v]*)
            """.replaceAll("[\n\r]", ""));

    public static HeaderSignal parse(String text) throws ParseException {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new ParseException("Unable to parse the header signal");
        }
        String filename = matcher.group("filename");
        int format = Util.parseOrDefault(matcher.group("format"), 8);
        int samplesPerFrame = Util.parseOrDefault(matcher.group("samplesPerFrame"), 1);
        int skew = Util.parseOrDefault(matcher.group("skew"), 0);
        int bytesOffset = Util.parseOrDefault(matcher.group("bytesOffset"), 0);
        float adcGain = Util.parseOrDefault(matcher.group("adcGain"), 200f);
        int adcZero = Util.parseOrDefault(matcher.group("adcZero"), 0);
        int baseline = Util.parseOrDefault(matcher.group("baseline"), adcZero);
        String unitsText = matcher.group("units");
        String units = !Util.isEmpty(unitsText) ? unitsText : "mV";
        int adcResolution = Util.parseOrDefault(matcher.group("adcResolution"), 12);
        int initialValue = Util.parseOrDefault(matcher.group("initialValue"), 0);
        int checksum = Util.parseOrDefault(matcher.group("checksum"), 0);
        int blockSize = Util.parseOrDefault(matcher.group("blockSize"), 0);
        String description = matcher.group("description");
        return new HeaderSignal(filename, format, samplesPerFrame, skew, bytesOffset, adcGain, baseline, units, adcResolution,
                adcZero, initialValue, checksum, blockSize, description);
    }

}
