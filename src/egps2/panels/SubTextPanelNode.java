package egps2.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

@SuppressWarnings("serial")
/**
 * SubTextPanelNode is a reusable Swing panel or dialog within eGPS.
 */
public class SubTextPanelNode extends JPanel{
	
	private FontAppearancePanel upComp;
	private FontAppearancePanel downComp;
	
	private String title;
	
	public SubTextPanelNode(String upTitle, String downTitle,Font titleFont ,Font defaultFont) {
		JPanel rightContentPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(rightContentPanel, BoxLayout.Y_AXIS);
		rightContentPanel.setLayout(boxLayout);

		String[] availableFamilyList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		upComp = new FontAppearancePanel(upTitle, availableFamilyList, titleFont );
		rightContentPanel.add(upComp);
		downComp = new FontAppearancePanel(downTitle, availableFamilyList, defaultFont);
		rightContentPanel.add(downComp);
		
		
		
		setLayout(new BorderLayout());
		rightContentPanel.setBorder(null);
		add(rightContentPanel, BorderLayout.CENTER);
		
		setBorder(null);
	}
	
	public Font getUpFont() {
		return upComp.getExampleLabel().getFont();
	}
	
	public Font getDownFont() {
		return downComp.getExampleLabel().getFont();
	}
	
	@Override
	public String toString() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
