# eGPS 模块系统架构原理

## 📌 概述

本文档深入讲解 eGPS 模块系统的架构设计、工作原理，以及 Plugin 模式和 Built-in 模式的技术实现细节。

## 🎯 核心理念

### 统一的模块接口

eGPS 的模块系统基于一个核心理念：**所有模块都实现相同的接口，无论它们如何部署。**

```
┌─────────────────────────────────────────────┐
│           IModuleLoader 接口                │
│  (定义模块的基本契约)                       │
└─────────────────┬───────────────────────────┘
                  │
      ┌───────────┴───────────┐
      │                       │
      ▼                       ▼
┌─────────────┐         ┌─────────────┐
│  Plugin JAR │         │ Built-in JAR│
│ ~/.egps2/   │         │ dependency- │
│ config/     │         │   egps/     │
│ plugin/     │         │             │
└─────────────┘         └─────────────┘
      │                       │
      └───────────┬───────────┘
                  │
                  ▼
       ┌──────────────────────┐
       │  ModuleDiscoveryService│
       │  (统一发现机制)       │
       └──────────┬─────────────┘
                  │
                  ▼
          ┌───────────────┐
          │ Module Gallery│
          │   (展示)      │
          └───────────────┘
```

---

## 🏗️ 架构组件

### 1. 核心接口：IModuleLoader

**位置**：`src/egps2/modulei/IModuleLoader.java`

所有模块（无论 Plugin 还是 Built-in）都必须实现这个接口：

```java
public interface IModuleLoader {
    /**
     * 获取模块在标签页中显示的名称
     */
    String getTabName();

    /**
     * 获取模块的简短描述
     */
    String getShortDescription();

    /**
     * 获取模块的 UI 面板
     */
    ModuleFace getFace();

/**
 * 获取模块的分类信息
 * [功能类型, 应用领域, 复杂度, 依赖性]
 */
int[] getCategory();

    /**
     * 获取模块图标（可选）
     */
    IconBean getIcon();

    /**
     * 获取英文文档（可选）
     */
    JPanel getEnglishDocument();

    /**
     * 获取中文文档（可选）
     */
JPanel getChineseDocument();
}
```

> 分类数组请通过 `ModuleClassification.getOneModuleClassification(...)` 使用四个维度的常量生成，避免手动 new 数组。

### 2. 基类：ModuleFace

**位置**：`src/egps2/frame/ModuleFace.java`

`ModuleFace` 是所有模块 UI 面板的抽象基类：

```java
public abstract class ModuleFace extends JPanel {
    private IModuleLoader loader;

    public ModuleFace(IModuleLoader loader) {
        this.loader = loader;
    }

    // 数据导入导出
    public abstract boolean canImport();
    public abstract void importData();
    public abstract boolean canExport();
    public abstract void exportData();

    // 模块特性
    public abstract String[] getFeatureNames();
    protected abstract void initializeGraphics();

    // 生命周期
    public void changeToThisTab() { /* 标签页切换时调用 */ }
    public void closeTab() { /* 标签页关闭时调用 */ }
    public boolean moduleExisted() { /* 是否有未保存数据 */ }
}
```

### 3. 快速模板：FastBaseTemplate

**位置**：`src/egps2/plugin/fastmodtem/FastBaseTemplate.java`

`FastBaseTemplate` 同时实现了 `IModuleLoader` 和继承了 `ModuleFace`：

```java
public abstract class FastBaseTemplate extends ModuleFace implements IModuleLoader {

    public FastBaseTemplate() {
        super(null);  // 自己就是 loader
    }

    @Override
    public ModuleFace getFace() {
        return this;  // 自己就是面板
    }

    // 提供默认实现
    @Override
    public boolean canImport() { return false; }

    @Override
    public void importData() {}

    @Override
    public boolean canExport() { return false; }

    @Override
    public void exportData() {}

    // ... 其他默认实现
}
```

**优势**：
- 减少样板代码
- 适合简单模块
- 加载器和面板合二为一

---

## 🔍 模块发现机制

### ModuleDiscoveryService

**位置**：`src/egps2/frame/features/ModuleDiscoveryService.java`

这是模块系统的核心组件，负责统一发现所有模块。

#### 工作流程

```
启动 eGPS
    │
    ▼
ModuleDiscoveryService.scanAllModuleClasses()
    │
    ├─► Scan Classpath (Reflections库)
    │   ├─ 扫描包：egps2, demo, module, operator, primary
    │   ├─ 查找：IModuleLoader 实现类
    │   └─ 过滤：应用排除规则
    │
    └─► Scan Plugin JARs (手动遍历)
        ├─ 遍历：~/.egps2/config/plugin/*.jar
        ├─ 遍历：dependency-egps/*.jar
        ├─ 使用：CustomURLClassLoader 加载
        ├─ 查找：IModuleLoader 实现类
        └─ 过滤：应用排除规则
    │
    ▼
返回统一的模块列表
    │
    ├─► 显示在 Module Gallery
    └─► 显示在菜单栏
```

#### 关键代码分析

```java
public class ModuleDiscoveryService {

    /**
     * 扫描所有模块
     */
    public static List<Class<?>> scanAllModuleClasses() {
        List<Class<?>> allModules = new ArrayList<>();

        // 1. 扫描 Classpath（内置模块）
        Set<Class<? extends IModuleLoader>> classpathModules =
            reflections.getSubTypesOf(IModuleLoader.class);

        for (Class<?> clazz : classpathModules) {
            if (shouldIncludeModule(clazz, false)) {
                allModules.add(clazz);
            }
        }

        // 2. 扫描 Plugin JARs（外部插件）
        URL[] pluginUrls = getPluginJarUrls();
        if (pluginUrls.length > 0) {
            CustomURLClassLoader pluginLoader =
                new CustomURLClassLoader(pluginUrls, ClassLoader.getSystemClassLoader());

            for (URL jarUrl : pluginUrls) {
                scanJarForModules(jarUrl, pluginLoader, allModules);
            }
        }

        return allModules;
    }

    /**
     * 判断是否应该包含此模块
     */
    private static boolean shouldIncludeModule(Class<?> clazz, boolean isFromJar) {
        // 排除抽象类和接口
        if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
            return false;
        }

        // 排除 SubTab 类
        if (DockableTabModuleFaceOfVoice.class.isAssignableFrom(clazz)) {
            return false;
        }

        // FastBaseTemplate 特殊处理
        if (FastBaseTemplate.class.isAssignableFrom(clazz)) {
            // FastBaseTemplate 本身：永远排除
            if (clazz.equals(FastBaseTemplate.class)) {
                return false;
            }

            // FastBaseTemplate 子类：
            // - 来自 JAR：包含（真实插件）
            // - 来自 Classpath：排除（模板示例）
            return isFromJar;
        }

        return true;
    }
}
```

---

## 🔄 两种部署模式的技术实现

### Plugin 模式（~/.egps2/config/plugin/）

#### 特点

1. **独立的 ClassLoader**

```java
// 为每个插件 JAR 创建独立的 ClassLoader
CustomURLClassLoader pluginLoader = new CustomURLClassLoader(
    new URL[]{jarUrl},
    getClass().getClassLoader()
);

// 加载插件类
Class<?> pluginClass = pluginLoader.loadClass("com.example.MyPlugin");
```

2. **类隔离**

- 每个插件有自己的 ClassLoader
- 避免类冲突
- 可以使用不同版本的依赖库

3. **动态加载**

```java
// 运行时发现和加载
File pluginDir = new File(System.getProperty("user.home"), ".egps2/config/plugin");
File[] jarFiles = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));

for (File jarFile : jarFiles) {
    // 动态加载每个 JAR
}
```

#### CustomURLClassLoader 实现

**位置**：`src/egps2/plugin/manager/CustomURLClassLoader.java`

```java
public class CustomURLClassLoader extends URLClassLoader {

    public CustomURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        // 先尝试从插件 JAR 加载
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            try {
                c = findClass(name);
            } catch (ClassNotFoundException e) {
                // 如果找不到，委托给父 ClassLoader
                c = super.loadClass(name, resolve);
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }
}
```

---

### Built-in 模式（dependency-egps/）

#### 特点

1. **共享 ClassLoader**

```java
// 使用应用的 ClassLoader
ClassLoader appLoader = getClass().getClassLoader();
Class<?> moduleClass = appLoader.loadClass("com.example.MyModule");
```

2. **类共享**

- 所有 Built-in 模块共享相同的 ClassLoader
- 共享依赖库
- 避免重复加载

3. **启动时加载**

```java
// 在 Launcher 启动时，dependency-egps 已经在 classpath 中
// java -cp "./out/production/egps-shell:dependency-egps/*" egps2.Launcher
```

#### 加载流程

```
1. JVM 启动
   └─ 加载 classpath: dependency-egps/*

2. Reflections 扫描
   └─ 发现所有 IModuleLoader 实现

3. ModuleDiscoveryService
   └─ 统一处理 Built-in 和 Plugin 模块
```

---

## 📊 两种模式对比

### 技术层面

| 特性 | Plugin 模式 | Built-in 模式 |
|------|------------|--------------|
| **ClassLoader** | CustomURLClassLoader | AppClassLoader |
| **类隔离** | ✅ 是 | ❌ 否 |
| **加载时机** | 运行时动态加载 | 启动时加载 |
| **内存占用** | 稍高（多个 ClassLoader） | 稍低（共享 ClassLoader） |
| **加载速度** | 稍慢（创建 ClassLoader） | 稍快（已在 classpath） |
| **依赖冲突** | 不容易冲突 | 可能冲突 |

### 发现机制

两种模式都被 `ModuleDiscoveryService` 统一处理：

```java
// Plugin 模式
private static void scanJarForModules(URL jarUrl, ClassLoader loader, List<Class<?>> result) {
    JarFile jarFile = new JarFile(new File(jarUrl.toURI()));
    Enumeration<JarEntry> entries = jarFile.entries();

    while (entries.hasMoreElements()) {
        JarEntry entry = entries.nextElement();
        String name = entry.getName();

        if (name.endsWith(".class")) {
            String className = name.replace("/", ".").replace(".class", "");
            try {
                Class<?> clazz = loader.loadClass(className);
                if (IModuleLoader.class.isAssignableFrom(clazz) &&
                    shouldIncludeModule(clazz, true)) {  // true = 来自 JAR
                    result.add(clazz);
                }
            } catch (Exception e) {
                // 忽略无法加载的类
            }
        }
    }
}

// Built-in 模式
Set<Class<? extends IModuleLoader>> classpathModules =
    reflections.getSubTypesOf(IModuleLoader.class);

for (Class<?> clazz : classpathModules) {
    if (shouldIncludeModule(clazz, false)) {  // false = 来自 Classpath
        result.add(clazz);
    }
}
```

---

## 🎨 关键设计决策

### 1. 为什么使用统一接口？

**优势**：
- 插件和内置模块使用完全相同的代码
- 可以自由转换部署方式
- 降低学习成本
- 简化系统架构

**实现**：
```java
// 无论是 Plugin 还是 Built-in，都实现相同的接口
public class MyModule implements IModuleLoader {
    // 相同的实现
}
```

### 2. 为什么需要 eGPS2.plugin.properties？

即使是 Built-in 模块也需要这个配置文件，因为：

1. **统一的加载机制**
```java
// PluginOperation.java
Properties props = new Properties();
props.load(jarFile.getInputStream(
    jarFile.getEntry("eGPS2.plugin.properties")
));

String launchClass = props.getProperty("launchClass");
Class<?> clazz = classLoader.loadClass(launchClass);
```

2. **元数据管理**
- 插件名称
- 版本信息
- 作者信息
- 描述

3. **向后兼容**
- 保持与旧版本的兼容性

### 3. 为什么 FastBaseTemplate 在 JAR 和 Classpath 中的行为不同？

**问题**：
- `FastBaseTemplate` 是一个模板基类
- 如果在 classpath 中也被发现，会出现在模块列表中

**解决方案**：
```java
// FastBaseTemplate 特殊处理
if (FastBaseTemplate.class.isAssignableFrom(clazz)) {
    if (clazz.equals(FastBaseTemplate.class)) {
        return false;  // 永远排除 FastBaseTemplate 本身
    }

    // FastBaseTemplate 的子类：
    // - 在 JAR 中 = 真实的插件模块 ✅
    // - 在 Classpath 中 = 示例/模板代码 ❌
    return isFromJar;
}
```

这样：
- Shell 中的 `FastBaseTemplate` 子类被排除（作为示例代码）
- JAR 中的 `FastBaseTemplate` 子类被包含（作为真实插件）

---

## 🚀 加载和实例化流程

### 完整流程

```
1. 应用启动
   └─ Launcher.main()
       └─ UnifiedAccessPoint.getInstanceFrame()
           └─ new MyFrame()
               └─ MainFrameProperties.getExistedLoaders()

2. 模块发现
   └─ EGPS2ServiceLoader.loadWithDiscovery()
       └─ ModuleDiscoveryService.scanAllModuleClasses()
           ├─ 扫描 Classpath (Built-in)
           └─ 扫描 Plugin JARs (Plugin)

3. 模块列表生成
   └─ IModuleElement[] 数组
       ├─ 包含所有发现的模块类
       └─ 标记状态 (AVAILABLE, NEWLY_DISCOVERED 等)

4. Module Gallery 显示
   └─ IntroMain (Module Gallery)
       └─ DemoButtonsOrganizer
           └─ 为每个模块创建按钮

5. 用户点击模块
   └─ UnifiedAccessPoint.loadTheModuleFromIModuleLoader()
       ├─ 实例化模块类: loader = clazz.newInstance()
       ├─ 获取面板: face = loader.getFace()
       └─ 添加到标签页: addTab(name, icon, face, desc)
```

### 实例化细节

```java
// UnifiedAccessPoint.java
public static void loadTheModuleFromIModuleLoader(IModuleLoader loader) {
    try {
        // 1. 获取模块信息
        String tabName = loader.getTabName();
        String shortDesc = loader.getShortDescription();
        IconBean iconBean = loader.getIcon();

        // 2. 创建图标
        Icon icon = null;
        if (iconBean != null) {
            icon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresizeWithTabIcon(
                iconBean.getInputStream(),
                iconBean.isSVG()
            );
        }

        // 3. 获取面板
        ModuleFace face = loader.getFace();

        // 4. 添加到主窗口
        MyFrame frame = getInstanceFrame();
        frame.addTab2mainTabbedPanel(tabName, icon, face, shortDesc);

    } catch (Exception e) {
        logger.error("Failed to load module", e);
    }
}
```

---

## 🔧 扩展点

### 1. 自定义模块发现

如果需要从其他位置加载模块：

```java
public class CustomModuleScanner {
    public static List<Class<?>> scanCustomLocation(File customDir) {
        List<Class<?>> modules = new ArrayList<>();

        // 遍历自定义目录
        File[] files = customDir.listFiles((dir, name) -> name.endsWith(".jar"));

        for (File file : files) {
            URL url = file.toURI().toURL();
            URLClassLoader loader = new URLClassLoader(new URL[]{url});

            // 扫描 JAR 中的模块
            // ...
        }

        return modules;
    }
}
```

### 2. 自定义模块过滤

```java
public class CustomModuleFilter {
    public static boolean shouldInclude(Class<?> clazz) {
        // 自定义过滤逻辑
        // 例如：只加载特定注解的类
        return clazz.isAnnotationPresent(MyModuleAnnotation.class);
    }
}
```

### 3. 模块生命周期钩子

```java
public interface IModuleLifecycle {
    void onLoad();      // 模块加载时
    void onUnload();    // 模块卸载时
    void onActivate();  // 模块激活时
    void onDeactivate(); // 模块停用时
}
```

---

## 📚 最佳实践

### 1. 模块设计

```java
// ✅ 好的设计：职责分离
public class MyModuleLoader implements IModuleLoader {
    private MyModulePanel panel;

    public MyModuleLoader() {
        this.panel = new MyModulePanel(this);
    }

    public ModuleFace getFace() {
        return panel;
    }
}

class MyModulePanel extends ModuleFace {
    // UI 和业务逻辑
}
```

```java
// ❌ 不好的设计：职责混乱
public class MyModule implements IModuleLoader {
    public ModuleFace getFace() {
        return new ModuleFace(this) {
            // 匿名内部类，难以维护
        };
    }
}
```

### 2. 错误处理

```java
public class MyModule implements IModuleLoader {
    private static final Logger logger = LoggerFactory.getLogger(MyModule.class);

    public MyModule() {
        try {
            initialize();
        } catch (Exception e) {
            logger.error("Failed to initialize module", e);
            // 提供降级方案
        }
    }
}
```

### 3. 资源管理

```java
public class MyModulePanel extends ModuleFace {
    private ExecutorService executor;

    public MyModulePanel(IModuleLoader loader) {
        super(loader);
        executor = Executors.newFixedThreadPool(4);
    }

    @Override
    public void closeTab() {
        // 清理资源
        executor.shutdown();
        super.closeTab();
    }
}
```

---

## 🎯 总结

### 核心架构原则

1. **统一接口**：所有模块实现 `IModuleLoader`
2. **位置无关**：代码与部署位置无关
3. **统一发现**：`ModuleDiscoveryService` 统一处理
4. **灵活部署**：Plugin 和 Built-in 可自由转换

### 技术栈

- **接口**：`IModuleLoader`
- **基类**：`ModuleFace`, `FastBaseTemplate`
- **发现**：`ModuleDiscoveryService`, `Reflections`
- **加载**：`CustomURLClassLoader`, `AppClassLoader`
- **显示**：`Module Gallery`, 菜单栏

### 设计优势

- ✅ 简单：统一的接口和实现
- ✅ 灵活：两种部署方式可自由选择
- ✅ 可扩展：易于添加新的模块发现机制
- ✅ 兼容性好：向后兼容旧版本

---

**版本**: eGPS 2.1+
**最后更新**: 2025-12-05
**作者**: eGPS Dev Team
