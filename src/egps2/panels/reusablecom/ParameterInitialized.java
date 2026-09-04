package egps2.panels.reusablecom;

/**
 * 为控制面板的每个小面板提供的一个接口！
 * 为什么要这样设计呢？
 * 因为这样可以把我们的绘图功能和图形界面分离开来！
 */
public interface ParameterInitialized {
	/**
	 * 给textField，spinner等初始化值！
	 * 注意初始化前要移除监听，然后再添加监听。
	 */
	void initializeParameters();
	/**
	 * 为按钮等组件添加监听！
	 */
	void addListeners();
	/**
	 * 移除监听，因为改变值之前需要移除监听！
	 */
	void removeListeners();
}
