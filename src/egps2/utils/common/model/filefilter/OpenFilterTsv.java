package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * TSV制表符分隔值文件打开过滤器，用于表格数据文件的打开对话框。
 * TSV tab-separated values file open filter for tabular data file open dialogs.
 *
 * <p>此过滤器用于打开TSV格式的数据文件，广泛应用于数据交换和生物信息学数据存储。
 * This filter is used for opening TSV format data files, widely used in data exchange and bioinformatics data storage.
 *
 * <p><strong>支持的扩展名：</strong> .tsv
 * Supported extensions: .tsv
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterTsv extends FileFilter{

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".tsv")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Tab-separated values (*.tsv)";
	}
}
