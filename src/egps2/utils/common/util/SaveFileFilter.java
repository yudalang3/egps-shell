package egps2.utils.common.util;

import javax.swing.filechooser.FileFilter;

/**
 * SaveFileFilter provides shared utility logic for eGPS modules and UI.
 */
public abstract class SaveFileFilter extends FileFilter {
    public abstract String getFileSuffix();
}
