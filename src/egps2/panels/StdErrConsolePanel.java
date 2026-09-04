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

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * StdErrConsolePanel is a reusable Swing panel or dialog within eGPS.
 */
public class StdErrConsolePanel extends JPanel implements ActionListener {

	private JButton clearConsoleButton;

	private javax.swing.JTextArea textArea;

	private JButton copyAndClose;

	public StdErrConsolePanel() {
		initComponents();
		// 重定向到通过文本组件构建的组件输出流中。
		System.setErr(new GUIPrintStream(System.err, textArea));
	}

	private void initComponents() {
		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		textArea = new javax.swing.JTextArea();
		textArea.setFont(defaultFont);
		clearConsoleButton = new JButton();
		clearConsoleButton.setFont(defaultFont);

		setLayout(new BorderLayout());

		add(new JScrollPane(textArea), BorderLayout.CENTER);
		clearConsoleButton.setText("Clear console");
		clearConsoleButton.setFocusable(false);
		clearConsoleButton.addActionListener(this);
		
		copyAndClose = new JButton("Copy and close");
		copyAndClose.setFocusable(false);
		copyAndClose.addActionListener(this);
		
		JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		jPanel.add(clearConsoleButton);
		jPanel.add(copyAndClose);
		add(jPanel, BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == clearConsoleButton) {
			textArea.setText("");
		}else if (source == copyAndClose) {
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
