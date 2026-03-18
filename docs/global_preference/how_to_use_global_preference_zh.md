# 如何使用全局偏好与字体系统

本文档说明模块和插件在 `egps-shell` 中如何正确使用当前全局偏好与字体系统，重点是字体入口、适用边界和常见接入方式。

---

## 目录

1. [快速开始](#快速开始)
2. [字体类型说明](#字体类型说明)
3. [获取全局字体](#获取全局字体)
4. [最佳实践](#最佳实践)
5. [常见场景示例](#常见场景示例)
6. [避免的错误](#避免的错误)
7. [常见问题](#常见问题)

---

## 快速开始

### 基本原则

**✅ 推荐做法**：使用全局字体配置
```java
// 获取全局配置
LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();

// 使用全局字体
JLabel label = new JLabel("Hello");
label.setFont(launchProperty.getLabelFont());
```

**❌ 不推荐做法**：硬编码字体
```java
// 不要这样做！
JLabel label = new JLabel("Hello");
label.setFont(new Font("Arial", Font.PLAIN, 12));
```

### 为什么要使用全局字体？

1. **一致性**：确保整个应用的UI字体风格统一
2. **可定制**：用户可以根据喜好调整字体
3. **可访问性**：支持大字体等无障碍需求
4. **维护性**：字体集中管理，修改方便

---

## 字体类型说明

当前 `LaunchProperty` 暴露了 26 组字体配置。

本文重点说明模块和插件最常接触的那部分；像 `ProgressBar`、`Slider`、`Spinner`、`ScrollPane` 这类较少直接手动设置的字体，主要由 `UIManager` 在壳层统一应用。

### 1. 模块字体（Module Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| Module Title | `getDefaultTitleFont()` | 模块面板标题 | Bold, 13pt |
| Module Content | `getDefaultFont()` | 模块内容文本 | Plain, 12pt |
| Document | `getDocumentFont()` | HTML文档、富文本 | Plain, 16pt |

**使用场景**：
- ModuleFace面板的标题
- 模块内的普通文本
- 文档查看器、编辑器

**代码示例**：
```java
public class MyModuleFace extends ModuleFace {
    @Override
    protected void initUI() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

        // 标题
        JLabel titleLabel = new JLabel("模块标题");
        titleLabel.setFont(lp.getDefaultTitleFont());

        // 内容
        JLabel contentLabel = new JLabel("这是内容文本");
        contentLabel.setFont(lp.getDefaultFont());

        // 文档区域
        JTextArea docArea = new JTextArea();
        docArea.setFont(lp.getDocumentFont());
    }
}
```

### 2. 对话框字体（Dialog Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| Dialog Title | `getDialogTitleFont()` | 对话框标题 | Bold, 14pt |
| Dialog Content | `getDialogContentFont()` | 对话框消息内容 | Plain, 12pt |
| Dialog Button | `getDialogButtonFont()` | 对话框按钮文字 | Plain, 12pt |

**使用场景**：
- 自定义JDialog
- 提示信息面板
- 确认对话框

**代码示例**：
```java
public class MyDialog extends JDialog {
    public MyDialog(Frame owner) {
        super(owner, "我的对话框", true);
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

        // 标题标签
        JLabel titleLabel = new JLabel("对话框标题");
        titleLabel.setFont(lp.getDialogTitleFont());

        // 消息内容
        JLabel messageLabel = new JLabel("这是消息内容");
        messageLabel.setFont(lp.getDialogContentFont());

        // 按钮
        JButton okButton = new JButton("确定");
        okButton.setFont(lp.getDialogButtonFont());
    }
}
```

### 3. 基础组件字体（Component Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| Label | `getLabelFont()` | 所有标签 | Plain, 12pt |
| Button | `getButtonFont()` | 所有按钮 | Plain, 12pt |
| CheckBox | `getCheckBoxFont()` | 复选框、单选框 | Plain, 12pt |

**使用场景**：
- 表单标签
- 工具栏按钮
- 选项选择

**代码示例**：
```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

// 标签
JLabel nameLabel = new JLabel("姓名：");
nameLabel.setFont(lp.getLabelFont());

// 按钮
JButton submitButton = new JButton("提交");
submitButton.setFont(lp.getButtonFont());

// 复选框
JCheckBox agreeCheckBox = new JCheckBox("我同意");
agreeCheckBox.setFont(lp.getCheckBoxFont());
```

### 4. 输入组件字体（Input Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| TextField | `getTextFieldFont()` | 文本框 | Plain, 12pt |
| TextArea | `getTextAreaFont()` | 文本区域 | Monospaced, 12pt |
| ComboBox | `getComboBoxFont()` | 下拉框 | Plain, 12pt |

**使用场景**：
- 表单输入框
- 代码编辑器
- 参数配置

**代码示例**：
```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

// 文本框
JTextField nameField = new JTextField(20);
nameField.setFont(lp.getTextFieldFont());

// 文本区域（推荐用于代码/日志）
JTextArea codeArea = new JTextArea(10, 40);
codeArea.setFont(lp.getTextAreaFont());  // 等宽字体

// 下拉框
JComboBox<String> typeCombo = new JComboBox<>(new String[]{"类型A", "类型B"});
typeCombo.setFont(lp.getComboBoxFont());
```

### 5. 数据展示字体（Data Display Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| Table | `getTableFont()` | 表格单元格 | Plain, 11pt |
| Table Header | `getTableHeaderFont()` | 表格表头 | Bold, 12pt |
| List | `getListFont()` | 列表项 | Plain, 12pt |
| Tree | `getTreeFont()` | 树节点 | Plain, 12pt |

**使用场景**：
- 数据表格
- 文件列表
- 树形导航

**代码示例**：
```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

// 表格
JTable table = new JTable(data, columns);
table.setFont(lp.getTableFont());
table.getTableHeader().setFont(lp.getTableHeaderFont());

// 列表
JList<String> fileList = new JList<>(files);
fileList.setFont(lp.getListFont());

// 树
JTree navTree = new JTree(rootNode);
navTree.setFont(lp.getTreeFont());
```

### 6. 工具组件字体（Tool Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| ToolTip | `getToolTipFont()` | 工具提示 | Plain, 11pt |
| ToolBar | `getToolBarFont()` | 工具栏 | Plain, 11pt |

**使用场景**：
- 按钮工具提示
- 工具栏标签

**代码示例**：
```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

// 设置工具提示
JButton button = new JButton("保存");
button.setToolTipText("保存当前文件");
// ToolTip字体由UIManager统一管理，通常不需要手动设置

// 工具栏（如果需要自定义标签）
JToolBar toolBar = new JToolBar();
JLabel toolbarLabel = new JLabel("工具：");
toolbarLabel.setFont(lp.getToolBarFont());
toolBar.add(toolbarLabel);
```

### 7. 菜单字体（Menu Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| First Level Menu | `getMenuFistLevelFont()` | 主菜单栏 | Plain, 12pt |
| Second Level Menu | `getMenuSecondLevelFont()` | 下拉菜单项 | Plain, 12pt |

**使用场景**：
- 自定义菜单栏（通常由主框架管理，模块较少使用）

### 8. Tab字体（Tab Fonts）

| 字体名 | 获取方法 | 用途 | 默认值 |
|-------|---------|------|--------|
| Selected Tab | `getSelectedTabTitleFont()` | 选中的标签页 | Bold, 12pt |
| Unselected Tab | `getUnSelectedTabTitleFont()` | 未选中的标签页 | Plain, 12pt |

**使用场景**：
- 自定义JTabbedPane（通常由UIManager管理，较少手动设置）

---

## 🔧 获取全局字体

### 方法一：通过UnifiedAccessPoint（推荐）

这是获取全局配置的标准方式：

```java
import egps2.UnifiedAccessPoint;
import egps2.LaunchProperty;

public class MyModule {
    public void setupUI() {
        // 获取全局配置
        LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();

        // 使用字体
        JLabel label = new JLabel("Hello");
        label.setFont(launchProperty.getLabelFont());
    }
}
```

### 方法二：缓存引用（便于复用）

如果一个类里会反复使用同一组字体，缓存 `LaunchProperty` 引用可以让代码更整洁：

```java
public class MyModuleFace extends ModuleFace {
    private final LaunchProperty launchProperty;

    public MyModuleFace() {
        // 在构造函数中缓存
        this.launchProperty = UnifiedAccessPoint.getLaunchProperty();
    }

    @Override
    protected void initUI() {
        // 直接使用缓存的引用
        JLabel label = new JLabel("Title");
        label.setFont(launchProperty.getDefaultTitleFont());
    }
}
```

### 方法三：依赖UIManager（自动应用）

对于很多标准 Swing 组件，字体会在壳程序调用 `applyFontsToUIManager()` 后通过 `UIManager` 统一应用，因此不一定需要手动 `setFont(...)`：

```java
// 这些组件会自动使用全局字体（无需手动setFont）
JLabel label = new JLabel("自动使用labelFont");
JButton button = new JButton("自动使用buttonFont");
JTextField field = new JTextField("自动使用textFieldFont");
```

**注意**：这并不意味着所有组件都会自动更新。自定义绘制、第三方组件、已经创建完成但未刷新过的组件，仍然可能需要你手动处理。

---

## 💡 最佳实践

### 1. 优先让壳层的 UIManager 配置生效

如果你的组件是标准Swing组件，让UIManager自动处理字体：

```java
// ✅ 好的做法：让UIManager处理
JButton button = new JButton("确定");
// 不设置font，自动使用全局buttonFont

// ❌ 不必要的做法（除非要覆盖）
JButton button = new JButton("确定");
button.setFont(UnifiedAccessPoint.getLaunchProperty().getButtonFont());
```

### 2. 为自定义组件明确设置字体

如果是自定义组件或继承的组件，需要明确设置：

```java
public class MyCustomPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ✅ 明确使用全局字体绘制文本
        g.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultFont());
        g.drawString("Custom Text", 10, 20);
    }
}
```

### 3. 根据语义选择字体

选择最符合组件语义的字体类型：

```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

// ✅ 标题用titleFont
JLabel titleLabel = new JLabel("配置选项");
titleLabel.setFont(lp.getDefaultTitleFont());

// ✅ 普通文本用defaultFont
JLabel descLabel = new JLabel("请选择配置项");
descLabel.setFont(lp.getDefaultFont());

// ✅ 代码/日志用textAreaFont（等宽）
JTextArea logArea = new JTextArea();
logArea.setFont(lp.getTextAreaFont());

// ✅ 数据表格用tableFont
JTable dataTable = new JTable();
dataTable.setFont(lp.getTableFont());
```

### 4. 避免混用字体大小

保持同类组件的字体一致：

```java
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

// ✅ 好的做法：所有标签使用同一字体
JLabel label1 = new JLabel("名称：");
label1.setFont(lp.getLabelFont());

JLabel label2 = new JLabel("年龄：");
label2.setFont(lp.getLabelFont());

// ❌ 不好的做法：随意修改大小
JLabel label3 = new JLabel("地址：");
Font customFont = lp.getLabelFont().deriveFont(18f);  // 不推荐
label3.setFont(customFont);
```

### 5. 响应字体变更（高级）

如果你的模块需要在用户修改字体后立即更新UI：

```java
public class MyModuleFace extends ModuleFace {
    private JLabel titleLabel;
    private JLabel contentLabel;

    @Override
    protected void initUI() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

        titleLabel = new JLabel("标题");
        titleLabel.setFont(lp.getDefaultTitleFont());

        contentLabel = new JLabel("内容");
        contentLabel.setFont(lp.getDefaultFont());
    }

    // 当界面被重新应用字体后，可在你的组件里做额外刷新
    public void onFontChanged() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
        titleLabel.setFont(lp.getDefaultTitleFont());
        contentLabel.setFont(lp.getDefaultFont());

        // 刷新UI
        SwingUtilities.updateComponentTreeUI(this);
    }
}
```

---

## 📖 常见场景示例

### 场景1：创建一个设置对话框

```java
public class SettingsDialog extends JDialog {
    public SettingsDialog(Frame owner) {
        super(owner, "设置", true);
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();

        setLayout(new BorderLayout());

        // 标题
        JLabel titleLabel = new JLabel("模块设置");
        titleLabel.setFont(lp.getDialogTitleFont());
        add(titleLabel, BorderLayout.NORTH);

        // 中间表单
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        JLabel nameLabel = new JLabel("名称：");
        nameLabel.setFont(lp.getLabelFont());
        JTextField nameField = new JTextField();
        nameField.setFont(lp.getTextFieldFont());

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        add(formPanel, BorderLayout.CENTER);

        // 底部按钮
        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("确定");
        okButton.setFont(lp.getDialogButtonFont());
        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(lp.getDialogButtonFont());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }
}
```

### 场景2：创建一个数据查看器

```java
public class DataViewerPanel extends JPanel {
    private JTable dataTable;
    private JTextArea detailsArea;

    public DataViewerPanel() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
        setLayout(new BorderLayout());

        // 顶部标题
        JLabel titleLabel = new JLabel("数据查看器");
        titleLabel.setFont(lp.getDefaultTitleFont());
        add(titleLabel, BorderLayout.NORTH);

        // 中间分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // 上半部分：数据表格
        dataTable = new JTable();
        dataTable.setFont(lp.getTableFont());
        dataTable.getTableHeader().setFont(lp.getTableHeaderFont());
        splitPane.setTopComponent(new JScrollPane(dataTable));

        // 下半部分：详情区域
        detailsArea = new JTextArea();
        detailsArea.setFont(lp.getTextAreaFont());  // 使用等宽字体
        detailsArea.setEditable(false);
        splitPane.setBottomComponent(new JScrollPane(detailsArea));

        add(splitPane, BorderLayout.CENTER);
    }
}
```

### 场景3：创建一个工具栏面板

```java
public class MyToolbarPanel extends JPanel {
    public MyToolbarPanel() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
        setLayout(new FlowLayout(FlowLayout.LEFT));

        // 工具栏标签
        JLabel toolLabel = new JLabel("工具：");
        toolLabel.setFont(lp.getToolBarFont());
        add(toolLabel);

        // 工具按钮
        JButton exportButton = new JButton("导出");
        exportButton.setFont(lp.getButtonFont());
        exportButton.setToolTipText("导出数据到文件");
        add(exportButton);

        JButton importButton = new JButton("导入");
        importButton.setFont(lp.getButtonFont());
        importButton.setToolTipText("从文件导入数据");
        add(importButton);
    }
}
```

### 场景4：创建一个文件浏览器

```java
public class FileBrowserPanel extends JPanel {
    private JTree fileTree;
    private JList<String> fileList;

    public FileBrowserPanel() {
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
        setLayout(new BorderLayout());

        // 左侧：目录树
        fileTree = new JTree();
        fileTree.setFont(lp.getTreeFont());

        // 右侧：文件列表
        fileList = new JList<>();
        fileList.setFont(lp.getListFont());

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            new JScrollPane(fileTree),
            new JScrollPane(fileList)
        );

        add(splitPane, BorderLayout.CENTER);
    }
}
```

---

## ❌ 避免的错误

### 错误1：硬编码字体

```java
// ❌ 错误：硬编码字体
JLabel label = new JLabel("Title");
label.setFont(new Font("Arial", Font.BOLD, 14));

// ✅ 正确：使用全局字体
JLabel label = new JLabel("Title");
label.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont());
```

### 错误2：忽略字体语义

```java
// ❌ 错误：对话框内容用tableFont
JLabel messageLabel = new JLabel("操作成功");
messageLabel.setFont(UnifiedAccessPoint.getLaunchProperty().getTableFont());

// ✅ 正确：对话框内容用dialogContentFont
JLabel messageLabel = new JLabel("操作成功");
messageLabel.setFont(UnifiedAccessPoint.getLaunchProperty().getDialogContentFont());
```

### 错误3：在同一个类里反复取值却不做整理

```java
// 这样并不会创建新的 LaunchProperty 实例，
// 但代码会显得零散，不利于维护
for (int i = 0; i < 100; i++) {
    JLabel label = new JLabel("Item " + i);
    label.setFont(UnifiedAccessPoint.getLaunchProperty().getLabelFont());
}

// ✅ 更清晰：先取一次引用
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
for (int i = 0; i < 100; i++) {
    JLabel label = new JLabel("Item " + i);
    label.setFont(lp.getLabelFont());
}
```

### 错误4：在静态初始化块中获取字体

```java
// ❌ 不推荐：过早把运行期字体快照固化到静态字段里
public class MyModule {
    private static final Font LABEL_FONT =
        UnifiedAccessPoint.getLaunchProperty().getLabelFont();
}

// ✅ 更稳妥：在实例方法或构造函数中获取
public class MyModule {
    private Font labelFont;

    public MyModule() {
        this.labelFont = UnifiedAccessPoint.getLaunchProperty().getLabelFont();
    }
}
```

### 错误5：混用不同来源的字体

```java
// ❌ 错误：混用UIManager和硬编码字体
JLabel label1 = new JLabel("Label 1");
label1.setFont(UIManager.getFont("Label.font"));  // 来自UIManager

JLabel label2 = new JLabel("Label 2");
label2.setFont(new Font("Arial", Font.PLAIN, 12));  // 硬编码

// ✅ 正确：统一使用LaunchProperty
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
JLabel label1 = new JLabel("Label 1");
label1.setFont(lp.getLabelFont());

JLabel label2 = new JLabel("Label 2");
label2.setFont(lp.getLabelFont());
```

---

## ❓ 常见问题

### Q1: 我的模块应该使用哪个字体？

**A**: 根据组件类型选择：
- 标题 → `getDefaultTitleFont()`
- 普通文本 → `getDefaultFont()`
- 对话框消息 → `getDialogContentFont()`
- 表格数据 → `getTableFont()`
- 代码/日志 → `getTextAreaFont()`（等宽）

### Q2: 字体会自动应用到所有组件吗？

**A**: 不是。很多标准 Swing 组件会通过 `UIManager` 使用全局字体，但下面这些情况仍然常常需要手动处理：
- 自定义组件
- 需要覆盖默认字体
- 在Graphics中绘制文本
- 第三方组件或历史遗留组件

### Q3: 用户修改字体后，我的模块会自动更新吗？

**A**: 只能说部分会跟上：
- 新创建的组件通常会使用新的 `UIManager` 字体
- 现有 Swing 组件通常需要重新刷新界面树，当前偏好面板会调用 `SwingUtilities.updateComponentTreeUI(UnifiedAccessPoint.getInstanceFrame())`
- 自定义绘制的文本仍需要你自己重绘或重新设定字体

### Q4: 我能创建自己的字体吗？

**A**: 不推荐。应该使用全局字体以保持一致性。如果确实需要：
```java
// 基于全局字体派生
LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
Font baseFont = lp.getDefaultFont();
Font customFont = baseFont.deriveFont(Font.BOLD, 18f);
```

### Q5: 如何在模块中处理中文等非拉丁字符？

**A**: 当前默认字体优先级会优先尝试 `Microsoft YaHei UI`、`Arial`、`SimSun`、`Times New Roman`。在常见中文环境下通常可以正常显示中文；如果你的运行环境字体集不完整，仍应实际检查显示效果：
```java
JLabel chineseLabel = new JLabel("你好世界");
chineseLabel.setFont(UnifiedAccessPoint.getLaunchProperty().getLabelFont());
```

### Q6: 表格表头字体和内容字体不一样怎么办？

**A**: 这是设计好的特性，表头使用更粗的字体：
```java
JTable table = new JTable();
table.setFont(lp.getTableFont());           // 内容：11pt
table.getTableHeader().setFont(lp.getTableHeaderFont());  // 表头：Bold 12pt
```

### Q7: 我的模块在加载时 LaunchProperty 还没初始化怎么办？

**A**: 一般直接通过 `UnifiedAccessPoint.getLaunchProperty()` 访问即可；它会按需初始化。只是从维护角度看，更适合在 `ModuleFace.initUI()` 或更晚的生命周期里使用，而不是在很早的静态初始化阶段固化一份字体值：
```java
public class MyModuleFace extends ModuleFace {
    @Override
    protected void initUI() {
        // ✅ 安全：此时LaunchProperty已初始化
        LaunchProperty lp = UnifiedAccessPoint.getLaunchProperty();
        setupFonts(lp);
    }
}
```

### Q8: 如何测试我的模块在不同字体下的显示效果？

**A**:
1. 运行应用程序
2. 打开当前的 Preference 面板
3. 修改相关字体
4. 点击Apply查看效果
5. 重新加载你的模块测试

---

## 📚 进一步阅读

- [全局字体系统设计与边界说明](./global_font_system_design.md)
- [当前全局字体系统说明](./global_font_system_statement.md)
- [全局字体相关文件位置与集成点说明](./global_font_system_code_map.md)
- [全局字体专题文档入口](./README.md)
- [eGPS模块开发指南](../../manuals/module_plugin_course/)
