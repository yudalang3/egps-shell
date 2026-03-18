# 03. VOICE Bookmark Reuse Feature Design

**Feature summary**: on the first open, a VOICE GUI creates a new bookmark node. On later opens in the same session, it reuses the last bookmark node instead of creating a new temporary node every time.

**Scope**: floating-dialog mode only (`AbstractGuiBaseVoiceFeaturedPanel` and its subclasses)

Related documents:

- `manuals/01VOICE_architecture.md`
- `manuals/02_VOICE_GUI_design.md`

---

## 1. Requirement analysis

### 1.1 Previous behavior

Each time the VOICE GUI dialog opened, a new bookmark node was created automatically. Repeated use left the bookmark tree full of temporary nodes.

### 1.2 Expected behavior

The first open creates the bookmark. Later opens in the same session should reuse it instead of creating a new one each time.

The goal is a cleaner bookmark tree and less repetitive temporary data.

---

## 2. Technical design

### 2.1 Core challenges

The main challenges are:

- dialog lifecycle does not naturally match bookmark-node lifecycle
- bookmark identity must survive reuse within one session
- user interaction still needs to take priority over automatic reuse

### 2.2 Reuse logic

The design can be understood as three steps:

1. store the bookmark identity when execution happens
2. attempt reuse during initialization
3. search for the matching node in the current tree

### 2.3 Priority and interaction constraints

The system should not override explicit user choice. Automatic reuse is only the default path when nothing else has already been selected.

### 2.4 Boundary handling

The design also needs to consider:

- bookmark deleted by the user
- multiple bookmarks with the same name
- bookmark content changed by the user
- state-machine constraints

---

## 3. Visual feedback

The feature should make reuse behavior understandable to the user rather than invisible and confusing.

That includes clear dialog-title behavior and state hints when reuse is happening.

---

## 4. Implementation steps

The design breaks down into a small set of implementation steps:

1. add memory fields
2. save bookmark identity when needed
3. find matching nodes during reuse
4. reuse the node during initialization
5. optionally update dialog title
6. verify through focused tests

---

## 5. Test cases

Typical tests include:

- first open: create a new bookmark
- first execution: save the bookmark identity
- second open: reuse the bookmark
- repeated use: keep reusing the same bookmark
- explicit user selection: user choice wins
- module close: session memory is cleared
- deleted node: reuse falls back safely
- exact-match and fallback-match cases

---

## 6. Summary

The bookmark reuse feature is valuable because it:

- keeps the bookmark tree cleaner
- reduces repeated temporary-node creation
- matches user expectations better in repeated floating-dialog workflows

The design is intentionally session-scoped, not permanent. That keeps the feature practical without turning it into a more invasive persistent-behavior system.

### Remaining limits

- it is still sensitive to how bookmark identity is matched
- deleted or renamed bookmarks require safe fallback behavior
- the feature applies only to the intended VOICE host mode

---

## Appendix: file-location quick reference

When implementing or reviewing this feature, focus on:

- floating-dialog VOICE host classes
- bookmark tree display classes
- bookmark operation classes
- linked bookmark state and edit-state handling

## Change log

This document reflects the corrected design direction for session-scoped bookmark reuse rather than permanent global bookmark memory.
