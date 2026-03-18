# 当前插件类型说明

本文说明当前 `egps-shell` 支持的插件实现类型，以及这些类型在当前 `egps-main` 实现中的实际接入方式。

## 1. 当前支持的两类插件实现

### 1.1 直接实现 `IModuleLoader`

这是最直接的插件写法。

适用场景：

- 插件希望自己控制加载器与面板的关系
- 插件需要更清晰地拆分 loader、panel、辅助类
- 插件需要复杂初始化逻辑

当前约定：

- 入口类实现 `IModuleLoader`
- 入口类能被 `PluginOperation` 或 `ModuleDiscoveryService` 成功实例化
- 插件 JAR 根目录包含 `eGPS2.plugin.properties`

### 1.2 在插件 JAR 中继承 `FastBaseTemplate`

这是当前支持的简化写法。

适用场景：

- 插件结构比较简单
- 插件希望复用模板提供的面板与交互骨架
- 一个类同时承担 loader 和 face 的职责即可满足需要

当前规则：

- 插件 JAR 中的 `FastBaseTemplate` 子类可以被视为真实插件模块
- 壳层源码或类路径内部的模板类、模板示例类不会被当成普通插件模块暴露
- `FastBaseTemplate` 本身不是插件入口类

## 2. 当前插件入口文件

当前插件 JAR 需要在根目录提供 `eGPS2.plugin.properties`。

当前实现里真正生效的字段是：

```properties
launchClass=com.example.MyPluginLoader
dependentJars=libA.jar;libB.jar
```

字段说明：

- `launchClass`：插件入口类，全路径类名，当前为必需项
- `dependentJars`：可选项，用于补充同目录依赖 JAR

本文档不把其它元数据字段写成正式契约，因为当前 `PluginProperty` 实现没有解析更多字段。

## 3. 两条接入路径

当前插件模块可以通过两条路径进入壳层：

### 3.1 Plugins 菜单直接加载

由 `PluginOperation` 负责：

- 读取 `~/.egps2/config/plugin/` 下的插件 JAR
- 读取每个 JAR 中的 `eGPS2.plugin.properties`
- 使用 `launchClass` 创建模块加载器
- 在主菜单的 `Plugins` 下提供直接入口

### 3.2 模块发现与配置合并路径

由 `ModuleDiscoveryService` 与 `EGPS2ServiceLoader.loadWithDiscovery(...)` 负责：

- 扫描类路径中的模块
- 扫描 `~/.egps2/config/plugin/` 下的插件 JAR
- 过滤掉接口、抽象类、SubTab、模板基类等不应作为独立模块的类
- 将扫描结果与 `~/.egps2/config/egps2.loading.module.config.txt` 合并

## 4. 当前边界

当前插件类型说明应按下面的边界理解：

- “能被发现”不等于“默认自动激活”
- “位于插件目录”不等于“自动出现在当前激活的 Gallery 列表中”
- 模板基类不是插件入口
- 插件 JAR 中与类路径重复的模块类会被视为重复实现；当前优先使用类路径版本

## 5. 什么时候选哪一种

推荐选择：

- 简单插件：优先考虑插件 JAR 中继承 `FastBaseTemplate`
- 复杂插件：直接实现 `IModuleLoader`

如果插件需要明确分离加载器、面板、依赖处理或生命周期控制，直接实现 `IModuleLoader` 更稳妥。

## 6. 相关文档

- `plugin_integration.md`：当前插件接入机制总说明
- `plugin_gallery_integration_statement.md`：当前插件与 Module Gallery 的关系说明
- `module_discovery_exclusion_rules.md`：模块发现排除规则
