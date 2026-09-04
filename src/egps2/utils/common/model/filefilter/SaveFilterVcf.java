package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * VCF变异调用格式文件保存过滤器，用于保存基因组变异数据文件的保存对话框。
 * VCF variant call format file save filter for saving genomic variant data file save dialogs.
 *
 * <p>此过滤器用于保存.vcf格式的基因组变异数据文件。
 * This filter is used for saving .vcf format genomic variant data files.
 *
 * <p><strong>支持的扩展名：</strong> .vcf, .VCF
 * Supported extensions: .vcf, .VCF
 *
 * <p><strong>VCF格式：</strong>标准的基因组变异数据格式，包含SNP、InDel等变异信息。
 * VCF format: Standard genomic variant data format containing SNP, InDel and other variant information.
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class SaveFilterVcf extends FileFilter {
	

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".vcf") || s.endsWith(".VCF");
	}

	@Override
	public String getDescription() {
		return "VCF (*.vcf)";
	}

}
