package egps2.modulei;

import javax.swing.JComponent;

/**
 * 模块帮助文档接口，定义了模块提供帮助文档的契约。
 * Module help documentation interface that defines the contract for providing help documentation.
 *
 * <p>此接口允许模块提供中英文双语的帮助文档，文档以Swing组件形式展示。
 * 帮助文档通常以HTML格式编写，存储在模块目录下的 {@code manual_en.html} 和 {@code manual_zh.html} 文件中。
 * This interface allows modules to provide bilingual (English/Chinese) help documentation.
 * Documentation is displayed as Swing components and typically written in HTML format,
 * stored as {@code manual_en.html} and {@code manual_zh.html} files in the module directory.
 *
 * <p><strong>实现说明：</strong>
 * Implementation notes:
 * <ul>
 *   <li>主框架会自动添加滚动面板（JScrollPane），开发者无需额外包装</li>
 *   <li>如果模块未提供帮助文档，方法应返回 {@code null}</li>
 *   <li>帮助文档在"Introduction"面板中显示</li>
 *   <li>Main frame automatically wraps content with JScrollPane, developers don't need to wrap</li>
 *   <li>Methods should return {@code null} if help documentation is not provided</li>
 *   <li>Help documentation is displayed in the "Introduction" panel</li>
 * </ul>
 *
 * <p><strong>文档格式：</strong>
 * Documentation format:
 * <br>支持HTML标签进行富文本格式化（如粗体、斜体、列表、链接等）。
 * 建议使用简洁的HTML结构，避免复杂的CSS样式。
 * HTML tags are supported for rich text formatting (bold, italic, lists, links, etc.).
 * Simple HTML structure is recommended, avoid complex CSS styles.
 *
 * <p>线程模型：文档加载在EDT线程中执行，应避免耗时的I/O操作。
 * Thread model: Documentation loading executes in EDT thread, avoid time-consuming I/O operations.
 *
 * @see javax.swing.JComponent
 * @see egps2.panels.InformationPanelFactory
 * @author eGPS Dev Team
 * @since 2.0.0.95
 */
public interface IHelp {

	
	/**
	 *  This will appear as Graphical Illustration in the Introduction module.
	 *  
	 *  参看更新 2.0.0.95
	 *  
	 *  Note, the main frame will wrap a JscollPanel. so developers do not need to wrap at all.
	 * @return null if developers don not implemented.
	 */
	JComponent getEnglishDocument();
	
	
	/**
	 * This will appear as Text Explanation in the Introduction module.
	 * 
	 * Note, the main frame will wrap a JscollPanel. so developers do not need to wrap at all.
	 * @return null if developers don not implemented.
	 */
	JComponent getChineseDocument();
}
