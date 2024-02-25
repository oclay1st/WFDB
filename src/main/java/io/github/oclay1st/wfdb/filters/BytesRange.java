package io.github.oclay1st.wfdb.filters;

/**
 * Represents a range of bytes
 * 
 * @param start the start byte index
 * @param end   the end byte index
 */
public record BytesRange(long start, long end) {

    /**
     * Constructor of ByteRange
     */
    public BytesRange {
        if (start < 0 || end < 0) {
            throw new IllegalArgumentException("Byte index can't be less than zero");
        }
        if (start > end) {
            throw new IllegalArgumentException("Start byte index can't be greater than end byte index");
        }
    }

    /**
     * Returns the total bytes of the range
     *
     * @return the value the total bytes
     */
    public long total() {
        return end - start;
    }

}
