package egps2.modulei;

/**
 * 计算任务接口，定义了可控计算任务的执行流程和进度追踪契约。
 * Running task interface that defines the execution flow and progress tracking contract for controllable computation tasks.
 *
 * <p>此接口是计算型模块的核心接口，支持两种类型的任务：
 * This interface is the core interface for computational modules, supporting two types of tasks:
 *
 * <p><strong>任务类型：</strong>
 * Task types:
 * <ol>
 *   <li><b>时间可估算任务</b> - 进度可预测，使用百分比进度条
 *       <ul>
 *         <li>需实现 {@link #isTimeCanEstimate()} 返回 {@code true}</li>
 *         <li>需实现 {@link #getTotalSteps()} 返回总步骤数</li>
 *         <li>{@link #processNext()} 返回当前完成的步骤数</li>
 *       </ul>
 *   </li>
 *   <li><b>时间未知任务</b> - 进度不可预测，使用滚动进度条
 *       <ul>
 *         <li>{@link #isTimeCanEstimate()} 返回 {@code false}</li>
 *         <li>不需要实现 {@link #getTotalSteps()}</li>
 *         <li>{@link #processNext()} 执行迭代步骤</li>
 *       </ul>
 *   </li>
 * </ol>
 *
 * <p><strong>执行流程（三个阶段）：</strong>
 * Execution flow (three phases):
 * <ol>
 *   <li>{@link #actionsBeforeStart()} - 任务启动前的准备工作</li>
 *   <li>{@link #processNext()} - 循环调用直到返回 {@link #PROGRESS_FINSHED}</li>
 *   <li>{@link #actionsAfterFinished()} - 任务完成后的清理工作</li>
 * </ol>
 *
 * <p><strong>进度追踪：</strong>
 * Progress tracking:
 * <ul>
 *   <li>{@link #processNext()} 必须返回实际完成的进度值，不能依赖调用次数</li>
 *   <li>当返回 {@link #PROGRESS_FINSHED} ({@value #PROGRESS_FINSHED}) 时表示任务完成</li>
 *   <li>{@link #getIndicateString()} 返回在状态栏显示的提示信息</li>
 *   <li>{@link #processNext()} must return the actual completed progress value, cannot rely on call count</li>
 *   <li>Returning {@link #PROGRESS_FINSHED} ({@value #PROGRESS_FINSHED}) indicates task completion</li>
 *   <li>{@link #getIndicateString()} returns the hint message displayed in the status bar</li>
 * </ul>
 *
 * <p><strong>异常处理：</strong>
 * Exception handling:
 * <br>所有方法都可能抛出 {@link Exception}，框架会捕获并向用户显示错误信息。
 * All methods may throw {@link Exception}, which the framework will catch and display to the user.
 *
 * <p><strong>示例实现：</strong>
 * Example implementation:
 * <pre>{@code
 * public class MyRunningTask implements RunningTask {
 *     private int currentStep = 0;
 *     private final int totalSteps = 100;
 *
 *     @Override
 *     public boolean isTimeCanEstimate() { return true; }
 *
 *     @Override
 *     public int getTotalSteps() { return totalSteps; }
 *
 *     @Override
 *     public int processNext() throws Exception {
 *         // 执行计算
 *         currentStep++;
 *         if (currentStep >= totalSteps) {
 *             return PROGRESS_FINSHED;
 *         }
 *         return currentStep;
 *     }
 * }
 * }</pre>
 *
 * <p>线程模型：任务通常在后台线程中执行，但 {@link #actionsAfterFinished()} 可能在EDT中调用（如更新UI）。
 * Thread model: Tasks typically execute in background threads, but {@link #actionsAfterFinished()} may be called in EDT (e.g., updating UI).
 *
 * @see IThreadOperator
 * @see egps2.frame.ComputationalModuleFace
 * @author eGPS Dev Team
 * @since 2.1
 */
public interface RunningTask{
	
	int PROGRESS_FINSHED = -2;
	/**
	 * 返回时间是否可估计，就是进度条是否是按照百分百走的。
	 * @return
	 */
	boolean isTimeCanEstimate();
	
	default String getIndicateString() {return "Computation...";};

	/**
	 * When this process been invoked, return the accomplished index.
	 * 
	 * 重要：一定要返回具体的值，因为仅仅根据processNext会不准
	 * 当返回 {@value #PROGRESS_FINSHED} 的时候就是结束的时候
	 * @return accomplished progress. 
	 */
	int processNext() throws Exception;
	
	/**
	 * 这个方法只会在 {@link #isTimeCanEstimate()} 返回true的时候才会被调用。
	 * @return 返回总的步骤数量
	 */
	default int getTotalSteps() {return 100;};
	
	/**
	 * action before start
	 * 
	 * @throws Exception
	 */
    default void actionsBeforeStart() throws Exception{}; 

	/**
	 * action after finished
	 * 
	 * @throws Exception
	 */
    default void actionsAfterFinished() throws Exception{};
}
