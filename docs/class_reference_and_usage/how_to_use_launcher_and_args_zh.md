# 如何启动 egps-shell 以及使用 `@eGPS.args`

本文档说明 `egps-shell` 当前的启动入口与运行参数文件用法。

**实现依据：** `src/egps2/Launcher.java`、`src/egps2/Launcher4Dev.java`、`eGPS.args`

## 当前启动入口

### 正常启动入口

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
```

这条命令是当前常规的壳程序启动路径。

### 开发模式启动入口

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
```

`Launcher4Dev` 当前只做一件事：先把 `Launcher.isDev = true`，然后委托给 `Launcher.main(args)`。

### 启动时直接打开某个模块

```bash
java -cp "./out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher egps2.builtin.modules.voice.IndependentModuleLoader
```

如果你在 `egps2.Launcher` 后面再传一个或多个模块加载器类全名，壳程序会先注册这些模块，再在主窗体可用后自动打开它们。

## `@eGPS.args` 是做什么的

`@eGPS.args` 提供当前这套 Swing/JIDE 程序在现代 JDK 下需要的 `--add-exports`、`--add-opens` 以及相关 JVM 参数。

除非你是在专门排查 JVM 参数问题，否则日常启动都应该带上它。

## 当前启动过程做了什么

`Launcher.main(...)` 目前会依次执行这些事情：

1. 设置 UTF-8 相关系统属性
2. 检查当前是否存在可用显示环境
3. 强制使用 `Locale.ENGLISH`
4. 准备 Swing/JIDE 的 Look & Feel
5. 初始化 `~/.egps2/config` 下的首次启动配置
6. 注册命令行传入的预定义模块加载器
7. 启动 GUI 并显示单例 `MyFrame`

## 配置目录位置

当前用户配置根目录是 `~/.egps2/config`。

代码中的来源是：

- `EGPSProperties.PROPERTIES_DIR_NAME = "config"`
- `EGPSProperties.PROPERTIES_DIR = <user.home>/.egps2/config`

## 实际使用规则

- 常规运行优先使用 `Launcher`。
- 只有在你确实要走开发模式行为时，才用 `Launcher4Dev`。
- 日常命令里保留 `@eGPS.args`。
- 如果希望启动后自动打开某个模块，就在启动类后面追加该模块加载器类名。

## 当前边界

- 这是 GUI 启动入口，要求当前环境有可用显示能力。
- 首次启动可能会自动生成配置内容。
- 启动参数里追加的模块类应当是 `IModuleLoader` 的实现类，不是任意 Java 类。
