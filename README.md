[![Maven Central](https://img.shields.io/maven-central/v/io.github.oclay1st/wfdb)](https://central.sonatype.com/artifact/io.github.oclay1st/wfdb)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=oclay1st_WFDB&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=oclay1st_WFDB)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=oclay1st_WFDB&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=oclay1st_WFDB)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=oclay1st_WFDB&metric=coverage)](https://sonarcloud.io/summary/new_code?id=oclay1st_WFDB)
# Java WFDB Library
This library is a pure Java implementation of the Waveform Database(WFDB) [specifications](https://github.com/wfdb/wfdb-spec).

## What is WFDB?
[Waveform Database (WFDB)](https://wfdb.io) is a set of file standards designed for reading and storing physiologic signal data, and associated annotations backed by MIT-LCP members.

## Features:
- [x] Parse single-segment and multi-segment records
- [x] Support for signal format: 8, 16, 24, 32, 61, 80, 160, 212, 310, 311 
- [x] Support for different signal formats and files
- [x] Filter by a range of time
- [x] Filter by signals indices
- [x] Export single-segment records

## Usage

Parse a single-segment record:
```
Path path = Path.of(...);
SingleSegementRecord record = SingleSegmentRecord.parse(path);
```

Parse and filter a single-segment record by time:
```
Path path = Path.of(...);
long start = 0; // 0 millisecond
long end = 1000; // first second 
Filter filter = new Filter.Builder().startTime(start).endTime(end).build();
SingleSegementRecord record = SingleSegmentRecord.parse(path, filter);
```

Parse and filter a single-segment record by signal indices:
```
Path path = Path.of(...);
Filter filter = new Filter.Builder().signals(new int[]{0, 1, 2}).build();
SingleSegementRecord record = SingleSegmentRecord.parse(path, filter);
```

Parse a multi-segment record:
```
Path path = Path.of(...);
MultiSegmentRecord record = MultiSegmentRecord.parse(path);
```

Export a single-segment record:
```
...
SingleSegmentRecord record = SingleSegmentRecord.parse(path, filter);
Path exportPath = Path.of(...);
record.export(exportPath);
```

## Contributing
Checkout for the missing features and feel free to open a issue and create a PR if you find something wrong.

