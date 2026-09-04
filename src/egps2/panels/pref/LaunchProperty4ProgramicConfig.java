package egps2.panels.pref;

import java.awt.Font;

import com.alibaba.fastjson.annotation.JSONType;

import egps2.LaunchProperty;

/**
 * 这个类是 <code>LaunchProperty</code> 类的简化类，因为那个类有一些属性是不需要设置的。
 * 
 * @author yudalang
 *
 */
//使用 @JSONType 注解指定字段顺序
@JSONType(orders = {
		"defaultFont",
		"defaultTitleFont",
		"selectedTabTitleFont",
		"unSelectedTabTitleFont",
		"menuFistLevelFont",
		"menuSecondLevelFont",
		"documentFont",
		"iconHeight",
		"iconWidth",
		"tabIconHeight",
		"tabIconWidth",
		"should_auto_click_import"
		})
/**
 * LaunchProperty4ProgramicConfig is a reusable Swing panel or dialog within eGPS.
 */
public class LaunchProperty4ProgramicConfig {

	public Font menuFistLevelFont = new Font("simSun", Font.PLAIN, 12);
	public Font menuSecondLevelFont = new Font("simSun", Font.PLAIN, 12);
	
	public Font selectedTabTitleFont = new Font("simSun", Font.BOLD, 12);
	public Font unSelectedTabTitleFont = new Font("simSun", Font.PLAIN, 12);
	public Font defaultFont = new Font("simSun", Font.PLAIN, 12);
	public Font defaultTitleFont = new Font("simSun", Font.BOLD, 13);
	
	public Font documentFont;
	
	public int iconHeight = 24;
	public int iconWidth = 24;
	
	public int tabIconHeight = 16;
	public int tabIconWidth = 16;
	
	public boolean should_auto_click_import = false;
	
	public LaunchProperty4ProgramicConfig() {
		
	}
	
	public LaunchProperty4ProgramicConfig(LaunchProperty prot) {
		this.menuFistLevelFont = prot.getMenuFistLevelFont();
		this.menuSecondLevelFont = prot.getMenuSecondLevelFont();
		this.selectedTabTitleFont = prot.getSelectedTabTitleFont();
		this.unSelectedTabTitleFont = prot.getUnSelectedTabTitleFont();
		this.defaultFont = prot.getDefaultFont();
		this.defaultTitleFont = prot.getDefaultTitleFont();
		this.iconHeight = prot.getIconHeight();
		this.iconWidth = prot.getIconWidth();
		this.tabIconHeight = prot.getTabIconHeight();
		this.tabIconWidth = prot.getTabIconWidth();
		this.should_auto_click_import = prot.isShould_auto_click_import();
		this.documentFont = prot.getDocumentFont();
	}
	
}
