package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import egps2.panels.dialog.SwingDialog;
import egps2.panels.InformationPanelFactory;

/**
 * FAQ (English) menu action displaying frequently asked questions in English.
 *
 * @see AbstractSoftAction
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionFaqEnglish extends AbstractSoftAction {

	public ActionFaqEnglish() {
		putValue(SHORT_DESCRIPTION, "Frequently asked questions (English)");
		putValue(NAME, "FAQ English");

		try {
			setIconBySvg("help/Faq.svg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JEditorPane html = null;
		try {
			URL url = getClass().getResource("html/Faq_English.html");
			html = new InformationPanelFactory().getInformationPanelFromResource(url);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		html.setEditable(false);
		JScrollPane scroller = new JScrollPane(html);
		scroller.setBorder(null);

		SwingDialog.QuickWrapperJCompWithDialog(scroller, "FAQ - Global Font System", 900, 800);
	}

}
