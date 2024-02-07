package io.github.oclay1st.wfdb;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the data info of each signal from the header file.
 *
 * @param filename        the name of file in which samples of the signal are
 *                        kept
 * @param format          the storage format of the signal
 * @param samplesPerFrame the samples per frame
 * @param skew            indicate the number of samples of the signal that are
 *                        considered to to precede the sample 0
 * @param bytesOffset     the offset of bytes form the beginning of the signal
 *                        file to sample 0
 * @param adcGain         the difference in sample values that would be observed
 *                        if a step of one physical unit occurred in the
 *                        original analog signal
 * @param baseline        the sample value corresponding to 0 physical units
 * @param units           the unit of the signal samples
 * @param adcResolution   the resolution for the analog to digital converter to
 *                        digitize the signal
 * @param adcZero         the sample value that would be observed if the analog
 *                        signal present at the ADC inputs had a level that fell
 *                        exactly in the middle of the input range of the ADC
 * @param initialValue    the value of sample 0 in the signal
 * @param checksum        the checksum of all samples in the signal
 * @param blockSize       the size of the block in where the signal must be read
 *                        in bytes
 * @param description     the description of the signal
 */
public record HeaderSignal(String filename, int format, @Deprecated int samplesPerFrame, @Deprecated int skew,
        @Deprecated int bytesOffset, float adcGain, @Deprecated int baseline, String units, int adcResolution,
        int adcZero, int initialValue, int checksum, int blockSize, String description) {

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

    /**
     * Parse the header signal from a text line.
     * As an example the text line may look like:
     * 100.dat 212 200 11 1024 995 -22131 0 MLII
     *
     * @param text the text that represents the signals info
     * @return a new {@link HeaderSignal} instance
     * @throws ParseException if the text can't be parsed
     */
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
        return new HeaderSignal(filename, format, samplesPerFrame, skew, bytesOffset, adcGain, baseline, units,
                adcResolution, adcZero, initialValue, checksum, blockSize, description);
    }

    /**
     * The samples per frame value
     * 
     * @deprecated recent versions of the spec ignore this field
     * @return the sample per frame value
     */
    @Deprecated
    public int samplesPerFrame() {
        return samplesPerFrame;
    }

    /**
     * The skew value
     * 
     * @deprecated recent versions of the spec ignore this field
     * @return the skew value
     */
    @Deprecated
    public int skew() {
        return skew;
    }

    /**
     * The byte offset
     * 
     * @deprecated recent versions of the spec ignore this field
     * @return the bytes offset value
     */
    @Deprecated
    public int bytesOffset() {
        return bytesOffset;
    }

    /**
     * The baseline specifies the sample value corresponding to 0 physical units
     * 
     * @deprecated recent versions of the spec ignore this field
     * @return the baseline value
     */
    @Deprecated
    public int baseline() {
        return baseline;
    }

    /**
     * Check if the given array of samples match the checksum. Both, signed and
     * unsigned checksum are currently accepted
     *
     * @param samples the array of samples
     * @return true if the checksum matches, otherwise false
     */
    public boolean matchChecksum(int[] samples) {
        int signedChecksum = calculateChecksum(samples);
        int unsignedChecksum = signedChecksum & 0xFFFF;
        return checksum == signedChecksum || checksum == unsignedChecksum;
    }

    /**
     * Calculate the checksum of a given array of signal samples
     * The checksum is a 16bit signed value: 2^16 = 65536
     * 
     * @param samples the array of samples
     * @return the checksum value
     */
    public static int calculateChecksum(int[] samples) {
        int sum = Arrays.stream(samples).sum();
        int unsignedChecksum = Math.floorMod(sum, 65536);
        return unsignedChecksum > 32767 ? unsignedChecksum - 65536 : unsignedChecksum;
    }

}
