# VOICE Module Architecture Guide

**VOICE** = **V**ersatile **O**pen **I**nput **C**lick **E**xecute

A comprehensive framework for rapidly developing parameter-driven GUI modules in eGPS.

**Version**: 2.1
**Last Updated**: 2025-12-04
**Package**: `egps2.builtin.modules.voice`

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Three Usage Frameworks](#three-usage-frameworks)
4. [Package Structure](#package-structure)
5. [Core Components](#core-components)
6. [Quick Start Guide](#quick-start-guide)
7. [Advanced Features](#advanced-features)
8. [Best Practices](#best-practices)

---

## Overview

### What is VOICE?

VOICE is a framework that simplifies the creation of interactive GUI modules by providing:
- **Parameter input interface** with syntax validation
- **Bookmark system** for saving common parameter sets
- **Example generation** for user guidance
- **Unified event processing** for consistent behavior
- **CLI support** for batch execution
- **Multiple integration patterns** (Floating, Tab, Dockable)

### Use Cases

VOICE is ideal for modules that:
- Accept structured text input (e.g., file paths, parameters)
- Need to save/load parameter presets
- Require both GUI and CLI interfaces
- Execute computational or file processing tasks

### Key Benefits

- **Rapid Development**: Inherit from base class, define parameters, implement execution logic
- **Consistent UX**: All VOICE modules share familiar input interface
- **Code Reuse**: 19 core classes handle GUI, events, parsing, and persistence
- **Flexibility**: Choose from 3 frameworks based on module complexity

---

## Architecture

### Design Philosophy

VOICE follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│  Framework Layer (3 base classes)                       │
│  - AbstractGuiBaseVoiceFeaturedPanel (Floating)        │
│  - TabModuleFaceOfVoice (Handytools)                   │
│  - DockableTabModuleFaceOfVoice (Dockable)             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  Core GUI Layer (VersatileOpenInputClickAbstractGuiBase)│
│  - Input area with syntax highlighting                  │
│  - Bookmark display and operations                      │
│  - Execute button with validation                       │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  Parameter Processing Layer (bean/)                     │
│  - AbstractParamsAssignerAndParser4VOICE               │
│  - VoiceParameterParser                                 │
│  - OrganizedParameterGetter                             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│  Execution Layer (Module Implementation)                │
│  - Your custom business logic                           │
└─────────────────────────────────────────────────────────┘
```

### GUI Layout

All VOICE modules share this standard layout:

```
┌─────────────────────────────────────────────────────────┐
│  VOICE Module Window/Panel                              │
│  ┌───────────────────┬───────────────────────────────┐  │
│  │                   │                               │  │
│  │  Bookmark Tree    │   Input Area                  │  │
│  │  (Left Panel)     │   ┌─────────────────────────┐ │  │
│  │                   │   │ #Example1               │ │  │
│  │  • Category A     │   │ param1: value1          │ │  │
│  │    ├─ Preset 1    │   │ param2: value2          │ │  │
│  │    └─ Preset 2    │   │ ...                     │ │  │
│  │  • Category B     │   └─────────────────────────┘ │  │
│  │    └─ Preset 3    │   ──────────────────────────  │  │
│  │                   │   Bookmark Operations         │  │
│  │                   │   [Save] [Load] [Delete] ...  │  │
│  │                   │   ──────────────────────────  │  │
│  │                   │   [Execute] [Reset] [Help]    │  │
│  └───────────────────┴───────────────────────────────┘  │
│  ┌───────────────────────────────────────────────────┐  │
│  │  Console Output (Optional, Tab modules only)      │  │
│  │  > Executing...                                   │  │
│  │  > Result: Success                                │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

---

## Three Usage Frameworks

VOICE provides three frameworks optimized for different integration patterns.

### 1. Floating Framework (浮动框架)

**Base Class**: `AbstractGuiBaseVoiceFeaturedPanel`
**Location**: `egps2.builtin.modules.voice.template.AbstractGuiBaseVoiceFeaturedPanel`

**Characteristics**:
- Independent dialog window (JDialog)
- Lightweight and quick to implement
- Suitable for standalone tools
- Minimal integration with main application

**When to Use**:
- Simple data import/conversion tools
- One-off utilities that don't need console output
- Modules that should feel like separate applications

**Implementation**:
```java
public class MyFloatingTool extends AbstractGuiBaseVoiceFeaturedPanel {

    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
        // Define parameters
        mapProducer.addKeyValueEntryBean("input", "/path/to/file", "Input file path");
        mapProducer.addKeyValueEntryBean("output", "/path/to/output", "Output directory");
    }

    @Override
    protected void execute(OrganizedParameterGetter params) throws Exception {
        // Your business logic
        String input = params.getValue("input");
        String output = params.getValue("output");
        // ... process data
    }
}
```

### 2. Handytools Framework (标签框架)

**Base Class**: `TabModuleFaceOfVoice`
**Location**: `egps2.builtin.modules.voice.fastmodvoice.TabModuleFaceOfVoice`

**Characteristics**:
- Embedded as a tab in the main window
- Includes bottom console for output
- Full integration with eGPS module system
- Implements `IModuleLoader` (self-contained)

**When to Use**:
- Complex computational modules
- Tools that produce extensive output
- Modules that need to be part of the main workflow

**Implementation**:
```java
public class MyHandytoolModule extends TabModuleFaceOfVoice {

    @Override
    protected void initializeGraphics(JPanel mainPanel) {
        initializeTheVoiceTools();
        // Add VOICE panel to your layout
        mainPanel.add(voiceTools.getPanel(), BorderLayout.CENTER);
    }

    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
        // Define parameters
        mapProducer.addKeyValueEntryBean("algorithm", "default", "Algorithm type");
    }

    @Override
    protected void startRun() throws Exception {
        OrganizedParameterGetter params = mapProducer.getParameterParser()
            .getOrganizedParameterGetter(voiceTools.getCurrentInput());
        // Your logic
    }

    @Override
    public String getTabName() { return "My Tool"; }

    @Override
    public String getShortDescription() { return "A handy tool"; }

    @Override
    public int[] getCategory() { /* classification */ }
}
```

### 3. Dockable Framework (可停靠子标签框架)

**Base Class**: `DockableTabModuleFaceOfVoice`
**Location**: `egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice`

**Characteristics**:
- Nested as a sub-tab within another module
- Shares console with parent module
- Lightweight (borrows infrastructure from parent)
- Ideal for tool collections

**When to Use**:
- Multiple related tools grouped together
- Sub-tools within a larger module
- Tool palettes or toolboxes

**Implementation**:
```java
public class MyDockableSubTab extends DockableTabModuleFaceOfVoice {

    public MyDockableSubTab(ComputationalModuleFace parentModule) {
        super(parentModule);
    }

    @Override
    protected void initializeGraphics(JPanel mainPanel) {
        initializeTheVoiceTools();
        mainPanel.add(voiceTools.getPanel());
    }

    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
        // Define parameters
    }

    @Override
    protected void startRun() throws Exception {
        // Your logic
    }

    @Override
    public String getTabName() { return "Sub Tool"; }
}
```

**Framework Comparison**:

| Feature | Floating | Handytools | Dockable |
|---------|----------|------------|----------|
| Window Type | JDialog | JPanel (Tab) | JPanel (Sub-tab) |
| Console Output | No | Yes | Yes (shared) |
| Integration Level | Low | High | Medium |
| Implementation Complexity | Simple | Moderate | Moderate |
| IModuleLoader | No | Yes | No |
| Use Case | Standalone tools | Main modules | Sub-tools |

---

## Package Structure

```
egps2.builtin.modules.voice/
│
├── VersatileOpenInputClickAbstractGuiBase.java
│   └─ Core GUI base class (485 lines)
│      • Manages dialog window and main layout
│      • Coordinates input, bookmark, and button panels
│      • Handles widget injection and event routing
│      • Provides persistence for dialog state
│
├── GUI Component Layer (7 classes)
│   ├── InputAreaPanel.java
│   │   └─ Multi-line text input with syntax hints
│   ├── BookmarkDisplayPanel.java
│   │   └─ Tree view for saved parameter sets
│   ├── BookmarkOperationPanel.java
│   │   └─ Buttons for bookmark CRUD operations
│   ├── BookMarkNode.java
│   │   └─ Tree node model for bookmarks
│   ├── HistoryNodeTreeRenderer.java
│   │   └─ Custom renderer for bookmark tree
│   ├── EventUniformlyProcessor.java
│   │   └─ Centralized event handling for all components
│   └── TextInputDialogWithOKCancel.java
│       └─ Dialog for editing bookmark names/categories
│
├── EditScriptState.java
│   └─ Enum: {NEWLY_EDIT, EDIT_MODE_EDIT, READ_HISTORY}
│
├── bean/ (Parameter Processing Layer - 3 classes)
│   ├── AbstractParamsAssignerAndParser4VOICE.java
│   │   └─ Abstract class for defining module parameters
│   │      • addKeyValueEntryBean(key, value, tip)
│   │      • Uses LinkedHashMap to preserve order
│   │      • Special keys: %1/%2 (categories), ^ (advanced separator)
│   ├── VoiceValueParameterBean.java
│   │   └─ Data class: {value, annotation, additionalInfo}
│   └── VoiceExampleGenerator.java
│       └─ Generates example text from parameter definitions
│
├── fastmodvoice/ (Framework Implementation - 6 classes)
│   ├── TabModuleFaceOfVoice.java (105 lines)
│   │   └─ Base for Handytools modules
│   ├── DockableTabModuleFaceOfVoice.java (48 lines)
│   │   └─ Base for Dockable sub-tabs
│   ├── SubTabModuleRunner.java
│   │   └─ CLI runner interface for batch execution
│   ├── VoiceParameterHandler4DIYTabModuleFace.java
│   │   └─ Bridges Tab modules with VOICE GUI
│   ├── VoiceParameterParser.java
│   │   └─ Parses input text into structured parameters
│   │      • Handles category markers (%1, %2)
│   │      • Supports advanced section (^)
│   │      • Parses key-value pairs with validation
│   └── OrganizedParameterGetter.java
│       └─ Convenience wrapper for retrieving parsed parameters
│          • getValue(key), getValueAsInt(key), etc.
│          • Category-aware parameter access
│
├── template/ (Floating Framework - 1 class)
│   └── AbstractGuiBaseVoiceFeaturedPanel.java (68 lines)
│       └─ Simplified base for floating dialog modules
│          • Combines GUI + parameter handling
│          • Minimal boilerplate for simple tools
│
└── images/ (SVG Icons - 6 files)
    ├── doEditing.svg      - Edit bookmark icon
    ├── execute.svg        - Execute button icon
    ├── focus.svg          - Focus input icon
    ├── reset.svg          - Reset input icon
    ├── treasureChest.svg  - Bookmark library icon
    └── tutorial.svg       - Help/tutorial icon
```

**Total**: 19 Java classes + 6 SVG icons

---

## Core Components

### 1. VersatileOpenInputClickAbstractGuiBase

**Role**: Foundation class providing the complete VOICE GUI.

**Key Methods**:
```java
// Subclass implements these:
protected abstract void execute(String inputs) throws Exception;
protected abstract String getExampleText();
protected abstract String getPersistingStorageString4Voice();

// Framework provides these:
public void run();  // Show the dialog
protected void addWidget(JComponent component, String name, Integer width);
protected JButton getExecuteButton();
```

**Features**:
- Singleton pattern per module (prevents multiple dialogs)
- Widget injection system for custom controls
- Automatic persistence of dialog size/position
- Thread-safe execution with progress feedback

### 2. AbstractParamsAssignerAndParser4VOICE

**Role**: Define what parameters your module needs.

**API**:
```java
// Add a parameter
VoiceValueParameterBean addKeyValueEntryBean(String key, String value, String tip);

// Add a batch of parameters
void addKeyValueEntryBean(LinkedHashMap<String, VoiceValueParameterBean> alreadyBean);

// Generate example text for users
String getExampleString();
String getExampleString(IModuleSignature signature);
```

**Parameter Syntax**:
```
# Comment lines start with #

# Category 1 marker (optional)
%1 File Inputs
inputFile: /path/to/input.txt
outputDir: /path/to/output/

# Category 2 marker
%2 Algorithm Settings
algorithm: fast
threshold: 0.05

# Advanced parameters (collapsed by default)
^ Advanced Options
debugMode: false
logLevel: INFO
```

**Special Keys**:
- `%1`, `%2`, `%3`, ... : Category dividers (helps organize complex parameter sets)
- `^` : Marks start of advanced section (collapsed in GUI)
- `@` : Reserved for remnant information (not commonly used)

### 3. VoiceParameterParser

**Role**: Parse user input text into structured parameters.

**Usage**:
```java
VoiceParameterParser parser = new VoiceParameterParser();
OrganizedParameterGetter getter = parser.getOrganizedParameterGetter(inputText);

// Access parsed values
String value = getter.getValue("inputFile");
int threshold = getter.getValueAsInt("threshold", 10);  // with default
List<String> files = getter.getValueAsList("fileList");  // comma-separated

// Category-aware access
Map<String, String> category1 = getter.getCategoryParameters(1);
```

**Parsing Rules**:
- Lines starting with `#` are comments (ignored)
- Format: `key: value` or `key=value`
- Whitespace around `:` or `=` is trimmed
- Empty lines are ignored
- Duplicate keys: last value wins

### 4. OrganizedParameterGetter

**Role**: Convenient API for retrieving parsed parameters.

**Common Methods**:
```java
String getValue(String key);
String getValue(String key, String defaultValue);
int getValueAsInt(String key);
int getValueAsInt(String key, int defaultValue);
double getValueAsDouble(String key);
boolean getValueAsBoolean(String key);
List<String> getValueAsList(String key);  // splits on comma
File getValueAsFile(String key);
```

**Null Safety**: Methods with defaults return the default if key is missing or empty.

### 5. Bookmark System

**Components**:
- `BookmarkDisplayPanel`: Tree view showing saved presets
- `BookmarkOperationPanel`: CRUD buttons
- `BookMarkNode`: Tree node model (category or leaf)
- Persistence: JSON files in `~/.egps/voice/[moduleName]/bookmarks.json`

**User Workflow**:
1. User enters parameters in input area
2. Clicks "Save" bookmark button
3. Enters bookmark name and category
4. Bookmark appears in tree
5. Later: click bookmark to load parameters

**Implementation**: Handled automatically by `VersatileOpenInputClickAbstractGuiBase`.

---

## Quick Start Guide

### Step 1: Choose Your Framework

| Question | Answer | Framework |
|----------|--------|-----------|
| Need console output? | No | Floating |
| Need console output? | Yes → Standalone module? | Yes → Handytools |
| Need console output? | Yes → Part of another module? | Yes → Dockable |

### Step 2: Create Your Class

**Example: Simple File Converter (Floating)**

```java
package com.myproject.tools;

import egps2.builtin.modules.voice.template.AbstractGuiBaseVoiceFeaturedPanel;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;

public class FileConverter extends AbstractGuiBaseVoiceFeaturedPanel {

    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
        // Define parameters with examples and tooltips
        mapProducer.addKeyValueEntryBean(
            "inputFile",
            "/path/to/input.txt",
            "Path to the input file"
        );
        mapProducer.addKeyValueEntryBean(
            "outputFormat",
            "CSV",
            "Output format: CSV, TSV, or JSON"
        );
        mapProducer.addKeyValueEntryBean(
            "outputFile",
            "/path/to/output.csv",
            "Path to save converted file"
        );
    }

    @Override
    protected void execute(OrganizedParameterGetter params) throws Exception {
        // Get parsed parameters
        String inputPath = params.getValue("inputFile");
        String format = params.getValue("outputFormat");
        String outputPath = params.getValue("outputFile");

        // Your business logic
        File inputFile = new File(inputPath);
        if (!inputFile.exists()) {
            throw new Exception("Input file not found: " + inputPath);
        }

        // Convert file...
        convertFile(inputFile, format, new File(outputPath));

        // Show success message
        JOptionPane.showMessageDialog(null, "Conversion successful!");
    }

    @Override
    protected String getPersistingStorageString4Voice() {
        // Unique identifier for saving bookmarks
        return "fileconverter.bookmarks";
    }

    private void convertFile(File input, String format, File output) {
        // Your conversion logic here
    }
}
```

### Step 3: Launch Your Module

**For Floating modules**:
```java
SwingUtilities.invokeLater(() -> {
    FileConverter converter = new FileConverter();
    converter.run();  // Shows the dialog
});
```

**For Tab modules**: Implement `IModuleLoader` and register with eGPS module system.

### Step 4: Test

1. Run your module
2. Observe the example text in the input area
3. Modify parameters
4. Click Execute
5. Save a bookmark preset
6. Try loading the preset

---

## Advanced Features

### 1. Multiple Examples

Provide multiple numbered examples for users:

```java
@Override
protected int getNumberOfExamples() {
    return 3;  // Will show #Example1, #Example2, #Example3
}

@Override
protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
    // Use VoiceExampleGenerator.generateMultipleExampleMarker(exampleNumber)
    mapProducer.addKeyValueEntryBean(
        "#Example1",
        "",
        "Basic usage example"
    );
    mapProducer.addKeyValueEntryBean("input", "/data/sample1.txt", "");

    mapProducer.addKeyValueEntryBean(
        "#Example2",
        "",
        "Advanced usage example"
    );
    mapProducer.addKeyValueEntryBean("input", "/data/sample2.txt", "");
    mapProducer.addKeyValueEntryBean("advanced", "true", "");
}
```

### 2. Parameter Categories

Organize many parameters into collapsible categories:

```java
@Override
protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
    // Category 1: Input/Output
    mapProducer.addKeyValueEntryBean("%1", "Input/Output Files", "");
    mapProducer.addKeyValueEntryBean("inputFile", "", "Input file");
    mapProducer.addKeyValueEntryBean("outputDir", "", "Output directory");

    // Category 2: Algorithm Settings
    mapProducer.addKeyValueEntryBean("%2", "Algorithm Parameters", "");
    mapProducer.addKeyValueEntryBean("algorithm", "default", "Algorithm type");
    mapProducer.addKeyValueEntryBean("threads", "4", "Number of threads");

    // Advanced section (collapsed)
    mapProducer.addKeyValueEntryBean("^", "Advanced Options", "");
    mapProducer.addKeyValueEntryBean("debugMode", "false", "Enable debug");
}
```

### 3. Custom Widgets

Inject custom GUI components into the VOICE dialog:

```java
@Override
protected void execute(String inputs) throws Exception {
    // Add a custom widget
    JButton customButton = new JButton("Browse Files");
    customButton.addActionListener(e -> {
        // File chooser logic
    });
    addWidget(customButton, "File Browser", 150);  // width=150px
}
```

Widgets appear to the right of the main input area.

### 4. CLI Support

Make your VOICE module executable from command line:

```java
public class MyModule extends TabModuleFaceOfVoice implements SubTabModuleRunner {

    @Override
    public void runFromCommandLine(String[] args) throws Exception {
        // args[0] = parameter file path
        String paramText = Files.readString(Paths.get(args[0]));
        OrganizedParameterGetter params = mapProducer.getParameterParser()
            .getOrganizedParameterGetter(paramText);
        executeLogic(params);
    }
}
```

**Usage**:
```bash
java -cp "..." egps2.builtin.modules.CLI com.myproject.MyModule params.txt
```

### 5. Module Signature Integration

If your module implements `IModuleSignature`, VOICE can include signature in examples:

```java
public class MyModule extends TabModuleFaceOfVoice implements IModuleSignature {

    @Override
    public String getSignature() {
        return "MyModule v1.2.0 - Author: John Doe";
    }

    // Example text will automatically include:
    // # Generated by: MyModule v1.2.0 - Author: John Doe
    // # Date: 2025-12-04
}
```

### 6. Validation

Add custom validation before execution:

```java
@Override
protected void execute(OrganizedParameterGetter params) throws Exception {
    // Validate parameters
    String input = params.getValue("inputFile");
    if (input == null || input.isEmpty()) {
        throw new IllegalArgumentException("Input file is required");
    }

    File file = new File(input);
    if (!file.exists()) {
        throw new FileNotFoundException("File not found: " + input);
    }

    // Proceed with execution
    processFile(file);
}
```

Exceptions are caught by the framework and displayed to the user.

---

## Best Practices

### Parameter Naming

✅ **DO**:
- Use descriptive, self-explanatory names: `inputFile`, `outputDirectory`, `algorithmType`
- Use camelCase for consistency
- Provide clear example values that demonstrate expected format

❌ **DON'T**:
- Use cryptic abbreviations: `inF`, `outD`, `alg`
- Use spaces in keys (parser may handle it, but avoid)

### Example Text Quality

✅ **DO**:
- Provide realistic example values users can copy/modify
- Include comments explaining non-obvious parameters
- Show multiple examples for different use cases

❌ **DON'T**:
- Leave examples empty or with placeholder text like "TODO"
- Omit parameter descriptions

### Error Handling

✅ **DO**:
```java
@Override
protected void execute(OrganizedParameterGetter params) throws Exception {
    try {
        // Business logic
    } catch (IOException e) {
        throw new Exception("Failed to read file: " + e.getMessage(), e);
    }
}
```

❌ **DON'T**:
```java
@Override
protected void execute(OrganizedParameterGetter params) {
    try {
        // Business logic
    } catch (Exception e) {
        e.printStackTrace();  // Silent failure, user sees nothing
    }
}
```

### Bookmark Storage Key

✅ **DO**:
- Use a unique, stable identifier: `"mymodule.toolname.bookmarks"`
- Include module namespace to avoid collisions

❌ **DON'T**:
- Use generic names: `"bookmarks"`, `"data"`
- Change the key between versions (breaks bookmark persistence)

### Threading

✅ **DO**:
- Execute long-running tasks in background threads
- Update GUI on EDT using `SwingUtilities.invokeLater()`

```java
@Override
protected void execute(OrganizedParameterGetter params) throws Exception {
    CompletableFuture.runAsync(() -> {
        // Long computation
        String result = performHeavyCalculation();

        SwingUtilities.invokeLater(() -> {
            // Update GUI with result
        });
    });
}
```

❌ **DON'T**:
- Block the EDT with long computations
- Update Swing components from background threads

### Parameter Defaults

✅ **DO**:
- Provide sensible defaults in example text
- Use `getValueAsInt(key, defaultValue)` for optional parameters

❌ **DON'T**:
- Assume all parameters are always provided
- Crash on missing optional parameters

---

## Migration from Old Code

If you have modules using deprecated VOICE classes, migrate as follows:

### Old DIYTools → New Handytools

**Before** (deprecated):
```java
public class OldModule extends DIYToolModuleFace { ... }
```

**After**:
```java
public class NewModule extends TabModuleFaceOfVoice { ... }
```

**Changes**:
- Rename `setParameterMap()` → `setParameter()`
- Rename `getParserMapBean()` → Access via `mapProducer.requiredParams`
- Update parameter access: `map.get(key)` → `params.getValue(key)`

### Old Template → New Template

**Before** (deprecated):
```java
public class OldTool extends CompleteModuleFace4Voice { ... }
```

**After**:
```java
public class NewTool extends AbstractGuiBaseVoiceFeaturedPanel { ... }
```

**Changes**:
- Same method names (mostly compatible)
- Update imports from `voice.template` package

---

## Troubleshooting

### Issue: Dialog doesn't show

**Cause**: Not calling `run()` on EDT.

**Solution**:
```java
SwingUtilities.invokeLater(() -> myModule.run());
```

### Issue: Parameters not parsed correctly

**Cause**: Incorrect input format (missing `:` or `=`).

**Solution**: Validate input format matches `key: value` or `key=value`.

### Issue: Bookmarks not saving

**Cause**: Invalid storage key or permission issues.

**Solution**:
- Check `getPersistingStorageString4Voice()` returns unique key
- Verify `~/.egps/` directory is writable

### Issue: Execute button doesn't work

**Cause**: Exception thrown in `execute()` without proper handling.

**Solution**: Check console for stack traces. Ensure exceptions propagate to framework.

---

## Examples in eGPS Codebase

Study these real-world VOICE modules:

1. **Handytools for Biologist** (`demo.handytools`)
   - Complex multi-category parameters
   - Multiple sub-tabs using Dockable framework

2. **VOICE Floating Demo** (`demo.floating`)
   - Simple standalone tool
   - Good example of AbstractGuiBaseVoiceFeaturedPanel

3. **VOICE Dockable Demo** (`demo.dockable`)
   - Parent module with multiple VOICE sub-tabs
   - Shows how to organize tool collections

---

## Changelog

### v2.1 (2025-12-04)
- **Removed**: 13 deprecated classes (diytools, old template classes)
- **Cleaned**: Package structure now contains only 19 core classes
- **Improved**: Code organization and clarity
- **No Breaking Changes**: All active frameworks still supported

### v2.0
- Introduced `fastmodvoice` framework
- Added `TabModuleFaceOfVoice` and `DockableTabModuleFaceOfVoice`
- Deprecated old DIYTools and template classes

### v1.x
- Initial VOICE framework
- Original template classes

---

## Summary

VOICE is a mature, battle-tested framework for building parameter-driven modules in eGPS. By handling GUI, parsing, bookmarks, and persistence, it lets you focus on your module's unique logic.

**Key Takeaways**:
- Choose the right framework (Floating, Handytools, or Dockable)
- Define parameters clearly with good examples
- Implement `execute()` with proper error handling
- Test with bookmarks to ensure good UX

For questions or issues, refer to existing VOICE modules in the codebase or contact the eGPS development team.
