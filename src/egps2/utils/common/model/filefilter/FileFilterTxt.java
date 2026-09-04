package egps2.utils.common.model.filefilter;

import egps2.utils.common.util.SaveFileFilter;

import java.io.File;

/**
 * 文本文件过滤器，用于TXT文件的保存对话框。
 * Text file filter for TXT file save dialogs.
 *
 * <p>此过滤器继承自{@link egps2.utils.common.util.SaveFileFilter}，提供文件后缀自动添加功能。
 * This filter extends {@link egps2.utils.common.util.SaveFileFilter}, providing automatic file suffix addition.
 *
 * <p><strong>支持的扩展名：</strong> .txt, .TXT
 * Supported extensions: .txt, .TXT
 *
 * @see egps2.utils.common.util.SaveFileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterTxt extends SaveFileFilter {
	
	public FileFilterTxt() {
		
	}
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".txt") || s.endsWith(".TXT");
	}

	@Override
	public String getDescription() {
		return "TXT (*.txt)";
	}

	@Override
	public String getFileSuffix() {
		return "txt";
	}
}
