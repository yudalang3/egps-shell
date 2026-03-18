# Global Font System Design and Boundaries

This document explains the design goals, actual scope, and current boundaries of the global font system in `egps-shell`.

## 1. Design Goals

The current global font system is designed to:

- provide semantic font entry points for common Swing components in the shell
- let users adjust those fonts by category in Preference
- persist the current settings and restore them on the next launch
- let most standard Swing components inherit those settings through `UIManager`

## 2. Current Font Grouping

`LaunchProperty` currently maintains multiple font groups by use case, including:

- menu fonts
- tab fonts
- module title and body fonts
- document fonts
- dialog fonts
- basic component fonts
- input-component fonts
- data-display fonts
- tooltip and toolbar fonts
- fonts for progress bars, sliders, spinners, scroll containers, and similar components

The purpose of these groups is not to maximize the number of font entries, but to give stable semantic entry points to the most common UI positions.

## 3. Current Application Path

The current application path is:

1. `LaunchProperty` stores the font objects for each category
2. `Launcher` calls `applyFontsToUIManager()` before creating the main UI
3. `PreferencePanel.applyAndClose()`:
   - reads values from the preference subpanels
   - updates `LaunchProperty`
   - calls `applyFontsToUIManager()` again
   - refreshes the current main UI through `SwingUtilities.updateComponentTreeUI(...)`
   - writes the settings back to `defaultGlobalProperties.json`

## 4. Current Persistence Path

The current font settings are serialized through `LaunchPropertyDTO` and saved to:

- `~/.egps/json/defaultGlobalProperties.json`

The current implementation still uses that historical path, so the documentation should not silently rewrite it to a different location.

## 5. Current Preference Entry

Users mainly adjust fonts through `File -> Preference -> Appearance`.

`PreferencePanel` currently provides panels or entry points for:

- module fonts
- menu fonts
- tab fonts
- document fonts
- dialog fonts
- basic component fonts
- input-component fonts
- data-display fonts
- neighboring appearance settings such as icon size

## 6. Current System Boundary

The current system should be understood with the following boundaries:

- it primarily covers standard Swing components and shell-managed common UI
- third-party components, hardcoded legacy fonts, or self-painted text may still require manual handling
- `UIManager` covers a large amount of default component behavior, but it cannot guarantee seamless inheritance for every historical screen
- text areas intentionally default to a monospaced font in the current implementation

## 7. Impact on Module Developers

If a module wants to stay aligned with the current global font system, it is recommended to:

- obtain semantic fonts through `UnifiedAccessPoint.getLaunchProperty()`
- avoid hardcoding fonts for standard Swing components
- explicitly choose the matching semantic font for custom-drawn text or special components

## 8. Related Documents

- `README.md`: directory entry and reading order
- `global_font_system_statement.md`: current runtime behavior
- `global_font_system_code_map.md`: current code locations and integration points
- `how_to_use_global_preference.md`: how modules and plugins should use these font entry points
- `global_font_system_code_map.md`: FAQ / History integration points and maintenance rules for this subsystem
