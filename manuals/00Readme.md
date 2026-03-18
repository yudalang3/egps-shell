# eGPS 2.1: Modular Design and Development Practice

> Welcome to the eGPS module manual.  
> This tutorial explains the design ideas behind the software, the evolution of the framework, and the practical path for building custom modules and plugins.

This document starts from the problem the framework is trying to solve, then walks through the shift from eGPS 1 to eGPS 2, and from 2.0 to 2.1.

The goal is not only to record what was built, but to answer why the architecture was shaped this way: when scientific desktop software grows from a personal tool into a long-lived team-maintained platform, which pains are common, which parts can be abstracted, and which framework decisions solve them once instead of repeatedly.

For broader background, see the review article in *Hereditas*: https://www.chinagene.cn/CN/10.16288/j.yczz.24-254

Major shifts:

- `eGPS 1 -> eGPS 2`: modularization, significant GUI refinement, and the introduction of the VOICE execution model
- `eGPS 2.0 -> eGPS 2.1`: less emphasis on rebuilding every core algorithm or data structure from scratch, and more emphasis on integrating mature third-party libraries while focusing on method orchestration and usability

---

## 30-second overview: what is this and why use it?

**What it is**: a modular desktop analysis-platform framework for scientific workflows. Each analysis capability exists as a module. A module follows a common declaration and runtime contract, and can be shipped either as built-in functionality or as an external plugin.

**Why it matters**: for scientific software, the real long-term cost is not writing one analysis that works once. The cost is keeping tools maintainable, reproducible, and extensible while many people collaborate, new methods keep arriving, and the software continues evolving. A modular framework moves “host integration, consistent interaction, distribution, plugin extension” into reusable rules so developers can focus on methods and data instead of shell plumbing.

---

## 1. What problem are we trying to solve?

If you have maintained a continuously growing desktop application, the pattern is familiar:

- features keep accumulating and the main project turns into a monolith
- every new feature must be connected manually to menus, toolbars, windows, import/export flows, and parameter persistence
- there is no clear team boundary, so everyone can modify the main frame directly
- plugin support sounds attractive until class loading, installation paths, duplicate modules, and user configuration all have to be designed from zero

eGPS came out of bioinformatics practice, but the reusable result is broader: a general desktop-platform skeleton based on “module contract + unified entry + automatic discovery + plugin isolation”.

---

## 2. What kinds of modules do we actually need?

The framework was not built only as a command-line library. A major goal is to support non-programmer users, so the module model must work well for GUI-driven workflows.

From a usage perspective, there are two large groups:

- command-line modules
- GUI modules

GUI modules matter especially in scientific practice because:

- end users often need visual workflows more than pure scripting
- the most expensive part is often turning an algorithm into a usable and repeatable analysis experience
- a large all-in-one application quickly becomes bloated, so modular delivery is preferable

---

## 3. What makes a module “general”?

The point of generality is consistency:

- users should get a predictable interaction model
- developers should know what a module must provide
- the host shell should know how to load, display, and manage modules consistently

For GUI modules in particular, common needs include:

1. users need a way to discover what the module does
2. users need a place to input parameters or data
3. users need a consistent execute interaction
4. results need to be inspectable or exportable
5. modules need predictable open/close/restore behavior

Scientific workflows often connect modules in sequence, so a unified platform and unified runtime model make those workflows easier to build and easier to maintain.

---

## 4. How should a general module framework be designed?

The core design principles are:

- all modules should follow a common standard contract
- the shell should own consistency
- each module should focus on its own method and interface
- extension mechanisms should let new capabilities be added as modules rather than scattered edits to the main program

From the perspective of many scientific GUI tasks, a common pattern appears again and again:

- open a module
- provide structured input
- click to execute

That recurring pattern became the basis of the VOICE model:

`Versatile Open Input Click Execute`

VOICE is not the whole platform, but it is the main reusable interaction model for parameter-driven GUI modules.

---

## 5. How is the framework actually implemented?

The current implementation centers on:

- a **module contract**: modules declare metadata such as name, description, category, and UI entity
- a **unified loading entry**: the shell opens modules through a common loading path
- **automatic discovery + plugin support**: built-in modules and plugin JARs can both be discovered and tracked

In practice, this means:

1. users locate modules through Module Gallery or other shell entry points
2. developers can distribute new functionality as plugins rather than rebuilding the whole application
3. the shell can manage loading, display, and runtime integration consistently

---

## 6. Quick practical takeaway

If you are using the framework rather than extending it, two ideas matter most:

1. find and open modules through the shell’s standard entry points
2. package new functionality as modules or plugins instead of wiring it directly into the shell each time

If you are starting from the development side, the next best step is to enter the tutorial track under `manuals/module_plugin_course/`.

---

## 7. Summary and outlook

The key value of the framework is not only “modularity” as a slogan. It is the reduction of repeated shell work:

- modules become units that can be discovered, described, installed, and reused
- a unified module contract lowers collaboration cost
- plugin delivery lowers the cost of sharing new tools

The next step is not to make the shell do everything, but to keep the framework boundary clean enough that new methods can be integrated without turning the main application back into a monolith.
