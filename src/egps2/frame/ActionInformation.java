package egps2.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import egps2.modulei.IModuleLoader;
import org.apache.commons.io.IOUtils;

import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;
import egps2.frame.gui.EGPSMainGuiUtil;
import egps2.modulei.IInformation;
import egps2.panels.InformationPanelFactory;

/**
 * "信息"菜单动作，显示当前激活模块的详细信息和文档。
 * Information menu action displaying detailed information and documentation of currently active module.
 *
 * <p>此动作响应用户点击"信息"菜单项或按Ctrl/Cmd+I快捷键，显示当前模块的使用说明、参数配置等信息。
 * This action responds to user clicking "Information" menu item or pressing Ctrl/Cmd+I shortcut key, displaying usage instructions, parameter configuration and other information of current module.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>显示模块信息 - Display module information</li>
 *   <li>调用模块的getInformation()方法 - Invoke module's getInformation() method</li>
 *   <li>根据模块状态自动启用/禁用 - Auto enable/disable based on module status</li>
 *   <li>快捷键：Ctrl/Cmd+I - Shortcut: Ctrl/Cmd+I</li>
 * </ul>
 *
 * @see AdjustedSoftAction
 * @see IInformation
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionInformation extends AdjustedSoftAction {

	private ActionAbout actionAbout;
	
	private String actionName = UnifiedAccessPoint.getResourceString("infor.Menu.label");

	public ActionInformation(ActionAbout actionAbout) {

		putValue(NAME, actionName);
		putValue(SHORT_DESCRIPTION, getShortDescriptionString());
		putValue(MNEMONIC_KEY, KeyEvent.VK_I);
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		setIcons("information.png");

		this.actionAbout = actionAbout;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		ModuleFace selectedModule = instanceFrame.getSelectedModule();

		if (selectedModule == null) {
			actionAbout.actionPerformed(e);
			return;
		}

		IInformation moduleInfo = selectedModule.getModuleInfo();
		if (moduleInfo == null) {

			String str = UnifiedAccessPoint.getResourceString("dialog.info");

			String content = UnifiedAccessPoint.getResourceString("features.not.implement");
			SwingDialog.showInfoMSGDialog(str, content);
			return;
		}

		String howModuleLaunch = moduleInfo.getHowModuleLaunch();
		String whatDataInvoked = moduleInfo.getWhatDataInvoked();
		String howUserOperates = moduleInfo.getHowUserOperates();
		String summaryOfResults = moduleInfo.getSummaryOfResults();
		Optional<IModuleLoader> moduleLoader = selectedModule.getModuleLoader();
		IModuleLoader loader = null;
		if (moduleLoader.isPresent()){
			loader = moduleLoader.get();
		}else if (selectedModule instanceof IModuleLoader){
			loader = (IModuleLoader) selectedModule;
		}
		String string = loader.getVersion().toString();

		String[] ret = { 
				"<html><body><blockquote>", 
				"<h2> The module version:</h2><p>",
				string,
				"<h2> How module launched:</h2><p>",
				howModuleLaunch,
				"</p><h2> What data invoked:</h2><p>",
				whatDataInvoked, 
				"</p><h2> How user operates:</h2><p>", 
				howUserOperates, 
				"</p><h2> Summary of results:</h2><p>",
				summaryOfResults, 
				"</p><tr>	<td ALIGN=center><br> <font color=\"#990000\" size=\"4\">If	you find the functionalites of the software has any form of help to	your research, please cite the following literatures.</font> <br>	<p>	<strong>How to cite </strong> <br />1. Dalang Yu, Lili Dong, Fangqi Yan, Hailong Mu, Bixia Tang, et al. (2019) eGPS 1.0: comprehensive software for multi-omic and evolutionary analyses.	 National Science Review, 6(5):867-869, <a href=\"https://doi.org/10.1093/nsr/nwz079\" target=\"_blank\"	style=\"color: deepskyblue;\">https://doi.org/10.1093/nsr/nwz079</a></p> </td></tr></blockquote></body></html>" };

		String wrapStringArraysAsString = MainFrameProperties.wrapStringArraysAsString(ret);

		JEditorPane informationPanel = null;
		try {
			informationPanel = new InformationPanelFactory().getInformationPanel(wrapStringArraysAsString);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JDialog jDialog = new JDialog(instanceFrame, actionName, true);
		EGPSMainGuiUtil.addEscapeListener(jDialog);

		JScrollPane comp = new JScrollPane(informationPanel);
		comp.setBorder(null);
		jDialog.add(comp);
		jDialog.setSize(800, 750);

		jDialog.setLocationRelativeTo(instanceFrame);
		jDialog.setVisible(true);

	}

	@Override
	protected String getShortDescriptionString() {
		String wrapStringArraysAsString = null;
		InputStream resourceAsStream = getClass().getResourceAsStream("html/tooltipOfInformationAction.html");
		try {
			wrapStringArraysAsString = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return wrapStringArraysAsString;
	}

	@Override
	public void setEnableStates(ModuleFace module) {
		if (module == null) {
			setEnabled(false);
		}else {
			setEnabled(module.getModuleInfo() != null);
		}
	}

}
