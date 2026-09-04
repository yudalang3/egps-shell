package egps2.frame;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import egps2.panels.dialog.SwingDialog;
import egps2.panels.InformationPanelFactory;

/**
 * "常见问题"菜单动作，显示软件常见问题解答(FAQ)对话框。
 * FAQ menu action displaying frequently asked questions dialog.
 *
 * <p>此动作响应用户点击"FAQ"菜单项，显示包含常见问题及解答的对话框，帮助用户快速解决问题。
 * This action responds to user clicking "Faq" menu item, displaying a dialog containing frequently asked questions and answers to help users quickly resolve issues.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>显示FAQ内容 - Display FAQ content</li>
 *   <li>根据当前语言加载对应 FAQ 页面 - Load the language-specific FAQ page</li>
 *   <li>提供常见问题解答 - Provide FAQ</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @author yudal
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionFaq extends AbstractSoftAction {

	public ActionFaq() {
		putValue(SHORT_DESCRIPTION, "The frequently asked questions.");
		putValue(NAME, "Faq");

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
			// Detect language and load the matching FAQ file
			boolean isEnglish = egps2.UnifiedAccessPoint.getLaunchProperty().isEnglish();
			String fileName = isEnglish ? "html/Faq_English.html" : "html/Faq_Chinese.html";
			URL url = getClass().getResource(fileName);

			html = new InformationPanelFactory().getInformationPanelFromResource(url);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		html.setEditable(false);
		JScrollPane scroller = new JScrollPane(html);
		scroller.setBorder(null);

		String title = egps2.UnifiedAccessPoint.getLaunchProperty().isEnglish() ?
				"FAQ - Global Font System" : "常见问题 - 全局字体系统";
		SwingDialog.QuickWrapperJCompWithDialog(scroller, title, 900, 800);
	}

}
