package egps.lnf.ch12_progress;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.DimensionUIResource;

import egps.lnf.BeautyEyeLNFHelper;

/**
 * JProgress bar look and feel
 */
public class __UI__ {

	public static void uiImpl() {
		UIManager.put("ProgressBar.background", new ColorUIResource(BeautyEyeLNFHelper.commonBackgroundColor));
		UIManager.put("ProgressBar.selectionForeground", new ColorUIResource(BeautyEyeLNFHelper.commonBackgroundColor));
		UIManager.put("ProgressBar.horizontalSize", new DimensionUIResource(146, 15));// 默认是146,12
		UIManager.put("ProgressBar.verticalSize", new DimensionUIResource(15, 146));// 默认是12,146
		UIManager.put("ProgressBar.border", new BorderUIResource(BorderFactory.createEmptyBorder(0, 0, 0, 0)));
	}
}
