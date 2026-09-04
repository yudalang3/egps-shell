package egps2;

import java.awt.AWTError;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;

import egps2.modulei.IModuleLoader;
import egps2.panels.graphicpro.CustomizeFontEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jidesoft.utils.SystemInfo;
import com.jidesoft.plaf.LookAndFeelFactory;

import egps2.panels.dialog.SwingDialog;
import egps2.frame.MyFrame;
import egps2.frame.features.AutoConfigThePropertiesAction;

/**
 * Launcher is part of the eGPS desktop application.
 */
public class Launcher {

	public static boolean isDev = false;
	public static boolean isLaunchFromR = false;
	private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

	public static void main(String[] args) throws Exception {

		// 设置支持中文
		System.setProperty("file.encoding", "UTF-8");
		// java出现libpng warning: iCCP: known incorrect sRGB profile
		System.setProperty("javax.imageio.plugins.jpeg.JPGImageWriter.spi",
				"com.sun.imageio.plugins.jpeg.JPEGImageWriterSpi");

		// Headless guard for CI/WSL without DISPLAY to avoid AWTError
		if (!displayAvailable()) {
			logger.error("No usable display detected. Start an X server/WSLg (set DISPLAY) before running the GUI.");
			return;
		}

		logger.trace("Rdebug   Before GUI Start....");

		// 设置 语言
		Locale.setDefault(Locale.ENGLISH);
		logger.trace("Rdebug   Launcher 45...");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		logger.trace("Rdebug   Launcher 67...");
		lafPrepare();
		logger.trace("Rdebug   Launcher 68...");

		// all platform
		ToolTipManager sharedInstance = ToolTipManager.sharedInstance();
		sharedInstance.setDismissDelay(10000000);
		sharedInstance.setInitialDelay(1000);

		File file = new File(EGPSProperties.PROPERTIES_DIR_NAME);
		boolean exists = file.exists();
		UnifiedAccessPoint.isLaunchFirstTime = exists;

		if (UnifiedAccessPoint.isLaunchFirstTime) {
			AutoConfigThePropertiesAction autoConfigThePropertiesAction = new AutoConfigThePropertiesAction(file);
			autoConfigThePropertiesAction.run();
		} else {
			// not first time launch
            // 默认路径和这个软件当前所在目录的路径都没有配置文件，就说明这个软件是别人处理过的。
			if (!new File(EGPSProperties.PROPERTIES_DIR).exists()) {
				SwingDialog.showErrorMSGDialog(null, "Missing Config File",
						"The conifg file in user.home is not exists, may cause by error.\nPlease re-install the software.");
				return;
			}

		}

		/**
		 * 设置自己特有的字体，这样字体就能出现在所有可用的地方了。
		 */
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		logger.trace("Rdebug   Launcher 56...");
		Font cousineDefinedFont = CustomizeFontEnum.COUSINEREGULARFONTFAMILY.getCousineDefinedFont(Font.PLAIN, 12);
		logger.trace("Rdebug   Launcher 57...");

		if (cousineDefinedFont == null) {
			System.err.println("Sorry, the font can not be found: COUSINEREGULARFONTFAMILY ");
		} else {
			if (!env.registerFont(cousineDefinedFont)) {
				System.err.println("Sorry, the font can not be installed ".concat(cousineDefinedFont.getName()));
			}
		}


		// launch the module if the args is not null
        // 如果参数不为空，则说明是启动时指定了模块，那么就启动指定的模块。
		if (args != null && args.length > 0) {
			for (String arg : args){
				UnifiedAccessPoint.registerActionAfterMainFrame(() -> {
					loadThePredefinedModule(arg);
				});
			}
		}

		// following is the GUI launching process
		SwingUtilities.invokeLater(() -> {
			try {
				logger.trace("Rdebug   GUI Start....");
				launchProgram();
				logger.trace("Rdebug   GUI End....");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});



	}

	private static void loadThePredefinedModule(String arg) {
		SwingUtilities.invokeLater(() -> {
            Object newInstance = null;
            try {
                newInstance = Class.forName(arg).getDeclaredConstructor().newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            IModuleLoader independentModuleLoader = (IModuleLoader) newInstance;
			UnifiedAccessPoint.loadTheModuleFromIModuleLoader(independentModuleLoader);
		});
	}

	private static void launchProgram() throws Exception {

        //查看所有的属性
//		UIDefaults defaults = UIManager.getDefaults();
//		List<String> strings = new ArrayList<>();
//		for (Map.Entry<Object, Object> entry : defaults.entrySet()) {
//			String str = entry.getKey() + "\t" + entry.getValue();
//			strings.add(str);
//			if (str.contains("TabbedPane")) {
//				System.out.println(str);
//			}
//		}
//		FileUtils.writeLines(new File("C:\\Users\\yudal\\Desktop\\tmp.txt"), strings);

		logger.trace("Rdebug   Launcher 88...");

		// Apply global fonts to UIManager before creating the frame
		LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
		launchProperty.applyFontsToUIManager();

        // 下面这里图形界面初始化的代码了
		MyFrame myFrame = UnifiedAccessPoint.getInstanceFrame();

		logger.trace("Rdebug   Before call the set Visible...");

		myFrame.setVisible(true);

		logger.trace("Rdebug   After call the set Visible...");

		new Thread(() -> {
			UnifiedAccessPoint.doActionAfterMainFrame();
		}).start();

	}

	private static boolean displayAvailable() {
		try {
			GraphicsEnvironment.getLocalGraphicsEnvironment();
			return true;
		} catch (AWTError awtError) {
			return false;
		}
	}

	/**
	 * 这里是设置 look and feel.
	 *
	 * @throws Exception
	 */
	public static void lafPrepare() throws Exception {
		// 设置look and feel
		{
			// JIDE components (e.g., JideTabbedPane) require the JIDE extension to provide ThemePainter.
			// Without it, BasicJideTabbedPaneUI#getPainter() can be null and crash during painting.
			try {
				if (!LookAndFeelFactory.isJideExtensionInstalled()) {
					LookAndFeelFactory.installJideExtension(LookAndFeelFactory.EXTENSION_STYLE_OFFICE2003);
				}
			} catch (Throwable t) {
				// Do not fail startup if JIDE extension cannot be installed.
				logger.warn("Failed to install JIDE extension. JIDE components may not render correctly.", t);
			}

			// 各种杂七杂八的设置
			egps.lnf.ch_x.__UI__.uiImpl();
			// 自定义弹出组件（包括toolTip组件和弹出菜单等）的L&F实现
			egps.lnf.ch7_popup.__UI__.uiImpl();

			// 自定义按钮的L&F实现
			egps.lnf.ch3_button.__UI__.uiImpl();
			// 自定义滚动条的L&F实现 ，去除scroll
			egps.lnf.ch4_scroll.__UI__.uiImpl();
			// 自定义JSlider的L&F实现
			egps.lnf.ch15_slider.__UI__.uiImpl();
			// 自定义单选按钮的L&F实现
			egps.lnf.ch13_radio_check_button.__UI__.uiImpl();
			// 自定义进度条的L&F实现
			egps.lnf.ch12_progress.__UI__.uiImpl();
			// 自定义下拉框的L&F实现
			egps.lnf.ch14_combox.__UI__.uiImpl();
			// 自定义Jtree的L&F实现
			egps.lnf.ch16_tree.__UI__.uiImpl();
			// 自定义JSplitPane的L&F实现
			egps.lnf.ch17_split.__UI__.uiImpl();
			// 自定义JList的L&F实现
			egps.lnf.ch19_list.__UI__.uiImpl();

			egps.lnf.ch6_textcoms.__UI__.uiImpl();

			// 自定义Toolbar的L&F实现
			egps.lnf.ch8_toolbar.__UI__.uiImpl();

			// 自定义JFileChooser的L&F实现
			// 文件查看列表的边框实现
			UIManager.put("FileChooser.listViewBorder",
					new BorderUIResource(new egps.lnf.ch4_scroll.ScrollPaneBorder()));
			// 此颜色将决定windows平台下文件选择面板的左边WindowsPlaceBar的背景色
			UIManager.put("ToolBar.shadow", new ColorUIResource(new Color(249, 248, 243)));

			// 设置美观属性
			UIManager.put("TabbedPane.shadow", new Color(102, 102, 102)); // 阴影色
			UIManager.put("TabbedPane.focus", new Color(153, 153, 255)); // 焦点色
			UIManager.put("TabbedPane.contentAreaColor", Color.WHITE); // 内容区域背景色
			UIManager.put("TabbedPane.tabInsets", new Insets(5, 10, 15, 10)); // Tab内边距，增加文本与边界的距离
			UIManager.put("TabbedPane.tabAreaInsets", new Insets(10, 10, 10, 90)); // Tab区域的外边距
			UIManager.put("TabbedPane.tabsOverlapBorder", true); // 允许Tab覆盖边框，使外观更紧凑

			UIManager.put("TabbedPane.background", new Color(245, 245, 245)); // 更柔和的背景色
			UIManager.put("TabbedPane.foreground", Color.black);
			UIManager.put("TabbedPane.highlight", new Color(33, 150, 243)); // Material Design 蓝色高亮

		}

		if (SystemInfo.isAnyMac()) {
			UIManager.put("MenuItem.background", new Color(245, 245, 245));
			UIManager.put("MenuItem.opaque", true);
			UIManager.put("TaskPane.titleBackgroundGradientStart", new Color(214, 223, 247));
			UIManager.put("TaskPane.titleBackgroundGradientEnd", new Color(214, 223, 247));

			System.setProperty("apple.awt.fileDialogForDirectories", "true");

			// take the menu bar off the jframe
			System.setProperty("apple.laf.useScreenMenuBar", "true");

			// set the name of the application menu item
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "eGPS software");

		} else {
			// 自定义菜单项的L&F实现
			egps.lnf.ch9_menu.__UI__.uiImpl();
		}

		UIManager.put("TipOfTheDay.background", new Color(198, 211, 246));
	}

}
