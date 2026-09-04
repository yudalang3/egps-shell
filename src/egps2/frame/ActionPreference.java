package egps2.frame;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import egps2.PreferencePanel;
import egps2.UnifiedAccessPoint;
import egps2.frame.gui.EGPSMainGuiUtil;
import egps2.builtin.modules.voice.TextInputDialogWithOKCancel;

/**
 * "首选项"菜单动作，打开应用程序设置和配置对话框。
 * Preference menu action opening application settings and configuration dialog.
 *
 * <p>此动作响应用户点击"首选项"菜单项，打开包含各种应用程序设置选项的对话框，如界面主题、字体大小、快捷键等。
 * This action responds to user clicking "Preference" menu item, opening a dialog containing various application setting options such as UI theme, font size, shortcuts, etc.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>配置应用程序设置 - Configure application settings</li>
 *   <li>自定义界面主题 - Customize UI theme</li>
 *   <li>调整字体和显示 - Adjust fonts and display</li>
 *   <li>修改快捷键 - Modify shortcuts</li>
 * </ul>
 *
 * @see AbstractSoftAction
 * @see PreferencePanel
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionPreference extends AbstractSoftAction {

	private String title;

	public ActionPreference() {

		title = UnifiedAccessPoint.getResourceString("Pref.action.title");

		putValue(NAME, UnifiedAccessPoint.getResourceString("Pref.action.name"));
		putValue(SHORT_DESCRIPTION, title);
		putValue(MNEMONIC_KEY, KeyEvent.VK_Q);
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

		setIcons("preference.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

		JDialog jDialog = new JDialog(instanceFrame, title, true);

		jDialog.setLayout(new BorderLayout());

		PreferencePanel comp = new PreferencePanel(UnifiedAccessPoint.getLaunchProperty());
		jDialog.add(comp, BorderLayout.CENTER);

		JButton defaultButton = new JButton(UnifiedAccessPoint.getResourceString("Pref.action.restore.name"));
		defaultButton.setFont(defaultFont);
		defaultButton.setToolTipText(UnifiedAccessPoint.getResourceString("Pref.action.restore.tip"));
		defaultButton.addActionListener(e1 -> {
			comp.restoreToDefaults();
			jDialog.dispose();
		});
		
		JButton showConfigProButton = new JButton(UnifiedAccessPoint.getResourceString("Pref.action.showconfig.name"));
		showConfigProButton.setFont(defaultFont);
		showConfigProButton.setToolTipText(UnifiedAccessPoint.getResourceString("Pref.action.showconfig.tip"));
		showConfigProButton.addActionListener(e2 -> {
			TextInputDialogWithOKCancel textInputDialogWithOKCancel = new TextInputDialogWithOKCancel();
			textInputDialogWithOKCancel.setCallbackFunction(() -> {
				jDialog.dispose();
			});
			EGPSMainGuiUtil.addEscapeListener(textInputDialogWithOKCancel);
			
			textInputDialogWithOKCancel.setSize(800, 600);
			textInputDialogWithOKCancel.setLocationRelativeTo(instanceFrame);
			textInputDialogWithOKCancel.setVisible(true);
		});
		
		JButton cancelButton = new JButton(UnifiedAccessPoint.getResourceString("general.button.cancel"));
		cancelButton.setFont(defaultFont);
		cancelButton.addActionListener(e1 -> {
			jDialog.dispose();
		});
		JButton appAndCloseButton = new JButton(UnifiedAccessPoint.getResourceString("general.button.applyAndClose"));
		appAndCloseButton.setFont(defaultFont);
		appAndCloseButton.setToolTipText(UnifiedAccessPoint.getResourceString("Pref.action.relaunch.tip"));
		appAndCloseButton.addActionListener(e1 -> {
			comp.applyAndClose();
			jDialog.dispose();
		});

		JPanel jPanel = new JPanel();
		BoxLayout boxLayout2 = new BoxLayout(jPanel, BoxLayout.X_AXIS);
		jPanel.setLayout(boxLayout2);

		jPanel.add(defaultButton);
		jPanel.add(showConfigProButton);

		jPanel.add(Box.createGlue());
		jPanel.add(cancelButton);

		jPanel.add(Box.createHorizontalStrut(5));
		jPanel.add(appAndCloseButton);
		jPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

		jDialog.add(jPanel, BorderLayout.SOUTH);

		jDialog.setSize(800, 600);
		jDialog.setLocationRelativeTo(instanceFrame);

		EGPSMainGuiUtil.addEscapeListener(jDialog);
		jDialog.setVisible(true);

	}

}
