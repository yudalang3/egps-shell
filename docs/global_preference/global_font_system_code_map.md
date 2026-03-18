# Global Font System Code Map

This document keeps only the stable information that is useful to readers: which code locations currently matter to the global font system, what each of them is responsible for, and which maintenance pitfalls are common.

## 1. Main Code Locations

| Location | Current responsibility |
| --- | --- |
| `src/egps2/LaunchProperty.java` | stores global fonts, provides defaults, and applies fonts to `UIManager` |
| `src/egps2/utils/LaunchPropertyDTO.java` | serializes font settings to JSON |
| `src/egps2/Launcher.java` | applies global fonts before the main UI is created |
| `src/egps2/PreferencePanel.java` | provides the font-setting UI, applies changes, and refreshes the interface |
| `src/egps2/panels/pref/` | contains the per-category font-setting panels |
| `src/egps2/frame/ActionFaq.java` | loads the language-specific FAQ page |
| `src/egps2/frame/html/HistoryJTreeDialogEnglish.java` | English History entry |
| `src/egps2/frame/html/HistoryJTreeDialogChinese.java` | Chinese History entry |
| `src/egps2/frame/html/Faq_English.html` | English FAQ page |
| `src/egps2/frame/html/Faq_Chinese.html` | Chinese FAQ page |
| `src/egps2/frame/html/History_English.html` | English History page |
| `src/egps2/frame/html/History_Chinese.html` | Chinese History page |

## 2. Current Integration Relationship

The current global font system is not implemented by a single class. It is composed by several layers:

- `LaunchProperty` defines semantic font roles and defaults
- `Launcher` injects those fonts into `UIManager` before the main window is created
- `PreferencePanel` gives users an adjustment entry and reapplies the change to the live UI
- FAQ / History help pages explain this topic to users

## 3. FAQ / History Help Entry Points

Current help entry points are:

- `ActionFaq` chooses `Faq_English.html` or `Faq_Chinese.html` according to `UnifiedAccessPoint.getLaunchProperty().isEnglish()`
- `HistoryJTreeDialogEnglish` loads `History_English.html`
- `HistoryJTreeDialogChinese` loads `History_Chinese.html`

External-facing explanations should use those current pages as the effective help entry points.

## 4. Common Maintenance Pitfalls

### 4.1 Updating `LaunchProperty` without updating `UIManager`

If a new font field is added but not mapped inside `applyFontsToUIManager()`, users may be able to configure it but standard Swing components will not actually use it.

### 4.2 Updating `PreferencePanel` without persistence

If the UI allows users to change a font but the result is not written back correctly through `LaunchProperty` or `LaunchPropertyDTO`, the setting disappears on the next launch.

### 4.3 Expecting `UIManager` to fix everything

`UIManager` is very effective for standard components, but it is not sufficient for:

- custom-drawn text
- legacy hardcoded fonts
- third-party components
- HTML pages that define their own styles

### 4.4 Forgetting bilingual help-page synchronization

FAQ and History both have English and Chinese versions. Updating only one side creates inconsistent documentation between languages.

### 4.5 Referring to outdated document roles in HTML help pages

If FAQ / History pages mention files under `docs/`, they should use the current document-role names rather than old drafting labels such as “summary”, “report”, or “plan”.

## 5. Recommended Reading Order

If you want to understand this topic efficiently, read in this order:

1. `README.md`
2. `global_font_system_statement.md`
3. `global_font_system_design.md`
4. `global_font_system_code_map.md`
5. `how_to_use_global_preference.md`

## 6. Division of Labor with the README

`README.md` is responsible for the directory entry, reading order, and maintenance reminders.  
This document is more specifically about which files currently carry the key responsibilities of this subsystem.
