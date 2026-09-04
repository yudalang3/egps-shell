package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * PNG位图图像文件过滤器，用于Swing JFileChooser文件选择对话框。
 * PNG bitmap image file filter for Swing JFileChooser file selection dialogs.
 *
 * <p>此过滤器接受.png和.PNG扩展名的文件以及所有目录。
 * This filter accepts files with .png and .PNG extensions and all directories.
 *
 * <p><strong>支持的扩展名：</strong> .png, .PNG
 * Supported extensions: .png, .PNG
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterPng extends FileFilter {
	
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".png") || s.endsWith(".PNG");
	}

	@Override
	public String getDescription() {
		return "Bitmap image: PNG (*.png)";
	}

}
