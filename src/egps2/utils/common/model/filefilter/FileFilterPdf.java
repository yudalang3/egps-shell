package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * PDF文档文件过滤器，用于Swing JFileChooser文件选择对话框。
 * PDF document file filter for Swing JFileChooser file selection dialogs.
 *
 * <p>此过滤器接受.pdf和.PDF扩展名的文件以及所有目录。
 * This filter accepts files with .pdf and .PDF extensions and all directories.
 *
 * <p><strong>支持的扩展名：</strong> .pdf, .PDF
 * Supported extensions: .pdf, .PDF
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterPdf extends FileFilter {
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".pdf") || s.endsWith(".PDF");
	}

	@Override
	public String getDescription() {
		return "Vector graphic: PDF (*.pdf)";
	}

}
