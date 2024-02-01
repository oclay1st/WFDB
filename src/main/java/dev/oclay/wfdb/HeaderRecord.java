package dev.oclay.wfdb;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record HeaderRecord(String name, int numberOfSegments, int numberOfSignals, float samplingFrequency,
        @Deprecated float counterFrequency, @Deprecated float baseCounter, int numberOfSamplesPerSignal,
        LocalTime baseTime, LocalDate baseDate) {

    private static final Pattern PATTERN = Pattern.compile("""
            (?<name>[-\\w]+)
            /?(?<numberOfSegments>\\d*)
            \\s*(?<numberOfSignals>\\d+)
            \\s*(?<samplingFrequency>\\d*\\.?\\d*)
            /*(?<counterFrequency>\\d*\\.?\\d*)
            \\(?(?<baseCounter>\\d*\\.?\\d*)\\)?
            \\s*(?<numberOfSamplesPerSignal>\\d*)
            \\s*(?<baseTime>\\d{1,2}:\\d{1,2}:\\d{1,2}\\.?\\d{0,6})?
            \\s*(?<baseDate>\\d{1,2}/\\d{1,2}/\\d{1,4})?
            """.replaceAll("[\n\r]", ""));

    private static final DateTimeFormatter BASE_TIME_FORMATTER = DateTimeFormatter.ofPattern("H:m:s");

    private static final DateTimeFormatter BASE_DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/y");

    public static HeaderRecord parse(String text) throws ParseException {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new ParseException("Unable to parse the header record");
        }
        String name = matcher.group("name");
        int numberOfSegments = Util.parseOrDefault(matcher.group("numberOfSegments"), 0);
        int numberOfSignals = Util.parseOrDefault(matcher.group("numberOfSignals"), 0);
        float samplingFrequency = Util.parseOrDefault(matcher.group("samplingFrequency"), 250f);
        float counterFrequency = Util.parseOrDefault(matcher.group("counterFrequency"), samplingFrequency);
        float baseCounter = Util.parseOrDefault(matcher.group("baseCounter"), 0f);
        int numberOfSamplesPerSignal = Util.parseOrDefault(matcher.group("numberOfSamplesPerSignal"), 0);
        String baseTimeText = matcher.group("baseTime");
        LocalTime baseTime = !Util.isEmpty(baseTimeText)
                ? LocalTime.parse(baseTimeText.split("\\.")[0], BASE_TIME_FORMATTER)
                : null;
        String baseDateText = matcher.group("baseDate");
        LocalDate baseDate = !Util.isEmpty(baseDateText) ? LocalDate.parse(baseDateText, BASE_DATE_FORMATTER) : null;
        return new HeaderRecord(name, numberOfSegments, numberOfSignals, samplingFrequency, counterFrequency,
                baseCounter, numberOfSamplesPerSignal, baseTime, baseDate);
    }

    public boolean isMultiSegment() {
        return numberOfSegments > 0;
    }

    public int totalNumberOfSamples() {
        return numberOfSamplesPerSignal() * numberOfSignals;
    }

    /**
     * The counter frequency value
     * 
     * @deprecated recent versions of the spec ignore this field
     */
    @Deprecated
    public float counterFrequency() {
        return counterFrequency;
    }

    /**
     * The counter value corresponding to sample 0
     * 
     * @deprecated recent versions of the spec ignore this field
     */
    @Deprecated
    public float baseCounter() {
        return baseCounter;
    }

}
