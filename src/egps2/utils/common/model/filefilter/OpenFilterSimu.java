package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * eGPS模拟器数据文件打开过滤器，用于eGPS模拟器生成数据文件的打开对话框。
 * eGPS simulator data file open filter for eGPS simulator generated data file open dialogs.
 *
 * <p>此过滤器用于打开.simu格式的eGPS模拟器数据文件。
 * This filter is used for opening .simu format eGPS simulator data files.
 *
 * <p><strong>支持的扩展名：</strong> .simu
 * Supported extensions: .simu
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterSimu extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".simu")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "eGPS simulator (*.simu)";
	}
}
