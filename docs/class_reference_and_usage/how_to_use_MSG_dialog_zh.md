# 如何使用消息对话框

使用 `SwingDialog` 向用户显示消息。其内部基于 `ModernDialog + HTML 3.2 + JEditorPane + JScrollPane`。

**位置：** `egps2/panels/dialog/SwingDialog.java`

## API 总览

| 类型 | 无 owner 版本 | 带 owner 版本 |
| --- | --- | --- |
| Info | `showInfoMSGDialog(title, msg)` | `showInfoMSGDialog(owner, title, msg)` |
| Success | `showSuccessMSGDialog(title, msg)` | `showSuccessMSGDialog(owner, title, msg)` |
| Warning | `showWarningMSGDialog(title, msg)` | `showWarningMSGDialog(owner, title, msg)` |
| Error | `showErrorMSGDialog(title, msg)` | `showErrorMSGDialog(owner, title, msg)` |
| Confirm | `showConfirmDialog(title, msg)` | `showConfirmDialog(owner, title, msg)` |
| Generic | `showMSGDialog(title, msg, type)` | `showMSGDialog(owner, title, msg, type)` |

## 基本用法

### 信息对话框
```java
SwingDialog.showInfoMSGDialog("Information", "Operation completed.");
```

### 成功对话框
```java
SwingDialog.showSuccessMSGDialog("Success", "File saved successfully!");
```

### 警告对话框
```java
SwingDialog.showWarningMSGDialog("Warning", "This may take a while.");
```

### 错误对话框
```java
SwingDialog.showErrorMSGDialog("Error", "Something went wrong.");
```

### 指定父窗口
```java
SwingDialog.showInfoMSGDialog(myFrame, "Title", "Message");
SwingDialog.showErrorMSGDialog(myDialog, "Error", "Failed!");
```

## 确认对话框（阻塞）

如果用户点击 “Yes” 返回 `true`，否则返回 `false`。

```java
boolean confirmed = SwingDialog.showConfirmDialog("Delete?", "Are you sure you want to delete this file?");
if (confirmed) {
    deleteFile();
} else {
    // User clicked No or closed dialog
}
```

带 owner 的写法：

```java
boolean confirmed = SwingDialog.showConfirmDialog(myFrame, "Delete?", "Are you sure?");
```

## 使用 Builder 创建自定义对话框

如果需要更多控制，可以使用 builder 模式。

### 基本 builder 用法
```java
SwingDialog.builder()
    .type(ModernDialogType.WARNING)
    .title("Unsaved Changes")
    .message("Do you want to save before closing?")
    .primaryButton("Save", e -> saveFile())
    .secondaryButton("Discard", e -> discardChanges())
    .show();
```

### 使用 Builder 获取用户选择

**方法 1：回调方式（适合简单操作）**
```java
SwingDialog.builder()
    .type(ModernDialogType.QUESTION)
    .title("Export Format")
    .message("Choose your export format:")
    .primaryButton("PDF", e -> exportAsPDF())
    .secondaryButton("CSV", e -> exportAsCSV())
    .show();
```

**方法 2：使用 `showAndGetResult()` 检查结果**
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
        break;
    case NO:
        break;
    case CLOSED:
        break;
}
```

**方法 3：自定义按钮并检查结果**
```java
ModernDialog dialog = SwingDialog.builder()
    .type(ModernDialogType.WARNING)
    .title("Save Changes?")
    .message("You have unsaved changes.")
    .primaryButton("Save", null)
    .secondaryButton("Discard", null)
    .build();

ModernDialog.DialogResult result = dialog.showAndGetResult();

if (result == ModernDialog.DialogResult.OK) {
    saveChanges();
} else if (result == ModernDialog.DialogResult.CANCEL) {
    discardChanges();
}
```

### Builder 方法一览

| 方法 | 说明 |
| --- | --- |
| `.type(ModernDialogType)` | 设置对话框类型：`INFO`、`SUCCESS`、`ERROR`、`WARNING`、`QUESTION` |
| `.title(String)` | 设置标题 |
| `.message(String)` | 设置消息内容，支持 `\n` 换行 |
| `.owner(Window)` | 设置父窗口 |
| `.okButton()` | 添加 “OK” 按钮，返回 `DialogResult.OK` |
| `.okButton(ActionListener)` | 添加带回调的 “OK” 按钮 |
| `.yesNoButtons()` | 添加 “Yes” / “No” 按钮 |
| `.primaryButton(text, action)` | 添加主按钮，返回 `DialogResult.OK` |
| `.secondaryButton(text, action)` | 添加次按钮，返回 `DialogResult.CANCEL` |
| `.build()` | 只构建，不显示 |
| `.show()` | 构建并显示 |

### DialogResult 值

| 值 | 含义 |
| --- | --- |
| `OK` | 用户点击 OK 或主按钮 |
| `YES` | 用户点击 Yes |
| `NO` | 用户点击 No |
| `CANCEL` | 用户点击 Cancel 或次按钮 |
| `CLOSED` | 用户直接关闭对话框 |

## 对话框类型

| 类型 | 图标颜色 | 适用场景 |
| --- | --- | --- |
| `INFO` | Blue | 一般信息 |
| `SUCCESS` | Green | 操作成功 |
| `WARNING` | Yellow/Orange | 提示风险或需要注意 |
| `ERROR` | Red | 错误或失败 |
| `QUESTION` | Purple | 需要用户作出选择 |

## 多行消息

使用 `\n` 换行：

```java
SwingDialog.showInfoMSGDialog("Results",
    "Found 10 items.\nProcessed: 8\nSkipped: 2");
```

## 便捷方法

```java
SwingDialog.successExportDataDialog();
SwingDialog.successFinishActionDialog();
```

## 说明

- 所有 `show*MSGDialog` 方法都是**非阻塞**的（内部使用 `SwingUtilities.invokeLater`）
- `showConfirmDialog` 是**阻塞**的（等待用户响应）
- Builder 的 `.show()` 是**阻塞**的，`.build()` 返回对话框对象以便手动控制
