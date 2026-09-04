package test.egps2.frame.features;

import egps2.frame.features.ModuleDiscoveryService;

import java.util.Set;

/**
 * Test for ModuleDiscoveryService to verify filtering logic.
 *
 * This test checks that the following classes are properly excluded:
 * - SubTab classes (extending DockableTabModuleFaceOfVoice)
 * - Core modules (already in Mainframe core menu)
 * - Template base classes (FastBaseTemplate and plugin template loaders)
 */
public class ModuleDiscoveryServiceTest {

    public static void main(String[] args) {
        System.out.println("=== Module Discovery Service Test ===");
        System.out.println("Testing exclusion logic...\n");

        // Create discovery service
        ModuleDiscoveryService service = new ModuleDiscoveryService();

        // Scan all modules
        Set<String> discoveredModules = service.scanAllModuleClasses();

        System.out.println("Total modules discovered: " + discoveredModules.size());
        System.out.println();

        // ===== Test 1: SubTab classes should be excluded =====
        String[] knownSubTabs = {
            "demo.dockable.SimpleExpressionProducer",
            "demo.dockable.SimpleAlignmentSimulator",
            "demo.dockable.LargeTextGeneratorSubTab",
            "demo.dockable.GroupwiseStatisticalTest",
            "demo.dockable.BiologicalPathwayEnrichment",
            "demo.dockable.AdvGroupwiseStatisticalTest"
        };

        System.out.println("Checking SubTab exclusion:");
        int excludedSubTabs = 0;
        for (String subTab : knownSubTabs) {
            boolean isExcluded = !discoveredModules.contains(subTab);
            String status = isExcluded ? "✓ EXCLUDED (correct)" : "✗ INCLUDED (error!)";
            System.out.println("  " + status + " - " + subTab);
            if (isExcluded) {
                excludedSubTabs++;
            }
        }
        System.out.println();

        // ===== Test 2: Core modules should be excluded =====
        String[] coreModules = {
            "egps2.builtin.modules.filemanager.IndependentModuleLoader",
            "egps2.builtin.modules.gallerymod.IndependentModuleLoader",
            "egps2.builtin.modules.lowtextedi.IndependentModuleLoader",
            "egps2.builtin.modules.largetextedi.IndependentModuleLoader",
            "demo.handytools.HandyToolExampleMain",
            "demo.dockable.IndependentModuleLoader",
            "demo.floating.IndependentModuleLoader",
            "egps2.builtin.modules.itoolmanager.IndependentModuleLoader"
        };

        System.out.println("Checking Core Module exclusion:");
        int excludedCoreModules = 0;
        for (String coreModule : coreModules) {
            boolean isExcluded = !discoveredModules.contains(coreModule);
            String status = isExcluded ? "✓ EXCLUDED (correct)" : "✗ INCLUDED (error!)";
            System.out.println("  " + status + " - " + coreModule);
            if (isExcluded) {
                excludedCoreModules++;
            }
        }
        System.out.println();

        // ===== Test 3: Template base classes should be excluded =====
        String[] templateBaseClasses = {
            "egps2.plugin.fastmodtem.FastBaseTemplate",
            "egps2.plugin.fastmodtem.IndependentModuleLoader"
        };

        System.out.println("Checking Template Base Class exclusion:");
        int excludedTemplates = 0;
        for (String template : templateBaseClasses) {
            boolean isExcluded = !discoveredModules.contains(template);
            String status = isExcluded ? "✓ EXCLUDED (correct)" : "✗ INCLUDED (error!)";
            System.out.println("  " + status + " - " + template);
            if (isExcluded) {
                excludedTemplates++;
            }
        }
        System.out.println();

        // ===== Summary =====
        System.out.println("Summary:");
        System.out.println("  SubTabs correctly excluded: " + excludedSubTabs + " / " + knownSubTabs.length);
        System.out.println("  Core modules correctly excluded: " + excludedCoreModules + " / " + coreModules.length);
        System.out.println("  Template classes correctly excluded: " + excludedTemplates + " / " + templateBaseClasses.length);
        System.out.println();

        int totalExpectedExclusions = knownSubTabs.length + coreModules.length + templateBaseClasses.length;
        int totalActualExclusions = excludedSubTabs + excludedCoreModules + excludedTemplates;

        if (totalActualExclusions == totalExpectedExclusions) {
            System.out.println("✓ Test PASSED: All exclusion rules working correctly!");
        } else {
            System.out.println("✗ Test FAILED: Some exclusion rules are not working!");
        }

        // Show a sample of discovered modules
        System.out.println("\nSample of discovered modules (first 10):");
        int count = 0;
        for (String module : discoveredModules) {
            System.out.println("  - " + module);
            if (++count >= 10) break;
        }
    }
}
