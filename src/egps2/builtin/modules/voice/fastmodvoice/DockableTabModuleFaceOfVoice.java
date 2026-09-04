package egps2.builtin.modules.voice.fastmodvoice;

import egps2.EGPSProperties;
import egps2.frame.ComputationalModuleFace;
import egps2.modulei.ModuleClassification;
import egps2.modulei.ModuleVersion;

/**
 * 抽象类 DockableTabModuleFaceOfVoice 继承自JPanel，提供了子选项卡基础模块的接口.
 * 该类的主要目的是为继承它的类提供一个基础框架，包括如何获取简短描述、选项卡名称以及可选的图标.
 *
 * 警告：initializeGraphics() 这个方法不要忘记调用，否则子选项卡的GUI内容不会被正确加载。
 */
@SuppressWarnings("serial")
public abstract class DockableTabModuleFaceOfVoice extends TabModuleFaceOfVoice {

    //protected DefaultParamsAssignerAndParserHandler4VOICE voiceInputParameterHandler = new DefaultParamsAssignerAndParserHandler4VOICE();

    protected ComputationalModuleFace computationalModuleFace;

    public DockableTabModuleFaceOfVoice(ComputationalModuleFace cmf) {
        super();
        this.computationalModuleFace = cmf;
    }


    /**
     * 获取该选项卡的分类，默认为如下。
     * 相当于是一个 Mock 对象，用于不需要分类的子Tab。
     * @return
     */
    @Override
    public int[] getCategory() {
        int[] ret = ModuleClassification.getOneModuleClassification(
                ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
                ModuleClassification.BYAPPLICATION_GENOMICS_INDEX,
                ModuleClassification.BYCOMPLEXITY_LEVEL_1_INDEX,
                ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER
        );
        return ret;
    }

    @Override
    protected void initializeTheVoiceTools() {
        super.initializeTheVoiceTools();
        voiceTools.setComputationalModuleFace(computationalModuleFace);
    }

    /**
     * The DockableTab is not need the version, because the IndependentModuleLoader is enough.
     * If user really need a version, implement it.
     * @return
     */
    @Override
    public ModuleVersion getVersion() {
        return EGPSProperties.MAINFRAME_CORE_VERSION;
    }

}

