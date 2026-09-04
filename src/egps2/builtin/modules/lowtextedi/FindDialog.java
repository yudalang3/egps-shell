package egps2.builtin.modules.lowtextedi;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * FindDialog belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class FindDialog extends JDialog implements ActionListener, KeyListener {
    // 添加字体常量
    private static final Font DEFAULT_FONT = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

	JLabel label;
	JTextField textField;
	JCheckBox caseSensitive;
	JButton find, close;
	boolean finishedFinding = true;
	Matcher matcher;
	private JEditorPane textPane;

	public FindDialog(JEditorPane textPane, boolean modal) {
		super(UnifiedAccessPoint.getInstanceFrame(), modal);
		this.textPane = textPane;
		// 设置默认大小
		setPreferredSize(new Dimension(400, 200));
		getContentPane().addKeyListener(this);
		getContentPane().setFocusable(true);
		initComponents();
		setTitle("Search");
		setLocationRelativeTo(textPane);
		pack();
	}

	public void showDialog() {
		setVisible(true);
	}

	private void initComponents() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
		panel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		label = new JLabel("Search:");
		label.setFont(DEFAULT_FONT);  // 设置字体
		label.setDisplayedMnemonic('F');
		panel1.add(label);
		panel1.add(Box.createHorizontalStrut(10));
		textField = new JTextField(20);
		textField.setFont(DEFAULT_FONT);  // 设置字体
		panel1.add(textField);
		label.setLabelFor(textField);
		mainPanel.add(panel1);

		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
		panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 15, 5));
		caseSensitive = new JCheckBox("Case sensitive");
		caseSensitive.setFont(DEFAULT_FONT);  // 设置字体
		panel2.add(caseSensitive);
		mainPanel.add(panel2);

		JPanel panel3 = new JPanel();
		panel3.setLayout(new BoxLayout(panel3, BoxLayout.X_AXIS));
		panel3.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		panel3.add(Box.createHorizontalGlue());
		find = new JButton("Search");
		find.setFont(DEFAULT_FONT);  // 设置字体
		find.setMargin(new Insets(5, 15, 5, 15));
		close = new JButton("Close");
		close.setFont(DEFAULT_FONT);  // 设置字体
		close.setMargin(new Insets(5, 15, 5, 15));
		find.addActionListener(this);
		close.addActionListener(this);
		panel3.add(find);
		panel3.add(Box.createHorizontalStrut(10));
		panel3.add(close);
		panel3.add(Box.createHorizontalGlue());
		mainPanel.add(panel3);

		getContentPane().add(mainPanel);
		textField.addKeyListener(this);
		find.addKeyListener(this);
		close.addKeyListener(this);
		caseSensitive.addKeyListener(this);
	}

	private void find(String pattern) {
		if (!finishedFinding) {
			if (matcher.find()) {
				int selectionStart = matcher.start();
				int selectionEnd = matcher.end();
				textPane.moveCaretPosition(matcher.start());
				textPane.select(selectionStart, selectionEnd);
			} else {
				finishedFinding = true;
				JOptionPane.showMessageDialog(this, "Already to the end of file.", "End file", JOptionPane.INFORMATION_MESSAGE);
				// closeDialog();
			}
		} else {
			matcher = Pattern.compile(pattern).matcher(textPane.getText());
			finishedFinding = false;
			find(pattern);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals("Search")) {
			String input = textField.getText();
			StringBuilder pattern = new StringBuilder();
			if (!caseSensitive.isSelected()) {
				pattern.append("(?i)");
			}
			pattern.append(input);
			find(pattern.toString());
		} else if (cmd.equals("Close")) {
			closeDialog();
		}
	}

	private void closeDialog() {
		setVisible(false);
		dispose();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println(e.getKeyCode());
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			closeDialog();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
