package egps2.frame;

import egps2.UnifiedAccessPoint;

/**
 * 自适应动作抽象基类，根据当前激活模块的状态自动调整可用性。
 * Adjusted action abstract base class automatically adjusting availability based on currently active module's state.
 *
 * <p>此类继承自{@link AbstractSoftAction}，为需要根据当前模块状态动态启用/禁用的动作提供基类。
 * This class extends {@link AbstractSoftAction}, providing base class for actions that need to be dynamically enabled/disabled based on current module state.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>获取当前激活模块 - Get currently active module</li>
 *   <li>根据模块状态调整可用性 - Adjust availability based on module state</li>
 *   <li>提供模块感知的动作行为 - Provide module-aware action behavior</li>
 * </ul>
 *
 * <p><strong>使用指南：</strong>
 * Usage guide:
 * <br>子类需实现{@link #setEnableStates(ModuleFace)}方法，定义根据模块状态启用/禁用的逻辑。
 * Subclasses need to implement {@link #setEnableStates(ModuleFace)} method, defining logic for enabling/disabling based on module state.
 *
 * @see AbstractSoftAction
 * @see ModuleFace
 * @author eGPS Dev Team
 * @since 2.1
 */
public abstract class AdjustedSoftAction extends AbstractSoftAction {
	public abstract void setEnableStates(ModuleFace module);
	
	/**
	 * Obtain the selected module face. The ModuleFace is the current active tab that users see by eyes.
	 * This is seeing is believing to the user.
	 */
	protected ModuleFace getSelectedModuleFace() {
		return UnifiedAccessPoint.getInstanceFrame().getSelectedModule();
	}
}
