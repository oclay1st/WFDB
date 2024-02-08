[![codecov](https://codecov.io/gh/oclay1st/WFDB/graph/badge.svg?token=BFPK6IMO1K)](https://codecov.io/gh/oclay1st/WFDB)
# Java WFDB Library
This library is a pure Java implementation of the Waveform Database(WFDB) [specifications](https://github.com/wfdb/wfdb-spec).

## What is WFDB?
[Waveform Database (WFDB)](https://wfdb.io) is a set of file standards designed for reading and storing physiologic signal data, and associated annotations backed by MIT-LCP members.

## Features:
- [x] Parse WFDB records
- [x] Support for signal format: 8, 16, 24, 32, 61, 80, 160, 212, 310, 311 
- [x] Support for different signal formats and files
- [x] Multi-segment support
- [ ] Parse ranges of time
- [ ] Export records

## Installation using Maven

```xml

<dependency>
  <groupId>com.github.oclay1st</groupId>
  <artifactId>wfdb</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Usage
Parse a single segment record:
```
Path path = Path.of(...);
SingleSegementRecord record = SingleSegmentRecord.parse(path);
```

Parse a multi-segment record:
```
Path path = Path.of(...);
MultiSegmentRecord record = MultiSegmentRecord.parse(path);
```

## Contributing
Checkout for the missing features and feel free to open a issue and create a PR if you find something wrong.

