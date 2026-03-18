# How to Use Message Dialogs

Use `SwingDialog` for displaying messages to users. Internally uses `ModernDialog` with HTML 3.2 + JEditorPane + JScrollPane.

**Location:** `egps2/panels/dialog/SwingDialog.java`

## API Overview

| Type    | Without owner                    | With owner                              |
|---------|----------------------------------|-----------------------------------------|
| Info    | `showInfoMSGDialog(title, msg)`    | `showInfoMSGDialog(owner, title, msg)`    |
| Success | `showSuccessMSGDialog(title, msg)` | `showSuccessMSGDialog(owner, title, msg)` |
| Warning | `showWarningMSGDialog(title, msg)` | `showWarningMSGDialog(owner, title, msg)` |
| Error   | `showErrorMSGDialog(title, msg)`   | `showErrorMSGDialog(owner, title, msg)`   |
| Confirm | `showConfirmDialog(title, msg)`    | `showConfirmDialog(owner, title, msg)`    |
| Generic | `showMSGDialog(title, msg, type)`  | `showMSGDialog(owner, title, msg, type)`  |

## Basic Usage

### Info Dialog
```java
SwingDialog.showInfoMSGDialog("Information", "Operation completed.");
```

### Success Dialog
```java
SwingDialog.showSuccessMSGDialog("Success", "File saved successfully!");
```

### Warning Dialog
```java
SwingDialog.showWarningMSGDialog("Warning", "This may take a while.");
```

### Error Dialog
```java
SwingDialog.showErrorMSGDialog("Error", "Something went wrong.");
```

### With Custom Parent Window
```java
// Pass owner window as first parameter
SwingDialog.showInfoMSGDialog(myFrame, "Title", "Message");
SwingDialog.showErrorMSGDialog(myDialog, "Error", "Failed!");
```

## Confirm Dialog (Blocking)

Returns `true` if user clicked "Yes", `false` otherwise.

```java
boolean confirmed = SwingDialog.showConfirmDialog("Delete?", "Are you sure you want to delete this file?");
if (confirmed) {
    // User clicked Yes
    deleteFile();
} else {
    // User clicked No or closed dialog
}
```

With owner:
```java
boolean confirmed = SwingDialog.showConfirmDialog(myFrame, "Delete?", "Are you sure?");
```

## Custom Dialog with Builder

For more control, use the builder pattern.

### Basic Builder Usage
```java
SwingDialog.builder()
    .type(ModernDialogType.WARNING)
    .title("Unsaved Changes")
    .message("Do you want to save before closing?")
    .primaryButton("Save", e -> saveFile())
    .secondaryButton("Discard", e -> discardChanges())
    .show();
```

### Getting User Selection with Builder

**Method 1: Using callbacks (recommended for simple actions)**
```java
SwingDialog.builder()
    .type(ModernDialogType.QUESTION)
    .title("Export Format")
    .message("Choose your export format:")
    .primaryButton("PDF", e -> exportAsPDF())
    .secondaryButton("CSV", e -> exportAsCSV())
    .show();
```

**Method 2: Using `showAndGetResult()` for checking result**
```java
ModernDialog dialog = SwingDialog.builder()
    .type(ModernDialogType.QUESTION)
    .title("Confirm Action")
    .message("Do you want to proceed?")
    .yesNoButtons()
    .build();

ModernDialog.DialogResult result = dialog.showAndGetResult();

switch (result) {
    case YES:
        // User clicked Yes
        break;
    case NO:
        // User clicked No
        break;
    case CLOSED:
        // User closed dialog without clicking a button
        break;
}
```

**Method 3: Custom buttons with result checking**
```java
ModernDialog dialog = SwingDialog.builder()
    .type(ModernDialogType.WARNING)
    .title("Save Changes?")
    .message("You have unsaved changes.")
    .primaryButton("Save", null)      // DialogResult.OK
    .secondaryButton("Discard", null) // DialogResult.CANCEL
    .build();

ModernDialog.DialogResult result = dialog.showAndGetResult();

if (result == ModernDialog.DialogResult.OK) {
    saveChanges();
} else if (result == ModernDialog.DialogResult.CANCEL) {
    discardChanges();
}
// CLOSED means user closed without clicking either button
```

### Builder Methods Reference

| Method | Description |
|--------|-------------|
| `.type(ModernDialogType)` | Set dialog type: `INFO`, `SUCCESS`, `ERROR`, `WARNING`, `QUESTION` |
| `.title(String)` | Set dialog title |
| `.message(String)` | Set message content (supports `\n` for newlines) |
| `.owner(Window)` | Set parent window |
| `.okButton()` | Add "OK" button (returns `DialogResult.OK`) |
| `.okButton(ActionListener)` | Add "OK" button with callback |
| `.yesNoButtons()` | Add "Yes" and "No" buttons |
| `.primaryButton(text, action)` | Add primary button (returns `DialogResult.OK`) |
| `.secondaryButton(text, action)` | Add secondary button (returns `DialogResult.CANCEL`) |
| `.build()` | Build dialog without showing |
| `.show()` | Build and show dialog |

### DialogResult Values

| Value | Meaning |
|-------|---------|
| `OK` | User clicked OK or primary button |
| `YES` | User clicked Yes |
| `NO` | User clicked No |
| `CANCEL` | User clicked Cancel or secondary button |
| `CLOSED` | User closed dialog without clicking a button (X button or ESC) |

## Dialog Types

| Type | Icon Color | Use Case |
|------|------------|----------|
| `INFO` | Blue | General information |
| `SUCCESS` | Green | Operation completed successfully |
| `WARNING` | Yellow/Orange | Caution or potential issues |
| `ERROR` | Red | Error or failure |
| `QUESTION` | Purple | Asking user to make a choice |

## Multi-line Messages

Use `\n` for line breaks:
```java
SwingDialog.showInfoMSGDialog("Results",
    "Found 10 items.\nProcessed: 8\nSkipped: 2");
```

## Convenience Methods

```java
// Show success dialog for export completion (uses i18n strings)
SwingDialog.successExportDataDialog();

// Show success dialog for action completion (uses i18n strings)
SwingDialog.successFinishActionDialog();
```

## Note

- All `show*MSGDialog` methods are **non-blocking** (use `SwingUtilities.invokeLater`)
- `showConfirmDialog` is **blocking** (waits for user response)
- Builder's `.show()` is **blocking**, `.build()` returns dialog for manual control
