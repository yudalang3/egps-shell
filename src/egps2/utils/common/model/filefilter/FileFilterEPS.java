package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * EPS矢量图形文件过滤器，用于Swing JFileChooser文件选择对话框。
 * EPS vector graphic file filter for Swing JFileChooser file selection dialogs.
 *
 * <p>此过滤器接受.eps和.EPS扩展名的文件以及所有目录。
 * This filter accepts files with .eps and .EPS extensions and all directories.
 *
 * <p><strong>支持的扩展名：</strong> .eps, .EPS
 * Supported extensions: .eps, .EPS
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterEPS extends FileFilter {
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".eps") || s.endsWith(".EPS");
	}

	@Override
	public String getDescription() {
		return "Vector graphic: EPS (*.eps)";
	}

}
