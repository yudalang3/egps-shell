package egps2.builtin.modules.itoolmanager;

import java.util.Arrays;
import java.util.List;

/**
 * Regression test for tracking unsaved loading-status edits in ITools Manager.
 */
public class LoadingSelectionTrackerTest {

    public static void main(String[] args) {
        testBaselineIsClean();
        testDetectsRealChangesAndResetAfterSave();
        testRevertingBackToBaselineClearsDirtyState();

        System.out.println("LoadingSelectionTrackerTest PASSED");
    }

    private static void testBaselineIsClean() {
        LoadingSelectionTracker tracker = new LoadingSelectionTracker(bools(true, false, true));

        assertFalse(tracker.isDirty(bools(true, false, true)),
                "Fresh baseline should not be dirty");
    }

    private static void testDetectsRealChangesAndResetAfterSave() {
        LoadingSelectionTracker tracker = new LoadingSelectionTracker(bools(true, false, true));

        assertTrue(tracker.isDirty(bools(false, false, true)),
                "Changing any loading flag should mark the table dirty");

        tracker.markSaved(bools(false, false, true));

        assertFalse(tracker.isDirty(bools(false, false, true)),
                "markSaved should reset the dirty baseline");
    }

    private static void testRevertingBackToBaselineClearsDirtyState() {
        LoadingSelectionTracker tracker = new LoadingSelectionTracker(bools(true, false, true));

        assertTrue(tracker.isDirty(bools(true, true, true)),
                "Intermediate edits should mark dirty");
        assertFalse(tracker.isDirty(bools(true, false, true)),
                "Returning to the baseline should clear the dirty flag");
        assertTrue(tracker.isDirty(bools(true, false)),
                "Size changes should also be treated as dirty");
    }

    private static List<Boolean> bools(Boolean... values) {
        return Arrays.asList(values);
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
