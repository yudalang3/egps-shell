package egps2.builtin.modules.itoolmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Regression test for synchronizing saved loading selections back into cached providers.
 */
public class ModuleLoadingStateSynchronizerTest {

    public static void main(String[] args) {
        testApplySelectionsSkipsUnavailableProviders();

        System.out.println("ModuleLoadingStateSynchronizerTest PASSED");
    }

    private static void testApplySelectionsSkipsUnavailableProviders() {
        List<IModuleElement> providers = new ArrayList<>();
        providers.add(new IModuleElement(new IndependentModuleLoader(), true, ModuleStatus.AVAILABLE));
        providers.add(new IModuleElement("missing.Plugin", false, "Class not found in classpath"));
        providers.add(new IModuleElement(new IndependentModuleLoader(), false, ModuleStatus.AVAILABLE_NOT_LOADED));

        ModuleLoadingStateSynchronizer.applySelections(List.of("false", "true"), providers);

        assertFalse(providers.get(0).isLoad(),
                "First available provider should receive the first saved selection");
        assertFalse(providers.get(1).isLoad(),
                "Unavailable providers should keep their original cached load flag");
        assertTrue(providers.get(2).isLoad(),
                "Second available provider should receive the second saved selection");
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }
}
