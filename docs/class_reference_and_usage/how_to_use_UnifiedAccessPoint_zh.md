# 如何使用 UnifiedAccessPoint

`UnifiedAccessPoint` 是 `egps-main` 当前提供的壳层级统一入口，用来访问一些共享运行时对象。

**实现依据：** `src/egps2/UnifiedAccessPoint.java`

## 它主要负责什么

当模块、面板或对话框需要下面这些壳层公共能力时，就应该走 `UnifiedAccessPoint`：

- 访问单例主窗口
- 访问 `LaunchProperty` 以及当前全局字体
- 访问国际化资源字符串
- 访问打包到程序里的图片资源
- 注册“主窗体显示后再执行”的动作
- 将一个 `IModuleLoader` 加载到当前壳窗口中

## 常见用法

### 读取全局字体和运行期偏好

```java
LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
Font defaultFont = launchProperty.getDefaultFont();
Font titleFont = launchProperty.getDefaultTitleFont();
```

当面板、对话框或模块需要跟随壳程序当前字体配置时，用这条路径。

### 读取国际化字符串

```java
String title = UnifiedAccessPoint.getResourceString("dialog.info");
String message = UnifiedAccessPoint.getResourceString("dialog.msg.finish");
```

如果资源文件里已经有对应文本，就不要再硬编码界面文案。

### 读取内置图片资源

```java
URL iconUrl = UnifiedAccessPoint.getImageResource("module/24gf-lock2.png");
```

这适用于打包在程序 `/images/` 目录下的资源。

### 获取主窗口

```java
MyFrame frame = UnifiedAccessPoint.getInstanceFrame();
```

只有在代码确实需要壳主窗口时再这样做，例如给对话框提供 owner，或者接入主标签页 UI。

### 注册主窗体可见后的动作

```java
UnifiedAccessPoint.registerActionAfterMainFrame(() -> {
    SwingUtilities.invokeLater(() -> {
        UnifiedAccessPoint.loadTheModuleFromIModuleLoader(new IndependentModuleLoader());
    });
});
```

当启动阶段的逻辑依赖 GUI 已经创建完成时，用这个机制。

### 把模块加载到壳里

```java
IModuleLoader loader = new IndependentModuleLoader();
ModuleFace moduleFace = UnifiedAccessPoint.loadTheModuleFromIModuleLoader(loader);
```

这是当前从 `IModuleLoader` 打开模块标签页的标准路径。

## 实际使用规则

- 读取字体和偏好时，可以直接用 `getLaunchProperty()`。
- 如果不确定 GUI 是否已经起来，先判断 `isGULaunched()`。
- 通过 `registerActionAfterMainFrame(...)` 注册的动作如果要操作 Swing 组件，仍然要切回 EDT，例如 `SwingUtilities.invokeLater(...)`。
- 加载模块时优先用 `loadTheModuleFromIModuleLoader(...)`，不要自己重复实现标签页接入逻辑。

## 不建议这样用

- 不要把 `UnifiedAccessPoint` 当成杂项业务状态的全局仓库。
- 不要因为拿到了主窗口就顺手在 GUI 回调里跑耗时任务。
- 不要在本该兼容 CLI 的代码里，为了图省事强行创建主窗口，除非这里确实需要 GUI。
