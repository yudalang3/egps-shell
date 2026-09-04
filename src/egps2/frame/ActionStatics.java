package egps2.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.JDialog;
import javax.swing.KeyStroke;

import org.apache.commons.io.IOUtils;

import egps2.UnifiedAccessPoint;
import egps2.frame.gui.EGPSMainGuiUtil;
import egps2.panels.StaticsPanel;

/**
 * "统计信息"菜单动作，显示软件使用统计和启动信息。
 * Statistics menu action displaying software usage statistics and launch information.
 *
 * <p>此动作响应用户点击"统计"菜单项或按Ctrl/Cmd+T快捷键，显示软件启动次数、功能使用频率等统计信息。
 * This action responds to user clicking "Statistics" menu item or pressing Ctrl/Cmd+T shortcut key, displaying software launch count, feature usage frequency and other statistics.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>显示启动统计 - Display launch statistics</li>
 *   <li>功能使用频率 - Feature usage frequency</li>
 *   <li>用户行为分析 - User behavior analysis</li>
 *   <li>快捷键：Ctrl/Cmd+T - Shortcut: Ctrl/Cmd+T</li>
 * </ul>
 *
 * @see AdjustedSoft4HelpInfoStaticsAction
 * @see StaticsPanel
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionStatics extends AdjustedSoft4HelpInfoStaticsAction {
	public ActionStatics() {
		putValue(NAME, "Statistics");
		putValue(SHORT_DESCRIPTION, getShortDescriptionString());
		putValue(MNEMONIC_KEY, KeyEvent.VK_T);
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_T, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		setIcons("statistics.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		
		ModuleFace selectedModule = instanceFrame.getSelectedModule();
		StaticsPanel tableDemo = new StaticsPanel();
		tableDemo.initializeThePanel(selectedModule);
		JDialog jDialog = new JDialog(instanceFrame, "Statics", true);
		

		EGPSMainGuiUtil.addEscapeListener(jDialog);
		
		jDialog.add(tableDemo);
		jDialog.setSize(800, 750);
		
		jDialog.setLocationRelativeTo(instanceFrame);
		jDialog.setVisible(true);
		
	}

	
	@Override
	protected String getShortDescriptionString() {
		String wrapStringArraysAsString = null;
		InputStream resourceAsStream = getClass().getResourceAsStream("html/tooltipOfStaticsAction.html");
		try {
			wrapStringArraysAsString = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wrapStringArraysAsString;
	}

}
