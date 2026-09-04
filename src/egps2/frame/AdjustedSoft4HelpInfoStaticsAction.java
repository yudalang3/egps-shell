package egps2.frame;


/**
 * 帮助、信息、统计动作专用的自适应动作基类，排除特定模块的可用性。
 * Adjusted action base class specific for help, information and statistics actions, excluding availability for specific modules.
 *
 * <p>此类继承自{@link AdjustedSoftAction}，为帮助、信息、统计等动作提供特殊的启用/禁用逻辑，排除Raven和bonus模块。
 * This class extends {@link AdjustedSoftAction}, providing special enable/disable logic for help, information and statistics actions, excluding Raven and bonus modules.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>自动排除Raven界面模块 - Automatically exclude Raven UI modules</li>
 *   <li>自动排除bonus工具模块 - Automatically exclude bonus tool modules</li>
 *   <li>仅对主要功能模块启用 - Enable only for main functional modules</li>
 * </ul>
 *
 * @see AdjustedSoftAction
 * @author eGPS Dev Team
 * @since 2.1
 */
public abstract class AdjustedSoft4HelpInfoStaticsAction extends AdjustedSoftAction {
	@Override
	public void setEnableStates(ModuleFace module) {
		if (module == null) {
			setEnabled(false);
		}else {
			String moduleClsName = module.getClass().getName();
			if (moduleClsName.startsWith("com.raven")){
				setEnabled(false);
				return;
			}
            setEnabled(true);
		}
	}
	
}
