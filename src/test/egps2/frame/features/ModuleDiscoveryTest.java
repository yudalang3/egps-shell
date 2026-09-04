package egps2.frame.features;

import egps2.builtin.modules.itoolmanager.IModuleElement;
import egps2.builtin.modules.itoolmanager.ModuleStatus;
import egps2.modulei.IModuleLoader;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Test class for module discovery functionality.
 *
 * <p>This test class verifies the module discovery system without requiring GUI startup.
 * It tests:
 * <ul>
 *   <li>Reflections-based module scanning</li>
 *   <li>Configuration file reading</li>
 *   <li>Smart merge of scanned and configured modules</li>
 *   <li>Handling of unavailable and newly discovered modules</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * java -cp "./out/production/egps-main.gui:dependency-egps/*" egps2.frame.features.ModuleDiscoveryTest
 * </pre>
 *
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ModuleDiscoveryTest {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("Module Discovery System Test");
        System.out.println("==============================================\n");

        // Get config file path from command line or use default
        String configPath = args.length > 0 ? args[0] :
            "docs/all_egps2.loading.module.config.txt";

        System.out.println("Config file: " + configPath);
        System.out.println();

        // Test 1: Scan all modules
        System.out.println("--- Test 1: Module Scanning ---");
        testModuleScanning();
        System.out.println();

        // Test 2: Read config file
        System.out.println("--- Test 2: Config File Reading ---");
        testConfigReading(configPath);
        System.out.println();

        // Test 3: Smart loading with discovery
        System.out.println("--- Test 3: Smart Loading with Discovery ---");
        testSmartLoading(configPath);
        System.out.println();

        System.out.println("==============================================");
        System.out.println("All tests completed!");
        System.out.println("==============================================");
    }

    /**
     * Test module scanning functionality
     */
    private static void testModuleScanning() {
        ModuleDiscoveryService service = new ModuleDiscoveryService();

        System.out.println("Scanning entire JVM classpath for modules...");

        long startTime = System.currentTimeMillis();
        Map<String, IModuleLoader> modules = service.scanAndLoadAllModules();
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Scan duration: " + duration + " ms");
        System.out.println("Total modules discovered: " + modules.size());
        System.out.println();

        // Group by package
        Map<String, Long> packageCount = modules.keySet().stream()
            .collect(Collectors.groupingBy(
                className -> {
                    int lastDot = className.lastIndexOf('.');
                    return lastDot > 0 ? className.substring(0, lastDot) : className;
                },
                Collectors.counting()
            ));

        System.out.println("Modules by package:");
        packageCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(entry ->
                System.out.printf("  %-50s : %3d modules%n", entry.getKey(), entry.getValue())
            );

        // Show first 10 modules
        System.out.println("\nFirst 10 discovered modules:");
        modules.keySet().stream()
            .limit(10)
            .forEach(className -> System.out.println("  - " + className));
    }

    /**
     * Test config file reading
     */
    private static void testConfigReading(String configPath) {
        EGPS2ServiceLoader<IModuleLoader> loader =
            new EGPS2ServiceLoader<>(IModuleLoader.class);

        List<IModuleLoader> modules = loader.load(configPath);

        System.out.println("Modules loaded from config: " + modules.size());
        System.out.println("Total providers tracked: " + loader.getAllProviders().size());

        // Show statistics by status
        Map<ModuleStatus, Long> statusCount = loader.getAllProviders().stream()
            .collect(Collectors.groupingBy(IModuleElement::getStatus, Collectors.counting()));

        System.out.println("\nModules by status:");
        statusCount.forEach((status, count) ->
            System.out.printf("  %-25s : %3d modules%n", status.getDisplayName(), count)
        );
    }

    /**
     * Test smart loading with discovery
     */
    private static void testSmartLoading(String configPath) {
        ModuleDiscoveryService discoveryService = new ModuleDiscoveryService();
        EGPS2ServiceLoader<IModuleLoader> loader =
            new EGPS2ServiceLoader<>(IModuleLoader.class);

        long startTime = System.currentTimeMillis();
        List<IModuleLoader> loadedModules =
            loader.loadWithDiscovery(configPath, discoveryService);
        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Smart loading duration: " + duration + " ms");
        System.out.println("Modules loaded: " + loadedModules.size());
        System.out.println("Total providers tracked: " + loader.getAllProviders().size());
        System.out.println();

        // Detailed statistics
        Map<ModuleStatus, Long> statusCount = loader.getAllProviders().stream()
            .collect(Collectors.groupingBy(IModuleElement::getStatus, Collectors.counting()));

        System.out.println("Detailed module statistics:");
        for (ModuleStatus status : ModuleStatus.values()) {
            long count = statusCount.getOrDefault(status, 0L);
            System.out.printf("  %-25s : %3d modules%n", status.getDisplayName(), count);
        }

        // Show newly discovered modules
        List<IModuleElement> newModules = loader.getAllProviders().stream()
            .filter(e -> e.getStatus() == ModuleStatus.NEWLY_DISCOVERED)
            .collect(Collectors.toList());

        if (!newModules.isEmpty()) {
            System.out.println("\nNewly discovered modules (not in config):");
            newModules.forEach(element ->
                System.out.println("  - " + element.getClassName())
            );
        }

        // Show unavailable modules
        List<IModuleElement> unavailableModules = loader.getAllProviders().stream()
            .filter(e -> e.getStatus() == ModuleStatus.UNAVAILABLE)
            .collect(Collectors.toList());

        if (!unavailableModules.isEmpty()) {
            System.out.println("\nUnavailable modules (in config but not found):");
            unavailableModules.forEach(element ->
                System.out.printf("  - %s%n    Error: %s%n",
                    element.getClassName(), element.getErrorMessage())
            );
        }

        // Verify configuration coverage
        long configuredModules = loader.getAllProviders().stream()
            .filter(e -> e.getStatus() == ModuleStatus.AVAILABLE ||
                        e.getStatus() == ModuleStatus.AVAILABLE_NOT_LOADED ||
                        e.getStatus() == ModuleStatus.UNAVAILABLE)
            .count();

        System.out.println("\nConfiguration coverage:");
        System.out.printf("  Configured modules: %d%n", configuredModules);
        System.out.printf("  Newly discovered: %d%n", newModules.size());
        System.out.printf("  Total known modules: %d%n", loader.getAllProviders().size());

        double coverage = configuredModules * 100.0 / loader.getAllProviders().size();
        System.out.printf("  Config coverage: %.1f%%%n", coverage);
    }
}
