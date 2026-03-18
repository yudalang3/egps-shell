# egps-main

[English](README.md) | [中文](README_zh.md)

![egps-shell Screenshot](https://github.com/yudalang3/egps-shell/blob/main/snapshot/ScreenShot_2025-12-13_171628_725.png?raw=true)

`egps-main` is the closed-source code repository for `egps-shell`. `egps-shell` is the GUI shell base of eGPS, used to host desktop modules. The main external-facing documentation lives in `docs/` and `manuals/`.

If you need a bundled distribution that includes `egps-base`, `egps-shell`, and `egps-pathway.evol.browser`, visit: https://github.com/yudalang3/egps-pathway.evol.browser

## Overview

- `egps-main` is the maintained source project.
- `egps-shell` is the GUI shell and baseline runtime described by the public docs.
- `egps2` is the main Java package namespace used by the current codebase.
- The application is Swing-based and supports modular loading, plugin integration, and VOICE-based workflows.

## Documentation Map

- `README.md` / `README_zh.md`: repository entry for `egps-main`
- `docs/`: reference documentation for `egps-shell`
- `manuals/`: tutorials and practical guides for `egps-shell`
- `manuals/module_plugin_course/`: focused material for module and plugin development

## Runtime Configuration

`egps-shell` mainly uses the following runtime locations and conventions:

- User configuration directory: `~/.egps2/config`
- Module loading configuration: `~/.egps2/config/egps2.loading.module.config.txt`
- Plugin directory: `~/.egps2/config/plugin/`
- Recommended runtime argument file: `@eGPS.args` (`eGPS.args` is a text file; `@eGPS.args` is the Java command-line syntax for reading that file and applying its contents as runtime arguments)

`@eGPS.args` contains the `--add-exports` and `--add-opens` options required by the current Java runtime setup, so it should be included in normal launches.

## Build From Source

This repository is typically developed in IntelliJ IDEA with JDK 25. Dependencies are mainly managed through `dependency-egps/*`.
It is a basic Java project and does not use Maven or Gradle as the build workflow, which keeps it straightforward for direct local use.

On macOS/Linux, a minimal manual compilation command looks like this:

```sh
javac -d ./out/production/egps-main.gui -cp "dependency-egps/*" $(find src -name "*.java")
```

After compilation, class files should be located in `out/production/egps-main.gui`. The repository's `build_jar_and_move.sh` can package the shell JAR and copy it into a local deployment directory, but it is mainly intended for the maintainer's own local environment.

## Run From Source

At runtime, you need both the compiled classes and the dependency JARs.

For Windows:

```sh
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher com.example.YourModuleLoader
```

For macOS/Linux:

```sh
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher com.example.YourModuleLoader
```

The third command launches a specific module directly by passing the fully qualified loader class name.

## VOICE CLI

If the module is VOICE-based and exposes `SubTabModuleRunner`, you can use `egps2.builtin.modules.CLI`. The first argument is the module class name, and the second argument is a configuration file in the same format used by the VOICE GUI.

Example on macOS/Linux:

```sh
java -cp "out/production/egps-main.gui:dependency-egps/*" @eGPS.args egps2.builtin.modules.CLI your.package.YourRunner path/to/config.txt
```

On Windows, use `;` instead of `:` in the classpath.

## Notes

- `docs/` and `manuals/` form the public-facing documentation set for `egps-shell`.
- This README stays repository-focused; deeper product and framework explanations belong in those two directories.

## AI-Assisted Development

We support and encourage users to develop their own tools on top of the eGPS 2.1 platform. Swing as a Java GUI framework rose in the 1990s; while it is no longer evolving with many new features, it remains stable and is still a practical development option.

### Case 1: Create a new VOICE-based module

```text
I am developing a new eGPS module in `egps-main` and want to use the `egps-shell` VOICE framework.
Please study:
- `docs/voiceFramework/VOICE_MODULE_ARCHITECTURE.md`
- `docs/voiceFramework/VOICE-GUI.md`
- `manuals/module_plugin_course/`

The module name is abcdefg.
The module description is abcdefg.
The version is 1.0.0.
The author is xyz.
The primary function is abcdefg.

Please implement it in the appropriate VOICE style and wire up the relevant entry points.
```

### Case 2: Refactor an existing module into VOICE style

```text
I am refactoring an existing eGPS module in `egps-main`.
Please use the `egps-shell` VOICE framework and study:
- `docs/voiceFramework/VOICE_MODULE_ARCHITECTURE.md`
- `docs/voiceFramework/VOICE-GUI.md`
- `manuals/module_plugin_course/`

The module name is xxxxxx.
The author is xxx.

Please convert it to the dockable, floating, or handytools style.
Assume we are targeting the floating style:
- all input parameters are `String`
- all return values are `String`
- path parameters use the `path.` prefix, such as `path.input.file`

Also help wire up the CLI so the module can run from the command line.
```
