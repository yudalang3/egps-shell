# egps-shell
面向 eGPS 2.1 的图形界面主框架（壳），基于 eGPS-base 构建。

# 手册

请查看 *module_dev_references/docs* 目录以获取 eGPS-shell 文档的参考。

## 概述

egps-shell 是 eGPS 软件的主要图形界面宿主，也就是基本的脚手架。虽然该 GUI 主框架是闭源的，但所有用户都可以免费使用。

## 下载

Windows、macOS 和 Linux 的预构建版本已在 Releases 发布。下载相应的包后即可直接运行。

为了本项目的快速迭代，现在暂时不加入JRE的运行环境，请自行添加JRE运行环境。

用户可以下载各种JAVA运行环境，如：Open、Oracle JDK、AdoptOpenJDK, Zulu， Microsoft JDK 等。

下面一些网址可以让你快速得到JAVA运行环境

[国内友人的集成下载网址](https://www.injdk.cn/)
[AdoptOpenJDK](https://adoptopenjdk.net/releases.html)
[Microsoft JDK](https://learn.microsoft.com/zh-cn/java/openjdk/install)



## 使用

1. 使用您偏好的 Java 构建工具，从 `src/test.plugin` 构建插件 JAR。
2. 将该 JAR 复制到 eGPS 配置指定的插件目录。
3. 启动 egps-shell；插件会在启动时被检测并加载。

面向 Windows 用户：

```shell
#java -cp "out/production/egps-main.gui;dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher4Dev
java -cp "dependency-egps/*" -Xmx12g '@eGPS.args' egps2.Launcher
```

面向 macOS/Linux 用户：

```shell
#java -cp "out/production/egps-main.gui:dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher4Dev
java -cp "dependency-egps/*" -Xmx12g @eGPS.args egps2.Launcher
```

## 说明

详细配置请参考 eGPS-base 文档，位于docs目录。


# AI-assistant prompt s


## Case study 1

重头利用 官方的VOICE框架 编写一个模块

```shell
我在编写一个eGPS模块，我需要用到主框架的VOICE功能，官方给出的示例代码在./module_dev_references/voice，文档说明在./module_dev_references/docs/下面
重点查看 VOICE_MODULE_ARCHITECTURE.md文件 和 module_plugin_course目录。
模块的名称是 abcdefg，模块的说明是 abcdefg， 模块的版本是 1.0.0，模块的作者是 xyz
主要功能是 abcdefg
```


## Case study 2

改装已有的模块

```shell
我在重构一个eGPS模块，名称是xxxxxx，模块的作者是xxx，我需要用到主框架的VOICE功能，官方给出的示例代码在./module_dev_references/voice，文档说明在./module_dev_references/docs/下面
重点查看 VOICE_MODULE_ARCHITECTURE.md文件 和 module_plugin_course目录。

请帮我改装成VOICE框架中的 dockable/floating/handytools形式
其中（假设你是floating的形式）所有的入口参数都是String，返回值也是String，path参数都需要以path.前缀开头，例如path.input.file

另外帮我顺便把CLI写好，我也要能够命令行运行
```