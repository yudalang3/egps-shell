# eGPS 模块开发快速开始指南

## 📌 概述

本教程将引导你在 **5 分钟内**创建并运行你的第一个 eGPS 模块。

## 🎯 学习目标

完成本教程后，你将：
- ✅ 了解 eGPS 的两种模块部署方式
- ✅ 使用自动化脚本创建示例插件
- ✅ 成功安装并运行插件
- ✅ 理解模块的基本结构

## 📚 前置知识

- 基本的 Java 编程知识
- 了解如何使用命令行
- 已经编译好的 eGPS 项目（`out/production/egps-main.gui` 目录存在）

## 🚀 方式 1: 使用自动化脚本（推荐）

### 步骤 1: 运行自动生成脚本

```bash
# 进入项目根目录
cd /path/to/egps-shell

# 运行脚本
bash docs/module_plugin_course/create-all-test-plugins.sh
```

### 步骤 2: 查看生成的文件

脚本会创建 `plug_dist` 目录，包含：

```
plug_dist/
├── fastbase-plugin.jar         # 简单型插件（继承 FastBaseTemplate）
├── direct-plugin.jar           # 复杂型插件（实现 IModuleLoader）
├── calculator-plugin.jar       # 实用计算器（含中英文文档）
├── clipboardpaste-plugin.jar   # 路径转换工具（Windows ↔ WSL）
└── README.txt                  # 安装说明
```

### 步骤 3: 选择部署方式

#### 方式 A: 作为外部插件部署（Plugin 模式）

```bash
# 复制到插件目录
cp plug_dist/*.jar ~/.egps2/config/plugin/
```

**特点**：
- 🔌 显示在 "Plugins" 菜单
- 🏷️ 在 Module Gallery 中显示蓝色 **[Plug]** 标识
- 📦 作为外部扩展存在
- 🔄 可以方便地添加/删除
- 👥 适合分发给其他用户

#### 方式 B: 作为内置模块部署（Built-in 模式）

```bash
# 复制到依赖目录
cp plug_dist/*.jar dependency-egps/
```

**特点**：
- 🏠 作为内置模块存在
- 📋 显示在 "iTools" 或其他分类菜单
- ⚡ 加载速度稍快
- 🔧 适合核心功能模块

> **重要**: 本质上这两种方式是一样的，区别只在于 JAR 文件的存放位置！

### 步骤 4: 启动 eGPS

For MacOS/Linux:

```bash
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
```

For Windows:

```bash
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher
```

### 步骤 5: 查看你的插件

**方法 1: 通过 Module Gallery（推荐）**
1. 按 `Ctrl+2` 或选择菜单 `File → Module gallery`
2. 在列表中找到你的插件
3. 双击打开

**方法 2: 通过菜单栏**
- 如果安装到 `~/.egps2/config/plugin/`：菜单 → `Plugins`
- 如果安装到 `dependency-egps/`：菜单 → `iTools` 或相关分类

## 🎓 理解生成的插件

### 插件 1: FastBase Plugin（简单型）

```java
public class FastBasePlugin extends FastBaseTemplate {

    public FastBasePlugin() {
        super();
        // 在这里构建 UI
        setLayout(new BorderLayout());
        add(new JLabel("Hello!"), BorderLayout.CENTER);
    }

    @Override
    public String getTabName() {
        return "FastBase Plugin";  // 标签页名称
    }

    @Override
    public String getShortDescription() {
        return "简单快速的插件";  // 简短描述
    }

    @Override
    public int[] getCategory() {
        return ModuleClassification.getOneModuleClassification(
            ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
            ModuleClassification.BYAPPLICATION_COMMON_MODULE_INDEX,
            ModuleClassification.BYCOMPLEXITY_LEVEL_1_INDEX,
            ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER
        );  // 分类
    }
}
```

**优点**：
- 代码量少（约 70 行）
- 快速开发
- 适合简单工具

### 插件 2: Direct Plugin（复杂型）

```java
// 加载器类
public class DirectPlugin implements IModuleLoader {
    private DirectPluginPanel panel;

    public DirectPlugin() {
        panel = new DirectPluginPanel(this);
    }

    @Override
    public ModuleFace getFace() {
        return panel;
    }

    // 实现其他接口方法...
}

// 面板类
class DirectPluginPanel extends ModuleFace {
    public DirectPluginPanel(IModuleLoader loader) {
        super(loader);
        // 构建 UI
    }

    // 实现 ModuleFace 方法...
}
```

**优点**：
- 架构清晰（加载器和面板分离）
- 完全控制
- 适合复杂功能

## 🔍 两种部署方式对比

| 特性 | Plugin 模式<br>（~/.egps2/config/plugin/） | Built-in 模式<br>（dependency-egps/） |
|------|------------------------------------------|-------------------------------------|
| **存放位置** | 用户配置目录 | 应用程序依赖目录 |
| **菜单显示** | "Plugins" 菜单 | "iTools" 或其他分类菜单 |
| **是否需要重新编译** | ❌ 否 | ❌ 否 |
| **是否能被 Gallery 发现** | ✅ 是 | ✅ 是 |
| **加载机制** | CustomURLClassLoader | 应用程序 ClassLoader |
| **适用场景** | 外部扩展、用户自定义功能 | 核心功能、默认工具 |
| **分发方式** | 直接分发 JAR 文件 | 包含在软件发布包中 |
| **开发/调试** | 方便（修改后重启即可） | 方便（修改后重启即可） |

**关键点**：
- 🎯 **本质相同**：两种方式的 JAR 结构完全一样，都实现 `IModuleLoader` 接口
- 📍 **唯一区别**：存放位置决定了在菜单中的显示位置
- 🔄 **可以互换**：同一个 JAR 文件可以在两个位置之间移动

## 🎨 自定义插件

基于生成的示例，你可以修改：

### 修改插件名称和描述

```java
@Override
public String getTabName() {
    return "我的工具";  // 修改这里
}

@Override
public String getShortDescription() {
    return "这是我创建的第一个工具";  // 修改这里
}
```

### 修改分类（Category）

```java
@Override
public int[] getCategory() {
    return ModuleClassification.getOneModuleClassification(
        ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,   // 功能类型
        ModuleClassification.BYAPPLICATION_COMMON_MODULE_INDEX,    // 应用领域
        ModuleClassification.BYCOMPLEXITY_LEVEL_1_INDEX,           // 复杂度
        ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER    // 依赖性
    );
}
```

> 分类请统一使用 `ModuleClassification.getOneModuleClassification(...)` 组合四个维度的常量。

### 添加功能按钮

```java
public MyPlugin() {
    super();
    setLayout(new BorderLayout());

    JButton myButton = new JButton("点击我");
    myButton.addActionListener(e -> {
        JOptionPane.showMessageDialog(this, "你点击了按钮！");
    });

    add(myButton, BorderLayout.CENTER);
}
```

## 📦 JAR 文件结构

无论部署到哪里，JAR 文件的结构都是一样的：

```
myplugin.jar
├── eGPS2.plugin.properties     # 插件配置文件（必需）
└── com/mycompany/              # 你的包结构
    └── MyPlugin.class          # 编译后的类文件
```

**eGPS2.plugin.properties 内容：**

```properties
launchClass=com.mycompany.MyPlugin
pluginName=My Plugin
version=1.0.0
author=Your Name
description=My first plugin
```

## 🐛 常见问题

### 问题 1: 插件没有出现在 Module Gallery

**解决方法**：
1. 检查 JAR 文件是否在正确的位置
2. 确认 `eGPS2.plugin.properties` 文件存在且配置正确
3. 重启 eGPS
4. 检查控制台是否有错误信息

### 问题 2: 插件加载失败

**解决方法**：
1. 确认类名和包名与 `eGPS2.plugin.properties` 中的 `launchClass` 一致
2. 确认类实现了 `IModuleLoader` 接口或继承了 `FastBaseTemplate`
3. 确认类有公共的无参构造函数
4. 检查依赖的类库是否包含在 JAR 中

### 问题 3: 想把 Plugin 改成 Built-in

**解决方法**：
```bash
# 直接移动 JAR 文件
mv ~/.egps2/config/plugin/myplugin.jar dependency-egps/
# 重启 eGPS
```

### 问题 4: 想把 Built-in 改成 Plugin

**解决方法**：
```bash
# 直接移动 JAR 文件
mv dependency-egps/myplugin.jar ~/.egps2/config/plugin/
# 重启 eGPS
```

## ✅ 下一步

恭喜！你已经成功创建并运行了你的第一个 eGPS 模块！

**继续学习**：
- 📄 [05_eGPS2.plugin.properties_zh.md](05_eGPS2.plugin.properties_zh.md) - **配置文件说明书（必读）**
- 📖 [02_PLUGIN_DEVELOPMENT_zh.md](02_PLUGIN_DEVELOPMENT_zh.md) - 详细的插件开发教程
- 📖 [03_BUILTIN_DEVELOPMENT_zh.md](03_BUILTIN_DEVELOPMENT_zh.md) - 内置模块开发教程
- 📖 [04_ARCHITECTURE_zh.md](04_ARCHITECTURE_zh.md) - 架构原理和高级主题

**实践建议**：
1. 先修改生成的示例代码，观察效果
2. 尝试添加更多的 UI 组件和功能
3. 学习如何处理用户输入和数据导入/导出
4. 探索 eGPS 提供的工具类和辅助方法

## 📞 获取帮助

- 查看 `docs/understanding/` 目录下的架构文档
- 参考 `src/egps2/builtin/modules/` 下的内置模块源代码
- 阅读 `CLAUDE.md` 了解项目整体结构

---

**版本**: eGPS 2.1+
**最后更新**: 2025-12-05
**作者**: eGPS Dev Team
