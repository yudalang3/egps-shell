package egps2.builtin.modules.voice.bean;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;

import top.signature.IModuleSignature;

/**
 * Function same as its name, only generate the example string.
 */
public class VoiceExampleGenerator {

    VoiceExampleGenerator() {

    }

	public String generateExampleWithLoader(Map<String, VoiceValueParameterBean> parserMapBean,
			IModuleSignature moduleLoader) {

		StringBuilder sb = new StringBuilder();

        Set<Entry<String, VoiceValueParameterBean>> entrySet = parserMapBean.entrySet();
		Iterator<Entry<String, VoiceValueParameterBean>> iterator = entrySet.iterator();
		String moduleID = null;
		while (iterator.hasNext()) {
			Entry<String, VoiceValueParameterBean> entry = iterator.next();
			String key = entry.getKey();
			if (key.charAt(0) == '@') {
				moduleID = entry.getValue().getValue();
				iterator.remove();
				break;
			}
		}

		if (moduleID == null) {
			if (moduleLoader != null) {
				sb.append("### Module name: ").append(moduleLoader.getTabName());
				sb.append(" ");
			}
		} else {
			sb.append("### Module ID: ").append(moduleID);
			sb.append(" ");
		}

		sb.append("# See the right question label for help.###\n");

		for (Entry<String, VoiceValueParameterBean> entry : entrySet) {
            VoiceValueParameterBean value = entry.getValue();

			if (Strings.isNotEmpty(value.getAnnotation())) {
                sb.append("# ");
                sb.append(value.getAnnotation());
                sb.append("\n");
            }

            String key = entry.getKey();
            String value2 = value.getValue();

			char firstChar = key.charAt(0);
			if ('%' == firstChar) {
				sb.append("## --- ");
				sb.append(value2);
				sb.append(" ------------------\n");
			} else if ('^' == firstChar) {
				sb.append(
						"##===================================================================================\n");
				sb.append(
						"##======================== Advanced parameters ===============================\n");
				sb.append(
						"##===================================================================================\n");
			} else {
				sb.append('$').append(key);
				sb.append('=').append(value2);

				sb.append("\n\n");
			}

        }

        return sb.toString();
	}

	public String generateExample(Map<String, VoiceValueParameterBean> parserMapBean) {
		String ret = generateExampleWithLoader(parserMapBean, null);
		return ret;
    }


}
