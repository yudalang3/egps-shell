package egps2.frame;

import java.awt.Component;
import java.util.InputMismatchException;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IThreadOperator;
import egps2.modulei.RunningTask;

/**
 * 计算型模块面板基类，为需要执行后台计算任务的模块提供统一的任务管理和进度追踪功能。
 * Base class for computational module panels, providing unified task management and progress tracking for modules that need to execute background computation tasks.
 *
 * <p>此类继承自 {@link ModuleFace} 并实现了 {@link IThreadOperator} 和 {@link Runnable} 接口，
 * 为计算密集型模块提供了完整的后台任务执行框架。
 * This class extends {@link ModuleFace} and implements {@link IThreadOperator} and {@link Runnable} interfaces,
 * providing a complete background task execution framework for computation-intensive modules.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>后台线程管理 - Background thread management</li>
 *   <li>任务进度追踪 - Task progress tracking</li>
 *   <li>任务生命周期控制（启动、停止、完成）- Task lifecycle control (start, stop, complete)</li>
 *   <li>自动集成到主框架的状态栏和工具栏 - Automatic integration with main frame's status bar and toolbar</li>
 *   <li>错误处理和用户提示 - Error handling and user prompts</li>
 * </ul>
 *
 * <p><strong>使用流程：</strong>
 * Usage flow:
 * <ol>
 *   <li>子类创建 {@link RunningTask} 实例，定义具体的计算任务</li>
 *   <li>调用 {@link #registerRunningTask(RunningTask)} 注册并启动任务</li>
 *   <li>框架自动在后台线程中执行任务，实时更新进度</li>
 *   <li>任务完成或出错时，框架自动处理清理工作并提示用户</li>
 *   <li>用户可通过工具栏的"停止"按钮中断任务</li>
 * </ol>
 *
 * <p><strong>任务执行过程：</strong>
 * Task execution process:
 * <pre>
 * 1. actionsBeforeStart()  - 任务启动前准备
 * 2. while (processNext() != PROGRESS_FINSHED)  - 循环执行任务步骤
 * 3. actionsAfterFinished()  - 任务完成后清理
 * </pre>
 *
 * <p><strong>进度显示：</strong>
 * Progress display:
 * <ul>
 *   <li>时间可估算任务：显示百分比进度条（0%-100%）</li>
 *   <li>时间未知任务：显示滚动进度条（indeterminate progress）</li>
 *   <li>状态栏显示任务提示信息（{@link RunningTask#getIndicateString()}）</li>
 *   <li>Time-estimable tasks: Display percentage progress bar (0%-100%)</li>
 *   <li>Time-unknown tasks: Display scrolling progress bar (indeterminate progress)</li>
 *   <li>Status bar displays task hint message ({@link RunningTask#getIndicateString()})</li>
 * </ul>
 *
 * <p><strong>错误处理：</strong>
 * Error handling:
 * <br>如果任务执行过程中抛出异常，框架会：
 * If an exception is thrown during task execution, the framework will:
 * <ol>
 *   <li>记录错误日志</li>
 *   <li>如果 {@code showPrompt} 为 true，弹出错误对话框</li>
 *   <li>清理任务状态，允许再次注册新任务</li>
 * </ol>
 *
 * <p><strong>线程安全：</strong>
 * Thread safety:
 * <br>{@link #registerRunningTask(RunningTask)} 方法是同步的，确保同一时刻只能有一个任务运行。
 * 如果尝试在已有任务运行时注册新任务，方法会返回 {@code false}。
 * {@link #registerRunningTask(RunningTask)} method is synchronized, ensuring only one task can run at a time.
 * Attempting to register a new task while one is already running returns {@code false}.
 *
 * <p><strong>示例：</strong>
 * Example:
 * <pre>{@code
 * public class MyComputationalModule extends ComputationalModuleFace {
 *     protected MyComputationalModule(IModuleLoader moduleLoader) {
 *         super(moduleLoader);
 *     }
 *
 *     public void startComputation() {
 *         RunningTask task = new RunningTask() {
 *             private int step = 0;
 *             @Override
 *             public boolean isTimeCanEstimate() { return true; }
 *             @Override
 *             public int getTotalSteps() { return 100; }
 *             @Override
 *             public int processNext() {
 *                 // 执行计算
 *                 step++;
 *                 return step >= 100 ? PROGRESS_FINSHED : step;
 *             }
 *         };
 *         registerRunningTask(task);
 *     }
 * }
 * }</pre>
 *
 * <p>子类化提示：
 * Subclassing tips:
 * <ul>
 *   <li>无需覆盖 {@link #run()} 方法，任务逻辑应封装在 {@link RunningTask} 中</li>
 *   <li>设置 {@code showPrompt = false} 可禁用完成提示对话框</li>
 *   <li>实现 {@link #getRunningTask()} 和 {@link #stopRunningTask()} 已由基类完成</li>
 * </ul>
 *
 * @see ModuleFace
 * @see RunningTask
 * @see IThreadOperator
 * @author eGPS Dev Team
 * @since 2.0
 */
@SuppressWarnings("serial")
public abstract class ComputationalModuleFace extends ModuleFace implements IThreadOperator, Runnable {

	private static final Logger logger = LoggerFactory.getLogger(ComputationalModuleFace.class);

	protected ComputationalModuleFace(IModuleLoader moduleLoader) {
		super(moduleLoader);
	}

	private RunningTask runningTask;
	private Thread thread;
	private int progressIndex = 0;

	/**
	 * 是否展示 show prompt
	 */
	protected boolean showPrompt = true;

	/**
	 * 当模块还有任务的时候返回false。 模块开发者调用这个方法注册功能，然后框架会自动运行这个程序。
	 * 
	 * @param task
	 * @return false: 表示没有成功，成功就是true
	 */
	public final boolean registerRunningTask(RunningTask task) {
		if (this.runningTask == null) {
			this.runningTask = task;

			thread = new Thread(this);
			thread.start();
			return true;
		} else {
			String str = UnifiedAccessPoint.getResourceString("thread.error.already");
			JOptionPane.showMessageDialog(this, str);
			return false;
		}
	}

	@Override
	public void run() {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();

		instanceFrame.refreshAllActionsInToolbarPlusStatesBar();
		boolean timeCanEstimate = runningTask.isTimeCanEstimate();

		try {
			logger.trace("Start the process logic ...");
			processLogic(instanceFrame, timeCanEstimate);
		} catch (Exception e) {
			String str = UnifiedAccessPoint.getResourceString("dialog.error");
			String message = e.getMessage();
			if (!Strings.isNotEmpty(message)) {
				message = e.toString();
			}
			SwingDialog.showErrorMSGDialog(str, message);
			e.printStackTrace();
		} finally {
			runningTask = null;
			instanceFrame.refreshAllActionsInToolbarPlusStatesBar();
			logger.trace("End the running process");
		}

	}

	private void processLogic(MyFrame instanceFrame, boolean timeCanEstimate) throws Exception {
		runningTask.actionsBeforeStart();

		logger.trace("Start the processLogic() method");
		if (timeCanEstimate) {
			int totalSteps = runningTask.getTotalSteps();
			if (totalSteps <= 0) {

				String str = UnifiedAccessPoint.getResourceString("thread.error.stepTooSmall");
				throw new InputMismatchException(str);
			}
			double onePercentInteger = 100d / totalSteps;
			double currentStep = 0;
			double prevProgress = 0;
			int progressIndes = 0;

			while ((progressIndes = runningTask.processNext()) != RunningTask.PROGRESS_FINSHED) {

				currentStep = progressIndes * onePercentInteger;
				if (currentStep - prevProgress >= onePercentInteger) {
					// 差异过大再更新
					// should update progress
					progressIndex = (int) currentStep;
					prevProgress = currentStep;
					instanceFrame.onlyRefreshButtomStatesBar(ComputationalModuleFace.this, null, progressIndex);

					logger.trace("The progress indicator");
				}
			}
		} else {

			instanceFrame.onlyRefreshButtomStatesBar(ComputationalModuleFace.this, runningTask.getIndicateString(),
					100);
			while (runningTask.processNext() != RunningTask.PROGRESS_FINSHED) {
				instanceFrame.onlyRefreshButtomStatesBar(ComputationalModuleFace.this, runningTask.getIndicateString(),
						100);
			}
			;
		}

		logger.trace("End the processLogic() method");
		runningTask.actionsAfterFinished();

		if (showPrompt) {
			/**
			 * 这个提示如果和 busy panel事件紧挨着，那么这个提示就不会出现
			 */
			Component selectedComponent = instanceFrame.getJTabbedPanel().getSelectedComponent();
			showPrompt(instanceFrame, selectedComponent);
		}
	}

	private void showPrompt(MyFrame instanceFrame, Component selectedComponent) {
		if (this != selectedComponent) {
			Optional<IModuleLoader> moduleLoader2 = getModuleLoader();
			String str = UnifiedAccessPoint.getResourceString("thread.info.taskFinished");
			if (moduleLoader2.isPresent()) {
				instanceFrame.prompt(str.concat(moduleLoader.getTabName()));
			} else {
				instanceFrame.prompt(str);
			}
		}
	}

	@Override
	public final Optional<RunningTask> getRunningTask() {
		return Optional.ofNullable(runningTask);
	}

	/**
	 * 如果一个执行的任务的时间是可以估计的，那么这个将会返回 进度。
	 */
	public int getProgressIndexIfNeeded() {
		return progressIndex;
	}

	@SuppressWarnings("deprecation")
	@Override
	public final void stopRunningTask() {
		if (runningTask != null) {
			/**
			 * 直接stop，不管是否这个任务的时间可以估计。
			 */
			progressIndex = 0;// 清零
			runningTask = null;
			thread.stop();
		}

	}

	/**
	 * 这两个判定是否可以退出的条件，下面那个更好，因为Thread不会被赋值为空
	 */
//	@Override
//	public boolean closeTab() {
//		if (thread != null) {
//			if (thread.isAlive()) {
//				return true;
//			}
//		}
//		return false;
//	}
	@Override
	public boolean closeTab() {
		Optional<RunningTask> runningTask = getRunningTask();
		boolean present = runningTask.isPresent();
		return present;
	}

}
