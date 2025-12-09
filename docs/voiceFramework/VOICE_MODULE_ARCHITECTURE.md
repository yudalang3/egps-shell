# VOICE Module Architecture Guide

**VOICE** = **V**ersatile **O**pen **I**nput **C**lick **E**xecute

A comprehensive framework for rapidly developing parameter-driven GUI modules in eGPS from version 2.1.

**Version**: 2.1.0
**Last Updated**: 2025-12-09
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
        mapProducer.addKeyValueEntryBean("%1","Alignment file for evolution","");
        mapProducer.addKeyValueEntryBean("input.msa.fasta",
                EGPSProperties.PROPERTIES_DIR + "/path/to/test.fa",
                "The multiple sequence alignment file\n#Actually, it is simulated by self");

        mapProducer.addKeyValueEntryBean("%2","Expression files","");
        mapProducer.addKeyValueEntryBean("input.expression.tpm",
                EGPSProperties.PROPERTIES_DIR + "/path/to/floatingVoice/exp.tpm",
                "The normalized tmp fasta file\n#Actually, it is simulated by self");

        mapProducer.addKeyValueEntryBean("%3","Molecular interaction files","");
        mapProducer.addKeyValueEntryBean("input.mol.picture",
                EGPSProperties.PROPERTIES_DIR + "/path/to/mol.mechanism.pptx",
                "The power point file of the molecular mechanism\n#Please download the test data, first.\n#While this is demo, just click button.");
    }

    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        // Your business logic
        String fasta_path = o.getSimplifiedString("input.msa.fasta");
        Optional<List<String>> lists = o.getComplicatedValues("input.expression.tpm");
        boolean has_fast = o.getSimplifiedBool("has.msa.fasta");
        double dispersion = o.getSimplifiedDouble("model.dispersion");
        int number = o.getSimplifiedInt("model.gene.number");
        String simplifiedStringWithDefault = o.getSimplifiedStringWithDefault("model.differentialRatio");
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
- Implements `IModuleLoader` and `ModuleFace` (self-contained)

**When to Use**:
- Complex computational modules
- Tools that produce extensive output
- Modules that need to be part of the main workflow

**Implementation**:



```java
public class MyHandytoolModule extends TabModuleFaceOfVoice {

    /**
     * Define parameters for the handy tool module
     * @param mapProducer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
        mapProducer.addKeyValueEntryBean("input.tsv.path", EXAMPLE_PATH, "The tsv file include header line.");
        mapProducer.addKeyValueEntryBean("x.variable", "X", "The x variable values");
        mapProducer.addKeyValueEntryBean("y.variable", "Y", "The y variable values");
        mapProducer.addKeyValueEntryBean("output.tsv.path", "", "Optional file, default is to the console");
        mapProducer.addKeyValueEntryBean("values.to.predict", "1,2,3", "x values to predict, split with ','. Leave blank if none or delete");

    }

    /**
     * Execute the handy tool functionality
     * Performs linear regression analysis on the provided data
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If there is an error during execution
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String inputFile = o.getSimplifiedString("input.tsv.path");
        String xVariable = o.getSimplifiedString("x.variable");
        String yVariable = o.getSimplifiedString("y.variable");

        String outputPath = o.getSimplifiedStringWithDefault("output.tsv.path");

        // Your logic here
        setText4Console(results); // This is how to output to the console
    }

    @Override
    public String getTabName() { return "My Tool"; }

    @Override
    public String getShortDescription() { return "A handy tool"; }

    @Override
    public int[] getCategory() {
        return ModuleClassification.getOneModuleClassification(
            ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
            ModuleClassification.BYAPPLICATION_GENOMICS_INDEX,
            ModuleClassification.BYCOMPLEXITY_LEVEL_1_INDEX,
            ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER
        );
    }
}
```

### 3. Dockable Framework (可停靠子标签框架)

**Base Class**: `DockableTabModuleFaceOfVoice`
**Location**: `egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice`

**Characteristics**:
- Nested as a `sub-tab` within another module
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

    /**
     * Constructor for the SimpleAlignmentSimulator module
     * @param cmf The computational module face
     */
    public SimpleAlignmentSimulator(ComputationalModuleFace cmf) {
        super(cmf);
    }

    /**
     * Define parameters for the alignment simulation
     * @param designer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE designer) {
        designer.addKeyValueEntryBean("num.sequence","5", "Total number of sequence to output, including the reference.");
        // Input like this
    }

    /**
     * Execute the alignment simulation process
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If simulation fails
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String fileName = o.getSimplifiedString("output.file.path");
        String refSequence = o.getSimplifiedString("ref.sequence");
        int numSequence = o.getSimplifiedInt("num.sequence");
        boolean includeIndel = o.getSimplifiedBool("include.indel");

        // Set default mutation probabilities
        double subProb = 0.02;  // Substitution probability
        double insProb = 0.02;  // Insertion probability
        double delProb = 0.02;  // Deletion probability

        // Your logic here

        // Report completion to console
        setText4Console(Arrays.asList("Finished writing file: " + fileName,
                "Mutation log written to: " + logPath));
    }

    @Override
    public String getShortDescription() {
        return "Simulate the short length multiple sequence alignment for demo";
    }
    @Override
    public String getTabName() {
        return "2.Simple alignment Simulator";
    }
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



---

## Advanced Features

### 1. Multiple Examples

Provide multiple numbered examples for users:

```java
    /**
 * Get the number of examples provided by this module
 * @return The number of examples (4)
 */
@Override
protected int getNumberOfExamples() {
    return 4;
}

/**
 * Get the example text for the current example index
 * @return The example text as a string
 */
@Override
protected String getExampleText() {
    String str = null;
    String firstExample = """
            ### For module: Handy tool for biologist # See the right label for help.
            # The tsv file include header line.
            $input.tsv.path=%s
            
            # The x variable values
            $x.variable=X
            
            # The y variable values
            $y.variable=Y
            
            """.formatted(EXAMPLE_PATH);
    if (exampleIndex == 0) {
        str = firstExample;
    } else if (exampleIndex == 1) {
        str = firstExample + """
                # x values to predict, split with ','. Leave blank if none or delete
                $values.to.predict=1,2,3
                """;
    } else if (exampleIndex == 2) {
        str = firstExample + """
                # Optional file, default is to the console
                $output.tsv.path=
                
                # x values to predict, split with ','. Leave blank if none or delete
                $values.to.predict=1,2,3
                """;
    } else if (exampleIndex == 3) {
        String outputPath = EGPSProperties.PROPERTIES_DIR + "/bioData/example/regression/regression.results.tsv";
        str = firstExample + """
                # Optional file, default is to the console
                $output.tsv.path=%s
                
                # x values to predict, split with ','. Leave blank if none or delete
                $values.to.predict=1,2,3
                """.formatted(outputPath);
        return str;
    }
    exampleIndex++;

    if (exampleIndex > 3) {
        exampleIndex = 0;
    }
    return str;
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

### 3. CLI Support

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

## Q & A

1. **Q**: Is Custom Widgets supported?

**A**: It can be done, but it's not recommended. Thinking for why you need the `VOICE` framework, Custom Widgets can be down by 
get rid of the VOICE framework. And write from the scratch.

2. **Q**:  What application can be built with VOICE?

**A**: Any application that requires parameter-driven modules can be built with VOICE.


## Changelog

### v2.1 (2025-12-04)
- **Removed**: 13 deprecated classes (diytools, old template classes)
- **Cleaned**: Package structure now contains only 19 core classes
- **Improved**: Code organization and clarity
- **No Breaking Changes**: All active frameworks still supported
- **Important: Do not support old un-mature API**

---

## Summary

VOICE is a mature, battle-tested framework for building parameter-driven modules in eGPS. By handling GUI, parsing, bookmarks, and persistence, it lets you focus on your module's unique logic.

**Key Takeaways**:
- Choose the right framework (Floating, Handytools, or Dockable)
- Define parameters clearly with good examples
- Implement `execute()` with proper error handling
- Test with bookmarks to ensure good UX

For questions or issues, refer to existing VOICE modules in the codebase or contact the eGPS development team.
