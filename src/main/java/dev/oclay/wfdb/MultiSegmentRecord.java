package dev.oclay.wfdb;

public record MultiSegmentRecord(MultiSegmentHeader header, SingleSegmentRecord[] records) {
    
}
