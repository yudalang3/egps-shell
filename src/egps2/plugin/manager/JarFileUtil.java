package egps2.plugin.manager;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;

/**
 * JAR文件处理工具类，提供读取JAR/ZIP文件内容的静态方法。
 * JAR file utility class providing static methods for reading JAR/ZIP file contents.
 *
 * <p>此工具类封装了对JAR和ZIP文件的访问操作，主要用于：
 * This utility class encapsulates access operations to JAR and ZIP files, mainly used for:
 * <ul>
 *   <li>读取插件配置文件（eGPS2.plugin.properties）</li>
 *   <li>提取JAR中的图标资源</li>
 *   <li>读取JAR中的任意文本文件</li>
 *   <li>Reading plugin configuration files (eGPS2.plugin.properties)</li>
 *   <li>Extracting icon resources from JARs</li>
 *   <li>Reading arbitrary text files from JARs</li>
 * </ul>
 *
 * <p><strong>主要方法：</strong>
 * Main methods:
 * <ul>
 *   <li>{@link #getEGPSPluginProperties(String)} - 读取插件配置文件</li>
 *   <li>{@link #getImageIcon(String, String)} - 从JAR中提取图标</li>
 *   <li>{@link #readZipFile(String, String)} - 读取ZIP/JAR中的文本文件</li>
 * </ul>
 *
 * <p><strong>插件配置文件：</strong>
 * Plugin configuration file:
 * <br>eGPS插件必须在JAR根目录包含 {@code eGPS2.plugin.properties} 文件，
 * 当前实现真正读取的字段是模块入口类 {@code launchClass}，以及可选的 {@code dependentJars}。
 * eGPS plugins must include {@code eGPS2.plugin.properties} file in the JAR root directory.
 * In the current implementation, the effective fields are {@code launchClass} and optional
 * {@code dependentJars}.
 *
 * <p><strong>图标处理：</strong>
 * Icon handling:
 * <br>{@link #getImageIcon(String, String)} 方法从JAR中读取图标文件，
 * 自动缩放到16x16像素，并返回 {@link ImageIcon} 对象。如果图标不存在，返回 {@code Optional.empty()}。
 * {@link #getImageIcon(String, String)} method reads icon files from JARs,
 * automatically scales to 16x16 pixels, and returns {@link ImageIcon} object.
 * Returns {@code Optional.empty()} if icon does not exist.
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * // 读取插件配置
 * String jarPath = "plugins/myPlugin.jar";
 * List<String> properties = JarFileUtil.getEGPSPluginProperties(jarPath);
 * for (String line : properties) {
 *     System.out.println(line);
 * }
 *
 * // 提取图标
 * Optional<ImageIcon> icon = JarFileUtil.getImageIcon(jarPath, "icons/module.png");
 * icon.ifPresent(i -> button.setIcon(i));
 *
 * // 读取任意文件
 * List<String> cssContent = JarFileUtil.readZipFile(jarPath, "style/main.css");
 * }</pre>
 *
 * <p><strong>异常处理：</strong>
 * Exception handling:
 * <br>所有方法都声明抛出 {@link IOException}，调用者需要处理文件不存在、读取失败等异常情况。
 * All methods declare throwing {@link IOException}, callers need to handle exceptions such as file not found, read failure, etc.
 *
 * <p><strong>性能考虑：</strong>
 * Performance considerations:
 * <ul>
 *   <li>每次调用都会打开和关闭ZIP文件，不适合频繁调用</li>
 *   <li>图标加载包含缩放操作，有一定性能开销</li>
 *   <li>建议缓存读取结果，避免重复读取</li>
 *   <li>Each call opens and closes the ZIP file, not suitable for frequent calls</li>
 *   <li>Icon loading includes scaling operation, has performance overhead</li>
 *   <li>Recommend caching read results to avoid repeated reads</li>
 * </ul>
 *
 * <p><strong>静态工具类：</strong>
 * Static utility class:
 * <br>此类不可实例化，所有方法都是静态的。
 * This class cannot be instantiated, all methods are static.
 *
 * <p>线程安全：此类的所有方法都是线程安全的（无共享状态）。
 * Thread safety: All methods of this class are thread-safe (no shared state).
 *
 * @see java.util.zip.ZipFile
 * @see javax.swing.ImageIcon
 * @see CustomURLClassLoader
 * @author eGPS Dev Team
 * @since 2.0
 */
public class JarFileUtil {

	public static List<String> getEGPSPluginProperties(String jarFilePath) throws IOException {
		return readZipFile(jarFilePath, "eGPS2.plugin.properties");
	}

	public static void main(String args[]) throws IOException {
		String relativeFilePath = "style/someCSSFile.css";
		String zipFilePath = "/someDirectory/someWarFile.war";
		List<String> contents = readZipFile(zipFilePath, relativeFilePath);
		System.out.println(contents);
	}
	
	public static Optional<ImageIcon> getImageIcon(String zipFilePath, String resource) throws IOException {
		long start = System.currentTimeMillis();
		//System.out.println(resource);
		ZipFile zipFile = new ZipFile(zipFilePath);
		ZipEntry entry = zipFile.getEntry(resource);
		if (entry == null) {
			zipFile.close();
			return Optional.empty();
		}
		
		//System.out.println("entry not empty");
		InputStream inputStream = zipFile.getInputStream(entry);
		byte[] bytes = IOUtils.toByteArray(inputStream);
		inputStream.close();
		
		ImageIcon imageIcon = new ImageIcon(bytes);
		imageIcon.setImage(imageIcon.getImage().getScaledInstance(16, 16, Image.SCALE_FAST));
		
		zipFile.close();
		
		//long end = System.currentTimeMillis();
		
		//System.out.println(end - start);
		
		return Optional.ofNullable(imageIcon);
	}

	public static List<String> readZipFile(String zipFilePath, String relativeFilePath) throws IOException {
		List<String> ret = new ArrayList<>();
		ZipFile zipFile = new ZipFile(zipFilePath);
		Enumeration<? extends ZipEntry> e = zipFile.entries();

		while (e.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) e.nextElement();
			
			// if the entry is not directory and matches relative file then extract it
			if (!entry.isDirectory() && entry.getName().equals(relativeFilePath)) {
				InputStream inputStream = zipFile.getInputStream(entry);

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				bufferedReader.lines().forEach(s -> {
					ret.add(s.trim());
				});
				bufferedReader.close();

				//break;
			} else {
				continue;
			}
			
			
		}
		
		zipFile.close();
		
		return ret;
	}

}
