package egps2.builtin.modules.itoolmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks whether the current loading-status selections differ from the last saved snapshot.
 */
final class LoadingSelectionTracker {

    private List<Boolean> savedSnapshot;

    LoadingSelectionTracker(List<Boolean> initialSnapshot) {
        markSaved(initialSnapshot);
    }

    boolean isDirty(List<Boolean> currentSnapshot) {
        return !savedSnapshot.equals(copyOf(currentSnapshot));
    }

    void markSaved(List<Boolean> currentSnapshot) {
        savedSnapshot = copyOf(currentSnapshot);
    }

    private List<Boolean> copyOf(List<Boolean> snapshot) {
        return new ArrayList<>(snapshot);
    }
}
