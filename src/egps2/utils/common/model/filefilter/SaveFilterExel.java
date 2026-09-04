package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Excel电子表格文件保存过滤器，用于保存Excel格式数据文件的保存对话框。
 * Excel spreadsheet file save filter for saving Excel format data file save dialogs.
 *
 * <p>此过滤器实现单例模式，用于保存.xls格式的Excel电子表格文件（Office 97-2003格式）。
 * This filter implements singleton pattern for saving .xls format Excel spreadsheet files (Office 97-2003 format).
 *
 * <p><strong>支持的扩展名：</strong> .xls, .XLS
 * Supported extensions: .xls, .XLS
 *
 * @see javax.swing.filechooser.FileFilter
 * @author mhl
 * @since 2.1
 */
public class SaveFilterExel extends FileFilter {

	private static SaveFilterExel instance = null;

	public static SaveFilterExel getInstance() {
		if (instance == null) {
			instance = new SaveFilterExel();
		}
		return instance;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".xls") || s.endsWith(".XLS");
	}

	@Override
	public String getDescription() {
		return "Excel (*.xls)";
	}

}
