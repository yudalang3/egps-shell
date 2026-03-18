# ITools Manager 当前模块发现与管理机制说明

本文说明当前 `egps-shell` 中 ITools Manager 所依赖的模块发现与状态管理机制。

## 1. 当前目标

当前机制的目标不是“把所有能扫到的类直接塞进界面”，而是：

- 扫描当前可用模块
- 识别配置文件与实际模块之间的不一致
- 为每个模块记录当前状态
- 让模块管理界面可以显示更完整的已知模块视图
- 保留用户配置文件作为“当前是否激活”的事实依据

## 2. 当前发现来源

当前模块发现主要来自两部分：

- JVM 类路径中的 `IModuleLoader` 实现
- `~/.egps2/config/plugin/` 下插件 JAR 中的 `IModuleLoader` 实现

实现依据：

- `ModuleDiscoveryService` 负责扫描
- `EGPS2ServiceLoader.loadWithDiscovery(...)` 负责将扫描结果与配置文件合并

## 3. 当前状态模型

当前系统使用模块状态来表示“已知模块”的不同情况，而不是只有加载/未加载两个值。

常见状态包括：

- `AVAILABLE`
- `AVAILABLE_NOT_LOADED`
- `NEWLY_DISCOVERED`
- `UNAVAILABLE`
- `DEPRECATED`

这些状态用于区分：

- 配置里有、扫描里也有的模块
- 扫描到了但配置里没有的新模块
- 配置里还保留但当前已经不可加载的模块

## 4. 当前合并规则

当前合并规则应理解为：

- 配置文件仍决定“当前激活哪些模块”
- 扫描结果负责补充“当前还知道哪些模块”
- 新发现模块默认不会自动激活
- 配置中存在但当前无法加载的模块会被保留为状态信息，而不是悄悄丢掉

因此，ITools Manager 展示的是“更完整的已知模块视图”，而不是“原始配置文件内容的机械回显”。

## 5. 当前界面关系

当前相关界面分工如下：

- `MainFrameProperties.getExistedLoaders()`：返回当前激活模块集合
- `MainFrameProperties.getAllProviders()`：返回更完整的 provider 状态集合
- `ElegantJTable`：展示模块状态与配置相关信息
- `DemoButtonsOrganizer` / Module Gallery：更偏当前激活模块的展示

这意味着模块管理界面和 Gallery 的“可见范围”并不完全相同，这是当前设计的一部分。

## 6. 当前排除规则

当前发现机制不会把所有 `IModuleLoader` 实现都当作独立模块。

已知排除重点包括：

- 接口与抽象类
- SubTab 类
- 模板基类
- 不应作为独立模块显示的辅助实现

这些规则的细节由同目录及插件系统文档进一步说明。

## 7. 当前边界

当前机制的边界是：

- 它负责发现和建模模块状态
- 它不等同于“自动启用所有新模块”
- 它不替代插件菜单的直接加载逻辑
- 它也不承诺所有发现结果都会立即在 Gallery 中以激活状态显示

## 8. 相关文档

- `../module&pluginSystem/plugin_integration.md`
- `../module&pluginSystem/module_discovery_exclusion_rules.md`
- `../module&pluginSystem/module_discovery_subtab_exclusion.md`
