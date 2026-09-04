package egps2.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * FontAppearancePanel is a reusable Swing panel or dialog within eGPS.
 */
public class FontAppearancePanel extends JPanel {

	private JComboBox<Integer> fontSizeComboBox;
	private JComboBox<String> fontFamilyComboBox;
	private JComboBox<String> fontStyleComboBox;
	private JLabel exampleLabel;


	/**
	 * Create the panel.
	 * @param availableFamilyList 
	 * @param initFont 
	 */
	public FontAppearancePanel(String name, String[] availableFamilyList, Font exampleFont) {
		
		LaunchProperty lauchProperty = UnifiedAccessPoint.getLaunchProperty();
		

		JPanel fontPanel = new JPanel(new GridBagLayout());
		fontPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		fontPanel.setAlignmentY(Component.TOP_ALIGNMENT);
		fontPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1), name,
				TitledBorder.LEFT, TitledBorder.TOP, lauchProperty.getDefaultTitleFont()));

		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints.anchor = GridBagConstraints.WEST;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;

		JLabel familyLabel = new JLabel(UnifiedAccessPoint.getResourceString("Pref.font.family.name"));
		familyLabel.setFont(lauchProperty.getDefaultFont());
		fontPanel.add(familyLabel, gridBagConstraints);

		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints2.anchor = GridBagConstraints.WEST;
		gridBagConstraints2.gridx = 1;
		gridBagConstraints2.gridy = 0;

		JLabel styleLabel = new JLabel(UnifiedAccessPoint.getResourceString("Pref.font.style.name"));
		styleLabel.setFont(lauchProperty.getDefaultFont());
		fontPanel.add(styleLabel, gridBagConstraints2);

		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.insets = new Insets(5, 5, 5, 0);
		gridBagConstraints3.anchor = GridBagConstraints.WEST;
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.gridy = 0;
		JLabel sizeLabel = new JLabel(UnifiedAccessPoint.getResourceString("Pref.font.size.name"));
		sizeLabel.setFont(lauchProperty.getDefaultFont());
		fontPanel.add(sizeLabel, gridBagConstraints3);

		JPanel examplePanel = new JPanel(new GridBagLayout());

		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.insets = new Insets(5, 5, 5, 5);
		gridBagConstraints4.anchor = GridBagConstraints.CENTER;
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.gridy = 0;

		
		String examStr = UnifiedAccessPoint.getResourceString("Pref.font.example.name");
		
		examplePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1),
				examStr, TitledBorder.LEFT, TitledBorder.TOP, lauchProperty.getDefaultTitleFont()));

		String sampleText = UnifiedAccessPoint.getResourceString("Pref.font.sample");
		
		exampleLabel = new JLabel(sampleText);
		exampleLabel.setFont(exampleFont);

		examplePanel.add(exampleLabel, gridBagConstraints4);

		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 0;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.gridwidth = 3;
		gridBagConstraints5.gridheight = 1;
		gridBagConstraints5.fill = GridBagConstraints.BOTH;
		gridBagConstraints5.anchor = GridBagConstraints.WEST;
		fontPanel.add(examplePanel, gridBagConstraints5);

		

		fontFamilyComboBox = new JComboBox<String>();
		for (String string : availableFamilyList) {
			fontFamilyComboBox.addItem(string);
		}
		
		
		String tooltipStr = UnifiedAccessPoint.getResourceString("Pref.font.tooltip");
		
		fontFamilyComboBox.setToolTipText(tooltipStr);
		fontFamilyComboBox.setFont(lauchProperty.getDefaultFont());
		fontFamilyComboBox.setSelectedItem(exampleFont.getFamily());
		fontFamilyComboBox.setFocusable(false);

		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String fontFamily = (String) fontFamilyComboBox.getSelectedItem().toString();
				int fontSize = Integer.valueOf(fontSizeComboBox.getSelectedItem().toString());
				int fontStyle = Integer.valueOf(fontStyleComboBox.getSelectedIndex());

				exampleLabel.setFont(new Font(fontFamily, fontStyle, fontSize));
			}
		};

		fontFamilyComboBox.addActionListener(actionListener);

		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.gridy = 1;
		gridBagConstraints6.gridwidth = 1;
		gridBagConstraints6.gridheight = 1;
		gridBagConstraints6.fill = GridBagConstraints.BOTH;
		gridBagConstraints6.anchor = GridBagConstraints.WEST;

		gridBagConstraints.gridy = 1;
		fontPanel.add(fontFamilyComboBox, gridBagConstraints6);

		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.insets = new Insets(0, 0, 5, 5);
		gridBagConstraints7.gridx = 1;
		gridBagConstraints7.gridy = 1;
		gridBagConstraints7.gridwidth = 1;
		gridBagConstraints7.gridheight = 1;
		gridBagConstraints7.fill = GridBagConstraints.BOTH;
		gridBagConstraints7.anchor = GridBagConstraints.WEST;

		// Font里面这三个style 的顺序是 PLAIN BOLD ITALILC
		String[] styleList = new String[] { "PLAIN", "BOLD", "ITALIC" ,"BOLD+ITALIC"};
		fontStyleComboBox = new JComboBox<String>();
		for (String string : styleList) {
			fontStyleComboBox.addItem(string);
		}
		fontStyleComboBox.setFont(lauchProperty.getDefaultFont());
		fontStyleComboBox.setFocusable(false);
		fontStyleComboBox.setToolTipText(tooltipStr);
		
		int style = exampleFont.getStyle();
		fontStyleComboBox.setSelectedIndex(style);
		fontStyleComboBox.addActionListener(actionListener);

		fontPanel.add(fontStyleComboBox, gridBagConstraints7);

		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.insets = new Insets(0, 0, 5, 0);
		gridBagConstraints8.gridx = 2;
		gridBagConstraints8.gridy = 1;
		gridBagConstraints8.gridwidth = 1;
		gridBagConstraints8.gridheight = 1;
		gridBagConstraints8.fill = GridBagConstraints.BOTH;
		gridBagConstraints8.anchor = GridBagConstraints.WEST;
		fontSizeComboBox = new JComboBox<>();
		fontSizeComboBox.setToolTipText(tooltipStr);

		for (int i = 4; i < 30; i++) {
			fontSizeComboBox.addItem(i);
		}
		fontSizeComboBox.setFont(lauchProperty.getDefaultFont());
		fontSizeComboBox.setFocusable(false);

		
		fontSizeComboBox.setSelectedItem(exampleFont.getSize());
		fontSizeComboBox.addActionListener(actionListener);


		fontPanel.add(fontSizeComboBox, gridBagConstraints8);

		BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(boxLayout);
		add(fontPanel);

	}
	
	public JLabel getExampleLabel() {
		return exampleLabel;
	}

}
