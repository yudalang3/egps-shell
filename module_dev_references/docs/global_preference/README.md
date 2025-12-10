# eGPS 全局字体设置改进项目

本目录包含了eGPS全局字体设置系统的完整改进文档和代码。

## 📁 目录结构

```
global_preference/
├── README.md                      # 本文件：项目总览
├── developer_guide.md             # 🎯 模块/插件开发者使用指南
├── global_preference.plan.md      # 详细规划文档
├── implementation_summary.md      # 实施总结报告
└── file_changes.md                # 文件修改清单
```

## 🎯 项目目标

解决eGPS应用中Swing组件字体不一致的问题，提供全面的字体自定义功能。

## ✨ 核心功能

### 新增字体类型（20个）

#### 对话框字体组
- `dialogTitleFont` - 对话框标题字体
- `dialogContentFont` - 对话框内容字体
- `dialogButtonFont` - 对话框按钮字体

#### 基础组件字体组
- `labelFont` - 标签字体
- `buttonFont` - 按钮字体
- `checkBoxFont` - 复选框/单选框字体

#### 输入组件字体组
- `textFieldFont` - 文本框字体
- `textAreaFont` - 文本区域字体
- `comboBoxFont` - 下拉框字体

#### 数据展示字体组
- `tableFont` - 表格内容字体
- `tableHeaderFont` - 表格表头字体
- `listFont` - 列表字体
- `treeFont` - 树形控件字体

#### 工具组件字体组
- `toolTipFont` - 工具提示字体
- `toolBarFont` - 工具栏字体

#### 其他组件字体
- `progressBarFont` - 进度条字体
- `sliderFont` - 滑块字体
- `spinnerFont` - 微调器字体
- `scrollPaneFont` - 滚动面板字体

### 原有字体（7个）
- `menuFistLevelFont` - 一级菜单字体
- `menuSecondLevelFont` - 二级菜单字体
- `selectedTabTitleFont` - 选中Tab字体
- `unSelectedTabTitleFont` - 未选中Tab字体
- `defaultFont` - 默认字体
- `defaultTitleFont` - 默认标题字体
- `documentFont` - 文档字体

**总计：27个可自定义字体**

## 📦 实施内容

### 阶段一：核心扩展
- ✅ 在`LaunchProperty.java`中添加20个新字体字段
- ✅ 在`LaunchPropertyDTO.java`中添加对应DTO字段
- ✅ 实现默认值初始化和向后兼容

### 阶段二：UIManager集成
- ✅ 创建`applyFontsToUIManager()`方法
- ✅ 设置40+个UIManager键
- ✅ 集成到`Launcher.java`启动流程

### 阶段三：设置界面
- ✅ 创建4个新字体设置面板
- ✅ 更新`PreferencePanel.java`
- ✅ 实现实时应用和UI刷新

## 🚀 快速开始

### 📘 模块开发者必读
```bash
cat docs/global_preference/developer_guide.md
```

### 查看详细规划
```bash
cat docs/global_preference/global_preference.plan.md
```

### 查看实施总结
```bash
cat docs/global_preference/implementation_summary.md
```

### 查看修改的文件列表
```bash
cat docs/global_preference/file_changes.md
```

## 📝 关键技术点

1. **全面覆盖**：通过UIManager.put()设置所有Swing组件默认字体
2. **实时生效**：使用`SwingUtilities.updateComponentTreeUI()`刷新UI
3. **类型安全**：所有字体存储为Font对象，通过FontDTO进行序列化
4. **开发友好**：提供完整的API和使用指南

## 🔧 编译说明

确保编译顺序正确，新编译的类应优先于dependency-egps中的旧类：

```bash
javac -d ./out/production/egps-main.gui \
  -cp "./out/production/egps-main.gui:dependency-egps/*" \
  src/egps2/LaunchProperty.java \
  src/egps2/utils/LaunchPropertyDTO.java \
  src/egps2/PreferencePanel.java \
  src/egps2/Launcher.java
```

## 📊 统计数据

- **新增Java类**：4个字体面板类
- **修改Java类**：4个核心类
- **新增字体字段**：20个
- **新增UIManager键**：40+个
- **新增代码行数**：约800行
- **实施时间**：约4小时

## 🎨 用户界面

用户可以在 **File → Preference → Appearance** 中找到所有字体设置：

```
Appearance
├─ Module font
├─ Menu font
├─ Tab font
├─ Document font
├─ Dialog fonts ⭐新增
├─ Component fonts ⭐新增
├─ Input fonts ⭐新增
├─ Data display fonts ⭐新增
├─ Look and Feel
└─ Icon Size
```

## 📄 许可证

遵循eGPS项目的许可证。

## 👥 贡献者

- Claude (AI Assistant) - 需求分析、规划设计、代码实现
- 项目维护者 - 需求提出、测试验证

## 📅 版本历史

- **v1.1** (2025-12-07) - 移除向后兼容代码，简化实现
- **v1.0** (2025-12-07) - 初始实现，完成三阶段核心功能

---

**推荐**：模块/插件开发者请先阅读 [developer_guide.md](developer_guide.md) 了解如何正确使用全局字体系统。
