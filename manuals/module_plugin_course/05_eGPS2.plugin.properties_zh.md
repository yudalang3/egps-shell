# eGPS2.plugin.properties 配置文件说明书

## 📌 概述

`eGPS2.plugin.properties` 是当前插件 JAR 使用的入口配置文件，主要用于描述插件入口类和依赖关系。

**文件位置**：必须放在 JAR 包的根目录

**文件格式**：`key=value` 格式（每行一个键值对）

**编码要求**：UTF-8

---

## 📋 配置项详解

### 当前真实实现先看这一条

按当前加载器实现，真正参与插件加载的字段只有：

- ✅ `launchClass`（必需）
- ✅ `dependentJars`（可选）

其它常见字段可以作为补充说明写在文件里，但当前 `PluginProperty` / `PluginOperation` 不依赖它们来完成加载。

### 1. launchClass（必需）

**描述**：插件的启动类，必须是实现了 `IModuleLoader` 接口或继承了 `FastBaseTemplate` 的类的完全限定名。

**格式**：
```properties
launchClass=完整包名.类名
```

**示例**：
```properties
launchClass=com.mycompany.myplugin.MyPlugin
launchClass=test.direct.DirectPlugin
launchClass=control.KaKsLoader
```

**要求**：
- ✅ 必须是完整的类名（包含包路径）
- ✅ 类必须实现 `egps2.modulei.IModuleLoader` 接口或继承 `egps2.plugin.fastmodtem.FastBaseTemplate`
- ✅ 类必须有公共的无参构造函数
- ✅ 类必须在 JAR 包中存在
- ❌ 不能为空
- ❌ 不能使用旧格式 `location.IModuleLoader.class:xxx`（已废弃）

**错误示例**：
```properties
# ❌ 错误：缺少包名
launchClass=MyPlugin

# ❌ 错误：使用了旧格式（已不支持）
location.IModuleLoader.class:com.mycompany.MyPlugin

# ❌ 错误：为空
launchClass=
```

---

### 2. pluginName（补充元数据，当前加载器不依赖）

**描述**：可作为补充元数据书写，便于人工阅读或后续扩展。

**格式**：
```properties
pluginName=插件名称
```

**示例**：
```properties
pluginName=Text Processing Tool
pluginName=KaKs Calculator
pluginName=数据分析工具
```

**当前状态**：
- ✅ 可以使用中文、英文或其他 Unicode 字符
- ✅ 建议简短明了（不超过 30 个字符）
- ⚠️ 当前加载器不会依赖这个字段完成加载

**注意**：
- 当前模块 UI 名称仍以 `IModuleLoader.getTabName()` 为准
- UI 中的显示名称由 `IModuleLoader.getTabName()` 方法返回

---

### 3. version（补充元数据，当前加载器不依赖）

**描述**：可作为补充版本信息书写，但当前插件加载器不会读取它。

**格式**：
```properties
version=主版本.次版本.修订版
```

**示例**：
```properties
version=1.0.0
version=2.1.5
version=1.0.0-beta
```

**推荐规范**：
- 遵循语义化版本（Semantic Versioning）
- 主版本号：不兼容的 API 修改
- 次版本号：向下兼容的功能性新增
- 修订号：向下兼容的问题修正

**当前状态**：
- ⚠️ 当前加载器不会依赖这个字段完成加载
- ✅ 可以包含预发布标识（如 `-alpha`, `-beta`, `-rc1`）

---

### 4. author（补充元数据，当前加载器不依赖）

**描述**：作者信息，可作为补充说明书写。

**格式**：
```properties
author=作者名称
```

**示例**：
```properties
author=Zhang San
author=eGPS Dev Team
author=MyCompany Inc.
author=张三 <zhangsan@example.com>
```

**当前状态**：
- ✅ 可以包含联系方式（如邮箱）
- ✅ 可以使用任何字符
- ⚠️ 当前加载器不会依赖这个字段完成加载

---

### 5. description（补充元数据，当前加载器不依赖）

**描述**：插件功能的补充说明，主要供人工阅读。

**格式**：
```properties
description=功能描述
```

**示例**：
```properties
description=A tool for processing large text files
description=Calculate Ka/Ks values for sequence pairs
description=用于分析基因组数据的可视化工具
```

**当前状态**：
- ✅ 建议一句话描述（不超过 100 个字符）
- ✅ 可以使用多语言
- ⚠️ 当前加载器不会依赖这个字段完成加载

**注意**：
- 这个描述主要用于配置文件记录
- 模块的详细描述由 `IModuleLoader.getShortDescription()` 方法返回

---

### 6. dependentJars（可选）

**描述**：插件依赖的其他 JAR 文件列表。

**格式**：
```properties
dependentJars=jar文件名1;jar文件名2;jar文件名3
```

**示例**：
```properties
# 单个依赖
dependentJars=commons-lang3-3.12.0.jar

# 多个依赖（用分号分隔）
dependentJars=commons-lang3-3.12.0.jar;guava-31.1.jar;fastjson-1.2.83.jar

# 无依赖（可以不写这一行）
# dependentJars=
```

**要求**：
- ✅ 使用分号 `;` 分隔多个 JAR 文件
- ✅ 只需要文件名，不需要路径
- ✅ 依赖的 JAR 文件必须放在插件 JAR 所在的同一目录
- ⚠️ 完全可选，如果没有外部依赖可以不写
- ❌ 不要使用逗号或其他分隔符

**文件位置**：
```
~/.egps2/config/plugin/
├── my-plugin.jar              # 主插件 JAR
├── commons-lang3-3.12.0.jar   # 依赖 JAR 1
├── guava-31.1.jar             # 依赖 JAR 2
└── fastjson-1.2.83.jar        # 依赖 JAR 3
```

**注意**：
- 依赖的 JAR 会被添加到插件的类加载器中
- eGPS 核心依赖（如 Swing、Apache Commons 等）通常不需要列出
- 只列出插件特有的依赖

---

## 📝 完整示例

### 示例 1：最简配置（仅必需项）

```properties
launchClass=com.example.SimplePlugin
```

### 示例 2：推荐配置

```properties
# 启动类（必需）
launchClass=com.mycompany.textprocessor.TextProcessorPlugin

# 插件信息（推荐）
pluginName=Text Processor
version=1.0.0
author=John Doe
description=A tool for advanced text processing and analysis
```

### 示例 3：带依赖的完整配置

```properties
# 启动类（必需）
launchClass=control.KaKsLoader

# 插件信息
pluginName=KaKs Calculator
version=2.1.0
author=eGPS Dev Team <dev@egps.org>
description=Calculate Ka/Ks ratios for molecular evolution analysis

# 依赖库
dependentJars=biojava-core-6.0.5.jar;commons-math3-3.6.1.jar
```

### 示例 4：中文插件配置

```properties
# 启动类
launchClass=cn.example.dataviewer.DataViewerPlugin

# 插件信息（支持中文）
pluginName=数据可视化工具
version=1.5.2
author=张三团队
description=用于生物信息学数据的交互式可视化分析

# 依赖
dependentJars=echarts-java-1.0.jar
```

---

## 🔧 配置文件模板

### FastBaseTemplate 插件模板

```properties
# ============================================
# eGPS Plugin Configuration
# ============================================

# 启动类（必需）
# 继承 egps2.plugin.fastmodtem.FastBaseTemplate
launchClass=your.package.YourPluginClass

# 插件信息
pluginName=Your Plugin Name
version=1.0.0
author=Your Name
description=Brief description of your plugin

# 依赖（如果有）
# dependentJars=lib1.jar;lib2.jar
```

### IModuleLoader 插件模板

```properties
# ============================================
# eGPS Plugin Configuration
# ============================================

# 启动类（必需）
# 实现 egps2.modulei.IModuleLoader 接口
launchClass=your.package.YourModuleLoader

# 插件信息
pluginName=Your Module Name
version=1.0.0
author=Your Name
description=Brief description of your module

# 依赖（如果有）
# dependentJars=lib1.jar;lib2.jar
```

---

## ⚠️ 常见错误

### 错误 1：launchClass 为空或格式错误

**错误配置**：
```properties
launchClass=
```

**错误信息**：
```
Error to load plug-in
Invalid plugin configuration: launchClass not found in eGPS2.plugin.properties.
Please use new format: launchClass=your.package.ClassName
```

**解决方法**：
```properties
launchClass=com.example.MyPlugin
```

---

### 错误 2：使用旧格式

**错误配置**（已废弃）：
```properties
location.IModuleLoader.class:com.example.MyPlugin
```

**错误信息**：
```
Invalid plugin configuration: launchClass not found...
```

**解决方法**：
```properties
launchClass=com.example.MyPlugin
```

---

### 错误 3：类名不完整

**错误配置**：
```properties
launchClass=MyPlugin
```

**错误信息**：
```
ClassNotFoundException: MyPlugin
```

**解决方法**：
```properties
launchClass=com.example.MyPlugin
```

---

### 错误 4：dependentJars 分隔符错误

**错误配置**：
```properties
# 错误：使用逗号分隔
dependentJars=lib1.jar,lib2.jar,lib3.jar
```

**解决方法**：
```properties
# 正确：使用分号分隔
dependentJars=lib1.jar;lib2.jar;lib3.jar
```

---

### 错误 5：文件编码问题

**问题**：配置文件包含中文但使用了错误的编码（如 GBK）。

**解决方法**：
- 确保配置文件使用 UTF-8 编码保存
- 在 IDE 中设置文件编码为 UTF-8
- 打包时确保使用 `-encoding UTF-8` 参数

---

## 📊 配置项优先级

| 配置项 | 优先级 | 说明 |
|--------|--------|------|
| `launchClass` | **必需** | 没有此项插件无法加载 |
| `pluginName` | 补充元数据 | 当前加载器不依赖，可用于人工阅读 |
| `version` | 补充元数据 | 当前加载器不依赖，可用于版本记录 |
| `author` | 补充元数据 | 当前加载器不依赖，仅用于信息记录 |
| `description` | 补充元数据 | 当前加载器不依赖，仅用于信息记录 |
| `dependentJars` | 按需 | 有外部依赖时必需 |

---

## 🔍 验证配置文件

### 手动验证清单

```bash
# 1. 检查文件是否存在于 JAR 根目录
jar tf your-plugin.jar | grep "eGPS2.plugin.properties"

# 2. 查看配置文件内容
unzip -p your-plugin.jar eGPS2.plugin.properties

# 3. 检查类是否存在
jar tf your-plugin.jar | grep "YourPluginClass.class"
```

### 配置文件检查要点

- ✅ 文件名正确：`eGPS2.plugin.properties`（区分大小写）
- ✅ 文件位置正确：JAR 包根目录
- ✅ 文件编码正确：UTF-8
- ✅ 包含 `launchClass` 配置
- ✅ `launchClass` 值不为空
- ✅ 类名完整（包含包路径）
- ✅ 格式正确：`key=value`（使用等号，不是冒号）
- ✅ `dependentJars` 使用分号分隔（如果有多个）

---

## 🎯 最佳实践

### 1. 优先保证生效字段正确

首先确保 `launchClass` 正确，按需填写 `dependentJars`。这是当前加载器真正依赖的部分。

### 2. 补充元数据可帮助维护

即使某些字段是可选的，也建议填写完整，这有助于：
- 插件的可维护性
- 问题追踪和调试
- 用户了解插件功能

### 3. 版本号规范

```properties
# 推荐：语义化版本
version=1.0.0      # 初始版本
version=1.1.0      # 新增功能
version=1.1.1      # 修复 bug
version=2.0.0      # 不兼容的重大更新
```

### 4. 依赖管理

```properties
# 好的做法：明确列出所有外部依赖
dependentJars=lib1-1.0.jar;lib2-2.3.jar

# 不好的做法：使用通配符或版本号不明确
# dependentJars=lib1.jar;lib2.jar  # 没有版本号，不利于维护
```

### 5. 注释使用

```properties
# 可以使用 # 开头的注释行
# 这些注释会被忽略

# 插件启动类
launchClass=com.example.MyPlugin

# 插件版本信息
version=1.0.0

# 空行会被忽略

author=John Doe
```

### 6. 文件结构建议

```properties
# ============================================
# Plugin Basic Information
# ============================================
launchClass=com.example.MyPlugin
pluginName=My Plugin
version=1.0.0

# ============================================
# Author Information
# ============================================
author=John Doe <john@example.com>

# ============================================
# Description
# ============================================
description=A comprehensive tool for data analysis

# ============================================
# Dependencies
# ============================================
dependentJars=commons-lang3-3.12.0.jar;guava-31.1.jar
```

---

## 📚 相关文档

- [快速开始教程](01_QUICK_START_zh.md)
- [插件开发教程](02_PLUGIN_DEVELOPMENT_zh.md)
- [内置模块开发教程](03_BUILTIN_DEVELOPMENT_zh.md)
- [架构原理说明](04_ARCHITECTURE_zh.md)
- [教程总览](README_zh.md)

---

## 🆕 版本历史

### v2.1+ (2025-12-05)

- ✅ 采用新格式：`key=value`
- ✅ 当前真实生效字段：`launchClass`（必需）
- ✅ 当前可选生效字段：`dependentJars`
- ℹ️ 常见补充元数据：`pluginName`, `version`, `author`, `description`
- ❌ 废弃旧格式：`location.IModuleLoader.class:xxx`

### v2.0 及更早版本

- 使用旧格式：`location.IModuleLoader.class:xxx`
- 该格式已在 v2.1 中废弃，不再支持

---

**版本**: eGPS 2.1+
**最后更新**: 2025-12-05
**作者**: eGPS Dev Team
