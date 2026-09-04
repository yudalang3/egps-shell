package egps2.panels.pref;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import egps2.LaunchProperty;
import egps2.panels.FontAppearancePanel;

@SuppressWarnings("serial")
/**
 * HtmlDocumentPanel is a reusable Swing panel or dialog within eGPS.
 */
public class HtmlDocumentPanel extends JPanel implements ActionListener{

	private LaunchProperty launchProperty;

	private FontAppearancePanel upComp;
	
	private String title = "Document font";
	
	public Font getUpFont() {
		return upComp.getExampleLabel().getFont();
	}
	
	public HtmlDocumentPanel(LaunchProperty launchProperty) {
		
		setLayout(new BorderLayout());

		String[] availableFamilyList = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		upComp = new FontAppearancePanel(title, availableFamilyList, launchProperty.getDocumentFont());
		
		add(upComp, BorderLayout.CENTER);
		
		this.launchProperty = launchProperty;
		
	}
	

	@Override
	public String toString() {
		return title;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JCheckBox source = (JCheckBox) e.getSource();
		launchProperty.setShould_auto_click_import(source.isSelected());
		
	}

}
