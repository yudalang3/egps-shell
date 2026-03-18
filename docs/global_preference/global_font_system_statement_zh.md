# 当前全局字体系统说明

本文概述当前 `egps-shell` 中全局字体系统已经实现了什么，以及它在运行时如何生效。

## 1. 当前已经具备的能力

当前系统已经实现：

- 在 `LaunchProperty` 中维护多组语义化字体
- 通过 `LaunchPropertyDTO` 进行持久化
- 在 `Launcher` 启动流程中把字体应用到 `UIManager`
- 在 `PreferencePanel` 中按类别调整这些字体
- 用户点击应用后立即刷新当前主界面
- FAQ / History 帮助入口可根据语言设置切换中英文页面

## 2. 当前运行时行为

当前运行时字体应用链路如下：

### 2.1 启动时

- `LaunchProperty.getInstance()` 读取已有配置；如果没有，则生成默认值
- `Launcher` 在创建主界面前调用 `launchProperty.applyFontsToUIManager()`
- 标准 Swing 组件优先使用这些全局字体设置

### 2.2 调整设置时

- 用户在 Preference 中修改字体
- `PreferencePanel.applyAndClose()` 将修改写回 `LaunchProperty`
- 同步刷新 `UIManager`
- 对当前主界面执行 `updateComponentTreeUI(...)`
- 将结果保存到 JSON 配置文件

## 3. 当前覆盖的主要界面类型

当前系统重点覆盖：

- 菜单与菜单项
- 标签页标题
- 模块标题与模块正文
- HTML 文档字体
- 对话框标题、正文、按钮
- 文本输入控件与文本区域
- 表格、表头、列表、树
- ToolTip、ToolBar
- ProgressBar、Slider、Spinner、ScrollPane 等常见组件

## 4. 当前帮助文档集成

当前帮助系统已经和全局字体专题文档打通：

- `ActionFaq` 根据语言设置加载 `Faq_English.html` 或 `Faq_Chinese.html`
- History 对话框根据语言加载 `History_English.html` 或 `History_Chinese.html`
- 这些帮助页当前介绍的是“全局字体系统”这一专题，而不是整个项目的总 FAQ

## 5. 当前限制

当前系统不是“所有界面百分之百自动统一”的承诺，仍然有这些边界：

- 手工写死字体的旧代码可能不会自动跟随
- 第三方组件未必完全遵守 `UIManager`
- 自绘文本、图形画布、特殊组件仍然需要显式处理
- 某些历史帮助页和专题页面的字体继承效果，取决于各自实现方式

## 6. 当前建议

对于当前壳层和模块开发者，推荐：

- 优先使用 `LaunchProperty` 中已有的语义化字体入口
- 尽量避免新增硬编码字体
- 如果是标准 Swing 组件，先依赖 `UIManager`，只在必要时手动覆盖
- 如果是专题页面或自绘内容，显式说明自己的字体策略

## 7. 相关文档

- `README.md`：本目录文档入口
- `global_font_system_design.md`：当前设计目标与边界
- `global_font_system_code_map.md`：当前相关代码位置与集成点
- `how_to_use_global_preference.md`：模块和插件如何使用当前全局偏好与字体系统
