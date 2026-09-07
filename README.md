# egps-shell

[English](README.md) | [中文](README_zh.md)

![egps-shell Screenshot](https://github.com/yudalang3/egps-shell/blob/main/snapshot/ScreenShot_2025-12-13_171628_725.png?raw=true)

`egps-shell` is a fully open-source software product that provides the GUI shell framework for hosting eGPS desktop modules. This repository contains its main framework source code under the Apache License 2.0; see [LICENSE](LICENSE). Reference documentation and development tutorials are available in `docs/` and `manuals/`.

If you need a bundled distribution that includes `egps-base`, `egps-shell`, and `egps-pathway.evol.browser`, visit: https://github.com/yudalang3/egps-pathway.evol.browser

## Overview

- `egps-shell` is the maintained open-source GUI framework and baseline runtime.
- `egps-main.gui` is the historical name retained by the IDEA module and build output directory.
- `egps2` is the main Java package namespace used by the current codebase.
- The application is Swing-based and supports modular loading, plugin integration, and VOICE-based workflows.

## Documentation Map

- `README.md` / `README_zh.md`: repository entry for `egps-shell`
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

Use JDK 25 and run commands from the repository root. Dependencies are managed through `dependency-egps/*`; this project does not use Maven or Gradle. The dependency directory is not tracked by Git, so prepare matching dependency JARs (including `egps-base`) first.

The current IDEA module inherits project output settings. Its name is `egps-main.gui`, normally producing `out/production/egps-main.gui`. The manual commands below use that path and exclude `src/test/`. Start with an empty output directory to avoid packaging stale classes or previously compiled tests.

`javac` does not copy images, HTML, fonts, or other resources. After compilation succeeds, copy non-Java files while preserving their paths relative to `src`.

Windows PowerShell 7:

```powershell
$buildDir = "out/production/egps-main.gui"
New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
$sourceRoot = (Resolve-Path src).Path
Get-ChildItem src -Recurse -Filter *.java |
    Where-Object { $_.FullName -notlike '*\src\test\*' } |
    ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' } |
    Set-Content -Encoding utf8 out/sources.txt
javac -encoding UTF-8 -d $buildDir -cp "dependency-egps/*" '@out/sources.txt'
if ($LASTEXITCODE -ne 0) { throw "Compilation failed" }
Get-ChildItem src -Recurse -File |
    Where-Object { $_.Extension -ne '.java' -and $_.FullName -notlike '*\src\test\*' } |
    ForEach-Object {
        $relativePath = $_.FullName.Substring($sourceRoot.Length + 1)
        $destination = Join-Path $buildDir $relativePath
        New-Item -ItemType Directory -Force -Path (Split-Path $destination) | Out-Null
        Copy-Item -LiteralPath $_.FullName -Destination $destination
    }
```

macOS/Linux (Bash with native Java):

```bash
build_dir="out/production/egps-main.gui"
mkdir -p "$build_dir"
find src -path src/test -prune -o -name '*.java' -print | sed 's/.*/"&"/' > out/sources.txt
javac -encoding UTF-8 -d "$build_dir" -cp "dependency-egps/*" @out/sources.txt &&
find src -path src/test -prune -o -type f ! -name '*.java' -exec sh -c '
    build_dir=$1
    shift
    for source_file do
        relative_path=${source_file#src/}
        mkdir -p "$build_dir/$(dirname "$relative_path")"
        cp "$source_file" "$build_dir/$relative_path"
    done
' sh "$build_dir" {} +
```

## Packaging and Local Deployment

After compiling and copying resources, package directly (both platforms):

```text
jar --create --file out/egps-shell-0.0.1.jar -C out/production/egps-main.gui .
```

This only packages existing output: it does not compile, bundle dependency JARs, or copy to deployment directories. Prepare dependencies separately for deployment.

Maintainers may have a local `build_jar_and_move.sh`. It is Git-ignored and is not a prerequisite for a fresh clone. It requires Bash, packages existing classes, and copies the JAR to predefined local directories; `/mnt/c/...` targets are intended for WSL. Check its destinations before use; it is not a general build command.

## Run From Source

At runtime, you need both the compiled classes and the dependency JARs.

For Windows PowerShell 7:

```powershell
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher4Dev
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher com.example.YourModuleLoader
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

On Windows PowerShell, use `;` instead of `:` in the classpath and quote the argument file as `'@eGPS.args'`.

## Tests and Completion Checks

Tests live under `src/test/` and use standalone `main()` entry points; no Maven/JUnit test workflow is configured. Compile tests separately into `out/test-classes` to keep them out of the release JAR.

Compile the main sources and copy resources before running this discovery diagnostic example.

Windows PowerShell 7:

```powershell
javac -encoding UTF-8 -d out/test-classes -cp "out/production/egps-main.gui;dependency-egps/*" src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java
java -cp "out/test-classes;out/production/egps-main.gui;dependency-egps/*" '@eGPS.args' test.egps2.frame.features.ModuleDiscoveryServiceTest
```

macOS/Linux:

```bash
javac -encoding UTF-8 -d out/test-classes -cp "out/production/egps-main.gui:dependency-egps/*" src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java
java -cp "out/test-classes:out/production/egps-main.gui:dependency-egps/*" @eGPS.args test.egps2.frame.features.ModuleDiscoveryServiceTest
```

- `test.egps2.frame.features.ModuleDiscoveryServiceTest`: checks scanning and filtering; its package really includes `test.`.
- `egps2.frame.features.ModuleDiscoveryTest`: checks scanning, configuration reading, and merging. Compile the corresponding source in the same directory and explicitly pass a prepared temporary configuration path rather than relying on its default example path. See the [module discovery documentation](docs/itoolsManager/itoolmanager_module_discovery_statement.md) for the configuration behavior.
- These entry points mainly print diagnostics; exit code zero does not establish that all expectations passed. In particular, `ModuleDiscoveryServiceTest` still expects core modules to be excluded, which differs from the [current exclusion rules](docs/module&pluginSystem/module_discovery_exclusion_rules.md). Review individual results. Scanning also reads the user plugin directory; use `-Duser.home=temporary-directory` and prepare fixtures when isolation is needed.

Before finishing, synchronize both README languages and affected paired documents, check local links, entry-point names and commands, and report which validations ran and their limitations. Do not require unconfigured lint steps.

## Notes

- `docs/` and `manuals/` form the public-facing documentation set for `egps-shell`.
- This README stays repository-focused; deeper product and framework explanations belong in those two directories.

## AI-Assisted Development

We support and encourage users to develop their own tools on top of the eGPS 2.1 platform. Swing as a Java GUI framework rose in the 1990s; while it is no longer evolving with many new features, it remains stable and is still a practical development option.

### Case 1: Create a new VOICE-based module

```text
I am developing a new eGPS module in `egps-shell` and want to use the `egps-shell` VOICE framework.
Please study:
- `manuals/01_VOICE_architecture.md`
- `manuals/02_VOICE_GUI_design.md`
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
I am refactoring an existing eGPS module in `egps-shell`.
Please use the `egps-shell` VOICE framework and study:
- `manuals/01_VOICE_architecture.md`
- `manuals/02_VOICE_GUI_design.md`
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
