# egps-shell 插件接入参考

本文说明当前 `egps-shell` 中插件接入机制的真实行为，并以当前 `egps-main` 代码实现为依据。

## 总览

当前 `egps-shell` 有两条相关但不同的插件路径：

1. **Plugins 菜单直接加载**：从 `~/.egps2/config/plugin/*.jar` 读取插件
2. **模块发现与配置合并加载**：由主壳层、Module Gallery 和模块管理表共同使用

这两条路径有重叠，但并不是同一条自动机制，不应混写成“插件放进去就自动完成所有接入”。

## Built-in Modules 与 Plugin Modules

- **Built-in modules**：来自主程序类路径的模块，包括源码编译出的模块和 `dependency-egps/*` 中的模块
- **Plugin modules**：放在 `~/.egps2/config/plugin/` 下的外部 JAR
- 两者都可能实现 `IModuleLoader`，但它们进入壳层的运行时路径不同

## 运行时路径

### 1. Plugins 菜单路径

`Plugins` 菜单由 `egps2.plugin.manager.PluginOperation` 配置。

当前行为：

- 扫描 `~/.egps2/config/plugin/` 下的 JAR
- 对每个 JAR 读取 `eGPS2.plugin.properties`
- 在用户点击菜单项时按需加载插件入口类
- 这条路径仍然独立于模块发现使用的 config-merge 路径

也就是说，即使壳层已经有更广义的模块发现系统，插件依然可以通过 JAR 菜单入口被直接打开。

### 2. 发现 + 配置合并路径

主壳层加载路径主要由以下组件协同完成：

- `egps2.frame.features.ModuleDiscoveryService`
- `egps2.frame.features.EGPS2ServiceLoader`
- `egps2.frame.MainFrameProperties`

当前行为：

- `ModuleDiscoveryService` 使用 Reflections 扫描整个 JVM 类路径
- 同时手动扫描 `~/.egps2/config/plugin/` 下的插件 JAR
- `EGPS2ServiceLoader.loadWithDiscovery(...)` 将扫描结果与 `~/.egps2/config/egps2.loading.module.config.txt` 合并
- `MainFrameProperties.getExistedLoaders()` 返回合并后被标记为当前要加载的模块集合

## “被发现”是什么意思

仅仅被发现，并不意味着模块会立刻进入当前激活模块列表。

当前合并逻辑是：

- **配置里有 + 扫描也有**：记为 available；只有当配置标记为 `true` 时才激活
- **配置里没有 + 扫描到了**：记为 newly discovered；默认不加载
- **配置里有 + 当前没扫描到**：记为 unavailable

这个区别很重要，因为当前壳层跟踪的模块数量，大于它立即激活的模块数量。

## Module Gallery 与模块管理界面

当前壳层**不是**“凡是扫描到的类都会自动出现在激活状态的 Gallery 中”。

实际情况是：

- `DemoButtonsOrganizer` 使用 `MainFrameProperties.getExistedLoaders()`
- `getExistedLoaders()` 反映的是配置合并后的加载结果，而不是原始扫描集合
- 因此 Gallery 遵循的是当前配置/激活状态
- 模块管理表（`itoolmanager`）使用 `MainFrameProperties.getAllProviders()`，可以展示 available、newly discovered、unavailable 等更完整的状态

所以更准确的表述是：

> `egps-shell` 会发现比当前立即加载更多的模块，而当前 Gallery 视图遵循的是配置支持下的 active loader set。

## 发现来源与规则

### 扫描来源

当前扫描来源包括：

- JVM 类路径
- `~/.egps2/config/plugin/` 下的插件 JAR

这比旧的固定包名写法更宽，不应再沿用只列少数包前缀的说法。

### 排除规则

当前 `ModuleDiscoveryService` 会排除：

- 接口
- 抽象类
- SubTab 风格类
- 模板基类，例如 `FastBaseTemplate` 本身

它**不是**通过一个单独的“core module exclusion”分支来解释为什么某些模块不会直接成为新激活的 Gallery 模块。核心模块处理与配置合并是不同问题。

### 重复处理

如果同一个模块类同时出现在类路径和插件 JAR 中：

- 优先使用类路径版本
- 忽略插件副本
- 壳层记录 warning，并显示重复警告对话框

## 插件包格式

当前 `egps2.plugin.manager.PluginProperty` 真正生效的字段是：

- **必需**：`launchClass`
- **可选且生效**：`dependentJars`

其它元数据字段可以出现在属性文件中，但当前解析器不会把它们作为核心加载条件。

示例：

```properties
launchClass=com.example.myplugin.MyPluginLoader
dependentJars=libA.jar;libB.jar
```

## 当前支持的插件实现方式

当前插件模块常见的两种写法是：

### 1. 直接实现 `IModuleLoader`

适用于插件需要显式控制 loader 行为和 panel 构造的情况。

### 2. 继承 `FastBaseTemplate`

适用于较简单的插件，让一个类同时承担 loader 和 panel 的职责，并复用模板基类。

当前重要规则：

- 插件 JAR 中的 `FastBaseTemplate` 子类可以被视为真实插件模块
- 编译进壳层源码/类路径的模板类则按模板侧基础设施对待，不会自动暴露为普通插件模块

## 实际总结

就当前 `egps-shell` 实现来说，插件接入应这样描述：

- 插件可作为 JAR 安装到 `~/.egps2/config/plugin/`
- 壳层同时存在直接 Plugins 菜单路径和 discovery/config-merge 路径
- 扫描会覆盖类路径模块和插件 JAR
- 被发现并不意味着模块自动成为 Gallery 中的激活项
- 当前 Gallery 遵循配置支持的 active loader set
- 模块管理表可以展示更完整的已知模块状态

## 相关文档

- `plugin_gallery_integration_statement.md` / `plugin_gallery_integration_statement_zh.md`：插件发现、配置合并加载与 Module Gallery 的关系
- `plugin_types_statement.md` / `plugin_types_statement_zh.md`：当前支持的插件实现方式及其边界
- `module_discovery_exclusion_rules.md` / `module_discovery_exclusion_rules_zh.md`：当前模块发现过滤规则
