# JProps

A simple properties file content manager.

[TOC]

## Syntax

The syntax is as follows:

``` 
jprops <command> [params]
```

Where:

The `command` is the command to perform. Use the `help` without any parameters to see all the available commands.

The `params` is one or more parameters to influence the behavior of the command.

## Usage

### Managing Broken Multiline

A broken multiline field in a properties file refers to a property that is intended to span multiple lines but is incorrectly formatted, causing it to be interpreted as multiple separate properties.  In a properly formatted properties file, a property that spans multiple lines would look something like this:

```properties
key = value line 1 \
      value line 2 \
      value line 3
```

The previous line should end with a backslash *(\\)*.  If this format is not followed, the property is considered `broken`. For example, if the backslash is missing, the properties file parser will interpret each line as a separate line, which is usually not the intended behavior.

#### View broken multiline

The command for displaying broken multiline properties in a properties file is as follows:

```
jprops bmline -p <file>
```

In this command, `jprops` is the main command for the JProps tool, `bmline` is the sub-command used to manage broken multiline properties,`-p`is the option to specify the properties file, and `<file>` is the path to the properties file you want to check for broken multiline properties.

#### Fixing broken multiline

To fix broken multiline properties in a properties file, use the following command:

```
jprops bmline -p <file> -fix
```

In this command, `jprops` is the main command for the JProps tool, `bmline` is the sub-command used to manage broken multiline properties, `-p` is the option to specify the properties file, `<file>` is the path to the properties file you want to check for broken multiline properties, and `-fix` is the option to fix the broken multiline properties.

### Managing Duplicates

A duplicate property in a properties file refers to a property that has the same key as another property.  In a properly formatted properties file, each property should have a unique key.  If a key is repeated, the properties file parser will usually use the last value for the key.  This can lead to unexpected behavior if the same key is used for different values.

#### Viewing duplicates

To view duplicate properties in a properties file, use the following command:

```
jprops duplicate -p <file>
```

In this command, `jprops` is the main command for the JProps tool, `duplicate` is the sub-command used to manage duplicate properties, `-p` is the option to specify the properties file, and `<file>` is the path to the properties file you want to check for duplicate properties.

#### Resolving duplicates

To resolve duplicate properties in a properties file, use the following command:

```
jprops duplicate -p <file> -dedupe
```

In this command, `jprops` is the main command for the JProps tool, `duplicate` is the sub-command used to manage duplicate properties, `-p` is the option to specify the properties file, `<file>` is the path to the properties file you want to check for duplicate properties, and `-dedupe` is the option to resolve the duplicate properties.

### Merging Properties Files

Merging properties files is a common task when working with multiple properties files that contain different configurations.  Merging properties files allows you to combine the configurations from multiple files into a single file.

#### Viewing what will be merged

To view the fields that will be merged from the source file to the destination file, use the following command:

```
jprops merge -sp <source-file> -dp <destination-file>
```

In this command, `jprops` is the main command for the JProps tool, `merge` is the sub-command used to manage merging properties files, `-sp` is the option to specify the source properties file, `<source-file>` is the path to the source properties file, `-dp` is the option to specify the destination properties file, and `<destination-file>` is the path to the destination properties file.

#### Doing the actual merge

The fields from the source file always overrides the fields in destination file.

To merge the fields from the source file to the destination file, use the following command:

```
jprops merge -sp <source-file> -dp <destination-file> -apply
```

In this command, `jprops` is the main command for the JProps tool, `merge` is the sub-command used to manage merging properties files, `-sp` is the option to specify the source properties file, `<source-file>` is the path to the source properties file, `-dp` is the option to specify the destination properties file, `<destination-file>` is the path to the destination properties file, and `-apply` is the option to apply the merge.

### Sorting Fields

Sorting fields in a properties file is a common task when working with properties files that contain a large number of properties.  Sorting the fields allows you to organize the properties in a more readable and maintainable way.

#### Viewing sorted fields

To view the sorted fields in a properties file, use the following command:

```
jprops sort -p <file>
```

In this command, `jprops` is the main command for the JProps tool, `sort` is the sub-command used to manage sorting fields in a properties file, `-p` is the option to specify the properties file, and `<file>` is the path to the properties file you want to sort.

#### Perform the sorting of fields

All non-field-value entries will be removed.

To sort the fields in a properties file, use the following command:

```
jprops sort -p <file> -apply 
```

In this command, `jprops` is the main command for the JProps tool, `sort` is the sub-command used to manage sorting fields in a properties file, `-p` is the option to specify the properties file, `<file>` is the path to the properties file you want to sort, and `-apply` is the option to apply the sort.

## Line Ending

All the available commands can use the `os` parameter to influence the line ending to use. For example to sort a properties files and use a `linux` line ending, use the following syntax:

```
jprops sort -p <file> -apply -os linux
```

If not provided the default it uses the system line ending.

The supported parameters for the `-os` is as follows:

* Windows
* Linux
* Mac

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## [Build](BUILD.md)

## [Changelog](CHANGELOG.md)

## Author

* Ronaldo Webb
