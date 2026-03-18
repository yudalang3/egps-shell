# Current Global Font System Statement

This document summarizes what the current global font system already provides in `egps-shell`, and how that behavior takes effect at runtime.

## 1. Current Capabilities

The current system already supports:

- maintaining multiple semantic font groups in `LaunchProperty`
- persisting those settings through `LaunchPropertyDTO`
- applying fonts to `UIManager` during the `Launcher` startup path
- adjusting those fonts by category in `PreferencePanel`
- refreshing the current main UI after the user clicks Apply
- switching FAQ / History help pages according to the current language setting

## 2. Current Runtime Behavior

### 2.1 At startup

- `LaunchProperty.getInstance()` reads existing settings or creates defaults
- `Launcher` calls `launchProperty.applyFontsToUIManager()` before creating the main window
- standard Swing components then inherit those global font settings first

### 2.2 When the user changes settings

- the user changes font settings in Preference
- `PreferencePanel.applyAndClose()` writes the change back to `LaunchProperty`
- `UIManager` is refreshed
- `updateComponentTreeUI(...)` is applied to the current main window
- the result is saved to the JSON config file

## 3. Main UI Areas Covered

The current system mainly covers:

- menus and menu items
- tab titles
- module titles and module body text
- HTML document fonts
- dialog titles, body text, and buttons
- text input controls and text areas
- tables, table headers, lists, and trees
- ToolTips and ToolBars
- common components such as `ProgressBar`, `Slider`, `Spinner`, and `ScrollPane`

## 4. Current Help Integration

The current help system is already connected to the global-font topic:

- `ActionFaq` loads `Faq_English.html` or `Faq_Chinese.html` according to the language setting
- the History dialogs load `History_English.html` or `History_Chinese.html`
- these help pages currently explain the global-font topic rather than acting as the master FAQ for the whole project

## 5. Current Limitations

The current system should not be read as a promise that every screen is automatically unified without exception.

Known boundaries include:

- legacy code that hardcodes fonts may not follow automatically
- third-party components do not necessarily honor `UIManager` completely
- custom-painted text, drawing canvases, and special components still need explicit handling
- some historical help pages or topic pages depend on their own implementation details for font inheritance

## 6. Current Guidance

For shell and module developers, the current recommendation is:

- prefer the existing semantic font accessors in `LaunchProperty`
- avoid introducing new hardcoded fonts unless there is a clear reason
- rely on `UIManager` first for standard Swing components, and override manually only when necessary
- document the font strategy explicitly for topic pages or custom-drawn content

## 7. Related Documents

- `README.md`: entry page for this documentation cluster
- `global_font_system_design.md`: current design goals and boundaries
- `global_font_system_code_map.md`: current code locations and integration points
- `how_to_use_global_preference.md`: how modules and plugins should use the current global preference and font system
