package egps2.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.io.FileUtils;

import egps2.UnifiedAccessPoint;
import utils.EGPSFileUtil;

@SuppressWarnings("serial")
/**
 * LicenseOfEGPSPanel is a reusable Swing panel or dialog within eGPS.
 */
public class LicenseOfEGPSPanel extends JPanel implements ActionListener {

	private javax.swing.JTextArea textArea;

	private JButton copyAndClose;

	public LicenseOfEGPSPanel() {
		initComponents();

		InputStream resourceAsStream = getClass().getResourceAsStream("eGPS_licenseTerms.txt");
        try {
			String line = EGPSFileUtil.getContentFromInputStreamAsString(resourceAsStream);
			textArea.setText(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initComponents() {
		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		textArea = new javax.swing.JTextArea();
		textArea.setFont(defaultFont);

		setLayout(new BorderLayout());

		add(new JScrollPane(textArea), BorderLayout.CENTER);
		textArea.setLineWrap(true);

		copyAndClose = new JButton("Copy and close");
		copyAndClose.setFocusable(false);
		copyAndClose.setFont(defaultFont);
		copyAndClose.addActionListener(this);

		JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jPanel.add(copyAndClose);
		add(jPanel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == copyAndClose) {
			String text = textArea.getText();
			// 获取系统剪切板
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			// 封装文本为 Transferable 对象
			StringSelection stringSelection = new StringSelection(text);
			// 将文本放入剪切板
			clipboard.setContents(stringSelection, null);

			Window root = (Window) SwingUtilities.getRoot(this);
			root.dispose();
		}
	}

}
