# eGPS Module Development Quick Start

## Overview

This tutorial helps you create and run your first eGPS module in about five minutes.

## Learning goals

After this quick start, you should be able to:

- understand the two deployment modes for eGPS modules
- generate example plugins with the automation script
- install and run a plugin successfully
- recognize the basic module structure

## Prerequisites

- basic Java knowledge
- comfort with command-line usage
- a compiled eGPS project (`out/production/egps-main.gui` exists)

## Method 1: use the automation script (recommended)

### Step 1: run the generator script

```bash
cd /path/to/egps-main.gui
bash docs/module_plugin_course/create-all-test-plugins.sh
```

### Step 2: inspect generated files

The script creates a `plug_dist` directory containing example plugin JARs, including simpler and more complex plugin styles.

### Step 3: choose a deployment mode

#### Option A: deploy as an external plugin

Copy the JAR into `~/.egps2/config/plugin/`.

#### Option B: deploy as a built-in module

Copy the JAR into `dependency-egps/`.

### Step 4: launch eGPS

Start the shell normally after the JAR is in place.

### Step 5: inspect the result

Use Module Gallery or the relevant menu entry to confirm that the module is visible and loadable.

## Understand the generated plugins

Typical generated examples include:

- a `FastBaseTemplate` plugin for simple tools
- a direct `IModuleLoader` plugin for more structured cases

## Compare the two deployment modes

At the module-code level, the same implementation can often be used for both modes. The main runtime differences are where the JAR is placed, how it enters the shell, and how it is presented.

## Customize a plugin

After generation, common edits include:

- changing the module name or description
- changing the category
- adding real business buttons or actions

## JAR structure

A typical plugin JAR includes:

- compiled classes
- `eGPS2.plugin.properties`
- optional resource files
- optional dependent JARs stored alongside the main plugin JAR

## Common questions

### The plugin does not appear in Module Gallery

Check whether the module is only discovered or also activated. The current Gallery follows the active loader set.

### The plugin failed to load

Verify `launchClass`, class packaging, and runtime dependencies.

### I want to convert a Plugin to Built-in

In many cases, moving the JAR to `dependency-egps/` is enough, but runtime behavior still depends on the current loader and config state.

### I want to convert a Built-in module to Plugin

Moving the JAR into `~/.egps2/config/plugin/` is the usual starting point, then verify the plugin-entry behavior.

## Next step

Continue with:

- `02_PLUGIN_DEVELOPMENT.md` if you want to build plugins
- `03_BUILTIN_DEVELOPMENT.md` if you want built-in deployment
- `04_ARCHITECTURE.md` if you want the deeper technical model
