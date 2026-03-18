# How to Use the Global Preference and Font System

This document explains how modules and plugins should use the current global preference and font system in `egps-shell`, with a focus on font entry points, practical boundaries, and common integration patterns.

---

## Contents

1. [Quick Start](#quick-start)
2. [Font Categories](#font-categories)
3. [Getting Global Fonts](#getting-global-fonts)
4. [Best Practices](#best-practices)
5. [Common Scenarios](#common-scenarios)
6. [Mistakes to Avoid](#mistakes-to-avoid)
7. [FAQ](#faq)

---

## Quick Start

### Basic Principle

**Recommended**: use the global font configuration.

```java
LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();

JLabel label = new JLabel("Hello");
label.setFont(launchProperty.getLabelFont());
```

**Not recommended**: hardcode fonts.

```java
JLabel label = new JLabel("Hello");
label.setFont(new Font("Arial", Font.PLAIN, 12));
```

### Why use global fonts?

1. **Consistency**: the UI keeps a unified font style across the application
2. **Customizability**: users can adjust fonts to their preference
3. **Accessibility**: larger-font and similar accessibility needs are supported
4. **Maintainability**: font settings are managed centrally

---

## Font Categories

`LaunchProperty` currently exposes 26 font groups.

This document focuses on the ones that modules and plugins touch most often. Fonts for components such as `ProgressBar`, `Slider`, `Spinner`, and `ScrollPane` are usually applied centrally by the shell through `UIManager`.

### 1. Module Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| Module Title | `getDefaultTitleFont()` | module panel title | Bold, 13pt |
| Module Content | `getDefaultFont()` | module body text | Plain, 12pt |
| Document | `getDocumentFont()` | HTML documents and rich text | Plain, 16pt |

Typical usage:

- titles inside `ModuleFace` panels
- regular module text
- document viewers and editors

```java
public class MyModuleFace extends ModuleFace {
    @Override
    protected void initUI() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

        JLabel titleLabel = new JLabel("Module Title");
        titleLabel.setFont(lp.getDefaultTitleFont());

        JLabel contentLabel = new JLabel("Body text");
        contentLabel.setFont(lp.getDefaultFont());

        JTextArea docArea = new JTextArea();
        docArea.setFont(lp.getDocumentFont());
    }
}
```

### 2. Dialog Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| Dialog Title | `getDialogTitleFont()` | dialog titles | Bold, 14pt |
| Dialog Content | `getDialogContentFont()` | message body | Plain, 12pt |
| Dialog Button | `getDialogButtonFont()` | dialog button text | Plain, 12pt |

Typical usage:

- custom `JDialog`
- message panels
- confirmation dialogs

```java
public class MyDialog extends JDialog {
    public MyDialog(Frame owner) {
        super(owner, "My Dialog", true);
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

        JLabel titleLabel = new JLabel("Dialog Title");
        titleLabel.setFont(lp.getDialogTitleFont());

        JLabel messageLabel = new JLabel("Dialog content");
        messageLabel.setFont(lp.getDialogContentFont());

        JButton okButton = new JButton("OK");
        okButton.setFont(lp.getDialogButtonFont());
    }
}
```

### 3. Basic Component Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| Label | `getLabelFont()` | labels | Plain, 12pt |
| Button | `getButtonFont()` | buttons | Plain, 12pt |
| CheckBox | `getCheckBoxFont()` | check boxes and radio buttons | Plain, 12pt |

```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

JLabel nameLabel = new JLabel("Name:");
nameLabel.setFont(lp.getLabelFont());

JButton submitButton = new JButton("Submit");
submitButton.setFont(lp.getButtonFont());

JCheckBox agreeCheckBox = new JCheckBox("I agree");
agreeCheckBox.setFont(lp.getCheckBoxFont());
```

### 4. Input Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| TextField | `getTextFieldFont()` | text fields | Plain, 12pt |
| TextArea | `getTextAreaFont()` | text areas | Monospaced, 12pt |
| ComboBox | `getComboBoxFont()` | combo boxes | Plain, 12pt |

```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

JTextField nameField = new JTextField(20);
nameField.setFont(lp.getTextFieldFont());

JTextArea codeArea = new JTextArea(10, 40);
codeArea.setFont(lp.getTextAreaFont());

JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Type A", "Type B"});
typeCombo.setFont(lp.getComboBoxFont());
```

### 5. Data Display Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| Table | `getTableFont()` | table cells | Plain, 11pt |
| Table Header | `getTableHeaderFont()` | table headers | Bold, 12pt |
| List | `getListFont()` | list items | Plain, 12pt |
| Tree | `getTreeFont()` | tree nodes | Plain, 12pt |

```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

JTable table = new JTable(data, columns);
table.setFont(lp.getTableFont());
table.getTableHeader().setFont(lp.getTableHeaderFont());

JList<String> fileList = new JList<>(files);
fileList.setFont(lp.getListFont());

JTree navTree = new JTree(rootNode);
navTree.setFont(lp.getTreeFont());
```

### 6. Tool Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| ToolTip | `getToolTipFont()` | tooltips | Plain, 11pt |
| ToolBar | `getToolBarFont()` | toolbar labels | Plain, 11pt |

```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

JButton button = new JButton("Save");
button.setToolTipText("Save current file");

JToolBar toolBar = new JToolBar();
JLabel toolbarLabel = new JLabel("Tools:");
toolbarLabel.setFont(lp.getToolBarFont());
toolBar.add(toolbarLabel);
```

### 7. Menu Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| First Level Menu | `getMenuFistLevelFont()` | main menu bar | Plain, 12pt |
| Second Level Menu | `getMenuSecondLevelFont()` | drop-down menu items | Plain, 12pt |

### 8. Tab Fonts

| Font | Getter | Use | Default |
| --- | --- | --- | --- |
| Selected Tab | `getSelectedTabTitleFont()` | selected tab title | Bold, 12pt |
| Unselected Tab | `getUnSelectedTabTitleFont()` | unselected tab title | Plain, 12pt |

---

## Getting Global Fonts

### Method 1: via `UnifiedAccessPoint` (recommended)

```java
import egps2.UnifiedAccessPoint;
import egps2.LaunchProperty;

public class MyModule {
    public void setupUI() {
        LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();

        JLabel label = new JLabel("Hello");
        label.setFont(launchProperty.getLabelFont());
    }
}
```

### Method 2: cache the reference when reuse is frequent

```java
public class MyModuleFace extends ModuleFace {
    private final LaunchProperty launchProperty;

    public MyModuleFace() {
        this.launchProperty = UnifiedAccessPoint.getLaunchProperty();
    }

    @Override
    protected void initUI() {
        JLabel label = new JLabel("Title");
        label.setFont(launchProperty.getDefaultTitleFont());
    }
}
```

### Method 3: rely on `UIManager` where automatic inheritance is enough

For many standard Swing components, fonts are already applied through `UIManager` after the shell calls `applyFontsToUIManager()`, so manual `setFont(...)` is not always necessary.

```java
JLabel label = new JLabel("Uses labelFont automatically");
JButton button = new JButton("Uses buttonFont automatically");
JTextField field = new JTextField("Uses textFieldFont automatically");
```

This does not mean every component updates automatically. Custom painting, third-party widgets, or already-created components may still need manual handling.

---

## Best Practices

### 1. Let the shell's `UIManager` setup work first

If the component is a standard Swing component, prefer automatic inheritance:

```java
JButton button = new JButton("OK");
```

Only set the font manually when you truly need to override or clarify the semantic choice.

### 2. Set fonts explicitly on custom components

```java
public class MyCustomPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultFont());
        g.drawString("Custom Text", 10, 20);
    }
}
```

### 3. Choose fonts by semantic role

```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

JLabel titleLabel = new JLabel("Configuration");
titleLabel.setFont(lp.getDefaultTitleFont());

JLabel descLabel = new JLabel("Choose a setting");
descLabel.setFont(lp.getDefaultFont());

JTextArea logArea = new JTextArea();
logArea.setFont(lp.getTextAreaFont());

JTable dataTable = new JTable();
dataTable.setFont(lp.getTableFont());
```

### 4. Avoid mixing sizes arbitrarily

Keep similar components consistent unless there is a clear semantic reason to differ.

### 5. Handle runtime font changes when needed

```java
public class MyModuleFace extends ModuleFace {
    private JLabel titleLabel;
    private JLabel contentLabel;

    @Override
    protected void initUI() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

        titleLabel = new JLabel("Title");
        titleLabel.setFont(lp.getDefaultTitleFont());

        contentLabel = new JLabel("Content");
        contentLabel.setFont(lp.getDefaultFont());
    }

    public void onFontChanged() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
        titleLabel.setFont(lp.getDefaultTitleFont());
        contentLabel.setFont(lp.getDefaultFont());
        SwingUtilities.updateComponentTreeUI(this);
    }
}
```

---

## Common Scenarios

### Scenario 1: settings dialog

Use dialog fonts for dialog titles, content, and buttons; use label and text-field fonts for the form body.

### Scenario 2: data viewer

Use `tableFont` / `tableHeaderFont` for tables and `textAreaFont` for detailed logs or source-like text.

### Scenario 3: toolbar panel

Use `toolBarFont` for toolbar labels and `buttonFont` for toolbar buttons.

### Scenario 4: file browser

Use `treeFont` for navigation trees and `listFont` for file lists.

---

## Mistakes to Avoid

### Mistake 1: hardcoding fonts

```java
JLabel label = new JLabel("Title");
label.setFont(new Font("Arial", Font.BOLD, 14));
```

Prefer:

```java
JLabel label = new JLabel("Title");
label.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont());
```

### Mistake 2: ignoring semantic roles

Do not use a table font for dialog content, or a title font for log output. Choose the semantic role that matches the component.

### Mistake 3: repeated scattered access with no local organization

Repeated calls to `getLaunchProperty()` do not create new instances, but a local reference is often clearer and easier to maintain.

### Mistake 4: taking font values too early in static initialization

Avoid freezing a runtime font snapshot into static fields too early. It is safer to read the font inside constructors or instance methods.

### Mistake 5: mixing different font sources

Do not mix `UIManager`, hardcoded fonts, and `LaunchProperty` fonts randomly inside the same UI.

---

## FAQ

### Q1: Which font should my module use?

Use the font that matches the semantic role:

- title -> `getDefaultTitleFont()`
- regular text -> `getDefaultFont()`
- dialog content -> `getDialogContentFont()`
- table data -> `getTableFont()`
- code/log text -> `getTextAreaFont()`

### Q2: Are fonts applied automatically to every component?

No. Many standard Swing components inherit from `UIManager`, but custom components, custom painting, and some third-party widgets may still need explicit handling.

### Q3: Will my module update automatically after the user changes fonts?

Only partly:

- newly created components typically use the new `UIManager` fonts
- existing Swing components usually need a UI tree refresh
- custom-drawn text still needs your own repaint or font reset logic

### Q4: Can I create my own font?

It is usually better to derive from a global font rather than invent a disconnected one.

### Q5: How should I handle Chinese or other non-Latin text?

The current fallback chain usually works in common Chinese environments, but actual rendering should still be tested on the real runtime fonts available in the environment.

### Q6: Why are table-header fonts different from table-body fonts?

That is intentional. Table headers use a stronger semantic font than normal cell content.

### Q7: What if `LaunchProperty` is not initialized yet when my module loads?

`UnifiedAccessPoint.getLaunchProperty()` initializes on demand, but from a maintenance perspective it is still better to use it from `ModuleFace.initUI()` or later rather than freezing values during very early static initialization.

### Q8: How do I test my module under different fonts?

1. run the application
2. open the current Preference panel
3. modify the relevant fonts
4. click Apply and inspect the result
5. reload your module if needed

---

## Further Reading

- [Global Font System Design and Boundaries](./global_font_system_design.md)
- [Current Global Font System Statement](./global_font_system_statement.md)
- [Global Font System Code Map](./global_font_system_code_map.md)
- [Global Preference Documentation Entry](./README.md)
- [eGPS Module Development Manual](../../manuals/module_plugin_course/)
