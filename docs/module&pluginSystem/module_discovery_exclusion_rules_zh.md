# 模块发现排除规则

本文说明当前 `egps-shell` 中 `ModuleDiscoveryService` 在扫描 `IModuleLoader` 实现时使用的排除规则。

## 目的

当前 discovery 服务会广泛扫描：

- JVM 类路径
- 已经存在于运行时的依赖 JAR
- `~/.egps2/config/plugin/` 下的插件 JAR

但并不是每个扫描到的 `IModuleLoader` 实现都应该被当作普通壳层模块。排除规则定义了这条边界。

## 当前排除规则

### 1. 接口与抽象类

这些类会被排除，因为它们不能直接作为可实例化的模块入口。

### 2. VOICE SubTab 类

继承 `DockableTabModuleFaceOfVoice` 的类会被排除，因为它们是父 VOICE 模块内部的子视图，而不是独立模块。

### 3. 模板基类

以下类被排除为模板基础设施：

- `egps2.plugin.fastmodtem.FastBaseTemplate`
- `egps2.plugin.fastmodtem.IndependentModuleLoader`

### 4. 编译进壳层源码/类路径的模板子类

如果某个类继承了 `FastBaseTemplate`，但它来自壳层源码/类路径而不是插件 JAR，那么它会被视为模板侧基础设施，并从普通 discovery 中排除。

### 5. 打包在插件 JAR 中的模板子类

这类模板子类会被保留，因为它们代表真实插件模块，而不是模板脚手架。

## 重要澄清

当前 `ModuleDiscoveryService` 的最终过滤方法中，并没有单独的“core module exclusion”分支。

因此，本文不应把当前行为描述成“内置核心模块是靠一个专门的排除分支过滤掉的”。

## 重复处理

如果相同的模块类名同时存在于：

- 类路径
- 插件 JAR

那么优先使用类路径副本，并忽略插件副本，同时记录 warning。

这属于重复解析，不属于普通过滤链上的排除规则。

## 相关文档

- `module_discovery_subtab_exclusion.md` / `module_discovery_subtab_exclusion_zh.md`
- `plugin_integration.md` / `plugin_integration_zh.md`
- `plugin_types_statement.md` / `plugin_types_statement_zh.md`
- `../voiceFramework/VOICE_MODULE_ARCHITECTURE.md` / `../voiceFramework/VOICE_MODULE_ARCHITECTURE_zh.md`
