package egps2.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import egps2.UnifiedAccessPoint;
import egps2.panels.ModuleInspector;

/**
 * "搜索"菜单动作，打开模块搜索和浏览对话框。
 * Search menu action opening module search and browsing dialog.
 *
 * <p>此动作响应用户点击"搜索"菜单项或按Ctrl/Cmd+F快捷键，打开模块检查器对话框用于搜索和浏览已加载的模块。
 * This action responds to user clicking "Search" menu item or pressing Ctrl/Cmd+F shortcut key, opening module inspector dialog for searching and browsing loaded modules.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>搜索已加载模块 - Search loaded modules</li>
 *   <li>浏览模块列表 - Browse module list</li>
 *   <li>快速定位模块 - Quick module location</li>
 *   <li>快捷键：Ctrl/Cmd+F - Shortcut: Ctrl/Cmd+F</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @see ModuleInspector
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionSearch extends AbstractSoftAction {

	private static final long serialVersionUID = -9154663336342661233L;
	public ActionSearch() {
		putValue(NAME, "Search");
		putValue(MNEMONIC_KEY, KeyEvent.VK_F);
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		setIcons("search_long.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		ModuleInspector moduleInspector = new ModuleInspector(instanceFrame);
		moduleInspector.display();
	}

}
