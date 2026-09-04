package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * VCF变异调用格式文件打开过滤器，用于基因组变异数据文件的打开对话框。
 * VCF variant call format file open filter for genomic variant data file open dialogs.
 *
 * <p>此过滤器支持VCF格式及其gzip压缩版本，用于基因组变异分析。
 * This filter supports VCF format and its gzip compressed version for genomic variant analysis.
 *
 * <p><strong>支持的扩展名：</strong> .vcf, .vcf.gz
 * Supported extensions: .vcf, .vcf.gz
 *
 * <p><strong>VCF格式：</strong>标准的基因组变异数据格式，包含SNP、InDel等变异信息。
 * VCF format: Standard genomic variant data format containing SNP, InDel and other variant information.
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class OpenFilterVcf extends FileFilter {

	@Override
	public boolean accept(File f) {
		if(f.isDirectory()){
			return true;
		} else {
			if (f.getName().endsWith(".vcf.gz") || f.getName().endsWith(".vcf")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {
		return "Variant call format (*.vcf,*.vcf.gz)";
	}
}
