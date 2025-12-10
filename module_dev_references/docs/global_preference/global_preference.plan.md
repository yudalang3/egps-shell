# eGPS 全局字体设置改进规划

## 1. 现状分析

### 1.1 现有字体设置 (LaunchProperty.java)

目前eGPS已经实现了以下字体设置：

| 字体名称 | 字段名 | 用途 | 默认值 |
|---------|--------|------|--------|
| **菜单字体** |
| 一级菜单字体 | `menuFistLevelFont` | 主菜单栏的一级菜单 | defaultFont (12pt) |
| 二级菜单字体 | `menuSecondLevelFont` | 下拉菜单项 | defaultFont (12pt) |
| **Tab字体** |
| 选中Tab标题字体 | `selectedTabTitleFont` | 选中的标签页标题 | defaultFont.deriveFont(BOLD) |
| 未选中Tab标题字体 | `unSelectedTabTitleFont` | 未选中的标签页标题 | defaultFont (12pt) |
| **模块字体** |
| 默认标题字体 | `defaultTitleFont` | 模块内的标题 | defaultFont.deriveFont(BOLD, 13pt) |
| 默认字体 | `defaultFont` | 模块内的普通文本 | defaultFont (12pt) |
| **文档字体** |
| 文档字体 | `documentFont` | HTML文档、文本编辑器等 | defaultFont (16pt) |

### 1.2 现有UIManager字体设置 (Launcher.java:177-182)

在程序启动时，只设置了极少数UIManager字体：

```java
UIManager.put("ToolTip.font", defaultFont);
UIManager.put("OptionPane.messageFont", defaultFont);
UIManager.put("OptionPane.buttonFont", defaultFont);
UIManager.put("TextField.font", defaultFont);
```

### 1.3 问题诊断

通过代码分析发现以下问题：

1. **覆盖不全面**：大量Swing组件的默认字体未被设置，导致：
   - JDialog标题栏使用系统默认字体
   - JLabel、JButton等基础组件可能不一致
   - JTable、JList、JTree等复杂组件字体未统一
   - JOptionPane的标题字体未设置

2. **分类不够细致**：
   - 缺少对话框专用字体（标题、内容）
   - 缺少表格/列表专用字体
   - 缺少输入框/文本区域专用字体
   - 缺少按钮/标签专用字体

3. **设置时机问题**：
   - 部分UIManager设置在主窗口显示之后（Launcher.java:177），可能导致已创建组件字体不更新
   - 缺少全局字体变更后的刷新机制

4. **Look & Feel冲突**：
   - 自定义L&F (egps.lnf.*) 可能覆盖部分字体设置
   - 没有协调机制确保自定义L&F与全局字体设置一致

---

## 2. 改进方案

### 2.1 新增字体类型

在`LaunchProperty.java`中新增以下字体字段：

#### A. 对话框字体组
```java
private Font dialogTitleFont;        // 对话框标题字体 (Bold, 14pt)
private Font dialogContentFont;      // 对话框内容字体 (Plain, 12pt)
private Font dialogButtonFont;       // 对话框按钮字体 (Plain, 12pt)
```

**应用范围：**
- `JDialog` 标题
- `JOptionPane` 标题和消息内容
- 对话框中的按钮

**UIManager键：**
```
Dialog.font
OptionPane.titleFont (自定义)
OptionPane.messageFont
OptionPane.buttonFont
```

#### B. 基础组件字体组
```java
private Font labelFont;              // 标签字体 (Plain, 12pt)
private Font buttonFont;             // 按钮字体 (Plain, 12pt)
private Font checkBoxFont;           // 复选框/单选框字体 (Plain, 12pt)
```

**应用范围：**
- 所有 `JLabel`
- 所有 `JButton`, `JToggleButton`
- `JCheckBox`, `JRadioButton`

**UIManager键：**
```
Label.font
Button.font
CheckBox.font
RadioButton.font
ToggleButton.font
```

#### C. 输入组件字体组
```java
private Font textFieldFont;          // 文本框字体 (Plain, 12pt)
private Font textAreaFont;           // 文本区域字体 (Monospaced, 12pt)
private Font comboBoxFont;           // 下拉框字体 (Plain, 12pt)
```

**应用范围：**
- `JTextField`, `JPasswordField`, `JFormattedTextField`
- `JTextArea`, `JTextPane`, `JEditorPane`
- `JComboBox`

**UIManager键：**
```
TextField.font
FormattedTextField.font
PasswordField.font
TextArea.font
TextPane.font
EditorPane.font
ComboBox.font
```

#### D. 列表/表格字体组
```java
private Font tableFont;              // 表格字体 (Plain, 11pt)
private Font tableHeaderFont;        // 表格表头字体 (Bold, 12pt)
private Font listFont;               // 列表字体 (Plain, 12pt)
private Font treeFont;               // 树形控件字体 (Plain, 12pt)
```

**应用范围：**
- `JTable` 单元格和表头
- `JList`
- `JTree`

**UIManager键：**
```
Table.font
TableHeader.font
List.font
Tree.font
```

#### E. 工具提示字体组
```java
private Font toolTipFont;            // 工具提示字体 (Plain, 11pt)
private Font toolBarFont;            // 工具栏字体 (Plain, 11pt)
```

**应用范围：**
- `JToolTip`
- `JToolBar`中的组件

**UIManager键：**
```
ToolTip.font
ToolBar.font
```

#### F. 其他组件字体
```java
private Font progressBarFont;        // 进度条字体 (Plain, 10pt)
private Font sliderFont;             // 滑块字体 (Plain, 10pt)
private Font spinnerFont;            // 微调器字体 (Plain, 12pt)
private Font scrollPaneFont;         // 滚动面板字体 (Plain, 12pt)
```

**应用范围：**
- `JProgressBar`
- `JSlider`
- `JSpinner`
- `JScrollPane`

**UIManager键：**
```
ProgressBar.font
Slider.font
Spinner.font
ScrollPane.font
```

### 2.2 字体分类体系

```
eGPS 全局字体体系
│
├─ 菜单系统字体 (Menu Fonts)
│  ├─ menuFistLevelFont          - 一级菜单
│  └─ menuSecondLevelFont         - 二级菜单
│
├─ Tab系统字体 (Tab Fonts)
│  ├─ selectedTabTitleFont        - 选中Tab
│  └─ unSelectedTabTitleFont      - 未选中Tab
│
├─ 模块字体 (Module Fonts)
│  ├─ defaultTitleFont            - 模块标题
│  ├─ defaultFont                 - 模块内容
│  └─ documentFont                - 文档/HTML显示
│
├─ 对话框字体 (Dialog Fonts) [NEW]
│  ├─ dialogTitleFont             - 对话框标题
│  ├─ dialogContentFont           - 对话框内容
│  └─ dialogButtonFont            - 对话框按钮
│
├─ 基础组件字体 (Component Fonts) [NEW]
│  ├─ labelFont                   - 标签
│  ├─ buttonFont                  - 按钮
│  └─ checkBoxFont                - 复选框/单选框
│
├─ 输入组件字体 (Input Fonts) [NEW]
│  ├─ textFieldFont               - 文本框
│  ├─ textAreaFont                - 文本区域
│  └─ comboBoxFont                - 下拉框
│
├─ 数据展示字体 (Data Fonts) [NEW]
│  ├─ tableFont                   - 表格内容
│  ├─ tableHeaderFont             - 表格表头
│  ├─ listFont                    - 列表
│  └─ treeFont                    - 树形控件
│
├─ 工具组件字体 (Tool Fonts) [NEW]
│  ├─ toolTipFont                 - 工具提示
│  └─ toolBarFont                 - 工具栏
│
└─ 其他组件字体 (Other Fonts) [NEW]
   ├─ progressBarFont             - 进度条
   ├─ sliderFont                  - 滑块
   ├─ spinnerFont                 - 微调器
   └─ scrollPaneFont              - 滚动面板
```

### 2.3 完整的UIManager字体映射表

需要在`Launcher.lafPrepare()`或新建的字体初始化方法中设置所有这些UIManager键：

| 字体类型 | LaunchProperty字段 | UIManager键 | 优先级 |
|---------|-------------------|-------------|--------|
| **对话框** |
| 对话框标题 | dialogTitleFont | InternalFrame.titleFont | 高 |
| 对话框内容 | dialogContentFont | OptionPane.messageFont | 高 |
| 对话框按钮 | dialogButtonFont | OptionPane.buttonFont | 高 |
| **基础组件** |
| 标签 | labelFont | Label.font | 高 |
| 按钮 | buttonFont | Button.font | 高 |
| 复选框 | checkBoxFont | CheckBox.font, RadioButton.font | 中 |
| 切换按钮 | buttonFont | ToggleButton.font | 中 |
| **输入组件** |
| 文本框 | textFieldFont | TextField.font | 高 |
| 格式化文本框 | textFieldFont | FormattedTextField.font | 中 |
| 密码框 | textFieldFont | PasswordField.font | 中 |
| 文本区域 | textAreaFont | TextArea.font | 高 |
| 文本面板 | textAreaFont | TextPane.font | 中 |
| 编辑器面板 | textAreaFont | EditorPane.font | 中 |
| 下拉框 | comboBoxFont | ComboBox.font | 高 |
| **数据展示** |
| 表格 | tableFont | Table.font | 高 |
| 表头 | tableHeaderFont | TableHeader.font | 高 |
| 列表 | listFont | List.font | 高 |
| 树 | treeFont | Tree.font | 高 |
| **工具组件** |
| 工具提示 | toolTipFont | ToolTip.font | 高 |
| 工具栏 | toolBarFont | ToolBar.font | 中 |
| **其他** |
| 进度条 | progressBarFont | ProgressBar.font | 低 |
| 滑块 | sliderFont | Slider.font | 低 |
| 微调器 | spinnerFont | Spinner.font | 中 |
| 滚动面板 | scrollPaneFont | ScrollPane.font | 低 |
| **菜单 (已有)** |
| 菜单 | menuFistLevelFont | Menu.font, MenuBar.font | 高 |
| 菜单项 | menuSecondLevelFont | MenuItem.font, CheckBoxMenuItem.font, RadioButtonMenuItem.font, PopupMenu.font | 高 |
| **Tab (已有)** |
| Tab面板 | selectedTabTitleFont | TabbedPane.font | 高 |
| **面板 (建议)** |
| 面板 | defaultFont | Panel.font | 中 |
| 边框标题 | defaultTitleFont | TitledBorder.font | 中 |

---

## 3. 实施计划

### 3.1 第一阶段：核心字体扩展

**目标：** 添加最关键的缺失字体类型

**任务：**
1. 在`LaunchProperty.java`中添加新字段：
   - `dialogTitleFont`
   - `dialogContentFont`
   - `dialogButtonFont`
   - `labelFont`
   - `buttonFont`
   - `tableFont`
   - `tableHeaderFont`

2. 在`LaunchPropertyDTO.java`和`FontDTO.java`中添加对应的DTO字段

3. 在`LaunchProperty`构造函数中初始化这些字段的默认值

4. 在`LaunchProperty(LaunchPropertyDTO)`构造函数中添加从DTO转换的代码

5. 在`PreferencePanel.createNodes()`中添加对应的设置面板节点

**预期效果：** 对话框、标签、按钮、表格字体可自定义

### 3.2 第二阶段：UIManager全面集成

**目标：** 确保所有Swing组件都使用全局字体设置

**任务：**
1. 创建新方法 `LaunchProperty.applyFontsToUIManager()`
2. 在该方法中设置所有UIManager字体键
3. 在`Launcher.lafPrepare()`之后调用此方法
4. 确保时序正确：L&F设置 → 自定义UI设置 → 字体应用

**关键代码位置：**
```java
// Launcher.java
public static void lafPrepare() throws Exception {
    // ... existing L&F setup ...

    // 在所有L&F设置完成后，应用全局字体
    LaunchProperty launchProperty = LaunchProperty.getInstance();
    launchProperty.applyFontsToUIManager();
}
```

**新增方法：**
```java
// LaunchProperty.java
public void applyFontsToUIManager() {
    // 对话框字体
    UIManager.put("OptionPane.messageFont", dialogContentFont);
    UIManager.put("OptionPane.buttonFont", dialogButtonFont);
    UIManager.put("InternalFrame.titleFont", dialogTitleFont);

    // 基础组件字体
    UIManager.put("Label.font", labelFont);
    UIManager.put("Button.font", buttonFont);
    UIManager.put("CheckBox.font", checkBoxFont);
    UIManager.put("RadioButton.font", checkBoxFont);
    UIManager.put("ToggleButton.font", buttonFont);

    // 输入组件字体
    UIManager.put("TextField.font", textFieldFont);
    UIManager.put("FormattedTextField.font", textFieldFont);
    UIManager.put("PasswordField.font", textFieldFont);
    UIManager.put("TextArea.font", textAreaFont);
    UIManager.put("TextPane.font", textAreaFont);
    UIManager.put("EditorPane.font", textAreaFont);
    UIManager.put("ComboBox.font", comboBoxFont);

    // 数据展示字体
    UIManager.put("Table.font", tableFont);
    UIManager.put("TableHeader.font", tableHeaderFont);
    UIManager.put("List.font", listFont);
    UIManager.put("Tree.font", treeFont);

    // 工具组件字体
    UIManager.put("ToolTip.font", toolTipFont);
    UIManager.put("ToolBar.font", toolBarFont);

    // 其他组件字体
    UIManager.put("ProgressBar.font", progressBarFont);
    UIManager.put("Slider.font", sliderFont);
    UIManager.put("Spinner.font", spinnerFont);
    UIManager.put("ScrollPane.font", scrollPaneFont);

    // 菜单字体 (已有)
    UIManager.put("Menu.font", menuFistLevelFont);
    UIManager.put("MenuBar.font", menuFistLevelFont);
    UIManager.put("MenuItem.font", menuSecondLevelFont);
    UIManager.put("CheckBoxMenuItem.font", menuSecondLevelFont);
    UIManager.put("RadioButtonMenuItem.font", menuSecondLevelFont);
    UIManager.put("PopupMenu.font", menuSecondLevelFont);

    // Tab字体 (已有)
    UIManager.put("TabbedPane.font", selectedTabTitleFont);

    // 面板字体
    UIManager.put("Panel.font", defaultFont);
    UIManager.put("TitledBorder.font", defaultTitleFont);
}
```

### 3.3 第三阶段：设置界面改进

**目标：** 在全局设置中提供用户友好的字体配置界面

**任务：**
1. 重构`PreferencePanel.createNodes()`，按字体分类组织设置项：
   ```
   Appearance
   ├─ Menu Fonts
   │  ├─ First level menu font
   │  └─ Second level menu font
   ├─ Tab Fonts
   │  ├─ Selected tab font
   │  └─ Unselected tab font
   ├─ Module Fonts
   │  ├─ Module title font
   │  ├─ Module content font
   │  └─ Document font
   ├─ Dialog Fonts [NEW]
   │  ├─ Dialog title font
   │  ├─ Dialog content font
   │  └─ Dialog button font
   ├─ Component Fonts [NEW]
   │  ├─ Label font
   │  ├─ Button font
   │  └─ CheckBox font
   ├─ Input Fonts [NEW]
   │  ├─ TextField font
   │  ├─ TextArea font
   │  └─ ComboBox font
   └─ Data Display Fonts [NEW]
      ├─ Table font
      ├─ Table header font
      ├─ List font
      └─ Tree font
   ```

2. 为每个字体分类创建专用的面板类：
   - `DialogFontPanel.java`
   - `ComponentFontPanel.java`
   - `InputFontPanel.java`
   - `DataDisplayFontPanel.java`

3. 每个面板使用`SubTextPanelNode`或新建的多字体面板显示多个字体选项

4. 在`PreferencePanel.applyAndClose()`中添加新面板的应用逻辑

### 3.4 第四阶段：动态字体更新

**目标：** 支持运行时字体更改并立即生效

**任务：**
1. 在`PreferencePanel.applyAndClose()`中，保存配置后：
   ```java
   // 重新应用字体到UIManager
   launchProperty.applyFontsToUIManager();

   // 刷新所有已显示的窗口
   SwingUtilities.updateComponentTreeUI(UnifiedAccessPoint.getInstanceFrame());
   ```

2. 创建字体变更通知机制：
   ```java
   // UnifiedAccessPoint.java
   private static List<FontChangeListener> fontChangeListeners = new ArrayList<>();

   public static void addFontChangeListener(FontChangeListener listener) {
       fontChangeListeners.add(listener);
   }

   public static void notifyFontChanged() {
       for (FontChangeListener listener : fontChangeListeners) {
           listener.onFontChanged();
       }
   }
   ```

3. 让需要响应字体变更的组件实现`FontChangeListener`

### 3.5 第五阶段：默认值优化

**目标：** 为不同字体类型设计合理的默认值

**建议默认值表：**

| 字体类型 | 字体族 | 样式 | 大小 | 备注 |
|---------|-------|------|------|------|
| menuFistLevelFont | DefaultFont | PLAIN | 12 | 保持现有 |
| menuSecondLevelFont | DefaultFont | PLAIN | 12 | 保持现有 |
| selectedTabTitleFont | DefaultFont | BOLD | 12 | 保持现有 |
| unSelectedTabTitleFont | DefaultFont | PLAIN | 12 | 保持现有 |
| defaultTitleFont | DefaultFont | BOLD | 13 | 保持现有 |
| defaultFont | DefaultFont | PLAIN | 12 | 保持现有 |
| documentFont | DefaultFont | PLAIN | 16 | 保持现有 |
| **对话框字体** |
| dialogTitleFont | DefaultFont | BOLD | 14 | 标题需突出 |
| dialogContentFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| dialogButtonFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| **基础组件字体** |
| labelFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| buttonFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| checkBoxFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| **输入组件字体** |
| textFieldFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| textAreaFont | "Courier New" or "Consolas" | PLAIN | 12 | 等宽字体便于编辑 |
| comboBoxFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| **数据展示字体** |
| tableFont | DefaultFont | PLAIN | 11 | 稍小以容纳更多数据 |
| tableHeaderFont | DefaultFont | BOLD | 12 | 表头需突出 |
| listFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| treeFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| **工具组件字体** |
| toolTipFont | DefaultFont | PLAIN | 11 | 稍小不干扰 |
| toolBarFont | DefaultFont | PLAIN | 11 | 稍小节省空间 |
| **其他** |
| progressBarFont | DefaultFont | PLAIN | 10 | 很小，仅显示百分比 |
| sliderFont | DefaultFont | PLAIN | 10 | 很小，显示刻度 |
| spinnerFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |
| scrollPaneFont | DefaultFont | PLAIN | 12 | 与defaultFont一致 |

---

## 4. 潜在问题及解决方案

### 4.1 与自定义L&F冲突

**问题：** egps.lnf.* 包中的自定义UI可能覆盖UIManager设置

**解决方案：**
1. 确保调用顺序：L&F初始化 → UIManager字体应用 → 主窗口创建
2. 在自定义UI类中，避免硬编码字体，而是从UIManager读取：
   ```java
   // 不要这样：
   g.setFont(new Font("Arial", Font.PLAIN, 12));

   // 应该这样：
   g.setFont(UIManager.getFont("Button.font"));
   ```

3. 审查`egps/lnf/`目录下所有UI实现，确保字体从UIManager获取

### 4.2 已创建组件不更新

**问题：** 改变字体设置后，已显示的组件字体不变

**解决方案：**
1. 使用`SwingUtilities.updateComponentTreeUI()`刷新组件树
2. 对于自定义组件，实现`FontChangeListener`接口手动更新
3. 提示用户：部分设置需要重启应用才能完全生效

### 4.3 性能问题

**问题：** 大量UIManager.put()调用可能影响启动速度

**解决方案：**
1. 将所有字体设置集中在一个方法中，一次性完成
2. 使用懒加载：仅在需要时才初始化字体对象
3. 缓存Font对象，避免重复创建

### 4.4 配置文件兼容性

**问题：** 新版本添加字段后，旧配置文件无法读取

**解决方案：**
1. 在`LaunchProperty(LaunchPropertyDTO)`构造函数中添加空值检查：
   ```java
   if (launchProperty.dialogTitleFont != null) {
       this.setDialogTitleFont(convertFont(launchProperty.dialogTitleFont));
   } else {
       // 使用默认值
       this.setDialogTitleFont(getDefaultDialogTitleFont());
   }
   ```

2. 提供配置文件版本升级机制

3. 保持向后兼容：缺少字段时使用合理的默认值

### 4.5 字体不存在

**问题：** 用户配置的字体在系统中不存在

**解决方案：**
1. 在`convertFont()`方法中添加字体可用性检查：
   ```java
   private Font convertFont(FontDTO font) {
       String name = font.getName();

       // 检查字体是否可用
       String[] availableFonts = GraphicsEnvironment
           .getLocalGraphicsEnvironment()
           .getAvailableFontFamilyNames();

       boolean fontExists = Arrays.asList(availableFonts).contains(name);

       if (!fontExists) {
           logger.warn("Font not found: {}, using default", name);
           return DefaultFont.getDefaultFontFamily(font.getStyle(), font.getSize());
       }

       return new Font(name, font.getStyle(), font.getSize());
   }
   ```

2. 在字体选择器中只显示系统可用的字体

---

## 5. 测试计划

### 5.1 单元测试

**测试内容：**
1. `LaunchProperty` 新字段的getter/setter
2. `FontDTO` 序列化/反序列化
3. `convertFont()` 字体转换正确性
4. 默认值初始化正确性

### 5.2 集成测试

**测试场景：**
1. 启动应用，检查所有组件字体是否应用全局设置
2. 修改字体设置，检查是否正确保存到配置文件
3. 重启应用，检查字体设置是否正确加载
4. 测试不同组件类型：
   - JDialog及其子组件
   - JOptionPane各种类型
   - JTable及表头
   - JList、JTree
   - 所有输入组件

### 5.3 UI测试

**测试检查项：**
1. 字体选择器是否正确显示当前字体
2. 字体变更是否实时预览
3. 保存后字体是否立即生效（或提示重启）
4. 各个设置面板布局是否合理

### 5.4 兼容性测试

**测试平台：**
1. Windows 10/11
2. macOS
3. Linux (Ubuntu/Fedora)

**测试内容：**
1. 默认字体在各平台是否正确选择
2. 字体渲染质量
3. 不同分辨率下的显示效果

---

## 6. 实施优先级

### 高优先级 (P0)
这些是最影响用户体验的，应立即实施：

1. **对话框字体** (dialogTitleFont, dialogContentFont, dialogButtonFont)
   - 理由：对话框使用频繁，字体不一致非常明显

2. **基础组件字体** (labelFont, buttonFont)
   - 理由：JLabel和JButton是最常用的组件

3. **UIManager集成** (applyFontsToUIManager方法)
   - 理由：确保字体设置真正生效的核心机制

### 中优先级 (P1)
这些能显著改善体验，应在P0完成后尽快实施：

4. **数据展示字体** (tableFont, tableHeaderFont, listFont, treeFont)
   - 理由：这些组件在模块中大量使用

5. **输入组件字体** (textFieldFont, textAreaFont, comboBoxFont)
   - 理由：影响用户输入体验

6. **设置界面改进** (字体分类面板)
   - 理由：提供更好的配置体验

### 低优先级 (P2)
这些是锦上添花，可以在后续版本中实施：

7. **工具组件字体** (toolTipFont, toolBarFont)
   - 理由：使用场景相对较少

8. **其他组件字体** (progressBarFont, sliderFont, spinnerFont, scrollPaneFont)
   - 理由：这些组件字体通常不需要自定义

9. **动态字体更新机制**
   - 理由：用户可以接受重启后生效

---

## 7. 实施时间估算

| 阶段 | 任务 | 预计工作量 | 依赖 |
|------|------|-----------|------|
| **第一阶段** | 核心字体扩展 | 4-6小时 | 无 |
| - | LaunchProperty字段添加 | 1小时 | 无 |
| - | DTO类更新 | 1小时 | LaunchProperty |
| - | 默认值初始化 | 1小时 | LaunchProperty |
| - | 设置面板节点 | 1-2小时 | LaunchProperty |
| - | 测试 | 1小时 | 以上全部 |
| **第二阶段** | UIManager集成 | 3-4小时 | 第一阶段 |
| - | applyFontsToUIManager方法 | 2小时 | 第一阶段 |
| - | 集成到Launcher | 0.5小时 | applyFontsToUIManager |
| - | 测试 | 1-1.5小时 | 以上全部 |
| **第三阶段** | 设置界面改进 | 6-8小时 | 第一阶段 |
| - | PreferencePanel重构 | 2小时 | 第一阶段 |
| - | 新建字体面板类 | 3-4小时 | PreferencePanel |
| - | applyAndClose更新 | 1小时 | 新面板类 |
| - | UI测试 | 1-2小时 | 以上全部 |
| **第四阶段** | 动态字体更新 | 4-5小时 | 第二、三阶段 |
| - | 字体变更通知机制 | 2小时 | 第二阶段 |
| - | 组件树刷新 | 1小时 | 通知机制 |
| - | 测试 | 1-2小时 | 以上全部 |
| **第五阶段** | 默认值优化 | 2-3小时 | 第一阶段 |
| - | 研究最佳默认值 | 1小时 | 第一阶段 |
| - | 更新初始化代码 | 0.5小时 | 研究 |
| - | 测试不同平台 | 1-1.5小时 | 以上全部 |

**总计：** 19-26 小时（约3-4个工作日）

---

## 8. 后续改进建议

### 8.1 字体预设方案

提供多个预设字体方案供用户快速选择：
- 紧凑方案（小字体，节省空间）
- 标准方案（当前默认）
- 舒适方案（大字体，易于阅读）
- 高对比度方案（适合视力不佳用户）

### 8.2 字体缩放功能

提供全局字体缩放功能（类似浏览器的页面缩放）：
- 用户可以设置字体缩放比例（80%-200%）
- 所有字体按比例缩放
- 保持字体间的相对大小关系

### 8.3 字体导入/导出

支持字体配置的导入/导出：
- 导出当前字体配置为JSON文件
- 导入其他用户的字体配置
- 方便团队统一字体设置

### 8.4 字体实时预览

在设置界面提供实时预览功能：
- 显示各种组件的示例
- 修改字体时立即看到效果
- 无需应用即可预览

---

## 9. 总结

### 现有问题
1. 大量Swing组件使用系统默认字体，未统一
2. 对话框字体无法自定义，导致不一致
3. UIManager字体设置不全面
4. 缺少细粒度的字体分类

### 改进方案
1. 新增20+个字体类型，覆盖所有常用Swing组件
2. 建立完整的字体分类体系
3. 全面集成UIManager字体设置
4. 改进设置界面，提供友好的配置体验

### 预期效果
1. 所有Swing组件字体统一、一致
2. 用户可以精细控制各类组件的字体
3. 提升整体UI专业度和美观度
4. 增强可访问性（支持大字体等）

### 下一步行动
1. 评审此规划文档
2. 确认实施优先级
3. 开始第一阶段开发
4. 迭代测试和改进

---

**文档版本：** v1.0
**创建日期：** 2025-12-07
**作者：** Claude (AI Assistant)
**审核状态：** 待评审
