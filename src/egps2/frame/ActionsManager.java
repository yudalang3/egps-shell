package egps2.frame;

import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

/**
 * 单例动作管理器，注册和调度所有应用程序菜单和工具栏的动作。
 * Singleton action manager that registers and dispatches all application menu and toolbar actions.
 *
 * <p>此类是框架中动作（Action）的中央注册表，管理所有顶层菜单和工具栏按钮的动作实例。
 * 通过单例模式确保所有动作实例的唯一性和一致性。
 * This class is the central registry for actions in the framework, managing all top-level menu
 * and toolbar button action instances. Singleton pattern ensures uniqueness and consistency of all action instances.
 *
 * <p><strong>管理的动作类别：</strong>
 * Managed action categories:
 * <ul>
 *   <li><b>线程动作 (Thread Actions)：</b>停止任务、查看线程状态</li>
 *   <li><b>数据动作 (Data Actions)：</b>导入数据、导出数据</li>
 *   <li><b>工具动作 (Tool Actions)：</b>帮助、信息、统计</li>
 *   <li><b>选项动作 (Option Actions)：</b>偏好设置、搜索</li>
 *   <li><b>关于动作 (About Actions)：</b>关于对话框、主页链接</li>
 *   <li><b>退出动作 (Exit Action)：</b>退出应用程序</li>
 * </ul>
 *
 * <p><strong>生命周期：</strong>
 * Lifecycle:
 * <ol>
 *   <li>应用启动时自动创建单例实例</li>
 *   <li>在构造器中初始化所有动作实例</li>
 *   <li>{@link egps2.frame.MyFrame} 通过此管理器获取动作并添加到菜单/工具栏</li>
 *   <li>动作实例在应用程序整个生命周期内保持有效</li>
 * </ol>
 *
 * <p><strong>使用方式：</strong>
 * Usage:
 * <pre>{@code
 * // 获取管理器单例
 * ActionsManager manager = ActionsManager.getInstance();
 *
 * // 获取数据导入/导出动作
 * List<AdjustedSoftAction> dataActions = manager.getDataActions();
 *
 * // 获取退出动作
 * ActionExit exitAction = manager.getActionExit();
 * }</pre>
 *
 * <p><strong>动作分组：</strong>
 * Action grouping:
 * <ul>
 *   <li>{@link #getDataActions()} - 返回数据相关动作列表（导入/导出）</li>
 *   <li>{@link #getThreadActions()} - 返回线程相关动作列表</li>
 *   <li>{@link #getOptionActions()} - 返回选项相关动作列表（帮助/信息/统计）</li>
 *   <li>{@link #getTripleTools()} - 返回三元组工具（帮助、信息、统计）</li>
 * </ul>
 *
 * <p><strong>设计模式：</strong>
 * Design patterns:
 * <ul>
 *   <li><b>单例模式：</b>确保全局唯一的动作管理器实例</li>
 *   <li><b>注册表模式：</b>集中管理所有动作实例</li>
 *   <li><b>命令模式：</b>每个动作封装一个具体的操作</li>
 * </ul>
 *
 * <p>线程安全：此类在应用启动时初始化，之后仅提供只读访问，因此是线程安全的。
 * Thread safety: This class is initialized at application startup and provides read-only access thereafter, thus is thread-safe.
 *
 * @see javax.swing.Action
 * @see AbstractSoftAction
 * @see AdjustedSoftAction
 * @see egps2.frame.MyFrame
 * @author eGPS Dev Team
 * @since 2.0
 */
public class ActionsManager {

	private static ActionsManager instance = new ActionsManager();

	private List<AdjustedSoftAction> threadActions;
	private final List<Action> optionActions;
	private List<Action> aboutActions;
	private List<AdjustedSoftAction> dataActions;

	private ActionExit actionExit;
	private ActionSearch actionSearch;
	private ActionPreference actionPerference;
	private ActionAbout actionAbout;
	private ActionLaunchLastModule actionLaunchLastModule;

	private ActionHomePage homePageAction;

	private final Triple<ActionHelp, ActionInformation, ActionStatics> tripleTools;


	public static ActionsManager getInstance() {
		return instance;
	}

	private ActionsManager() {

		dataActions = new ArrayList<>();
		dataActions.add(new ActionImportData());
		dataActions.add(new ActionExportData());

		actionAbout = new ActionAbout();
		
		optionActions = new ArrayList<>();

		ActionHelp actionHelp = new ActionHelp();
		optionActions.add(actionHelp);
		ActionInformation actionInformation = new ActionInformation(actionAbout);
		optionActions.add(actionInformation);
		ActionStatics actionStatics = new ActionStatics();
		optionActions.add(actionStatics);
		tripleTools = Triple.of(actionHelp, actionInformation, actionStatics);

		threadActions = new ArrayList<>();
		threadActions.add(new ActionStop());
		
		aboutActions = new ArrayList<>();
		aboutActions.add(actionAbout);

		// Add bilingual FAQ and History actions
		aboutActions.add(new ActionFaqEnglish());
		aboutActions.add(new ActionFaqChinese());
		aboutActions.add(new ActionHistoryEnglish());
		aboutActions.add(new ActionHistoryChinese());

		homePageAction = new ActionHomePage();
		aboutActions.add(homePageAction);
		aboutActions.add(new ActionReportBugs());
//		aboutActions.add(new ActionStdOutConsole());
		aboutActions.add(new ActionLicenseTerm());
		aboutActions.add(new ActionStdErrConsole());

		actionSearch = new ActionSearch();

		actionExit = new ActionExit();
		actionPerference = new ActionPreference();
		actionLaunchLastModule = new ActionLaunchLastModule();



	}
	
	public ActionHomePage getHomePageAction() {
		return homePageAction;
	}

	public ActionExit getActionExit() {
		return actionExit;
	}

	public ActionSearch getActionSearch() {
		return actionSearch;
	}

	public List<AdjustedSoftAction> getDataActions() {
		return dataActions;
	}

	public List<Action> getOptionActions() {
		return optionActions;
	}

	public List<AdjustedSoftAction> getThreadActions() {
		return threadActions;
	}

	public ActionPreference getActionPreference() {
		return actionPerference;
	}

	public ActionAbout getActionAbout() {
		return actionAbout;
	}

	public ActionLaunchLastModule getActionLaunchLastModule() {
		return actionLaunchLastModule;
	}

	public List<Action> getAboutActions() {
		return aboutActions;
	}
	
	public Triple<ActionHelp, ActionInformation, ActionStatics> getTripleTools() {
		return tripleTools;
	}
}
