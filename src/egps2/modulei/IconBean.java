package egps2.modulei;

import java.io.InputStream;
import java.util.InputMismatchException;

/**
 * 模块图标数据对象，封装图标的输入流和格式信息。
 * Module icon data object that encapsulates icon input stream and format information.
 *
 * <p>此类用于在模块加载器中提供图标资源。由于包含 {@link java.io.InputStream} 实例，
 * 每个 IconBean 对象只能使用一次，不可重复使用，否则会导致流已被读取的错误。
 * This class is used to provide icon resources in module loaders. Since it contains an
 * {@link java.io.InputStream} instance, each IconBean object can only be used once
 * and cannot be reused, otherwise it will cause stream-already-read errors.
 *
 * <p><strong>重要约束：</strong>
 * Important constraints:
 * <ul>
 *   <li>每次调用 {@link IModuleLoader#getIcon()} 必须创建新的 IconBean 实例</li>
 *   <li>不要将 IconBean 存储为全局变量或单例</li>
 *   <li>{@link #getInputStream()} 方法只能调用一次，第二次调用会抛出异常</li>
 *   <li>Each call to {@link IModuleLoader#getIcon()} must create a new IconBean instance</li>
 *   <li>Do not store IconBean as a global variable or singleton</li>
 *   <li>{@link #getInputStream()} method can only be called once, second call throws exception</li>
 * </ul>
 *
 * <p><strong>支持的图标格式：</strong>
 * Supported icon formats:
 * <ul>
 *   <li>SVG 矢量图（推荐）- SVG vector graphics (recommended)</li>
 *   <li>PNG/JPG 位图 - PNG/JPG bitmap</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * @Override
 * public IconBean getIcon() {
 *     IconBean iconBean = new IconBean();
 *     iconBean.setInputStream(getClass().getResourceAsStream("icon.svg"));
 *     iconBean.setSVG(true);
 *     return iconBean;
 * }
 * }</pre>
 *
 * <p>线程安全：此类非线程安全，应在单线程环境中使用（通常在EDT中）。
 * Thread safety: This class is not thread-safe and should be used in a single-threaded environment (typically in EDT).
 *
 * @see IModuleLoader#getIcon()
 * @author eGPS Dev Team
 * @since 2.1
 */
public class IconBean {

	private InputStream inputStream;
	private boolean isSVG = true;
	private boolean hasUsed = false;
	
	public IconBean() {
	}

	public InputStream getInputStream() {
		if (hasUsed) {
			String str = "The input Stream has already used...";
			throw new InputMismatchException(str);
		}
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public boolean isSVG() {
		return isSVG;
	}

	public void setSVG(boolean isSVG) {
		this.isSVG = isSVG;
	}

	public boolean hasResource() {
		return inputStream != null;
	}
}
