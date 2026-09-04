package egps2.panels.pref;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import egps2.EGPSProperties;
import egps2.LaunchProperty;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
/**
 * TestDataDirPanel is a preference panel for configuring test data directory.
 */
public class TestDataDirPanel extends JPanel {

	private JTextField pathTextField;
	private LaunchProperty launchProperty;

	public TestDataDirPanel(LaunchProperty launchProperty) {
		this.launchProperty = launchProperty;
		// Layout:
		// Row 0: [label][text field grows][Browse... (right)]
		// Row 1:                        [Restore defaults spans 2 cells, aligns with Browse...]
		//
		// NOTE: Keep a small, consistent spacing between the TextField and the Browse button.
		setLayout(new MigLayout("insets 15, fillx", "[right]10[grow,fill]5[right]", "[]10[]"));

		JLabel lblDirPath = new JLabel("Dir. path:");
		lblDirPath.setFont(launchProperty.getDefaultFont());
		add(lblDirPath, "cell 0 0");

		pathTextField = new JTextField();
		pathTextField.setFont(launchProperty.getDefaultFont());
		String testDataDir = launchProperty.getTestDataDir();
		if (testDataDir == null || testDataDir.trim().isEmpty()) {
			testDataDir = EGPSProperties.PROPERTIES_DIR;
		}
		pathTextField.setText(testDataDir);
		add(pathTextField, "cell 1 0, growx");
		pathTextField.setColumns(30);

		JButton btnBrowse = new JButton("Browse...");
		btnBrowse.setFont(launchProperty.getDefaultFont());
		btnBrowse.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setCurrentDirectory(new File(pathTextField.getText()));

			int result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedDir = fileChooser.getSelectedFile();
				pathTextField.setText(selectedDir.getAbsolutePath());
			}
		});
		add(btnBrowse, "cell 2 0, alignx right");

		JButton btnRestoreDefaults = new JButton("Restore defaults");
		btnRestoreDefaults.setFont(launchProperty.getDefaultFont());
		btnRestoreDefaults.addActionListener(e -> {
			pathTextField.setText(EGPSProperties.PROPERTIES_DIR);
		});
		add(btnRestoreDefaults, "cell 1 1 2 1, alignx right");
	}

	public String getTestDataDir() {
		return pathTextField.getText();
	}

	@Override
	public String toString() {
		return "Test data dir";
	}
}
