package egps2.builtin.modules.voice.bean;

/**
 * Key对应的Value已经，提示信息。这个提示信息会 用# 字符作为注释行。
 */
public class VoiceValueParameterBean {
	String annotation;
	String value;

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
