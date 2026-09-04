package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * CSV逗号分隔值文件打开过滤器，用于表格数据文件的打开对话框。
 * CSV comma-separated values file open filter for tabular data file open dialogs.
 *
 * <p>此过滤器用于打开CSV格式的数据文件，广泛应用于数据交换和统计分析。
 * This filter is used for opening CSV format data files, widely used in data exchange and statistical analysis.
 *
 * <p><strong>支持的扩展名：</strong> .csv
 * Supported extensions: .csv
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterCsv extends FileFilter{

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".csv")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Comma-separated values (*.csv)";
	}
}
