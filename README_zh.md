# egps-shell
面向 eGPS 2.1 的图形界面主框架（壳），基于 eGPS-base 构建。

## 简介

egps-shell 是 eGPS 软件的图形界面宿主。虽然该主框架是闭源的，但所有用户都可免费获取并使用。

## 下载

我们在 Releases 发布了 Windows、macOS、Linux 的预构建版本，下载后即可直接使用。

## 插件示例

`src/test.plugin` 目录提供了一个完整示例。您可以将其打包为 JAR，并放入 eGPS 配置的插件路径，egps-shell 会识别并加载。

### 文件概览

- `IndependentModuleLoader.java` — 用于加载外部模块/插件的入口。
- `MainFace.java` — 壳使用的基础界面实现。
- `WorkBanch4XXX.java` — XXX 模块的工作台示例实现。
- `XXXMainFace.java` — 与工作台集成的示例界面类。

## 使用步骤

1. 使用任意 Java 构建工具，从 `src/test.plugin` 打包生成插件 JAR。
2. 将 JAR 复制到 eGPS 配置指定的插件目录。
3. 启动 egps-shell；插件会在启动时被检测并加载。

## 说明

详细配置请参考 eGPS-base 文档。