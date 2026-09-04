package egps2.frame;

import java.util.InputMismatchException;
import java.util.ResourceBundle;

/**
 * 国际化资源管理类，提供统一的资源束（ResourceBundle）访问接口。
 * Internationalization resource management class that provides unified resource bundle access interface.
 *
 * <p>此类封装了Java标准 {@link java.util.ResourceBundle} 的访问，简化了应用程序中
 * 国际化字符串的获取。虽然应用程序默认使用英文locale，但资源束机制保留了未来多语言支持的可能性。
 * This class encapsulates access to Java's standard {@link java.util.ResourceBundle}, simplifying
 * the retrieval of internationalized strings in the application. Although the application defaults
 * to English locale, the resource bundle mechanism preserves the possibility of future multi-language support.
 *
 * <p><strong>资源束配置：</strong>
 * Resource bundle configuration:
 * <ul>
 *   <li><b>资源文件名：</b>frame.properties（位于classpath根目录）</li>
 *   <li><b>编码：</b>UTF-8（由 {@link egps2.Launcher} 在启动时设置）</li>
 *   <li><b>加载策略：</b>延迟加载，首次调用时初始化</li>
 *   <li><b>Resource file name:</b> frame.properties (located in classpath root)</li>
 *   <li><b>Encoding:</b> UTF-8 (set by {@link egps2.Launcher} at startup)</li>
 *   <li><b>Loading strategy:</b> Lazy loading, initialized on first call</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * // 获取资源字符串
 * String welcomeMsg = MyResourceBundle.getString("welcome.message");
 * String buttonLabel = MyResourceBundle.getString("button.ok.label");
 *
 * // 如果key不存在，会抛出 InputMismatchException
 * }</pre>
 *
 * <p><strong>错误处理：</strong>
 * Error handling:
 * <br>如果请求的key在资源束中不存在，{@link #getString(String)} 会抛出 {@link InputMismatchException}。
 * 开发者应确保所有使用的key都在 frame.properties 文件中定义。
 * If the requested key does not exist in the resource bundle, {@link #getString(String)} throws {@link InputMismatchException}.
 * Developers should ensure all used keys are defined in the frame.properties file.
 *
 * <p><strong>静态工具类：</strong>
 * Static utility class:
 * <br>此类不可实例化，所有方法都是静态的。资源束实例在首次访问时创建并缓存。
 * This class cannot be instantiated, all methods are static. The resource bundle instance is created and cached on first access.
 *
 * <p>线程安全：{@link #getResourceBundle()} 方法不是线程安全的，但在实际使用中，
 * 由于首次调用通常发生在应用启动的EDT线程中，因此不存在并发问题。
 * Thread safety: {@link #getResourceBundle()} method is not thread-safe, but in practice,
 * since the first call typically occurs in the EDT thread during application startup, no concurrency issues exist.
 *
 * @see java.util.ResourceBundle
 * @see egps2.Launcher
 * @author eGPS Dev Team
 * @since 2.0
 */
public class MyResourceBundle {
	private static ResourceBundle bundle;

	/**
	 * Returns the resource bundle associated with this demo. Used to get accessable
	 * and internationalized strings.
	 *
	 * @return the resource bundle
	 */
	public static ResourceBundle getResourceBundle() {
		if (bundle == null) {
			bundle = ResourceBundle.getBundle("frame");
		}
		return bundle;
	}

	/**
	 * This method returns a string from the demo's resource bundle.
	 *
	 * @param key the key
	 * @return the string
	 */
	public static String getString(String key) {
		String value = getResourceBundle().getString(key);

		if (value == null) {
			throw new InputMismatchException("Cannot found the string:\t".concat(key));
		}
		return value;
	}
}
