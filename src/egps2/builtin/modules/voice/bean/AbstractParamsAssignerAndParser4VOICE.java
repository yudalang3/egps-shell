package egps2.builtin.modules.voice.bean;

import java.util.LinkedHashMap;
import java.util.Map;

import egps2.builtin.modules.voice.fastmodvoice.VoiceParameterParser;
import top.signature.IModuleSignature;

/**
 * 抽象类MapProducer4VOICE用于处理参数解析和生成示例数据
 * 它维护了一个字符串键和ValueParameterBean对象的映射，并提供了添加条目、获取映射、生成示例字符串等功能
 * 
 * Assigner就是添加模块支持什么参数， Parser就是把用户输入的字符串解析成数据结构
 */
public abstract class AbstractParamsAssignerAndParser4VOICE {

	/**
	 * 因为这个所需的参数需要考虑参数的顺序，
	 */
	protected LinkedHashMap<String, VoiceValueParameterBean> requiredParams = new LinkedHashMap<>();
	protected VoiceParameterParser voiceParameterParser = new VoiceParameterParser();
	protected VoiceExampleGenerator voiceExampleGenerator = new VoiceExampleGenerator();

	public AbstractParamsAssignerAndParser4VOICE() {

	}

	/**
	 * 向parserMapBean中添加一个键值对条目，并返回该条目
	 *
	 * Important, for category of parameters, please use %1, %2 and %3 and so on.
	 * <pre>
	 * The specific key values:
	 * % Parameters category: If your have multiple categories, please use %1, %2 and %3 and so on.
	 * ^ Advanced parameters line separator: only 1 for VOICE-GUI
	 * &#64; The module information
	 * </pre>
	 *
	 * @param key   要添加的条目键
	 * @param value 要添加的条目值
	 * @param tip   该条目的注释信息
	 * @return 添加的ValueParameterBean对象
	 */
	public VoiceValueParameterBean addKeyValueEntryBean(String key, String value, String tip) {
		VoiceValueParameterBean ret = new VoiceValueParameterBean();
		ret.setValue(value);
		ret.setAnnotation(tip);

		requiredParams.put(key, ret);
		return ret;
	}
	/**
	 * 将给定的键值对条目豆对象添加到已有的requiredParams映射中
	 * 该方法用于扩展requiredParams映射，通过将另一个Map对象alreadyBean的内容合并进来实现
	 *
	 * @param alreadyBean 包含键值对条目豆对象的Map对象，将被添加到requiredParams中
	 */
	public void addKeyValueEntryBean(LinkedHashMap<String, VoiceValueParameterBean> alreadyBean) {
	    requiredParams.putAll(alreadyBean);
	}

	
	public LinkedHashMap<String, VoiceValueParameterBean> getRequiredParams() {
		return requiredParams;
	}

	public String getExampleString() {
        return voiceExampleGenerator.generateExample(requiredParams);
	}

	public String getExampleString(IModuleSignature moduleLoader) {
		return voiceExampleGenerator.generateExampleWithLoader(requiredParams, moduleLoader);
	}

	/**
	 * 将给定的字符串转换为键值对字符串映射
	 *
	 * @param str 输入的字符串，用于生成键值对映射
	 * @return 返回一个Map对象，包含键值对映射
	 */
	public Map<String, String> getKeyValueStringMap(String str) {
        return voiceParameterParser.parseLongSingleString4simplifiedMap(str);
	}

	public VoiceExampleGenerator getExampleGenerator() {
		return voiceExampleGenerator;
	}

	public VoiceParameterParser getParameterParser() {
		return voiceParameterParser;
	}
}
