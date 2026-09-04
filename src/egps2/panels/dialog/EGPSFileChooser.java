package egps2.panels.dialog;

import java.awt.HeadlessException;
import java.io.File;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import egps2.EGPSProperties;
import utils.storage.MapPersistence;
import egps2.UnifiedAccessPoint;


/**
 * 
 * <h1>目的</h1>
 * <p>
 *  
 * 对原来的类增加记忆功能。
 *  </p>
 *  
 * 
 * <h1>使用方法</h1>
 * 
 * <blockquote>
 * 用法同原来的JFileChooser
 * </blockquote>
 * 
 * <h1>注意点</h1>
 * <ol>
 * <li>
 * 用法同原来的JFileChooser
 * </li>
 * </ol>
 * 
 * @implSpec
 * 就是对原来 JfileChooser进行了封装，但是增加了记忆的功能。
 * 
 * @author yudal
 *
 */
@SuppressWarnings("serial")
public class EGPSFileChooser extends JFileChooser{

	private String storePath = EGPSProperties.JSON_DIR.concat("/egps.input.chooser.gz");
	private Map<String, String> str2strMap;
	private String clasNamePath;
	
	public EGPSFileChooser(Class<?> targetClz) {
		str2strMap = MapPersistence.getStr2strMap(storePath);

		clasNamePath = targetClz.getName();
		String string = str2strMap.get(clasNamePath);
		if (string == null) {
			string = "";
		}
		String lastPath = string;
		if (lastPath != null) {
			setCurrentDirectory(new File(lastPath));
		}
		
	}
	
	
	
	public int showOpenDialog() throws HeadlessException {
		String str = UnifiedAccessPoint.getResourceString("dialog.open");
		setDialogTitle(str);
		int res = super.showOpenDialog(UnifiedAccessPoint.getInstanceFrame());
		if (res == APPROVE_OPTION) {
			File file = getSelectedFile();
			str2strMap.put( clasNamePath, file.getParent());
			MapPersistence.storeStr2strMap(str2strMap,storePath);
		}
		
		return res;
	}
	
	public int showSaveDialog() throws HeadlessException {
		String str = UnifiedAccessPoint.getResourceString("dialog.save");
		setDialogTitle(str);
		int res = super.showSaveDialog(UnifiedAccessPoint.getInstanceFrame());
		
		if (res == APPROVE_OPTION) {
			File file = getSelectedFile();
			str2strMap.put( clasNamePath, file.getParent());
			MapPersistence.storeStr2strMap(str2strMap,storePath);
		}
		
		return res;
	}
	
	
	public int showOpenDialog(JFrame jFrame) throws HeadlessException {
		String str = UnifiedAccessPoint.getResourceString("dialog.open");
		setDialogTitle(str);
		int res = super.showOpenDialog(jFrame);
		if (res == APPROVE_OPTION) {
			File file = getSelectedFile();
			str2strMap.put( clasNamePath, file.getParent());
			MapPersistence.storeStr2strMap(str2strMap,storePath);
		}
		
		return res;
	}
	
	public int showSaveDialog(JFrame jFrame) throws HeadlessException {
		
		String str = UnifiedAccessPoint.getResourceString("dialog.save");
		
		setDialogTitle(str);
		int res = super.showSaveDialog(jFrame);
		
		if (res == APPROVE_OPTION) {
			File file = getSelectedFile();
			str2strMap.put( clasNamePath, file.getParent());
			MapPersistence.storeStr2strMap(str2strMap,storePath);
		}
		
		return res;
	}
}
