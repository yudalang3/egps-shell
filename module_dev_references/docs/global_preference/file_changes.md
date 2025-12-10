# 文件修改清单

本文档列出了全局字体设置改进项目中所有涉及的文件。

---

## 📁 新建文件

### 字体面板类（4个）

| 文件路径 | 说明 | 行数 | 状态 |
|---------|------|------|------|
| `src/egps2/panels/pref/DialogFontPanel.java` | 对话框字体设置面板 | 66 | ✅ 新建 |
| `src/egps2/panels/pref/ComponentFontPanel.java` | 基础组件字体设置面板 | 65 | ✅ 新建 |
| `src/egps2/panels/pref/InputFontPanel.java` | 输入组件字体设置面板 | 68 | ✅ 新建 |
| `src/egps2/panels/pref/DataDisplayFontPanel.java` | 数据展示组件字体设置面板 | 72 | ✅ 新建 |

**总计**：4个文件，271行代码

### 文档文件（4个）

| 文件路径 | 说明 | 状态 |
|---------|------|------|
| `global_preference/README.md` | 项目总览文档 | ✅ 新建 |
| `global_preference/global_preference.plan.md` | 详细规划文档 | ✅ 移动 |
| `global_preference/implementation_summary.md` | 实施总结报告 | ✅ 新建 |
| `global_preference/file_changes.md` | 本文件清单 | ✅ 新建 |

---

## 📝 修改文件

### 核心类（4个）

#### 1. LaunchProperty.java
**路径**：`src/egps2/LaunchProperty.java`

**修改内容**：
- ➕ 添加20个私有Font字段
- ➕ 添加40个getter/setter方法
- ➕ 添加`applyFontsToUIManager()`方法（118行）
- ➕ 添加`convertFontWithDefault()`辅助方法（9行）
- ➕ 在默认构造函数中初始化所有新字体（~40行）
- ➕ 在DTO构造函数中转换所有新字体（~60行）
- ➕ 添加`import javax.swing.UIManager;`

**变更统计**：
- 原大小：~570行
- 新大小：~920行
- **净增加：+350行**

**关键修改位置**：
```
LaunchProperty.java:54-83   → 新增字段声明
LaunchProperty.java:112-145 → 新增字段初始化
LaunchProperty.java:163-211 → DTO构造函数中的字段转换
LaunchProperty.java:236-241 → 新增convertFontWithDefault方法
LaunchProperty.java:504-717 → 新增getter/setter和applyFontsToUIManager
```

---

#### 2. LaunchPropertyDTO.java
**路径**：`src/egps2/utils/LaunchPropertyDTO.java`

**修改内容**：
- ➕ 添加20个公共FontDTO字段
- 🔄 更新@JSONType注解的字段顺序
- ➕ 在构造函数中添加字段转换逻辑

**变更统计**：
- 原大小：~130行
- 新大小：~190行
- **净增加：+60行**

**关键修改位置**：
```
LaunchPropertyDTO.java:12-51  → 更新@JSONType注解
LaunchPropertyDTO.java:74-103 → 新增字段声明
LaunchPropertyDTO.java:145-174 → 构造函数中的字段转换
```

---

#### 3. PreferencePanel.java
**路径**：`src/egps2/PreferencePanel.java`

**修改内容**：
- ➕ 导入4个新字体面板类
- ➕ 导入`javax.swing.SwingUtilities`
- ➕ 在`createNodes()`中添加4个新节点
- ➕ 在`applyAndClose()`中添加新面板处理逻辑
- ➕ 调用`applyFontsToUIManager()`
- ➕ 调用`SwingUtilities.updateComponentTreeUI()`

**变更统计**：
- 原大小：~280行
- 新大小：~360行
- **净增加：+80行**

**关键修改位置**：
```
PreferencePanel.java:11     → 添加SwingUtilities导入
PreferencePanel.java:20-23  → 导入新面板类
PreferencePanel.java:169-202 → createNodes()中添加4个新节点
PreferencePanel.java:294-315 → applyAndClose()中的新面板处理
PreferencePanel.java:333-335 → 应用字体和刷新UI
```

---

#### 4. Launcher.java
**路径**：`src/egps2/Launcher.java`

**修改内容**：
- ➕ 在`launchProgram()`中添加字体应用逻辑
- ➖ 移除旧的零散UIManager设置代码

**变更统计**：
- 原大小：~260行
- 新大小：~254行
- **净减少：-6行**

**关键修改位置**：
```
Launcher.java:164-166 → 添加字体应用逻辑
Launcher.java:176-183 → 删除旧的UIManager设置（已移至applyFontsToUIManager）
```

**删除的代码**：
```java
// 删除这些零散的设置
Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
UIManager.put("ToolTip.font", defaultFont);
UIManager.put("OptionPane.messageFont", defaultFont);
UIManager.put("OptionPane.buttonFont", defaultFont);
UIManager.put("TextField.font", defaultFont);
```

**新增的代码**：
```java
// 统一调用applyFontsToUIManager
LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
launchProperty.applyFontsToUIManager();
```

---

## 📊 变更统计汇总

### 代码行数统计

| 文件类型 | 文件数 | 新增行数 | 删除行数 | 净变化 |
|---------|--------|---------|---------|--------|
| 新建Java类 | 4 | 271 | 0 | +271 |
| 修改Java类 | 4 | 490 | 10 | +480 |
| 新建文档 | 4 | ~2000 | 0 | +2000 |
| **总计** | **12** | **~2761** | **10** | **+2751** |

### 新增功能统计

| 功能类别 | 数量 |
|---------|------|
| 新增类 | 4 |
| 新增字段 | 20 |
| 新增方法 | 45 |
| UIManager键 | 40+ |
| 文档页数 | ~30 |

---

## 🔍 详细变更清单

### 新增字段列表（LaunchProperty.java）

```java
// Dialog fonts (3个)
private Font dialogTitleFont;
private Font dialogContentFont;
private Font dialogButtonFont;

// Component fonts (3个)
private Font labelFont;
private Font buttonFont;
private Font checkBoxFont;

// Input fonts (3个)
private Font textFieldFont;
private Font textAreaFont;
private Font comboBoxFont;

// Data display fonts (4个)
private Font tableFont;
private Font tableHeaderFont;
private Font listFont;
private Font treeFont;

// Tool fonts (2个)
private Font toolTipFont;
private Font toolBarFont;

// Other fonts (4个)
private Font progressBarFont;
private Font sliderFont;
private Font spinnerFont;
private Font scrollPaneFont;
```

### 新增方法列表（LaunchProperty.java）

**Getter方法（20个）**：
- `getDialogTitleFont()`
- `getDialogContentFont()`
- `getDialogButtonFont()`
- `getLabelFont()`
- `getButtonFont()`
- `getCheckBoxFont()`
- `getTextFieldFont()`
- `getTextAreaFont()`
- `getComboBoxFont()`
- `getTableFont()`
- `getTableHeaderFont()`
- `getListFont()`
- `getTreeFont()`
- `getToolTipFont()`
- `getToolBarFont()`
- `getProgressBarFont()`
- `getSliderFont()`
- `getSpinnerFont()`
- `getScrollPaneFont()`

**Setter方法（20个）**：
- `setDialogTitleFont(Font)`
- `setDialogContentFont(Font)`
- `setDialogButtonFont(Font)`
- `setLabelFont(Font)`
- `setButtonFont(Font)`
- `setCheckBoxFont(Font)`
- `setTextFieldFont(Font)`
- `setTextAreaFont(Font)`
- `setComboBoxFont(Font)`
- `setTableFont(Font)`
- `setTableHeaderFont(Font)`
- `setListFont(Font)`
- `setTreeFont(Font)`
- `setToolTipFont(Font)`
- `setToolBarFont(Font)`
- `setProgressBarFont(Font)`
- `setSliderFont(Font)`
- `setSpinnerFont(Font)`
- `setScrollPaneFont(Font)`

**工具方法（2个）**：
- `convertFontWithDefault(FontDTO, Supplier<Font>)` - 带默认值的字体转换
- `applyFontsToUIManager()` - 应用所有字体到UIManager

### UIManager键列表（40+个）

**对话框相关**：
- `OptionPane.messageFont`
- `OptionPane.buttonFont`
- `InternalFrame.titleFont`

**基础组件**：
- `Label.font`
- `Button.font`
- `CheckBox.font`
- `RadioButton.font`
- `ToggleButton.font`

**输入组件**：
- `TextField.font`
- `FormattedTextField.font`
- `PasswordField.font`
- `TextArea.font`
- `TextPane.font`
- `EditorPane.font`
- `ComboBox.font`

**数据展示组件**：
- `Table.font`
- `TableHeader.font`
- `List.font`
- `Tree.font`

**工具组件**：
- `ToolTip.font`
- `ToolBar.font`

**其他组件**：
- `ProgressBar.font`
- `Slider.font`
- `Spinner.font`
- `ScrollPane.font`

**菜单组件（已有）**：
- `Menu.font`
- `MenuBar.font`
- `MenuItem.font`
- `CheckBoxMenuItem.font`
- `RadioButtonMenuItem.font`
- `PopupMenu.font`

**Tab组件（已有）**：
- `TabbedPane.font`

**面板组件**：
- `Panel.font`
- `TitledBorder.font`

---

## 🗂️ 文件依赖关系

### 核心依赖图

```
LaunchProperty.java
    ├─→ UIManager (javax.swing)
    ├─→ FontDTO (egps2.utils)
    └─→ LaunchPropertyDTO (egps2.utils)

LaunchPropertyDTO.java
    ├─→ LaunchProperty (egps2)
    └─→ FontDTO (egps2.utils)

PreferencePanel.java
    ├─→ LaunchProperty (egps2)
    ├─→ SwingUtilities (javax.swing)
    ├─→ DialogFontPanel (egps2.panels.pref)
    ├─→ ComponentFontPanel (egps2.panels.pref)
    ├─→ InputFontPanel (egps2.panels.pref)
    └─→ DataDisplayFontPanel (egps2.panels.pref)

Launcher.java
    ├─→ LaunchProperty (egps2)
    └─→ UnifiedAccessPoint (egps2)

DialogFontPanel.java
ComponentFontPanel.java
InputFontPanel.java
DataDisplayFontPanel.java
    └─→ FontAppearancePanel (egps2.panels)
```

---

## 🎯 影响范围分析

### 直接影响

1. **启动流程**：`Launcher.java` - 字体在UI创建前应用
2. **配置系统**：`LaunchProperty.java`, `LaunchPropertyDTO.java` - 扩展配置模型
3. **设置界面**：`PreferencePanel.java` - 新增字体设置选项
4. **所有Swing组件**：通过UIManager全局影响

### 间接影响

1. **配置文件格式**：JSON配置增加20个新字段
2. **首次启动**：新字段使用默认值初始化
3. **用户体验**：所有对话框、按钮、表格等组件字体统一

### 无影响区域

1. ✅ 业务逻辑模块 - 无影响
2. ✅ 数据处理模块 - 无影响
3. ✅ 网络通信模块 - 无影响
4. ✅ 插件系统 - 无影响
5. ✅ 自定义Look&Feel - 通过UIManager协同工作

---

## 📋 编译清单

### 完整编译顺序

```bash
# 1. 编译基础DTO类
javac -d ./out/production/egps-main.gui \
  -cp "dependency-egps/*" \
  src/egps2/utils/FontDTO.java

# 2. 编译LaunchPropertyDTO
javac -d ./out/production/egps-main.gui \
  -cp "./out/production/egps-main.gui:dependency-egps/*" \
  src/egps2/utils/LaunchPropertyDTO.java

# 3. 编译LaunchProperty
javac -d ./out/production/egps-main.gui \
  -cp "./out/production/egps-main.gui:dependency-egps/*" \
  src/egps2/LaunchProperty.java

# 4. 编译字体面板类
javac -d ./out/production/egps-main.gui \
  -cp "./out/production/egps-main.gui:dependency-egps/*" \
  src/egps2/panels/pref/DialogFontPanel.java \
  src/egps2/panels/pref/ComponentFontPanel.java \
  src/egps2/panels/pref/InputFontPanel.java \
  src/egps2/panels/pref/DataDisplayFontPanel.java

# 5. 编译PreferencePanel
javac -d ./out/production/egps-main.gui \
  -cp "./out/production/egps-main.gui:dependency-egps/*" \
  src/egps2/PreferencePanel.java

# 6. 编译Launcher
javac -d ./out/production/egps-main.gui \
  -cp "./out/production/egps-main.gui:dependency-egps/*" \
  src/egps2/Launcher.java
```

### 快速编译命令

```bash
# 一次性编译所有修改的文件
javac -d ./out/production/egps-main.gui \
  -cp "./out/production/egps-main.gui:dependency-egps/*" \
  src/egps2/utils/FontDTO.java \
  src/egps2/utils/LaunchPropertyDTO.java \
  src/egps2/LaunchProperty.java \
  src/egps2/panels/pref/DialogFontPanel.java \
  src/egps2/panels/pref/ComponentFontPanel.java \
  src/egps2/panels/pref/InputFontPanel.java \
  src/egps2/panels/pref/DataDisplayFontPanel.java \
  src/egps2/PreferencePanel.java \
  src/egps2/Launcher.java
```

---

## 🔐 版本控制建议

### Git提交策略

建议分为以下几个提交：

**Commit 1: 核心数据模型扩展**
```bash
git add src/egps2/LaunchProperty.java
git add src/egps2/utils/LaunchPropertyDTO.java
git commit -m "feat: add 20 new font fields to LaunchProperty

- Add dialog fonts (title, content, button)
- Add component fonts (label, button, checkbox)
- Add input fonts (textfield, textarea, combobox)
- Add data display fonts (table, tableheader, list, tree)
- Add tool fonts (tooltip, toolbar)
- Add other component fonts (progressbar, slider, spinner, scrollpane)
- Add convertFontWithDefault() helper method
- Add applyFontsToUIManager() method with 40+ UIManager keys
"
```

**Commit 2: 新建字体面板类**
```bash
git add src/egps2/panels/pref/DialogFontPanel.java
git add src/egps2/panels/pref/ComponentFontPanel.java
git add src/egps2/panels/pref/InputFontPanel.java
git add src/egps2/panels/pref/DataDisplayFontPanel.java
git commit -m "feat: add 4 new font setting panels

- DialogFontPanel for dialog fonts
- ComponentFontPanel for component fonts
- InputFontPanel for input fonts
- DataDisplayFontPanel for data display fonts
"
```

**Commit 3: 更新设置界面**
```bash
git add src/egps2/PreferencePanel.java
git commit -m "feat: integrate new font panels into PreferencePanel

- Add 4 new font panel nodes in createNodes()
- Add panel handling logic in applyAndClose()
- Call applyFontsToUIManager() to apply fonts
- Call SwingUtilities.updateComponentTreeUI() to refresh UI
"
```

**Commit 4: 集成到启动流程**
```bash
git add src/egps2/Launcher.java
git commit -m "feat: apply fonts to UIManager on application startup

- Call applyFontsToUIManager() before creating main frame
- Remove old scattered UIManager font settings
- Ensure fonts are applied before UI creation
"
```

**Commit 5: 添加文档**
```bash
git add global_preference/
git commit -m "docs: add comprehensive documentation for font system

- Add README.md with project overview
- Add global_preference.plan.md with detailed planning
- Add implementation_summary.md with implementation report
- Add file_changes.md with file change list
"
```

---

## 📝 备注

### 重要提醒

1. **编译顺序**：必须先编译LaunchProperty，再编译PreferencePanel
2. **Classpath顺序**：新编译的类必须在dependency-egps之前
3. **向后兼容**：旧配置文件会自动使用默认值填充缺失字段
4. **UI刷新**：修改字体后需要调用updateComponentTreeUI()才能看到效果

### 维护建议

1. **添加新字体类型**：
   - 在LaunchProperty中添加字段
   - 在LaunchPropertyDTO中添加对应字段
   - 在构造函数中初始化和转换
   - 添加getter/setter
   - 在applyFontsToUIManager()中添加UIManager键
   - 创建或更新设置面板

2. **修改默认值**：
   - 修改LaunchProperty构造函数中的初始化代码
   - 修改convertFontWithDefault()调用中的默认值Supplier

3. **调试技巧**：
   - 使用javap查看编译后的class是否有新方法
   - 检查classpath确保加载正确的class文件
   - 打印UIManager.getFont()验证字体是否生效

---

**文档版本**：v1.0
**最后更新**：2025-12-07
**维护者**：Claude (AI Assistant)
