package egps2.builtin.modules.itoolmanager;

import java.util.List;

/**
 * Applies saved loading-status selections back into the cached provider list.
 */
final class ModuleLoadingStateSynchronizer {

    private ModuleLoadingStateSynchronizer() {
    }

    static void applySelections(List<String> loadingSelections, List<IModuleElement> providers) {
        int selectionIndex = 0;

        for (IModuleElement provider : providers) {
            if (provider.getLoader() == null) {
                continue;
            }
            if (selectionIndex >= loadingSelections.size()) {
                return;
            }

            provider.setLoad(Boolean.parseBoolean(loadingSelections.get(selectionIndex)));
            selectionIndex++;
        }
    }
}
