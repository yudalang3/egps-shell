# Module Versioning System

**Version:** 2.2
**Date:** 2024-12-09
**Status:** ✅ Implemented

## Overview

eGPS 2.2 introduces a mandatory module versioning system based on Semantic Versioning 2.0.0. All modules (built-in and plugins) must now implement the `getVersion()` method to provide version information.

## Motivation

1. **Version Tracking**: Know exactly which version of each module is loaded
2. **Debugging Support**: Users can report module versions when filing bug reports
3. **Dependency Management**: Future support for version compatibility checks
4. **Update Notification**: Compare local vs. server versions
5. **Professionalism**: Industry-standard practice for modular systems

## Architecture

### ModuleVersion Class

Location: `src/egps2/modulei/ModuleVersion.java`

```java
public class ModuleVersion implements Comparable<ModuleVersion>, Serializable {
    private final int major;
    private final int minor;
    private final int patch;

    // Constructors
    public ModuleVersion(int major, int minor, int patch)

    // Parsing
    public static ModuleVersion parse(String versionString)

    // Comparison
    public boolean isNewerThan(ModuleVersion other)
    public boolean isOlderThan(ModuleVersion other)
    public boolean isCompatibleWith(ModuleVersion other) // Same major version

    // String representation
    public String toString() // "2.1.0"
}
```

### IModuleLoader Interface Update

All modules **must** implement the new `getVersion()` method:

```java
public interface IModuleLoader extends IHelp, ICategory, IModuleSignature {

    /**
     * Get the version of this module.
     * All modules MUST implement this method.
     *
     * @return ModuleVersion object representing this module's version
     */
    ModuleVersion getVersion();

    // ... other methods
}
```

### Mainframe Core Version

Location: `src/egps2/EGPSProperties.java`

```java
public class EGPSProperties {
    /**
     * Mainframe core module version (shared by all built-in core modules).
     * Used by File Manager, Module Gallery, Text Editors, Demo modules, etc.
     */
    public static final ModuleVersion MAINFRAME_CORE_VERSION =
        new ModuleVersion(2, 2, 0);
}
```

## Implementation Guide

### For Mainframe Core Modules

Core modules (File Manager, iTools Manager, Text Editors, Demo modules) should use the shared version:

```java
package egps2.builtin.modules.filemanager;

import egps2.EGPSProperties;
import egps2.modulei.ModuleVersion;

public class IndependentModuleLoader implements IModuleLoader {

    @Override
    public ModuleVersion getVersion() {
        return EGPSProperties.MAINFRAME_CORE_VERSION;
    }

    // ... other methods
}
```

### For Custom/Plugin Modules

Custom modules should define their own version:

```java
package com.example.myplugin;

import egps2.modulei.ModuleVersion;

public class IndependentModuleLoader implements IModuleLoader {

    @Override
    public ModuleVersion getVersion() {
        return new ModuleVersion(1, 0, 0);
    }

    // ... other methods
}
```

## Semantic Versioning Rules

Follow [Semantic Versioning 2.0.0](https://semver.org/):

### Version Format: `MAJOR.MINOR.PATCH`

- **MAJOR**: Incompatible API changes
  - Example: Changing method signatures, removing public methods
  - `1.5.3` → `2.0.0`

- **MINOR**: Add functionality in a backward compatible manner
  - Example: Adding new methods, new features
  - `1.5.3` → `1.6.0`

- **PATCH**: Backward compatible bug fixes
  - Example: Fixing bugs without API changes
  - `1.5.3` → `1.5.4`

### Examples

```java
// Initial release
new ModuleVersion(1, 0, 0)

// Added new feature (backward compatible)
new ModuleVersion(1, 1, 0)

// Bug fix
new ModuleVersion(1, 1, 1)

// Breaking API change
new ModuleVersion(2, 0, 0)
```

## Version Comparison

```java
ModuleVersion v1 = new ModuleVersion(1, 5, 3);
ModuleVersion v2 = new ModuleVersion(2, 0, 0);

// Comparison
v2.isNewerThan(v1);      // true
v1.isOlderThan(v2);      // true
v1.compareTo(v2);        // negative (v1 < v2)

// Compatibility check (same major version)
ModuleVersion v3 = new ModuleVersion(1, 6, 0);
v1.isCompatibleWith(v3); // true (both major=1)
v1.isCompatibleWith(v2); // false (different major)
```

## Module Discovery Integration

The `ModuleDiscoveryService` automatically logs module versions:

```
[DEBUG] Successfully loaded module: egps2.builtin.modules.filemanager.IndependentModuleLoader v2.2.0
[DEBUG] Successfully loaded module: demo.dockable.IndependentModuleLoader v2.2.0
[DEBUG] Successfully loaded module: com.example.myplugin.IndependentModuleLoader v1.0.0
```

## Migration Checklist

For existing modules being updated to 2.2:

- [ ] Add `import egps2.modulei.ModuleVersion;`
- [ ] Add `import egps2.EGPSProperties;` (for core modules only)
- [ ] Implement `getVersion()` method
  - Core modules: return `EGPSProperties.MAINFRAME_CORE_VERSION`
  - Custom modules: return `new ModuleVersion(major, minor, patch)`
- [ ] Compile and test
- [ ] Update module documentation with version number

## Future Enhancements

### Planned Features

1. **Module Gallery Display**
   - Show version column in module list
   - Highlight outdated modules

2. **Dependency Checking**
   - Specify required module versions
   - Warn about incompatible versions

3. **Update Notifications**
   - Check server for newer versions
   - Prompt user to update

4. **Version Diagnostics**
   - Display all loaded modules and versions in About dialog
   - Export version report for bug reports

### Example: Dependency Declaration (Future)

```java
@Override
public ModuleDependency[] getDependencies() {
    return new ModuleDependency[] {
        new ModuleDependency("egps2.builtin.modules.filemanager.IndependentModuleLoader",
                             "2.0.0", "3.0.0"), // Requires 2.x series
    };
}
```

## FAQ

### Q: What version should I use for a brand new module?
**A:** Start with `1.0.0` for the initial release.

### Q: When should I increment the major version?
**A:** When you make incompatible API changes that would break existing code using your module.

### Q: Can I use version `0.x.x` for experimental modules?
**A:** Yes, version `0.x.x` indicates initial development. Breaking changes can happen at any time.

### Q: Do I need to update the version for internal code refactoring?
**A:** Only if the refactoring affects the public API. Internal changes without API impact should increment PATCH.

### Q: How do I handle multiple release branches?
**A:** Use different major versions:
- `1.x.x` - Stable branch
- `2.x.x` - Next major release
- `3.x.x` - Future development

## References

- [Semantic Versioning 2.0.0](https://semver.org/)
- `ModuleVersion` JavaDoc: `src/egps2/modulei/ModuleVersion.java`
- `IModuleLoader` JavaDoc: `src/egps2/modulei/IModuleLoader.java`
- `EGPSProperties` JavaDoc: `src/egps2/EGPSProperties.java`

## See Also

- [Module Discovery System](./itoolmanager_module_discovery_design.md)
- [Plugin Integration Guide](PLUGIN_INTEGRATION.md)
- [Developer Guide](../global_preference/developer_guide.md)
