package egps2.builtin.modules.itoolmanager;

/**
 * 工具管理器模块加载器，提供eGPS内置工具和模块的集中管理界面。
 * Tool manager module loader providing centralized management interface for eGPS built-in tools and modules.
 *
 * <p>此模块实现 {@link egps2.modulei.IModuleLoader} 接口，为用户提供模块浏览和管理功能。
 * This module implements {@link egps2.modulei.IModuleLoader} interface, providing module browsing and management functionality for users.
 *
 * <p><strong>模块信息：</strong>
 * Module information:
 * <ul>
 *   <li>Tab 名称：Tool Manager</li>
 *   <li>功能：查看和管理已加载的模块、工具列表</li>
 *   <li>依赖：无特殊外部依赖</li>
 *   <li>入口面板：{@link GuiMain}</li>
 * </ul>
 *
 * <p>加载方式：通过 {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader} 动态加载。
 * Loading method: Dynamically loaded via {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader}.
 *
 * @see egps2.modulei.IModuleLoader
 * @see GuiMain
 * @author eGPS Dev Team
 * @since 2.1
 */

import java.io.InputStream;

import egps2.EGPSProperties;
import egps2.frame.ModuleFace;
import egps2.modulei.IconBean;
import egps2.modulei.IModuleLoader;
import egps2.modulei.ModuleClassification;
import egps2.modulei.ModuleVersion;

/**
 * IndependentModuleLoader belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class IndependentModuleLoader implements IModuleLoader {


	@Override
	public String getTabName() {
		return "ITools Manager";
	}

	@Override
	public String getShortDescription() {
		return "The loading manager for the independent modules.";
	}

	@Override
	public ModuleFace getFace() {
		GuiMain guiMain = new GuiMain(this);
		return guiMain;
	}

	@Override
	public IconBean getIcon() {
		InputStream resourceAsStream = getClass().getResourceAsStream("/images/maincore/ITools Manager.svg");
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
