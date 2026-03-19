# 模块发现中的 SubTab 排除规则

本文说明当前 `egps-shell` 中，为什么 VOICE 的 SubTab 类不会作为普通模块被 discovery 系统发现。

## 当前规则

继承 `DockableTabModuleFaceOfVoice` 的类，不会被 `ModuleDiscoveryService` 当作独立模块处理。

当前理由是：

- SubTab 属于某个父级 dockable VOICE 模块的一部分
- 它依赖父模块提供的容器环境和生命周期
- 它不应该像独立模块加载器那样被单独实例化

## 为什么需要这条规则

discovery 系统的目标是找出可直接被壳层加载的模块。

SubTab 不符合这个角色：

- 它们是更大 VOICE 模块中的子视图
- 不应该作为独立条目出现在模块管理视图中
- 也不应该像普通顶层模块那样被自动跟踪

## 当前实现依据

当前排除逻辑通过 `ModuleDiscoveryService` 中的 `isSubTabClass(...)` 完成。

这段逻辑会：

- 加载 `egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice`
- 判断发现到的类是否继承它
- 将匹配子类从普通模块列表中排除

## 典型结构

```text
Parent module loader
  -> creates a dockable VOICE face
     -> owns one or more SubTab classes
```

在这类结构里，父模块才是模块发现系统的目标；SubTab 只是父模块的内部组成部分。

## 与其它排除规则的关系

这条规则只是整个 discovery 过滤集合中的一部分。

另见：

- `module_discovery_exclusion_rules.md` / `module_discovery_exclusion_rules_zh.md`
- `../../manuals/01_VOICE_architecture.md` / `../../manuals/01_VOICE_architecture_zh.md`
