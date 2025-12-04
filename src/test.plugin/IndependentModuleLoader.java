package test.plugin;

/**
 * 快速模块模板加载器，为开发者提供快速创建新模块的模板框架。
 * Fast module template loader providing a template framework for developers to quickly create new modules.
 *
 * <p>此模块实现 {@link egps2.modulei.IModuleLoader} 接口，作为新模块开发的起始模板。
 * This module implements {@link egps2.modulei.IModuleLoader} interface, serving as a starting template for new module development.
 *
 * <p><strong>模块信息：</strong>
 * Module information:
 * <ul>
 *   <li>Tab 名称：Fast Module Loader</li>
 *   <li>功能：模块开发模板、快速启动框架</li>
 *   <li>状态：当前显示为未实现状态，供开发者基于此模板进行开发</li>
 *   <li>依赖：无特殊外部依赖</li>
 *   <li>入口面板：{@link XXXMainFace}</li>
 * </ul>
 *
 * <p><strong>使用指南：</strong>
 * Usage guide:
 * <br>开发者可以复制此模板包，修改类名和功能实现，快速创建新的eGPS模块。
 * Developers can copy this template package, modify class names and functionality implementation to quickly create new eGPS modules.
 *
 * <p>加载方式：通过 {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader} 动态加载。
 * Loading method: Dynamically loaded via {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader}.
 *
 * @see egps2.modulei.IModuleLoader
 * @see XXXMainFace
 * @author eGPS Dev Team
 * @since 2.1
 */

import javax.swing.JComponent;

import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;
import egps2.modulei.ModuleClassification;
import egps2.panels.InformationPanelFactory;

/**
 * IndependentModuleLoader supports the plugin/template system for extending eGPS.
 */
public class IndependentModuleLoader implements IModuleLoader{

	@Override
	public JComponent getEnglishDocument() {
		return null;
	}

	@Override
	public JComponent getChineseDocument() {
		return new InformationPanelFactory().getUnimplementedInformationPanel();
	}

	@Override
	public String getTabName() {
		return "Fast Module Loader";
	}

	@Override
	public String getShortDescription() {
		return "";
	}

	@Override
	public ModuleFace getFace() {
		return new XXXMainFace(this);
	}

	@Override
	public int[] getCategory() {
		int[] ret = ModuleClassification.getOneModuleClassification(
				ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
				ModuleClassification.BYAPPLICATION_VISUALIZATION_INDEX
		);
		return ret;
	}
}
