package io.github.oclay1st.wfdb.filters;

import java.util.List;
import java.util.stream.IntStream;

import io.github.oclay1st.wfdb.records.HeaderRecord;
import io.github.oclay1st.wfdb.records.HeaderSignal;
import io.github.oclay1st.wfdb.records.SingleSegmentHeader;

/**
 * Represents a filter processor.
 *
 */
public class FilterProcessor {

    private final Filter filter;

    private final SingleSegmentHeader header;

    private final boolean isFilterAsDefault;

    private FilterProcessor(Filter filter, SingleSegmentHeader header, boolean isFilterAsDefault) {
        this.filter = filter;
        this.header = header;
        this.isFilterAsDefault = isFilterAsDefault;
    }

    /**
     * Process the filter, fill data and create a new instance of a
     * {@link FilterProcessor}
     *
     * @param filter the {@link Filter} instance
     * @param header the {@link HeaderSignal} instance
     * @return a new instance of a filter processor
     */
    public static FilterProcessor process(Filter filter, SingleSegmentHeader header) {
        long duration = header.record().durationTime().toMillis();
        long startMilliseconds = filter.startTime() != null ? filter.startTime() : 0;
        long endMilliseconds = filter.endTime() != null ? filter.endTime() : duration;
        int[] indices = IntStream.range(0, header.signals().size()).toArray();
        if (filter.signals() != null) {
            indices = filter.signals();
        }
        Filter newFilter = new Filter.Builder()
                .startTime(startMilliseconds)
                .endTime(endMilliseconds)
                .signals(indices)
                .build();
        return new FilterProcessor(newFilter, header, filter.isAsDefault());
    }

    /**
     * Generate a filtered header.
     *
     * @return a {@link SingleSegmentHeader} instance
     */
    public SingleSegmentHeader generateFilteredHeader() {
        if (isFilterAsDefault) {
            return header;
        }
        return new SingleSegmentHeader(generateHeaderRecord(), generateHeaderSignals(), header.comments());
    }

    /**
     * Generate a header record.
     *
     * @return a new {@link HeaderRecord} instance
     */
    private HeaderRecord generateHeaderRecord() {
        HeaderRecord headerRecord = header.record();
        int numberOfSignals = filter.signals().length;
        long duration = filter.endTime() - filter.startTime();
        int numberOfSamplesPerSignal = Math.round(headerRecord.samplingFrequency() * duration / 1000);
        return new HeaderRecord(headerRecord.name(), headerRecord.numberOfSegments(), numberOfSignals,
                headerRecord.samplingFrequency(), headerRecord.counterFrequency(), headerRecord.baseCounter(),
                numberOfSamplesPerSignal, headerRecord.baseTime(), headerRecord.baseDate());
    }

    /**
     * Generate an array of header signals.
     *
     * @return a new array of {@link HeaderSignal}
     */
    private List<HeaderSignal> generateHeaderSignals() {
        return IntStream.of(filter.signals())
                .mapToObj(index -> {
                    HeaderSignal signal = header.signals().get(index);
                    return new HeaderSignal(signal.filename(), signal.format(), signal.samplesPerFrame(), signal.skew(),
                            signal.bytesOffset(), signal.adcGain(), signal.baseline(), signal.unit(),
                            signal.adcResolution(), signal.adcZero(), 0, 0, signal.blockSize(), signal.description());
                }).toList();
    }

    /**
     * Calculate the bytes range for the signals of the same file
     *
     * @param headerSignals the array of header signals
     * @return a {@link BytesRange} instance
     */
    public BytesRange calculateBytesRange(List<HeaderSignal> headerSignals) {
        // Take the format, the bytes offset of the first header signals because by
        // definition those values are the same in the signal file
        float bytesPerSample = headerSignals.get(0).format().bytesPerSample();
        int bytesOffset = headerSignals.get(0).bytesOffset();
        long start = bytesOffset + calculateByteIndex(filter.startTime(), bytesPerSample, headerSignals.size());
        long end = bytesOffset + calculateByteIndex(filter.endTime(), bytesPerSample, headerSignals.size());
        return new BytesRange(start, end);
    }

    /**
     * Calculate the byte index given the milliseconds, the bytes per sample and the
     * number of signals
     *
     * @param milliseconds    the value of the milliseconds
     * @param bytesPerSample  the value of the bytes per sample
     * @param numberOfSignals the value of number of signals
     * @return the byte index value
     */
    private long calculateByteIndex(long milliseconds, float bytesPerSample, int numberOfSignals) {
        int numberOfSamplesPerSignal = Math.round(header.record().samplingFrequency() * milliseconds / 1000);
        int numberOfSamples = numberOfSamplesPerSignal * numberOfSignals;
        return (int) Math.ceil(numberOfSamples * bytesPerSample);
    }

}
