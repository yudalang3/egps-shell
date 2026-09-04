package egps2.frame;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import egps2.utils.EGPSIconUtil;

/**
 * Swing Action抽象基类，为eGPS框架的所有工具栏和菜单动作提供通用功能。
 * Abstract base class for Swing Actions providing common functionality for all toolbar and menu actions in eGPS framework.
 *
 * <p>此类继承自{@link javax.swing.AbstractAction}，为子类提供图标加载和工具提示管理功能。
 * This class extends {@link javax.swing.AbstractAction}, providing icon loading and tooltip management functionality for subclasses.
 *
 * <p><strong>主要功能：</strong>
 * Main functionality:
 * <ul>
 *   <li>PNG/SVG图标加载 - PNG/SVG icon loading</li>
 *   <li>图标尺寸调整 - Icon size adjustment</li>
 *   <li>工具提示生成 - Tooltip generation</li>
 * </ul>
 *
 * <p><strong>使用指南：</strong>
 * Usage guide:
 * <br>子类需实现{@link java.awt.event.ActionListener#actionPerformed}方法，并在构造函数中设置图标和描述。
 * Subclasses need to implement {@link java.awt.event.ActionListener#actionPerformed} method and set icon and description in constructor.
 *
 * @see javax.swing.AbstractAction
 * @see ActionsManager
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public abstract class AbstractSoftAction extends AbstractAction {

	protected void setIcons(String name) {
		URL resource = getClass().getResource("/images/toolbar/" + name);
		ImageIcon imageIcon = new ImageIcon(resource);
		putValue(SMALL_ICON, imageIcon);
//		setIconsWithSize(name, 24, 24);
	}

	protected void setIconBySvg(String name) throws IOException {
		InputStream resource = getClass().getResourceAsStream("/images/toolbar/".concat(name));
		ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
		putValue(SMALL_ICON, imageIcon);
	}

	protected void setIconsWithSize(String name,int x,int y) {
		URL resource = getClass().getResource("/images/toolbar/" + name);
		ImageIcon imageIcon = new ImageIcon(resource);
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(x, y, Image.SCALE_SMOOTH));
		putValue(SMALL_ICON, imageIcon);
	}
	
	protected String getShortDescriptionString() {
		 String ret = "<html><body bgcolor=\"#AABBFF\">In case you thought that tooltips had to be<p>" +
                 "boring, one line descriptions, the <font color=blue size=+2>Swing!</font> team<p>" +
                 "is happy to shatter your illusions.<p>" +
                 "In Swing, you can use HTML to <ul><li>Have Lists<li><b>Bold</b> text<li><em>emphasized</em>" +
                 "text<li>text with <font color=red>Color</font><li>text in different <font size=+3>sizes</font>" +
                 "<li>and <font face=AvantGarde>Fonts</font></ul>Oh, and they can be multi-line, too.</body></html>";
		 
		 return ret;
	}
	
	
}
