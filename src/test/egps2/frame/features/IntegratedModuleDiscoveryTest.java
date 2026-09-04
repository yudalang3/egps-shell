package egps2.frame.features;

import egps2.modulei.IModuleLoader;
import java.util.Map;
import java.util.Set;

/**
 * Test for integrated module discovery (built-in modules + plugins).
 *
 * <p>This test demonstrates that the ModuleDiscoveryService now scans both:
 * <ul>
 *   <li>Built-in modules from the classpath (packages: egps2, demo, module, operator, primary)</li>
 *   <li>External plugin JARs from ~/.egps2/config/plugin directory</li>
 * </ul>
 *
 * <p>All discovered modules (both built-in and plugins) will be displayed in the Module Gallery
 * and can be accessed equally through the UI.
 *
 * <p><strong>Usage:</strong>
 * <pre>
 * # Compile the test
 * javac -d ./out/production/egps-main.gui -cp "dependency-egps/*:./out/production/egps-main.gui" \
 *       src/test/egps2/frame/features/IntegratedModuleDiscoveryTest.java
 *
 * # Run the test
 * java -cp "./out/production/egps-main.gui:dependency-egps/*" \
 *      egps2.frame.features.IntegratedModuleDiscoveryTest
 * </pre>
 *
 * @author eGPS Dev Team
 * @since 2.1
 */
public class IntegratedModuleDiscoveryTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("INTEGRATED MODULE DISCOVERY TEST (Built-in + Plugins)");
        System.out.println("=".repeat(80));
        System.out.println();

        // Initialize the discovery service
        ModuleDiscoveryService service = new ModuleDiscoveryService();

        // Test 1: Scan for all module classes (built-in + plugins)
        System.out.println("TEST 1: Scanning for all IModuleLoader implementations");
        System.out.println("-".repeat(80));
        Set<String> allModules = service.scanAllModuleClasses();

        System.out.println("Total modules discovered: " + allModules.size());
        System.out.println();

        // Categorize and display modules
        System.out.println("Discovered modules:");
        System.out.println("-".repeat(80));

        int builtinCount = 0;
        int pluginCount = 0;

        for (String className : allModules) {
            boolean isPlugin = isLikelyPlugin(className);
            String type = isPlugin ? "[PLUGIN]" : "[BUILTIN]";

            if (isPlugin) {
                pluginCount++;
            } else {
                builtinCount++;
            }

            System.out.printf("%-12s %s%n", type, className);
        }

        System.out.println();
        System.out.println("Summary:");
        System.out.println("  Built-in modules: " + builtinCount);
        System.out.println("  Plugin modules:   " + pluginCount);
        System.out.println("  Total:            " + allModules.size());
        System.out.println();

        // Test 2: Load and display module information
        System.out.println("TEST 2: Loading sample modules");
        System.out.println("-".repeat(80));

        int loadedCount = 0;
        int failedCount = 0;

        for (String className : allModules) {
            try {
                IModuleLoader loader = service.loadModuleInstance(className);
                String tabName = loader.getTabName();
                String description = loader.getShortDescription();

                System.out.printf("✓ Loaded: %s%n", className);
                System.out.printf("  Tab Name: %s%n", tabName);
                System.out.printf("  Description: %s%n", description != null ? description : "(none)");
                System.out.println();

                loadedCount++;

                // Only show first 5 to avoid too much output
                if (loadedCount >= 5) {
                    System.out.println("... (showing first 5 modules)");
                    break;
                }
            } catch (Exception e) {
                failedCount++;
                System.out.printf("✗ Failed to load: %s%n", className);
                System.out.printf("  Error: %s%n", e.getMessage());
                System.out.println();
            }
        }

        System.out.println("Loading summary:");
        System.out.println("  Successfully loaded: " + loadedCount);
        System.out.println("  Failed to load:      " + failedCount);
        System.out.println();

        // Test 3: Verify plugin directory
        System.out.println("TEST 3: Plugin directory status");
        System.out.println("-".repeat(80));

        String pluginDir = egps2.EGPSProperties.PROPERTIES_DIR + "/plugin";
        java.io.File dir = new java.io.File(pluginDir);

        System.out.println("Plugin directory: " + pluginDir);
        System.out.println("Exists: " + dir.exists());
        System.out.println("Is directory: " + dir.isDirectory());

        if (dir.exists() && dir.isDirectory()) {
            java.io.File[] jars = dir.listFiles((d, name) -> name.endsWith(".jar"));
            System.out.println("JAR files found: " + (jars != null ? jars.length : 0));

            if (jars != null && jars.length > 0) {
                System.out.println("\nPlugin JARs:");
                for (java.io.File jar : jars) {
                    System.out.println("  - " + jar.getName());
                }
            } else {
                System.out.println("\nNo plugin JARs found. To test plugin discovery:");
                System.out.println("  1. Create a plugin JAR with an IModuleLoader implementation");
                System.out.println("  2. Place it in: " + pluginDir);
                System.out.println("  3. Ensure it has eGPS2.plugin.properties in the JAR root");
                System.out.println("  4. Run this test again");
            }
        }

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("TEST COMPLETE");
        System.out.println("=".repeat(80));
        System.out.println();
        System.out.println("IMPORTANT NOTES:");
        System.out.println("  - All discovered modules will appear in the Module Gallery");
        System.out.println("  - Plugins have equal status with built-in modules");
        System.out.println("  - Module Gallery entry: Menu -> Module gallery (Ctrl+2)");
        System.out.println("  - Plugins can be accessed both from menu and Module Gallery");
    }

    /**
     * Heuristic to determine if a module is likely from a plugin
     * (not in standard eGPS packages)
     */
    private static boolean isLikelyPlugin(String className) {
        return !className.startsWith("egps2.") &&
               !className.startsWith("demo.") &&
               !className.startsWith("egps.");
    }
}
