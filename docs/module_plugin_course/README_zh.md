# eGPS 模块与插件开发教程

> **欢迎来到 eGPS 模块开发教程！**
>
> 本教程将引导你从零开始学习如何为 eGPS 开发自定义模块和插件。
> 无论你是想快速创建一个简单工具，还是开发复杂的分析模块，这里都有完整的指导。

---

## 📚 教程目录

### 🚀 [01. 快速开始](01_QUICK_START_zh.md)

**适合人群**：所有开发者

**学习内容**：
- 5 分钟创建你的第一个模块
- 使用自动化脚本快速生成插件
- 理解 Plugin 和 Built-in 两种部署方式
- 安装和测试插件

**亮点**：
- ✅ 一键生成示例插件
- ✅ 立即看到效果
- ✅ 理解核心概念

**时间**：5-10 分钟

---

### 🔌 [02. 插件开发教程（Plugin 模式）](02_PLUGIN_DEVELOPMENT_zh.md)

**适合人群**：希望开发外部扩展的开发者

**学习内容**：
- 两种插件开发方式详解
  - 方式 1：继承 `FastBaseTemplate`（简单快速）
  - 方式 2：实现 `IModuleLoader`（灵活强大）
- 完整的开发流程
- 编译、打包、安装步骤
- 进阶技巧和最佳实践

**示例项目**：
- 文本处理工具（FastBaseTemplate）
- 数据分析工具（IModuleLoader）

**时间**：30-60 分钟

---

### 🏠 [03. 内置模块开发教程（Built-in 模式）](03_BUILTIN_DEVELOPMENT_zh.md)

**适合人群**：希望开发核心功能的开发者

**学习内容**：
- Built-in 与 Plugin 的关系
- 如何将插件转为内置模块
- 内置模块的优势和适用场景
- 两种模式的自由转换

**核心概念**：
- **本质相同**：代码和 JAR 结构完全一样
- **位置决定**：存放位置决定展示方式
- **可以互换**：一个命令即可转换

**示例项目**：
- 系统信息工具

**时间**：20-30 分钟

---

### 🏗️ [04. 架构原理说明](04_ARCHITECTURE_zh.md)

**适合人群**：希望深入理解系统的开发者

**学习内容**：
- eGPS 模块系统架构
- `IModuleLoader` 接口设计
- `ModuleFace` 基类详解
- `FastBaseTemplate` 工作原理
- 模块发现机制（`ModuleDiscoveryService`）
- Plugin 和 Built-in 的技术实现
- ClassLoader 和类加载机制
- 扩展点和自定义开发

**适合场景**：
- 需要定制模块加载机制
- 开发复杂的插件系统
- 贡献代码到 eGPS 核心

**时间**：60-90 分钟

---

### 📄 [配置文件说明书](eGPS2.plugin.properties_zh.md)

**适合人群**：所有插件开发者

**学习内容**：
- `eGPS2.plugin.properties` 配置文件格式详解
- 所有配置项的详细说明（必需/可选）
- 配置项格式要求和示例
- 常见错误和解决方法
- 最佳实践和配置模板

**核心配置项**：
- ✅ `launchClass` - 启动类（必需）
- ✅ `pluginName` - 插件名称（推荐）
- ✅ `version` - 版本号（推荐）
- ✅ `author` - 作者信息（可选）
- ✅ `description` - 功能描述（可选）
- ✅ `dependentJars` - 依赖库（按需）

**时间**：10-15 分钟

---

## 🛠️ 自动化工具

### create-all-test-plugins.sh

**位置**：`docs/module_plugin_course/create-all-test-plugins.sh`

**功能**：
- 一键生成 4 个功能完整的示例插件
- 自动编译、打包
- 输出到 `plug_dist` 目录

**使用方法**：

```bash
# 进入项目根目录
cd /path/to/egps-main.gui

# 运行脚本
bash docs/module_plugin_course/create-all-test-plugins.sh

# 查看生成的文件
ls plug_dist/
```

**生成内容**：
- `fastbase-plugin.jar` - 继承 FastBaseTemplate 的简单示例
- `direct-plugin.jar` - 实现 IModuleLoader 的复杂示例
- `calculator-plugin.jar` - 实用计算器（含中英文文档）
- `clipboardpaste-plugin.jar` - 路径转换工具（Windows ↔ WSL）
- `README.txt` - 完整安装说明

---

## 📖 学习路径推荐

### 🌟 路径 1：快速上手（推荐新手）

```
1. 快速开始 (01)
   └─ 运行自动脚本，看到效果
      └─ 5 分钟

2. 插件开发 - FastBaseTemplate (02)
   └─ 学习简单的开发方式
      └─ 30 分钟

3. 实践项目
   └─ 创建自己的第一个插件
      └─ 1-2 小时
```

**总时间**：2-3 小时
**产出**：一个可用的插件

---

### 🚀 路径 2：全面掌握（推荐进阶）

```
1. 快速开始 (01)
   └─ 了解基本概念
      └─ 10 分钟

2. 插件开发 - 两种方式 (02)
   └─ 掌握完整开发流程
      └─ 60 分钟

3. 内置模块开发 (03)
   └─ 理解部署方式
      └─ 30 分钟

4. 架构原理 (04)
   └─ 深入理解系统
      └─ 90 分钟

5. 实践项目
   └─ 开发复杂的插件
      └─ 4-8 小时
```

**总时间**：6-10 小时
**产出**：深入理解 + 高质量插件

---

### 🎯 路径 3：按需学习（推荐实用主义者）

根据你的需求选择：

| 需求 | 阅读章节 | 时间 |
|------|----------|------|
| **快速创建简单工具** | 01 + 02 (方式1) | 30 分钟 |
| **开发复杂插件** | 01 + 02 (方式2) | 90 分钟 |
| **了解部署方式** | 01 + 03 | 40 分钟 |
| **贡献核心代码** | 全部 | 3 小时 |
| **定制加载机制** | 04 + 源码 | 2 小时 |

---

## 🎯 核心概念速查

### Plugin 模式 vs Built-in 模式

```
┌─────────────────────────────────────────────────────┐
│               同一个 JAR 文件                        │
│         (代码、结构完全相同)                         │
└──────────────────┬──────────────────────────────────┘
                   │
       ┌───────────┴───────────┐
       │                       │
       ▼                       ▼
┌──────────────┐        ┌──────────────┐
│ Plugin 模式   │        │ Built-in 模式 │
├──────────────┤        ├──────────────┤
│ 位置:         │        │ 位置:         │
│ ~/.egps2/    │        │ dependency-  │
│ config/      │        │   egps/      │
│ plugin/      │        │              │
├──────────────┤        ├──────────────┤
│ 显示:         │        │ 显示:         │
│ "Plugins"    │        │ "iTools"     │
│ 菜单         │        │ 菜单         │
├──────────────┤        ├──────────────┤
│ 适合:         │        │ 适合:         │
│ - 外部扩展   │        │ - 核心功能   │
│ - 用户工具   │        │ - 默认工具   │
│ - 实验功能   │        │ - 随应用发布 │
└──────────────┘        └──────────────┘
       │                       │
       └───────────┬───────────┘
                   │
                   ▼
         ┌─────────────────┐
         │ 都出现在         │
         │ Module Gallery  │
         │    (Ctrl+2)     │
         └─────────────────┘
```

**关键点**：
- ✅ 代码完全一样
- ✅ 可以自由转换（移动 JAR 文件）
- ✅ 都会被自动发现
- ✅ 都使用相同的接口
- ✅ Plugin 模式会显示 **[Plug]** 标识
- ✅ 重复模块会弹出警告对话框

### 🏷️ [Plug] 标识（v2.1+）

Plugin 模式的模块在 Module Gallery 中会显示蓝色 **[Plug]** 标识：

```
Module Gallery:
  ├─ File Manager          (Built-in)
  ├─ Simple Calculator [Plug]  (Plugin)
  └─ ClipboardPaste [Plug]     (Plugin)
```

**作用**：
- 快速识别外部插件
- 区分内置模块和外部扩展
- 用户友好的视觉提示

### ⚠️ 重复模块检测（v2.1+）

当同一模块同时存在于两个位置时：

```
~/.egps2/config/plugin/my-plugin.jar  ← Plugin
dependency-egps/my-plugin.jar         ← Built-in
```

**系统行为**：
1. ✅ 优先使用 classpath (dependency-egps) 版本
2. ⚠️ 控制台输出警告信息
3. 📋 弹出警告对话框提示用户
4. 💡 建议删除其中一个 JAR 文件

**推荐做法**：保留一个位置的 JAR，删除另一个

---

### 两种开发方式

#### 方式 1: 继承 FastBaseTemplate

```java
public class MyTool extends FastBaseTemplate {
    public MyTool() {
        super();
        // 构建 UI
    }

    public String getTabName() { return "我的工具"; }
    public String getShortDescription() { return "简单工具"; }
    public int[] getCategory() { return new int[]{0,0,0,0}; }
}
```

**特点**：代码少、快速开发、适合简单工具

---

#### 方式 2: 实现 IModuleLoader

```java
// 加载器
public class MyModuleLoader implements IModuleLoader {
    private MyPanel panel;

    public MyModuleLoader() {
        panel = new MyPanel(this);
    }

    public ModuleFace getFace() { return panel; }
    // 其他方法...
}

// 面板
class MyPanel extends ModuleFace {
    public MyPanel(IModuleLoader loader) {
        super(loader);
        // 构建 UI
    }
    // 其他方法...
}
```

**特点**：架构清晰、完全控制、适合复杂功能

---

## 📦 JAR 文件结构

无论哪种方式，JAR 文件结构都相同：

```
myplugin.jar
├── eGPS2.plugin.properties     # 配置文件（必需）
│   ├── launchClass=...
│   ├── pluginName=...
│   ├── version=...
│   └── author=...
│
└── com/mycompany/              # 你的代码
    ├── MyPlugin.class
    └── MyPanel.class
```

---

## 🎨 示例项目

### 自动生成的示例插件

使用脚本一键生成 4 个功能完整的示例插件：

```bash
# 使用脚本生成所有示例
bash docs/module_plugin_course/create-all-test-plugins.sh
```

**生成的插件**：

1. **fastbase-plugin.jar** - FastBaseTemplate 简单示例
   - 代码量：约 70 行
   - 特点：最简单的实现方式
   - 适合：快速开发工具型插件

2. **direct-plugin.jar** - IModuleLoader 复杂示例
   - 代码量：约 200 行
   - 特点：架构清晰，Loader/Panel 分离
   - 适合：大型复杂插件

3. **calculator-plugin.jar** - 实用计算器 ⭐
   - 代码量：约 400 行
   - 功能：基本算术运算、键盘支持、错误处理
   - 文档：完整的中英文 HTML 文档
   - 适合：学习如何实现实用工具

4. **clipboardpaste-plugin.jar** - 路径转换工具 ⭐
   - 代码量：约 500 行
   - 功能：Windows ↔ WSL 路径转换、剪贴板集成
   - 文档：完整的中英文 HTML 文档
   - 适合：学习如何开发开发者工具

### 内置模块示例

参考源码：
- 文件管理器：`src/egps2/builtin/modules/filemanager/`
- 模块画廊：`src/egps2/builtin/modules/gallerymod/`
- 文本编辑器：`src/egps2/builtin/modules/largetextedi/`

---

## 🔧 开发工具推荐

### IDE

- **IntelliJ IDEA**（推荐）
  - 优秀的 Java 支持
  - 内置 Maven/Gradle
  - 强大的重构功能

- **Eclipse**
  - 免费开源
  - 丰富的插件生态

### 构建工具

- **手动脚本**（本教程使用）
  - 简单直接
  - 易于理解

- **Maven**（可选）
  - 依赖管理
  - 标准化构建

- **Gradle**（可选）
  - 灵活强大
  - Groovy/Kotlin DSL

---

## 📚 相关文档

### eGPS 核心文档

- [CLAUDE.md](../../CLAUDE.md) - 项目整体说明
- [module&pluginSystem/](../module&pluginSystem/) - 模块系统技术文档
- [understanding/](../understanding/) - 架构理解文档

### 源码参考

- 核心接口：`src/egps2/modulei/IModuleLoader.java`
- 基础类：`src/egps2/frame/ModuleFace.java`
- 快速模板：`src/egps2/plugin/fastmodtem/FastBaseTemplate.java`
- 模块发现：`src/egps2/frame/features/ModuleDiscoveryService.java`
- 配置读取：`src/egps2/plugin/manager/PluginProperty.java`
- 插件加载：`src/egps2/plugin/manager/PluginOperation.java`
- 内置模块示例：`src/egps2/builtin/modules/`

### 教程文档

- [01. 快速开始](01_QUICK_START_zh.md)
- [02. 插件开发教程](02_PLUGIN_DEVELOPMENT_zh.md)
- [03. 内置模块开发教程](03_BUILTIN_DEVELOPMENT_zh.md)
- [04. 架构原理说明](04_ARCHITECTURE_zh.md)
- [配置文件说明书](eGPS2.plugin.properties_zh.md) ⭐ **必读**

---

## 🤝 获取帮助

### 常见问题

查看各教程文档的"常见问题"部分：
- [01 - 快速开始 FAQ](01_QUICK_START_zh.md#常见问题)
- [02 - 插件开发 FAQ](02_PLUGIN_DEVELOPMENT_zh.md#常见问题)
- [03 - 内置模块 FAQ](03_BUILTIN_DEVELOPMENT_zh.md#常见问题)

### 调试技巧

```java
// 使用日志
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyPlugin extends FastBaseTemplate {
    private static final Logger logger = LoggerFactory.getLogger(MyPlugin.class);

    public MyPlugin() {
        super();
        logger.info("MyPlugin initialized");
        logger.debug("Debug info: {}", someValue);
    }
}
```

### 查看控制台

启动时能看到模块加载的详细信息：

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" egps2.Launcher
```

---

## ✨ 快速开始

**第一步**：运行自动脚本

```bash
cd /path/to/egps-main.gui
bash docs/module_plugin_course/create-all-test-plugins.sh
```

**第二步**：安装插件

```bash
# 方式 A: 作为插件
cp plug_dist/*.jar ~/.egps2/config/plugin/

# 或方式 B: 作为内置模块
cp plug_dist/*.jar dependency-egps/
```

**第三步**：启动 eGPS

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" egps2.Launcher
```

**第四步**：查看你的模块

- 按 `Ctrl+2` 打开 Module Gallery
- 或从菜单选择 `Plugins` / `iTools`

---

## 🎓 学习提示

1. **边学边做**：每看完一章，立即动手实践
2. **参考示例**：内置模块的源码是最好的学习资料
3. **小步前进**：先从简单的 FastBaseTemplate 开始
4. **理解原理**：掌握架构后，开发更得心应手
5. **善用工具**：自动化脚本能节省大量时间

---

## 📅 版本信息

- **教程版本**：1.0
- **eGPS 版本**：2.1+
- **最后更新**：2025-12-05
- **作者**：eGPS Dev Team

---

## 📝 更新日志

### 2025-12-05 - v1.0
- ✅ 完整的教程体系
- ✅ 自动化脚本工具
- ✅ 详细的示例代码
- ✅ 架构原理说明

---

## 🚀 开始学习

准备好了吗？让我们从[快速开始](01_QUICK_START_zh.md)开始吧！

祝你学习愉快！ 🎉

---

**eGPS Dev Team**
2025-12-05
