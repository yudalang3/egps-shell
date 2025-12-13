# egps-shell

[English](README.md) | [中文](README_zh.md)

![eGPS-shell Screenshot](https://github.com/yudalang3/egps-shell/blob/main/snapshot/ScreenShot_2025-12-13_171628_725.png?raw=true)

A GUI main framework (shell) for eGPS 2.1, built on eGPS-base.

# Manuals

Please check the `module_dev_references/docs` directory for the eGPS-shell documentation references.

## Overview

egps-shell is the primary GUI host for eGPS software—the base scaffold. Although the GUI main framework is closed-source, all users can use it free of charge.

## Downloads

Prebuilt packages for Windows, macOS, and Linux are available under Releases. Download the package for your platform and run it directly.

To keep iteration fast, the JRE runtime is not bundled for now. Please install a JRE yourself. You can use any Java runtime such as OpenJDK, Oracle JDK, AdoptOpenJDK, Zulu, or Microsoft JDK.

Some handy links to get a Java runtime quickly:

- [Integrated downloads for users in mainland China](https://www.injdk.cn/)
- [AdoptOpenJDK](https://adoptopenjdk.net/releases.html)
- [Microsoft JDK](https://learn.microsoft.com/en-us/java/openjdk/install)

## Usage

1. Build a plugin JAR from `src/test.plugin` using your preferred Java build tool.
2. Copy that JAR to the plugin directory specified by the eGPS configuration.
3. Launch egps-shell; the plugin is detected and loaded at startup.

For Windows users:

```shell
# java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher4Dev
java -cp "dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher
```

For macOS/Linux users:

```shell
# java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
java -cp "dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
```

## Notes

For detailed configuration, see the eGPS-base documentation in the docs directory.

# AI-assistant prompts

## Case study 1

Build a module from scratch using the official VOICE framework:

```shell
I am developing an eGPS module and need the main framework's VOICE capability. The official sample code is in ./module_dev_references/voice and the documentation is under ./module_dev_references/docs/. Focus on VOICE_MODULE_ARCHITECTURE.md and the module_plugin_course directory.

The module name is abcdefg, the module description is abcdefg, the module version is 1.0.0, and the author is xyz.
The primary function is abcdefg.
```

## Case study 2

Refactor an existing module:

```shell
I am refactoring an eGPS module named xxxxxx by author xxx. I need to use the main framework's VOICE capability. The official sample code is in ./module_dev_references/voice and the documentation is under ./module_dev_references/docs/. Focus on VOICE_MODULE_ARCHITECTURE.md and the module_plugin_course directory.

Please convert it to the dockable/floating/handytools style in the VOICE framework.
Assume we are using the floating style: all entry parameters are String and all return values are String. Path parameters must use the path. prefix, such as path.input.file.

Also help me wire up the CLI so I can run it from the command line.
```
