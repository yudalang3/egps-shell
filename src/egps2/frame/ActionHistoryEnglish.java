package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;

import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;
import egps2.frame.html.HistoryJTreeDialogEnglish;

/**
 * History (English) menu action displaying software development history in English.
 *
 * @see AbstractSoftAction
 * @see HistoryJTreeDialogEnglish
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionHistoryEnglish extends AbstractSoftAction {

	public ActionHistoryEnglish() {
		putValue(SHORT_DESCRIPTION, "Development history (English)");
		putValue(NAME, "History English");

		try {
			setIconBySvg("help/History Chinese.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		HistoryJTreeDialogEnglish historyJTreeDialog = new HistoryJTreeDialogEnglish();

		SwingDialog.QuickWrapperJCompWithDialog(historyJTreeDialog, "Development History - Global Font System", 900, 800);
	}

}
