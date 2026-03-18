# eGPS Built-in Module Development Tutorial (Built-in Mode)

## What is Built-in mode?

Built-in mode packages a module as a JAR and places it in `dependency-egps/` so it ships with the application as part of the built-in tool set.

## Relation to Plugin mode

- the same module code can often be reused for both deployment styles
- the difference is not only path location; runtime path, loader behavior, and presentation also differ
- many JARs can be moved between the two locations, but activation still depends on current merge and loader behavior

Plugin mode:

- location: `~/.egps2/config/plugin/`

Built-in mode:

- location: `dependency-egps/`

## Core idea

At the code level, Built-in mode and Plugin mode are close. But they should not be described as runtime-identical.

### Shared points

- both can implement `IModuleLoader` or extend `FastBaseTemplate`
- JAR structure can often stay the same
- compilation and packaging are usually very similar
- both may be tracked by discovery mechanisms

### Important distinctions

- Plugin mode uses `launchClass` from `eGPS2.plugin.properties` and may use `dependentJars`
- Built-in mode as a classpath module does not rely on `eGPS2.plugin.properties` as its primary loading requirement
- discovered does not mean auto-activated
- Module Gallery follows the current active loader set

## Development flow

### Method 1: develop as Plugin first, then convert to Built-in

This is often the most practical route:

1. develop and test in Plugin mode
2. once stable, move the JAR into `dependency-egps/`
3. restart and verify built-in behavior

### Method 2: develop directly as Built-in

This is appropriate when the module is clearly part of the shipped default tool set from the start.

## Advantages of Built-in modules

- no separate plugin installation step for the user
- natural inclusion in the default application bundle
- simpler deployment story for core tools

The performance difference between Built-in and Plugin mode is usually not a meaningful user-facing reason by itself.

## Converting between Plugin and Built-in

In many cases the same JAR can be moved:

- Plugin -> Built-in: move from `~/.egps2/config/plugin/` to `dependency-egps/`
- Built-in -> Plugin: move in the opposite direction

But after moving, verify:

- runtime loading path
- active/inactive state
- menu presentation
- duplicate-module handling

## Decision guide

Choose Built-in mode if:

- the module belongs to the shipped default tool set
- the application should include it by default
- deployment should be centralized with the shell release

Choose Plugin mode if:

- it should be installed independently
- it is experimental or externally distributed
- the user should be able to manage it separately

Support both if:

- the same implementation is useful in multiple distribution models

## FAQ

### Does a Built-in module need special code?

Usually no. The main differences are packaging, placement, and runtime path, not a completely separate module API.

### Can the same module exist in both places?

That is not recommended. Duplicate classes can trigger duplicate-resolution behavior, and the classpath copy currently wins.

### Can Built-in modules be updated?

Yes, but the update path is different from swapping a plugin JAR in the user plugin directory.

### Is there a big performance difference?

Usually no meaningful end-user difference.

## Summary

1. the same module code can often support both deployment modes
2. runtime behavior is not completely identical
3. deployment location affects how the shell treats the module
4. Plugin-first development is often the most practical workflow
