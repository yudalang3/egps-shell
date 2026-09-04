package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * MAF多序列比对文件打开过滤器，用于生物信息学多序列比对文件的打开对话框。
 * MAF multiple alignment format file open filter for bioinformatics multiple alignment file open dialogs.
 *
 * <p>此过滤器支持MAF格式及其gzip压缩版本，用于基因组序列多重比对分析。
 * This filter supports MAF format and its gzip compressed version, used for genomic sequence multiple alignment analysis.
 *
 * <p><strong>支持的扩展名：</strong> .maf, .maf.gz
 * Supported extensions: .maf, .maf.gz
 *
 * <p><strong>MAF格式：</strong>UCSC基因组浏览器使用的多序列比对格式，常用于比较基因组学。
 * MAF format: Multiple alignment format used by UCSC Genome Browser, commonly used in comparative genomics.
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterMaf extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".maf.gz") || f.getName().endsWith(".maf")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Multiple alignment format (*.maf,*.maf.gz)";
	}
}
