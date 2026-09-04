package egps2.builtin.modules.largetextedi.actions;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.largetextedi.TextEditorMain;
import egps2.builtin.modules.largetextedi.gui.EditorScrollBar;
import egps2.builtin.modules.largetextedi.gui.EgpsTextPane;
import egps2.builtin.modules.largetextedi.gui.TextEditorDataManager;
import egps2.builtin.modules.largetextedi.gui.TextEditorViewPort;
import egps2.builtin.modules.largetextedi.util.EditorJDialog;

/**
 * GoToLineJDialog belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class GoToLineJDialog extends EditorJDialog implements ActionListener, DocumentListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5406234864870401387L;

	private static final String TILTLE = "Go to Line";

	private final String cancel = "Cancel";

	private final String jumpNumber = "Go to Line";

	private Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

	private JPanel contentPane;

	private TextEditorMain textEditorMain;

	private TextEditorDataManager editorDataManager;

	private EgpsTextPane egpsTextPane;

	private TextEditorViewPort textEditorViewPort;

	private int maxLength;

	private JTextField jumpNumberField;

	private JButton jumpNumberButton;

	private JLabel hide;

	public GoToLineJDialog(JFrame owner, TextEditorMain textEditorMain) {
		super(owner, TILTLE);

		this.textEditorMain = textEditorMain;

		this.editorDataManager = textEditorMain.getEditorDataManager();

		this.maxLength = editorDataManager.getMaxLength();

		this.egpsTextPane = textEditorMain.getEgpsTextPane();

		this.textEditorViewPort = this.egpsTextPane.getTextEditorViewPort();

		setSize(600, 400);

		setLocationRelativeTo(owner);

		setContentPane(getMainPanel());

		setIconImage(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		setVisible(true);

	}

	private JPanel getMainPanel() {

		if (contentPane == null) {

			contentPane = new JPanel(null);

			JLabel jumpLable = new JLabel("Enter line number (1 .. " + this.maxLength + "):");
			jumpLable.setFont(defaultFont);

			jumpLable.setBounds(15, 10, 360, 25);

			contentPane.add(jumpLable);

			jumpNumberField = new JTextField(24);
			jumpNumberField.setFont(defaultFont);

			int caretPosition = egpsTextPane.getCaretPosition();

			int selectLineNumber = egpsTextPane.getLineNumber(caretPosition) + textEditorViewPort.getStartPosition();

			jumpNumberField.setText("" + selectLineNumber);

			jumpNumberField.select(0, jumpNumberField.getDocument().getLength());

			jumpNumberField.setBounds(15, 40, 360, 25);
			contentPane.add(jumpNumberField);

			hide = new JLabel();

			hide.setFont(defaultFont);

			hide.setVisible(false);

			hide.setBounds(15, 70, 360, 25);

			contentPane.add(hide);

			JPanel buttonJPanel = new JPanel(new GridBagLayout());

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.insets = new Insets(5, 10, 5, 5);

			gridBagConstraints.anchor = GridBagConstraints.EAST;

			jumpNumberButton = new JButton(jumpNumber);
			jumpNumberButton.addActionListener(this);
			jumpNumberButton.setFont(defaultFont);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			buttonJPanel.add(jumpNumberButton, gridBagConstraints);
			jumpNumberField.getDocument().addDocumentListener(this);
			getRootPane().setDefaultButton(jumpNumberButton);
			JButton cancelButton = new JButton(cancel);
			cancelButton.addActionListener(this);
			cancelButton.setFont(defaultFont);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			buttonJPanel.add(cancelButton, gridBagConstraints);
			buttonJPanel.setBounds(150, 120, 260, 25);

			contentPane.add(buttonJPanel);

		}

		return contentPane;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals(this.jumpNumber)) {
			EditorScrollBar editorScrollBar = this.textEditorMain.getEditorScrollBar();

			String text = this.jumpNumberField.getText();
			int jumpLineNumber = Integer.parseInt(text);

			editorScrollBar.setValue(jumpLineNumber);

			Element defaultRootElement = this.egpsTextPane.getDocument().getDefaultRootElement();

			Element element = defaultRootElement.getElement(jumpLineNumber - textEditorViewPort.getStartPosition());

			this.egpsTextPane.setCaretPosition(element.getStartOffset());

			this.dispose();// 对话框销毁

		} else if (actionCommand.equals(cancel)) {
			// this.setVisible(false);// 对话框不可见
			this.dispose();// Dialog destruction
		}
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

		String str = jumpNumberField.getText();

		if (str.length() == 0) {
			jumpNumberButton.setEnabled(true);

			hide.setVisible(false);

			return;
		}

		if (isNumeric(str)) {

			if (isRange(str)) {

				hide.setVisible(false);

				jumpNumberButton.setEnabled(true);

				return;
			}

			String text = "Line number out of range";

			hide.setText(text);

			hide.setVisible(true);

			jumpNumberButton.setEnabled(false);

		} else {
			String text = "Not a number";

			hide.setText(text);

			hide.setVisible(true);

			jumpNumberButton.setEnabled(false);
		}

	}

	public boolean isRange(String str) {

		int parseInt = Integer.parseInt(str);

		return parseInt <= maxLength && parseInt > 0 ? true : false;

	}

	public boolean isNumeric(String str) {

		Pattern pattern = Pattern.compile("[0-9]*");

		Matcher isNum = pattern.matcher(str);

		return isNum.matches();
	}

}
