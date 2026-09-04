package egps2.builtin.modules.lowtextedi;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import egps2.panels.dialog.EGPSFontChooser;
import egps2.panels.graphicpro.CustomizeFontEnum;
import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.IconObtainer;

/**
 * CtrlPanel belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class CtrlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JEditorPane textPane;

	// undo and redo
	private Document editorPaneDocument;
	protected UndoHandler undoHandler = new UndoHandler();
	protected UndoManager undoManager = new UndoManager();
	private UndoAction undoAction = null;
	private RedoAction redoAction = null;
	private JButton btnCopy;
	private JButton btnCut;
	private JButton btnPaste;
	private JButton btnSelectAll;
	private JButton btnSearch;
	private JButton btnUndo;
	private JButton btnRedo;
	private JButton btnFont;
	private final JButton equalWidthFont;
	private JButton btnIncreaseFontSize;
	private JButton btnDecreaseFontSize;
	private JButton btnWrapLine;

	/**
	 * Create the panel.
	 */
	public CtrlPanel() {

		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

		BoxLayout gridBagLayout = new BoxLayout(this, BoxLayout.Y_AXIS);
		setLayout(gridBagLayout);

		btnSearch = new JButton();
		btnSearch.setToolTipText("Find content.");
		btnSearch.setIcon(IconObtainer.get("search16x16.png"));
		btnSearch.setHorizontalAlignment(SwingConstants.LEFT);
		btnSearch.setRequestFocusEnabled(false);
		btnSearch.addActionListener(this);
		add(btnSearch);

		btnSelectAll = new JButton();
		btnSelectAll.setToolTipText("Select all");
		btnSelectAll.setIcon(IconObtainer.get("selectall.png"));
		btnSelectAll.setHorizontalAlignment(SwingConstants.LEFT);
		btnSelectAll.setRequestFocusEnabled(false);
		btnSelectAll.addActionListener(this);
		add(btnSelectAll);

		addSeperator();

		btnCopy = new JButton();
		btnCopy.setToolTipText("Copy");
		btnCopy.setIcon(EGPSShellIcons.get("copy.png"));
		btnCopy.setHorizontalAlignment(SwingConstants.LEFT);
		btnCopy.setRequestFocusEnabled(false);
		btnCopy.addActionListener(this);
		add(btnCopy);

		btnCut = new JButton();
		btnCut.setToolTipText("Cut");
		btnCut.setHorizontalAlignment(SwingConstants.LEFT);
		btnCut.setIcon(EGPSShellIcons.get("cut.png"));
		btnCut.setRequestFocusEnabled(false);
		btnCut.addActionListener(this);
		add(btnCut);

		btnPaste = new JButton();
		btnPaste.setToolTipText("Paste");
		btnPaste.setIcon(EGPSShellIcons.get("paste.png"));
		btnPaste.setRequestFocusEnabled(false);
		btnPaste.addActionListener(this);
		btnPaste.setHorizontalAlignment(SwingConstants.LEFT);
		add(btnPaste);

		addSeperator();
		
		undoAction = new UndoAction();
		redoAction = new RedoAction();
		
		btnUndo = new JButton(undoAction);
		btnUndo.setText(null);
		btnUndo.setToolTipText("undo");
		btnUndo.setIcon(IconObtainer.get("undo.png"));
		btnUndo.setHorizontalAlignment(SwingConstants.LEFT);
		btnUndo.setRequestFocusEnabled(false);
		add(btnUndo);
		
		btnRedo = new JButton(redoAction);
		btnRedo.setText(null);
		btnRedo.setToolTipText("redo");
		btnRedo.setIcon(IconObtainer.get("redo.png"));
		btnRedo.setHorizontalAlignment(SwingConstants.LEFT);
		btnRedo.setRequestFocusEnabled(false);
		add(btnRedo);

		addSeperator();
		
		btnFont = new JButton();
		btnFont.setIcon(IconObtainer.get("font2.png"));
		btnFont.setToolTipText("Change font");
		btnFont.addActionListener(this);
		add(btnFont);

		equalWidthFont = new JButton("---");
		equalWidthFont.setToolTipText("Change font face to the equal width font");
		equalWidthFont.addActionListener(this);
		add(equalWidthFont);

		btnIncreaseFontSize = new JButton();
		btnIncreaseFontSize.addActionListener(this);
		btnIncreaseFontSize.setToolTipText("Increase font size");
		btnIncreaseFontSize.setIcon(EGPSShellIcons.get("magnify_font.png"));
		add(btnIncreaseFontSize);
		
		btnDecreaseFontSize = new JButton();
		btnDecreaseFontSize.addActionListener(this);
		btnDecreaseFontSize.setIcon(EGPSShellIcons.get("reduce_font.png"));
		btnDecreaseFontSize.setToolTipText("Decrease the font size");
		add(btnDecreaseFontSize);

		addSeperator();


		// https://forums.oracle.com/ords/apexds/post/setlinewrap-for-jeditorpane-6656
		// Sorry, this feature is not supported by JEditorPane.
//		btnWrapLine = new JButton();
//		InputStream tutorialIcon = EGPSShellIcons.getVectorGraphResourceAsStream("WordWrap.svg");
//		ImageIcon iconFromSVGByStream = EGPSIconUtil.getIconFromSVGByStream(tutorialIcon, 16, 16);
//		btnWrapLine.setIcon(iconFromSVGByStream);
//		add(btnWrapLine);

	}

	private void addSeperator() {
		// Seperator
		add(Box.createVerticalStrut(5));
		add(new DashedSeparator());
		add(Box.createVerticalStrut(5));
	}

	public void setEditorPanel(JEditorPane textPane) {
		this.textPane = textPane;

		editorPaneDocument = textPane.getDocument();
		editorPaneDocument.addUndoableEditListener(undoHandler);

//		KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.META_MASK);
//		KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.META_MASK);
		
//		textPane.getInputMap().put(undoKeystroke, "undoKeystroke");
//		textPane.getActionMap().put("undoKeystroke", undoAction);

//		textPane.getInputMap().put(redoKeystroke, "redoKeystroke");
//		textPane.getActionMap().put("redoKeystroke", redoAction);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		Font currentFont = textPane.getFont();
		if (source == btnSelectAll) {
			textPane.requestFocus();
			textPane.selectAll();
		}else if (source == btnCopy) {
			textPane.copy();
		}else if (source == btnCut) {
			textPane.cut();
		}else if (source == btnPaste) {
			textPane.paste();
		}else if (source == btnWrapLine) {
//			textPane.wra
		}else if (source == btnSearch) {
			FindDialog find = new FindDialog(textPane, false);
			find.showDialog();
		} else if (source == equalWidthFont) {
			Font defaultFont = textPane.getFont();
			Font cousineDefinedFont = CustomizeFontEnum.COUSINEREGULARFONTFAMILY
					.getCousineDefinedFont(defaultFont.getStyle(), defaultFont.getSize());
			textPane.setFont(cousineDefinedFont);
		} else if (source == btnFont) {
			EGPSFontChooser fontChooser = new EGPSFontChooser(currentFont);
			int result = fontChooser.showDialog(UnifiedAccessPoint.getInstanceFrame());
			if (result == EGPSFontChooser.OK_OPTION) {
				Font font = fontChooser.getSelectedFont();
				textPane.setFont(font);
			}
		}else if (source == btnIncreaseFontSize) {
			float size = currentFont.getSize2D() + 1;
			if (size > 50) {
				size = 50f;
			}
			Font deriveFont = currentFont.deriveFont(size);
			textPane.setFont(deriveFont);
		}else if (source == btnDecreaseFontSize) {
			float size = currentFont.getSize2D() - 1;
			if (size < 3) {
				size = 3f;
			}
			Font deriveFont = currentFont.deriveFont(size);
			textPane.setFont(deriveFont);
		}
		
	}

	/**
	 * UndoHandler belongs to a built-in eGPS module (loader, panel, or helper).
	 */
	class UndoHandler implements UndoableEditListener {

		/**
		 * Messaged when the Document has created an edit, the edit is added to
		 * <code>undoManager</code>, an instance of UndoManager.
		 */
		public void undoableEditHappened(UndoableEditEvent e) {
			undoManager.addEdit(e.getEdit());
			undoAction.update();
			redoAction.update();
		}
	}

	@SuppressWarnings("serial")
	/**
	 * UndoAction belongs to a built-in eGPS module (loader, panel, or helper).
	 */
	class UndoAction extends AbstractAction {
		public UndoAction() {
			super("Undo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.undo();
			} catch (CannotUndoException ex) {
				 ex.printStackTrace();
			}
			update();
			redoAction.update();
		}

		protected void update() {
			if (undoManager.canUndo()) {
				setEnabled(true);
//				putValue(Action.NAME, undoManager.getUndoPresentationName());
			} else {
				setEnabled(false);
//				putValue(Action.NAME, "Undo");
			}
		}
	}

	@SuppressWarnings("serial")
	/**
	 * RedoAction belongs to a built-in eGPS module (loader, panel, or helper).
	 */
	class RedoAction extends AbstractAction {
		public RedoAction() {
			super("Redo");
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				undoManager.redo();
			} catch (CannotRedoException ex) {
				ex.printStackTrace();
			}
			update();
			undoAction.update();
		}

		protected void update() {
			if (undoManager.canRedo()) {
				setEnabled(true);
//				putValue(Action.NAME, undoManager.getRedoPresentationName());
			} else {
				setEnabled(false);
//				putValue(Action.NAME, "Redo");
			}
		}
	}

}
