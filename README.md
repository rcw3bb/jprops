# JProps

A simple properties file manager.

[TOC]

## Syntax

``` 
jprops <command> [params]
```

Where:

The `command` is the command to perform. Use the `help` without any parameters to see all the available commands.

The `params` is one or more parameters to influence the behavior of the command.

## Usage

### Managing Broken Multiline

#### View broken multiline

```
jprops bmline -p <file>
```

#### Fixing broken multiline

```
jprops bmline -p <file> -fix
```

### Managing Duplicates

#### Viewing duplicates

```
jprops duplicate -p <file>
```

#### Resolving duplicates

```
jprops duplicate -p <file> -dedupe
```

### Merging Properties Files

#### Viewing what will be merged

```
jprops merge -sp <source-file> -dp <destination-file>
```

#### Doing the actual merge

The fields from the source file always overrides the fields in destination file.

```
jprops merge -sp <source-file> -dp <destination-file> -apply
```

### Sorting Fields

#### Viewing sorted fields

```
jprops sort -p <file>
```

#### Perform the sorting of fields

All non-field-value entries will be removed.

```
jprops sort -p <file> -apply 
```

## Line Ending

All the available commands can use the `os` parameter to influence the line ending to use. For example to sort a properties files and use a `linux` line ending, use the following syntax:

```
jprops sort -p <file> -apply -os linux
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## [Build](BUILD.md)

## [Changelog](CHANGELOG.md)

## Author

* Ronaldo Webb
