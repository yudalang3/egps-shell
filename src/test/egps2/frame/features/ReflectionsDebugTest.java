package egps2.frame.features;

import egps2.EGPSProperties;
import egps2.modulei.IModuleLoader;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Debug test to see exactly what Reflections finds in plugin JARs
 */
public class ReflectionsDebugTest {

    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(80));
        System.out.println("REFLECTIONS DEBUG TEST");
        System.out.println("=".repeat(80));
        System.out.println();

        // 1. Check plugin directory
        String pluginDirPath = EGPSProperties.PROPERTIES_DIR + "/plugin";
        File pluginDir = new File(pluginDirPath);
        System.out.println("Plugin directory: " + pluginDirPath);
        System.out.println("Exists: " + pluginDir.exists());
        System.out.println();

        if (!pluginDir.exists() || !pluginDir.isDirectory()) {
            System.out.println("Plugin directory does not exist!");
            return;
        }

        // 2. List plugin JARs
        File[] jars = pluginDir.listFiles((dir, name) -> name.endsWith(".jar"));
        System.out.println("Found " + (jars != null ? jars.length : 0) + " JAR files:");
        List<URL> pluginUrls = new ArrayList<>();

        if (jars != null) {
            for (File jar : jars) {
                URL url = jar.toURI().toURL();
                pluginUrls.add(url);
                System.out.println("  - " + jar.getName() + " => " + url);
            }
        }
        System.out.println();

        // 3. Test Reflections with plugin JAR only
        System.out.println("TEST 1: Scan plugin JAR only (no package filter)");
        System.out.println("-".repeat(80));

        ConfigurationBuilder config1 = new ConfigurationBuilder()
                .setScanners(Scanners.SubTypes)
                .setUrls(pluginUrls);  // Only scan plugin JARs, no package filter

        Reflections reflections1 = new Reflections(config1);
        Set<Class<? extends IModuleLoader>> subTypes1 = reflections1.getSubTypesOf(IModuleLoader.class);

        System.out.println("Found " + subTypes1.size() + " IModuleLoader subclasses:");
        for (Class<? extends IModuleLoader> clazz : subTypes1) {
            System.out.println("  - " + clazz.getName());
            System.out.println("    Abstract: " + java.lang.reflect.Modifier.isAbstract(clazz.getModifiers()));
            System.out.println("    Interface: " + clazz.isInterface());
            System.out.println("    Superclass: " + clazz.getSuperclass().getName());
        }
        System.out.println();

        // 4. Test with package filter
        System.out.println("TEST 2: Scan with package filter");
        System.out.println("-".repeat(80));

        ConfigurationBuilder config2 = new ConfigurationBuilder()
                .setScanners(Scanners.SubTypes)
                .forPackages("egps2", "demo", "test")  // Include 'test' package
                .addUrls(pluginUrls);

        Reflections reflections2 = new Reflections(config2);
        Set<Class<? extends IModuleLoader>> subTypes2 = reflections2.getSubTypesOf(IModuleLoader.class);

        System.out.println("Found " + subTypes2.size() + " IModuleLoader subclasses:");
        for (Class<? extends IModuleLoader> clazz : subTypes2) {
            System.out.println("  - " + clazz.getName());
        }
        System.out.println();

        // 5. Try to load the plugin class directly
        System.out.println("TEST 3: Direct class loading");
        System.out.println("-".repeat(80));

        try {
            egps2.plugin.manager.CustomURLClassLoader classLoader =
                    new egps2.plugin.manager.CustomURLClassLoader(
                            pluginUrls.toArray(new URL[0]),
                            ClassLoader.getSystemClassLoader());

            String pluginClassName = "test.plugin.example.TestPluginLoader";
            Class<?> pluginClass = classLoader.loadClass(pluginClassName);
            System.out.println("✓ Successfully loaded: " + pluginClass.getName());
            System.out.println("  Implements IModuleLoader: " + IModuleLoader.class.isAssignableFrom(pluginClass));

            if (IModuleLoader.class.isAssignableFrom(pluginClass)) {
                IModuleLoader instance = (IModuleLoader) pluginClass.getDeclaredConstructor().newInstance();
                System.out.println("  Tab Name: " + instance.getTabName());
                System.out.println("  Description: " + instance.getShortDescription());
            }
        } catch (Exception e) {
            System.out.println("✗ Failed to load plugin class:");
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("DEBUG TEST COMPLETE");
        System.out.println("=".repeat(80));
    }
}
