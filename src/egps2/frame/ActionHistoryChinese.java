package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;

import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;
import egps2.frame.html.HistoryJTreeDialogChinese;

/**
 * History (Chinese) menu action displaying software development history in Chinese.
 *
 * @see AbstractSoftAction
 * @see HistoryJTreeDialogChinese
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionHistoryChinese extends AbstractSoftAction {

	public ActionHistoryChinese() {
		putValue(SHORT_DESCRIPTION, "开发历史（中文）");
		putValue(NAME, "History Chinese");

		try {
			setIconBySvg("help/History Chinese.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		HistoryJTreeDialogChinese historyJTreeDialog = new HistoryJTreeDialogChinese();

		SwingDialog.QuickWrapperJCompWithDialog(historyJTreeDialog, "开发历史 - 全局字体系统", 900, 800);
	}

}
