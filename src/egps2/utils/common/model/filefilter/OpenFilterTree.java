package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * 系统发育树文件打开过滤器，用于多种树格式文件的打开对话框。
 * Phylogenetic tree file open filter for multiple tree format file open dialogs.
 *
 * <p>此过滤器支持多种系统发育树文件格式，用于进化分析和树可视化。
 * This filter supports multiple phylogenetic tree file formats for evolutionary analysis and tree visualization.
 *
 * <p><strong>支持的扩展名：</strong>
 * Supported extensions:
 * <ul>
 *   <li>.nex, .nexus - NEXUS格式 (NEXUS format)</li>
 *   <li>.nhx - New Hampshire Extended格式 (New Hampshire Extended format)</li>
 *   <li>.nwk - Newick格式 (Newick format)</li>
 *   <li>.tree, .tre - 通用树格式 (Generic tree format)</li>
 * </ul>
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterTree extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".nex") || f.getName().endsWith(".nexus") || f.getName().endsWith(".nhx") 
					|| f.getName().endsWith(".nwk") || f.getName().endsWith(".tree") || f.getName().endsWith(".tre")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Tree format (*.nex,*.nexus,*.nhx,*.nwk,*.tree,*.tre)";
	}
}
