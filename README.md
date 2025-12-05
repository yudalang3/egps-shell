# egps-shell
A GUI mainframe (shell) for eGPS v2.1, built on eGPS-base.


# Manuals

Please see the docs file for references to the eGPS-shell documentation.

## Overview

egps-shell is the primary GUI host for eGPS software. While the GUI mainframe is closed-source, it is freely available for all users.

## Downloads

Prebuilt releases for Windows, macOS, and Linux are published under Releases. Download the appropriate package and run it directly.

## Usage

1. Build a plugin JAR from `src/test.plugin` using your preferred Java build tool.
2. Copy the JAR to the plugin directory specified by the eGPS configuration.
3. Start egps-shell; the plugin should be detected and loaded at startup.

For Windows users:

```shell
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher4Dev
```

For macOS/Linux users:

```shell
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
```

## Notes

For detailed configuration options, please refer to the eGPS-base documentation.
