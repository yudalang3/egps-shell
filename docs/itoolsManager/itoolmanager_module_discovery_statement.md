# Current ITools Manager Discovery and Status Model

This document explains the module-discovery and status-management mechanism currently used by ITools Manager in `egps-shell`.

## 1. Current Goal

The purpose of the current mechanism is not “scan every class and dump it into the UI”.

Its actual goals are:

- scan currently available modules
- detect mismatches between the config file and the real runtime modules
- record the current status of each known module
- let the management UI show a broader known-module view
- keep the user config file as the source of truth for current activation

## 2. Current Discovery Sources

Current discovery mainly comes from two sources:

- `IModuleLoader` implementations on the JVM classpath
- `IModuleLoader` implementations inside plugin JARs under `~/.egps2/config/plugin/`

Implementation basis:

- `ModuleDiscoveryService` performs scanning
- `EGPS2ServiceLoader.loadWithDiscovery(...)` merges scan results with the config file

## 3. Current Status Model

The current system uses module status values to describe the different states of known modules instead of a simple loaded/unloaded split.

Common statuses include:

- `AVAILABLE`
- `AVAILABLE_NOT_LOADED`
- `NEWLY_DISCOVERED`
- `UNAVAILABLE`
- `DEPRECATED`

These statuses distinguish between:

- modules present in both config and scan results
- newly discovered modules that were not yet listed in config
- modules still recorded in config but no longer loadable at runtime

## 4. Current Merge Rules

The current merge rules should be understood like this:

- the config file still decides which modules are currently active
- scan results extend the system's knowledge of which modules currently exist
- newly discovered modules are not auto-activated by default
- modules that remain in config but are currently unloadable are preserved as status information rather than silently discarded

As a result, ITools Manager shows a broader known-module view, not a mechanical echo of the raw config file.

## 5. Current UI Relationship

The current related UI responsibilities are:

- `MainFrameProperties.getExistedLoaders()`: returns the currently active module set
- `MainFrameProperties.getAllProviders()`: returns the broader provider-status set
- `ElegantJTable`: shows module status and configuration-related information
- `DemoButtonsOrganizer` / Module Gallery: focus more on currently active modules

This means the module-management UI and the Gallery do not have exactly the same visible scope, and that difference is part of the current design.

## 6. Current Exclusion Rules

The current discovery mechanism does not treat every `IModuleLoader` implementation as an independent module.

Known exclusions include:

- interfaces and abstract classes
- SubTab classes
- template base classes
- helper implementations that should not be shown as standalone modules

Those rules are described in more detail by the plugin-system documents.

## 7. Current Boundary

The current mechanism is responsible for discovery and status modeling.

It is not:

- an “auto-enable every new module” feature
- a replacement for the direct loading logic of the Plugins menu
- a promise that every discovered result will immediately appear in the Gallery as active

## 8. Related Documents

- `../module&pluginSystem/plugin_integration.md`
- `../module&pluginSystem/module_discovery_exclusion_rules.md`
- `../module&pluginSystem/module_discovery_subtab_exclusion.md`
