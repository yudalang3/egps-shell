package egps2.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.Action;
import javax.swing.KeyStroke;

import org.apache.commons.io.IOUtils;

/**
 * "导入数据"菜单动作，将外部数据导入到当前激活的模块。
 * Import data menu action importing external data into currently active module.
 *
 * <p>此动作响应用户点击"导入"菜单项或按Ctrl/Cmd+O快捷键，调用当前模块的数据导入功能。
 * This action responds to user clicking "Import" menu item or pressing Ctrl/Cmd+O shortcut key, invoking data import functionality of current module.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>导入外部数据文件 - Import external data files</li>
 *   <li>调用模块的importData()方法 - Invoke module's importData() method</li>
 *   <li>根据模块的canImport()状态自动启用/禁用 - Auto enable/disable based on module's canImport() status</li>
 *   <li>快捷键：Ctrl/Cmd+O - Shortcut: Ctrl/Cmd+O</li>
 * </ul>
 *
 * @see AdjustedSoftAction
 * @see ModuleFace#importData()
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionImportData extends AdjustedSoftAction {

    private static final long serialVersionUID = 5902596847622961595L;

    public ActionImportData() {
        putValue(NAME, "Import ");
        putValue(Action.SHORT_DESCRIPTION, getShortDescriptionString());

        /**
         * 助记符 (Mnemonic) 是与界面组件绑定的快捷键，需要激活界面后使用（比如按下 Alt+O 来触发某个菜单或按钮）。
         * 加速器 (Accelerator) 是全局快捷键，可以在不激活任何特定组件的情况下直接使用（比如 Ctrl+O 或 Cmd+O 打开文件）。
         */
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_O);
        putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        setIcons("import.png");

	}

    @Override
    public void actionPerformed(ActionEvent e) {
        ModuleFace module = getSelectedModuleFace();
		module.importData();
    }

    @Override
    public void setEnableStates(ModuleFace module) {

        if (module == null) {
            setEnabled(false);
        } else {
            setEnabled(module.canImport());
        }
    }

    @Override
    protected String getShortDescriptionString() {

        String wrapStringArraysAsString = null;
        InputStream resourceAsStream = getClass().getResourceAsStream("html/tooltipOfImportAction.html");

        try {
            wrapStringArraysAsString = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapStringArraysAsString;

    }

}
