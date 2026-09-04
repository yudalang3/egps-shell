package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * ZIP压缩文件过滤器，用于压缩数据源的文件选择对话框。
 * ZIP compressed file filter for compressed source data file selection dialogs.
 *
 * <p>此过滤器接受.zip和.ZIP扩展名的文件以及所有目录。
 * This filter accepts files with .zip and .ZIP extensions and all directories.
 *
 * <p><strong>支持的扩展名：</strong> .zip, .ZIP
 * Supported extensions: .zip, .ZIP
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterZip extends FileFilter {
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".zip") || s.endsWith(".ZIP");
	}

	@Override
	public String getDescription() {
		return "Zipped source data (*.zip)";
	}

}
