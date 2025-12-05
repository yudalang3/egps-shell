# ITools Manager 模块发现与管理设计方案

## 📝 文档元信息

- **创建日期**: 2025-12-03
- **作者**: Claude (基于用户需求设计)
- **版本**: v1.0
- **相关模块**: `egps2.builtin.modules.itoolmanager`
- **状态**: 设计阶段 (待实现)

---

## 🎯 用户需求原文

> ITools Manager 这模块你知道在哪吗？我感觉它打开之后应该去找一下有哪些模块可以被发现。我记得依赖的库里面，有一个reflections-0.10.2.jar包，可以通过反射扫描得到所有可用的。帮我设计一下，因为这里面有一个配置文件，如果出现1.配置文件中的模块大于所有可用模块；2.相反，这些情况怎么处理呢？怎么样做一个好的管理器呢？

### 需求分析

用户提出了以下核心需求：

1. **自动发现功能**: ITools Manager 打开时应该自动扫描和发现所有可用的模块
2. **使用 Reflections 库**: 项目中已有 `reflections-0.10.2.jar`，应该利用它通过反射扫描所有实现了 `IModuleLoader` 接口的类
3. **配置文件与实际模块不一致的处理**:
   - **场景A**: 配置文件中的模块 > 实际可用模块（配置中有些模块类已被删除或不存在）
   - **场景B**: 配置文件中的模块 < 实际可用模块（有新增的模块但配置文件中没有记录）
4. **设计一个"好的管理器"**: 需要优雅、健壮、用户友好的解决方案

---

## 💭 设计思考过程

### 第一步：定位和理解现有架构

通过代码分析发现：

1. **ITools Manager 位置**: `src/egps2/builtin/modules/itoolmanager/`
2. **核心组件**:
   - `IndependentModuleLoader`: 模块加载器入口
   - `GuiMain`: 主界面面板（继承自 `ComputationalModuleFace`）
   - `ElegantJTable`: 美观的表格展示组件，显示所有模块
   - `IModuleElement`: 封装模块加载器和加载状态的数据类

3. **现有加载机制**:
   - 配置文件路径: `~/.egps/config/egps2.loading.module.config.txt`
   - 格式: `完整类名\ttrue/false` (tab 分隔)
   - 加载器: `EGPS2ServiceLoader<IModuleLoader>`
     - 逐行读取配置文件
     - 通过 `Class.forName()` 反射加载类
     - 只返回 `toLoad=true` 的模块

4. **现有问题**:
   - ❌ 没有自动发现机制，新增模块必须手动添加到配置文件
   - ❌ 配置文件可能包含已删除的模块类名，导致加载警告
   - ❌ 用户无法知道哪些是新发现的模块，哪些是不可用的
   - ❌ 没有配置文件与实际代码的同步机制

### 第二步：确定设计目标

针对用户需求和现有问题，设计目标应该是：

1. **智能发现**: 使用 Reflections 库自动扫描所有实现 `IModuleLoader` 接口的类
2. **状态管理**: 引入模块状态枚举，区分可用、不可用、新发现等状态
3. **合并策略**: 智能合并扫描结果和配置文件，处理不一致情况
4. **用户可见**: UI 上清晰显示每个模块的状态，让用户一目了然
5. **配置同步**: 提供配置文件的自动更新和清理功能
6. **健壮性**: 优雅处理错误，不因单个模块失败而影响整体

### 第三步：核心设计决策

#### 决策1: 模块状态系统

引入 `ModuleStatus` 枚举，而不是简单的 boolean 标志：

```
AVAILABLE              - 模块可用且已加载
AVAILABLE_NOT_LOADED   - 模块可用但未加载
NEWLY_DISCOVERED       - 新发现的模块（配置文件中不存在）
UNAVAILABLE            - 配置文件中存在但无法加载
DEPRECATED             - 已标记为过时的模块（预留）
```

**理由**:
- 单一的 boolean 无法区分"不可用"和"未加载"
- 状态枚举便于扩展（如未来添加"需要更新"状态）
- 每个状态可以关联不同的 UI 表现（颜色、图标）

#### 决策2: 双源合并策略

采用"扫描优先，配置为辅"的策略：

1. **第一步**: Reflections 扫描所有可用模块（事实来源）
2. **第二步**: 读取配置文件（用户偏好）
3. **第三步**: 合并两者：
   - 配置中有 + 扫描到 = `AVAILABLE` 或 `AVAILABLE_NOT_LOADED`
   - 配置中有 + 扫描不到 = `UNAVAILABLE` (标记为不可用，但保留记录)
   - 配置中无 + 扫描到 = `NEWLY_DISCOVERED` (默认不加载，让用户选择)

**理由**:
- 以实际可用模块为准，配置文件只是偏好设置
- 不会因为配置文件错误导致启动失败
- 新模块默认不加载，避免意外破坏用户环境
- 保留不可用模块记录，方便用户了解历史状态

#### 决策3: UI 可视化增强

在表格中添加"Status"列，使用颜色编码：

```
绿色 (深绿色)   - 可用
蓝色 (钢蓝色)   - 可用但未加载
橙色 (加粗)     - 新发现
红色 (斜体)     - 不可用
灰色 (斜体)     - 已过时
```

**理由**:
- 视觉化状态比文字更直观
- 橙色高亮吸引用户注意新模块
- 红色警告让用户知道哪些模块有问题

#### 决策4: 智能导出策略

导出配置时：
- 自动清理不可用模块（可选保留为注释）
- 添加时间戳和统计信息
- 保留注释说明配置格式
- 新发现的模块添加 `[NEW]` 标记

**理由**:
- 保持配置文件整洁
- 注释形式的不可用模块便于追溯
- 统计信息帮助用户理解变化

### 第四步：关键挑战与解决方案

#### 挑战1: Reflections 扫描性能

**问题**: 扫描整个 classpath 可能需要几秒钟

**解决方案**:
- 限定扫描范围: 仅扫描 `egps2` 和 `demo` 包
- 首次启动时扫描并缓存结果
- 后续启动使用配置文件，提供手动"刷新扫描"按钮
- 考虑在后台线程执行扫描，避免阻塞 UI

#### 挑战2: 配置文件中有已删除的模块

**问题**: `com.example.DeletedModule` 在配置中，但类已被删除

**解决方案**:
- 标记为 `UNAVAILABLE` 状态
- 在 UI 中显示（红色斜体），让用户知晓
- 记录错误信息 "Class not found in classpath"
- 导出时注释掉或完全移除（用户可选）
- 日志中记录警告而非错误，不影响程序运行

**优点**:
- 不会导致启动失败
- 用户清楚知道哪些模块失效了
- 可以决定是否保留配置（也许以后会恢复该模块）

#### 挑战3: 新增模块未在配置文件中

**问题**: `com.example.NewModule` 存在但配置文件中没有

**解决方案**:
- 标记为 `NEWLY_DISCOVERED` 状态
- 在 UI 中高亮显示（橙色加粗）
- **默认不自动加载**（关键决策）
- 用户勾选后可以保存到配置文件
- 提供"启用所有新模块"快捷按钮（可选）

**优点**:
- 不会自动加载未知模块，保持系统稳定
- 用户能清楚看到有新模块可用
- 灵活选择是否启用，符合最小惊讶原则

#### 挑战4: 模块加载失败

**问题**: 扫描到类，但实例化时抛异常（如缺少依赖）

**解决方案**:
- 标记为 `UNAVAILABLE` 状态
- 记录详细错误信息（如 "Load error: NoClassDefFoundError: SomeDependency"）
- 在 tooltip 中显示完整错误堆栈
- 不影响其他模块加载

### 第五步：架构设计

```
┌─────────────────────────────────────────────────────────┐
│                    ITools Manager                        │
│                   (User Interface)                       │
└───────────────────┬─────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────────────────────┐
│              EGPS2ServiceLoader                          │
│         (加载协调器 - Orchestrator)                       │
│                                                          │
│  loadWithDiscovery(configPath, discoveryService)        │
│    ├─ 读取配置文件 (readConfigFile)                      │
│    ├─ 调用扫描服务 (scanAllModuleClasses)               │
│    ├─ 合并结果 (mergeResults)                            │
│    └─ 返回 IModuleElement 列表                           │
└───────────┬──────────────────────┬──────────────────────┘
            │                      │
            ▼                      ▼
┌─────────────────────┐  ┌──────────────────────────────┐
│ ModuleDiscoveryService│  │   Config File                │
│                      │  │   (~/.egps/config/           │
│ - scanAllModuleClasses│  │    egps2.loading.module.     │
│   (使用 Reflections)  │  │    config.txt)               │
│ - loadModuleInstance │  │                              │
└──────────────────────┘  └──────────────────────────────┘
            │
            ▼
┌─────────────────────────────────────────────────────────┐
│              Reflections Library                         │
│       (org.reflections:reflections:0.10.2)              │
│                                                          │
│  扫描 egps2.* 和 demo.* 包下所有实现                      │
│  IModuleLoader 接口的非抽象类                             │
└─────────────────────────────────────────────────────────┘
```

---

## 1. 当前架构分析

### 1.1 现有组件
- **位置**: `src/egps2/builtin/modules/itoolmanager/`
- **核心类**:
  - `IndependentModuleLoader`: 模块加载器
  - `GuiMain`: 主界面面板
  - `ElegantJTable`: 模块列表展示表格
  - `IModuleElement`: 模块元素封装（loader + 加载状态）

### 1.2 现有加载机制
- **配置文件**: `~/.egps/config/egps2.loading.module.config.txt`
- **格式**: `完整类名\ttrue/false`（tab分隔）
- **加载器**: `EGPS2ServiceLoader<IModuleLoader>`
  - 读取配置文件
  - 通过反射加载指定类
  - 返回需要加载的模块列表（toLoad=true）

### 1.3 当前问题
1. ❌ 无自动发现机制 - 新增模块需手动添加到配置文件
2. ❌ 配置文件可能包含已删除的模块类名
3. ❌ 缺少配置文件与实际代码的同步机制
4. ❌ 用户无法知道哪些模块是新发现的、哪些是不可用的

---

## 2. 设计目标

### 2.1 核心功能
1. ✅ **自动发现**: 使用 Reflections 库扫描所有实现 `IModuleLoader` 接口的类
2. ✅ **智能合并**: 合并扫描结果与配置文件，处理不一致情况
3. ✅ **状态标识**: 清晰标识模块状态（可用/不可用/新发现）
4. ✅ **配置同步**: 提供配置文件的自动更新和清理功能

### 2.2 用户体验
- 打开 ITools Manager 时自动刷新模块列表
- 可视化显示模块状态（图标/颜色）
- 提供"刷新扫描"按钮手动触发重新发现
- 导出配置时自动清理不可用模块

---

## 3. 详细设计

### 3.1 模块状态枚举

```java
package egps2.builtin.modules.itoolmanager;

/**
 * 模块状态枚举
 * Module status enumeration for tracking the availability and loading state of modules
 */
public enum ModuleStatus {
    /**
     * 模块可用且已加载
     * Module is available and loaded
     */
    AVAILABLE("Available", "模块可用且已加载"),

    /**
     * 模块可用但未加载
     * Module is available but not loaded
     */
    AVAILABLE_NOT_LOADED("Available (Not Loaded)", "模块可用但未加载"),

    /**
     * 新发现的模块（配置文件中不存在）
     * Newly discovered module (not in config file)
     */
    NEWLY_DISCOVERED("Newly Discovered", "新发现的模块（配置文件中不存在）"),

    /**
     * 配置文件中存在但无法加载
     * Module in config but cannot be loaded
     */
    UNAVAILABLE("Unavailable", "配置文件中存在但无法加载"),

    /**
     * 已标记为过时的模块
     * Module marked as deprecated
     */
    DEPRECATED("Deprecated", "已标记为过时的模块");

    private final String displayName;
    private final String description;

    ModuleStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
```

### 3.2 增强的 IModuleElement

```java
package egps2.builtin.modules.itoolmanager;

import egps2.modulei.IModuleLoader;

/**
 * 增强的模块元素，包含状态信息
 * Enhanced module element with status information
 */
public class IModuleElement {
    private IModuleLoader loader;           // 模块加载器实例（可能为null）
    private boolean toLoad;                 // 是否要加载
    private ModuleStatus status;            // 模块状态
    private String className;               // 完整类名
    private String errorMessage;            // 错误信息（如果不可用）

    /**
     * 构造器 - 用于可用模块
     * Constructor for available modules
     */
    public IModuleElement(IModuleLoader loader, boolean toLoad, ModuleStatus status) {
        this.loader = loader;
        this.toLoad = toLoad;
        this.status = status;
        this.className = loader != null ? loader.getClass().getName() : null;
    }

    /**
     * 构造器 - 用于不可用模块
     * Constructor for unavailable modules
     */
    public IModuleElement(String className, boolean toLoad, String errorMessage) {
        this.className = className;
        this.toLoad = toLoad;
        this.status = ModuleStatus.UNAVAILABLE;
        this.errorMessage = errorMessage;
    }

    // Getters and setters
    public IModuleLoader getLoader() {
        return loader;
    }

    public boolean isLoad() {
        return toLoad;
    }

    public void setLoad(boolean toLoad) {
        this.toLoad = toLoad;
    }

    public ModuleStatus getStatus() {
        return status;
    }

    public String getClassName() {
        return className;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
```

### 3.3 模块发现服务类

创建新类: `src/egps2/frame/features/ModuleDiscoveryService.java`

```java
package egps2.frame.features;

import egps2.modulei.IModuleLoader;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 模块发现服务
 * Module discovery service using Reflections library to scan for all IModuleLoader implementations
 *
 * <p>此服务使用 Reflections 库扫描 classpath 中所有实现 IModuleLoader 接口的类。
 * This service uses the Reflections library to scan the classpath for all classes implementing IModuleLoader.
 *
 * <p>使用方式：
 * Usage:
 * <pre>
 * ModuleDiscoveryService service = new ModuleDiscoveryService();
 * Set&lt;String&gt; moduleClasses = service.scanAllModuleClasses();
 * IModuleLoader loader = service.loadModuleInstance(className);
 * </pre>
 *
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ModuleDiscoveryService {
    private static final Logger log = LoggerFactory.getLogger(ModuleDiscoveryService.class);

    /**
     * 扫描所有实现 IModuleLoader 接口的类
     * Scans for all classes implementing IModuleLoader interface
     *
     * @return 类名集合 Set of fully qualified class names
     */
    public Set<String> scanAllModuleClasses() {
        Set<String> moduleClasses = new HashSet<>();

        try {
            log.info("Starting module discovery scan...");

            // 配置 Reflections 扫描器
            // Configure Reflections scanner
            Reflections reflections = new Reflections(
                new ConfigurationBuilder()
                    .forPackages("egps2", "demo")  // 扫描 egps2 和 demo 包
                    .setScanners(Scanners.SubTypes)
            );

            // 获取所有实现 IModuleLoader 接口的类
            // Get all classes implementing IModuleLoader interface
            Set<Class<? extends IModuleLoader>> subTypes =
                reflections.getSubTypesOf(IModuleLoader.class);

            for (Class<? extends IModuleLoader> clazz : subTypes) {
                // 排除抽象类和接口
                // Exclude abstract classes and interfaces
                if (!clazz.isInterface() &&
                    !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                    moduleClasses.add(clazz.getName());
                    log.debug("Discovered module: {}", clazz.getName());
                }
            }

            log.info("Module discovery complete. Total modules discovered: {}", moduleClasses.size());
        } catch (Exception e) {
            log.error("Error during module scanning", e);
        }

        return moduleClasses;
    }

    /**
     * 尝试加载单个模块实例
     * Attempts to load a single module instance
     *
     * @param className 完整类名 Fully qualified class name
     * @return IModuleLoader 实例 IModuleLoader instance
     * @throws Exception 如果加载失败 If loading fails
     */
    public IModuleLoader loadModuleInstance(String className) throws Exception {
        Class<?> clazz = Class.forName(className);
        if (IModuleLoader.class.isAssignableFrom(clazz)) {
            return (IModuleLoader) clazz.getDeclaredConstructor().newInstance();
        }
        throw new IllegalArgumentException(
            "Class " + className + " does not implement IModuleLoader");
    }
}
```

### 3.4 增强的 EGPS2ServiceLoader

在 `src/egps2/frame/features/EGPS2ServiceLoader.java` 中添加新方法:

```java
/**
 * 智能加载：结合配置文件和自动发现
 * Smart loading: combines config file and auto-discovery
 *
 * <p>此方法执行以下步骤：
 * This method performs the following steps:
 * <ol>
 *   <li>使用 Reflections 扫描所有可用模块</li>
 *   <li>读取配置文件中的用户偏好</li>
 *   <li>合并两者，处理不一致情况</li>
 *   <li>返回所有模块元素（包括可用和不可用的）</li>
 * </ol>
 *
 * @param configFilePath 配置文件路径 Path to config file
 * @param discoveryService 模块发现服务 Module discovery service
 * @return 需要加载的模块列表 List of modules to load
 */
public List<T> loadWithDiscovery(String configFilePath,
                                   ModuleDiscoveryService discoveryService) {

    // 1. 扫描所有可用模块
    // 1. Scan all available modules
    Set<String> discoveredModules = discoveryService.scanAllModuleClasses();
    log.info("Discovered {} modules via reflection", discoveredModules.size());

    // 2. 读取配置文件
    // 2. Read config file
    Map<String, Boolean> configuredModules = readConfigFile(configFilePath);
    log.info("Found {} modules in config file", configuredModules.size());

    // 3. 合并结果
    // 3. Merge results
    List<IModuleElement> allElements = new ArrayList<>();
    List<T> loadedProviders = new ArrayList<>();
    Set<String> processedClasses = new HashSet<>();

    // 3.1 处理配置文件中的模块
    // 3.1 Process modules from config file
    for (Map.Entry<String, Boolean> entry : configuredModules.entrySet()) {
        String className = entry.getKey();
        boolean toLoad = entry.getValue();
        processedClasses.add(className);

        if (discoveredModules.contains(className)) {
            // 模块可用
            // Module is available
            try {
                T loader = loadOnePlugin(className);
                ModuleStatus status = toLoad ?
                    ModuleStatus.AVAILABLE : ModuleStatus.AVAILABLE_NOT_LOADED;
                IModuleElement element = new IModuleElement(
                    (IModuleLoader)loader, toLoad, status);
                allElements.add(element);

                if (toLoad) {
                    loadedProviders.add(loader);
                }
                log.debug("Loaded configured module: {}", className);
            } catch (Exception e) {
                // 扫描到了但无法加载（可能有其他问题）
                // Scanned but failed to load (may have other issues)
                IModuleElement element = new IModuleElement(
                    className, toLoad, "Load error: " + e.getMessage());
                allElements.add(element);
                log.warn("Failed to load module {}: {}", className, e.getMessage());
            }
        } else {
            // 配置文件中有，但扫描时未发现（模块已删除或移动）
            // In config but not discovered (module deleted or moved)
            IModuleElement element = new IModuleElement(
                className, toLoad, "Class not found in classpath");
            allElements.add(element);
            log.warn("Module in config but not discovered: {}", className);
        }
    }

    // 3.2 处理新发现的模块（配置文件中没有）
    // 3.2 Process newly discovered modules (not in config)
    for (String className : discoveredModules) {
        if (!processedClasses.contains(className)) {
            try {
                T loader = loadOnePlugin(className);
                // 新发现的模块默认不加载（策略可配置）
                // Newly discovered modules are not loaded by default (policy configurable)
                boolean toLoad = shouldAutoLoadNewModule(className);
                IModuleElement element = new IModuleElement(
                    (IModuleLoader)loader, toLoad, ModuleStatus.NEWLY_DISCOVERED);
                allElements.add(element);

                if (toLoad) {
                    loadedProviders.add(loader);
                }
                log.info("Discovered new module not in config: {}", className);
            } catch (Exception e) {
                log.warn("Found class {} but failed to instantiate: {}",
                    className, e.getMessage());
            }
        }
    }

    // 4. 更新内部列表
    // 4. Update internal list
    allProviders.clear();
    allProviders.addAll(allElements);

    // 5. 生成统计报告
    // 5. Generate statistics report
    logDiscoveryStatistics(allElements, discoveredModules.size(),
                          configuredModules.size());

    return loadedProviders;
}

/**
 * 读取配置文件
 * Reads the configuration file
 */
private Map<String, Boolean> readConfigFile(String configFilePath) {
    Map<String, Boolean> result = new LinkedHashMap<>();
    File file = new File(configFilePath);

    if (!file.exists()) {
        log.warn("Config file not found: {}", configFilePath);
        return result;
    }

    try {
        List<String> lines = FileUtils.readLines(file, StandardCharsets.UTF_8);
        for (String line : lines) {
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            String[] splits = EGPSStringUtil.split(line, '\t', 2);
            if (splits.length >= 2) {
                result.put(splits[0], Boolean.parseBoolean(splits[1]));
            }
        }
    } catch (IOException e) {
        log.error("Error reading config file", e);
    }

    return result;
}

/**
 * 决定新发现的模块是否自动加载
 * Determines whether newly discovered modules should be auto-loaded
 *
 * 策略：默认不加载，避免破坏用户配置
 * Strategy: Default to not load, to avoid disrupting user configuration
 */
private boolean shouldAutoLoadNewModule(String className) {
    // 可以根据包名、命名规则等制定策略
    // Can implement policies based on package name, naming conventions, etc.

    // 例如：demo 包下的模块默认不加载
    // Example: Modules under demo package are not loaded by default
    if (className.startsWith("demo.")) {
        return false;
    }

    // 其他模块也默认不加载，让用户手动选择
    // Other modules also not loaded by default, let user choose manually
    return false;
}

/**
 * 记录发现统计信息
 * Logs discovery statistics
 */
private void logDiscoveryStatistics(List<IModuleElement> elements,
                                    int discoveredCount, int configCount) {
    long available = elements.stream()
        .filter(e -> e.getStatus() == ModuleStatus.AVAILABLE ||
                    e.getStatus() == ModuleStatus.AVAILABLE_NOT_LOADED)
        .count();
    long newlyDiscovered = elements.stream()
        .filter(e -> e.getStatus() == ModuleStatus.NEWLY_DISCOVERED)
        .count();
    long unavailable = elements.stream()
        .filter(e -> e.getStatus() == ModuleStatus.UNAVAILABLE)
        .count();

    log.info("=== Module Discovery Statistics ===");
    log.info("Scanned: {} | Configured: {}", discoveredCount, configCount);
    log.info("Available: {} | New: {} | Unavailable: {}",
             available, newlyDiscovered, unavailable);
    log.info("===================================");
}
```

### 3.5 UI 增强（ElegantJTable）

#### 3.5.1 添加状态列

修改 `src/egps2/builtin/modules/itoolmanager/ElegantJTable.java` 表格结构，添加"Status"列：

```java
private final int colIndexStatus = 9;  // 新增状态列

// 在 CustomTableModel 中
private final String[] columnNames = {
    "Number", "Icon", "Name", "Tooltip",
    nameStrings1, nameStrings2, nameStrings3, nameStrings4,
    "Loading", "Status"  // 新增
};

private final Class<?>[] columnTypes = {
    Integer.class, ImageIcon.class, String.class, ImageIcon.class,
    String.class, String.class, String.class, String.class,
    Boolean.class, String.class  // 新增 (或 ModuleStatus.class)
};
```

#### 3.5.2 状态列渲染器

```java
/**
 * 状态列渲染器
 * Status column renderer with color coding
 */
private class StatusRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);

        if (value instanceof ModuleStatus status) {
            label.setText(status.getDisplayName());

            // 根据状态设置不同的颜色
            // Set different colors based on status
            switch (status) {
                case AVAILABLE:
                    label.setForeground(new Color(34, 139, 34)); // 深绿色 Forest Green
                    break;
                case AVAILABLE_NOT_LOADED:
                    label.setForeground(new Color(70, 130, 180)); // 钢蓝色 Steel Blue
                    break;
                case NEWLY_DISCOVERED:
                    label.setForeground(new Color(255, 140, 0)); // 橙色 Dark Orange
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    break;
                case UNAVAILABLE:
                    label.setForeground(new Color(220, 20, 60)); // 深红色 Crimson
                    label.setFont(label.getFont().deriveFont(Font.ITALIC));
                    break;
                case DEPRECATED:
                    label.setForeground(Color.GRAY);
                    label.setFont(label.getFont().deriveFont(Font.ITALIC));
                    break;
            }

            label.setToolTipText(status.getDescription());
        }

        return label;
    }
}
```

#### 3.5.3 添加刷新按钮

在 `createSearchPanel()` 方法中添加刷新按钮：

```java
JButton refreshButton = new JButton("Refresh Scan");
refreshButton.setFont(defaultFont);
refreshButton.setToolTipText("Re-scan all available modules");
refreshButton.addActionListener(e -> {
    refreshModuleList();
});
refreshButton.setFocusable(false);
searchPanel.add(refreshButton);
searchPanel.add(Box.createHorizontalStrut(10));
```

```java
/**
 * 刷新模块列表
 * Refreshes the module list by re-scanning
 */
private void refreshModuleList() {
    // 清空现有数据
    // Clear existing data
    tableModel.setRowCount(0);

    // 重新扫描和加载
    // Re-scan and load
    ModuleDiscoveryService discoveryService = new ModuleDiscoveryService();
    EGPS2ServiceLoader<IModuleLoader> loader =
        new EGPS2ServiceLoader<>(IModuleLoader.class);
    loader.loadWithDiscovery(EGPSProperties.EGPS_MODULE_CONFIG_PATH,
                            discoveryService);

    // 重新填充表格
    // Re-populate table
    allProviders = loader.getAllProviders();
    populateTestData();

    // 显示统计信息
    // Show statistics
    SwingDialog.showInfoMSGDialog("Refresh Complete",
        String.format("Found %d modules\n" +
                     "New discoveries will be marked in orange",
                     allProviders.size()));
}
```

#### 3.5.4 智能导出配置

修改 `GuiMain.exportData()` 方法：

```java
@Override
public void exportData() {
    List<String> exportData = Lists.newArrayList();
    exportData.add("# eGPS2 Module Loading Configuration");
    exportData.add("# Auto-generated on: " + new Date());
    exportData.add("# Format: <FullClassName>\\t<true|false>");
    exportData.add("# Lines starting with # are comments");
    exportData.add("");

    List<IModuleElement> allProviders = elegantJTable.getAllProviders();

    // 统计信息
    // Statistics
    int availableCount = 0;
    int unavailableCount = 0;
    int newCount = 0;

    for (IModuleElement provider : allProviders) {
        ModuleStatus status = provider.getStatus();

        // 跳过不可用的模块（可选：可以保留但注释掉）
        // Skip unavailable modules (optional: can keep but comment out)
        if (status == ModuleStatus.UNAVAILABLE) {
            unavailableCount++;
            exportData.add("# [UNAVAILABLE] " + provider.getClassName() +
                          "\t" + provider.isLoad() +
                          " # " + provider.getErrorMessage());
            continue;
        }

        if (status == ModuleStatus.NEWLY_DISCOVERED) {
            newCount++;
            exportData.add("# [NEW] Module discovered");
        }

        IModuleLoader loader = provider.getLoader();
        if (loader != null) {
            availableCount++;
            String className = loader.getClass().getName();
            boolean toLoad = provider.isLoad();
            exportData.add(className + "\t" + toLoad);
        }
    }

    // 添加统计摘要
    // Add statistics summary
    exportData.add("");
    exportData.add("# === Statistics ===");
    exportData.add("# Available: " + availableCount);
    exportData.add("# Newly discovered: " + newCount);
    exportData.add("# Unavailable (commented out): " + unavailableCount);

    // 保存文件
    // Save file
    File file = new File(EGPSProperties.EGPS_MODULE_CONFIG_PATH);
    try {
        FileUtils.writeLines(file, exportData);
        String msg = String.format(
            "Configuration saved successfully!\n\n" +
            "Modules saved: %d\n" +
            "New modules: %d\n" +
            "Removed unavailable: %d\n\n" +
            "Please restart eGPS to apply changes.",
            availableCount, newCount, unavailableCount
        );
        SwingDialog.showInfoMSGDialog("Export Complete", msg);
    } catch (IOException e) {
        log.error("Failed to export configuration", e);
        SwingDialog.showErrorMSGDialog("Export Failed", e.getMessage());
    }
}
```

---

## 4. 处理策略总结

### 4.1 场景A: 配置文件中的模块 > 实际可用模块

**具体场景**: 配置文件有 `com.example.DeletedModule`，但类已被删除

**处理策略**:
1. ✅ 标记为 `UNAVAILABLE` 状态
2. ✅ 在表格中显示（红色斜体）
3. ✅ 显示错误信息（"Class not found in classpath"）
4. ✅ 导出时注释掉或完全移除（用户可选）
5. ✅ 日志中记录警告

**代码实现**:
```java
// In loadWithDiscovery()
if (!discoveredModules.contains(className)) {
    // 配置文件中有，但扫描时未发现
    IModuleElement element = new IModuleElement(
        className, toLoad, "Class not found in classpath");
    allElements.add(element);
    log.warn("Module in config but not discovered: {}", className);
}
```

**优点**:
- 用户可见哪些模块失效了
- 不会导致启动失败
- 可以决定是否保留配置（也许以后会恢复）

### 4.2 场景B: 配置文件中的模块 < 实际可用模块

**具体场景**: 新增了 `com.example.NewModule`，但配置文件中没有

**处理策略**:
1. ✅ 标记为 `NEWLY_DISCOVERED` 状态
2. ✅ 在表格中高亮显示（橙色加粗）
3. ✅ 默认不自动加载（避免破坏用户环境）
4. ✅ 用户可勾选后导出配置
5. ✅ 提供"全部启用新模块"快捷按钮

**代码实现**:
```java
// In loadWithDiscovery()
for (String className : discoveredModules) {
    if (!processedClasses.contains(className)) {
        // 新发现的模块
        T loader = loadOnePlugin(className);
        boolean toLoad = shouldAutoLoadNewModule(className); // 默认 false
        IModuleElement element = new IModuleElement(
            (IModuleLoader)loader, toLoad, ModuleStatus.NEWLY_DISCOVERED);
        allElements.add(element);
        log.info("Discovered new module not in config: {}", className);
    }
}
```

**优点**:
- 不会自动加载未知模块，保持稳定
- 用户能清楚看到有新模块可用
- 灵活选择是否启用

---

## 5. 实现步骤

### Phase 1: 核心功能 (优先级: 高)
- [ ] 1. 添加 `ModuleStatus` 枚举 (`src/egps2/builtin/modules/itoolmanager/ModuleStatus.java`)
- [ ] 2. 增强 `IModuleElement` 类，添加 status、className、errorMessage 字段
- [ ] 3. 创建 `ModuleDiscoveryService` 类 (`src/egps2/frame/features/ModuleDiscoveryService.java`)
- [ ] 4. 在 `EGPS2ServiceLoader` 中实现 `loadWithDiscovery()` 方法
- [ ] 5. 修改 `MainFrameProperties.getExistedLoaders()` 使用新的加载方法

### Phase 2: UI 增强 (优先级: 中)
- [ ] 6. 在 `ElegantJTable` 中添加状态列（第10列）
- [ ] 7. 实现 `StatusRenderer` 渲染器（颜色编码）
- [ ] 8. 添加"Refresh Scan"按钮
- [ ] 9. 实现 `refreshModuleList()` 方法

### Phase 3: 智能导出 (优先级: 中)
- [ ] 10. 增强 `exportData()` 方法，支持过滤不可用模块
- [ ] 11. 添加导出统计信息（时间戳、计数）
- [ ] 12. 支持导出时注释掉不可用模块

### Phase 4: 附加功能 (优先级: 低)
- [ ] 13. 添加"Enable All New"按钮（启用所有新发现的模块）
- [ ] 14. 添加模块过滤功能（仅显示某种状态的模块）
- [ ] 15. 支持模块详情查看（双击查看完整信息）
- [ ] 16. 添加配置文件备份功能（.bak）

---

## 6. 配置文件示例

### 6.1 当前配置文件格式（改进前）

```properties
egps2.builtin.modules.filemanager.IndependentModuleLoader	true
egps2.builtin.modules.itoolmanager.IndependentModuleLoader	true
egps2.builtin.modules.lowtextedi.IndependentModuleLoader	true
com.old.DeletedModule	false
```

**问题**:
- 无注释说明
- 无时间戳
- 包含已删除的模块类名

### 6.2 改进后的配置文件格式

```properties
# eGPS2 Module Loading Configuration
# Auto-generated on: 2025-12-03 10:30:45
# Format: <FullClassName>\t<true|false>
# Lines starting with # are comments

# === Core Modules ===
egps2.builtin.modules.filemanager.IndependentModuleLoader	true
egps2.builtin.modules.itoolmanager.IndependentModuleLoader	true
egps2.builtin.modules.lowtextedi.IndependentModuleLoader	true
egps2.builtin.modules.largetextedi.IndependentModuleLoader	true

# === Optional Modules ===
egps2.builtin.modules.gallerymod.IndependentModuleLoader	false
egps2.builtin.modules.voice.dockable.IndependentModuleLoader	false
egps2.builtin.modules.voice.floating.IndependentModuleLoader	false

# === Demo Modules ===
demo.handytools.HandyToolExampleMain	false
demo.dockable.IndependentModuleLoader	false
demo.floating.IndependentModuleLoader	false

# [NEW] Module discovered
demo.newfeature.IndependentModuleLoader	false

# === Unavailable Modules (commented out) ===
# [UNAVAILABLE] com.old.DeletedModule	false # Class not found in classpath
# [UNAVAILABLE] demo.broken.BrokenModule	false # Load error: NoClassDefFoundError

# === Statistics ===
# Available: 13
# Newly discovered: 1
# Unavailable (commented out): 2
```

---

## 7. 优势与注意事项

### 7.1 设计优势
1. ✅ **自动化**: 无需手动维护模块列表
2. ✅ **安全性**: 新模块默认不加载，用户主动选择
3. ✅ **可追溯**: 清晰记录模块状态变化
4. ✅ **容错性**: 优雅处理缺失或损坏的模块
5. ✅ **可扩展**: 易于添加新的发现策略和状态
6. ✅ **用户友好**: 颜色编码直观，操作简单

### 7.2 注意事项

#### 7.2.1 性能问题
⚠️ **Reflections 扫描可能需要几秒钟**

**建议**:
- 首次启动时扫描并缓存结果
- 后续使用配置文件 + 可选刷新
- 可以在后台线程执行扫描，显示进度条
- 限定扫描范围（仅 `egps2` 和 `demo` 包）

**代码示例**:
```java
// 后台扫描
SwingWorker<Set<String>, Void> worker = new SwingWorker<>() {
    @Override
    protected Set<String> doInBackground() {
        return discoveryService.scanAllModuleClasses();
    }

    @Override
    protected void done() {
        // 更新 UI
    }
};
worker.execute();
```

#### 7.2.2 依赖检查
⚠️ **确保 `reflections-0.10.2.jar` 在 classpath 中**

检查方法:
```bash
ls dependency-egps/ | grep reflections
```

如果不存在，需要添加依赖。

#### 7.2.3 包范围限定
⚠️ **明确指定扫描包，避免扫描整个 classpath**

当前配置:
```java
.forPackages("egps2", "demo")  // 仅扫描这两个包
```

如果有其他模块包，需要添加到这里。

#### 7.2.4 类加载顺序
⚠️ **某些模块可能依赖其他模块**

当前设计不处理依赖关系。如果需要，可以：
- 在 `IModuleLoader` 中添加 `getDependencies()` 方法
- 加载时按依赖拓扑排序

#### 7.2.5 向后兼容
⚠️ **保留旧的加载方式作为 fallback**

```java
public List<T> load(String configFilePath) {
    // 旧的加载方式，作为 fallback
    // 不使用 Reflections，仅读取配置文件
}

public List<T> loadWithDiscovery(String configFilePath, ...) {
    // 新的加载方式，结合 Reflections
}
```

---

## 8. 测试计划

### 8.1 单元测试
- [ ] 测试 `ModuleDiscoveryService.scanAllModuleClasses()`
  - 验证能发现所有实现 `IModuleLoader` 的类
  - 验证排除抽象类和接口
- [ ] 测试 `EGPS2ServiceLoader.loadWithDiscovery()` 各种场景
  - 空配置文件
  - 配置有不存在的模块
  - 配置缺少新模块
- [ ] 测试配置文件读写
  - 测试 `readConfigFile()`
  - 测试导出功能

### 8.2 集成测试
- [ ] **场景1**: 空配置文件 + 多个可用模块
  - 预期: 所有模块标记为 `NEWLY_DISCOVERED`
- [ ] **场景2**: 配置文件有已删除的模块
  - 预期: 删除的模块标记为 `UNAVAILABLE`
- [ ] **场景3**: 新增模块未在配置文件中
  - 预期: 新模块标记为 `NEWLY_DISCOVERED`，默认不加载
- [ ] **场景4**: 混合场景（新增 + 删除 + 正常）
  - 预期: 所有状态正确显示

### 8.3 UI 测试
- [ ] 测试表格显示各种状态
  - 验证颜色编码正确
  - 验证 tooltip 显示
- [ ] 测试刷新功能
  - 点击"Refresh Scan"按钮
  - 验证重新扫描并更新表格
- [ ] 测试导出功能
  - 导出后检查配置文件格式
  - 验证不可用模块被注释
- [ ] 测试搜索和过滤
  - 搜索框输入文本
  - 验证表格正确过滤

---

## 9. 未来扩展

### 9.1 高级功能
- **模块依赖管理**: 模块A依赖模块B，自动处理加载顺序
- **模块版本控制**: 支持同一模块的多个版本
- **热重载模块**: 无需重启软件即可加载/卸载模块
- **模块市场**: 从远程仓库下载新模块
- **模块权限管理**: 不同模块需要不同权限

### 9.2 性能优化
- **增量扫描**: 只检查变化的JAR文件
- **并行加载模块**: 使用线程池同时加载多个模块
- **延迟加载**: 按需加载模块，而非启动时全部加载
- **缓存元数据**: 将扫描结果缓存到文件，加快后续启动

### 9.3 用户体验优化
- **模块分组折叠**: 按类别折叠/展开
- **快速操作**: 右键菜单（启用、禁用、查看详情）
- **模块详情面板**: 双击模块查看详细信息（作者、版本、依赖等）
- **配置导入导出**: 支持导入其他用户的配置

---

## 10. 参考资料

- **Reflections库文档**: https://github.com/ronmamo/reflections
- **Java ServiceLoader**: https://docs.oracle.com/javase/8/docs/api/java/util/ServiceLoader.html
- **设计模式**: Plugin Pattern, Strategy Pattern, Observer Pattern
- **eGPS项目文档**: `CLAUDE.md`, `docs/understanding_builtin_modules_en.md`

---

## 11. 附录

### 11.1 相关文件路径

```
src/egps2/builtin/modules/itoolmanager/
├── IndependentModuleLoader.java        # 模块加载器入口
├── GuiMain.java                        # 主界面面板
├── ElegantJTable.java                  # 表格组件
├── IModuleElement.java                 # 模块元素（需要增强）
└── ModuleStatus.java                   # 模块状态枚举（新增）

src/egps2/frame/features/
├── EGPS2ServiceLoader.java             # 服务加载器（需要增强）
└── ModuleDiscoveryService.java         # 模块发现服务（新增）

dependency-egps/
└── reflections-0.10.2.jar              # Reflections 依赖库

~/.egps/config/
└── egps2.loading.module.config.txt     # 模块配置文件
```

### 11.2 关键接口和类

```java
// 核心接口
egps2.modulei.IModuleLoader

// 核心类
egps2.frame.MainFrameProperties
egps2.UnifiedAccessPoint
egps2.EGPSProperties

// 相关枚举
egps2.modulei.ModuleClassification
```

---

## 📌 总结

这个设计方案完整地解决了用户提出的需求：

1. ✅ **自动发现**: 使用 Reflections 库扫描所有 `IModuleLoader` 实现
2. ✅ **配置 > 实际**: 标记为 `UNAVAILABLE`，注释掉导出
3. ✅ **配置 < 实际**: 标记为 `NEWLY_DISCOVERED`，默认不加载
4. ✅ **好的管理器**: 健壮、直观、可扩展

核心思想是**"扫描优先，配置为辅"**，既保证了系统的灵活性和自动化，又保持了用户配置的稳定性和可控性。

---

**文档结束 | End of Document**
