package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JDialog;

import egps2.Launcher;
import egps2.UnifiedAccessPoint;
import egps2.panels.StdErrConsolePanel;

/**
 * "错误控制台"菜单动作，显示标准错误输出控制台（开发模式）。
 * Error console menu action displaying standard error output console (development mode).
 *
 * <p>此动作响应用户点击"错误控制台"菜单项，打开显示标准错误输出和日志信息的控制台窗口，仅在开发模式下可用。
 * This action responds to user clicking "Error Console" menu item, opening a console window displaying standard error output and log information, available only in development mode.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>显示标准错误输出 - Display standard error output</li>
 *   <li>实时日志查看 - Real-time log viewing</li>
 *   <li>调试信息输出 - Debug information output</li>
 *   <li>仅开发模式可用 - Available only in development mode</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @see StdErrConsolePanel
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionStdErrConsole extends AbstractSoftAction {

	private static final long serialVersionUID = -9154663336342661233L;
	private String actionName;
	private JDialog dialog;
	private StdErrConsolePanel consolePanel;

	public ActionStdErrConsole() {
		actionName = "Error Console";
		putValue(NAME, actionName);
		putValue(SHORT_DESCRIPTION, getShortDescriptionString());

		try {
			setIconBySvg("help/Error Console.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (Launcher.isDev) {
			
		} else {
			consolePanel = new StdErrConsolePanel();
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (consolePanel != null) {
			getDialog().setSize(950, 800);
			dialog.setLocationRelativeTo(UnifiedAccessPoint.getInstanceFrame());
			dialog.setVisible(true);
		}
	}

	public JDialog getDialog() {
		if (dialog == null) {
			dialog = new JDialog(UnifiedAccessPoint.getInstanceFrame(), true);
			dialog.setTitle(actionName);
			dialog.add(consolePanel);
		}
		return dialog;
	}

	@Override
	protected String getShortDescriptionString() {

		String[] ret = { "<html><body>", "<p>We provide a dialog to help users see the error information.</p>",
				"Please feel free to report <br>", "You feedbacks contribute a lot to us.", "</body></html>" };

		return MainFrameProperties.wrapStringArraysAsString(ret);
	}

}
