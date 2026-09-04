package egps2.builtin.modules.lowtextedi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import egps2.EGPSProperties;
import egps2.UnifiedAccessPoint;
import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.modulei.ModuleClassification;
import egps2.modulei.ModuleVersion;
import egps2.panels.dialog.SwingDialog;
import org.apache.commons.io.FileUtils;

/**
 * 轻量文本编辑器模块加载器，提供简单快速的文本编辑功能。
 * Lightweight text editor module loader providing simple and fast text editing functionality.
 *
 * <p>此模块实现 {@link IModuleLoader} 接口，为小型文本文件提供轻量级编辑器。
 * This module implements {@link IModuleLoader} interface, providing a lightweight editor for small text files.
 *
 * <p><strong>模块信息：</strong>
 * Module information:
 * <ul>
 *   <li>Tab 名称：Low volume text editor</li>
 *   <li>功能：快速编辑小型文本文件，启动速度快，占用资源少</li>
 *   <li>限制：适用于小于10MB的文本文件</li>
 *   <li>依赖：无特殊外部依赖</li>
 *   <li>入口面板：{@link TextViewMainFace}</li>
 * </ul>
 *
 * <p><strong>与大文本编辑器的区别：</strong>
 * Differences from large text editor:
 * <br>此编辑器针对小文件优化，启动快但功能较少；大文本编辑器功能丰富但启动较慢。
 * This editor is optimized for small files with fast startup but fewer features; large text editor has rich features but slower startup.
 *
 * <p>加载方式：通过 {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader} 动态加载。
 * Loading method: Dynamically loaded via {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader}.
 *
 * @see IModuleLoader
 * @see TextViewMainFace
 * @see egps2.builtin.modules.largetextedi.IndependentModuleLoader
 * @author eGPS Dev Team
 * @since 2.1
 */
public class IndependentModuleLoader implements IModuleLoader {

    ImportDataInfo importDataInfo;
    private String tabName = "Low volume text editor";

    public void setImportDataInfo(String content, File file) {
        importDataInfo = new ImportDataInfo();

        if (content == null) {
            if (file == null) {
                throw new IllegalArgumentException("Content and file cannot both be null.");
            } else {
                try {
                    String s = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
					importDataInfo.setContent(s);
					importDataInfo.setInputFile(file);
                } catch (Exception e) {
					SwingDialog.showErrorMSGDialog("Error", "Cannot read file: " + e.getMessage());
					throw new RuntimeException(e);
                }
            }
        } else {
            if (file == null) {
				importDataInfo.setContent(content);
            } else {
				importDataInfo.setContent(content);
				importDataInfo.setInputFile(file);
            }
        }

        if (importDataInfo.getContent().length() > 80000){
            UnifiedAccessPoint.getInstanceFrame().prompt("The content is more than 80000 characters. Please wait a second to load.");
        }
        tabName = assignTabName(file);
    }

    private String assignTabName(File file) {
        String defaultName = "Low volume text editor";
        String ret = null;
        if (file == null){
            ret = defaultName;
        }else {
            ret = file.getName();
            if (ret.length() > defaultName.length()){
                ret = ret.substring(0, defaultName.length() - 3).concat("...");
            }
        }

        return ret;
    }

    @Override
    public String getTabName() {
        return tabName;
    }

    @Override
    public String getShortDescription() {
        return "This is the convenient module to view and edit the low volume text.";
    }

    @Override
    public IconBean getIcon() {
        InputStream resourceAsStream = getClass().getResourceAsStream("/images/maincore/Low volume text editor.svg");
        IconBean iconBean = new IconBean();
        iconBean.setSVG(true);
        iconBean.setInputStream(resourceAsStream);

        return iconBean;
    }

    @Override
    public ModuleFace getFace() {
        if (importDataInfo == null) {
            setImportDataInfo("",null);
        }
        TextViewMainFace textViewMainFace = new TextViewMainFace(this, importDataInfo);
        // 初始化之后这个content内容就清空，防止影响下一次加载。因为这个 IModuleLoader对象在软件启动的时候会加载一次，后面一直存在
        importDataInfo = null;
        return textViewMainFace;
    }

    @Override
    public int[] getCategory() {
        int[] ret = ModuleClassification.getOneModuleClassification(
                ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
                ModuleClassification.BYAPPLICATION_COMMON_MODULE_INDEX, ModuleClassification.BYCOMPLEXITY_LEVEL_2_INDEX,
                ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER);
        return ret;
    }



	@Override
	public ModuleVersion getVersion() {
		return EGPSProperties.MAINFRAME_CORE_VERSION;
	}

}
