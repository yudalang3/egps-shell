package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * RNA-seq标准化表达谱文件打开过滤器，用于RNA测序表达数据文件的打开对话框。
 * RNA-seq normalized expression profile file open filter for RNA sequencing expression data file open dialogs.
 *
 * <p>此过滤器用于打开.rnaExp格式的RNA-seq标准化表达数据文件，包含基因表达量信息。
 * This filter is used for opening .rnaExp format RNA-seq normalized expression data files containing gene expression level information.
 *
 * <p><strong>支持的扩展名：</strong> .rnaExp
 * Supported extensions: .rnaExp
 *
 * <p><strong>用途：</strong>RNA测序数据分析、差异表达分析、基因表达可视化。
 * Usage: RNA-seq data analysis, differential expression analysis, gene expression visualization.
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterRNAExp extends FileFilter {

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".rnaExp")) {
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "RNA-seq normalized expression profile (*.rnaExp)";
	}

}
