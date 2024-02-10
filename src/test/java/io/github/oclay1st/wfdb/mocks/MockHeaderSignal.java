package io.github.oclay1st.wfdb.mocks;

import io.github.oclay1st.wfdb.HeaderSignal;
import io.github.oclay1st.wfdb.SignalFormat;
import io.github.oclay1st.wfdb.SignalUnit;

public class MockHeaderSignal {

    public static class Builder {
        private String filename = "test.dat";
        private SignalFormat format = SignalFormat.FORMAT_16;
        private int samplesPerFrame = 1;
        private int skew = 0;
        private int bytesOffset = 0;
        private float adcGain = 0;
        private int baseline = 0;
        private SignalUnit unit = SignalUnit.MILLIVOLT;
        private int adcResolution = 0;
        private int adcZero = 0;
        private int initialValue = 0;
        private int checksum = 0;
        private int blockSize = 0;
        private String description = "Signal Test";

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder format(SignalFormat format) {
            this.format = format;
            return this;
        }

        public Builder samplesPerFrame(int samplesPerFrame) {
            this.samplesPerFrame = samplesPerFrame;
            return this;
        }

        public Builder skew(int skew) {
            this.skew = skew;
            return this;
        }

        public Builder bytesOffset(int bytesOffset) {
            this.bytesOffset = bytesOffset;
            return this;
        }

        public Builder adcGain(float adcGain) {
            this.adcGain = adcGain;
            return this;
        }

        public Builder baseline(int baseline) {
            this.baseline = baseline;
            return this;
        }

        public Builder unit(SignalUnit unit) {
            this.unit = unit;
            return this;
        }

        public Builder adcResolution(int adcResolution) {
            this.adcResolution = adcResolution;
            return this;
        }

        public Builder adcZero(int adcZero) {
            this.adcZero = adcZero;
            return this;
        }

        public Builder initialValue(int initialValue) {
            this.initialValue = initialValue;
            return this;
        }

        public Builder checksum(int checksum) {
            this.checksum = checksum;
            return this;
        }

        public Builder blockSize(int blockSize) {
            this.blockSize = blockSize;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public HeaderSignal build() {
            return new HeaderSignal(filename, format, samplesPerFrame, skew, bytesOffset, adcGain, baseline, unit,
                    adcResolution, adcZero, initialValue, checksum, blockSize, description);
        }

    }
}
