package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 距离矩阵文件保存过滤器，用于保存遗传距离或进化距离矩阵的保存对话框。
 * Distance matrix file save filter for saving genetic distance or evolutionary distance matrix save dialogs.
 *
 * <p>此过滤器实现单例模式，用于保存.dist格式的距离矩阵文件，常用于系统发育分析。
 * This filter implements singleton pattern for saving .dist format distance matrix files, commonly used in phylogenetic analysis.
 *
 * <p><strong>支持的扩展名：</strong> .dist, .DIST
 * Supported extensions: .dist, .DIST
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class SaveFilterDist extends FileFilter {
	
	private static SaveFilterDist instance = null;
	
	public static SaveFilterDist getInstance() {
		if (instance == null) {
			instance = new SaveFilterDist();
		}
		return instance;
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".dist") || s.endsWith(".DIST");
	}

	@Override
	public String getDescription() {
		return "DIST (*.dist)";
	}

}
