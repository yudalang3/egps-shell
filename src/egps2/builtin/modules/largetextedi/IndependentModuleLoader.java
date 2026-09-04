package egps2.builtin.modules.largetextedi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import egps2.EGPSProperties;
import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.modulei.ModuleClassification;
import egps2.modulei.ModuleVersion;

/**
 * 大文本编辑器模块加载器，为大容量文本文件提供高效的查看和编辑功能。
 * Large text editor module loader providing efficient viewing and editing for large volume text files.
 *
 * <p>此模块实现 {@link IModuleLoader} 接口，为处理大型文本文件提供专门优化的编辑器。
 * This module implements {@link IModuleLoader} interface, providing a specially optimized editor for handling large text files.
 *
 * <p><strong>模块信息：</strong>
 * Module information:
 * <ul>
 *   <li>Tab 名称：Large volume text view</li>
 *   <li>功能：查看和编辑大容量文本文件，支持行号、查找、替换、语法高亮等</li>
 *   <li>优化：针对大文件进行内存优化，支持数MB甚至GB级文本文件</li>
 *   <li>依赖：无特殊外部依赖</li>
 *   <li>入口面板：{@link TextEditorMain}</li>
 * </ul>
 *
 * <p>加载方式：通过 {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader} 动态加载。
 * Loading method: Dynamically loaded via {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader}.
 *
 * @see IModuleLoader
 * @see TextEditorMain
 * @author eGPS Dev Team
 * @since 2.1
 */
public class IndependentModuleLoader implements IModuleLoader{

	String path = EGPSProperties.JSON_DIR.concat("/temp.blank.txt");
	
	
	@Override
	public String getTabName() {
		return "Large volume text view";
	}

	@Override
	public String getShortDescription() {
		return "View and edit the large volume text file.";
	}

	@Override
	public ModuleFace getFace() {
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new TextEditorMain(this,file);
	}
	
	@Override
	public IconBean getIcon() {
		InputStream resourceAsStream = getClass().getResourceAsStream("/images/maincore/Large volume text view.svg");
		IconBean iconBean = new IconBean();
		iconBean.setSVG(true);
		iconBean.setInputStream(resourceAsStream);

		return iconBean;
	}
	
	@Override
	public int[] getCategory() {
		int[] ret = ModuleClassification.getOneModuleClassification(
				ModuleClassification.BYFUNCTIONALITY_PROFESSIONAL_COMPUTATION_INDEX,
				ModuleClassification.BYAPPLICATION_COMMON_MODULE_INDEX,
				ModuleClassification.BYCOMPLEXITY_LEVEL_5_INDEX,
				ModuleClassification.BYDEPENDENCY_COMPUTATIONAL_MECHANISM_EMPLOYED
		);
		return ret;
	}


	@Override
	public ModuleVersion getVersion() {
		return EGPSProperties.MAINFRAME_CORE_VERSION;
	}

}
