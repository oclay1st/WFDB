package io.github.oclay1st.wfdb.records;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.oclay1st.wfdb.exceptions.ParseException;
import io.github.oclay1st.wfdb.utils.CommonUtil;

/**
 * Represents the data info from the first line of a header file.
 *
 * @param name                     the record name that identify the record
 * @param numberOfSegments         the number of segments
 * @param numberOfSignals          the number of signal
 * @param samplingFrequency        the samples per seconds in each signal
 * @param counterFrequency         the counter frequency
 * @param baseCounter              the counter value corresponding
 *                                 to sample 0
 * @param numberOfSamplesPerSignal the number of samples per signal
 * @param baseTime                 the time of day that corresponds to the
 *                                 beginning of the record, in HH:MM:SS format
 * @param baseDate                 the date that corresponds to the beginning of
 *                                 the record, in DD/MM/YYYY format
 */
public record HeaderRecord(String name, int numberOfSegments, int numberOfSignals, float samplingFrequency,
        float counterFrequency, float baseCounter, int numberOfSamplesPerSignal, LocalTime baseTime,
        LocalDate baseDate) {

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

    /**
     * Parse the header record from a text line.
     * 
     * <pre>
     * # As an example of the text line may look like:
     * 100 2 360 650000 0:0:0 10/01/2001
     * </pre>
     * 
     * @param text the text that represents the header record info
     * @return a new {@link HeaderRecord} instance
     * @throws ParseException if the text can't be parsed
     */
    public static HeaderRecord parse(String text) throws ParseException {
        Matcher matcher = PATTERN.matcher(text);
        if (!matcher.matches()) {
            throw new ParseException("Unable to parse the header record: " + text);
        }
        String name = matcher.group("name");
        int numberOfSegments = CommonUtil.parseOrDefault(matcher.group("numberOfSegments"), 1);
        int numberOfSignals = CommonUtil.parseOrDefault(matcher.group("numberOfSignals"), 0);
        float samplingFrequency = CommonUtil.parseOrDefault(matcher.group("samplingFrequency"), 250f);
        float counterFrequency = CommonUtil.parseOrDefault(matcher.group("counterFrequency"), samplingFrequency);
        float baseCounter = CommonUtil.parseOrDefault(matcher.group("baseCounter"), 0f);
        int numberOfSamplesPerSignal = CommonUtil.parseOrDefault(matcher.group("numberOfSamplesPerSignal"), 0);
        LocalTime baseTime = parseBaseTime(matcher.group("baseTime"));
        LocalDate baseDate = parseBaseDate(matcher.group("baseDate"));
        return new HeaderRecord(name, numberOfSegments, numberOfSignals, samplingFrequency, counterFrequency,
                baseCounter, numberOfSamplesPerSignal, baseTime, baseDate);
    }

    private static LocalTime parseBaseTime(String text) {
        return !CommonUtil.isEmpty(text) ? LocalTime.parse(text.split("\\.")[0], BASE_TIME_FORMATTER) : null;
    }

    private static LocalDate parseBaseDate(String text) {
        return !CommonUtil.isEmpty(text) ? LocalDate.parse(text, BASE_DATE_FORMATTER) : null;
    }

    /**
     * Determine if the header belongs to a multi-segment record
     * 
     * @return true if the number of segments is greater than 1, otherwise false
     */
    public boolean isMultiSegment() {
        return numberOfSegments > 1;
    }

    /**
     * The whole time of the record that was recorded
     *
     * @return the value of the duration time
     */
    public Duration durationTime() {
        int milliseconds = (int) (numberOfSamplesPerSignal / samplingFrequency * 1000);
        return Duration.ofMillis(milliseconds);
    }

    /**
     * Returns the header record representation
     *
     * @return the text line representation
     */
    public String toTextLine() {
        StringBuilder builder = new StringBuilder(name);
        if (numberOfSegments > 1) {
            builder.append("/").append(numberOfSegments);
        }
        builder.append(" ").append(numberOfSignals);
        builder.append(" ").append(samplingFrequency);
        if (counterFrequency != samplingFrequency) {
            builder.append("/").append(counterFrequency);
        }
        if (baseCounter != 0) {
            builder.append("(").append(baseCounter).append(")");
        }
        builder.append(" ").append(numberOfSamplesPerSignal);
        if (baseTime != null) {
            builder.append(" ").append(BASE_TIME_FORMATTER.format(baseTime));
        }
        if (baseDate != null) {
            builder.append(" ").append(BASE_DATE_FORMATTER.format(baseDate));
        }
        return builder.toString();
    }

}
