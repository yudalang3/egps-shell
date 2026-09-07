# egps-shell

[English](README.md) | [中文](README_zh.md)

![egps-shell 截图](https://github.com/yudalang3/egps-shell/blob/main/snapshot/ScreenShot_2025-12-13_171628_725.png?raw=true)

`egps-shell` 是一个完全开源的软件产品，为 eGPS 提供承载桌面模块的 GUI 壳框架。本仓库包含其主框架源代码，采用 Apache License 2.0 开源许可证，详见 [LICENSE](LICENSE)。参考文档和开发教程位于 `docs/` 和 `manuals/`。

如果你需要一个包含 `egps-base`、`egps-shell` 和 `egps-pathway.evol.browser` 的打包版本，请访问：https://github.com/yudalang3/egps-pathway.evol.browser

## 概述

- `egps-shell` 是当前维护中的开源 GUI 主框架与基础运行时。
- `egps-main.gui` 是现有 IDEA 模块名和构建输出目录中保留的历史名称。
- `egps2` 是当前代码库使用的主要 Java 包命名空间。
- 这个应用基于 Swing，支持模块化加载、插件接入，以及基于 VOICE 的工作流。

## 文档结构

- `README.md` / `README_zh.md`：`egps-shell` 的仓库入口说明
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

使用 JDK 25，从仓库根目录执行命令。依赖通过 `dependency-egps/*` 管理，本项目不使用 Maven 或 Gradle。该依赖目录不随 Git 提交，需要事先准备匹配的依赖 JAR（包括 `egps-base`）。

当前 IDEA 配置继承项目输出设置，模块名为 `egps-main.gui`，通常输出到 `out/production/egps-main.gui`。以下手动命令使用同一路径，排除 `src/test/`。请使用空的输出目录，避免打包旧类或此前编译的测试类。

`javac` 不会复制图片、HTML、字体等资源；编译成功后必须按源码相对路径复制非 Java 文件。

Windows PowerShell 7：

```powershell
$buildDir = "out/production/egps-main.gui"
New-Item -ItemType Directory -Force -Path $buildDir | Out-Null
$sourceRoot = (Resolve-Path src).Path
Get-ChildItem src -Recurse -Filter *.java |
    Where-Object { $_.FullName -notlike '*\src\test\*' } |
    ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' } |
    Set-Content -Encoding utf8 out/sources.txt
javac -encoding UTF-8 -d $buildDir -cp "dependency-egps/*" '@out/sources.txt'
if ($LASTEXITCODE -ne 0) { throw "Compilation failed" }
Get-ChildItem src -Recurse -File |
    Where-Object { $_.Extension -ne '.java' -and $_.FullName -notlike '*\src\test\*' } |
    ForEach-Object {
        $relativePath = $_.FullName.Substring($sourceRoot.Length + 1)
        $destination = Join-Path $buildDir $relativePath
        New-Item -ItemType Directory -Force -Path (Split-Path $destination) | Out-Null
        Copy-Item -LiteralPath $_.FullName -Destination $destination
    }
```

macOS/Linux（Bash，使用本机 Java）：

```bash
build_dir="out/production/egps-main.gui"
mkdir -p "$build_dir"
find src -path src/test -prune -o -name '*.java' -print | sed 's/.*/"&"/' > out/sources.txt
javac -encoding UTF-8 -d "$build_dir" -cp "dependency-egps/*" @out/sources.txt &&
find src -path src/test -prune -o -type f ! -name '*.java' -exec sh -c '
    build_dir=$1
    shift
    for source_file do
        relative_path=${source_file#src/}
        mkdir -p "$build_dir/$(dirname "$relative_path")"
        cp "$source_file" "$build_dir/$relative_path"
    done
' sh "$build_dir" {} +
```

## 打包与本地部署

编译并复制资源后，可直接打包（两个平台通用）：

```text
jar --create --file out/egps-shell-0.0.1.jar -C out/production/egps-main.gui .
```

该命令只打包当前输出，不编译、不包含依赖 JAR，也不复制到部署目录。部署时需另行准备依赖。

维护者本地可能有 `build_jar_and_move.sh`；该脚本被 Git 忽略，不是克隆仓库后的必备文件。它需要 Bash，只打包已有类文件，再复制到预设本地目录，其中 `/mnt/c/...` 路径面向 WSL。运行前需自行核对目标，不应把它当作通用构建命令。

## 从源码运行

运行时需要同时带上编译产物和依赖 JAR。

Windows PowerShell 7：

```powershell
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher4Dev
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher com.example.YourModuleLoader
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

Windows PowerShell 下将 classpath 中的 `:` 换成 `;`，并将参数文件写成带引号的 `'@eGPS.args'`。

## 测试与收尾检查

测试位于 `src/test/`，使用独立 `main()` 入口；没有配置 Maven/JUnit 测试流程。测试需单独编译，输出到 `out/test-classes`，不要混入发布 JAR。

下面的发现诊断示例需先完成主源码编译和资源复制。

Windows PowerShell 7：

```powershell
javac -encoding UTF-8 -d out/test-classes -cp "out/production/egps-main.gui;dependency-egps/*" src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java
java -cp "out/test-classes;out/production/egps-main.gui;dependency-egps/*" '@eGPS.args' test.egps2.frame.features.ModuleDiscoveryServiceTest
```

macOS/Linux：

```bash
javac -encoding UTF-8 -d out/test-classes -cp "out/production/egps-main.gui:dependency-egps/*" src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java
java -cp "out/test-classes:out/production/egps-main.gui:dependency-egps/*" @eGPS.args test.egps2.frame.features.ModuleDiscoveryServiceTest
```

- `test.egps2.frame.features.ModuleDiscoveryServiceTest`：检查扫描与过滤，包名确实含 `test.`。
- `egps2.frame.features.ModuleDiscoveryTest`：检查扫描、配置读取与合并；编译同目录对应源码后，显式传入准备好的临时配置路径，不依赖它的默认示例路径。配置行为见[模块发现说明](docs/itoolsManager/itoolmanager_module_discovery_statement_zh.md)。
- 这些入口主要打印诊断，退出码为零不等于断言全部通过。尤其 `ModuleDiscoveryServiceTest` 仍包含“核心模块应被排除”的旧预期，与[当前排除规则](docs/module&pluginSystem/module_discovery_exclusion_rules_zh.md)不同，应逐项核对输出。扫描也会读取用户插件目录；需要隔离时用 `-Duser.home=临时目录`，并准备测试夹具。

收尾时同步中英文 README 和改动涉及的配对文档，检查本地链接、入口类名与命令，并报告实际执行的验证及其局限；不要求未配置的 lint 步骤。

## 说明

- `docs/` 和 `manuals/` 共同构成 `egps-shell` 的对外说明文档集。
- 这份 README 只聚焦 `egps-shell` 仓库本身；更深入的产品说明和框架说明应放在上述两个目录中。

## AI 辅助开发

我们支持并鼓励用户基于eGPS 2.1平台开发属于自己的Tools。JAVA的Swing开发框架兴起于上个世纪90年代，虽然不再开发新的功能，但是功能非常稳定，是开发的一种选择。

### Case 1：新建一个基于 VOICE 的模块

```text
我正在 `egps-shell` 中开发一个新的 eGPS 模块，希望使用 `egps-shell` 的 VOICE 框架。
请先阅读：
- `manuals/01_VOICE_architecture_zh.md`
- `manuals/02_VOICE_GUI_design_zh.md`
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
我正在 `egps-shell` 中重构一个现有的 eGPS 模块。
请使用 `egps-shell` 的 VOICE 框架，并先阅读：
- `manuals/01_VOICE_architecture_zh.md`
- `manuals/02_VOICE_GUI_design_zh.md`
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
