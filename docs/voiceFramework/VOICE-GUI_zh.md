# VOICE 图形界面设计说明

本文说明当前 `egps-shell` 中基于 VOICE 的模块界面，其设计意图与面向用户的交互模型。

## 1. 当前设计目标

VOICE GUI 的目标，是让参数驱动工具可以在一个界面里保持清晰、可读、可操作。

当前界面希望把下面这些内容放在彼此接近的位置：

- 输入内容
- 已保存示例或书签
- 执行控制
- 当前编辑状态的反馈

## 2. 当前主要区域

### 输入区

输入区是参数文本、可编辑内容和执行型输入的主要工作区。

### 书签 / 示例区

书签树负责展示可复用示例和用户保存输入。当前 GUI 会区分系统提供示例和用户自管内容。

### 动作控制区

界面通过按钮和周边控件暴露 execute、edit、focus、reset、导入相关动作及其它 VOICE 专用操作。

## 3. 当前交互原则

当前 VOICE GUI 遵循这些原则：

- 示例内容与用户内容应能被视觉区分
- 编辑状态应可见
- 破坏性动作不应与安全的可复用动作采用同样的呈现
- 执行相关控件应尽量靠近它所作用的输入区

## 4. 当前状态信号

当前设计使用少量状态线索，例如：

- linked 与 not-linked
- editable 与 read-oriented
- example content 与 user-managed content

这些状态信号当前通过 VOICE 图片、标签和面板状态处理实现，而不是依赖一套独立的状态引擎。

## 5. 当前边界

本文描述的是现有 VOICE 类已经体现出来的当前 GUI 规范。

它不是未来控件路线图，也不应保留下面这些规划性章节：

- implementation roadmap
- future GUI phases
- step-by-step change logs

## 6. 相关文档

- `VOICE_MODULE_ARCHITECTURE.md` / `VOICE_MODULE_ARCHITECTURE_zh.md`
