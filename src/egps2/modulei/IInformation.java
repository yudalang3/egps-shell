package egps2.modulei;

/**
 * 模块信息接口，定义了模块元数据和运行时信息的契约。
 * Module information interface that defines the contract for module metadata and runtime information.
 *
 * <p>此接口用于在信息面板中展示模块的详细信息，帮助用户了解模块的使用方式、
 * 数据要求、操作方法和结果输出。所有信息将显示在模块的"Information"面板中。
 * This interface displays detailed module information in the information panel, helping users
 * understand module usage, data requirements, operation methods, and result output.
 * All information is displayed in the module's "Information" panel.
 *
 * <p><strong>信息类别：</strong>
 * Information categories:
 * <ul>
 *   <li>{@link #getHowModuleLaunch()} - 模块启动方式说明</li>
 *   <li>{@link #getWhatDataInvoked()} - 所需数据类型和格式</li>
 *   <li>{@link #getHowUserOperates()} - 用户操作指南</li>
 *   <li>{@link #getSummaryOfResults()} - 结果输出说明</li>
 * </ul>
 *
 * <p><strong>格式支持：</strong>
 * Format support:
 * <br>返回的字符串支持简单的HTML标签（如 {@code <br>}、{@code <b>}、{@code <i>} 等），
 * 用于格式化显示。建议使用换行符和简单标签提升可读性。
 * Returned strings support simple HTML tags (such as {@code <br>}, {@code <b>}, {@code <i>}, etc.)
 * for formatted display. Use line breaks and simple tags to improve readability.
 *
 * <p><strong>默认实现：</strong>
 * Default implementation:
 * <br>接口提供了部分方法的默认实现（如 {@link #getHowModuleLaunch()} 和 {@link #getHowUserOperates()}），
 * 模块可根据需要覆盖这些方法。{@link BaseInformationImp} 类提供了JavaBean风格的完整实现。
 * The interface provides default implementations for some methods, which modules can override as needed.
 * {@link BaseInformationImp} provides a complete JavaBean-style implementation.
 *
 * <p>线程模型：信息获取方法在EDT线程中调用，应避免阻塞操作。
 * Thread model: Information retrieval methods are called in EDT thread, avoid blocking operations.
 *
 * @see BaseInformationImp
 * @author eGPS Dev Team
 * @since 2.1
 */
public interface IInformation {

	/**
	 * The information that appeared in the information panel.
	 * 
	 * 之前有乱七八糟的跳转，都分不清楚。
	 * 
	 * Simple html tags are supported.
	 */
	default String getHowModuleLaunch() {
		String moduleLaunchWay = "Module launched by click the open button.";
		return moduleLaunchWay;
	};
	
	/**
	 * The information that appeared in the information panel.
	 * 
	 * Simple html tags are supported.
	 */
	String getWhatDataInvoked();
	
	/**
	 * The information that appeared in the information panel.
	 * 
	 * Simple html tags are supported.
	 */
	default String getHowUserOperates() {
		return "For data, this is a pure visualization module, no operation on it.<br>";
	}
	
	/**
	 * The information that appeared in the information panel.
	 * 
	 * Simple html tags are supported.
	 */
	String getSummaryOfResults();
}
