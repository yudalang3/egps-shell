# Plugin Integration with Module Gallery (v2.1+)

## Overview

Starting with eGPS v2.1, the plugin system has been fully integrated with the Module Gallery. **Plugins and built-in modules now have equal status** and are both automatically discovered and displayed in the Module Gallery interface.

## What Changed

### Before v2.1
- Plugins were only accessible through the "Plugins" menu
- Module Gallery only showed built-in modules
- Plugins and built-in modules were treated differently

### After v2.1
- **Unified Discovery**: `ModuleDiscoveryService` now scans both:
  - Built-in modules from classpath (packages: `egps2`, `demo`, `module`, `operator`, `primary`)
  - External plugin JARs from `~/.egps2/config/plugin` directory
- **Module Gallery Integration**: All discovered modules appear in Module Gallery
- **Equal Status**: Plugins can be accessed from both menu bar and Module Gallery
- **Automatic Detection**: New plugins are automatically discovered without manual configuration

## Architecture

### Module Discovery Flow

```
┌─────────────────────────────────────────────────────────┐
│          ModuleDiscoveryService.scanAllModuleClasses()  │
└────────────────┬────────────────────────────────────────┘
                 │
        ┌────────┴────────┐
        │                 │
        ▼                 ▼
┌───────────────┐  ┌──────────────────┐
│  Scan Built-in│  │  Scan Plugin JARs │
│  from Classpath│  │ from ~/.egps2/   │
│               │  │   config/plugin   │
└───────┬───────┘  └────────┬─────────┘
        │                   │
        └────────┬──────────┘
                 │
                 ▼
       ┌─────────────────────┐
       │  Unified Module List │
       │  (Built-in + Plugins)│
       └──────────┬───────────┘
                  │
         ┌────────┴────────┐
         │                 │
         ▼                 ▼
  ┌─────────────┐   ┌─────────────┐
  │ Menu Bar    │   │  Module     │
  │ (Plugins    │   │  Gallery    │
  │  Menu)      │   │  (Ctrl+2)   │
  └─────────────┘   └─────────────┘
```

### Key Components

#### 1. ModuleDiscoveryService
**Location**: `src/egps2/frame/features/ModuleDiscoveryService.java`

**Enhancements**:
- `scanAllModuleClasses()` - Scans both classpath and plugin JARs
- `getPluginJarUrls()` - Discovers plugin JAR files
- `loadModuleInstance()` - Loads modules from classpath or plugins
- `loadModuleFromPlugins()` - Handles plugin-specific class loading

#### 2. Module Gallery (IntroMain)
**Location**: `src/egps2/builtin/modules/gallerymod/`

**Integration**:
- `DemoButtonsOrganizer` uses `MainFrameProperties.getExistedLoaders()`
- `getExistedLoaders()` uses `ModuleDiscoveryService` for scanning
- All discovered modules (built-in + plugins) appear in the gallery

#### 3. Plugin Loading
**Location**: `src/egps2/plugin/manager/`

**Components**:
- `CustomURLClassLoader` - Plugin class isolation
- `PluginOperation` - Menu integration for plugins
- `JarFileUtil` - JAR file reading utilities

## Plugin Development

### Two Approaches to Plugin Development

#### Approach 1: Implement IModuleLoader Directly

Best for complex plugins with custom requirements:

```java
package com.example.myplugin;

import egps2.modulei.IModuleLoader;
import egps2.frame.ModuleFace;

public class MyPluginLoader implements IModuleLoader {
    @Override
    public String getTabName() {
        return "My Plugin";
    }

    @Override
    public String getShortDescription() {
        return "Description of my plugin";
    }

    @Override
    public ModuleFace getFace() {
        return new MyPluginPanel(this);
    }

    @Override
    public int[] getCategory() {
        // Define module category
        return new int[]{0, 1, 2, 3};
    }
}
```

#### Approach 2: Extend FastBaseTemplate (Recommended for Simple Plugins)

Best for simple plugins that need basic functionality:

```java
package com.example.myplugin;

import egps2.plugin.fastmodtem.FastBaseTemplate;
import javax.swing.*;
import java.awt.*;

/**
 * IMPORTANT: This class will be discovered as a module when:
 * ✅ Packaged in a plugin JAR (e.g., ~/.egps2/config/plugin/myplugin.jar)
 *
 * This class will be excluded (treated as template) when:
 * ❌ Compiled into the shell/classpath (main application)
 */
public class MySimplePlugin extends FastBaseTemplate {

    public MySimplePlugin() {
        super();
        setLayout(new BorderLayout());

        JLabel label = new JLabel("My Simple Plugin", JLabel.CENTER);
        add(label, BorderLayout.CENTER);
    }

    @Override
    public String getTabName() {
        return "My Simple Plugin";
    }

    @Override
    public String getShortDescription() {
        return "A simple plugin using FastBaseTemplate";
    }

    @Override
    public int[] getCategory() {
        return new int[]{0, 0, 0, 0}; // Utility, General, Simple, No Deps
    }
}
```

**Key Difference with FastBaseTemplate**:
- `FastBaseTemplate` extends `ModuleFace` and implements `IModuleLoader`
- Provides default implementations for common methods
- Your plugin class IS both the loader and the panel
- Reduces boilerplate code for simple plugins

**Discovery Rule**:
- ✅ **In plugin JAR**: Discovered and shown in Module Gallery
- ❌ **In shell/classpath**: Excluded (treated as template)

This allows `FastBaseTemplate` to be used as a template in the shell while enabling plugin developers to extend it for actual plugin modules.

### Creating a Plugin

**Step 1: Create Plugin Properties File** (`eGPS2.plugin.properties` in JAR root):
```properties
launchClass=com.example.myplugin.MyPluginLoader
pluginName=My Plugin
version=1.0.0
author=Your Name
```

**Step 2: Package as JAR**:
```bash
jar cvf myplugin.jar -C bin/ .
```

**Step 3: Install Plugin**:
```bash
cp myplugin.jar ~/.egps2/config/plugin/
```

**Step 4: Restart eGPS** - Your plugin will automatically appear in:
- Menu Bar → Plugins menu
- Module Gallery (Ctrl+2)

## Entry Points

Users can access all modules (built-in and plugins) through:

1. **Menu Bar** (Top menu)
   - File → Tools → (Built-in modules organized by category)
   - File → Plugins → (External plugin modules)

2. **Module Gallery** (Recommended)
   - Press `Ctrl+2` or select "Module gallery" from menu
   - Browse modules by category
   - Both built-in and plugins displayed together
   - Visual interface with icons and descriptions

## Testing

### Test Plugin Discovery

```bash
# Compile test
javac -d ./out/production/egps-main.gui \
      -cp "dependency-egps/*:./out/production/egps-main.gui" \
      src/test/egps2/frame/features/IntegratedModuleDiscoveryTest.java

# Run test
java -cp "./out/production/egps-main.gui:dependency-egps/*" \
     egps2.frame.features.IntegratedModuleDiscoveryTest
```

### Expected Output
```
INTEGRATED MODULE DISCOVERY TEST (Built-in + Plugins)
================================================================================

TEST 1: Scanning for all IModuleLoader implementations
--------------------------------------------------------------------------------
Total modules discovered: X

Discovered modules:
--------------------------------------------------------------------------------
[BUILTIN]    egps2.builtin.modules.filemanager.IndependentModuleLoader
[BUILTIN]    egps2.builtin.modules.gallerymod.IndependentModuleLoader
[PLUGIN]     com.example.myplugin.MyPluginLoader
...

Summary:
  Built-in modules: Y
  Plugin modules:   Z
  Total:            X
```

## Benefits

### For Users
- ✅ Single unified interface (Module Gallery) for all modules
- ✅ Consistent experience for built-in and plugin modules
- ✅ Visual browsing with icons and descriptions
- ✅ Automatic plugin discovery (no manual configuration)

### For Plugin Developers
- ✅ Plugins treated as first-class modules
- ✅ Automatic visibility in Module Gallery
- ✅ Same API as built-in modules (IModuleLoader)
- ✅ Class isolation through CustomURLClassLoader

### For eGPS Platform
- ✅ Extensible architecture
- ✅ Clean separation of concerns
- ✅ No code duplication
- ✅ Future-proof design

## Technical Details

### Class Loading Strategy

**Built-in Modules**:
- Loaded via standard Java ClassLoader
- Classes available on application classpath

**Plugin Modules**:
- Loaded via `CustomURLClassLoader`
- Class isolation (plugins can use different library versions)
- Parent delegation model reversed (plugin classes loaded first)
- ClassLoader kept alive to maintain plugin classes in memory

### Exclusion Rules

The discovery system excludes:
- Abstract classes and interfaces
- SubTab classes (extending `DockableTabModuleFaceOfVoice`)
- Template base classes with special handling:
  - `FastBaseTemplate` itself: **Always excluded**
  - `FastBaseTemplate` subclasses:
    - **If from plugin JAR**: ✅ **Included** (actual plugin module)
    - **If from shell/classpath**: ❌ **Excluded** (template in shell)
  - `egps2.plugin.fastmodtem.IndependentModuleLoader`: **Always excluded**

**Why this matters**: `FastBaseTemplate` is designed as a template base class. In the shell, it and its subclasses serve as templates. But when a developer extends `FastBaseTemplate` in a plugin JAR, that subclass IS the actual plugin module and should be discovered.

### Performance Considerations

- Discovery scans run once at startup
- Results cached in `MainFrameProperties`
- Plugin JARs remain loaded (memory tradeoff for functionality)
- Reflections library used for efficient classpath scanning

## Migration Guide

### For Existing Plugin Developers

Your existing plugins will work without changes! The integration is backward compatible:

1. Plugins accessible through menu (as before)
2. **NEW**: Plugins now also appear in Module Gallery
3. No code changes required in plugin implementation

### For Users with Existing Plugins

Simply place your plugin JARs in `~/.egps2/config/plugin/` and restart eGPS. They will automatically appear in both the menu and Module Gallery.

## Troubleshooting

### Plugin Not Appearing

1. **Check plugin directory**:
   ```bash
   ls ~/.egps2/config/plugin/
   ```

2. **Verify JAR structure**:
   ```bash
   jar tf myplugin.jar | grep eGPS2.plugin.properties
   ```

3. **Check logs** (enable debug logging):
   Look for `ModuleDiscoveryService` messages in console output

4. **Validate IModuleLoader implementation**:
   - Class must be public and concrete (not abstract)
   - Must have public no-arg constructor
   - Must properly implement all IModuleLoader methods

### Module Gallery Not Showing Plugins

1. **Force module cache reset**:
   Call `MainFrameProperties.resetModuleCache()` programmatically

2. **Check exclusion rules**:
   Ensure your plugin class doesn't match exclusion criteria

3. **Verify package name**:
   Plugin classes should not be in `egps2.*` or `demo.*` packages

## Future Enhancements

Potential improvements for future versions:

- [ ] Hot-reload plugins without restart
- [ ] Plugin dependency management
- [ ] Plugin marketplace/repository
- [ ] Version compatibility checking
- [ ] Plugin sandboxing/security
- [ ] Plugin configuration UI

## References

- **Module Discovery**: `src/egps2/frame/features/ModuleDiscoveryService.java`
- **Plugin Loading**: `src/egps2/plugin/manager/PluginOperation.java`
- **Module Gallery**: `src/egps2/builtin/modules/gallerymod/`
- **Test Case**: `src/test/egps2/frame/features/IntegratedModuleDiscoveryTest.java`
- **CLAUDE.md**: Project documentation and build instructions

## Support

For questions or issues:
- Check the test case for working examples
- Review logs for discovery/loading errors
- Consult IModuleLoader interface documentation
- Contact eGPS development team

---

**Version**: 2.1+
**Last Updated**: 2025-12-04
**Authors**: eGPS Dev Team
