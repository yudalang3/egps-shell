package egps2;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;

import utils.EGPSFileUtil;
import egps2.utils.FontDTO;
import egps2.utils.LaunchPropertyDTO;
import egps2.EGPSProperties;

/**
 * 这个启动属性的存储应该是主框架内部使用的 对于其它人开发的模块不应该从这里获取一些属性。
 * 
 * 从这个public的属性来看就可以知道不能这样。 但是我们从脚本型语言使用的经验来看，这个public属性是最好用的。 所以直接还是这样用吧
 * 
 * LaunchProperty4ProgramicConfig 这个类是命令行设置的。
 * 
 * @author yudalang
 *
 */
public class LaunchProperty {

	private String lastLaunchedModuleClz;
	private String textAntiAliasString;
	private Object graphicsTextAntialiasObj;

	private int width = 1500;
	private int height = 900;
	private int locationX = 0;
	private int locationY = 0;
	private boolean restoreToDefault = false;

	private Font menuFistLevelFont;
	private Font menuSecondLevelFont;

	private Font selectedTabTitleFont;
	private Font unSelectedTabTitleFont;
	private Font defaultFont;
	private Font defaultTitleFont;
	private Font documentFont;

	// Dialog fonts
	private Font dialogTitleFont;
	private Font dialogContentFont;
	private Font dialogButtonFont;

	// Component fonts
	private Font labelFont;
	private Font buttonFont;
	private Font checkBoxFont;

	// Input fonts
	private Font textFieldFont;
	private Font textAreaFont;
	private Font comboBoxFont;

	// Data display fonts
	private Font tableFont;
	private Font tableHeaderFont;
	private Font listFont;
	private Font treeFont;

	// Tool fonts
	private Font toolTipFont;
	private Font toolBarFont;

	// Other component fonts
	private Font progressBarFont;
	private Font sliderFont;
	private Font spinnerFont;
	private Font scrollPaneFont;

	private String testDataDir;

	private Map<String, Integer> moduleName2times = new HashMap<>();

	private int launchTimes = 0;

	private int currentToolTip = 0;

	private int iconHeight = 24;
	private int iconWidth = 24;

	private int tabIconHeight = 16;
	private int tabIconWidth = 16;
	private boolean should_auto_click_import = false;
	private boolean isEnglish = true;

	public LaunchProperty() {

		Font defaultFontWithPriority = DefaultFont.getDefaultFontFamily(Font.PLAIN, 12);

		setMenuFistLevelFont(defaultFontWithPriority);
		setMenuSecondLevelFont(defaultFontWithPriority);

		setSelectedTabTitleFont(defaultFontWithPriority.deriveFont(Font.BOLD));
		setUnSelectedTabTitleFont(defaultFontWithPriority);
		setDefaultFont(defaultFontWithPriority);
		setDefaultTitleFont(new Font(defaultFontWithPriority.getFamily(), Font.BOLD, 13));
		setDocumentFont(defaultFontWithPriority.deriveFont(16f));

		// Initialize dialog fonts
		setDialogTitleFont(new Font(defaultFontWithPriority.getFamily(), Font.BOLD, 14));
		setDialogContentFont(defaultFontWithPriority);
		setDialogButtonFont(defaultFontWithPriority);

		// Initialize component fonts
		setLabelFont(defaultFontWithPriority);
		setButtonFont(defaultFontWithPriority);
		setCheckBoxFont(defaultFontWithPriority);

		// Initialize input fonts
		setTextFieldFont(defaultFontWithPriority);
		// Use monospaced font for text area
		Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);
		setTextAreaFont(monospacedFont);
		setComboBoxFont(defaultFontWithPriority);

		// Initialize data display fonts
		setTableFont(defaultFontWithPriority.deriveFont(11f));
		setTableHeaderFont(new Font(defaultFontWithPriority.getFamily(), Font.BOLD, 12));
		setListFont(defaultFontWithPriority);
		setTreeFont(defaultFontWithPriority);

		// Initialize tool fonts
		setToolTipFont(defaultFontWithPriority.deriveFont(11f));
		setToolBarFont(defaultFontWithPriority.deriveFont(11f));

		// Initialize other component fonts
		setProgressBarFont(defaultFontWithPriority.deriveFont(10f));
		setSliderFont(defaultFontWithPriority.deriveFont(10f));
		setSpinnerFont(defaultFontWithPriority);
		setScrollPaneFont(defaultFontWithPriority);

		setTextAntiAliasString("TEXT_ANTIALIAS_ON");
		setTestDataDir(EGPSProperties.PROPERTIES_DIR);
	}

	public LaunchProperty(LaunchPropertyDTO launchProperty) {
		this.setLastLaunchedModuleClz(launchProperty.lastLaunchedModuleClz);
		this.setWidth(launchProperty.width);
		this.setHeight(launchProperty.height);
		this.setLocationX(launchProperty.locationX);
		this.setLocationY(launchProperty.locationY);
		this.setRestoreToDefault(launchProperty.restoreToDefault);
		this.setMenuFistLevelFont(convertFont(launchProperty.menuFistLevelFont));
		this.setMenuSecondLevelFont(convertFont(launchProperty.menuSecondLevelFont));
		this.setSelectedTabTitleFont(convertFont(launchProperty.selectedTabTitleFont));
		this.setUnSelectedTabTitleFont(convertFont(launchProperty.unSelectedTabTitleFont));
		this.setDefaultFont(convertFont(launchProperty.defaultFont));
		this.setDefaultTitleFont(convertFont(launchProperty.defaultTitleFont));
		this.setDocumentFont(convertFont(launchProperty.documentFont));

		// Convert dialog fonts
		this.setDialogTitleFont(convertFont(launchProperty.dialogTitleFont));
		this.setDialogContentFont(convertFont(launchProperty.dialogContentFont));
		this.setDialogButtonFont(convertFont(launchProperty.dialogButtonFont));

		// Convert component fonts
		this.setLabelFont(convertFont(launchProperty.labelFont));
		this.setButtonFont(convertFont(launchProperty.buttonFont));
		this.setCheckBoxFont(convertFont(launchProperty.checkBoxFont));

		// Convert input fonts
		this.setTextFieldFont(convertFont(launchProperty.textFieldFont));
		this.setTextAreaFont(convertFont(launchProperty.textAreaFont));
		this.setComboBoxFont(convertFont(launchProperty.comboBoxFont));

		// Convert data display fonts
		this.setTableFont(convertFont(launchProperty.tableFont));
		this.setTableHeaderFont(convertFont(launchProperty.tableHeaderFont));
		this.setListFont(convertFont(launchProperty.listFont));
		this.setTreeFont(convertFont(launchProperty.treeFont));

		// Convert tool fonts
		this.setToolTipFont(convertFont(launchProperty.toolTipFont));
		this.setToolBarFont(convertFont(launchProperty.toolBarFont));

		// Convert other component fonts
		this.setProgressBarFont(convertFont(launchProperty.progressBarFont));
		this.setSliderFont(convertFont(launchProperty.sliderFont));
		this.setSpinnerFont(convertFont(launchProperty.spinnerFont));
		this.setScrollPaneFont(convertFont(launchProperty.scrollPaneFont));

		this.setModuleName2times(launchProperty.moduleName2times);
		this.setLaunchTimes(launchProperty.launchTimes);
		this.setCurrentToolTip(launchProperty.currentToolTip);
		this.setIconHeight(launchProperty.iconHeight);
		this.setIconWidth(launchProperty.iconWidth);
		this.setTabIconHeight(launchProperty.tabIconHeight);
		this.setTabIconWidth(launchProperty.tabIconWidth);
		this.setShould_auto_click_import(launchProperty.should_auto_click_import);
		this.setTextAntiAliasString(launchProperty.antialiasOption);
		this.setEnglish(launchProperty.isEnglish);
		this.setTestDataDir(launchProperty.testDataDir);

	}

	private Font convertFont(FontDTO font) {
		String name = font.getName();
		int style = font.getStyle();
		int size = font.getSize();
		return new Font(name, style, size);
	}

	/**
	 * 
	 * @param name : the module name
	 * @return The module launch times except this time
	 */
	public int increaseTheModuleLaunchTime(String name) {
		int launchTimesExceptThisTime = 0;

		Integer integer = moduleName2times.get(name);

		if (integer == null) {
			launchTimesExceptThisTime = 0;
			moduleName2times.put(name, 1);
		} else {
			moduleName2times.put(name, 1 + integer);
			launchTimesExceptThisTime = integer;
		}

		return launchTimesExceptThisTime;
	}

	final static LaunchProperty getInstance() {
		String path = EGPSProperties.JSON_DIR.concat("/defaultGlobalProperties.json");
		LaunchProperty ret = null;
		File file = new File(path);
		if (file.exists()) {
			try {
				String readFileToString = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
				LaunchPropertyDTO propDTO = JSONObject.parseObject(readFileToString, LaunchPropertyDTO.class);
				ret = new LaunchProperty(propDTO);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (ret == null) {
			ret = new LaunchProperty();
		} else {
			if (Math.abs(ret.getLocationX()) > 18000 || Math.abs(ret.getLocationY()) > 18000) {
				ret.setWidth(1200);
				ret.setHeight(800);
				ret.setLocationX(0);
				ret.setLocationY(0);
				System.err.println("Found the mainframe location is abnormal, change it to default states.");
			}
		}
		return ret;
	}

	public void saveTheProperties(boolean clear) throws IOException {
		String path = EGPSProperties.JSON_DIR.concat("/defaultGlobalProperties.json");
		if (clear) {
			EGPSFileUtil.forceDelete(new File(path));
		} else {
			String jsonString = JSONObject.toJSONString(new LaunchPropertyDTO(this), true);
			FileUtils.writeStringToFile(new File(path), jsonString, StandardCharsets.US_ASCII);
		}
	}

    public String getLastLaunchedModuleClz() {
        return lastLaunchedModuleClz;
    }

    public void setLastLaunchedModuleClz(String lastLaunchedModuleClz) {
        this.lastLaunchedModuleClz = lastLaunchedModuleClz;
    }

    public void setTextAntiAliasString(String textAntiAliasString) {
        this.textAntiAliasString = textAntiAliasString;
    }

	public String getantialiasOption() {
        return textAntiAliasString;
    }

    /**
     * This is the for quick get object, because the value will be frequently
     * invoked.
     */
    public Object getGraphicsTextAntialiasObj() {
        return graphicsTextAntialiasObj;
    }

    public void setGraphicsTextAntialiasObj(Object graphicsTextAntialiasObj) {
        this.graphicsTextAntialiasObj = graphicsTextAntialiasObj;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public boolean isRestoreToDefault() {
        return restoreToDefault;
    }

    public void setRestoreToDefault(boolean restoreToDefault) {
        this.restoreToDefault = restoreToDefault;
    }

    public Font getMenuFistLevelFont() {
        return menuFistLevelFont;
    }

    public void setMenuFistLevelFont(Font menuFistLevelFont) {
        this.menuFistLevelFont = menuFistLevelFont;
    }

    public Font getMenuSecondLevelFont() {
        return menuSecondLevelFont;
    }

    public void setMenuSecondLevelFont(Font menuSecondLevelFont) {
        this.menuSecondLevelFont = menuSecondLevelFont;
    }

    public Font getSelectedTabTitleFont() {
        return selectedTabTitleFont;
    }

    public void setSelectedTabTitleFont(Font selectedTabTitleFont) {
        this.selectedTabTitleFont = selectedTabTitleFont;
    }

    public Font getUnSelectedTabTitleFont() {
        return unSelectedTabTitleFont;
    }

    public void setUnSelectedTabTitleFont(Font unSelectedTabTitleFont) {
        this.unSelectedTabTitleFont = unSelectedTabTitleFont;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }

    public void setDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
    }

    public Font getDefaultTitleFont() {
        return defaultTitleFont;
    }

    public void setDefaultTitleFont(Font defaultTitleFont) {
        this.defaultTitleFont = defaultTitleFont;
    }

    public Font getDocumentFont() {
        return documentFont;
    }

    public void setDocumentFont(Font documentFont) {
        this.documentFont = documentFont;
    }

    /**
     *  注意，这里的
     * 简介模块的使用次数为用来作为整个软件的启动次数
     *
     * @return 返回不可修改的 Map 视图，防止外部修改
     */
    public Map<String, Integer> getModuleName2times() {
        return Collections.unmodifiableMap(moduleName2times);
    }

    public void setModuleName2times(Map<String, Integer> moduleName2times) {
        this.moduleName2times = moduleName2times;
    }

    public int getLaunchTimes() {
        return launchTimes;
    }

    public void setLaunchTimes(int launchTimes) {
        this.launchTimes = launchTimes;
    }

    public int getCurrentToolTip() {
        return currentToolTip;
    }

    public void setCurrentToolTip(int currentToolTip) {
        this.currentToolTip = currentToolTip;
    }

    public int getIconHeight() {
        return iconHeight;
    }

    public void setIconHeight(int iconHeight) {
        this.iconHeight = iconHeight;
    }

    public int getIconWidth() {
        return iconWidth;
    }

    public void setIconWidth(int iconWidth) {
        this.iconWidth = iconWidth;
    }

    public int getTabIconHeight() {
        return tabIconHeight;
    }

    public void setTabIconHeight(int tabIconHeight) {
        this.tabIconHeight = tabIconHeight;
    }

    public int getTabIconWidth() {
        return tabIconWidth;
    }

    public void setTabIconWidth(int tabIconWidth) {
        this.tabIconWidth = tabIconWidth;
    }

    public boolean isShould_auto_click_import() {
        return should_auto_click_import;
    }

    public void setShould_auto_click_import(boolean should_auto_click_import) {
        this.should_auto_click_import = should_auto_click_import;
    }

    public boolean isEnglish() {
        return isEnglish;
    }

    public void setEnglish(boolean english) {
        isEnglish = english;
    }

    // Dialog fonts getters and setters
    public Font getDialogTitleFont() {
        return dialogTitleFont;
    }

    public void setDialogTitleFont(Font dialogTitleFont) {
        this.dialogTitleFont = dialogTitleFont;
    }

    public Font getDialogContentFont() {
        return dialogContentFont;
    }

    public void setDialogContentFont(Font dialogContentFont) {
        this.dialogContentFont = dialogContentFont;
    }

    public Font getDialogButtonFont() {
        return dialogButtonFont;
    }

    public void setDialogButtonFont(Font dialogButtonFont) {
        this.dialogButtonFont = dialogButtonFont;
    }

    // Component fonts getters and setters
    public Font getLabelFont() {
        return labelFont;
    }

    public void setLabelFont(Font labelFont) {
        this.labelFont = labelFont;
    }

    public Font getButtonFont() {
        return buttonFont;
    }

    public void setButtonFont(Font buttonFont) {
        this.buttonFont = buttonFont;
    }

    public Font getCheckBoxFont() {
        return checkBoxFont;
    }

    public void setCheckBoxFont(Font checkBoxFont) {
        this.checkBoxFont = checkBoxFont;
    }

    // Input fonts getters and setters
    public Font getTextFieldFont() {
        return textFieldFont;
    }

    public void setTextFieldFont(Font textFieldFont) {
        this.textFieldFont = textFieldFont;
    }

    public Font getTextAreaFont() {
        return textAreaFont;
    }

    public void setTextAreaFont(Font textAreaFont) {
        this.textAreaFont = textAreaFont;
    }

    public Font getComboBoxFont() {
        return comboBoxFont;
    }

    public void setComboBoxFont(Font comboBoxFont) {
        this.comboBoxFont = comboBoxFont;
    }

    // Data display fonts getters and setters
    public Font getTableFont() {
        return tableFont;
    }

    public void setTableFont(Font tableFont) {
        this.tableFont = tableFont;
    }

    public Font getTableHeaderFont() {
        return tableHeaderFont;
    }

    public void setTableHeaderFont(Font tableHeaderFont) {
        this.tableHeaderFont = tableHeaderFont;
    }

    public Font getListFont() {
        return listFont;
    }

    public void setListFont(Font listFont) {
        this.listFont = listFont;
    }

    public Font getTreeFont() {
        return treeFont;
    }

    public void setTreeFont(Font treeFont) {
        this.treeFont = treeFont;
    }

    // Tool fonts getters and setters
    public Font getToolTipFont() {
        return toolTipFont;
    }

    public void setToolTipFont(Font toolTipFont) {
        this.toolTipFont = toolTipFont;
    }

    public Font getToolBarFont() {
        return toolBarFont;
    }

    public void setToolBarFont(Font toolBarFont) {
        this.toolBarFont = toolBarFont;
    }

    // Other component fonts getters and setters
    public Font getProgressBarFont() {
        return progressBarFont;
    }

    public void setProgressBarFont(Font progressBarFont) {
        this.progressBarFont = progressBarFont;
    }

    public Font getSliderFont() {
        return sliderFont;
    }

    public void setSliderFont(Font sliderFont) {
        this.sliderFont = sliderFont;
    }

    public Font getSpinnerFont() {
        return spinnerFont;
    }

    public void setSpinnerFont(Font spinnerFont) {
        this.spinnerFont = spinnerFont;
    }

    public Font getScrollPaneFont() {
        return scrollPaneFont;
    }

    public void setScrollPaneFont(Font scrollPaneFont) {
        this.scrollPaneFont = scrollPaneFont;
    }

    /**
     * Apply all fonts to UIManager for global Swing component styling
     */
    public void applyFontsToUIManager() {
        // Dialog fonts
        UIManager.put("OptionPane.messageFont", dialogContentFont);
        UIManager.put("OptionPane.buttonFont", dialogButtonFont);
        UIManager.put("InternalFrame.titleFont", dialogTitleFont);

        // Component fonts
        UIManager.put("Label.font", labelFont);
        UIManager.put("Button.font", buttonFont);
        UIManager.put("CheckBox.font", checkBoxFont);
        UIManager.put("RadioButton.font", checkBoxFont);
        UIManager.put("ToggleButton.font", buttonFont);

        // Input fonts
        UIManager.put("TextField.font", textFieldFont);
        UIManager.put("FormattedTextField.font", textFieldFont);
        UIManager.put("PasswordField.font", textFieldFont);
        UIManager.put("TextArea.font", textAreaFont);
        UIManager.put("TextPane.font", textAreaFont);
        UIManager.put("EditorPane.font", textAreaFont);
        UIManager.put("ComboBox.font", comboBoxFont);

        // Data display fonts
        UIManager.put("Table.font", tableFont);
        UIManager.put("TableHeader.font", tableHeaderFont);
        UIManager.put("List.font", listFont);
        UIManager.put("Tree.font", treeFont);

        // Tool fonts
        UIManager.put("ToolTip.font", toolTipFont);
        UIManager.put("ToolBar.font", toolBarFont);

        // Other component fonts
        UIManager.put("ProgressBar.font", progressBarFont);
        UIManager.put("Slider.font", sliderFont);
        UIManager.put("Spinner.font", spinnerFont);
        UIManager.put("ScrollPane.font", scrollPaneFont);

        // Menu fonts (existing)
        UIManager.put("Menu.font", menuFistLevelFont);
        UIManager.put("MenuBar.font", menuFistLevelFont);
        UIManager.put("MenuItem.font", menuSecondLevelFont);
        UIManager.put("CheckBoxMenuItem.font", menuSecondLevelFont);
        UIManager.put("RadioButtonMenuItem.font", menuSecondLevelFont);
        UIManager.put("PopupMenu.font", menuSecondLevelFont);

        // Tab fonts (existing)
        UIManager.put("TabbedPane.font", selectedTabTitleFont);

        // Panel fonts
        UIManager.put("Panel.font", defaultFont);
        UIManager.put("TitledBorder.font", defaultTitleFont);
    }

    /**
     * DefaultFont is part of the eGPS desktop application.
     */
    static class DefaultFont {

		private static final String[] FONTS = { "Microsoft YaHei UI", "Arial", "SimSun", "Times New Roman" };

		static Font getDefaultFontFamily(int style, int size) {

			String[] availableFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

			for (String string : FONTS) {
				for (String s : availableFonts) {
					if (s.equalsIgnoreCase(string)) {
						return new Font(s, style, size);
					}
				}
			}
			return new FontUIResource("System default", style, size);
		}

	}

	public Object getTextAntiAliasString() {
		if (getGraphicsTextAntialiasObj() == null) {
			initializeAntialiasOption();
		}

		return getGraphicsTextAntialiasObj();
	}

	void initializeAntialiasOption() {
		switch (getantialiasOption()) {
		case "TEXT_ANTIALIAS_ON":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			break;
		case "TEXT_ANTIALIAS_OFF":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			break;
		case "TEXT_ANTIALIAS_DEFAULT":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
			break;
		case "TEXT_ANTIALIAS_GASP":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			break;
		case "TEXT_ANTIALIAS_LCD_HRGB":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			break;
		case "TEXT_ANTIALIAS_LCD_HBGR":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
			break;
		case "TEXT_ANTIALIAS_LCD_VRGB":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB);
			break;
		case "TEXT_ANTIALIAS_LCD_VBGR":
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
			break;
		default:
			// Handle unknown option or use a default setting
			setGraphicsTextAntialiasObj(RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
		}

	}

	public String getTestDataDir() {
		return testDataDir;
	}

	public void setTestDataDir(String testDataDir) {
		this.testDataDir = testDataDir;
	}

}
