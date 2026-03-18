# eGPS Module System Architecture

## Overview

This document explains the architecture of the eGPS module system, its runtime model, and the technical relationship between Plugin mode and Built-in mode.

## Core idea

The key architectural decision is a unified module contract:

- all modules follow the same module interface
- deployment mode changes how they enter the shell, not the basic module identity itself

At the center is `IModuleLoader`.

## Architecture components

### 1. Core interface: `IModuleLoader`

This defines the basic contract for a module loader.

### 2. Base class: `ModuleFace`

This is the main UI base for many GUI modules.

### 3. Fast template: `FastBaseTemplate`

This supports simpler plugin/module development where the template can carry more of the shell behavior.

## Module discovery

### `ModuleDiscoveryService`

The discovery layer is responsible for scanning modules from:

- the runtime classpath
- plugin JARs under `~/.egps2/config/plugin/`

It also applies exclusion rules and duplicate handling.

### Current flow

1. scan runtime sources
2. filter out non-module or infrastructure classes
3. merge results with the loading config
4. expose active loaders separately from the broader known-module set

## Two deployment modes in technical terms

### Plugin mode

- external JARs under `~/.egps2/config/plugin/`
- direct plugin-menu loading path exists
- plugin entry depends on `launchClass`
- custom class-loading behavior is involved

### Built-in mode

- JARs available through the classpath, commonly under `dependency-egps/`
- treated as classpath modules
- does not rely on plugin-entry parsing in the same way as Plugin mode

## Comparison

### Technically

- shared module contract
- different runtime path
- different loader role
- different presentation details

### In discovery terms

- both may be discovered
- discovery does not equal activation
- Gallery follows the active loader set

## Important design decisions

### Why a unified interface?

Because the shell needs one clear way to reason about modules, even if deployment differs.

### Why `eGPS2.plugin.properties`?

Because plugin JARs need a compact way to declare entry information for plugin-mode loading.

### Why does `FastBaseTemplate` behave differently in JARs and classpath source?

Because plugin-side template subclasses can be real plugin modules, while shell-side template classes are part of the framework scaffolding.

## Loading and instantiation flow

The complete runtime path includes:

1. discovery
2. config merge
3. active-loader selection
4. module instantiation
5. shell presentation

## Extension points

Possible advanced extension points include:

- custom discovery logic
- custom discovery filtering
- module lifecycle hooks

## Best practices

- keep module boundaries clear
- avoid mixing shell concerns into business logic
- treat plugin metadata and runtime loading as separate concerns
- verify duplicate and activation behavior explicitly

## Summary

The architecture works because it separates:

- module contract
- discovery
- activation
- presentation
- deployment mode

That separation is the main reason the shell can grow without turning everything back into ad hoc host edits.
