
package egps2.utils.common.model.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * FASTA序列文件打开过滤器，用于生物信息学序列文件的打开对话框。
 * FASTA sequence file open filter for bioinformatics sequence file open dialogs.
 *
 * <p>此过滤器实现单例模式，支持多种FASTA文件扩展名。
 * This filter implements singleton pattern and supports multiple FASTA file extensions.
 *
 * <p><strong>支持的扩展名：</strong>
 * Supported extensions:
 * <ul>
 *   <li>.fas, .fasta - 标准FASTA格式 (Standard FASTA format)</li>
 *   <li>.fst - FASTA序列文件 (FASTA sequence file)</li>
 *   <li>.ffn - FASTA核酸编码序列 (FASTA nucleotide coding sequences)</li>
 *   <li>.ffa - FASTA氨基酸序列 (FASTA amino acid sequences)</li>
 *   <li>.fsa, .faa - FASTA序列归档 (FASTA sequence archive)</li>
 *   <li>.frn - FASTA非编码RNA序列 (FASTA non-coding RNA sequences)</li>
 *   <li>.fa - FASTA简写格式 (FASTA abbreviated format)</li>
 * </ul>
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
public class OpenFilterFasta extends FileFilter {

	private static OpenFilterFasta instance = null;

	public static OpenFilterFasta getInstance() {
		if (instance == null) {
			instance = new OpenFilterFasta();
		}
		return instance;
	}

	@Override
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		} else {
			if (f.getName().endsWith(".fas") || f.getName().endsWith(".fasta") || f.getName().endsWith(".fst")
					|| f.getName().endsWith(".ffn") || f.getName().endsWith(".ffa") || f.getName().endsWith(".fsa")
					|| f.getName().endsWith(".faa") || f.getName().endsWith(".frn") || f.getName().endsWith(".fa")
					|| f.getName().endsWith(".fst")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getDescription() {

		return "FASTA(*.FAS;*.FASTA; *.FST;*.FFN; *.FFA; *.FAS; *.FAA; *.FRN; *.FA)";
	}
}
