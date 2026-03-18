# 当前插件与 Module Gallery 集成说明

本文说明 `egps-shell` 中插件发现、配置支持的模块加载以及 Module Gallery 之间的当前最终关系。

更完整的插件机制说明，请参见 `plugin_integration.md` / `plugin_integration_zh.md`。

## 当前结果

当前壳层同时结合了：

- 从 `~/.egps2/config/plugin/` 加载插件 JAR
- 基于类路径的模块发现
- 基于配置的激活模块选择
- 基于 active loader set 的 Gallery 展示

这意味着插件模块属于壳层运行时的一部分，但它们仍然受和其它模块相同的 active / inactive 决策约束。

## Gallery 实际展示的是什么

当前 Module Gallery 并不是简单地把 discovery 能找到的每个类都展示出来。

当前行为：

- `ModuleDiscoveryService` 可以发现插件模块
- `EGPS2ServiceLoader.loadWithDiscovery(...)` 会把发现结果与 `~/.egps2/config/egps2.loading.module.config.txt` 合并
- `MainFrameProperties.getExistedLoaders()` 返回当前 active loader set
- `DemoButtonsOrganizer` 和 `ModuleInspector` 用这组 active loader 向用户展示模块

因此，Gallery 跟随的是当前激活模块集合，而不是原始发现结果。

## 插件为什么仍然可见

即使 Gallery 依据 active loader set 展示，插件相关信息在当前壳层里仍然可以通过几种方式可见：

- `Plugins` 菜单提供直接加载入口
- 模块管理视图可以暴露更完整的已跟踪模块状态
- 如果某个 active module 来自插件目录，Gallery 和 Inspector 可以附加 `[Plug]` 标识

这是建立在当前加载模型之上的展示细节，不是另一套独立的激活系统。

## 重要边界

当前实现应按以下约束来描述：

- 被发现的插件模块默认不会自动激活
- 当同一模块类同时出现在类路径和插件 JAR 中时，优先使用类路径版本
- 模板基类不应被当作普通 active module
- 插件 discovery 与 Plugins 菜单直接加载相关，但不是同一条路径

## 阅读建议

推荐按下面顺序阅读：

- `plugin_integration.md` / `plugin_integration_zh.md`：插件加载与发现行为的完整参考
- 本文：面向 Gallery 结果的简明解释
- `plugin_types_statement.md` / `plugin_types_statement_zh.md`：当前支持的插件实现风格
