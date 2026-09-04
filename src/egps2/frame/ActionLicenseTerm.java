package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JDialog;

import egps2.UnifiedAccessPoint;
import egps2.panels.LicenseOfEGPSPanel;

/**
 * "许可证条款"菜单动作，显示软件许可证和使用条款对话框。
 * License term menu action displaying software license and usage terms dialog.
 *
 * <p>此动作响应用户点击"许可证"菜单项，显示eGPS的软件许可协议和使用条款。
 * This action responds to user clicking "License" menu item, displaying eGPS software license agreement and usage terms.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>显示许可证条款 - Display license terms</li>
 *   <li>显示使用协议 - Display usage agreement</li>
 *   <li>法律信息 - Legal information</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @see LicenseOfEGPSPanel
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionLicenseTerm extends AbstractSoftAction {

	private final String actionName;

	public ActionLicenseTerm() {
		actionName = "License";
		putValue(NAME, actionName);
		putValue(SHORT_DESCRIPTION, getShortDescriptionString());
		try {
			setIconBySvg("help/License.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog2 = getDialog();
		dialog2.setSize(950, 800);
		dialog2.setLocationRelativeTo(UnifiedAccessPoint.getInstanceFrame());
		dialog2.setVisible(true);
	}

	public JDialog getDialog() {
		JDialog dialog = new JDialog(UnifiedAccessPoint.getInstanceFrame(), true);
		dialog.setTitle(actionName);
		LicenseOfEGPSPanel panel = new LicenseOfEGPSPanel();
		dialog.add(panel);
		return dialog;
	}

	@Override
	protected String getShortDescriptionString() {
		return new String("Display the License Terms");
	}

}
