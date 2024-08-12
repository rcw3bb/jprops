# Changelog

[TOC]

## 1.3.2: 2024-08-13

### Fix
* The executeLogics in MergeProcessor was bypassed when the key being processes was not in the source properties. 

## 1.3.1: 2024-07-01

### Fix
* Any extra arguments passed to the command will cause the args parsing to stop.

## 1.3.0: 2024-06-26

### New
* Autodetect the EOL when the -os parameter is not provided.
* Add encoding parameter to all the processor.

## 1.2.1: 2024-06-24

### Fix
* Logging stops when it reaches the max file size.

## 1.2.0: 2024-06-24

### New 

* Logback configuration is now part of the distribution.
* Show the available commands if not command was provided.

### Update

* DRYed the Processor implementations.

## 1.1.1 : 2024-06-20

### Fix

* Fix NPE for the uses cases not tested.

## 1.1.0 : 2024-06-19

### New

* Implement broken multiline processor.

## 1.0.1 : 2024-06-14

### Fix

* Fix NPE on the merge command when the destination has more fields than the source.

## 1.0.0 : 2024-06-11

### Initial Version

