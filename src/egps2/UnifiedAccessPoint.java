package egps2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import utils.string.EGPSStringUtil;
import egps2.utils.EGPSIconUtil;
import egps2.frame.ComputationalModuleFace;
import egps2.frame.ModuleFace;
import egps2.frame.MyFrame;
import egps2.frame.MyResourceBundle;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;

/**
 * 所有资源的统一入口
 */
public class UnifiedAccessPoint {

	private static MyFrame instanceFrame;
	private static LaunchProperty launchProperty;
	static boolean isLaunchFirstTime = false;

	private static List<Runnable> actionsAfterMainFrame = new ArrayList<>();

	private static final Logger logger = LoggerFactory.getLogger(UnifiedAccessPoint.class);

	/**
	 * 判断图形界面是否已经启动
	 * <p>
	 * 此方法主要用于命令行环境下，判断是否需要启动图形界面
	 * 通过检查instanceFrame对象是否为null来判断，如果instanceFrame不为null， 则表示图形界面已经启动
	 *
	 * @return 如果图形界面已经启动，返回true；否则返回false
	 */
	public static boolean isGULaunched() {
		return instanceFrame != null;
	}

	public static MyFrame getInstanceFrame() {
		if (instanceFrame == null) {
			/*
			 * 创建程序实例在这里
			 */
			logger.trace("Rdebug   UniSoftInstance 40...");
			LaunchProperty launchProperty = getLaunchProperty();
			logger.trace("Rdebug   UniSoftInstance 42...");
			instanceFrame = new MyFrame() {

			};
			logger.trace("Rdebug   UniSoftInstance 52...");
			instanceFrame.setSize(launchProperty.getWidth(), launchProperty.getHeight());
			instanceFrame.setLocation(launchProperty.getLocationX(), launchProperty.getLocationY());

		}
		return instanceFrame;
	}

	static void doActionAfterMainFrame() {
		SwingUtilities.invokeLater(() -> instanceFrame.showHints());

		/*
		 * 该线程本身不是EDT
		 */
//		System.out.println("SwingUtilities.isEventDispatchThread()\t" + SwingUtilities.isEventDispatchThread());

		for (Runnable action : actionsAfterMainFrame) {
			action.run();
		}
		actionsAfterMainFrame = null;
	}

	public static void registerActionAfterMainFrame(Runnable run) {
		actionsAfterMainFrame.add(run);
	}

	public static LaunchProperty getLaunchProperty() {

		if (launchProperty == null) {
			logger.trace("Rdebug   UniSoftInstance 82...");
			launchProperty = LaunchProperty.getInstance();
			logger.trace("Rdebug   UniSoftInstance 84...");
			// Need to use persistent storage
			// This will do when the software exist
		}

		return launchProperty;
	}

	public static boolean isFirstTimeLaunched() {
		return isLaunchFirstTime;
	}

	public static String getResourceString(String string) {
		return MyResourceBundle.getString(string);
	}

	public static URL getImageResource(String name) {
		return UnifiedAccessPoint.class.getResource("/images/" + name);
	}

	public static InputStream getImageResourceAsStream(String name) {
		return UnifiedAccessPoint.class.getResourceAsStream("/images/" + name);
	}

	public static void promoteAtBottom(ComputationalModuleFace computationalModuleFace, String s) {
		if (computationalModuleFace == null) {
			System.out.println(s);
			return;
		}
		if (UnifiedAccessPoint.isGULaunched()) {
			UnifiedAccessPoint.getInstanceFrame().onlyRefreshButtomStatesBar(computationalModuleFace, s, 100);
		} else {
			System.out.println(s);
		}
	}

	public static Map<String, String> getExternalProgramPath() {
		HashMap<String, String> newHashMap = Maps.newHashMap();
		String path = EGPSProperties.EGPS_WRAPPER_PROGRAM_CONFIG_PATH;
		File file = new File(path);
		if (file.exists()) {
			try {
				List<String> lines = FileUtils.readLines(file, StandardCharsets.US_ASCII);
				Iterator<String> iterator = lines.iterator();
				while (iterator.hasNext()) {
					String next = iterator.next();
					if (next.startsWith("#")) {
						iterator.remove();
					}
				}

				for (String string : lines) {
					String[] split = EGPSStringUtil.split(string, '=', 2);
					newHashMap.put(split[0], split[1]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return newHashMap;

	}

	/**
	 * 这是载入一个模块的快捷方法，需要放入一个IModuleLoader实例
	 * 
	 * @param next
	 * @return the moduleFace
	 */
	public static ModuleFace loadTheModuleFromIModuleLoader(IModuleLoader next) {

		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		ModuleFace face = next.getFace();
		String shortDescription = next.getShortDescription();
		String name = next.getTabName();
		Icon icon = null;

		IconBean iconBean = next.getIcon();
		if (iconBean != null && iconBean.hasResource()) {
			try {
				icon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresizeWithTabIcon(iconBean.getInputStream(),
						iconBean.isSVG());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		instanceFrame.addTab2mainTabbedPanel(name, icon, face, shortDescription);

		String name2 = next.getClass().getName();
		UnifiedAccessPoint.getLaunchProperty().setLastLaunchedModuleClz(name2);

		return face;
	}

}
