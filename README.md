# egps-shell
A GUI mainframe (shell) for eGPS v2.1, built on eGPS-base.

## Overview

egps-shell is the primary GUI host for eGPS software. While the GUI mainframe is closed-source, it is freely available for all users.

## Downloads

Prebuilt releases for Windows, macOS, and Linux are published under Releases. Download the appropriate package and run it directly.

## Plugin example

The `src/test.plugin` directory contains a complete example plugin. You can package it as a JAR and place it in the plugin path configured in eGPS so that egps-shell recognizes and loads it.

### Files overview

- `IndependentModuleLoader.java` — Entry point for loading external modules/plugins.
- `MainFace.java` — Base GUI face implementation used by the shell.
- `WorkBanch4XXX.java` — Sample workbench implementation for the XXX module.
- `XXXMainFace.java` — Example face class that integrates with the workbench.

## Usage

1. Build a plugin JAR from `src/test.plugin` using your preferred Java build tool.
2. Copy the JAR to the plugin directory specified by the eGPS configuration.
3. Start egps-shell; the plugin should be detected and loaded at startup.

## Notes

For detailed configuration options, please refer to the eGPS-base documentation.
