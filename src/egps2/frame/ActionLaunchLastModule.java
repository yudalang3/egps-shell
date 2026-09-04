package egps2.frame;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.KeyStroke;

/**
 * "Launch Last Module"菜单动作，重新打开最后启动的模块。
 * Launch Last Module action, reopens the last launched module.
 *
 * <p>此动作响应用户点击"Launch Last Module"菜单项或按下Ctrl+R快捷键，
 * 自动加载上次打开的模块，方便用户快速返回工作状态。
 * This action responds to user clicking "Launch Last Module" menu item or pressing Ctrl+R shortcut,
 * automatically loading the last opened module for quick return to work state.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>读取上次启动的模块记录 - Read last launched module record</li>
 *   <li>自动加载该模块 - Automatically load that module</li>
 *   <li>支持快捷键 Ctrl+R - Support keyboard shortcut Ctrl+R</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @see MainFrameProperties#launchLastOpenedModule()
 * @author eGPS Dev Team
 * @since 2.2
 */
@SuppressWarnings("serial")
public class ActionLaunchLastModule extends AbstractSoftAction {

	public ActionLaunchLastModule() {
		putValue(SHORT_DESCRIPTION, "Launch the last opened module");
		putValue(NAME, "Launch Last Module");

		// Set Ctrl+R keyboard shortcut
		KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK);
		putValue(ACCELERATOR_KEY, keyStroke);

		try {
			setIconBySvg("recover.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MainFrameProperties.launchLastOpenedModule();
	}

}
