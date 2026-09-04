package egps2.builtin.modules.largetextedi.gui;

import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

/**
 * The user can invoke static 'getFileChooser' method to get a instance of
 * saveFileChooser. Note: The user must invoke static 'setEGPSLastPath' method
 * to save the lastPath.
 * 
 * The encapsulation class of FileChooser. This class can memory the lastPath
 * which is the path of File that the user selected last time.
 * 
 * 这个是不叫老的类了，不推荐使用这个类。用EGPSFileChooser替代！
 * 
 * @author GF,YDL,MHl
 *
 */
public class EGPSOutputFileChooser4TextEditor extends JFileChooser {

	private static final long serialVersionUID = -5025987842538940685L;
	private static String lastPath = null;
	private static Preferences pref;
	private static EGPSOutputFileChooser4TextEditor instance = null;

	private EGPSOutputFileChooser4TextEditor(String currentPath) {
		super(currentPath);
		setDialogTitle("Save as");
		setAcceptAllFileFilterUsed(false);
		setMultiSelectionEnabled(false);
	}

	public static EGPSOutputFileChooser4TextEditor getFileChooser() {
		instance = null;
		pref = Preferences.userRoot().node("EGPSOutputFileChooser");
		lastPath = pref.get("lastPath", "");
		if (instance == null) {
			instance = new EGPSOutputFileChooser4TextEditor(getLastPath());
		}
		return instance;
	}

	private static String getLastPath() {
		return lastPath;
	}

	public static void setEGPSLastPath(String lastPaths) {
		lastPath = lastPaths;
		pref.put("lastPath", lastPath);
	}
}