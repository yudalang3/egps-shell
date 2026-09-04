package egps2.frame;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

import javax.swing.KeyStroke;

import egps2.UnifiedAccessPoint;
import egps2.modulei.RunningTask;

/**
 * "停止"菜单动作，停止当前正在运行的计算任务。
 * Stop menu action stopping currently running computational task.
 *
 * <p>此动作响应用户点击"停止"菜单项或按Ctrl/Cmd+X快捷键，停止当前模块正在执行的计算任务。
 * This action responds to user clicking "Stop" menu item or pressing Ctrl/Cmd+X shortcut key, stopping the computational task currently being executed by the module.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>停止运行中的任务 - Stop running task</li>
 *   <li>调用任务的stopTask()方法 - Invoke task's stopTask() method</li>
 *   <li>根据任务状态自动启用/禁用 - Auto enable/disable based on task status</li>
 *   <li>快捷键：Ctrl/Cmd+X - Shortcut: Ctrl/Cmd+X</li>
 * </ul>
 *
 * @see AdjustedSoftAction
 * @see RunningTask
 * @author eGPS Dev Team
 * @since 2.1
 */
public class ActionStop extends AdjustedSoftAction {

	private static final long serialVersionUID = -9154663336342661233L;

	public ActionStop() {
		putValue(NAME, "Stop");
		putValue(MNEMONIC_KEY, KeyEvent.VK_X);
		putValue(ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		putValue(SHORT_DESCRIPTION, "Stop the running task.");
		
		setIcons("stop.png");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		ComputationalModuleFace cModuleFace = (ComputationalModuleFace) instanceFrame.getSelectedModule();
		
		cModuleFace.stopRunningTask();
		
		instanceFrame.refreshAllActionsInToolbarPlusStatesBar();
		
	}

	@Override
	public void setEnableStates(ModuleFace module) {
		if (module != null) {
			if (module instanceof ComputationalModuleFace) {
				ComputationalModuleFace cModuleFace = (ComputationalModuleFace) module;
				Optional<RunningTask> runningTask = cModuleFace.getRunningTask();
				if (runningTask.isPresent()) {
					setEnabled(true);
					return;
				}
			}
			
		}
		
		setEnabled(false);
	}


}
