package dev.oclay.wfdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public record Header(Record record, Signal[] signals) {

    public static Header parse(InputStream input) throws IOException, ParseException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        Record headerRecord = null;
        Signal[] signals = null;
        int signalIndex = 0;
        String headerLine;
        boolean recordProcessed = false;
        while ((headerLine = reader.readLine()) != null) {
            String stripedHeaderLine = headerLine.strip();
            if (stripedHeaderLine.isEmpty() || stripedHeaderLine.charAt(0) == '#') {
                continue;
            }
            if (!recordProcessed) {
                headerRecord = Record.parse(stripedHeaderLine);
                signals = new Signal[headerRecord.numberOfSignals()];
                recordProcessed = true;
            } else {
                signals[signalIndex] = Signal.parse(stripedHeaderLine);
                signalIndex++;
            }
        }
        return new Header(headerRecord, signals);
    }

    public boolean isSingleFileFormat() {
        return Arrays.stream(signals)
                .allMatch(signal -> signal.format() == signals[0].format() &&
                        signal.filename().equals(signals[0].filename()));
    }

    @Override
    public String toString() {
        return "Header [record = " + record + ", signals = " + Arrays.toString(signals) + "]";
    }

}
