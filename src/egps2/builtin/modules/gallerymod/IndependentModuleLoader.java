package egps2.builtin.modules.gallerymod;

/**
 * 图片画廊模块加载器，提供图片浏览和展示功能。
 * Gallery module loader providing image browsing and display functionality.
 *
 * <p>此模块实现 {@link egps2.modulei.IModuleLoader} 接口，为用户提供图片展示和演示功能。
 * This module implements {@link egps2.modulei.IModuleLoader} interface, providing image display and demonstration functionality for users.
 *
 * <p><strong>模块信息：</strong>
 * Module information:
 * <ul>
 *   <li>Tab 名称：Gallery Demonstration</li>
 *   <li>功能：图片浏览、展示、演示</li>
 *   <li>依赖：无特殊外部依赖</li>
 *   <li>入口面板：{@link IntroMain}</li>
 * </ul>
 *
 * <p>加载方式：通过 {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader} 动态加载。
 * Loading method: Dynamically loaded via {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader}.
 *
 * @see egps2.modulei.IModuleLoader
 * @see IntroMain
 * @author eGPS Dev Team
 * @since 2.1
 */

import java.io.InputStream;

import javax.swing.JPanel;

import egps2.EGPSProperties;
import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.modulei.ModuleClassification;
import egps2.modulei.ModuleVersion;

/**
 * IndependentModuleLoader belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class IndependentModuleLoader implements IModuleLoader{

	public static final String INTRO_MODULE_NAME = EGPSProperties.MODULE_INTRO_NAME;
	
	public IndependentModuleLoader() {

	}

	@Override
	public JPanel getEnglishDocument() {
		return null;
	}

	@Override
	public JPanel getChineseDocument() {
		return null;
	}

	@Override
	public String getTabName() {
		return INTRO_MODULE_NAME;
	}

	@Override
	public String getShortDescription() {
		return "Introduction of the modules in eGPS.";
	}

	@Override
	public IconBean getIcon() {
		InputStream resourceAsStream = getClass().getResourceAsStream("/images/maincore/Introduction.svg");
		IconBean iconBean = new IconBean();
		iconBean.setSVG(true);
		iconBean.setInputStream(resourceAsStream);
		return iconBean;
	}

	

	@Override
	public ModuleFace getFace() {
		return new IntroMain(this);
	}
	
	@Override
	public int[] getCategory() {
		//这里不重要，因为这个结果不会被用到。
		int[] ret = ModuleClassification.getOneModuleClassification(
				ModuleClassification.BYFUNCTIONALITY_COMPLICATED_VISUALIZATION_INDEX
		);
		return ret;
	}


	@Override
	public ModuleVersion getVersion() {
		return EGPSProperties.MAINFRAME_CORE_VERSION;
	}

}
