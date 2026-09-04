package egps2.plugin.manager;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 自定义URL类加载器，用于动态加载外部JAR文件中的插件模块。
 * Custom URL class loader for dynamically loading plugin modules from external JAR files.
 *
 * <p>此类加载器改变了Java标准的"双亲委派"模型，优先从自己的URL列表中加载类，
 * 只有在找不到时才委托给父类加载器。这种策略允许插件使用自己的库版本，避免与主应用的类冲突。
 * This class loader alters Java's standard "parent delegation" model, prioritizing loading classes
 * from its own URL list, and only delegating to the parent class loader when not found.
 * This strategy allows plugins to use their own library versions, avoiding class conflicts with the main application.
 *
 * <p><strong>类加载策略：</strong>
 * Class loading strategy:
 * <ol>
 *   <li>首先尝试从插件JAR中加载类（调用 {@link #findClass(String)}）</li>
 *   <li>如果找不到，再委托给父类加载器（调用 {@code super.loadClass()}）</li>
 *   <li>这与标准的"父类优先"策略相反</li>
 * </ol>
 *
 * <p><strong>父委托策略对比：</strong>
 * Comparison with parent delegation:
 * <ul>
 *   <li><b>标准策略：</b>Parent → findClass()</li>
 *   <li><b>此类策略：</b>findClass() → Parent</li>
 *   <li><b>优点：</b>插件可以使用自己的依赖版本，实现类隔离</li>
 *   <li><b>缺点：</b>可能加载多份相同的类，增加内存开销</li>
 * </ul>
 *
 * <p><strong>使用场景：</strong>
 * Use cases:
 * <ul>
 *   <li>动态加载插件模块（实现 {@link egps2.modulei.IModuleLoader} 的类）</li>
 *   <li>加载插件自带的第三方库（避免版本冲突）</li>
 *   <li>实现热插拔功能（卸载旧插件，加载新插件）</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * // 创建类加载器，指定插件JAR的URL
 * URL[] urls = {new File("plugins/myPlugin.jar").toURI().toURL()};
 * CustomURLClassLoader classLoader = new CustomURLClassLoader(urls);
 *
 * // 加载插件类
 * Class<?> pluginClass = classLoader.loadClass("com.example.MyPlugin");
 * IModuleLoader loader = (IModuleLoader) pluginClass.newInstance();
 *
 * // 使用完毕后关闭类加载器
 * classLoader.close();
 * }</pre>
 *
 * <p><strong>线程安全：</strong>
 * Thread safety:
 * <br>{@link #loadClass(String)} 方法是 {@code synchronized} 的，确保线程安全。
 * 但同一个类加载器不应在多个线程中同时用于加载不同的类。
 * {@link #loadClass(String)} method is {@code synchronized}, ensuring thread safety.
 * However, the same class loader should not be used to load different classes simultaneously in multiple threads.
 *
 * <p><strong>资源管理：</strong>
 * Resource management:
 * <br>使用完毕后应调用 {@link #close()} 方法关闭类加载器，释放JAR文件句柄。
 * After use, call {@link #close()} method to close the class loader and release JAR file handles.
 *
 * @see java.net.URLClassLoader
 * @see egps2.plugin.manager.JarFileUtil
 * @see egps2.modulei.IModuleLoader
 * @author eGPS Dev Team
 * @since 2.0
 */
public class CustomURLClassLoader extends URLClassLoader {

	public CustomURLClassLoader(URL[] urls, ClassLoader parent) {
		super(urls, parent);
	}

	public CustomURLClassLoader(URL[] urls) {
		super(urls);
	}

	@Override
	public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
		// 首先尝试从自己的URL列表中加载类
		try {
			return findClass(name);
		} catch (ClassNotFoundException e) {
			// 如果找不到，再委托给父类加载器
			return super.loadClass(name);
		}
	}
}