package egps2.modulei;

/**
 * 模块UI面板接口，定义了可视化模块的视图契约。
 * Module UI panel interface that defines the visual module view contract.
 *
 * <p>此接口为所有GUI模块定义了统一的生命周期管理方法和基本能力。
 * 每个模块的UI面板应实现此接口或继承其默认实现类 {@link egps2.frame.ModuleFace}。
 * This interface defines unified lifecycle management methods and basic capabilities for all GUI modules.
 * Each module's UI panel should implement this interface or extend its default implementation {@link egps2.frame.ModuleFace}.
 *
 * <p><strong>核心职责：</strong>
 * Core responsibilities:
 * <ul>
 *   <li>管理模块标签页的关闭行为 - Manage module tab closing behavior</li>
 *   <li>处理标签页切换事件 - Handle tab switching events</li>
 *   <li>提供数据导入导出能力 - Provide data import/export capabilities</li>
 *   <li>支持统计功能追踪 - Support feature usage tracking</li>
 *   <li>提供帮助文档 - Provide help documentation</li>
 *   <li>显示开发团队信息 - Display development team credits</li>
 * </ul>
 *
 * <p><strong>生命周期方法：</strong>
 * Lifecycle methods:
 * <ul>
 *   <li>{@link #closeTab()} - 模块标签页关闭时调用</li>
 *   <li>{@link #changeToThisTab()} - 切换到此标签页时调用</li>
 * </ul>
 *
 * <p><strong>设计说明：</strong>
 * Design notes:
 * <br>虽然此接口可以改为抽象类，但保持接口形式以支持更灵活的继承结构。
 * 默认实现类 {@link egps2.frame.ModuleFace} 提供了大部分通用功能。
 * Although this interface could be changed to an abstract class, it remains an interface
 * to support more flexible inheritance structures. The default implementation class
 * {@link egps2.frame.ModuleFace} provides most common functionality.
 *
 * <p>线程模型：所有UI方法必须在EDT（Event Dispatch Thread）中调用。
 * Thread model: All UI methods must be called in EDT (Event Dispatch Thread).
 *
 * @see egps2.frame.ModuleFace
 * @see Credit
 * @see DataOperator
 * @see IStatistics
 * @see IHelp
 * @author eGPS Dev Team
 * @since 2.1
 */
public interface IModuleFace extends Credit, DataOperator, IStatistics, IHelp {
	
	/**
	 * Close current module. Ask user whether to save the view panel or not depends
	 * on getViewPanel(). If false, it means that the user has select "cancel"
	 * option. If true, it means that the user can save the view panel to other
	 * formats or leave it alone.
	 * 
	 * 
	 * @return true or false
	 * 
	 *         false的话就关闭这个tab；true的话表示的是有重要内容或者在运行程序就不退出！
	 */
	boolean closeTab();

	/**
	 * Actions for every time from other module come to this module
	 *
	 * 根据现在的现实情况，似乎这个方法和 initializeGraphics方法有些重叠。因为模块一开始初始化并且跳转进去之后就能实现 initialize方法中的内容。
	 * 但是现在我还是不合并这个方法和initiG的内容，因为在语义上这是两件完全不同的事情。
	 */
	void changeToThisTab();

}
