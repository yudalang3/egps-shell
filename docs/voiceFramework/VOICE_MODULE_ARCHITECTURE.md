# VOICE Module Architecture Guide

`VOICE` stands for `Versatile Open Input Click Execute`.

This document describes the current architecture of the VOICE framework inside `egps-shell`.

## 1. What VOICE Provides

VOICE is the shell's parameter-driven GUI framework for modules that need:

- structured text input
- example generation
- bookmark or saved-input handling
- unified execution behavior
- both GUI and command-style usage patterns

The current implementation lives mainly under `src/egps2/builtin/modules/voice/`.

## 2. Main Architectural Pieces

### Base GUI layer

Core classes such as `VersatileOpenInputClickAbstractGuiBase` and `InputAreaPanel` provide the common input-area behavior and surrounding GUI structure.

### Bookmark and state layer

Classes such as `BookmarkDisplayPanel`, `BookmarkOperationPanel`, `BookMarkNode`, and `EditScriptState` manage saved examples, bookmark state, and editing modes.

### Event-processing layer

`EventUniformlyProcessor` centralizes common interaction handling so different VOICE-based modules follow the same execution flow.

### Parameter layer

Classes under `bean/` and `template/` handle parameter modeling, parsing, assignment, and example generation.

### Fast VOICE tab layer

Classes under `fastmodvoice/` support VOICE modules that expose tabbed or dockable sub-views, including `TabModuleFaceOfVoice` and `DockableTabModuleFaceOfVoice`.

## 3. Current Integration Patterns

The current framework supports multiple ways to host VOICE-driven behavior:

- a normal module face
- a tab-oriented VOICE module
- a dockable VOICE module with SubTabs

These are related patterns in one framework, not separate products.

## 4. Why SubTabs Are Treated Differently

SubTabs under `DockableTabModuleFaceOfVoice` are internal parts of a parent module.

Because of that:

- they participate in VOICE composition
- they are not treated as standalone modules by ordinary module discovery

This is why the module-discovery documentation has a dedicated SubTab exclusion rule.

## 5. Current Boundary

This document describes the current VOICE framework as implemented in the shell.

It should not be read as:

- a future roadmap
- a changelog
- a record of how the framework was built step by step

When a behavior is described here, it should be something the current codebase already supports.

## 6. Related Documents

- `VOICE-GUI.md`
- `../module&pluginSystem/module_discovery_subtab_exclusion.md`
