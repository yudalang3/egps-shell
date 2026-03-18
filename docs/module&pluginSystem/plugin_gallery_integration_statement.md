# Current Plugin and Module Gallery Integration

This document describes the current end-state relationship between plugin discovery, config-backed module loading, and the Module Gallery in `egps-shell`.

For the broader plugin mechanism, see `plugin_integration.md`.

## Current Result

The shell currently combines:

- plugin JAR loading from `~/.egps2/config/plugin/`
- classpath-based module discovery
- config-backed active module selection
- gallery presentation based on the active loader set

This means plugin modules are part of the shell runtime, but they are still governed by the same active/inactive decisions used for other modules.

## What the Gallery Actually Shows

The current Module Gallery does not simply show every class that discovery can find.

Current behavior:

- `ModuleDiscoveryService` can discover plugin modules
- `EGPS2ServiceLoader.loadWithDiscovery(...)` merges discovery results with `~/.egps2/config/egps2.loading.module.config.txt`
- `MainFrameProperties.getExistedLoaders()` returns the currently active loader set
- `DemoButtonsOrganizer` and `ModuleInspector` use that active loader set for user-facing module presentation

So the gallery follows the configured active modules, not the raw discovery result.

## How Plugins Remain Visible

Even though the gallery uses the active loader set, plugin-related information is still visible in the current shell:

- the `Plugins` menu provides a direct loading path
- module management views can expose broader tracked module status
- gallery and inspector views can add a `[Plug]` style badge when an active module comes from the plugin directory

This is a presentation detail on top of the current loading model. It is not a separate activation system.

## Important Boundaries

The current implementation should be described with these constraints:

- discovered plugin modules are not auto-activated by default
- duplicate module classes found in both classpath and plugin JARs are resolved in favor of the classpath copy
- template base classes are not treated as ordinary active modules
- plugin discovery and plugin menu loading are related but not identical paths

## Reader Guidance

Use the documents in this order:

- `plugin_integration.md`: full reference for plugin loading and discovery behavior
- this document: concise explanation of the current gallery-facing outcome
- `plugin_types_statement.md`: current supported plugin implementation styles
