/**
 * 文件过滤器工具包，为Swing JFileChooser提供各种文件格式的过滤器。
 * File filter utility package providing various file format filters for Swing JFileChooser.
 *
 * <p>此包包含用于文件对话框的文件类型过滤器，支持生物信息学数据格式和常见图像/文档格式。
 * This package contains file type filters for file dialogs, supporting bioinformatics data formats and common image/document formats.
 *
 * <p><strong>过滤器类型：</strong>
 * Filter types:
 * <ul>
 *   <li><strong>FileFilter*</strong> - 通用文件过滤器（PNG, PDF, SVG, TXT, ZIP等）</li>
 *   <li><strong>OpenFilter*</strong> - 打开文件对话框专用过滤器（FASTA, VCF, Tree, CSV等）</li>
 *   <li><strong>SaveFilter*</strong> - 保存文件对话框专用过滤器（VCF, JPG, TIFF, Excel等）</li>
 * </ul>
 *
 * <p><strong>支持的生物信息学格式：</strong>
 * Supported bioinformatics formats:
 * <ul>
 *   <li>FASTA - 序列文件格式 (Sequence file format)</li>
 *   <li>VCF/VCF.GZ - 变异调用格式 (Variant Call Format)</li>
 *   <li>NWK - Newick树文件 (Newick tree file)</li>
 *   <li>MAF - 多序列比对格式 (Multiple Alignment Format)</li>
 * </ul>
 *
 * <p><strong>支持的图像格式：</strong>
 * Supported image formats:
 * PNG, JPG, TIFF, SVG, EPS, PDF
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * JFileChooser chooser = new JFileChooser();
 * chooser.setFileFilter(OpenFilterFasta.getInstance());
 * int result = chooser.showOpenDialog(parent);
 * }</pre>
 *
 * @see javax.swing.filechooser.FileFilter
 * @author eGPS Dev Team
 * @since 2.1
 */
package egps2.utils.common.model.filefilter;