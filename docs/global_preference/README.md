# eGPS Global Preference and Font Documentation

This directory contains the reader-facing documentation set for the current global preference and font system in `egps-shell`.

It is not the README for the whole repository. Its job is to explain what this subdirectory documents, where the current entry points are, and which files to read first.

## What This Directory Covers

The documents here focus on:

- current global font configuration behavior
- preference-panel entry points related to fonts
- developer guidance for using the font system correctly
- bilingual FAQ / History pages for this topic
- relevant code locations and maintenance notes for this subsystem

## Document Map

- `README.md`: directory entry and reading order
- `global_font_system_statement.md`: current font-system behavior and runtime flow
- `global_font_system_design.md`: current design goals and system boundaries
- `global_font_system_code_map.md`: relevant code locations, integration points, and maintenance pitfalls
- `how_to_use_global_preference.md`: guidance for modules and plugins to use the current global preference and font system correctly

## Recommended Reading Order

If you want the fastest path into this subsystem, read:

1. `global_font_system_statement.md`
2. `global_font_system_design.md`
3. `global_font_system_code_map.md`
4. `how_to_use_global_preference.md`

## Main Runtime Basis

The current runtime behavior described in this directory is primarily implemented by:

- `LaunchProperty`
- `LaunchPropertyDTO`
- `Launcher`
- `PreferencePanel`
- `ActionFaq`
- `HistoryJTreeDialogEnglish`
- `HistoryJTreeDialogChinese`

## Common Pitfalls

- Adding a new font field without mapping it in `applyFontsToUIManager()`
- Updating the preference UI without saving through `LaunchPropertyDTO`
- Assuming `UIManager` will fix every hardcoded or custom-drawn font automatically
- Updating only one language version of FAQ / History pages

## FAQ / History Help Pages

This topic is also exposed through bilingual Help content:

- `ActionFaq` loads `Faq_English.html` or `Faq_Chinese.html` according to the current language setting
- `HistoryJTreeDialogEnglish` loads `History_English.html`
- `HistoryJTreeDialogChinese` loads `History_Chinese.html`

When updating these pages, keep English and Chinese content aligned on the same current behavior.

## Relation to the Main docs Index

This directory is a specialized documentation cluster under `docs/`.

Use the top-level `docs/README_TableOfContents.md` or `docs/README_TableOfContents_zh.md` when you need the broader documentation map for `egps-shell`.
