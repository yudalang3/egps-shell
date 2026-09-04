package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;

import egps2.UnifiedAccessPoint;
import egps2.panels.ReflectDialog;

/**
 * "报告错误"菜单动作，打开错误报告对话框。
 * Report bugs menu action opening bug report dialog.
 *
 * <p>此动作响应用户点击"报告错误"菜单项，打开错误报告对话框用于收集软件运行时问题和用户反馈。
 * This action responds to user clicking "Report bugs" menu item, opening bug report dialog for collecting software runtime issues and user feedback.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>报告软件错误 - Report software bugs</li>
 *   <li>提交用户反馈 - Submit user feedback</li>
 *   <li>自动收集系统信息 - Auto collect system information</li>
 *   <li>生成错误报告 - Generate error report</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @see ReflectDialog
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionReportBugs extends AbstractSoftAction {

	private static final long serialVersionUID = -9154663336342661233L;
	private final String actionName;

	public ActionReportBugs() {
		actionName = "Report bugs";
		putValue(NAME, actionName);
		putValue(SHORT_DESCRIPTION, getShortDescriptionString());

		try {
			setIconBySvg("help/Report bugs.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		
		ReflectDialog reflectDialog = new ReflectDialog(instanceFrame, actionName, true);
		
		reflectDialog.setSize(600, 700);
		reflectDialog.setLocationRelativeTo(instanceFrame);
		reflectDialog.setVisible(true);
	}

	@Override
	protected String getShortDescriptionString() {

		String[] ret = { "<html><body>",
				"<p>We provide a dialog to help users to send a bug.</p>",
				"Please feel free to report <br>",
				"You feedbacks contribute a lot to us.",
				"</body></html>" };

		return MainFrameProperties.wrapStringArraysAsString(ret);
	}

}
