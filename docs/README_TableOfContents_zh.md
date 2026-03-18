# egps-shell 文档索引

这个 `docs/` 目录是 `egps-shell` 的参考文档集合。

`egps-shell` 是面向外部说明的 GUI 壳层与基础运行时；它当前对应的闭源实现位于 `egps-main` 仓库中。

## 写作口径

`docs/` 目录默认应以 `egps-shell` 为主语。

整套文档建议统一采用下面的写法：

- 面向外部说明时，使用 `egps-shell` 作为主语。
- 提到实现依据时，再说明该行为在 `egps-main` 中由哪些类或模块实现。

## README、docs 与 manuals 的边界

| 位置 | 主要主题 | 用途 | 典型问题 |
| --- | --- | --- | --- |
| `README.md`、`README_zh.md` | `egps-main` | 仓库入口说明 | 这个仓库是什么？当前源码树怎么构建和运行？ |
| `docs/` | `egps-shell` | 参考文档 | 壳层如何启动？模块如何发现？VOICE 如何工作？ |
| `manuals/` | `egps-shell` | 教程与实践指南 | 我该怎样学习这个框架？怎样一步一步开发模块或插件？ |

## 当前目录中的文档类型

### 运行时与架构参考

- `docs/launchSystem/understanding_how_main.gui_launched.md`：说明当前启动流程、启动入口类和运行时配置路径。
- `docs/module&pluginSystem/plugin_integration.md`：说明当前插件加载与模块发现的整体机制。
- `docs/module&pluginSystem/plugin_gallery_integration_statement.md`：说明当前插件、配置合并结果与 Module Gallery 之间的关系。
- `docs/module&pluginSystem/plugin_types_statement.md`：说明当前支持的插件实现类型及其边界。
- `docs/module&pluginSystem/module_discovery_exclusion_rules.md`：说明当前模块发现过滤规则。
- `docs/module&pluginSystem/module_discovery_subtab_exclusion.md`：说明为什么 VOICE 的 SubTab 不作为普通独立模块发现。
- `docs/module&pluginSystem/module_versioning_system.md`：说明当前模块版本契约与 `ModuleVersion` 用法。
- `docs/voiceFramework/VOICE_MODULE_ARCHITECTURE.md`：说明当前 VOICE 框架分层与接入方式。
- `docs/voiceFramework/VOICE-GUI.md`：说明当前 VOICE 图形界面的设计意图和交互模型。
- `docs/itoolsManager/itoolmanager_module_discovery_statement.md`：说明当前 ITools Manager 的模块发现与状态建模机制。

### 子系统与专题说明

- `docs/global_preference/README.md`：全局偏好与字体专题文档入口页。
- `docs/global_preference/global_font_system_statement.md`：说明当前全局字体系统的运行行为。
- `docs/global_preference/global_font_system_design.md`：说明当前全局字体系统的设计目标与边界。
- `docs/global_preference/global_font_system_code_map.md`：说明全局字体系统当前涉及的代码位置、集成点和维护坑位。
- `docs/global_preference/how_to_use_global_preference.md`：说明模块与插件如何正确使用当前全局偏好与字体系统。
### 类参考与用法文档

- `docs/class_reference_and_usage/how_to_use_MSG_dialog.md`：说明 MSG 对话框辅助 API 的当前用法。
- `docs/class_reference_and_usage/how_to_use_MSG_dialog_zh.md`：中文版 MSG 对话框辅助 API 用法说明。
- `docs/class_reference_and_usage/how_to_use_UnifiedAccessPoint.md`：说明壳层统一入口 `UnifiedAccessPoint` 的适用场景与用法。
- `docs/class_reference_and_usage/how_to_use_UnifiedAccessPoint_zh.md`：中文版 `UnifiedAccessPoint` 用法说明。
- `docs/class_reference_and_usage/how_to_use_launcher_and_args.md`：说明如何启动 `egps-shell` 以及何时保留 `@eGPS.args`。
- `docs/class_reference_and_usage/how_to_use_launcher_and_args_zh.md`：中文版启动命令与 `@eGPS.args` 用法说明。
- `docs/class_reference_and_usage/understanding_classes_overview.md`：英文版当前源码主要类总览。
- `docs/class_reference_and_usage/understanding_classes_overview_zh.md`：中文版当前源码主要类总览。
- `docs/class_reference_and_usage/understanding_frame_core.md`：英文版主框架核心类说明。
- `docs/class_reference_and_usage/understanding_frame_core_zh.md`：中文版主框架核心类说明。
- `docs/class_reference_and_usage/understanding_module_interfaces.md`：英文版模块接口与契约说明。
- `docs/class_reference_and_usage/understanding_module_interfaces_zh.md`：中文版模块接口与契约说明。
- `docs/class_reference_and_usage/understanding_panels_dialogs.md`：英文版可复用面板与对话框说明。
- `docs/class_reference_and_usage/understanding_panels_dialogs_zh.md`：中文版可复用面板与对话框说明。
- `docs/class_reference_and_usage/understanding_utilities_templates.md`：英文版共享工具类与模板辅助类说明。
- `docs/class_reference_and_usage/understanding_utilities_templates_zh.md`：中文版共享工具类与模板辅助类说明。
- `docs/files_with_main_methods.md`：英文版当前定义 `main(...)` 入口方法的源码文件清单。
- `docs/files_with_main_methods_zh.md`：当前定义 `main(...)` 入口方法的源码文件清单。

### 索引与维护导航

- `docs/README_TableOfContents.md`：当前 `docs/` 目录的英文索引页。
- `docs/README_TableOfContents_zh.md`：当前 `docs/` 目录的中文索引页。
- `docs/readme_docs_manuals_update_plan.md`：说明 `README`、`docs/`、`manuals` 三层文档当前分工。

## 推荐阅读顺序

如果第一次阅读这套参考文档，建议按下面顺序进入：

1. `docs/launchSystem/understanding_how_main.gui_launched.md`
2. `docs/module&pluginSystem/plugin_integration.md`
3. `docs/module&pluginSystem/module_discovery_exclusion_rules.md`
4. `docs/voiceFramework/VOICE_MODULE_ARCHITECTURE.md`
5. `docs/voiceFramework/VOICE-GUI.md`
6. `manuals/00Readme.md`
7. `manuals/module_plugin_course/README_zh.md`

## 文档中反复引用的运行时约定

- 用户配置目录：`~/.egps2/config`
- 模块加载配置：`~/.egps2/config/egps2.loading.module.config.txt`
- 插件目录：`~/.egps2/config/plugin/`
- 推荐运行参数文件：`@eGPS.args`
