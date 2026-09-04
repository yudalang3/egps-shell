package egps2.utils.common.model.filefilter;

import egps2.utils.common.util.SaveFileFilter;

import java.io.File;

/**
 * Newick系统发育树文件保存过滤器，用于保存树结构数据的保存对话框。
 * Newick phylogenetic tree file save filter for saving tree structure data save dialogs.
 *
 * <p>此过滤器实现单例模式并继承自{@link egps2.utils.common.util.SaveFileFilter}，提供文件后缀自动添加功能。
 * This filter implements singleton pattern and extends {@link egps2.utils.common.util.SaveFileFilter}, providing automatic file suffix addition.
 *
 * <p><strong>支持的扩展名：</strong> .nwk, .NWK
 * Supported extensions: .nwk, .NWK
 *
 * <p><strong>Newick格式：</strong>标准的系统发育树表示格式，用括号嵌套表示树结构。
 * Newick format: Standard phylogenetic tree representation format using nested parentheses.
 *
 * @see egps2.utils.common.util.SaveFileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class SaveFilterNwk extends SaveFileFilter {
	
	private static SaveFilterNwk instance = null;
	
	public static SaveFilterNwk getInstance() {
		if (instance == null) {
			instance = new SaveFilterNwk();
		}
		return instance;
	}
	
	@Override
	public boolean accept(File f) {
       if (f.isDirectory()) {
           return true;
       }
       String s = f.getName();
       return s.endsWith(".nwk")||s.endsWith(".NWK");
  }

	@Override
	public String getDescription() {
		return "TREE FORMAT (*.nwk)";
	}

	@Override
	public String getFileSuffix() {
		return "nwk";
	}
}
