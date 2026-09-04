package egps2.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Map;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JPanel;

import egps2.EGPSProperties;
import utils.storage.MapPersistence;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.gallerymod.IntroMain;
import egps2.modulei.CreditBean;
import egps2.modulei.IInformation;
import egps2.modulei.IModuleFace;
import egps2.modulei.IModuleLoader;

/**
 * 模块UI面板抽象基类，提供 {@link IModuleFace} 接口的骨架实现。
 * Abstract base class for module UI panels, providing skeletal implementation of the {@link IModuleFace} interface.
 *
 * <p>此类简化了模块UI面板的实现成本，为所有模块提供了通用的功能和生命周期管理。
 * 每个模块的UI面板应继承此类，并实现必要的抽象方法。
 * This class simplifies the implementation cost of module UI panels, providing common functionality
 * and lifecycle management for all modules. Each module's UI panel should extend this class
 * and implement necessary abstract methods.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>模块加载器引用管理 - Module loader reference management</li>
 *   <li>模块属性持久化（保存/恢复用户配置）- Module properties persistence (save/restore user configuration)</li>
 *   <li>功能使用统计追踪 - Feature usage statistics tracking</li>
 *   <li>数据导入导出的默认实现 - Default implementation for data import/export</li>
 *   <li>帮助文档和团队信息的默认实现 - Default implementation for help documentation and team credits</li>
 * </ul>
 *
 * <p><strong>构造器约定：</strong>
 * Constructor conventions:
 * <br>构造器被声明为 {@code protected}，只允许相应的 {@link IModuleLoader} 创建实例。
 * 这确保了模块面板只能通过正确的加载器实例化，维护了模块加载的规范性。
 * The constructor is declared as {@code protected}, allowing only the corresponding {@link IModuleLoader}
 * to create instances. This ensures that module panels can only be instantiated through proper loaders,
 * maintaining the normative nature of module loading.
 *
 * <p><strong>属性持久化：</strong>
 * Properties persistence:
 * <br>模块可以使用 {@code properties} Map 存储用户配置（如窗口大小、参数设置等），
 * 这些配置会自动保存到 {@code ~/.egps/[ModuleName].json} 文件中。
 * Modules can use the {@code properties} Map to store user configurations (such as window size, parameter settings, etc.),
 * which are automatically saved to the {@code ~/.egps/[ModuleName].json} file.
 *
 * <p><strong>生命周期方法：</strong>
 * Lifecycle methods:
 * <ul>
 *   <li>{@link #closeTab()} - 标签页关闭时调用，默认实现检查是否有运行中的任务</li>
 *   <li>{@link #changeToThisTab()} - 切换到此标签页时调用，默认实现为空</li>
 *   <li>{@link #closeTab()} - Called when tab is closed, default implementation checks for running tasks</li>
 *   <li>{@link #changeToThisTab()} - Called when switching to this tab, default implementation is empty</li>
 * </ul>
 *
 * <p><strong>子类化指南：</strong>
 * Subclassing guidelines:
 * <ol>
 *   <li>继承 ModuleFace 并提供 {@code protected} 构造器，接受 {@link IModuleLoader} 参数</li>
 *   <li>使用 {@link BorderLayout} 布局管理器（已在基类中设置）</li>
 *   <li>实现必要的接口方法（如 {@link #getFeatureNames()}、{@link #getDevTeam()} 等）</li>
 *   <li>如需数据导入导出，覆盖 {@link #canImport()}、{@link #importData()} 等方法</li>
 *   <li>如需运行计算任务，考虑继承 {@link ComputationalModuleFace} 而非直接继承此类</li>
 * </ol>
 *
 * <p><strong>示例：</strong>
 * Example:
 * <pre>{@code
 * public class MyModuleFace extends ModuleFace {
 *     protected MyModuleFace(IModuleLoader moduleLoader) {
 *         super(moduleLoader);
 *         initializeUI();
 *     }
 *
 *     private void initializeUI() {
 *         // 添加UI组件到面板
 *         add(myMainPanel, BorderLayout.CENTER);
 *     }
 *
 *     @Override
 *     public String[] getFeatureNames() {
 *         return new String[]{"Import", "Export", "Visualize"};
 *     }
 * }
 * }</pre>
 *
 * <p>线程模型：所有UI操作必须在EDT中执行。如需后台任务，使用 {@link ComputationalModuleFace}。
 * Thread model: All UI operations must execute in EDT. For background tasks, use {@link ComputationalModuleFace}.
 *
 * @see IModuleFace
 * @see IModuleLoader
 * @see ComputationalModuleFace
 * @see egps2.UnifiedAccessPoint
 * @author yudalang
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class ModuleFace extends JPanel implements IModuleFace {
	protected IModuleLoader moduleLoader;
	private Map<String, Integer> properties;
	private String jsonPath;

	/**
	 * <pre>
	 * 这个构造方法需要先传入一个ModuleLoader， 这其实也是一个包含了动作的声明，什么意思呢？
	 * 这里面就蕴含了，这个模块是怎么样被打开的，因为只有loader才能加载模块。
	 * 
	 * 注意，这里的构造方法的限定符是 protected，因为这个构造方法不能随便被其它包下的类调用。
	 * 这个类只能被相对应的 ModuleLoader才能创建。
	 * 最终的子类可以直接将这个方法限定为 default，可以定死只能让相应的 moduleLoader来加载。
	 * 
	 * 如果用户想要直接能够创建，那么用户可以自己选择再在子类中，写一个专门的构造器。
	 * 不过这种需求我想在感觉只有在调试的时候才有。
	 * 
	 * </pre>
	 * 
	 * @param moduleLoader : 这个loader可以是直接 open a blank tab。也可以是历史记录跳过来的。
	 */
	protected ModuleFace(IModuleLoader moduleLoader) {
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);

		// 这里是允许传入null值的，因为在一些情况下没有MoudleLoader，但是此时要注意。 getModuleLoader方法返回的值为null。
		// 详见 getModuleLoader方法: #getModuleLoaders。
		if (moduleLoader == null) {
			return;
		}
		this.moduleLoader = moduleLoader;

		if (getClass() == IntroMain.class) {
			return;
		}

		// 如果在命令行下面使用防止启动eGPS
		if (!UnifiedAccessPoint.isGULaunched()) {
			return;
		}

		// 不能用这个name，因为像 editor是文件名称：String name = moduleLoader.getTabName();
		String name = moduleLoader.getClass().getName();

		boolean isFirst = UnifiedAccessPoint.getLaunchProperty().increaseTheModuleLaunchTime(name) == 0;
		if (isFirst) {
			MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
			instanceFrame.reachOneAchievements("First time", "Welcome to use this module");
		}

	}

	/**
	 * 模块加载之后，就由Main Frame来调用这个方法。为什么要新增这样一个类呢？因为这样就可以在
	 * initializeGraphics()方法前后添加执行代码了。
	 */
	void initializeModuleFaceGUI() {
		initializeGraphics();

		// 这个功能去掉了。
//		if (UniSoftInstance.getLauchProperty().should_auto_click_import && canImport()) {
//			SwingUtilities.invokeLater(() -> {
//				importData();
//			});
//		}

	};

	/**
	 * 一开始载入JPanel等组件的时候是不知道可用的width和height等属性的。
	 * 很多大型可视化模块需要得到这些属性，因此需要在载入JPanel之后调用绘制方法。
	 * 故需要一个额外的步骤来初始化，框架会自己调用这个方法，不需要用户来调用它，而是只要实现它。
	 */
	protected abstract void initializeGraphics();

	/**
	 * 这个方法是为 模块的统计功能所服务的， 开发者可以在用户关闭模块的时候，更新用户使用的功能的情况。
	 * 
	 * 为什么会是关闭的时候呢？而不是实时或者最早的时候呢？ 因为得是一个有效的操作才行，而且不能太占用资源。
	 * 当然，也不一定，有些人操作是实时的，所以可以直接记录。
	 * 
	 * 这个方法对于开发模块的用户一定要主动调用，这样才能记录一下模块的情况，这个肯定得是模块主动来注册的。
	 * 
	 * @param featureName 用entry的形式返回模块使用的次数。
	 */
	protected void recordFeatureUsed4user(String featureName) {
		/**
		 * 模块肯定是在用户有一个有效的操作之后才调用这个方法的。
		 */

		if (properties == null) {
			initializeProperties();
		}

		Integer integer = properties.get(featureName);
		int count = integer == null ? 0 : integer;
		if (count == 0) {
			// 提示语
			UnifiedAccessPoint.getInstanceFrame()
					.prompt("Congratulations, you have unlocked the feature of ".concat(featureName));
		}

		properties.put(featureName, count + 1);

	}
	/**
	 * 下面的两个方法为模块提供了一种默认的模块实现方式，用户需要自己继承getFeatureNames方法。
	 * 然后再调用invoke...方法主动注册事件。
	 *
	 * @return 功能集合，默认是Null，表示开发者还没有实现。
	 */
	@Override
	public String[] getFeatureNames() {
		return null;
	}

	/**
	 * 该方法和 recordFeatureUsed4user()的区别在于：
	 * 1. 开发者可以快速用int 来记录，而不是输入字符串，字符串容易有错别字
	 * 2. 这个方法是public的，所以除了子类，其它类也能调用
	 * @param index
	 */
	public void invokeTheFeatureMethod(int index) {
		String[] featureNames = getFeatureNames();
		if (index >= featureNames.length || index < 0) {
			throw new IllegalArgumentException("");
		}
		recordFeatureUsed4user(featureNames[index]);
	}

	private void initializeProperties() {
		String name = getClass().getName();

		StringBuilder sb = new StringBuilder();
		sb.append(EGPSProperties.JSON_DIR).append("/").append(name);
		sb.append(".ser.gz");
		jsonPath = sb.toString();

		try {
			properties = MapPersistence.getStr2numberMap(jsonPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean moduleExisted() {
		if (properties != null) {
			MapPersistence.storeStr2numberMap(properties, jsonPath);
		}
		releaseObjectsReferenceForGC();
		return closeTab();
	}

	private void releaseObjectsReferenceForGC() {
		moduleLoader = null;
		properties = null;
	}

	public final Map<String, Integer> getFeatureUsedCountMap(String[] features) {
		if (properties == null) {
			initializeProperties();
		}

		for (String string : features) {
			Integer integer = properties.get(string);
			if (integer == null) {
				properties.put(string, 0);
			}
		}

		return properties;
	}

	@Override
	public JComponent getEnglishDocument() {
		if (moduleLoader == null) {
			return null;
		}
		return moduleLoader.getEnglishDocument();
	}

	@Override
	public JComponent getChineseDocument() {
		if (moduleLoader == null) {
			return null;
		}
		return moduleLoader.getChineseDocument();
	}

	public IInformation getModuleInfo() {
		return null;
	}

	@Override
	public CreditBean getDevTeam() {
		return new CreditBean();
	}

	@Override
	public boolean closeTab() {
		return false;
	}

	@Override
	public void changeToThisTab() {

	}

	/**
	 * 在一些情况下是没有ModuleLoader的，例如DIYTools，它本身就是ModuleLoader，
	 * 因为这些小工具不需要区分ModuleLoader和ModuleFace。
	 * 还有就是用DIYTools作为模块的子面板。这个也不需要。
	 * 
	 * @return moduleLoader可能为null
	 */
	public Optional<IModuleLoader> getModuleLoader() {
		return Optional.ofNullable(moduleLoader);
	}
}
