package egps2.modulei;

import java.io.IOException;
import java.net.URL;

import javax.swing.JComponent;

import egps2.frame.ModuleFace;
import egps2.panels.InformationPanelFactory;
import top.signature.IModuleSignature;

/**
 * 核心模块加载器接口，所有eGPS模块必须实现此接口。
 * Core module loader interface that all eGPS modules must implement.
 *
 * <p>此接口定义了模块化框架的核心契约，每个模块通过实现此接口向系统声明其基本信息和能力。
 * 系统服务（{@link egps2.UnifiedAccessPoint}）会自动发现并加载所有实现了此接口的模块。
 * This interface defines the core contract for the modular framework. Each module declares
 * its basic information and capabilities by implementing this interface. The system service
 * will automatically discover and load all instances implementing this interface.
 *
 * <p>模块信息包括：
 * Module information includes:
 * <ul>
 *   <li>模块名称（Tab标签名）- Module name (tab label)</li>
 *   <li>简短描述 - Short description</li>
 *   <li>图形界面面板 - Graphical UI panel</li>
 *   <li>模块图标 - Module icon</li>
 *   <li>分类信息 - Category classification</li>
 *   <li>模块版本 - Module version (Semantic Versioning)</li>
 *   <li>帮助文档 - Help documentation</li>
 * </ul>
 *
 * <p><strong>实现约定：</strong>
 * Implementation conventions:
 * <ul>
 *   <li>模块加载器类通常命名为 {@code IndependentModuleLoader}</li>
 *   <li>每个模块应提供 {@link ModuleFace} 的子类作为UI面板</li>
 *   <li>图标应每次调用时重新创建，不要使用全局变量</li>
 *   <li>Module loader class is typically named {@code IndependentModuleLoader}</li>
 *   <li>Each module should provide a {@link ModuleFace} subclass as UI panel</li>
 *   <li>Icons should be recreated on each call, do not use global variables</li>
 * </ul>
 *
 * <p><strong>加载方式：</strong>
 * Loading method:
 * <pre>{@code
 * UnifiedAccessPoint.loadTheModuleFromIModuleLoader(moduleLoaderInstance);
 * }</pre>
 *
 * <p>线程模型：模块加载在EDT线程中执行，模块UI初始化应避免阻塞操作。
 * Thread model: Module loading executes in EDT thread, module UI initialization should avoid blocking operations.
 *
 * @see ModuleFace
 * @see egps2.UnifiedAccessPoint
 * @see ICategory
 * @see IHelp
 * @see ModuleVersion
 * @author eGPS Dev Team
 * @since 2.1
 */
public interface IModuleLoader extends IHelp, ICategory, IModuleSignature {

	static IconBean emptyIconBean = new IconBean();

	@Override
	String getShortDescription();

	/**
	 * Get the version of this module.
	 *
	 * <p>All modules MUST implement this method to provide version information.
	 * Version should follow Semantic Versioning 2.0.0 specification:
	 * <ul>
	 *   <li><strong>MAJOR</strong>: Incompatible API changes</li>
	 *   <li><strong>MINOR</strong>: Add functionality in a backward compatible manner</li>
	 *   <li><strong>PATCH</strong>: Backward compatible bug fixes</li>
	 * </ul>
	 *
	 * <p><strong>For Mainframe core modules:</strong>
	 * Use the shared version instance from {@link egps2.EGPSProperties#MAINFRAME_CORE_VERSION}
	 *
	 * <p><strong>For custom modules:</strong>
	 * Create your own ModuleVersion instance with appropriate version numbers.
	 *
	 * <p>Example implementation:
	 * <pre>{@code
	 * // For core modules
	 * @Override
	 * public ModuleVersion getVersion() {
	 *     return EGPSProperties.MAINFRAME_CORE_VERSION;
	 * }
	 *
	 * // For custom modules
	 * @Override
	 * public ModuleVersion getVersion() {
	 *     return new ModuleVersion(1, 2, 3);
	 * }
	 * }</pre>
	 *
	 * @return ModuleVersion object representing this module's version
	 * @see ModuleVersion
	 * @see egps2.EGPSProperties#MAINFRAME_CORE_VERSION
	 * @since 2.2
	 */
	ModuleVersion getVersion();

	/**
	 * 模块应该有一个Icon，如果没有就 hasResource() 返回 true.
	 * 注意，这里的Icon不能设置为一个全局变量，而是每次获取都要重新创建一个对象
	 */
	default IconBean getIcon() {
		return emptyIconBean;
	};

	/**
	 * 图形界面模块专门有的
	 */
	ModuleFace getFace();
	
	
	@Override
	String getTabName();

	@Override
	default JComponent getEnglishDocument() {
		URL resource = getClass().getResource("manual_en.html");
		if (resource == null) {
			return null;
		}
		try {
			return new InformationPanelFactory().getInformationPanelFromResource(resource);
		} catch (IOException e) {
			return null;
		}
		
	}

	@Override
	default JComponent getChineseDocument() {
		URL resource = getClass().getResource("manual_zh.html");
		if (resource == null) {
			return null;
		}
		try {
			return new InformationPanelFactory().getInformationPanelFromResource(resource);
		} catch (IOException e) {
			return null;
		}
	}
}
