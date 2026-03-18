# `eGPS2.plugin.properties` Configuration Guide

## Overview

`eGPS2.plugin.properties` is the current entry configuration file for plugin JARs. Its primary role is to declare the plugin entry class and related dependency information.

Location:

- it must be placed at the root of the JAR

Format:

- `key=value`

Encoding:

- UTF-8

---

## What the current implementation actually uses

Under the current loader implementation, the fields that truly affect plugin loading are:

- `launchClass` (required)
- `dependentJars` (optional)

Other fields can still be included as helpful metadata, but `PluginProperty` / `PluginOperation` do not depend on them for core loading.

## 1. `launchClass` (required)

This is the fully qualified name of the plugin entry class.

The class must:

- implement `IModuleLoader` or extend `FastBaseTemplate`
- have a public no-argument constructor
- exist inside the JAR

## 2. `pluginName`

This can be used as supplemental metadata for human readability, but the current loader does not depend on it.

## 3. `version`

This can be used as supplemental metadata for version tracking, but the current loader does not depend on it.

## 4. `author`

Supplemental metadata only.

## 5. `description`

Supplemental metadata only.

## 6. `dependentJars` (optional)

This field lists sibling JAR dependencies separated by semicolons.

Example:

```properties
dependentJars=libA.jar;libB.jar;libC.jar
```

These JARs are expected to live alongside the main plugin JAR in the plugin directory.

---

## Example configurations

### Minimal configuration

```properties
launchClass=com.example.SimplePlugin
```

### Recommended practical configuration

```properties
launchClass=com.example.TextProcessorPlugin
pluginName=Text Processor
version=1.0.0
author=Jane Doe
description=Text processing utility
```

### Configuration with dependencies

```properties
launchClass=com.example.DataPlugin
pluginName=Data Plugin
dependentJars=commons-lang3-3.12.0.jar;guava-31.1.jar
```

---

## Common mistakes

### Empty or invalid `launchClass`

The plugin cannot load without a valid `launchClass`.

### Old property format

Old formats such as `location.IModuleLoader.class:...` are obsolete in the current implementation.

### Incomplete class name

Use the full package name, not only the simple class name.

### Wrong `dependentJars` delimiter

Use semicolons, not commas.

### Encoding problems

Keep the file in UTF-8.

---

## Validation ideas

Manual checks:

1. confirm that `eGPS2.plugin.properties` exists at the JAR root
2. inspect the file contents
3. confirm the target entry class is present in the JAR

---

## Best practices

1. get the effective fields correct first: `launchClass`, then `dependentJars` if needed
2. use extra metadata to improve maintainability, not as a substitute for real runtime validation
3. keep dependency naming explicit
4. keep the file readable and annotated if the plugin is maintained by a team

---

## Summary

- effective required field: `launchClass`
- effective optional field: `dependentJars`
- optional metadata: `pluginName`, `version`, `author`, `description`
- old format is obsolete
