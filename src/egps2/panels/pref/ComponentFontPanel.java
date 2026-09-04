package egps2.panels.pref;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import egps2.panels.FontAppearancePanel;

@SuppressWarnings("serial")
/**
 * ComponentFontPanel manages component-related font settings
 */
public class ComponentFontPanel extends JPanel {

	private FontAppearancePanel labelFontPanel;
	private FontAppearancePanel buttonFontPanel;
	private FontAppearancePanel checkBoxFontPanel;

	private String title = "Component fonts";

	public ComponentFontPanel(Font labelFont, Font buttonFont, Font checkBoxFont) {
		JPanel rightContentPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(rightContentPanel, BoxLayout.Y_AXIS);
		rightContentPanel.setLayout(boxLayout);

		String[] availableFamilyList = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();

		labelFontPanel = new FontAppearancePanel("Label font", availableFamilyList, labelFont);
		rightContentPanel.add(labelFontPanel);

		buttonFontPanel = new FontAppearancePanel("Button font", availableFamilyList, buttonFont);
		rightContentPanel.add(buttonFontPanel);

		checkBoxFontPanel = new FontAppearancePanel("CheckBox / RadioButton font", availableFamilyList, checkBoxFont);
		rightContentPanel.add(checkBoxFontPanel);

		setLayout(new BorderLayout());
		rightContentPanel.setBorder(null);
		add(rightContentPanel, BorderLayout.CENTER);

		setBorder(null);
	}

	public Font getLabelFont() {
		return labelFontPanel.getExampleLabel().getFont();
	}

	public Font getButtonFont() {
		return buttonFontPanel.getExampleLabel().getFont();
	}

	public Font getCheckBoxFont() {
		return checkBoxFontPanel.getExampleLabel().getFont();
	}

	@Override
	public String toString() {
		return this.title;
	}
}
