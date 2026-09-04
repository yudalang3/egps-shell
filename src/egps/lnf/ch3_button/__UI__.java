package egps.lnf.ch3_button;

import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;

import egps.lnf.BeautyEyeLNFHelper;

public class __UI__
{
	
	public static void uiImpl()
	{
		//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> JButton相关ui属性设定
		UIManager.put("Button.background",new ColorUIResource(BeautyEyeLNFHelper.commonBackgroundColor));
		//Button.foreground的设定不起效，这可能是LNF里的bug，因NLLookAndFeel
//		是继承自它们所以暂时无能为力，就这么的吧，以后再说
		UIManager.put("Button.foreground",new ColorUIResource(BeautyEyeLNFHelper.commonForegroundColor));
		
		//以下属性将决定按钮获得焦点时的焦点虚线框的绘制偏移量哦
		UIManager.put("Button.dashedRectGapX",3);//windows LNF中默认是3
		UIManager.put("Button.dashedRectGapY",3);//windows LNF中默认是3
		UIManager.put("Button.dashedRectGapWidth",6);//windows LNF中默认是6
		UIManager.put("Button.dashedRectGapHeight",6);//windows LNF中默认是6
		
		UIManager.put("ButtonUI", BEButtonUI.class.getName());
		UIManager.put("Button.margin",new InsetsUIResource(6, 6, 6, 6));//new InsetsUIResource(6, 8, 6, 8));
//		//此border可以与Button.margin连合使用，而者之和即查整个Button的内衬哦
		UIManager.put("Button.border" ,new BEButtonUI
					.XPEmptyBorder(new Insets(0,0,0,0)));//default is 3,3,3,3
		//获得焦点时的虚线框颜色
		UIManager.put("Button.focus",new ColorUIResource(130,130,130));
	}
}
