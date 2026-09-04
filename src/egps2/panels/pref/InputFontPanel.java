package egps2.panels.pref;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import egps2.panels.FontAppearancePanel;

@SuppressWarnings("serial")
/**
 * InputFontPanel manages input component font settings
 */
public class InputFontPanel extends JPanel {

	private FontAppearancePanel textFieldFontPanel;
	private FontAppearancePanel textAreaFontPanel;
	private FontAppearancePanel comboBoxFontPanel;

	private String title = "Input fonts";

	public InputFontPanel(Font textFieldFont, Font textAreaFont, Font comboBoxFont) {
		JPanel rightContentPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(rightContentPanel, BoxLayout.Y_AXIS);
		rightContentPanel.setLayout(boxLayout);

		String[] availableFamilyList = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();

		textFieldFontPanel = new FontAppearancePanel("TextField font", availableFamilyList, textFieldFont);
		rightContentPanel.add(textFieldFontPanel);

		textAreaFontPanel = new FontAppearancePanel("TextArea font (monospaced recommended)", availableFamilyList,
				textAreaFont);
		rightContentPanel.add(textAreaFontPanel);

		comboBoxFontPanel = new FontAppearancePanel("ComboBox font", availableFamilyList, comboBoxFont);
		rightContentPanel.add(comboBoxFontPanel);

		setLayout(new BorderLayout());
		rightContentPanel.setBorder(null);
		add(rightContentPanel, BorderLayout.CENTER);

		setBorder(null);
	}

	public Font getTextFieldFont() {
		return textFieldFontPanel.getExampleLabel().getFont();
	}

	public Font getTextAreaFont() {
		return textAreaFontPanel.getExampleLabel().getFont();
	}

	public Font getComboBoxFont() {
		return comboBoxFontPanel.getExampleLabel().getFont();
	}

	@Override
	public String toString() {
		return this.title;
	}
}
