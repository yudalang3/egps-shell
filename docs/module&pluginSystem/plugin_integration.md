# egps-shell Plugin Integration Reference

This document describes how plugin integration currently works in `egps-shell`, with implementation behavior verified against the current `egps-main` codebase.

## Overview

`egps-shell` supports two related but distinct plugin paths:

1. **Plugin menu loading** from `~/.egps2/config/plugin/*.jar`
2. **Module discovery and config-based loading** used by the main shell, Module Gallery, and the module management table

These paths overlap, but they are not the same pipeline and should not be described as if they were a single automatic mechanism.

## Built-in Modules vs Plugin Modules

- **Built-in modules** are available from the main application classpath, including modules compiled from source and modules packaged in `dependency-egps/*`.
- **Plugin modules** are external JARs placed in `~/.egps2/config/plugin/`.
- Both kinds of modules may implement `IModuleLoader`, but they enter the shell through different runtime paths.

## Runtime Paths

### 1. Plugins menu path

The Plugins menu is configured by `egps2.plugin.manager.PluginOperation`.

Current behavior:

- It scans `~/.egps2/config/plugin/` for JAR files.
- For each JAR, it reads `eGPS2.plugin.properties`.
- It loads the plugin entry class on demand when the user clicks the menu item.
- This path remains independent from the config-merge path used by module discovery.

This means a plugin can still have a direct JAR-menu entry even though the shell also has a broader module discovery system.

### 2. Discovery + config merge path

The main shell loading path is coordinated by:

- `egps2.frame.features.ModuleDiscoveryService`
- `egps2.frame.features.EGPS2ServiceLoader`
- `egps2.frame.MainFrameProperties`

Current behavior:

- `ModuleDiscoveryService` scans the full JVM classpath using Reflections.
- It also manually scans plugin JARs under `~/.egps2/config/plugin/`.
- `EGPS2ServiceLoader.loadWithDiscovery(...)` merges discovered classes with `~/.egps2/config/egps2.loading.module.config.txt`.
- `MainFrameProperties.getExistedLoaders()` returns the modules that are actually marked to load after that merge step.

## What “Discovered” Means

Discovery alone does not mean a module is immediately part of the active module list.

The current merge behavior is:

- **In config + discovered**: tracked as available; loaded only if the config flag says `true`
- **Not in config + discovered**: tracked as newly discovered; default is not loaded
- **In config + not discovered**: tracked as unavailable

This distinction matters because the shell currently tracks more modules than it actively loads.

## Module Gallery and Module Management Behavior

The current shell does **not** behave like “everything discovered automatically appears as an active gallery module”.

What actually happens now:

- `DemoButtonsOrganizer` uses `MainFrameProperties.getExistedLoaders()`
- `getExistedLoaders()` reflects the config-merged load result, not the raw discovery set
- The gallery therefore respects the current load/config state
- The module management table (`itoolmanager`) uses `MainFrameProperties.getAllProviders()` and can show tracked statuses such as available, newly discovered, and unavailable

So the accurate statement is:

> `egps-shell` discovers more modules than it immediately loads, and the current gallery view follows the configured active loader set.

## Discovery Sources and Rules

### Scan sources

Current discovery scans:

- the JVM classpath
- plugin JARs from `~/.egps2/config/plugin/`

This is broader than the old fixed-package wording that only mentioned a small package list.

### Exclusion rules

`ModuleDiscoveryService` currently excludes:

- interfaces
- abstract classes
- SubTab-style classes
- template base classes such as `FastBaseTemplate` itself

It does **not** use “core module exclusion” as the primary explanation for why some modules are not treated as newly active gallery modules. Core-module handling and config merge are separate concerns.

### Duplicate handling

If the same module class is found in both the classpath and a plugin JAR:

- the classpath version takes precedence
- the plugin copy is ignored
- the shell logs a warning and shows a warning dialog

## Plugin Package Format

The current parser in `egps2.plugin.manager.PluginProperty` effectively uses these fields:

- **Required**: `launchClass`
- **Optional and effective**: `dependentJars`

Other metadata fields may exist in plugin property files, but the current parser does not treat them as core loading requirements.

Example:

```properties
launchClass=com.example.myplugin.MyPluginLoader
dependentJars=libA.jar;libB.jar
```

## Supported Plugin Implementation Styles

Current plugin modules can be authored in two common ways:

### 1. Implement `IModuleLoader` directly

Use this when the plugin needs explicit control over loader behavior and panel construction.

### 2. Extend `FastBaseTemplate`

Use this for simpler modules where the plugin can reuse the template base and behave as both loader and panel.

Important current rule:

- `FastBaseTemplate` subclasses inside plugin JARs can be treated as real plugin modules
- template classes compiled into the shell/classpath are treated differently and are not automatically exposed as normal plugin modules

## Practical Summary

For the current `egps-shell` implementation, plugin integration should be described like this:

- Plugins can be installed as JARs under `~/.egps2/config/plugin/`
- The shell has both a direct Plugins-menu path and a discovery/config-merge path
- Discovery scans classpath modules and plugin JARs
- Being discovered does not automatically make a module active in the gallery
- The current gallery respects the config-backed active loader set
- The module management table can still show broader tracked status information

## Related Documents

- `plugin_gallery_integration_statement.md` - current relationship between plugin discovery, config-backed loading, and Module Gallery
- `plugin_types_statement.md` - current supported plugin implementation styles and boundaries
- `module_discovery_exclusion_rules.md` - current discovery filtering rules
