package egps2.panels.pref;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import egps2.panels.FontAppearancePanel;

@SuppressWarnings("serial")
/**
 * DialogFontPanel manages dialog-related font settings
 */
public class DialogFontPanel extends JPanel {

	private FontAppearancePanel titleFontPanel;
	private FontAppearancePanel contentFontPanel;
	private FontAppearancePanel buttonFontPanel;

	private String title = "Dialog fonts";

	public DialogFontPanel(Font dialogTitleFont, Font dialogContentFont, Font dialogButtonFont) {
		JPanel rightContentPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(rightContentPanel, BoxLayout.Y_AXIS);
		rightContentPanel.setLayout(boxLayout);

		String[] availableFamilyList = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();

		titleFontPanel = new FontAppearancePanel("Dialog title font", availableFamilyList, dialogTitleFont);
		rightContentPanel.add(titleFontPanel);

		contentFontPanel = new FontAppearancePanel("Dialog content font", availableFamilyList, dialogContentFont);
		rightContentPanel.add(contentFontPanel);

		buttonFontPanel = new FontAppearancePanel("Dialog button font", availableFamilyList, dialogButtonFont);
		rightContentPanel.add(buttonFontPanel);

		setLayout(new BorderLayout());
		rightContentPanel.setBorder(null);
		add(rightContentPanel, BorderLayout.CENTER);

		setBorder(null);
	}

	public Font getTitleFont() {
		return titleFontPanel.getExampleLabel().getFont();
	}

	public Font getContentFont() {
		return contentFontPanel.getExampleLabel().getFont();
	}

	public Font getButtonFont() {
		return buttonFontPanel.getExampleLabel().getFont();
	}

	@Override
	public String toString() {
		return this.title;
	}
}
