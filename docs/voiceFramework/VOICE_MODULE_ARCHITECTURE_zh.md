# VOICE 模块架构指南

`VOICE` 是 `Versatile Open Input Click Execute` 的缩写。

本文说明当前 `egps-shell` 中 VOICE 框架的实际架构。

## 1. VOICE 提供什么

VOICE 是壳层中的参数驱动 GUI 框架，适合这类模块：

- 需要结构化文本输入
- 需要生成示例
- 需要书签或已保存输入管理
- 需要统一的执行行为
- 既要支持 GUI，也要兼顾命令式使用方式

当前实现主要位于 `src/egps2/builtin/modules/voice/`。

## 2. 主要架构层次

### 基础 GUI 层

`VersatileOpenInputClickAbstractGuiBase`、`InputAreaPanel` 等核心类提供公共输入区行为和外围 GUI 结构。

### 书签与状态层

`BookmarkDisplayPanel`、`BookmarkOperationPanel`、`BookMarkNode`、`EditScriptState` 等类负责已保存示例、书签状态和编辑模式管理。

### 事件处理层

`EventUniformlyProcessor` 统一处理常见交互逻辑，让不同 VOICE 模块遵循相同执行流。

### 参数层

`bean/` 和 `template/` 下的类负责参数建模、解析、赋值和示例生成。

### Fast VOICE Tab 层

`fastmodvoice/` 下的类支持带 Tab 或 dockable 子视图的 VOICE 模块，包括 `TabModuleFaceOfVoice` 和 `DockableTabModuleFaceOfVoice`。

## 3. 当前集成方式

当前框架支持多种承载 VOICE 行为的方式：

- 普通模块面板
- Tab 形态的 VOICE 模块
- 带 SubTabs 的 dockable VOICE 模块

这些都属于同一套框架中的相关模式，而不是彼此独立的产品。

## 4. 为什么 SubTab 要被区别对待

`DockableTabModuleFaceOfVoice` 下的 SubTab 是父模块的内部组成部分。

因此：

- 它们会参与 VOICE 组合
- 但不会被普通模块发现机制当作独立模块处理

这也是模块发现文档需要单独说明 SubTab 排除规则的原因。

## 5. 当前边界

本文描述的是当前代码中已经实现的 VOICE 框架。

不应把它读成：

- 未来路线图
- 变更日志
- 一步一步构建 VOICE 的过程记录

凡是本文提到的行为，都应是当前代码库已经支持的能力。

## 6. 相关文档

- `VOICE-GUI.md` / `VOICE-GUI_zh.md`
- `../module&pluginSystem/module_discovery_subtab_exclusion.md` / `../module&pluginSystem/module_discovery_subtab_exclusion_zh.md`
