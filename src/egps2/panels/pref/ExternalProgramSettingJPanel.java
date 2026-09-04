package egps2.panels.pref;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;

import egps2.EGPSProperties;
import egps2.LaunchProperty;

@SuppressWarnings("serial")
/**
 * ExternalProgramSettingJPanel is a reusable Swing panel or dialog within eGPS.
 */
public class ExternalProgramSettingJPanel extends JPanel {

	private JTextArea textArea;
	private boolean changed = false;

	public ExternalProgramSettingJPanel(LaunchProperty launchProperty) {
		setBorder(new EmptyBorder(15, 15, 15, 15));

		setLayout(new BorderLayout());

		textArea = new JTextArea();
		textArea.setFont(launchProperty.getDefaultFont());

		textArea.setText(getConfigContent());

		changed = false;
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				changed = true;
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				changed = true;
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// Not used for plain text components.
			}
		});

		add(new JScrollPane(textArea));

	}

	private String getConfigContent() {
		String path = EGPSProperties.EGPS_WRAPPER_PROGRAM_CONFIG_PATH;
		File file = new File(path);
		String ret = null;
		if (file.exists()) {
			try {
				ret = FileUtils.readFileToString(file, StandardCharsets.US_ASCII);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public void saveTheFile() {
		if (changed) {
			try {
				String path = EGPSProperties.EGPS_WRAPPER_PROGRAM_CONFIG_PATH;
				File file = new File(path);
				String text = textArea.getText();
				FileUtils.writeStringToFile(file, text, StandardCharsets.US_ASCII);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		changed = false;
	}

	@Override
	public String toString() {
		return "External program";
	}

}
