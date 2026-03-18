# 02. VOICE GUI Design Review (Current State)

This document focuses on the GUI-side implementation of VOICE. Using `AbstractGuiBaseVoiceFeaturedPanel` as the typical entry, it explains the shared GUI base `VersatileOpenInputClickAbstractGuiBase`, the three main subpanels (Bookmark / Input / Operation), and the current execution logic behind them.

Related document: `manuals/01VOICE_architecture.md`

The goal here is practical clarity: how the GUI actually boots, how events are routed, how state changes, and especially how bookmark nodes are created, linked, and persisted.

---

## 1. Entry classes and host shape

The GUI core of VOICE is not only `AbstractGuiBaseVoiceFeaturedPanel`. It is built around one shared GUI base:

- `VersatileOpenInputClickAbstractGuiBase`
  - assembles the GUI
  - centralizes event routing
  - manages runnable execution
  - controls dialog lifecycle
  - defines bookmark persistence paths
- `AbstractGuiBaseVoiceFeaturedPanel`
  - a floating-dialog-oriented wrapper over the GUI base
- `TabModuleFaceOfVoice`
  - embeds the same GUI model into a main-frame tab and can add a console area

---

## 2. Current GUI layout

The current layout is organized around three major regions:

- bookmark tree
- main input area
- operation area

In tab-style VOICE modules, a console region may be added below the main interaction area.

This layout is intentionally stable because the framework is trying to keep user interaction predictable across different tools.

---

## 3. Key objects and responsibilities

### 3.1 GUI base: `VersatileOpenInputClickAbstractGuiBase`

This is the central coordinator for GUI assembly and common runtime behavior.

### 3.2 The three subpanels

- `BookmarkDisplayPanel`: bookmark tree display and related popup behavior
- `InputAreaPanel`: parameter text editing and example loading
- `BookmarkOperationPanel`: save/create/link operations and state handling

### 3.3 Unified event processor: `EventUniformlyProcessor`

This processor coordinates cross-panel interaction so VOICE modules follow one consistent event flow.

---

## 4. Two core states

### 4.1 `linkedBookMarkNode`

This represents the bookmark currently linked to the input area.

### 4.2 `EditScriptState`

This state machine describes whether the current script is in an editable, linked, or other execution-relevant state.

Those two states explain much of the visible VOICE interaction behavior.

---

## 5. Typical interaction sequence

### 5.1 Single-click a bookmark

Loads the script into the input area.

### 5.2 Double-click a bookmark

Triggers execution, effectively equivalent to pressing Execute.

### 5.3 Click Execute

Also triggers execution, with the current debounce behavior applied by the framework.

---

## 6. Bookmark-tree persistence and initialization

### 6.1 Initialization flow

`BookmarkDisplayPanel` is responsible for building and initializing the tree at construction time.

### 6.2 Persistence path

Bookmark data is persisted according to the GUI base’s bookmark-path rules, so examples and user-created nodes can be restored later.

---

## 7. Automatic bookmark creation and persistence

The current implementation includes logic for:

- adding new nodes to the bookmark tree
- linking the input area to the relevant bookmark
- saving bookmark state when needed

This is one of the most important parts of VOICE because bookmarks are not only a storage feature. They are also part of the user interaction model and parameter reuse model.

The practical takeaway is:

- bookmark creation is not random GUI decoration
- it is connected to execution flow, linked-state behavior, and persistence behavior

---

## Summary

The current VOICE GUI is not a loose set of widgets. It is a coordinated interaction system:

- one shared GUI base
- three major subpanels
- unified event routing
- explicit state handling for bookmarks and input editing

If you understand those four pieces, the rest of the VOICE GUI code becomes much easier to read.
