# VOICE GUI Design Specification

This document describes the current GUI design intent and user-facing interaction model for VOICE-based modules in `egps-shell`.

## 1. Current Design Goal

The VOICE GUI is designed to make parameter-driven tools readable and operable from one screen.

The current interface aims to keep these pieces close together:

- input content
- saved examples or bookmarks
- execution controls
- feedback about current editing state

## 2. Current Main Areas

### Input area

The input area is the main workspace for parameter text, editable content, and execution-oriented input.

### Bookmark / example area

The bookmark tree presents reusable examples and user-managed saved inputs. The current GUI distinguishes between system-provided examples and user-owned items.

### Action controls

The GUI exposes actions such as execute, edit, focus, reset, import-related behavior, and other VOICE-specific operations through buttons and surrounding controls.

## 3. Current Interaction Principles

The current VOICE GUI follows these principles:

- examples and user content should be visually distinguishable
- editing state should be visible
- destructive actions should not be presented the same way as safe reusable actions
- execution-related controls should stay close to the input they operate on

## 4. Current State Signals

The current design uses a small set of state cues such as:

- linked vs. not-linked states
- editable vs. read-oriented states
- example content vs. user-managed content

These signals are implemented through VOICE images, labels, and panel-state handling rather than through a separate standalone state engine.

## 5. Current Boundary

This document is about the current GUI specification as reflected by the existing VOICE classes.

It is not a roadmap for hypothetical future widgets, and it should not keep planning sections such as:

- implementation roadmap
- future GUI phases
- step-by-step change logs

## 6. Related Documents

- `VOICE_MODULE_ARCHITECTURE.md`
