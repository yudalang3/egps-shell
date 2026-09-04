package egps2.frame.features;

import egps2.builtin.modules.itoolmanager.IModuleElement;
import egps2.builtin.modules.itoolmanager.ModuleStatus;
import egps2.modulei.IModuleLoader;
import egps2.panels.dialog.SwingDialog;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.string.EGPSStringUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * EGPS2ServiceLoader supports the main eGPS window, actions, or tab management.
 *
 * <p>Enhanced with module discovery capabilities using Reflections library.
 * Supports both traditional config-based loading and automatic module scanning.
 */
public class EGPS2ServiceLoader<T> {
    private static final Logger log = LoggerFactory.getLogger(EGPS2ServiceLoader.class);
    private final Class<T> service;
    private final List<IModuleElement> allProviders = new ArrayList<>();
    private ModuleDiscoveryService discoveryService;

    public EGPS2ServiceLoader(Class<T> service) {
        this.service = service;
    }

    /**
     * Sets the discovery service for loading modules from both classpath and plugins
     */
    public void setDiscoveryService(ModuleDiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    public List<IModuleElement> getAllProviders() {
        return allProviders;
    }

    public List<T> load(String configFilePath) {

        File file = new File(configFilePath);

        if (!file.exists()) {
            log.error("Sorry, the file is not exist to loading the modules.");
            return Collections.emptyList();
        }

		List<String> strings;
        try {
			strings = FileUtils.readLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
            SwingDialog.showErrorMSGDialog("Module config error", "Error loading the module configuration file.");
            return Collections.emptyList();
        }

        List<T> providers = new ArrayList<>();

        List<String> moduleLoadingErrors = new ArrayList<>();

        for (String string : strings) {
            if (string.isEmpty() || string.startsWith("#")) {
                continue;
            }
            String[] splits = EGPSStringUtil.split(string, '\t', 2);
            var classPath = splits[0];
            boolean toLoad = Boolean.parseBoolean(splits[1]);

            try {
                T t = loadOnePlugin(classPath);
                IModuleElement iModuleElement = new IModuleElement((IModuleLoader) t, toLoad);
                allProviders.add(iModuleElement);
                if (toLoad) {
                    providers.add(t);
                }
            } catch (Exception e) {
                //log.error(e.getMessage());
                log.warn("Can not found the module {}", classPath);
				moduleLoadingErrors.add(string);
            }
        }

		if (!moduleLoadingErrors.isEmpty()){
			StringBuilder sBuilder = new StringBuilder();
			sBuilder.append("Following modules need to check: ");
			for (String error : moduleLoadingErrors){
				sBuilder.append("\n").append(error);
			}
			SwingDialog.showErrorMSGDialog("Module loading error",  sBuilder.toString());
		}

        return providers;
    }

    protected T loadOnePlugin(String line) throws Exception {
        // Try using ModuleDiscoveryService first (supports both classpath and plugin JARs)
        if (discoveryService != null && service == IModuleLoader.class) {
            try {
                IModuleLoader loader = discoveryService.loadModuleInstance(line);
                @SuppressWarnings("unchecked")
                T provider = (T) loader;
                return provider;
            } catch (Exception e) {
                // Fall through to traditional loading
                log.debug("ModuleDiscoveryService failed for {}, trying Class.forName", line);
            }
        }

        // Traditional loading (classpath only)
        Class<?> clazz = Class.forName(line);
        if (service.isAssignableFrom(clazz)) {
            @SuppressWarnings("unchecked")
            T provider = (T) clazz.getDeclaredConstructor().newInstance();
            return provider;
        } else {
            throw new IllegalArgumentException(
                    "Class " + clazz.getName() + " does not implement " + service.getName());
        }
    }

//    public static void main(String[] args) {
//        // 假设有一个接口 MyService，配置文件路径为 /path/to/custom/services.txt
//        EGPS2ServiceLoader<MyService> loader = new EGPS2ServiceLoader<>(MyService.class);
//        List<MyService> services = loader.load("/path/to/custom/services.txt");
//
//        // 打印加载到的服务实现
//        for (MyService service : services) {
//            System.out.println(service.getClass().getName());
//        }
//    }

    /**
     * Smart loading: combines config file and auto-discovery
     *
     * <p>This method performs the following steps:
     * <ol>
     *   <li>Uses Reflections to scan all available modules (including plugins)</li>
     *   <li>Reads user preferences from config file</li>
     *   <li>Merges both sources, handling inconsistencies</li>
     *   <li>Returns all module elements (including available and unavailable)</li>
     * </ol>
     *
     * @param configFilePath Path to config file
     * @param discoveryService Module discovery service
     * @return List of modules to load
     */
    public List<T> loadWithDiscovery(String configFilePath, ModuleDiscoveryService discoveryService) {
        // Set the discovery service for plugin loading support
        this.discoveryService = discoveryService;

        // 1. Scan all available modules (including plugins from ~/.egps2/config/plugin)
        Set<String> discoveredModules = discoveryService.scanAllModuleClasses();
        log.info("Discovered {} modules via reflection (including plugins)", discoveredModules.size());

        // 2. Read config file
        Map<String, Boolean> configuredModules = readConfigFile(configFilePath);
        log.info("Found {} modules in config file", configuredModules.size());

        // 3. Merge results
        List<T> loadedProviders = new ArrayList<>();
        Set<String> processedClasses = new HashSet<>();

        // 3.1 Process modules from config file
        for (Map.Entry<String, Boolean> entry : configuredModules.entrySet()) {
            String className = entry.getKey();
            boolean toLoad = entry.getValue();
            processedClasses.add(className);

            if (discoveredModules.contains(className)) {
                // Module is available
                try {
                    T loader = loadOnePlugin(className);
                    ModuleStatus status = toLoad ?
                        ModuleStatus.AVAILABLE : ModuleStatus.AVAILABLE_NOT_LOADED;
                    IModuleElement element = new IModuleElement(
                        (IModuleLoader)loader, toLoad, status);
                    allProviders.add(element);

                    if (toLoad) {
                        loadedProviders.add(loader);
                    }
                    log.debug("Loaded configured module: {}", className);
                } catch (Exception e) {
                    // Scanned but failed to load (may have other issues)
                    IModuleElement element = new IModuleElement(
                        className, toLoad, "Load error: " + e.getMessage());
                    allProviders.add(element);
                    log.warn("Failed to load module {}: {}", className, e.getMessage());
                }
            } else {
                // In config but not discovered (module deleted or moved)
                IModuleElement element = new IModuleElement(
                    className, toLoad, "Class not found in classpath");
                allProviders.add(element);
                log.warn("Module in config but not discovered: {}", className);
            }
        }

        // 3.2 Process newly discovered modules (not in config)
        for (String className : discoveredModules) {
            if (!processedClasses.contains(className)) {
                try {
                    T loader = loadOnePlugin(className);
                    // Newly discovered modules are not loaded by default
                    boolean toLoad = shouldAutoLoadNewModule(className);
                    IModuleElement element = new IModuleElement(
                        (IModuleLoader)loader, toLoad, ModuleStatus.NEWLY_DISCOVERED);
                    allProviders.add(element);

                    if (toLoad) {
                        loadedProviders.add(loader);
                    }
                    if (isCoreModuleClass(className)){

                    }else {
                        log.info("Discovered new module not in config: {}", className);
                    }
                } catch (Exception e) {
                    log.warn("Found class {} but failed to instantiate: {}",
                        className, e.getMessage());
                }
            }
        }

        // 4. Generate statistics report
        logDiscoveryStatistics(allProviders, discoveredModules.size(), configuredModules.size());

        return loadedProviders;
    }

    /**
     * Checks if a class is a core module (already in Mainframe core menu).
     *
     * <p>Core modules are hardcoded in MyFrame's Mainframe core menu and should not
     * be reported as "newly discovered" modules by the module discovery system.
     *
     * <p>Core modules include:
     * <ul>
     *   <li>egps2.builtin.modules.filemanager.IndependentModuleLoader</li>
     *   <li>egps2.builtin.modules.gallerymod.IndependentModuleLoader</li>
     *   <li>egps2.builtin.modules.lowtextedi.IndependentModuleLoader</li>
     *   <li>egps2.builtin.modules.largetextedi.IndependentModuleLoader</li>
     *   <li>demo.handytools.HandyToolExampleMain</li>
     *   <li>demo.dockable.IndependentModuleLoader</li>
     *   <li>demo.floating.IndependentModuleLoader</li>
     *   <li>egps2.builtin.modules.itoolmanager.IndependentModuleLoader</li>
     * </ul>
     *
     * @param className Class to check
     * @return true if the class is a core module, false otherwise
     */
    private boolean isCoreModuleClass(String className) {
        // List of core modules that are hardcoded in Mainframe core menu
        return className.equals("egps2.builtin.modules.filemanager.IndependentModuleLoader") ||
                className.equals("egps2.builtin.modules.gallerymod.IndependentModuleLoader") ||
                className.equals("egps2.builtin.modules.lowtextedi.IndependentModuleLoader") ||
                className.equals("egps2.builtin.modules.largetextedi.IndependentModuleLoader") ||
                className.equals("demo.handytools.HandyToolExampleMain") ||
                className.equals("demo.dockable.IndependentModuleLoader") ||
                className.equals("demo.floating.IndependentModuleLoader") ||
                className.equals("egps2.builtin.modules.itoolmanager.IndependentModuleLoader");
    }

    /**
     * Reads the configuration file
     *
     * @param configFilePath Path to config file
     * @return Map of class name to load flag
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
     * Determines whether newly discovered modules should be auto-loaded
     *
     * Strategy: Default to not load, to avoid disrupting user configuration
     *
     * @param className Class name of the module
     * @return true if should auto-load
     */
    private boolean shouldAutoLoadNewModule(String className) {
        // Modules under demo package are not loaded by default
        if (className.startsWith("demo.")) {
            return false;
        }

        // Other modules also not loaded by default, let user choose manually
        return false;
    }

    /**
     * Logs discovery statistics
     *
     * @param elements List of module elements
     * @param discoveredCount Number of discovered modules
     * @param configCount Number of configured modules
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
}
