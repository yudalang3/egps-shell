package egps2.panels.pref;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import egps2.panels.FontAppearancePanel;

@SuppressWarnings("serial")
/**
 * DataDisplayFontPanel manages data display component font settings
 */
public class DataDisplayFontPanel extends JPanel {

	private FontAppearancePanel tableFontPanel;
	private FontAppearancePanel tableHeaderFontPanel;
	private FontAppearancePanel listFontPanel;
	private FontAppearancePanel treeFontPanel;

	private String title = "Data display fonts";

	public DataDisplayFontPanel(Font tableFont, Font tableHeaderFont, Font listFont, Font treeFont) {
		JPanel rightContentPanel = new JPanel();
		BoxLayout boxLayout = new BoxLayout(rightContentPanel, BoxLayout.Y_AXIS);
		rightContentPanel.setLayout(boxLayout);

		String[] availableFamilyList = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getAvailableFontFamilyNames();

		tableFontPanel = new FontAppearancePanel("Table cell font", availableFamilyList, tableFont);
		rightContentPanel.add(tableFontPanel);

		tableHeaderFontPanel = new FontAppearancePanel("Table header font", availableFamilyList, tableHeaderFont);
		rightContentPanel.add(tableHeaderFontPanel);

		listFontPanel = new FontAppearancePanel("List font", availableFamilyList, listFont);
		rightContentPanel.add(listFontPanel);

		treeFontPanel = new FontAppearancePanel("Tree font", availableFamilyList, treeFont);
		rightContentPanel.add(treeFontPanel);

		setLayout(new BorderLayout());
		rightContentPanel.setBorder(null);
		add(rightContentPanel, BorderLayout.CENTER);

		setBorder(null);
	}

	public Font getTableFont() {
		return tableFontPanel.getExampleLabel().getFont();
	}

	public Font getTableHeaderFont() {
		return tableHeaderFontPanel.getExampleLabel().getFont();
	}

	public Font getListFont() {
		return listFontPanel.getExampleLabel().getFont();
	}

	public Font getTreeFont() {
		return treeFontPanel.getExampleLabel().getFont();
	}

	@Override
	public String toString() {
		return this.title;
	}
}
