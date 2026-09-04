package egps2.frame;

import egps2.EGPSProperties;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * "主页"菜单动作，在默认浏览器中打开软件官方网站。
 * Home page menu action opening software official website in default browser.
 *
 * <p>此动作响应用户点击"主页"菜单项，使用系统默认浏览器打开eGPS官方网站。
 * This action responds to user clicking "Home page" menu item, opening eGPS official website using system default browser.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>打开官方网站 - Open official website</li>
 *   <li>使用系统默认浏览器 - Use system default browser</li>
 *   <li>网址：{@value egps2.EGPSProperties#EVOLGEN_LAB_WEBSITE}</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionHomePage extends AbstractSoftAction {

	private static final long serialVersionUID = -9154663336342661233L;

	public ActionHomePage() {
		putValue(NAME, "Home page");
		putValue(SHORT_DESCRIPTION, "The website of this software.");


		try {
			setIconBySvg("help/Home page.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		boolean desktopSupported = Desktop.isDesktopSupported();
		if (desktopSupported) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(EGPSProperties.EVOLGEN_LAB_WEBSITE));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
		}

	}

	
	@Override
	protected String getShortDescriptionString() {
		
		String[] ret = { 
				"<html><body>",
				"Show some launch statistics about this module, like launch times, unlocked features<br>",
				"<font color=blue size=+1>Including:</font>",
				"<ul>",
				"<li>The launch frequency of module.<br>",
				"<li>Features that unlocked by user.<br>",
				"</ul>",
				"</body></html>" };

		return MainFrameProperties.wrapStringArraysAsString(ret);
	}

}
