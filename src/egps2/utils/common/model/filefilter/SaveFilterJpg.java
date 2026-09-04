package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * JPEG位图图像文件保存过滤器，用于保存JPEG格式图像的保存对话框。
 * JPEG bitmap image file save filter for saving JPEG format image save dialogs.
 *
 * <p>此过滤器用于保存.jpg格式的位图图像文件，适用于照片和图表导出。
 * This filter is used for saving .jpg format bitmap image files, suitable for photo and chart export.
 *
 * <p><strong>支持的扩展名：</strong> .jpg, .JPG
 * Supported extensions: .jpg, .JPG
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class SaveFilterJpg extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".jpg") || s.endsWith(".JPG");
	}

	@Override
	public String getDescription() {
		return "Bitmap image: JPEG (*.jpg)";
	}

}
