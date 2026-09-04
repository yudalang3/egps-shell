package egps2.utils.common.model.filefilter;

import egps2.utils.common.util.SaveFileFilter;

import java.io.File;

/**
 * PowerPoint演示文稿文件过滤器，用于保存单页幻灯片的文件选择对话框。
 * PowerPoint presentation file filter for saving single slide file selection dialogs.
 *
 * <p>此过滤器继承自{@link egps2.utils.common.util.SaveFileFilter}，提供文件后缀自动添加功能。
 * This filter extends {@link egps2.utils.common.util.SaveFileFilter}, providing automatic file suffix addition.
 *
 * <p><strong>支持的扩展名：</strong> .pptx, .PPTX
 * Supported extensions: .pptx, .PPTX
 *
 * <p><strong>用途：</strong>保存单页PowerPoint幻灯片（Office 2007+格式）。
 * Usage: Save single PowerPoint slides (Office 2007+ format).
 *
 * @see egps2.utils.common.util.SaveFileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterPPTX extends SaveFileFilter {
	
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String s = f.getName();
		return s.endsWith(".pptx") || s.endsWith(".PPTX");
	}

	@Override
	public String getDescription() {
		return "Single powerPoint slides: PPTX (*.pptx)";
	}

	@Override
	public String getFileSuffix() {
		return "pptx";
	}
}
