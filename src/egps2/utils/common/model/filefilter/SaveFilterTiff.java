package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * TIFF位图图像文件保存过滤器，用于保存TIFF格式图像的保存对话框。
 * TIFF bitmap image file save filter for saving TIFF format image save dialogs.
 *
 * <p>此过滤器实现单例模式，用于保存.tif格式的高质量位图图像文件，适用于科学图像和出版物。
 * This filter implements singleton pattern for saving .tif format high-quality bitmap image files, suitable for scientific images and publications.
 *
 * <p><strong>支持的扩展名：</strong> .tif, .TIF
 * Supported extensions: .tif, .TIF
 *
 * @see javax.swing.filechooser.FileFilter
 * @author mhl
 * @since 2.1
 */
public class SaveFilterTiff extends FileFilter {

	private static SaveFilterTiff instance = null;

	public static SaveFilterTiff getInstance() {
		if (instance == null) {
			instance = new SaveFilterTiff();
		}
		return instance;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".tif") || s.endsWith(".TIF");
	}

	@Override
	public String getDescription() {
		return "TIFF (*.tif)";
	}

}
