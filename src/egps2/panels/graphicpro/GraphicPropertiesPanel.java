package egps2.panels.graphicpro;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.jidesoft.swing.JideTabbedPane;

import egps2.utils.EGPSIconUtil;
import egps2.frame.InstantFillAndLineJPanel;
import egps2.frame.InstantSizeAndPositionJPanel;

@SuppressWarnings("serial")
/**
 * GraphicPropertiesPanel is a reusable Swing panel or dialog within eGPS.
 */
public class GraphicPropertiesPanel extends JPanel {

	private InstantFillAndLineJPanel fillAndLineJPanel;
	private InstantSizeAndPositionJPanel sizeAndPositionJPanel;

	/**
	 * Create the panel.
	 */
	public GraphicPropertiesPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPanel = getTabbedPanel();
		add(tabbedPanel, BorderLayout.CENTER);

	}
	
	public JTabbedPane getTabbedPanel() {
		JideTabbedPane tabbedPane = new JideTabbedPane(JideTabbedPane.TOP);
		
		
		
		ImageIcon imageIconOfFillAndLine = null;
		ImageIcon imageIconOfDiminsion = null;
		try {
			InputStream resource = getClass().getResourceAsStream("/images/toolbar/painting.svg");
			imageIconOfFillAndLine = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
			InputStream resource2 = getClass().getResourceAsStream("/images/toolbar/dimensions.svg");
			imageIconOfDiminsion = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource2, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		fillAndLineJPanel = new InstantFillAndLineJPanel();
		tabbedPane.addTab("", imageIconOfFillAndLine, fillAndLineJPanel, "<html>Fill and line: <br>Set the shape properties, i.e. fill and line.");
		
		sizeAndPositionJPanel = new InstantSizeAndPositionJPanel();
		tabbedPane.addTab("", imageIconOfDiminsion, sizeAndPositionJPanel, "<html>Size and other properties: <br>Set the size properties of shape.");
		
		return tabbedPane;
	}

	public void refreshStates() {
		fillAndLineJPanel.refreshStates();
		sizeAndPositionJPanel.refreshStates();
	}


}
