package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * eGPS增强型树文件保存过滤器，用于保存eGPS特有的树格式文件的保存对话框。
 * eGPS enhanced tree file save filter for saving eGPS-specific tree format file save dialogs.
 *
 * <p>此过滤器实现单例模式，用于保存.etree格式的增强型树文件，包含额外的可视化和注释信息。
 * This filter implements singleton pattern for saving .etree format enhanced tree files containing additional visualization and annotation information.
 *
 * <p><strong>支持的扩展名：</strong> .etree, .ETREE
 * Supported extensions: .etree, .ETREE
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class SaveFilterItreeV extends FileFilter {
	
	private static SaveFilterItreeV instance = null;
	
	public static SaveFilterItreeV getInstance() {
		if (instance == null) {
			instance = new SaveFilterItreeV();
		}
		return instance;
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".etree") || s.endsWith(".ETREE");
	}

	@Override
	public String getDescription() {
		return "ETREE (*.etree)";
	}

}
