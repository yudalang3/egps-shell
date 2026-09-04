package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * List列表文件打开过滤器，用于样本列表或数据列表文件的打开对话框。
 * List file open filter for sample list or data list file open dialogs.
 *
 * <p>此过滤器用于打开.list格式的列表文件，通常包含样本名称或文件路径列表。
 * This filter is used for opening .list format list files, typically containing sample names or file path lists.
 *
 * <p><strong>支持的扩展名：</strong> .list
 * Supported extensions: .list
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterList extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".list")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "List (*.list)";
	}
}
