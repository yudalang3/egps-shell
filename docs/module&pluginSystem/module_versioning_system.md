# Module Versioning System

This document describes the current module-version contract used by `egps-shell`.

## Current Contract

All module loaders implement `IModuleLoader.getVersion()` and return a `ModuleVersion` value.

The current version model uses semantic version triplets:

- `major`
- `minor`
- `patch`

The supporting type is `egps2.modulei.ModuleVersion`.

## Current Core API

### `ModuleVersion`

`ModuleVersion` currently provides:

- construction from `major.minor.patch`
- parsing from string
- comparison
- compatibility checks based on major version
- `toString()` formatting

### `IModuleLoader.getVersion()`

The current interface requires every module loader to provide a version.

Typical usage:

- built-in shell modules often return `EGPSProperties.MAINFRAME_CORE_VERSION`
- custom modules and plugins can return their own `new ModuleVersion(...)`

## Current Shared Core Version

The current codebase defines a shared core constant:

- `EGPSProperties.MAINFRAME_CORE_VERSION`

At the time of the current source snapshot, that constant is `0.0.1`.

This is the value used by several built-in modules and templates unless they supply a more specific version.

## What the Current System Does

The version contract currently supports:

- showing module version information in UI or diagnostics
- comparing versions in code
- exposing a stable version field for built-in modules and plugins

## Current Boundary

The current system does not by itself provide:

- automatic update delivery
- dependency resolution between modules
- remote compatibility negotiation

Those behaviors should not be described as current runtime features unless they are implemented separately in the future.

## Implementation Basis

The current behavior is defined mainly by:

- `src/egps2/modulei/IModuleLoader.java`
- `src/egps2/modulei/ModuleVersion.java`
- `src/egps2/EGPSProperties.java`
