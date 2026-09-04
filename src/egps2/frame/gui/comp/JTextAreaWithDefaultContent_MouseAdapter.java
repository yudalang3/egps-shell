package egps2.frame.gui.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;

import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * JTextAreaWithDefaultContent_MouseAdapter supports the main eGPS window, actions, or tab management.
 */
public class JTextAreaWithDefaultContent_MouseAdapter extends JPanel {

	private JTextArea textArea;

	private MouseAdapter mouseListener;

	private UndoManager undoManager = new UndoManager();

	/**
	 * Create the panel.
	 */
	public JTextAreaWithDefaultContent_MouseAdapter() {

		setBackground(Color.WHITE);

		Font globalFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		setLayout(new BorderLayout(0, 0));

		textArea = new JTextArea();
		textArea.setForeground(Color.lightGray);
		textArea.setFont(globalFont);

		textArea.setAutoscrolls(true);
		textArea.setLineWrap(false);

		mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				textArea.setForeground(Color.black);
				textArea.removeMouseListener(mouseListener);
				mouseListener = null;
			}
		};
		textArea.addMouseListener(mouseListener);

		textArea.getDocument().addUndoableEditListener(undoManager);

		textArea.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.isControlDown()) {
					
					switch (evt.getKeyCode()) {
					case KeyEvent.VK_Z:
						if (undoManager.canUndo()) {
							undoManager.undo();
						}
						break;
					case KeyEvent.VK_Y:
						if (undoManager.canRedo()) {
							undoManager.redo();
						}
						break;
//					case KeyEvent.VK_C:
//						System.out.println("copy");
//						textArea.copy();
//						break;
//					case KeyEvent.VK_V:
//						textArea.paste();
//						break;
//					case KeyEvent.VK_X:
//						textArea.cut();
//						break;
					default:
						break;
					}
				}
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
			}
		});

		JScrollPane comp = new JScrollPane(textArea);
		textArea.setBorder(BorderFactory.createEmptyBorder(6,6,0,0));
		add(comp, BorderLayout.CENTER);
	}

	public void setTextContents(List<String> contents) {
		StringBuilder sBuilder = new StringBuilder();
		for (String string : contents) {
			sBuilder.append(string).append("\n");
		}

		boolean shouldAdd = textArea.getText().length() == 0;
		textArea.setText(sBuilder.toString());

		textArea.setCaretPosition(0);

		MyListener myListener = new MyListener(() -> {
			textArea.setForeground(Color.black);
		});

		if (shouldAdd) {
			textArea.getDocument().addDocumentListener(myListener);
		}
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void addDocumentListenerWhenContentChange(Runnable runnable) {
		DocumentListener listener = new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				runnable.run();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {

				runnable.run();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {

			}

		};

		textArea.getDocument().addDocumentListener(listener);
	}

}

/**
 * MyListener supports the main eGPS window, actions, or tab management.
 */
class MyListener implements DocumentListener {

	private Runnable callBackFuntion;

	public MyListener(Runnable callBackFuntion) {
		this.callBackFuntion = callBackFuntion;
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		usrEdited(e);
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		usrEdited(e);

	}

	private void usrEdited(DocumentEvent e) {
		Document document = e.getDocument();
		document.removeDocumentListener(this);
		callBackFuntion.run();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {

	}

}
