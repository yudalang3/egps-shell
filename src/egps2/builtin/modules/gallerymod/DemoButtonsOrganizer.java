package egps2.builtin.modules.gallerymod;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXButton;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import egps2.EGPSProperties;
import egps2.utils.EGPSIconUtil;
import utils.storage.MapPersistence;
import egps2.Launcher;
import egps2.UnifiedAccessPoint;
import egps2.frame.MainFrameProperties;
import egps2.frame.MyFrame;
import egps2.frame.HintManager.Hint;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.modulei.ModuleClassification;
import egps2.builtin.modules.itoolmanager.IModuleElement;

/**
 * DemoButtonsOrganizer belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class DemoButtonsOrganizer {

	RightDemoPanel rightDemoPanel;
	private final IModuleLoader[] existedLoaders;
	private final Icon defaultIcon;
	private boolean firstLoadedButton = true;
	/**
	 * 用来进行持久化保存
	 */
	private Map<String, Integer> str2numberMap;

	public DemoButtonsOrganizer() throws IOException {
		InputStream imageResourceAsStream = UnifiedAccessPoint.getImageResourceAsStream("module/waiting_blank.png");
		defaultIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(imageResourceAsStream, false);

		// Use getExistedLoaders() to respect user configuration
		// Only show modules that user has configured to load (consistent with iTools menu and ModuleInspector)
		existedLoaders = MainFrameProperties.getExistedLoaders();
	}


	Map<String, Integer> getStr2numberMap() {
		if (str2numberMap == null) {
			final String storePath = getStorePath();
			str2numberMap = MapPersistence.getStr2numberMap(storePath);
		}
		return str2numberMap;
	}

	private String getStorePath() {
		final String storePath = EGPSProperties.JSON_DIR.concat("/egps.DemoButtonsOrganizer.saveData.gz");
		return storePath;
	}

	public void configModulesByCat(JXTaskPaneContainer taskPaneContainer,ModuleClassification byfunctionality) {
		Map<String, Integer> str2numberMap = getStr2numberMap();

		int sizeCategory1 = byfunctionality.getSize();
		Map<Integer, List<IModuleLoader>> organizeModuleLoaders = organizeModuleLoaders(byfunctionality);

		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
		for (int i = 0; i < sizeCategory1; i++) {
			String string = byfunctionality.getNameStrings()[i];
			JXTaskPane taskPanel = getTaskPanel(organizeModuleLoaders.get(i));
			taskPanel.setTitle(string);
			taskPanel.setFont(defaultTitleFont);
			taskPaneContainer.add(taskPanel);

			Integer integer = str2numberMap.get(string);
			boolean boolean1 = false;
			if (integer != null && integer > 0) {
				boolean1 = true;
			}

			taskPanel.setCollapsed(boolean1);
		}

	}

	private JXTaskPane getTaskPanel(List<IModuleLoader> list) {
		JXTaskPane operationPane = new JXTaskPane();

		for (IModuleLoader iModuleLoader : list) {

			addModuleGalleryDisplayButtons(operationPane, iModuleLoader);
		}

		operationPane.setBackground(Color.blue);

		return operationPane;
	}

	private void addModuleGalleryDisplayButtons(JXTaskPane operationPane, IModuleLoader iModuleLoader) {
		JXButton moduleTurnToButton = new JXButton();

		// ====== 这部分属于第一次启动的提示，相对独立
		if (UnifiedAccessPoint.isFirstTimeLaunched() && firstLoadedButton) {
			MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
			// 注册一个 Hint 事件
			String resourceString = UnifiedAccessPoint.getResourceString("Application.hint.intro.button");
			Hint firstHint = new Hint(resourceString, moduleTurnToButton, SwingConstants.RIGHT);
			instanceFrame.appendOneHint(firstHint);
		}
		firstLoadedButton = false;
		// =======

		// Check if this module is from plugin directory
		boolean isPlugin = isPluginModule(iModuleLoader);

		// Set button text with badge if it's a plugin
		String buttonText = iModuleLoader.getTabName();
		if (isPlugin) {
			// Add "Plug" badge with HTML formatting for better visual effect
			buttonText = "<html>" + buttonText + " <b style='color:#0066CC;'>[Plug]</b></html>";
		}
		moduleTurnToButton.setText(buttonText);

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		moduleTurnToButton.setFont(defaultFont);
		moduleTurnToButton.setToolTipText(iModuleLoader.getShortDescription());
		IconBean iconBean = iModuleLoader.getIcon();

		Icon icon = null;
		if (iconBean != null && iconBean.hasResource()) {
			try {
				icon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(iconBean.getInputStream(), iconBean.isSVG());
			} catch (IOException e) {
				e.printStackTrace();
			}
			moduleTurnToButton.setIcon(icon);

		} else {
			moduleTurnToButton.setIcon(defaultIcon);
		}
		moduleTurnToButton.setHorizontalAlignment(SwingConstants.LEFT);

		String shortDescription = iModuleLoader.getShortDescription();
		if (shortDescription == null) {
			shortDescription = "<html><i>Left click to see the module illustraction.<br>Double click or right click to open module.</i>";
		} else {
			shortDescription = "<html><i>Left click to see the illustraction. Double click or right click to open module.</i><br><br>".concat(shortDescription);
		}


		if (Launcher.isDev) {
			shortDescription = shortDescription.concat("<br><br>" + iModuleLoader.getClass().getName());
			if (isPlugin) {
				shortDescription = shortDescription.concat("<br><strong style='color:blue;'>[From ~/.egps2/config/plugin/]</strong>");
			}
		}

		moduleTurnToButton.setToolTipText(shortDescription);

		operationPane.add(moduleTurnToButton);

		moduleTurnToButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON3 || e.getClickCount() == 2) {
					MainFrameProperties.loadTheModuleFromIModuleLoader(iModuleLoader);
				} else {
					rightDemoPanel.loadModuleIntroduction(iModuleLoader);
				}
			}
		});

	}

	/**
	 * Checks if a module is from plugin directory (~/.egps2/config/plugin/)
	 *
	 * @param loader Module loader to check
	 * @return true if the module is from plugin directory, false otherwise
	 */
	private boolean isPluginModule(IModuleLoader loader) {
		try {
			// Get the class location
			java.security.ProtectionDomain protectionDomain = loader.getClass().getProtectionDomain();
			java.security.CodeSource codeSource = protectionDomain.getCodeSource();

			if (codeSource == null) {
				return false;
			}

			java.net.URL location = codeSource.getLocation();
			if (location == null) {
				return false;
			}

			String locationPath = location.getPath();

			// Normalize path separators and check if it's in plugin directory
			// Plugin directory: ~/.egps2/config/plugin/ or %USERPROFILE%\.egps2\config\plugin\
			String normalizedPath = locationPath.replace('\\', '/');
			boolean isFromPluginDir = normalizedPath.contains("/.egps2/config/plugin/") ||
			                          normalizedPath.contains(".egps2/config/plugin/");

			return isFromPluginDir;
		} catch (Exception e) {
			return false;
		}
	}

	private Map<Integer, List<IModuleLoader>> organizeModuleLoaders(ModuleClassification moduleClassification) {
		// 这个ordinal就是这个类别的索引
		// 比如 moduleClassification 是 byFunction，那么这个ordinal就是0
		// 下面的typeIndex 变量就是其中的子类的名称
		int ordinal = moduleClassification.ordinal();
		int sizeOfCategory = moduleClassification.getSize();
		Map<Integer, List<IModuleLoader>> ret = new HashMap<>();

		for (int i = 0; i < sizeOfCategory; i++) {
			ret.put(i, new ArrayList<>());
		}

		for (IModuleLoader iModuleLoader : existedLoaders) {
			int[] category = iModuleLoader.getCategory();
			if (category == null) {
				System.err.println(iModuleLoader.toString() + "\t do not have category information.");
			}

			if (ordinal >= category.length) {
				continue;
			}
			int typeIndex = category[ordinal];
			List<IModuleLoader> list = ret.get(typeIndex);

			list.add(iModuleLoader);
		}

		return ret;
	}

	public void setRightDemoPanel(RightDemoPanel rightDemoPanel) {
		this.rightDemoPanel = rightDemoPanel;

	}


	public void doCloseModuleAction(JXTaskPaneContainer taskPanelContainer) {
		Component[] components = taskPanelContainer.getComponents();

		Map<String, Integer> str2numberMap = getStr2numberMap();

		for (Component component : components) {
			if (component instanceof JXTaskPane) {
				JXTaskPane jxTaskPane = (JXTaskPane) component;
				Integer saveNumber = jxTaskPane.isCollapsed() ? 1 : 0;
				str2numberMap.put(jxTaskPane.getTitle(), saveNumber);
			}
		}

		MapPersistence.storeStr2numberMap(str2numberMap, getStorePath());
	}

}
