# Module Discovery Service - Exclusion Rules

**Date**: 2025-12-04
**Version**: 2.1
**Status**: ✅ Implemented and Tested

---

## Overview

The `ModuleDiscoveryService` automatically scans the classpath to discover all classes implementing `IModuleLoader`. However, not all implementations should be treated as standalone modules. This document describes the exclusion rules that filter out non-module classes.

---

## Exclusion Rules

The `ModuleDiscoveryService` excludes the following types of classes:

### 1. **Interfaces and Abstract Classes** ❌

**Why**: Cannot be instantiated directly.

**Examples**:
- Any interface extending `IModuleLoader`
- Abstract classes implementing `IModuleLoader`

**Implementation**: Basic Java reflection check
```java
if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
    continue;
}
```

---

### 2. **SubTab Classes** ❌

**What are SubTabs?**
- Classes extending `DockableTabModuleFaceOfVoice`
- Nested components within parent dockable modules
- Not independent modules - should only be loaded by their parent

**Why Exclude?**
- SubTabs are sub-components, not standalone modules
- They depend on parent module infrastructure
- Should NOT appear in Module Manager
- Should NOT be loaded by EGPS2ServiceLoader

**Example Architecture**:
```
demo.dockable.IndependentModuleLoader  ← Real module (discovered ✓)
  └─ Creates: DockableModuleMainFace
       ├─ SubTab 1: SimpleExpressionProducer     ← SubTab (excluded ✓)
       ├─ SubTab 2: SimpleAlignmentSimulator     ← SubTab (excluded ✓)
       ├─ SubTab 3: LargeTextGeneratorSubTab     ← SubTab (excluded ✓)
       └─ SubTab 4: GroupwiseStatisticalTest     ← SubTab (excluded ✓)
```

**Excluded SubTabs** (6 total):
1. `demo.dockable.SimpleExpressionProducer`
2. `demo.dockable.SimpleAlignmentSimulator`
3. `demo.dockable.LargeTextGeneratorSubTab`
4. `demo.dockable.GroupwiseStatisticalTest`
5. `demo.dockable.BiologicalPathwayEnrichment`
6. `demo.dockable.AdvGroupwiseStatisticalTest`

**Implementation**:
```java
private boolean isSubTabClass(Class<?> clazz) {
    try {
        Class<?> dockableTabClass = Class.forName(
            "egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice");
        return dockableTabClass.isAssignableFrom(clazz) && !dockableTabClass.equals(clazz);
    } catch (ClassNotFoundException e) {
        return false;
    }
}
```

---

### 3. **Core Modules** ❌

**What are Core Modules?**
- Modules hardcoded in MyFrame's **Mainframe core** menu
- Essential modules always available via menu (Ctrl+1 through Ctrl+8)
- Should NOT be reported as "newly discovered" modules

**Why Exclude?**
- They are already integrated into the main menu
- Users access them via menu, not through ITools Manager
- Prevents duplicate entries in module lists
- Reduces noise in "newly discovered modules" logs

**Core Modules List** (8 total):

| Module | Shortcut | Menu Location |
|--------|----------|---------------|
| `egps2.builtin.modules.filemanager.IndependentModuleLoader` | Ctrl+1 | Mainframe core → The Resource Manager |
| `egps2.builtin.modules.gallerymod.IndependentModuleLoader` | Ctrl+2 | Mainframe core → Module gallery |
| `egps2.builtin.modules.lowtextedi.IndependentModuleLoader` | Ctrl+3 | Mainframe core → Low volume text editor |
| `egps2.builtin.modules.largetextedi.IndependentModuleLoader` | Ctrl+4 | Mainframe core → Large volume text view |
| `demo.handytools.HandyToolExampleMain` | Ctrl+5 | Mainframe core → Handy tool for biologist |
| `demo.dockable.IndependentModuleLoader` | Ctrl+6 | Mainframe core → VOICE demo: Dockable |
| `demo.floating.IndependentModuleLoader` | Ctrl+7 | Mainframe core → VOICE demo: Floating |
| `egps2.builtin.modules.itoolmanager.IndependentModuleLoader` | Ctrl+8 | Mainframe core → ITools Manager |

**Implementation**:
```java
private boolean isCoreModuleClass(Class<?> clazz) {
    String className = clazz.getName();

    return className.equals("egps2.builtin.modules.filemanager.IndependentModuleLoader") ||
           className.equals("egps2.builtin.modules.gallerymod.IndependentModuleLoader") ||
           className.equals("egps2.builtin.modules.lowtextedi.IndependentModuleLoader") ||
           className.equals("egps2.builtin.modules.largetextedi.IndependentModuleLoader") ||
           className.equals("demo.handytools.HandyToolExampleMain") ||
           className.equals("demo.dockable.IndependentModuleLoader") ||
           className.equals("demo.floating.IndependentModuleLoader") ||
           className.equals("egps2.builtin.modules.itoolmanager.IndependentModuleLoader");
}
```

**Reference**:
- Core modules are loaded in `MainFrameProperties.loadInternalCoreModules()`
- Located in: `src/egps2/frame/MainFrameProperties.java:183-331`

---

### 4. **Template Base Classes** ❌

**What are Template Base Classes?**
- Abstract base classes used as templates for creating new modules
- Part of the fast module template system
- NOT actual modules - only their subclasses are modules

**Why Exclude?**
- They are templates, not functional modules
- Cannot be instantiated or used independently
- Only their concrete subclasses should be discovered

**Template Base Classes** (2 total):
1. `egps2.plugin.fastmodtem.FastBaseTemplate`
   - Base template for fast module creation
   - Provides scaffolding for new modules

2. `egps2.plugin.fastmodtem.IndependentModuleLoader`
   - Template loader class
   - Only subclasses implementing specific functionality should be discovered

**Implementation**:
```java
private boolean isTemplateBaseClass(Class<?> clazz) {
    String className = clazz.getName();

    return className.equals("egps2.plugin.fastmodtem.FastBaseTemplate") ||
           className.equals("egps2.plugin.fastmodtem.IndependentModuleLoader");
}
```

**Note**: Subclasses of these templates ARE discovered as modules (that's the intended behavior).

---

## Discovery Flow

```
┌─────────────────────────────────────┐
│   Scan classpath with Reflections   │
│   Find all IModuleLoader subclasses │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Filter 1: Interfaces & Abstract    │
│  └─ Exclude non-instantiable classes│
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Filter 2: SubTab Classes           │
│  └─ Exclude DockableTabModuleFace*  │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Filter 3: Core Modules             │
│  └─ Exclude Mainframe core menu mods│
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Filter 4: Template Base Classes    │
│  └─ Exclude FastBaseTemplate, etc. │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  ✓ Valid Standalone Modules         │
│  (Ready for discovery system)       │
└─────────────────────────────────────┘
```

---

## Test Results

**Test File**: `src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java`

**Test Command**:
```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" \
  test.egps2.frame.features.ModuleDiscoveryServiceTest
```

**Output**:
```
=== Module Discovery Service Test ===
Testing exclusion logic...

Total modules discovered: 12

Checking SubTab exclusion:
  ✓ EXCLUDED (correct) - demo.dockable.SimpleExpressionProducer
  ✓ EXCLUDED (correct) - demo.dockable.SimpleAlignmentSimulator
  ✓ EXCLUDED (correct) - demo.dockable.LargeTextGeneratorSubTab
  ✓ EXCLUDED (correct) - demo.dockable.GroupwiseStatisticalTest
  ✓ EXCLUDED (correct) - demo.dockable.BiologicalPathwayEnrichment
  ✓ EXCLUDED (correct) - demo.dockable.AdvGroupwiseStatisticalTest

Checking Core Module exclusion:
  ✓ EXCLUDED (correct) - egps2.builtin.modules.filemanager.IndependentModuleLoader
  ✓ EXCLUDED (correct) - egps2.builtin.modules.gallerymod.IndependentModuleLoader
  ✓ EXCLUDED (correct) - egps2.builtin.modules.lowtextedi.IndependentModuleLoader
  ✓ EXCLUDED (correct) - egps2.builtin.modules.largetextedi.IndependentModuleLoader
  ✓ EXCLUDED (correct) - demo.handytools.HandyToolExampleMain
  ✓ EXCLUDED (correct) - demo.dockable.IndependentModuleLoader
  ✓ EXCLUDED (correct) - demo.floating.IndependentModuleLoader
  ✓ EXCLUDED (correct) - egps2.builtin.modules.itoolmanager.IndependentModuleLoader

Checking Template Base Class exclusion:
  ✓ EXCLUDED (correct) - egps2.plugin.fastmodtem.FastBaseTemplate
  ✓ EXCLUDED (correct) - egps2.plugin.fastmodtem.IndependentModuleLoader

Summary:
  SubTabs correctly excluded: 6 / 6
  Core modules correctly excluded: 8 / 8
  Template classes correctly excluded: 2 / 2

✓ Test PASSED: All exclusion rules working correctly!
```

---

## Impact

### Before Exclusion Rules

**Problem**: Module discovery found **29 modules**, including:
- 6 SubTab classes (incorrectly discovered)
- 8 Core modules (already in Mainframe core menu)
- 2 Template base classes (not functional modules)
- Logs flooded with "Discovered new module not in config" for core modules

**Issues**:
- SubTabs appeared as standalone modules (confusing)
- Core modules reported as "newly discovered" (noise)
- Template base classes listed as modules (incorrect)
- Module Manager showed non-module entries

### After Exclusion Rules

**Result**: Module discovery finds **12 valid modules**
- Only actual standalone modules discovered
- No SubTabs in module list
- Core modules silently excluded (they're already accessible via menu)
- Template base classes filtered out
- Clean logs without duplicate module warnings

**Benefits**:
- Accurate module count
- Clean module discovery logs
- Proper separation of concerns
- Better user experience in ITools Manager

---

## Module Count Breakdown

| Category | Count | Status |
|----------|-------|--------|
| **Total IModuleLoader implementations** | 29 | Scanned by Reflections |
| **- SubTabs** | 6 | Excluded ✓ |
| **- Core modules** | 8 | Excluded ✓ |
| **- Template base classes** | 2 | Excluded ✓ |
| **- Interfaces/Abstract** | 1 | Excluded ✓ |
| **= Valid standalone modules** | **12** | **Discovered** ✓ |

---

## For Developers

### Adding a New Core Module

If you add a new module to the Mainframe core menu, you must also add it to the exclusion list in `ModuleDiscoveryService.isCoreModuleClass()`:

```java
private boolean isCoreModuleClass(Class<?> clazz) {
    String className = clazz.getName();

    return className.equals("egps2.builtin.modules.filemanager.IndependentModuleLoader") ||
           // ... existing entries ...
           className.equals("your.new.core.module.IndependentModuleLoader");  // ADD HERE
}
```

### Creating a New Module Template

If you create a new template base class (like FastBaseTemplate), add it to `ModuleDiscoveryService.isTemplateBaseClass()`:

```java
private boolean isTemplateBaseClass(Class<?> clazz) {
    String className = clazz.getName();

    return className.equals("egps2.plugin.fastmodtem.FastBaseTemplate") ||
           className.equals("your.new.template.BaseClass");  // ADD HERE
}
```

### Creating SubTabs

When creating SubTab classes, ensure they extend `DockableTabModuleFaceOfVoice`. They will be automatically excluded from module discovery:

```java
// This will be EXCLUDED from module discovery ✓
public class MySubTab extends DockableTabModuleFaceOfVoice {
    public MySubTab(ComputationalModuleFace parent) {
        super(parent);
    }
}
```

---

## Related Files

### Source Code
- `src/egps2/frame/features/ModuleDiscoveryService.java` - Discovery logic with exclusion rules
- `src/egps2/frame/MainFrameProperties.java` - Core module menu initialization

### Tests
- `src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java` - Comprehensive exclusion test

### Documentation
- `docs/MODULE_DISCOVERY_SUBTAB_EXCLUSION.md` - SubTab exclusion details
- `docs/VOICE_MODULE_ARCHITECTURE.md` - VOICE framework documentation
- `docs/BONUS_MODULES_REFACTORING_2.1.md` - Bonus module refactoring

---

## Future Exclusions

Potential future exclusion categories:
- **Test modules** - Modules used only for testing
- **Internal utility modules** - Not meant for end-users
- **Deprecated modules** - Old modules kept for compatibility
- **Development scaffolding** - Debug and dev-only modules

---

## Summary

The `ModuleDiscoveryService` implements 4 exclusion rules to ensure only valid standalone modules are discovered:

1. ❌ **Interfaces & Abstract Classes** (1 excluded)
2. ❌ **SubTab Classes** (6 excluded)
3. ❌ **Core Modules** (8 excluded)
4. ❌ **Template Base Classes** (2 excluded)

**Result**: 12 valid modules discovered (down from 29)

**Status**: ✅ All exclusion rules tested and working correctly

---

**Last Updated**: 2025-12-04
**Version**: 2.1
