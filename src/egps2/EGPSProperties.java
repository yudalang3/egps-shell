package egps2;

import java.awt.Color;
import java.io.File;

import egps2.modulei.ModuleVersion;


/**
 * 为了避免与JDK中的 Properties类重名，改成 EGPSProperties。
 * 查看 edu.sinh.beauty.unisoft.frame.ActionAbout 类中的版本变更
 * @author yudal
 *
 */
public class EGPSProperties {

	public final static String PROPERTIES_DIR_NAME = "config";
	public final static String PROPERTIES_DIR;
	public final static String JSON_DIR;
	public final static String BIO_DATA_DIR;
	public final static String EGPS_MODULE_CONFIG_PATH;
	public final static String EGPS_WRAPPER_PROGRAM_CONFIG_PATH;

	// ========== Website and Homepage URLs ==========
	public final static String EGPS_HOMEPAGE = "http://www.egps-software.net/";
	public final static String DEFAULT_WEB_LINK = "http://www.egps-software.org";
	public final static String EVOLGEN_LAB_WEBSITE = "http://www.picb.ac.cn/evolgen/";
	public final static String EVOLGEN_LAB_NAME = "EvolGen";

	// ========== Version Information ==========
	/**
	 * 这样写防止出错，因为有时候一不留神会把 Version: 这个也覆盖掉
	 */
	public static final String EGPS_VERSION_HEADER = "Version: ";
	public static final String EGPS_VERSION = EGPS_VERSION_HEADER.concat(new Version().toString());

	/**
	 * Mainframe core module version (shared by all built-in core modules).
	 *
	 * <p>This version is used by all Mainframe core modules including:
	 * <ul>
	 *   <li>File Manager</li>
	 *   <li>Module Gallery (iTools Manager)</li>
	 *   <li>Text Editors (Low/Large)</li>
	 *   <li>Demo modules (Dockable, Floating, HandyTools)</li>
	 * </ul>
	 *
	 * <p>Version format follows Semantic Versioning 2.0.0:
	 * <ul>
	 *   <li>2.2.0: Current version with new module versioning system</li>
	 * </ul>
	 *
	 * @see ModuleVersion
	 * @see egps2.modulei.IModuleLoader#getVersion()
	 * @since 2.2
	 */
	public static final ModuleVersion MAINFRAME_CORE_VERSION = new ModuleVersion(0, 0, 1);

	// ========== Team and Attribution ==========
	public final static String DEFAULT_TEAM_TITLE = "eGPS developers team:";

	// ========== Module Default Names ==========
	public static final String MODULE_INTRO_NAME = "Introduction";
	public static final String BOOKMARK_DEFAULT_CATEGORY = "DefaultCategory";
	public static final String BOOKMARK_EXAMPLES_CATEGORY = "Examples";

	static {
		String userHomeDir = System.getProperty("user.home");
		File dir = new File(userHomeDir, ".egps2");
		PROPERTIES_DIR = new File(dir, PROPERTIES_DIR_NAME).getAbsolutePath().replace("\\", "/");
		JSON_DIR = new File(dir, "config/jsonData").getAbsolutePath().replace("\\", "/");
		BIO_DATA_DIR = new File(dir, "config/bioData").getAbsolutePath().replace("\\", "/");
		EGPS_MODULE_CONFIG_PATH = new File(dir, "config/egps2.loading.module.config.txt").getAbsolutePath()
				.replace("\\", "/");
		EGPS_WRAPPER_PROGRAM_CONFIG_PATH = new File(dir, "config/software/egps.wrap.software.txt").getAbsolutePath()
				.replace("\\", "/");

	}


	public static StringBuilder getSpecificationHeader() {
		StringBuilder ret = new StringBuilder();
		ret.append("# Lines start with '#' is annotation, start with '$' is parameter. Blank line will be ignored. ");
		ret.append("For table-like data, each column is separated with tab key.\n");
		ret.append("# Creates a color with red, green, blue, and alpha values in the range (0 - 255). E.g 0,0,0,255. Or employ the hex-string, like #FF0AFF, #FFFFFFFF\n");
		ret.append("# Creates a font with name, style and size values. Style can be PLAIN(0), BOLD(1), ITALIC(2), or BOLD+ITALIC(3). E.g Arial,0,12\n");
		ret.append("\n");
		
		return ret;
	}
	
	
	
	public static Color dragFrameColor = new Color(0, 0, 182, 50);

	public static Color selectedColor = new Color(24, 113, 255, 58);

}
