# egps-shell
面向 eGPS 2.1 的图形界面主框架（壳），基于 eGPS-base 构建。

# 手册

请查看 docs 目录以获取 eGPS-shell 文档的参考。

## 概述

egps-shell 是 eGPS 软件的主要图形界面宿主。虽然该 GUI 主框架是闭源的，但所有用户都可以免费使用。

## 下载

Windows、macOS 和 Linux 的预构建版本已在 Releases 发布。下载相应的包后即可直接运行。

## 使用

1. 使用您偏好的 Java 构建工具，从 `src/test.plugin` 构建插件 JAR。
2. 将该 JAR 复制到 eGPS 配置指定的插件目录。
3. 启动 egps-shell；插件会在启动时被检测并加载。

面向 Windows 用户：

```shell
java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher4Dev
```

面向 macOS/Linux 用户：

```shell
java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
```

## 说明

详细配置请参考 eGPS-base 文档。