package egps2.panels.pref;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import egps2.LaunchProperty;

//去掉Actions一栏，因为这个不实用
@SuppressWarnings("serial")
/**
 * ModuleActionsPanel is a reusable Swing panel or dialog within eGPS.
 */
public class ModuleActionsPanel extends JPanel implements ActionListener{

	private LaunchProperty launchProperty;


	public ModuleActionsPanel(LaunchProperty launchProperty) {
		JCheckBox chckbxNewCheckBox = new JCheckBox("Auto click import button");
		chckbxNewCheckBox.setFocusable(false);
		chckbxNewCheckBox.setFont(launchProperty.getDefaultFont());
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 0;
		add(chckbxNewCheckBox, gbc_chckbxNewCheckBox);

		chckbxNewCheckBox.setSelected(launchProperty.isShould_auto_click_import());
		chckbxNewCheckBox.addActionListener(this);
		
		this.launchProperty = launchProperty;
		
	}
	

	@Override
	public String toString() {
		return "Module actions";
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		JCheckBox source = (JCheckBox) e.getSource();
		launchProperty.setShould_auto_click_import(source.isSelected());
		
	}

}
