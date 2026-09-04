package egps2.builtin.modules.filemanager;

import java.io.InputStream;

import egps2.EGPSProperties;
import egps2.frame.ModuleFace;
import egps2.modulei.IconBean;
import egps2.modulei.IModuleLoader;
import egps2.modulei.ModuleClassification;
import egps2.modulei.ModuleVersion;

/**
 * 文件管理器模块加载器，提供跨平台的文件浏览和管理功能。
 * File manager module loader providing cross-platform file browsing and management functionality.
 *
 * <p>此模块实现 {@link IModuleLoader} 接口，为eGPS框架提供资源管理功能。
 * This module implements {@link IModuleLoader} interface, providing resource management functionality for the eGPS framework.
 *
 * <p><strong>模块信息：</strong>
 * Module information:
 * <ul>
 *   <li>Tab 名称：The Resource Manager</li>
 *   <li>功能：跨平台文件浏览器，支持文件导航、书签、属性查看</li>
 *   <li>依赖：无特殊外部依赖</li>
 *   <li>入口面板：{@link GuiMain}</li>
 * </ul>
 *
 * <p>加载方式：通过 {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader} 动态加载。
 * Loading method: Dynamically loaded via {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader}.
 *
 * @see IModuleLoader
 * @see GuiMain
 * @author eGPS Dev Team
 * @since 2.1
 */
public class IndependentModuleLoader implements IModuleLoader {

	String path = EGPSProperties.JSON_DIR.concat("/egps.file.manager.txt");


	@Override
	public String getTabName() {
		return "The Resource Manager";
	}

	@Override
	public String getShortDescription() {
		return "Cross platform resource manager for the eGPS.";
	}

	@Override
	public ModuleFace getFace() {
		GuiMain guiMain = new GuiMain(this);
		return guiMain;
	}

	@Override
	public IconBean getIcon() {
		InputStream resourceAsStream = getClass().getResourceAsStream("/images/maincore/The Resource Manager.svg");
		IconBean iconBean = new IconBean();
		iconBean.setSVG(true);
		iconBean.setInputStream(resourceAsStream);

		return iconBean;
	}

	@Override
	public int[] getCategory() {
		int[] ret = ModuleClassification.getOneModuleClassification(
				ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
				ModuleClassification.BYAPPLICATION_COMMON_MODULE_INDEX,
				ModuleClassification.BYCOMPLEXITY_LEVEL_2_INDEX,
				ModuleClassification.BYDEPENDENCY_COMPUTATIONAL_MECHANISM_EMPLOYED
		);
		return ret;
	}

	@Override
	public ModuleVersion getVersion() {
		return EGPSProperties.MAINFRAME_CORE_VERSION;
	}

}
