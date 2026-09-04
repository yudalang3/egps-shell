package egps2.builtin.modules.filemanager;

import java.nio.file.Path;
import java.util.Optional;

/**
 * DirNavigatorPanelElement belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class DirNavigatorPanelElement {

    final private Path currDirPath;
    final private Optional<Path> selectedPathOpt;

    public DirNavigatorPanelElement(Path currDirPath, Optional<Path> selectedPathOpt)
    {
        this.currDirPath = currDirPath;
        this.selectedPathOpt = selectedPathOpt;
    }

    public Path getCurrDirPath() {
        return currDirPath;
    }

    public Optional<Path> getSelectedPathOpt() {
        return selectedPathOpt;
    }
}
