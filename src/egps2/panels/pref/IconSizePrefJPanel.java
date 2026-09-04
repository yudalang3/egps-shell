package egps2.panels.pref;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import egps2.LaunchProperty;

@SuppressWarnings("serial")
/**
 * IconSizePrefJPanel is a reusable Swing panel or dialog within eGPS.
 */
public class IconSizePrefJPanel extends JPanel {

	private SpinnerNumberModel modelOfDefaultIconHeight;
	private SpinnerNumberModel modelOfDefaultIconWidth;
	private JSpinner spinnerTabDefaultHeight;
	private JSpinner spinnerTabDefaultWidth;

	public IconSizePrefJPanel(LaunchProperty launchProperty) {
		setBorder(new EmptyBorder(15, 15, 15, 15));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 25, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("Default icon size");
		lblNewLabel.setFont(launchProperty.getDefaultTitleFont());
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel.gridwidth = 2;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Height: ");
		lblNewLabel_1.setFont(launchProperty.getDefaultFont());
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		add(lblNewLabel_1, gbc_lblNewLabel_1);

		JSpinner spinnerIconDefaultHeight = new JSpinner();
		spinnerIconDefaultHeight.setFont(launchProperty.getDefaultFont());
		modelOfDefaultIconHeight = new SpinnerNumberModel(Byte.valueOf((byte) launchProperty.getIconHeight()),
				Byte.valueOf((byte) 10), Byte.valueOf((byte) 50), Byte.valueOf((byte) 1));
		spinnerIconDefaultHeight.setModel(modelOfDefaultIconHeight);
		GridBagConstraints gbc_spinnerIconDefaultHeight = new GridBagConstraints();
		gbc_spinnerIconDefaultHeight.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerIconDefaultHeight.gridx = 3;
		gbc_spinnerIconDefaultHeight.gridy = 1;
		add(spinnerIconDefaultHeight, gbc_spinnerIconDefaultHeight);

		JLabel lblNewLabel_2 = new JLabel("Width: ");
		lblNewLabel_2.setFont(launchProperty.getDefaultFont());
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 2;
		add(lblNewLabel_2, gbc_lblNewLabel_2);

		JSpinner spinnerIconDefaultWidth = new JSpinner();
		spinnerIconDefaultWidth.setFont(launchProperty.getDefaultFont());
		modelOfDefaultIconWidth = new SpinnerNumberModel(Byte.valueOf((byte) launchProperty.getIconWidth()),
				Byte.valueOf((byte) 10), Byte.valueOf((byte) 50), Byte.valueOf((byte) 1));
		spinnerIconDefaultWidth.setModel(modelOfDefaultIconWidth);
		GridBagConstraints gbc_spinnerIconDefaultWidth = new GridBagConstraints();
		gbc_spinnerIconDefaultWidth.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerIconDefaultWidth.gridx = 3;
		gbc_spinnerIconDefaultWidth.gridy = 2;
		add(spinnerIconDefaultWidth, gbc_spinnerIconDefaultWidth);

		JLabel lblNewLabel_3 = new JLabel("Tab icon size");
		lblNewLabel_3.setFont(launchProperty.getDefaultTitleFont());
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridwidth = 2;
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		add(lblNewLabel_3, gbc_lblNewLabel_3);

		JLabel lblNewLabel_4 = new JLabel("Height:");
		lblNewLabel_4.setFont(launchProperty.getDefaultFont());
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 4;
		add(lblNewLabel_4, gbc_lblNewLabel_4);

		spinnerTabDefaultHeight = new JSpinner();
		spinnerTabDefaultHeight.setFont(launchProperty.getDefaultFont());
		spinnerTabDefaultHeight.setModel(new SpinnerNumberModel(Byte.valueOf((byte) launchProperty.getTabIconHeight()),
				Byte.valueOf((byte) 10), Byte.valueOf((byte) 50), Byte.valueOf((byte) 1)));
		spinnerTabDefaultHeight.setFont(launchProperty.getDefaultFont());
		GridBagConstraints gbc_spinnerTabDefaultHeight = new GridBagConstraints();
		gbc_spinnerTabDefaultHeight.insets = new Insets(0, 0, 5, 0);
		gbc_spinnerTabDefaultHeight.gridx = 3;
		gbc_spinnerTabDefaultHeight.gridy = 4;
		add(spinnerTabDefaultHeight, gbc_spinnerTabDefaultHeight);

		JLabel lblTabDefaultWidth = new JLabel("Width:");
		lblTabDefaultWidth.setFont(launchProperty.getDefaultFont());
		GridBagConstraints gbc_lblTabDefaultWidth = new GridBagConstraints();
		gbc_lblTabDefaultWidth.insets = new Insets(0, 0, 0, 5);
		gbc_lblTabDefaultWidth.anchor = GridBagConstraints.EAST;
		gbc_lblTabDefaultWidth.gridx = 1;
		gbc_lblTabDefaultWidth.gridy = 5;
		add(lblTabDefaultWidth, gbc_lblTabDefaultWidth);

		spinnerTabDefaultWidth = new JSpinner();
		spinnerTabDefaultWidth.setFont(launchProperty.getDefaultFont());
		spinnerTabDefaultWidth.setModel(new SpinnerNumberModel(Byte.valueOf((byte) launchProperty.getTabIconWidth()),
				Byte.valueOf((byte) 10), Byte.valueOf((byte) 50), Byte.valueOf((byte) 1)));
		spinnerTabDefaultWidth.setFont(launchProperty.getDefaultFont());
		GridBagConstraints gbc_spinnerTabDefaultWidth = new GridBagConstraints();
		gbc_spinnerTabDefaultWidth.gridx = 3;
		gbc_spinnerTabDefaultWidth.gridy = 5;
		add(spinnerTabDefaultWidth, gbc_spinnerTabDefaultWidth);

	}
	
	public Dimension getDefaultIconSize() {
		Byte value = (Byte) modelOfDefaultIconWidth.getValue();
		Byte value2 = (Byte) modelOfDefaultIconHeight.getValue();
		return new Dimension(value, value2);
	}
	
	public Dimension getTabIconSize() {
		Byte value = (Byte) spinnerTabDefaultWidth.getValue();
		Byte value2 = (Byte) spinnerTabDefaultHeight.getValue();
		
		return new Dimension(value, value2 );
	}

	@Override
	public String toString() {
		return "Icon size";
	}

}
