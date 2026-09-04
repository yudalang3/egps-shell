package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * eGPS模拟器数据文件保存过滤器，用于保存eGPS模拟器生成数据文件的保存对话框。
 * eGPS simulator data file save filter for saving eGPS simulator generated data file save dialogs.
 *
 * <p>此过滤器实现单例模式，用于保存.simu格式的eGPS模拟器数据文件。
 * This filter implements singleton pattern for saving .simu format eGPS simulator data files.
 *
 * <p><strong>支持的扩展名：</strong> .simu, .SIMU
 * Supported extensions: .simu, .SIMU
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class SaveFilterSimu extends FileFilter {
	
	private static SaveFilterSimu instance = null;
	
	public static SaveFilterSimu getInstance() {
		if (instance == null) {
			instance = new SaveFilterSimu();
		}
		return instance;
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".simu") || s.endsWith(".SIMU");
	}

	@Override
	public String getDescription() {
		return "SIMU (*.simu)";
	}

}
