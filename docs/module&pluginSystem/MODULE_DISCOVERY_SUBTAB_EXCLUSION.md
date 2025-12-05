# Module Discovery Service - SubTab Exclusion

**Date**: 2025-12-04
**Issue**: SubTab classes should not be discovered as standalone modules
**Status**: ✅ Fixed

---

## Problem

The `ModuleDiscoveryService` was incorrectly identifying SubTab classes (extending `DockableTabModuleFaceOfVoice`) as standalone modules. These classes are not independent modules - they are sub-components that should only be loaded by their parent module.

### Why SubTabs Are Not Modules

**SubTab classes** (extending `DockableTabModuleFaceOfVoice`):
- Are nested components within a parent dockable module
- Depend on their parent module's infrastructure
- Should NOT be instantiated independently
- Should NOT appear in the Module Manager
- Should NOT be loaded by `EGPS2ServiceLoader`

**Example of SubTab architecture:**

```
demo.dockable.IndependentModuleLoader  ← Real module (discovered ✓)
  └─ Creates: DockableModuleMainFace
       ├─ SubTab 1: SimpleExpressionProducer     ← SubTab (excluded ✓)
       ├─ SubTab 2: SimpleAlignmentSimulator     ← SubTab (excluded ✓)
       ├─ SubTab 3: LargeTextGeneratorSubTab     ← SubTab (excluded ✓)
       └─ SubTab 4: GroupwiseStatisticalTest     ← SubTab (excluded ✓)
```

---

## Solution

Added filtering logic to `ModuleDiscoveryService.scanAllModuleClasses()` to exclude SubTab classes.

### Code Changes

**File**: `src/egps2/frame/features/ModuleDiscoveryService.java`

**Added method**:
```java
/**
 * Checks if a class is a SubTab (extends DockableTabModuleFaceOfVoice).
 *
 * <p>SubTab classes are not standalone modules - they are sub-components
 * that should be loaded by their parent module, not by the module discovery system.
 *
 * @param clazz Class to check
 * @return true if the class is a SubTab, false otherwise
 */
private boolean isSubTabClass(Class<?> clazz) {
    try {
        // Try to load DockableTabModuleFaceOfVoice class
        Class<?> dockableTabClass = Class.forName(
            "egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice");

        // Check if the class extends DockableTabModuleFaceOfVoice
        return dockableTabClass.isAssignableFrom(clazz) && !dockableTabClass.equals(clazz);
    } catch (ClassNotFoundException e) {
        // If DockableTabModuleFaceOfVoice doesn't exist, no classes can be SubTabs
        return false;
    }
}
```

**Updated scanning logic**:
```java
for (Class<? extends IModuleLoader> clazz : subTypes) {
    // Exclude abstract classes and interfaces
    if (clazz.isInterface() ||
        java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
        continue;
    }

    // Exclude SubTab classes (NEW!)
    if (isSubTabClass(clazz)) {
        log.debug("Excluding SubTab class: {}", clazz.getName());
        continue;
    }

    moduleClasses.add(clazz.getName());
}
```

---

## Excluded SubTab Classes

The following SubTab classes are now correctly excluded from module discovery:

1. **demo.dockable.SimpleExpressionProducer**
   - Parent module: demo.dockable.IndependentModuleLoader
   - Purpose: Simulates gene expression profiles

2. **demo.dockable.SimpleAlignmentSimulator**
   - Parent module: demo.dockable.IndependentModuleLoader
   - Purpose: Simulates sequence alignments

3. **demo.dockable.LargeTextGeneratorSubTab**
   - Parent module: demo.dockable.IndependentModuleLoader
   - Purpose: Generates large text files for testing

4. **demo.dockable.GroupwiseStatisticalTest**
   - Parent module: demo.dockable.IndependentModuleLoader
   - Purpose: Performs statistical tests between groups

5. **demo.dockable.BiologicalPathwayEnrichment**
   - Parent module: demo.dockable.IndependentModuleLoader
   - Purpose: Pathway enrichment analysis

6. **demo.dockable.AdvGroupwiseStatisticalTest**
   - Parent module: demo.dockable.IndependentModuleLoader
   - Purpose: Advanced statistical tests

---

## Testing

**Test file**: `src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java`

**Test command**:
```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" \
  test.egps2.frame.features.ModuleDiscoveryServiceTest
```

**Test results**:
```
=== Module Discovery Service Test ===
Testing SubTab exclusion logic...

Total modules discovered: 23

Checking SubTab exclusion:
  ✓ EXCLUDED (correct) - demo.dockable.SimpleExpressionProducer
  ✓ EXCLUDED (correct) - demo.dockable.SimpleAlignmentSimulator
  ✓ EXCLUDED (correct) - demo.dockable.LargeTextGeneratorSubTab
  ✓ EXCLUDED (correct) - demo.dockable.GroupwiseStatisticalTest
  ✓ EXCLUDED (correct) - demo.dockable.BiologicalPathwayEnrichment
  ✓ EXCLUDED (correct) - demo.dockable.AdvGroupwiseStatisticalTest

Summary:
  SubTabs correctly excluded: 6 / 6

✓ Test PASSED: All SubTab classes are correctly excluded!
```

---

## Impact

### Before Fix

- Module discovery found **29 modules** (including 6 invalid SubTabs)
- SubTabs appeared in Module Manager (confusing for users)
- Attempting to load a SubTab independently would cause errors
- Module configuration file contained invalid entries

### After Fix

- Module discovery finds **23 modules** (correct count)
- Only valid standalone modules appear in Module Manager
- SubTabs are properly hidden from users
- Cleaner module configuration

---

## Module Discovery Exclusion Rules

The `ModuleDiscoveryService` now excludes:

1. **Interfaces** - Cannot be instantiated
2. **Abstract classes** - Cannot be instantiated
3. **SubTab classes** - Not standalone modules (NEW!)

Future exclusions could include:
- Test modules
- Internal utility modules
- Deprecated modules

---

## For Developers

### How to Create a Dockable Module with SubTabs

**Step 1**: Create the main module loader
```java
package demo.mymodule;

public class IndependentModuleLoader implements IModuleLoader {
    @Override
    public ModuleFace getFace() {
        return new MyModuleMainFace(this);
    }
    // ... other methods
}
```

**Step 2**: Create the main face with tabs
```java
package demo.mymodule;

public class MyModuleMainFace extends ComputationalModuleFace {
    @Override
    protected void initializeGraphics(JPanel mainPanel) {
        JTabbedPane tabs = new JTabbedPane();

        // Add SubTabs
        tabs.add("SubTab 1", new MySubTab1(this));
        tabs.add("SubTab 2", new MySubTab2(this));

        mainPanel.add(tabs);
    }
}
```

**Step 3**: Create SubTab classes
```java
package demo.mymodule;

// This class will be EXCLUDED from module discovery ✓
public class MySubTab1 extends DockableTabModuleFaceOfVoice {
    public MySubTab1(ComputationalModuleFace parent) {
        super(parent);
    }
    // ... SubTab implementation
}
```

---

## Related Files

- `src/egps2/frame/features/ModuleDiscoveryService.java` - Discovery logic
- `src/egps2/frame/features/EGPS2ServiceLoader.java` - Module loading
- `src/egps2/builtin/modules/voice/fastmodvoice/DockableTabModuleFaceOfVoice.java` - SubTab base class
- `src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java` - Test

---

## Conclusion

SubTab classes are now correctly excluded from module discovery, preventing them from appearing as standalone modules in the Module Manager and configuration files.

**Status**: ✅ Fixed and tested
