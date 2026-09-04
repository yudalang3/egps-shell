package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Newick系统发育树文件过滤器，用于生物信息学树文件的文件选择对话框。
 * Newick phylogenetic tree file filter for bioinformatics tree file selection dialogs.
 *
 * <p>此过滤器接受.nwk和.NWK扩展名的文件，<strong>不接受目录</strong>。
 * This filter accepts files with .nwk and .NWK extensions, <strong>does not accept directories</strong>.
 *
 * <p><strong>支持的扩展名：</strong> .nwk, .NWK
 * Supported extensions: .nwk, .NWK
 *
 * <p><strong>Newick格式：</strong>标准的系统发育树表示格式，用括号嵌套表示树结构。
 * Newick format: Standard phylogenetic tree representation format using nested parentheses.
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class FileFilterNwk extends FileFilter {
	
	
	public FileFilterNwk() {
		
	}
	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return false;
		}

		String s = f.getName();

		return s.endsWith(".nwk") || s.endsWith(".NWK");
	}

	@Override
	public String getDescription() {
		return "Tree (*.nwk, *.NWK)";
	}

}
