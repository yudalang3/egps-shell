package egps2.utils;

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.annotation.JSONType;

import egps2.LaunchProperty;

//使用 @JSONType 注解指定字段顺序
@JSONType(orders = { "width",
		"height",
		"locationX",
		"locationY",
		"should_auto_click_import",
		"antialiasOption",
		"defaultFont",
		"defaultTitleFont",
		"documentFont",
		"selectedTabTitleFont",
		"unSelectedTabTitleFont",
		"menuFistLevelFont",
		"menuSecondLevelFont",
		"dialogTitleFont",
		"dialogContentFont",
		"dialogButtonFont",
		"labelFont",
		"buttonFont",
		"checkBoxFont",
		"textFieldFont",
		"textAreaFont",
		"comboBoxFont",
		"tableFont",
		"tableHeaderFont",
		"listFont",
		"treeFont",
		"toolTipFont",
		"toolBarFont",
		"progressBarFont",
		"sliderFont",
		"spinnerFont",
		"scrollPaneFont",
		"iconHeight",
		"iconWidth",
		"tabIconHeight",
		"tabIconWidth",
		"testDataDir",
		"restoreToDefault",
		"moduleName2times",
		"launchTimes",
		"currentToolTip"})
/**
 * LaunchPropertyDTO provides shared utility logic for eGPS modules and UI.
 */
public class LaunchPropertyDTO {

	public String lastLaunchedModuleClz;

	public int width = 1500;
	public int height = 900;
	public int locationX = 0;
	public int locationY = 0;
	public boolean restoreToDefault = false;

	public FontDTO menuFistLevelFont;
	public FontDTO menuSecondLevelFont;

	public FontDTO selectedTabTitleFont;
	public FontDTO unSelectedTabTitleFont;
	public FontDTO defaultFont;
	public FontDTO defaultTitleFont;
	public FontDTO documentFont;

	// Dialog fonts
	public FontDTO dialogTitleFont;
	public FontDTO dialogContentFont;
	public FontDTO dialogButtonFont;

	// Component fonts
	public FontDTO labelFont;
	public FontDTO buttonFont;
	public FontDTO checkBoxFont;

	// Input fonts
	public FontDTO textFieldFont;
	public FontDTO textAreaFont;
	public FontDTO comboBoxFont;

	// Data display fonts
	public FontDTO tableFont;
	public FontDTO tableHeaderFont;
	public FontDTO listFont;
	public FontDTO treeFont;

	// Tool fonts
	public FontDTO toolTipFont;
	public FontDTO toolBarFont;

	// Other component fonts
	public FontDTO progressBarFont;
	public FontDTO sliderFont;
	public FontDTO spinnerFont;
	public FontDTO scrollPaneFont;

	/**
	 * com.sinh.beauty.unisoft.module.intro.IntroMain.class 注意，这里的
	 * 简介模块的使用次数为用来作为整个软件的启动次数
	 */
	public Map<String, Integer> moduleName2times = new HashMap<>();

	public int launchTimes = 0;

	public int currentToolTip = 0;

	public int iconHeight = 24;
	public int iconWidth = 24;

	public int tabIconHeight = 16;
	public int tabIconWidth = 16;
	public boolean should_auto_click_import = false;

	public String antialiasOption;

	public boolean isEnglish = true;

	public String testDataDir;

	public LaunchPropertyDTO() {

	}

	public LaunchPropertyDTO(LaunchProperty launchProperty) {
		this.lastLaunchedModuleClz = launchProperty.getLastLaunchedModuleClz();
		this.width = launchProperty.getWidth();
		this.height = launchProperty.getHeight();
		this.locationX = launchProperty.getLocationX();
		this.locationY = launchProperty.getLocationY();
		this.restoreToDefault = launchProperty.isRestoreToDefault();
		this.menuFistLevelFont = convertFont(launchProperty.getMenuFistLevelFont());
		this.menuSecondLevelFont = convertFont(launchProperty.getMenuSecondLevelFont());
		this.selectedTabTitleFont = convertFont(launchProperty.getSelectedTabTitleFont());
		this.unSelectedTabTitleFont = convertFont(launchProperty.getUnSelectedTabTitleFont());
		this.defaultFont = convertFont(launchProperty.getDefaultFont());
		this.defaultTitleFont = convertFont(launchProperty.getDefaultTitleFont());
		this.documentFont = convertFont(launchProperty.getDocumentFont());

		// Convert dialog fonts
		this.dialogTitleFont = convertFont(launchProperty.getDialogTitleFont());
		this.dialogContentFont = convertFont(launchProperty.getDialogContentFont());
		this.dialogButtonFont = convertFont(launchProperty.getDialogButtonFont());

		// Convert component fonts
		this.labelFont = convertFont(launchProperty.getLabelFont());
		this.buttonFont = convertFont(launchProperty.getButtonFont());
		this.checkBoxFont = convertFont(launchProperty.getCheckBoxFont());

		// Convert input fonts
		this.textFieldFont = convertFont(launchProperty.getTextFieldFont());
		this.textAreaFont = convertFont(launchProperty.getTextAreaFont());
		this.comboBoxFont = convertFont(launchProperty.getComboBoxFont());

		// Convert data display fonts
		this.tableFont = convertFont(launchProperty.getTableFont());
		this.tableHeaderFont = convertFont(launchProperty.getTableHeaderFont());
		this.listFont = convertFont(launchProperty.getListFont());
		this.treeFont = convertFont(launchProperty.getTreeFont());

		// Convert tool fonts
		this.toolTipFont = convertFont(launchProperty.getToolTipFont());
		this.toolBarFont = convertFont(launchProperty.getToolBarFont());

		// Convert other component fonts
		this.progressBarFont = convertFont(launchProperty.getProgressBarFont());
		this.sliderFont = convertFont(launchProperty.getSliderFont());
		this.spinnerFont = convertFont(launchProperty.getSpinnerFont());
		this.scrollPaneFont = convertFont(launchProperty.getScrollPaneFont());

		this.moduleName2times = launchProperty.getModuleName2times();
		this.launchTimes = launchProperty.getLaunchTimes();
		this.currentToolTip = launchProperty.getCurrentToolTip();
		this.iconHeight = launchProperty.getIconHeight();
		this.iconWidth = launchProperty.getIconWidth();
		this.tabIconHeight = launchProperty.getTabIconHeight();
		this.tabIconWidth = launchProperty.getTabIconWidth();
		this.should_auto_click_import = launchProperty.isShould_auto_click_import();
		this.antialiasOption = launchProperty.getantialiasOption();
		this.isEnglish = launchProperty.isEnglish();
		this.testDataDir = launchProperty.getTestDataDir();

	}

	private FontDTO convertFont(Font font) {
		FontDTO fontDTO = new FontDTO(font);
		return fontDTO;
	}


//	public static void main(String[] args) {
//		// 初始化 Font 对象
//
//		LaunchProperty launchProperty = new LaunchProperty();
//		// 创建 DTO
//		LaunchPropertyDTO fontDTO = new LaunchPropertyDTO(launchProperty);
//
//		LaunchProperty launchProperty2 = new LaunchProperty(fontDTO);
//		// 序列化为 JSON
//		String jsonString = JSONObject.toJSONString(fontDTO, true);
//
//		// 输出 JSON 字符串
//		System.out.println(jsonString);
//	}
}
