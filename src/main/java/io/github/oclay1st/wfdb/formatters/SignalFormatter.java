package io.github.oclay1st.wfdb.formatters;

import io.github.oclay1st.wfdb.HeaderSignal;

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
     * @param headerSignals the array of signals header
     * @return a formatted array of samples
     */
    int[] convertBytesToSamples(byte[] source, HeaderSignal[] headerSignals);

    /**
     * Convert the current formatted samples to the sourcing data.
     *
     * @param samples       the formatted array of samples
     * @param headerSignals the array of signals header
     * @return the raw data of the signals samples
     */
    byte[] convertSamplesToBytes(int[] samples, HeaderSignal[] headerSignals);

}
