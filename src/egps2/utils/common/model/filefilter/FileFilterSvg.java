package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * SVG矢量图形文件过滤器，用于Swing JFileChooser文件选择对话框。
 * SVG vector graphic file filter for Swing JFileChooser file selection dialogs.
 *
 * <p>此过滤器接受.svg和.SVG扩展名的文件以及所有目录。
 * This filter accepts files with .svg and .SVG extensions and all directories.
 *
 * <p><strong>支持的扩展名：</strong> .svg, .SVG
 * Supported extensions: .svg, .SVG
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterSvg extends FileFilter {
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".svg") || s.endsWith(".SVG");
	}

	@Override
	public String getDescription() {
		return "Vector graphic: SVG (*.svg)";
	}

}
