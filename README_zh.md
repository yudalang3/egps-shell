# egps-main

[English](README.md) | [中文](README_zh.md)

![egps-shell 截图](https://github.com/yudalang3/egps-shell/blob/main/snapshot/ScreenShot_2025-12-13_171628_725.png?raw=true)

`egps-main` 是 `egps-shell` 的闭源代码仓库。`egps-shell` 是 eGPS 的 GUI 壳底座，用来承载桌面模块；面向外部的说明文档主要放在 `docs/` 和 `manuals/` 中。

如果你需要一个包含 `egps-base`、`egps-shell` 和 `egps-pathway.evol.browser` 的打包版本，请访问：https://github.com/yudalang3/egps-pathway.evol.browser

## 概述

- `egps-main` 是当前维护中的源代码工程。
- `egps-shell` 是对外说明中的 GUI 壳底座和基础运行时。
- `egps2` 是当前代码库使用的主要 Java 包命名空间。
- 这个应用基于 Swing，支持模块化加载、插件接入，以及基于 VOICE 的工作流。

## 文档结构

- `README.md` / `README_zh.md`：`egps-main` 的仓库入口说明
- `docs/`：`egps-shell` 的参考说明文档
- `manuals/`：`egps-shell` 的教程与实践手册
- `manuals/module_plugin_course/`：模块与插件开发的专题材料

## 运行时配置

`egps-shell` 运行时主要使用下面这些目录和约定：

- 用户配置目录：`~/.egps2/config`
- 模块配置文件：`~/.egps2/config/egps2.loading.module.config.txt`
- 插件目录：`~/.egps2/config/plugin/`
- 推荐运行参数文件：`@eGPS.args` （eGPS.args是一个文本文件，@eGPS.args是java运行时的命令行表示读取该文件并设置为命令行参数）

`@eGPS.args` 中包含当前 Java 运行环境所需的 `--add-exports` 和 `--add-opens` 选项，因此日常启动时建议一并带上。

## 从源码构建

当前仓库通常在 IntelliJ IDEA + JDK 25 环境下开发。依赖主要通过 `dependency-egps/*` 管理；
本项目采用了基础的JAVA工程，并没有使用Maven 和 Gradle 并不是这里的构建工作流，方便用户直接使用。

在 macOS/Linux 上，最小化的手动编译命令可以写成：

```sh
javac -d ./out/production/egps-main.gui -cp "dependency-egps/*" $(find src -name "*.java")
```

编译完成后，类文件应位于 `out/production/egps-main.gui`。仓库里的 `build_jar_and_move.sh` 可以把壳层 JAR 打包出来并复制到本地部署目录，不过它主要面向维护者自己的本地环境。

## 从源码运行

运行时需要同时带上编译产物和依赖 JAR。

Windows：

```sh
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher com.example.YourModuleLoader
```

macOS/Linux：

```sh
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher com.example.YourModuleLoader
```

第三条命令用于直接打开某个模块，参数需要传入模块加载器类的完整类名。

## VOICE CLI

如果是基于 VOICE 的模块，并且暴露了 `SubTabModuleRunner`，可以使用 `egps2.builtin.modules.CLI`。第一个参数是模块类名，第二个参数是和 VOICE GUI 相同格式的配置文件。

macOS/Linux 示例：

```sh
java -cp "out/production/egps-main.gui:dependency-egps/*" @eGPS.args egps2.builtin.modules.CLI your.package.YourRunner path/to/config.txt
```

如果是在 Windows 下运行，只需要把 classpath 里的 `:` 换成 `;`。

## 说明

- `docs/` 和 `manuals/` 共同构成 `egps-shell` 的对外说明文档集。
- 这份 README 只聚焦 `egps-main` 仓库本身；更深入的产品说明和框架说明应放在上述两个目录中。

## AI 辅助开发

我们支持并鼓励用户基于eGPS 2.1平台开发属于自己的Tools。JAVA的Swing开发框架兴起于上个世纪90年代，虽然不再开发新的功能，但是功能非常稳定，是开发的一种选择。

### Case 1：新建一个基于 VOICE 的模块

```text
我正在 `egps-main` 中开发一个新的 eGPS 模块，希望使用 `egps-shell` 的 VOICE 框架。
请先阅读：
- `docs/voiceFramework/VOICE_MODULE_ARCHITECTURE.md`
- `docs/voiceFramework/VOICE-GUI.md`
- `manuals/module_plugin_course/`

模块名称是 abcdefg。
模块说明是 abcdefg。
版本号是 1.0.0。
作者是 xyz。
主要功能是 abcdefg。

请基于合适的 VOICE 风格完成实现，并把相关入口一起接好。
```

### Case 2：把已有模块重构为 VOICE 风格

```text
我正在 `egps-main` 中重构一个现有的 eGPS 模块。
请使用 `egps-shell` 的 VOICE 框架，并先阅读：
- `docs/voiceFramework/VOICE_MODULE_ARCHITECTURE.md`
- `docs/voiceFramework/VOICE-GUI.md`
- `manuals/module_plugin_course/`

模块名称是 xxxxxx。
作者是 xxx。

请把它改造成 VOICE 框架中的 dockable、floating 或 handytools 风格。
假设我们采用 floating 风格：
- 所有入口参数都是 `String`
- 所有返回值都是 `String`
- 路径参数都使用 `path.` 前缀，例如 `path.input.file`

另外也请把 CLI 一并接好，让它可以通过命令行运行。
```
