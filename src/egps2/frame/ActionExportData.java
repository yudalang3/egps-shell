package egps2.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.KeyStroke;

import org.apache.commons.io.IOUtils;

/**
 * "导出数据"菜单动作，将当前模块的数据导出到外部文件。
 * Export data menu action exporting current module's data to external files.
 *
 * <p>此动作响应用户点击"导出"菜单项或按Ctrl/Cmd+S快捷键，调用当前模块的数据导出功能。
 * This action responds to user clicking "Export" menu item or pressing Ctrl/Cmd+S shortcut key, invoking data export functionality of current module.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>导出模块数据 - Export module data</li>
 *   <li>调用模块的exportData()方法 - Invoke module's exportData() method</li>
 *   <li>根据模块的canExport()状态自动启用/禁用 - Auto enable/disable based on module's canExport() status</li>
 *   <li>快捷键：Ctrl/Cmd+S - Shortcut: Ctrl/Cmd+S</li>
 * </ul>
 *
 * @see AdjustedSoftAction
 * @see ModuleFace#exportData()
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionExportData extends AdjustedSoftAction {

	private static final long serialVersionUID = -9154663336342661233L;

	public ActionExportData() {
		putValue(NAME, "Export ");
		putValue(SHORT_DESCRIPTION, getShortDescriptionString());
		putValue(MNEMONIC_KEY, KeyEvent.VK_S);
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		setIcons("export.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ModuleFace module = getSelectedModuleFace();
		
		module.exportData();
		
	}

	@Override
	public void setEnableStates(ModuleFace module) {
		if (module == null) {
			setEnabled(false);
		}else {
			setEnabled(module.canExport());
		}
	}

	@Override
	protected String getShortDescriptionString() {
		String wrapStringArraysAsString = null;
		InputStream resourceAsStream = getClass().getResourceAsStream("html/tooltipOfExportAction.html");
		try {
			wrapStringArraysAsString = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wrapStringArraysAsString;
	}

}
