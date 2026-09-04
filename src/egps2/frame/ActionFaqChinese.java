package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import egps2.panels.dialog.SwingDialog;
import egps2.panels.InformationPanelFactory;

/**
 * FAQ (Chinese) menu action displaying frequently asked questions in Chinese.
 *
 * @see AbstractSoftAction
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionFaqChinese extends AbstractSoftAction {

	public ActionFaqChinese() {
		putValue(SHORT_DESCRIPTION, "常见问题解答（中文）");
		putValue(NAME, "FAQ Chinese");

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
			URL url = getClass().getResource("html/Faq_Chinese.html");
			html = new InformationPanelFactory().getInformationPanelFromResource(url);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		html.setEditable(false);
		JScrollPane scroller = new JScrollPane(html);
		scroller.setBorder(null);

		SwingDialog.QuickWrapperJCompWithDialog(scroller, "常见问题 - 全局字体系统", 900, 800);
	}

}
