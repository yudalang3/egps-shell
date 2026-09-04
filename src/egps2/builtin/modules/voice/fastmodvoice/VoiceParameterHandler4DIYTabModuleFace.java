package egps2.builtin.modules.voice.fastmodvoice;

import egps2.frame.ComputationalModuleFace;
import egps2.frame.DefaultParamsAssignerAndParserHandler4VOICE;
import egps2.builtin.modules.voice.VersatileOpenInputClickAbstractGuiBase;
import egps2.modulei.RunningTask;

/**
 * 为 TabModuleFaceOfVoice 创建的VOICE输入参数处理器。用户鼠标点击之后的处理事件在这里。
 * 这个类只会在 TabModuleFaceOfVoice 被初始化
 */
public class VoiceParameterHandler4DIYTabModuleFace extends VersatileOpenInputClickAbstractGuiBase {

    // 创建默认输入参数处理器实例，这个实例和 ComputationalModuleFace中用的是一样的。这个类和 ComputationalModuleFace是相互引用的。
	public final DefaultParamsAssignerAndParserHandler4VOICE defaultVoiceInputParamHandler = new DefaultParamsAssignerAndParserHandler4VOICE();
    // 注意对于 一个VOICE来说 ， ComputationalModuleFace和它是同一个实例。但是对于SubTab来说就不一样了。
    private final TabModuleFaceOfVoice tabModuleFaceOfVoice;
    private ComputationalModuleFace computationalModuleFace;

    public VoiceParameterHandler4DIYTabModuleFace(TabModuleFaceOfVoice tabModuleFaceOfVoice) {
        this.tabModuleFaceOfVoice = tabModuleFaceOfVoice;
        this.computationalModuleFace = tabModuleFaceOfVoice;
    }

    public void setComputationalModuleFace(ComputationalModuleFace computationalModuleFace) {
        this.computationalModuleFace = computationalModuleFace;
    }

    @Override
    public String getExampleText() {
		return defaultVoiceInputParamHandler.getExampleString(tabModuleFaceOfVoice);
    }

    @Override
    public void execute(String inputs) throws Exception {
        RunningTask runningTask = new RunningTask() {
            @Override
            public int processNext() throws Exception {
				/*
				 * 如果要命令行运行的话，这里可以写一个CLI的 Parser
				 */
				VoiceParameterParser parser = defaultVoiceInputParamHandler.getParameterParser();
				OrganizedParameterGetter organizedParameterGetter = parser.getOrganizedParameterGetter(inputs);
				tabModuleFaceOfVoice.execute(organizedParameterGetter);
                return PROGRESS_FINSHED;
            }

            @Override
            public boolean isTimeCanEstimate() {
                return false;
            }

        };
        /*
         * 有可能在DIYTools里面，也有可能是 DockableTools里面或者是在 VOICE-floating里面
         */
        if (computationalModuleFace == null) {
            tabModuleFaceOfVoice.registerRunningTask(runningTask);
        } else {
            computationalModuleFace.registerRunningTask(runningTask);
        }
    }


}
