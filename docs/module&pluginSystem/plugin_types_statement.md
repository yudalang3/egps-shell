# Current Plugin Implementation Types

This document explains the plugin implementation styles currently supported by `egps-shell`, and how those styles are actually integrated by the current `egps-main` implementation.

## 1. Two Supported Plugin Styles

### 1.1 Implement `IModuleLoader` directly

This is the most explicit plugin style.

Typical use cases:

- the plugin wants full control over the relationship between loader and panel
- the plugin wants a clearer split between loader, panel, and helper classes
- the plugin needs more complex initialization logic

Current expectations:

- the entry class implements `IModuleLoader`
- the entry class can be instantiated by `PluginOperation` or `ModuleDiscoveryService`
- the plugin JAR includes `eGPS2.plugin.properties` at its root

### 1.2 Extend `FastBaseTemplate` inside a plugin JAR

This is the simplified style currently supported by the shell.

Typical use cases:

- the plugin structure is simple
- the plugin wants to reuse the template-provided interaction skeleton
- one class handling both loader and face responsibilities is sufficient

Current rules:

- `FastBaseTemplate` subclasses inside plugin JARs can be treated as real plugin modules
- template classes or template examples compiled into the shell source/classpath are not exposed as ordinary plugin modules
- `FastBaseTemplate` itself is not a plugin entry class

## 2. Current Plugin Entry File

The current plugin JAR format requires `eGPS2.plugin.properties` at the JAR root.

The fields that are actually effective in the current implementation are:

```properties
launchClass=com.example.MyPluginLoader
dependentJars=libA.jar;libB.jar
```

Field meaning:

- `launchClass`: the fully qualified plugin entry class; currently required
- `dependentJars`: optional sibling JAR dependencies loaded from the same directory

This document does not treat other metadata keys as part of the formal loading contract, because the current `PluginProperty` implementation does not parse them.

## 3. Two Integration Paths

Current plugin modules can enter the shell through two related paths.

### 3.1 Direct loading from the Plugins menu

Handled by `PluginOperation`:

- scans plugin JARs under `~/.egps2/config/plugin/`
- reads `eGPS2.plugin.properties` from each JAR
- creates the module loader using `launchClass`
- exposes a direct entry under the `Plugins` menu

### 3.2 Module discovery plus config merge

Handled by `ModuleDiscoveryService` together with `EGPS2ServiceLoader.loadWithDiscovery(...)`:

- scans modules on the runtime classpath
- scans plugin JARs under `~/.egps2/config/plugin/`
- filters out interfaces, abstract classes, SubTabs, template base classes, and similar non-module entries
- merges the scan result with `~/.egps2/config/egps2.loading.module.config.txt`

## 4. Current Boundaries

This topic should be read with the following boundaries in mind:

- “discoverable” does not mean “auto-activated by default”
- “located in the plugin directory” does not mean “automatically shown in the currently active Gallery list”
- template base classes are not valid plugin entry classes
- if a plugin JAR contains a module class that duplicates a classpath module, the classpath copy wins

## 5. Which Style to Choose

Recommended choices:

- simple plugin: prefer a plugin JAR that extends `FastBaseTemplate`
- complex plugin: implement `IModuleLoader` directly

If the plugin needs explicit separation of loader, panel, dependency handling, or lifecycle control, implementing `IModuleLoader` directly is the safer choice.

## 6. Related Documents

- `plugin_integration.md`: full reference for the current plugin-integration mechanism
- `plugin_gallery_integration_statement.md`: relationship between plugins and Module Gallery
- `module_discovery_exclusion_rules.md`: discovery filtering rules
