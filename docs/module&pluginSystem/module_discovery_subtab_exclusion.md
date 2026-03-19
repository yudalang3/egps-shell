# SubTab Exclusion in Module Discovery

This document explains the current rule that excludes VOICE SubTab classes from ordinary module discovery in `egps-shell`.

## Current Rule

Classes extending `DockableTabModuleFaceOfVoice` are not treated as standalone modules by `ModuleDiscoveryService`.

Current reasoning:

- a SubTab is part of a parent dockable VOICE module
- it depends on the parent module's surrounding container and lifecycle
- it should not be instantiated as an independent module loader

## Why This Rule Exists

The discovery system is meant to identify loadable shell modules.

SubTabs do not fit that role:

- they are child views inside a larger VOICE module
- they are not meant to appear as separate entries in module-management views
- they should not be auto-tracked as if they were ordinary top-level modules

## Current Implementation Basis

The current exclusion is implemented in `ModuleDiscoveryService` through an `isSubTabClass(...)` check.

That check:

- loads `egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice`
- detects whether a discovered class extends it
- excludes matching subclasses from the ordinary module list

## Example Shape

Typical structure:

```text
Parent module loader
  -> creates a dockable VOICE face
     -> owns one or more SubTab classes
```

In this structure, the parent module is the module-discovery target. The SubTabs are internal parts of that module.

## Relation to Other Exclusion Rules

This rule is one part of the broader discovery filter set.

See:

- `module_discovery_exclusion_rules.md`
- `../../manuals/01_VOICE_architecture.md`
