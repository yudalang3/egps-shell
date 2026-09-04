package egps2.builtin.modules.largetextedi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.Icon;

import egps2.utils.EGPSIconUtil;
import egps2.UnifiedAccessPoint;
import egps2.frame.ModuleFace;
import egps2.frame.MyFrame;
import egps2.modulei.IconBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author yudalang
 * @date Dec 27, 2018 3:33:00 PM
 */
public class MethodsForText2Editor {

	private static final Logger log = LoggerFactory.getLogger(MethodsForText2Editor.class);

	public void addNewTextEditorTab(File file, boolean isBlank) {

		IndependentModuleLoader independentModuleLoader = new IndependentModuleLoader();
		independentModuleLoader.path = file.getAbsolutePath();

		ModuleFace face = independentModuleLoader.getFace();

		IconBean iconBean = independentModuleLoader.getIcon();
		Icon icon = null;
		try {
			icon = EGPSIconUtil
					.getIconFromSVGByStreamSoftwaresizeWithTabIcon(iconBean.getInputStream(), iconBean.isSVG());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		if (isBlank) {
			instanceFrame.addTab2mainTabbedPanel(independentModuleLoader.getTabName(), icon, face,
					independentModuleLoader.getShortDescription());
		} else {
			String currentFileName = file.getName();
			if (currentFileName.length() > 20) {
				currentFileName = currentFileName.replaceAll(currentFileName.substring(20), "...");
			}

			instanceFrame.addTab2mainTabbedPanel(currentFileName, icon, face,
					independentModuleLoader.getShortDescription());
			
		}
		// 因为这样才不会因为egpsTextPanel不会锁住。
//		try {
//			TimeUnit.MILLISECONDS.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
	}

}
