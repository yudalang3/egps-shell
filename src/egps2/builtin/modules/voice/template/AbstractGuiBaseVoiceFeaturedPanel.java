package egps2.builtin.modules.voice.template;

import egps2.builtin.modules.voice.VersatileOpenInputClickAbstractGuiBase;
import egps2.frame.DefaultParamsAssignerAndParserHandler4VOICE;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.builtin.modules.voice.fastmodvoice.VoiceParameterParser;

/**
 * 和父类不同，这里提供了更加高级的一些封装的功能：
 * 将获取示例数据和执行按钮点击之后的动作两个方法实现了。
 * 用户只需要编写软件有什么参数，以及如何获取所需参数并执行就行了。
 *
 * 这个类的功能相当于是将输入框和执行按钮封装起来，让用户只需要关注自己需要什么参数，以及如何执行就行了。
 *
 * This = AbstractParamsAssignerAndParser4VOICE + VersatileOpenInputClickAbstractGuiBase.
 * 相当于是GUI + 输出所需参数与参数的解析器 的组合。
 *
 * 注意：使用的时候请继承该类，重写setParameter()方法，并实现execute()方法即可。
 *
 * @author yudalang
 */
public abstract class AbstractGuiBaseVoiceFeaturedPanel extends VersatileOpenInputClickAbstractGuiBase {
    private final VoiceParameterParser voiceParameterParser;
	protected final DefaultParamsAssignerAndParserHandler4VOICE defaultVoiceInputParameterHandler = new DefaultParamsAssignerAndParserHandler4VOICE();

    public AbstractGuiBaseVoiceFeaturedPanel() {
        super();
        voiceParameterParser = defaultVoiceInputParameterHandler.getParameterParser();
		// 不能在构造函数中设置参数，因为此时子类还没有初始好，所以需要延迟设置参数。在模块需要使用的是时候调用，getExampleText()方法调用。
        //setParameter(defaultVoiceInputParameterHandler);
    }
    @Override
    protected String getExampleText() {
        setParameter(defaultVoiceInputParameterHandler);
//        It is impossible, because this is never be the instance of IModuleSignature
		// Users can re-write this method.
//		if (this instanceof IModuleSignature) {
//			return defaultVoiceInputParameterHandler.getExampleString((IModuleSignature) this);
//		} else {
//			return defaultVoiceInputParameterHandler.getExampleString();
//		}

		return defaultVoiceInputParameterHandler.getExampleString();

    }

    @Override
    protected void execute(String inputs) throws Exception {
        OrganizedParameterGetter organizedParameterGetter = voiceParameterParser.getOrganizedParameterGetter(inputs);
        execute(organizedParameterGetter);
    }

    /**
     * 配置模块需要什么参数
     * @param mapProducer 框架自己会提供的参数解析器
     */
    protected abstract void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer);

    /**
     * 执行该模块的功能.
     *
     * @param o 一个参数解析器，用于解析用户输入的参数.
     * @throws Exception 如果执行过程中发生任何异常，则抛出该异常.
     */
    protected abstract void execute(OrganizedParameterGetter o) throws Exception;
}
