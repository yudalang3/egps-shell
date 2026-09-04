package egps2.modulei;

import java.util.Optional;

/**
 * 线程操作接口，用于工具栏线程控制按钮和底部状态栏的线程管理。
 * Thread operator interface for toolbar thread control buttons and bottom status bar thread management.
 *
 * <p>此接口允许模块向主框架暴露其当前运行的计算任务，使得用户可以通过工具栏或状态栏
 * 查看任务进度并控制任务的执行（如停止任务）。
 * This interface allows modules to expose their currently running computation tasks to the main frame,
 * enabling users to view task progress and control task execution (such as stopping tasks)
 * through the toolbar or status bar.
 *
 * <p><strong>主要用途：</strong>
 * Main purposes:
 * <ul>
 *   <li>向主框架报告当前运行的任务 - Report currently running tasks to the main frame</li>
 *   <li>允许用户通过UI停止长时间运行的任务 - Allow users to stop long-running tasks via UI</li>
 *   <li>在状态栏显示任务进度和状态 - Display task progress and status in the status bar</li>
 * </ul>
 *
 * <p><strong>任务状态：</strong>
 * Task states:
 * <ul>
 *   <li>无任务运行：{@link #getRunningTask()} 返回 {@code Optional.empty()}</li>
 *   <li>有任务运行：返回包含 {@link RunningTask} 实例的 {@code Optional}</li>
 *   <li>No task running: {@link #getRunningTask()} returns {@code Optional.empty()}</li>
 *   <li>Task running: returns {@code Optional} containing a {@link RunningTask} instance</li>
 * </ul>
 *
 * <p><strong>停止任务策略：</strong>
 * Task stopping strategies:
 * <ul>
 *   <li><b>可控任务：</b>等待下一次 {@code processNext()} 方法调用时优雅终止</li>
 *   <li><b>不可控任务：</b>直接调用 {@code Thread.stop()} 方法（已废弃但必要时使用）</li>
 *   <li><b>Controllable tasks:</b> Wait for the next {@code processNext()} call to gracefully terminate</li>
 *   <li><b>Uncontrollable tasks:</b> Directly call {@code Thread.stop()} method (deprecated but necessary when needed)</li>
 * </ul>
 *
 * <p>线程模型：每个模块通常只运行一条计算线程，因此直接停止是安全的。
 * Thread model: Each module typically runs only one computation thread, so direct stopping is safe.
 *
 * @see RunningTask
 * @see java.util.Optional
 * @author eGPS Dev Team
 * @since 2.1
 */
public interface IThreadOperator {

	/**
	 * 返回当前运行的 Task，有可能没有。
	 * 就是说一个模块有可能需要计算，但是当前用户还没有点击计算。 
	 */
	Optional<RunningTask> getRunningTask();
	
	/**
	 * 终止线程：
	 * 如果是可控的计算任务，我们就等下一次processNext方法的时候终止任务；
	 * 如果是不可控任务就直接调用 Thread的stop方法，同时相关对象设置为null。
	 * 因为一个模块就一条线程所以，直接调用stop没问题。
	 * 
	 * 说明如果对于可控的计算任务，用户也想直接就结束，那么就直接点关闭按钮即可。
	 */
	void stopRunningTask();
}
