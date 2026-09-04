package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 蛋白质组数据文件打开过滤器，用于蛋白质组学数据文件的打开对话框。
 * Proteomic data file open filter for proteomic data file open dialogs.
 *
 * <p>此过滤器用于打开.pro格式的蛋白质组学数据文件。
 * This filter is used for opening .pro format proteomic data files.
 *
 * <p><strong>支持的扩展名：</strong> .pro
 * Supported extensions: .pro
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterPro extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".pro")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Proteomic data (*.pro)";
	}
}
