# VOICE Module Architecture Guide

**VOICE** = **V**ersatile **O**pen **I**nput **C**lick **E**xecute

This framework is used in eGPS 2.1+ to build GUI-oriented analysis modules, especially for scientific and bioinformatics workflows. It also supports three practical usage modes: GUI interaction, script-like parameter execution, and optional command-line use.

Developer API package: `egps2.builtin.modules.voice`

---

## 1. What problem does VOICE solve?

VOICE standardizes the common shell around parameter-driven tools. A developer mainly needs to:

- define parameters and defaults
- implement execution logic

The framework then provides:

- text-based parameter input
- example/template generation
- bookmark persistence and reuse
- unified execute/reset/help interaction
- optional console output in tab-based modules
- optional command-line access for batch workflows

---

## 2. Overall architecture

VOICE can be understood as four layers:

1. **hosting layer**: decides whether the module appears as a floating dialog, main-frame tab, or dockable sub-tab
2. **shared GUI layer**: standard layout with bookmark tree, input area, and operation controls
3. **parameter layer**: turns text parameters into structured values
4. **execution layer**: your own analysis or processing logic

### Standard layout

A typical VOICE module exposes:

- bookmark / example tree
- main input area
- operation controls
- optional console area for tab-style modules

---

## 3. Three hosting styles

### A. Floating dialog style (`JDialog`)

Best for lightweight tools that open as standalone dialogs.

### B. Main-window tab style

Best for full modules that should participate in the shell as normal module tabs.

### C. Dockable sub-tab style

Best for grouped tools where several related steps live under one parent module.

Typical workflow examples include:

- comparative-genomics pipelines with several related steps
- transcriptomics workflows combining differential-expression analysis and enrichment analysis

---

## 4. Parameter text and parsing

The core VOICE workflow is still centered on parameter text.

### 4.1 You define parameters

The module defines its expected parameter structure, defaults, and optional examples.

### 4.2 The user edits parameter text

Users work with structured text rather than a completely ad hoc set of controls.

### 4.3 Special markers can help organize complex parameters

For larger tools, the parameter text can still remain readable if grouped and annotated consistently.

### 4.4 Execution reads structured parameters

During execution, your module reads parsed parameters and hands them to its business logic.

---

## 5. Why use VOICE in practice?

VOICE reduces repeated GUI work:

- less repeated shell code
- more consistent interaction across modules
- lower learning cost for users moving from one tool to another
- easier reuse of examples, bookmarks, and execution flow

This matters because many scientific tools follow the same “prepare input -> execute -> inspect output” shape, even when the underlying analysis method differs.

---

## 6. Optional command-line / batch usage

Some VOICE modules can also expose command-line execution, for example through `SubTabModuleRunner`.

That makes the same module more flexible:

- GUI for interactive use
- command-line entry for batch or scripted runs

The command-line side is optional. VOICE remains primarily a GUI framework, but it is designed so modules can extend into scriptable workflows where useful.

---

## Q&A

### What kinds of modules fit VOICE best?

Modules that can be described as “structured parameters in, execution happens, result comes out” are usually a good fit.

### Is VOICE limited to one UI style?

No. The core interaction model is shared, but it can be hosted as a floating dialog, full tab module, or dockable child view.

### Is VOICE the whole module system?

No. VOICE is a framework within the broader shell and module architecture. It standardizes one important class of parameter-driven GUI modules.
