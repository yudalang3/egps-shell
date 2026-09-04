package egps2.plugin.manager;

import java.io.File;
import java.util.List;
import java.util.Optional;

import utils.string.EGPSStringUtil;

/**
 * PluginProperty supports the plugin/template system for extending eGPS.
 */
public class PluginProperty {

	private String launchClass;

	private String[] dependentJars;

	private File jarFile;

	public PluginProperty(List<String> lines) {
		for (String string : lines) {
			string = string.trim();
			if (string.isEmpty() || string.charAt(0) == '#') {
				continue;
			}

			// Only support new format: key=value
			int equalsIndex = string.indexOf("=");

			if (equalsIndex != -1) {
				String key = string.substring(0, equalsIndex).trim();
				String value = string.substring(equalsIndex + 1).trim();

				if (key.equals("launchClass")) {
					launchClass = value;
				} else if (key.equals("dependentJars")) {
					dependentJars = EGPSStringUtil.split(value, ';');
				}
			}
		}
	}

	/**
	 * @return the launchClass
	 */
	public String getLaunchClass() {
		return launchClass;
	}

	/**
	 * @return the dependentJars
	 */
	public Optional<String[]> getDependentJars() {
		return Optional.ofNullable(dependentJars);
	}

	/**
	 * @return the jarFile
	 */
	public File getJarFile() {
		return jarFile;
	}

	/**
	 * @param jarFile the jarFile to set
	 */
	public void setJarFile(File jarFile) {
		this.jarFile = jarFile;
	}

}
