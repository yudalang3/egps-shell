package egps2.utils.common.util;

import java.io.InputStream;

import javax.swing.ImageIcon;

import egps2.UnifiedAccessPoint;

/**
 * Provides a single static method for loading icons as resources.
 */
public class EGPSShellIcons {
	/**
	 * Load an icon as a resource from the "images" directory.
	 */
	public static ImageIcon get(String name) {

    	return new ImageIcon(EGPSShellIcons.class.getClassLoader().getResource("resources/images/" + name));
	}

	/**
	 * 这个Icon很受欢迎，所以单独拿出来
	 * @return
	 */
	public static ImageIcon getHelpIcon() {
		return new ImageIcon(EGPSShellIcons.class.getClassLoader().getResource("resources/images/help_blue.png"));
	}
	
	public static InputStream getVectorGraphResourceAsStream(String name) {
		InputStream resource = UnifiedAccessPoint.class.getResourceAsStream("/resources/vectorgraph/" + name);
		return resource;
	}
}
