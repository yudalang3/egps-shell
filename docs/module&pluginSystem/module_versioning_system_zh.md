# 模块版本系统说明

本文说明当前 `egps-shell` 使用的模块版本契约。

## 当前契约

所有模块加载器都实现 `IModuleLoader.getVersion()`，并返回一个 `ModuleVersion` 值。

当前版本模型使用语义化三元组：

- `major`
- `minor`
- `patch`

支持类型是 `egps2.modulei.ModuleVersion`。

## 当前核心 API

### `ModuleVersion`

当前 `ModuleVersion` 提供：

- 从 `major.minor.patch` 构造
- 从字符串解析
- 比较
- 基于 major version 的兼容性检查
- `toString()` 格式化

### `IModuleLoader.getVersion()`

当前接口要求每个模块加载器都提供一个版本。

典型用法：

- built-in 壳层模块通常返回 `EGPSProperties.MAINFRAME_CORE_VERSION`
- 自定义模块和插件可以返回自己的 `new ModuleVersion(...)`

## 当前共享核心版本

当前代码库定义了一个共享核心常量：

- `EGPSProperties.MAINFRAME_CORE_VERSION`

在当前源码快照中，该常量是 `0.0.1`。

除非某个 built-in 模块或模板提供了更具体的版本值，否则它们常使用这个共享版本。

## 当前系统能做什么

当前版本契约支持：

- 在 UI 或诊断信息中展示模块版本
- 在代码中比较版本
- 为 built-in 模块和插件暴露一个稳定的版本字段

## 当前边界

当前系统本身并不提供：

- 自动更新分发
- 模块之间的依赖解析
- 远程兼容性协商

除非未来有单独实现，否则这些能力不应被写成当前运行时特性。

## 实现依据

当前行为主要由以下文件定义：

- `src/egps2/modulei/IModuleLoader.java`
- `src/egps2/modulei/ModuleVersion.java`
- `src/egps2/EGPSProperties.java`
