package egps2.frame.features;

import egps2.EGPSProperties;
import egps2.modulei.IModuleLoader;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Module discovery service using Reflections library to scan for all IModuleLoader implementations.
 *
 * <p>This service uses the Reflections library to scan the classpath for all classes implementing IModuleLoader.
 * It provides automatic module discovery functionality for the ITools Manager and Module Gallery.
 *
 * <p><strong>V2.1+ Enhancement:</strong> Now includes plugin JAR scanning from ~/.egps2/config/plugin directory.
 * Discovery treats built-in modules and plugin modules as scan sources, but discovery results still need
 * to be merged with configuration before they become active loaders shown by Module Gallery.
 *
 * <p>Usage:
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
     * Default constructor that scans the entire JVM classpath for modules.
     * No package restrictions - discovers IModuleLoader implementations in any package.
     */
    public ModuleDiscoveryService() {
    }

    /**
     * Scans for all classes implementing IModuleLoader interface
     *
     * <p><strong>Scan Sources:</strong>
     * <ul>
     *   <li>All classes in the entire JVM classpath (no package restrictions)</li>
     *   <li>This includes all JARs in dependency-egps/ directory</li>
     *   <li>External plugin JARs from ~/.egps2/config/plugin directory</li>
     * </ul>
     *
     * <p><strong>Exclusion Rules:</strong>
     * <ul>
     *   <li>Abstract classes and interfaces are excluded</li>
     *   <li>SubTab classes (extending DockableTabModuleFaceOfVoice) are excluded</li>
     *   <li>Core modules (already in Mainframe core menu) are excluded</li>
     *   <li>Template base classes (FastBaseTemplate, plugin template loaders) are excluded</li>
     * </ul>
     *
     * @return Set of fully qualified class names
     */
    public Set<String> scanAllModuleClasses() {
        Set<String> moduleClasses = new HashSet<>();
        List<String> duplicateWarnings = new ArrayList<>();

        try {
            log.info("Starting module discovery scan for entire JVM classpath");

            // Get plugin JAR URLs (NOT in classpath)
            List<URL> pluginUrls = getPluginJarUrls();

            if (!pluginUrls.isEmpty()) {
                log.info("Found {} plugin JARs to scan", pluginUrls.size());
            }

            // Step 1: Scan entire classpath using Reflections
            // This includes dependency-egps/*.jar which is already in classpath
            // Use ClasspathHelper to get all URLs from the JVM's classpath
            ConfigurationBuilder configBuilder = new ConfigurationBuilder()
                    .setScanners(Scanners.SubTypes)
                    .addUrls(ClasspathHelper.forJavaClassPath());

            Reflections reflections = new Reflections(configBuilder);
            Set<Class<? extends IModuleLoader>> classpathModules =
                reflections.getSubTypesOf(IModuleLoader.class);

            log.debug("Found {} modules in classpath", classpathModules.size());

            // Process classpath modules (includes dependency-egps JARs)
            for (Class<? extends IModuleLoader> clazz : classpathModules) {
                if (shouldIncludeModule(clazz)) {
                    moduleClasses.add(clazz.getName());
                    log.debug("Discovered classpath module: {}", clazz.getName());
                }
            }

            // Step 2: Manually scan plugin JARs (from ~/.egps2/config/plugin/)
            // Pass existing moduleClasses to detect duplicates
            Set<String> pluginModules = scanPluginJars(pluginUrls, moduleClasses, duplicateWarnings);
            moduleClasses.addAll(pluginModules);

            log.info("Module discovery complete. Total modules discovered: {} ({} from classpath, {} from plugins)",
                    moduleClasses.size(),
                    classpathModules.size(),
                    pluginModules.size());

            // Show duplicate warnings if any
            if (!duplicateWarnings.isEmpty()) {
                showDuplicateWarningDialog(duplicateWarnings);
            }
        } catch (Exception e) {
            log.error("Error during module scanning", e);
            e.printStackTrace();
        }

        return moduleClasses;
    }

    /**
     * Manually scans plugin JARs for IModuleLoader implementations
     *
     * @param pluginUrls List of plugin JAR URLs
     * @param existingModules Set of already discovered module class names
     * @param duplicateWarnings List to collect duplicate warnings
     * @return Set of plugin module class names
     */
    private Set<String> scanPluginJars(List<URL> pluginUrls, Set<String> existingModules, List<String> duplicateWarnings) {
        Set<String> pluginModules = new HashSet<>();

        for (URL pluginUrl : pluginUrls) {
            try {
                File jarFile = new File(pluginUrl.toURI());
                log.debug("Scanning plugin JAR: {}", jarFile.getName());

                // Create custom class loader for this plugin
                egps2.plugin.manager.CustomURLClassLoader classLoader =
                        new egps2.plugin.manager.CustomURLClassLoader(
                                new URL[]{pluginUrl},
                                ClassLoader.getSystemClassLoader());

                // List all classes in the JAR
                java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
                java.util.Enumeration<java.util.jar.JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    java.util.jar.JarEntry entry = entries.nextElement();
                    String name = entry.getName();

                    // Check if it's a class file
                    if (name.endsWith(".class") && !name.contains("$")) {
                        // Convert to class name
                        String className = name.replace('/', '.').replace(".class", "");

                        try {
                            // IMPORTANT: First check if this class is already loaded in parent classloader
                            // This avoids LinkageError when plugin JAR contains classes from main app
                            Class<?> clazz = null;
                            boolean isFromPlugin = false;

                            try {
                                // Try to load from parent (main app) first
                                clazz = Class.forName(className, false, ClassLoader.getSystemClassLoader());

                                // Check if this is a duplicate module
                                if (IModuleLoader.class.isAssignableFrom(clazz) && existingModules.contains(className)) {
                                    String warning = String.format("Duplicate module detected: %s\n  - Found in classpath (dependency-egps or source)\n  - Also found in plugin: %s\n  - Using classpath version, ignoring plugin version",
                                            className, jarFile.getName());
                                    log.warn(warning);
                                    duplicateWarnings.add(warning);
                                }

                                // If successful, this class is from main app, skip it
                                log.trace("Skipping class {} (already in main app)", className);
                                continue;
                            } catch (ClassNotFoundException e) {
                                // Not in main app, load from plugin JAR
                                isFromPlugin = true;
                            }

                            if (isFromPlugin) {
                                // Load the class from plugin JAR
                                clazz = classLoader.loadClass(className);

                                // Check if it implements IModuleLoader
                                if (IModuleLoader.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                                    // Check exclusions
                                    if (shouldIncludeModule(clazz)) {
                                        pluginModules.add(className);
                                        log.info("Discovered plugin module: {} from {}", className, jarFile.getName());
                                    }
                                }
                            }
                        } catch (ClassNotFoundException | NoClassDefFoundError e) {
                            log.trace("Could not load class {}: {}", className, e.getMessage());
                        } catch (LinkageError e) {
                            // Skip classes that cause linkage errors (duplicate definitions)
                            log.trace("Skipping class {} due to LinkageError: {}", className, e.getMessage());
                        }
                    }
                }

                jar.close();
            } catch (Exception e) {
                log.warn("Error scanning plugin JAR {}: {}", pluginUrl, e.getMessage());
            }
        }

        return pluginModules;
    }

    /**
     * Shows a warning dialog for duplicate modules
     *
     * @param duplicateWarnings List of duplicate warning messages
     */
    private void showDuplicateWarningDialog(List<String> duplicateWarnings) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            StringBuilder message = new StringBuilder();
            message.append("Duplicate modules detected!\n\n");
            message.append("The following modules exist in both classpath (dependency-egps) and plugin directory:\n\n");

            for (String warning : duplicateWarnings) {
                message.append("• ").append(warning).append("\n\n");
            }

            message.append("\nRecommendation:\n");
            message.append("Remove the JAR from one location to avoid confusion.\n");
            message.append("Classpath version takes precedence.");

            egps2.panels.dialog.SwingDialog.showWarningMSGDialog(
                    "Duplicate Module Warning",
                    message.toString());
        });
    }

    /**
     * Determines if a module class should be included
     */
    private boolean shouldIncludeModule(Class<?> clazz) {
        // Exclude abstract classes and interfaces
        if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }

        // Exclude SubTab classes
        if (isSubTabClass(clazz)) {
            return false;
        }

        // Exclude template base classes (with plugin handling)
        if (isTemplateBaseClass(clazz)) {
            return false;
        }

        return true;
    }

    /**
     * Gets URLs of all plugin JAR files from the plugin directory
     *
     * @return List of URLs pointing to plugin JAR files
     */
    private List<URL> getPluginJarUrls() {
        List<URL> urls = new ArrayList<>();
        File pluginDir = new File(EGPSProperties.PROPERTIES_DIR + "/plugin");

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            log.debug("Plugin directory does not exist: {}", pluginDir.getAbsolutePath());
            return urls;
        }

        File[] files = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            log.debug("No plugin JAR files found in: {}", pluginDir.getAbsolutePath());
            return urls;
        }

        for (File file : files) {
            try {
                urls.add(file.toURI().toURL());
                log.debug("Added plugin JAR to scan: {}", file.getName());
            } catch (Exception e) {
                log.warn("Failed to add plugin JAR {}: {}", file.getName(), e.getMessage());
            }
        }

        return urls;
    }

    /**
     * Gets URLs of all JAR files from the dependency-egps directory
     *
     * <p>This allows modules packaged as JAR files in dependency-egps to be discovered,
     * even if they are not in the standard package namespaces (egps2, demo, etc.).
     *
     * @return List of URLs pointing to dependency JAR files
     */
    private List<URL> getDependencyJarUrls() {
        List<URL> urls = new ArrayList<>();

        // Try to locate dependency-egps directory
        // It should be in the classpath, but we need to scan it for JARs
        String[] possiblePaths = {
            "dependency-egps",                    // Relative to current directory
            "./dependency-egps",                  // Explicit relative
            "../dependency-egps",                 // One level up
            "../../dependency-egps"               // Two levels up (for test scenarios)
        };

        File dependencyDir = null;
        for (String path : possiblePaths) {
            File dir = new File(path);
            if (dir.exists() && dir.isDirectory()) {
                dependencyDir = dir;
                log.debug("Found dependency-egps directory at: {}", dir.getAbsolutePath());
                break;
            }
        }

        if (dependencyDir == null) {
            log.debug("dependency-egps directory not found in standard locations");
            return urls;
        }

        File[] files = dependencyDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            log.debug("No JAR files found in: {}", dependencyDir.getAbsolutePath());
            return urls;
        }

        for (File file : files) {
            try {
                // Only scan JARs that might contain plugins
                // Skip known library JARs by checking if they have eGPS2.plugin.properties
                if (hasPluginProperties(file)) {
                    urls.add(file.toURI().toURL());
                    log.debug("Added dependency JAR to scan: {}", file.getName());
                } else {
                    log.trace("Skipping dependency JAR (no plugin properties): {}", file.getName());
                }
            } catch (Exception e) {
                log.warn("Failed to add dependency JAR {}: {}", file.getName(), e.getMessage());
            }
        }

        return urls;
    }

    /**
     * Checks if a JAR file contains eGPS2.plugin.properties
     *
     * @param jarFile JAR file to check
     * @return true if the JAR contains plugin properties, false otherwise
     */
    private boolean hasPluginProperties(File jarFile) {
        try {
            java.util.jar.JarFile jar = new java.util.jar.JarFile(jarFile);
            boolean hasProps = jar.getEntry("eGPS2.plugin.properties") != null;
            jar.close();
            return hasProps;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks if a class is a SubTab (extends DockableTabModuleFaceOfVoice).
     *
     * <p>SubTab classes are not standalone modules - they are sub-components
     * that should be loaded by their parent module, not by the module discovery system.
     *
     * @param clazz Class to check
     * @return true if the class is a SubTab, false otherwise
     */
    private boolean isSubTabClass(Class<?> clazz) {
        try {
            // Try to load DockableTabModuleFaceOfVoice class
            Class<?> dockableTabClass = Class.forName(
                "egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice");

            // Check if the class extends DockableTabModuleFaceOfVoice
            return dockableTabClass.isAssignableFrom(clazz) && !dockableTabClass.equals(clazz);
        } catch (ClassNotFoundException e) {
            // If DockableTabModuleFaceOfVoice doesn't exist, no classes can be SubTabs
            return false;
        }
    }


    /**
     * Checks if a class is a template base class that should be excluded.
     *
     * <p><strong>Exclusion Logic:</strong>
     * <ul>
     *   <li>FastBaseTemplate itself is ALWAYS excluded (it's the template base)</li>
     *   <li>FastBaseTemplate subclasses:
     *     <ul>
     *       <li>If from plugin JAR: INCLUDED (actual plugin module)</li>
     *       <li>If from classpath: EXCLUDED (template in shell)</li>
     *     </ul>
     *   </li>
     *   <li>egps2.plugin.fastmodtem.IndependentModuleLoader is ALWAYS excluded (template)</li>
     * </ul>
     *
     * @param clazz Class to check
     * @return true if the class should be excluded, false otherwise
     */
    private boolean isTemplateBaseClass(Class<?> clazz) {
        String className = clazz.getName();

        // FastBaseTemplate itself is always excluded (it's the base template)
        if (className.equals("egps2.plugin.fastmodtem.FastBaseTemplate")) {
            return true;
        }

        // IndependentModuleLoader template is always excluded
        if (className.equals("egps2.plugin.fastmodtem.IndependentModuleLoader")) {
            return true;
        }

        // Check if this class extends FastBaseTemplate
        if (isFastBaseTemplateSubclass(clazz)) {
            // If it's from a JAR file (plugin or dependency), it's a real module - include it
            // If it's from classpath source code, it's a template example - exclude it
            boolean isFromJar = isClassFromPluginJar(clazz);

            if (isFromJar) {
                log.debug("Including FastBaseTemplate subclass from JAR: {}", className);
                return false; // Include it
            } else {
                log.debug("Excluding FastBaseTemplate subclass from source: {}", className);
                return true; // Exclude it
            }
        }

        return false;
    }

    /**
     * Checks if a class extends FastBaseTemplate.
     *
     * @param clazz Class to check
     * @return true if the class extends FastBaseTemplate, false otherwise
     */
    private boolean isFastBaseTemplateSubclass(Class<?> clazz) {
        try {
            Class<?> fastBaseTemplateClass = Class.forName("egps2.plugin.fastmodtem.FastBaseTemplate");
            return fastBaseTemplateClass.isAssignableFrom(clazz) && !fastBaseTemplateClass.equals(clazz);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Determines if a class was loaded from a JAR file (plugin or dependency).
     *
     * <p>This method checks the class's code source to determine if it originates
     * from a JAR file, as opposed to being compiled from source code.
     *
     * @param clazz Class to check
     * @return true if the class is from a JAR file, false otherwise
     */
    private boolean isClassFromPluginJar(Class<?> clazz) {
        try {
            // Get the location of the class
            java.security.ProtectionDomain protectionDomain = clazz.getProtectionDomain();
            java.security.CodeSource codeSource = protectionDomain.getCodeSource();

            if (codeSource == null) {
                return false;
            }

            URL location = codeSource.getLocation();
            if (location == null) {
                return false;
            }

            String locationPath = location.getPath();

            // Check if the location is a JAR file (ends with .jar)
            boolean isFromJar = locationPath.toLowerCase().endsWith(".jar");

            log.trace("Class {} location: {} (isFromJar: {})",
                     clazz.getName(), locationPath, isFromJar);

            return isFromJar;
        } catch (Exception e) {
            log.warn("Failed to determine class source for {}: {}", clazz.getName(), e.getMessage());
            return false;
        }
    }

    /**
     * Attempts to load a single module instance
     *
     * @param className Fully qualified class name
     * @return IModuleLoader instance
     * @throws Exception If loading fails
     */
    public IModuleLoader loadModuleInstance(String className) throws Exception {
        // First try to load from classpath (built-in modules)
        try {
            Class<?> clazz = Class.forName(className);
            if (IModuleLoader.class.isAssignableFrom(clazz)) {
                return (IModuleLoader) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (ClassNotFoundException e) {
            // Not found in classpath, try plugin JARs
            log.debug("Module {} not found in classpath, searching in plugins", className);
        }

        // Try to load from plugin JARs
        IModuleLoader pluginLoader = loadModuleFromPlugins(className);
        if (pluginLoader != null) {
            return pluginLoader;
        }

        throw new IllegalArgumentException(
            "Class " + className + " does not implement IModuleLoader or cannot be loaded");
    }

    /**
     * Attempts to load a module from plugin JARs
     *
     * @param className Fully qualified class name
     * @return IModuleLoader instance or null if not found
     */
    private IModuleLoader loadModuleFromPlugins(String className) {
        List<URL> pluginUrls = getPluginJarUrls();

        for (URL pluginUrl : pluginUrls) {
            try {
                // Create a custom class loader for this plugin
                egps2.plugin.manager.CustomURLClassLoader classLoader =
                        new egps2.plugin.manager.CustomURLClassLoader(
                                new URL[]{pluginUrl},
                                ClassLoader.getSystemClassLoader());

                Class<?> clazz = classLoader.loadClass(className);
                if (IModuleLoader.class.isAssignableFrom(clazz)) {
                    IModuleLoader loader = (IModuleLoader) clazz.getDeclaredConstructor().newInstance();
                    log.info("Successfully loaded plugin module {} from {}", className, pluginUrl);
                    // Note: We're not closing the classLoader here because the loaded class needs it
                    return loader;
                }
            } catch (ClassNotFoundException e) {
                // This plugin doesn't contain the class, try next one
                continue;
            } catch (Exception e) {
                log.warn("Failed to load module {} from plugin {}: {}", className, pluginUrl, e.getMessage());
            }
        }

        return null;
    }

    /**
     * Scans and returns a map of module class names to their instances
     *
     * @return Map of class name to IModuleLoader instance
     */
    public Map<String, IModuleLoader> scanAndLoadAllModules() {
        Map<String, IModuleLoader> modules = new LinkedHashMap<>();
        Set<String> moduleClasses = scanAllModuleClasses();

        for (String className : moduleClasses) {
            try {
                IModuleLoader loader = loadModuleInstance(className);
                modules.put(className, loader);
                log.debug("Successfully loaded module: {} v{}", className, loader.getVersion());
            } catch (Exception e) {
                log.warn("Failed to load module {}: {}", className, e.getMessage());
            }
        }

        log.info("Loaded {} out of {} discovered modules", modules.size(), moduleClasses.size());
        return modules;
    }
}
