# Module Discovery Exclusion Rules

This document explains the current exclusion rules used by `ModuleDiscoveryService` when scanning for `IModuleLoader` implementations in `egps-shell`.

## Purpose

The discovery service scans broadly:

- the JVM classpath
- dependency JARs already present in the runtime
- plugin JARs under `~/.egps2/config/plugin/`

Not every discovered `IModuleLoader` implementation should be treated as an ordinary shell module. The exclusion rules define that boundary.

## Current Exclusion Rules

### 1. Interfaces and abstract classes

These are excluded because they are not directly instantiable module entries.

### 2. VOICE SubTab classes

Classes extending `DockableTabModuleFaceOfVoice` are excluded because they are child views inside a parent VOICE module, not standalone modules.

### 3. Template base classes

The following are excluded as template infrastructure:

- `egps2.plugin.fastmodtem.FastBaseTemplate`
- `egps2.plugin.fastmodtem.IndependentModuleLoader`

### 4. Template subclasses compiled into the shell source/classpath

If a class extends `FastBaseTemplate` but comes from the shell source/classpath rather than a plugin JAR, it is treated as template-side infrastructure and excluded from ordinary discovery.

### 5. Template subclasses packaged in plugin JARs

These are included, because they represent real plugin modules rather than template scaffolding.

## Important Clarification

The current `ModuleDiscoveryService` implementation does not use a separate “core module exclusion” rule in its final filtering method.

So this document should not describe current behavior as if built-in core modules are filtered by a dedicated exclusion branch there.

## Duplicate Handling

If the same module class name exists in both:

- the classpath
- a plugin JAR

the classpath copy takes precedence, and the plugin copy is ignored with a warning.

This is duplicate resolution, not an exclusion rule in the ordinary filter chain.

## Related Documents

- `module_discovery_subtab_exclusion.md`
- `plugin_integration.md`
- `plugin_types_statement.md`
- `../../manuals/01_VOICE_architecture.md`
