package egps2.frame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;

import egps2.UnifiedAccessPoint;
import egps2.panels.InformationPanelFactory;
import egps2.frame.gui.EGPSMainGuiUtil;
/**
 * "关于"菜单动作，显示开发团队信息和版本更新检查对话框。
 * About menu action displaying development team information and version update check dialog.
 *
 * <p>此动作响应用户点击"关于"菜单项，显示包含开发团队信息、版本号等的对话框，并提供版本更新检查功能。
 * This action responds to user clicking "About" menu item, displaying a dialog containing development team information, version number, etc., and providing version update check functionality.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>显示开发团队信息 - Display development team information</li>
 *   <li>显示软件版本信息 - Display software version information</li>
 *   <li>提供版本更新检查 - Provide version update check</li>
 *   <li>从about.html读取显示内容 - Read display content from about.html</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @author yudal
 * @since 2.1
 */
public class ActionAbout extends AbstractSoftAction {

	private static final long serialVersionUID = -9154663336342661233L;

	public ActionAbout() {
		putValue(SHORT_DESCRIPTION, "The development team information.");
		putValue(NAME, "About");

		try {
			setIconBySvg("help/About.svg");
		} catch (IOException e) {
			throw new IllegalStateException("Can not find the icon file for help/About.svg");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        getJDialog();
	}

	private void getJDialog(){
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		JDialog jDialog = new JDialog(instanceFrame);
		jDialog.setTitle(UnifiedAccessPoint.getResourceString("action.about.dialog.title"));
		{
			JEditorPane html;
			try {
				URL url = getClass().getResource("html/about.html");
				html = new InformationPanelFactory().getInformationPanelFromResource(url);
			} catch (IOException e2) {
				throw new IllegalStateException(e2);
			}
			html.setEditable(false);
			//html.setFocusable(false);
			JScrollPane scroller = new JScrollPane(html);
			scroller.setBorder(null);
			jDialog.add(scroller, BorderLayout.CENTER);
		}

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		JButton cancelButton = new JButton(UnifiedAccessPoint.getResourceString("cancel.text"));
		cancelButton.setFont(defaultFont);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

		cancelButton.addActionListener(evt -> {
			jDialog.dispose();
		});

		buttonPanel.add(cancelButton);
		jDialog.add(buttonPanel, BorderLayout.SOUTH);

		EGPSMainGuiUtil.addEscapeListener(jDialog);
		jDialog.setSize(900, 800);
		jDialog.setLocationRelativeTo(instanceFrame);
		jDialog.setVisible(true);
	}

}
