package egps2.panels.reusablecom;

import java.awt.Font;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;

import egps2.UnifiedAccessPoint;

/**
 * ReusableCompoentBuilder is a reusable Swing panel or dialog within eGPS.
 */
public class ReusableCompoentBuilder {

	
	public static JXTaskPane wrapperJPanelWithJXTaskPane(JPanel jPanel, String name) {
		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
		JXTaskPane tmpJxTaskPane = new JXTaskPane();
		tmpJxTaskPane.setFont(defaultTitleFont);
		tmpJxTaskPane.setTitle(name);
		tmpJxTaskPane.add(jPanel);
		return tmpJxTaskPane;
	}

}
