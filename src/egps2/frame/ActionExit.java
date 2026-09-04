package egps2.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import egps2.UnifiedAccessPoint;

/**
 * "退出"菜单动作，安全退出eGPS应用程序。
 * Exit menu action for safely exiting eGPS application.
 *
 * <p>此动作响应用户点击"退出"菜单项或按F4快捷键，执行应用程序的安全退出流程。
 * This action responds to user clicking "Exit" menu item or pressing F4 shortcut key, executing safe application exit process.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>安全退出应用程序 - Safe application exit</li>
 *   <li>保存应用程序状态 - Save application state</li>
 *   <li>清理资源 - Clean up resources</li>
 *   <li>快捷键：F4 - Shortcut: F4</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @see MyFrame#exitSoftware()
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionExit extends AbstractSoftAction{

	private static final long serialVersionUID = -3518548920624908609L;
	public ActionExit() {
		putValue(Action.NAME, "Exit");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_F4);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	
		setIcons("exit.png");

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		
		instanceFrame.exitSoftware();
		
	}

}
