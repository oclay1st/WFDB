package io.github.oclay1st.wfdb.formatters;

/**
 * Represents the signal formatter interface
 */
public sealed interface SignalFormatter permits SignalFormatter8, SignalFormatter16, SignalFormatter24,
        SignalFormatter32, SignalFormatter61, SignalFormatter80, SignalFormatter160, SignalFormatter212,
        SignalFormatter310, SignalFormatter311 {

    /**
     * Convert the sourcing data to samples of the current format.
     *
     * @param source        the raw data of the signals samples
     * @return a formatted array of samples
     */
    int[] convertBytesToSamples(byte[] source);

    /**
     * Convert the current formatted samples to the sourcing data.
     *
     * @param samples       the formatted array of samples
     * @return the raw data of the signals samples
     */
    byte[] convertSamplesToBytes(int[] samples);

}
