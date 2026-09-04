package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * VCF压缩格式文件保存过滤器，用于保存gzip压缩的基因组变异数据文件的保存对话框。
 * VCF compressed format file save filter for saving gzip compressed genomic variant data file save dialogs.
 *
 * <p>此过滤器用于保存.vcf.gz格式的压缩基因组变异数据文件，减少存储空间。
 * This filter is used for saving .vcf.gz format compressed genomic variant data files to reduce storage space.
 *
 * <p><strong>支持的扩展名：</strong> .vcf.gz, .VCF.GZ
 * Supported extensions: .vcf.gz, .VCF.GZ
 *
 * <p><strong>VCF.GZ格式：</strong>gzip压缩的VCF格式，包含SNP、InDel等变异信息，节省存储空间。
 * VCF.GZ format: gzip compressed VCF format containing SNP, InDel and other variant information, saving storage space.
 *
 * @see javax.swing.filechooser.FileFilter
 * @author YFQ
 * @since 2.1
 */
public class SaveFilterVcfGz extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String s = f.getName();

		return s.endsWith(".vcf.gz") || s.endsWith(".VCF.GZ");
	}

	@Override
	public String getDescription() {
		return "VCF (*.vcf.gz)";
	}

}
