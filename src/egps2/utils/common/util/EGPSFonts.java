package egps2.utils.common.util;

import java.awt.Font;

/**
 * Note: This is only for test when developing a new module! You can't used to
 * building a module! Because this is not the effective way to produce a
 * random Color!!
 *
 * @author yudalang
 *
 */
public class EGPSFonts {

	private static StringBuilder sBuilder;

	/**
	 * Creates a font with the name, style and size values. Style can be PLAIN,
	 * BOLD, ITALIC, or BOLD+ITALIC. 0 for PLAIN; 1 for BOLD; 2 for ITALIC E.g
	 * Arial,0,12
	 * 
	 * @param str: Arial,0,12
	 * @return
	 */
	public static Font parseFont(String str) {
		String[] split = str.split(",");
		if (split.length < 3) {
			throw new IllegalArgumentException("Please input string like Arial,0,12");
		}

		int style = Integer.parseInt(split[1]);
		int size = Integer.parseInt(split[2]);

		return new Font(split[0], style, size);
	}

	public static String codeFont(Font font) {
		StringBuilder sBuilder = getsBuilder();
		sBuilder.setLength(0);
		sBuilder.append(font.getFamily()).append(",");
		sBuilder.append(font.getStyle()).append(",");
		sBuilder.append(font.getSize());
		return sBuilder.toString();
	}

	public static StringBuilder getsBuilder() {
		if (sBuilder == null) {
			sBuilder = new StringBuilder(256);
		}
		return sBuilder;
	}
	
}
