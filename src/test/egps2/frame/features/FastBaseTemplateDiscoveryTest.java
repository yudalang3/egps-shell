package egps2.frame.features;

import egps2.modulei.IModuleLoader;
import egps2.plugin.fastmodtem.FastBaseTemplate;

import java.io.File;
import java.util.Set;

/**
 * Test to verify FastBaseTemplate subclass discovery rules.
 *
 * <p><strong>FastBaseTemplate Discovery Rules:</strong>
 * <ul>
 *   <li>FastBaseTemplate itself: ALWAYS excluded</li>
 *   <li>FastBaseTemplate subclasses from plugin JARs: INCLUDED (actual plugins)</li>
 *   <li>FastBaseTemplate subclasses from shell/classpath: EXCLUDED (templates)</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * <pre>
 * # Compile the test
 * javac -d ./out/production/egps-main.gui -cp "dependency-egps/*:./out/production/egps-main.gui" \
 *       src/test/egps2/frame/features/FastBaseTemplateDiscoveryTest.java
 *
 * # Run the test
 * java -cp "./out/production/egps-main.gui:dependency-egps/*" \
 *      egps2.frame.features.FastBaseTemplateDiscoveryTest
 * </pre>
 *
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FastBaseTemplateDiscoveryTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("FASTBASETEMPLATE DISCOVERY RULE TEST");
        System.out.println("=".repeat(80));
        System.out.println();

        // Test 1: Verify FastBaseTemplate itself is excluded
        System.out.println("TEST 1: Verify FastBaseTemplate exclusion");
        System.out.println("-".repeat(80));

        ModuleDiscoveryService service = new ModuleDiscoveryService();
        Set<String> allModules = service.scanAllModuleClasses();

        boolean fastBaseTemplateFound = allModules.contains("egps2.plugin.fastmodtem.FastBaseTemplate");
        boolean independentLoaderFound = allModules.contains("egps2.plugin.fastmodtem.IndependentModuleLoader");

        System.out.println("FastBaseTemplate found: " + fastBaseTemplateFound +
                         (fastBaseTemplateFound ? " ❌ FAIL (should be excluded)" : " ✅ PASS"));
        System.out.println("IndependentModuleLoader found: " + independentLoaderFound +
                         (independentLoaderFound ? " ❌ FAIL (should be excluded)" : " ✅ PASS"));
        System.out.println();

        // Test 2: Check for FastBaseTemplate subclasses
        System.out.println("TEST 2: Check FastBaseTemplate subclasses");
        System.out.println("-".repeat(80));

        int shellSubclassCount = 0;
        int pluginSubclassCount = 0;

        System.out.println("Scanning all discovered modules...");
        for (String className : allModules) {
            try {
                Class<?> clazz = Class.forName(className);
                if (isSubclassOf(clazz, FastBaseTemplate.class) && !clazz.equals(FastBaseTemplate.class)) {
                    boolean isFromPlugin = isFromPluginLocation(clazz);

                    if (isFromPlugin) {
                        pluginSubclassCount++;
                        System.out.println("✅ [PLUGIN] " + className + " - Correctly INCLUDED");
                    } else {
                        shellSubclassCount++;
                        System.out.println("❌ [SHELL]  " + className + " - Should be EXCLUDED!");
                    }
                }
            } catch (ClassNotFoundException e) {
                // Skip
            }
        }

        System.out.println();
        System.out.println("Summary:");
        System.out.println("  FastBaseTemplate subclasses from plugins: " + pluginSubclassCount + " (included)");
        System.out.println("  FastBaseTemplate subclasses from shell:   " + shellSubclassCount + " (should be 0)");
        System.out.println();

        // Test 3: Plugin directory status
        System.out.println("TEST 3: Plugin directory status");
        System.out.println("-".repeat(80));

        String pluginDir = egps2.EGPSProperties.PROPERTIES_DIR + "/plugin";
        File dir = new File(pluginDir);

        System.out.println("Plugin directory: " + pluginDir);
        System.out.println("Exists: " + dir.exists());

        if (dir.exists() && dir.isDirectory()) {
            File[] jars = dir.listFiles((d, name) -> name.endsWith(".jar"));
            System.out.println("JAR files: " + (jars != null ? jars.length : 0));

            if (jars != null && jars.length > 0) {
                System.out.println("\nPlugin JARs:");
                for (File jar : jars) {
                    System.out.println("  - " + jar.getName());
                }
                System.out.println("\nIf these JARs contain FastBaseTemplate subclasses,");
                System.out.println("they WILL be discovered and shown in Module Gallery.");
            } else {
                System.out.println("\n⚠ No plugin JARs found.");
                System.out.println("\nTo test FastBaseTemplate plugin discovery:");
                System.out.println("  1. Create a class extending FastBaseTemplate");
                System.out.println("  2. Compile and package it as a plugin JAR");
                System.out.println("  3. Place JAR in: " + pluginDir);
                System.out.println("  4. Run this test again");
                System.out.println("\nExample plugin class:");
                System.out.println("  public class MyPlugin extends FastBaseTemplate {");
                System.out.println("      public String getTabName() { return \"My Plugin\"; }");
                System.out.println("      // ... implement other methods");
                System.out.println("  }");
            }
        } else {
            System.out.println("Plugin directory does not exist. Create it with:");
            System.out.println("  mkdir -p " + pluginDir);
        }

        System.out.println();
        System.out.println("=".repeat(80));
        System.out.println("TEST COMPLETE");
        System.out.println("=".repeat(80));
        System.out.println();

        // Overall result
        boolean allTestsPassed = !fastBaseTemplateFound &&
                                !independentLoaderFound &&
                                shellSubclassCount == 0;

        if (allTestsPassed) {
            System.out.println("✅ ALL TESTS PASSED!");
            System.out.println();
            System.out.println("Discovery rules are working correctly:");
            System.out.println("  ✓ FastBaseTemplate itself is excluded");
            System.out.println("  ✓ Template classes are excluded from shell");
            System.out.println("  ✓ Plugin FastBaseTemplate subclasses will be included");
        } else {
            System.out.println("❌ SOME TESTS FAILED");
            System.out.println();
            System.out.println("Please review the test output above.");
        }
    }

    /**
     * Check if a class is a subclass of another class
     */
    private static boolean isSubclassOf(Class<?> subclass, Class<?> superclass) {
        return superclass.isAssignableFrom(subclass) && !subclass.equals(superclass);
    }

    /**
     * Check if a class is from plugin location
     */
    private static boolean isFromPluginLocation(Class<?> clazz) {
        try {
            java.security.CodeSource codeSource = clazz.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                return false;
            }

            java.net.URL location = codeSource.getLocation();
            if (location == null) {
                return false;
            }

            String path = location.getPath();
            return path.contains("/plugin/") || path.contains("\\plugin\\");
        } catch (Exception e) {
            return false;
        }
    }
}
