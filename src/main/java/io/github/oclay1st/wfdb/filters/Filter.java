package io.github.oclay1st.wfdb.filters;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the filter of a record.
 * 
 * @param startTime the value of the start on milliseconds
 * @param endTime   the value of the end on milliseconds
 * @param signals   the array of signals indices
 */
public record Filter(Long startTime, Long endTime, int[] signals) {

    /**
     * Represents a builder of a {@link Filter} instance.
     */
    public static class Builder {

        private Long startTime;

        private Long endTime;

        private int[] signals;

        /**
         * Set the value of the start on milliseconds.
         * 
         * @param milliseconds the value of the starting position on milliseconds
         * @return the builder instance
         */
        public Builder startTime(long milliseconds) {
            this.startTime = milliseconds;
            return this;
        }

        /**
         * Set the value of the end on milliseconds.
         * 
         * @param milliseconds the value of the ending position on milliseconds
         * @return the builder instance
         */
        public Builder endTime(long milliseconds) {
            this.endTime = milliseconds;
            return this;
        }

        /**
         * Set the value of the array of signal indices.
         *
         * @param indices the array of signal indices
         * @return the builder instance
         */
        public Builder signals(int[] indices) {
            this.signals = indices;
            return this;
        }

        /**
         * Build a new {@link Filter} instance.
         * 
         * @return the new instance
         */
        public Filter build() {
            return new Filter(startTime, endTime, signals);
        }

    }

    /**
     * Returns true if the filter was created with default values.
     * 
     * @return true if all the attributes are as default, otherwise false.
     */
    public boolean isAsDefault() {
        return startTime == null && endTime == null && signals == null;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Filter filter && Objects.equals(startTime, filter.startTime)
                && Objects.equals(endTime, filter.endTime) && Arrays.equals(signals, filter.signals);
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hash(startTime, endTime) + Arrays.hashCode(signals);
    }

    @Override
    public String toString() {
        String signalsText = Arrays.toString(signals);
        return "Filter [startTime = " + startTime + ", endTime = " + endTime + ", signals = " + signalsText + "]";
    }

}
