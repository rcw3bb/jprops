# Build

## Pre-requisite

* [BuildTools for Visual Studio](https://visualstudio.microsoft.com/downloads/)
  * Install **Desktop development with C++**

* [GraalVM for Java 21](https://www.graalvm.org/downloads/)

## Testing

Run the following command to test the application:

```
gradlew clean check
```

> The preceding command must be run from the location where you've cloned the repository.

## Building

Run the following command to build the application:

```
gradlew build
```

> The preceding command must be run from the location where you've cloned the repository.

## Building Standalone Executable

Run the following command to build the application executable:

```
gradlew buildExec
```

> The executable will be available in the following directory:
>
> ```
> <REPO_DIR>\build\bin
> ```
> REPO_DIR is the location where you've cloned the repository.

## Packaging with Java

The package is not building the executable but packaging the application with java.

Run the following command to build the application:

```
gradlew packWin
```

> The preceding command must be run from the location where you've cloned the repository.

> The package will be available in following directory:
>
> ```
> <REPO_DIR>\build\pack
> ```
>
> REPO_DIR is the location where you've cloned the repository.

