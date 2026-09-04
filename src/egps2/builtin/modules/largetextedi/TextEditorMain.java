package egps2.builtin.modules.largetextedi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.text.Element;

import com.jidesoft.swing.JideTabbedPane;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.builtin.modules.largetextedi.gui.EGPSOutputFileChooser4TextEditor;
import egps2.panels.dialog.SwingDialog;
import egps2.panels.graphicpro.CustomizeFontEnum;
import egps2.utils.common.manager.EGPSUndoManager;
import egps2.utils.common.model.datatransfer.EGPSClipboard;
import egps2.utils.common.model.datatransfer.EGPSSelection;
import egps2.utils.common.model.filefilter.FileFilterTxt;
import egps2.UnifiedAccessPoint;
import egps2.frame.ComputationalModuleFace;
import egps2.builtin.modules.largetextedi.actions.FindAndReplaceDialog;
import egps2.builtin.modules.largetextedi.actions.GoToLineJDialog;
import egps2.builtin.modules.largetextedi.actions.ListOfTextEditActions;
import egps2.builtin.modules.largetextedi.gui.EditorScrollBar;
import egps2.builtin.modules.largetextedi.gui.EgpsTextPane;
import egps2.builtin.modules.largetextedi.gui.LineNumberHeaderView;
import egps2.builtin.modules.largetextedi.gui.TextEditorDataManager;
import egps2.builtin.modules.largetextedi.gui.TextEditorViewPort;
import egps2.builtin.modules.largetextedi.io.EditorWriteFile;
import egps2.modulei.AdjusterFillAndLine;
import egps2.modulei.IInformation;
import egps2.modulei.IModuleLoader;

/**
 * @author MHl,Ydl,YFQ
 * @version 1.0
 *          <p>
 *          Description:
 *          </p>
 * @date 2019-06-04 15:20:59
 */
public class TextEditorMain extends ComputationalModuleFace implements KeyListener, AdjusterFillAndLine {

	private static final long serialVersionUID = -1990766473460873574L;
	private EGPSUndoManager editUndoManager;
//	private ActionsManage actionManager = BioMainFrame.getInstance().getActionManager();
	private EGPSClipboard editorClipboard;
	private EGPSSelection editorSelection;

	private JideTabbedPane tabbedPane = UnifiedAccessPoint.getInstanceFrame().getJTabbedPanel();

	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

	private EditorScrollBar editorScrollBar;
	// private EditorLineNumberPane lineNumberPane;
	private LineNumberHeaderView lineNumberPane;

	private EgpsTextPane editorTextPane;

	// private TextEditorDataManager editorDataManager;

	private Color backgroundColor = Color.white;

	private TextEditorViewPort textEditorViewPort;
	private TextEditorDataManager editorDataManager;

	private JScrollPane scrollPane;
	private File inputFile;

	TextEditorMain(IModuleLoader moduleLoader, File inputFile) {
		super(moduleLoader);
		/**
		 * 不展示 promot
		 */
		showPrompt = false;

		setLayout(new BorderLayout());

		this.editorDataManager = new TextEditorDataManager(this);

		this.textEditorViewPort = new TextEditorViewPort();

		adjustFillFont(CustomizeFontEnum.COUSINEREGULARFONTFAMILY.getCousineDefinedFont(Font.PLAIN, 12));

		this.editorScrollBar = new EditorScrollBar(this);
		this.editorTextPane = new EgpsTextPane(this);
		this.inputFile = inputFile;
		add(getJScrollPane(), BorderLayout.CENTER);
		add(editorScrollBar, BorderLayout.EAST);
		addKeyListener(this);
		setTransferHandler();

		SwingUtilities.invokeLater(() -> {
			this.editorDataManager.setFile(inputFile);
		});
	}

	private void setTransferHandler() {
		// 初始化 文件的drop
		@SuppressWarnings("serial")
		TransferHandler handler = new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				// 我不知道为什么，貌似这个不起作用！！！！
				return TransferHandler.LINK;
			}

			@Override
			public boolean canImport(TransferSupport info) {
				// we only import FileList
				if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					return false;
				}
				return true;
			}

			@SuppressWarnings("unchecked")
			@Override
			public boolean importData(TransferSupport info) {
				if (!info.isDrop()) {
					return false;
				}
				// Check for FileList flavor
				if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
					SwingDialog.showErrorMSGDialog("Input error", "The eGPS doesn't support a drop of this type.");
					return false;
				}

				// Get the fileList that is being dropped.
				Transferable t = info.getTransferable();
				List<File> data;
				try {
					Object transferData = t.getTransferData(DataFlavor.javaFileListFlavor);
					data = (List<File>) transferData;
				} catch (Exception e) {
					return false;
				}
				// 得到文件之后，执行的动作！
				// 需要判定当前是否没有内容，没有就删掉自己
				if (editorTextPane.getText().isEmpty()) {
					tabbedPane.remove(TextEditorMain.this);
				}
				importNewFilesWorkhorse(data);
				return true;
			}

		};
		editorTextPane.setTransferHandler(handler);

	}

	private int horizontalScrollBarValue;

	public JScrollPane getJScrollPane() {
		if (scrollPane == null) {
			scrollPane = new JScrollPane(editorTextPane);
			scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			scrollPane.setRowHeaderView(new LineNumberHeaderView(this));
			JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
			horizontalScrollBar.addAdjustmentListener(e -> {
				horizontalScrollBarValue = horizontalScrollBar.getValue();
				// System.out.println("horizontalScrollBar " + horizontalScrollBar.getValue());

			});
			// When the window width is narrowed much, and then widened much, the background
			// of scrollPane.viewPort will be shown in the right side.
			// Change this color to see the wrong effect.
			scrollPane.getViewport().setBackground(backgroundColor);
		}
		return scrollPane;

	}

	public int getHorizontalScrollBarValue() {
		return horizontalScrollBarValue;
	}

	public EgpsTextPane getEgpsTextPane() {
		return editorTextPane;
	}

	public TextEditorViewPort getTextEditorViewPort() {
		return textEditorViewPort;
	}

	public JComponent getViewPanel() {

		return editorTextPane;
	}

//	@Override
//	public String[] getTeamAndAuthors() {
//		String[] info = new String[3];
//
//		info[0] = "EvolGen";
//		info[1] = Authors.MUHAILONG + "," + Authors.YANFANGQI + "," + Authors.YUDALANG + "," + Authors.LIHAIPENG;
//		info[2] = "http://www.picb.ac.cn/evolgen/";
//
//		return info;
//	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see egps.core.interfaces.module.UndoRedo#getModuleUndoRedoManager()
	 */
	public EGPSUndoManager getModuleUndoRedoManager() {

		if (editUndoManager == null) {
			editUndoManager = new EGPSUndoManager();
		}

		return editUndoManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see egps.core.interfaces.module.UndoRedo#undo()
	 */
	public void undo() {
		ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

		if (canUndo()) {

			listOfTextEditActions.undo();
			// textEditorMain.getEditorScrollBar().actionsAfterValueChanged();
			int lineNumberOnShow = listOfTextEditActions.getLineNumberOnShow();

			EditorScrollBar editorScrollBar = getEditorScrollBar();

			int value = editorScrollBar.getValue();

			int showLineMaxValue = value + editorScrollBar.getEveryPageShowLineNumberMaxCount();

			if ((lineNumberOnShow >= value && lineNumberOnShow < showLineMaxValue) || lineNumberOnShow == 0) {
				getEditorScrollBar().actionsAfterValueChanged();

			} else {
				getEditorScrollBar().setValue(lineNumberOnShow);
			}
			int startPosition = textEditorViewPort.getStartPosition();

			if (lineNumberOnShow < 0 || lineNumberOnShow < startPosition) {
				return;
			}

			Element rootElements = editorTextPane.getDocument().getDefaultRootElement();

			Element element = rootElements.getElement(lineNumberOnShow - startPosition);

			if (element != null) {
				int editorOffset = element.getStartOffset();

				editorTextPane.setEditorOffset(editorOffset);

			} else {
				editorTextPane.requestFocus();
			}
		}

		editorTextPane.clearSelection();
	}

	public void redo() {

		ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

		if (canRedo()) {
			listOfTextEditActions.redo();
			// textEditorMain.getEditorScrollBar().actionsAfterValueChanged();
			int lineNumberOnShow = listOfTextEditActions.getLineNumberOnShow();

			EditorScrollBar editorScrollBar = getEditorScrollBar();

			int value = editorScrollBar.getValue();

			int showLineMaxValue = value + editorScrollBar.getEveryPageShowLineNumberMaxCount();

			if ((lineNumberOnShow >= value && lineNumberOnShow < showLineMaxValue) || lineNumberOnShow == 0) {
				editorScrollBar.actionsAfterValueChanged();

			} else {
				editorScrollBar.setValue(lineNumberOnShow);
			}

			int startPosition = textEditorViewPort.getStartPosition();

			if (lineNumberOnShow < 0 || lineNumberOnShow < startPosition) {
				return;
			}

			Element rootElements = editorTextPane.getDocument().getDefaultRootElement();

			Element element = rootElements.getElement(lineNumberOnShow - startPosition);

			if (element != null) {
				int editorOffset = element.getStartOffset();
				editorTextPane.setEditorOffset(editorOffset);
			} else {
				editorTextPane.requestFocus();
			}
		}

		editorTextPane.clearSelection();
	}

	public void cut() {

		editorTextPane.cut();

		updateItem();
	}

	public void copy() {

		editorTextPane.copy();
		updateItem();
	}

	public void paste() {
		// int oldPos = editorTextPane.getCaretPosition();
		// int strLength = getPasteStrLength();
		editorTextPane.paste();
		tabbedPane.getSelectedComponent().requestFocus();
		// editorTextPane.setCaretPosition(oldPos + strLength);
		updateItem();
	}

	public void delete() {
		editorTextPane.replaceSelection("");
		updateItem();
//		bioMainFrame.updateMenuItems();
		tabbedPane.getSelectedComponent().requestFocus();
	}

	public void jumpLineNumber() {

		new GoToLineJDialog(UnifiedAccessPoint.getInstanceFrame(), this);
	}

	public boolean canJumpLineNumber() {
		return true;
	}

	public EGPSClipboard getModuleClipboard() {
		return getClipboard();
	}

	public EGPSSelection getModuleSelection() {
		return getSelection();
	}

	@Override
	public void changeToThisTab() {
		updateItem();

	}

	public void updateItem() {

//		String selectText = editorTextPane.getSelectedText();
//
//		if (selectText == null || selectText.equals("")) {
//			actionManager.get(AbstractActionsMaps.Edit_Cut).setEnabled(false);
//			actionManager.get(AbstractActionsMaps.Edit_Copy).setEnabled(false);
//			actionManager.get(AbstractActionsMaps.Edit_Delete).setEnabled(false);
//		} else {
//			actionManager.get(AbstractActionsMaps.Edit_Cut).setEnabled(true);
//			actionManager.get(AbstractActionsMaps.Edit_Copy).setEnabled(true);
//			actionManager.get(AbstractActionsMaps.Edit_Delete).setEnabled(true);
//
//		}
//
//		actionManager.get(AbstractActionsMaps.Edit_GoToLine).setEnabled(true);
//		Transferable content = clipboard.getContents(null);
//		if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
//			String text;
//			try {
//				text = (String) content.getTransferData(DataFlavor.stringFlavor);
//				if (text == null) {
//					actionManager.get(AbstractActionsMaps.Edit_Paste).setEnabled(false);
//				} else {
//					actionManager.get(AbstractActionsMaps.Edit_Paste).setEnabled(true);
//				}
//			} catch (UnsupportedFlavorException | IOException e) {
//				e.printStackTrace();
//			}
//		} else {
//			actionManager.get(AbstractActionsMaps.Edit_Paste).setEnabled(false);
//		}
//
//		if (editUndoManager.canUndo()) {
//			actionManager.get(AbstractActionsMaps.Edit_Undo).setEnabled(true);
//		}
//
//		if (editUndoManager.canRedo()) {
//			actionManager.get(AbstractActionsMaps.Edit_Redo).setEnabled(true);
//		}
	}

	public EGPSClipboard getClipboard() {
		if (editorClipboard == null) {
			editorClipboard = new EGPSClipboard();
		}
		return editorClipboard;
	}

	public EGPSSelection getSelection() {
		if (editorSelection == null) {
			editorSelection = new EGPSSelection();
		}
		return editorSelection;
	}

	private int getPasteStrLength() {
		Transferable content = clipboard.getContents(null);
		if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {

			try {
				String text = (String) content.getTransferData(DataFlavor.stringFlavor);
				return text.length();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	public TextEditorDataManager getEditorDataManager() {
		return editorDataManager;
	}

	public LineNumberHeaderView getEditorLineNumberPane() {
		return lineNumberPane;
	}

	public EditorScrollBar getEditorScrollBar() {
		return editorScrollBar;
	}

	public void saveViewPanelAs() {

		SwingUtilities.invokeLater(() -> {
			// swing thread
			JFileChooser jfc = EGPSOutputFileChooser4TextEditor.getFileChooser();

			jfc.setDialogTitle("Save the results as ... ");
			jfc.setAcceptAllFileFilterUsed(false);

			jfc.setDialogType(JFileChooser.SAVE_DIALOG);

			jfc.addChoosableFileFilter(new FileFilterTxt());

			if (jfc.showSaveDialog(UnifiedAccessPoint.getInstanceFrame()) == JFileChooser.APPROVE_OPTION) {

				File selectedFile = jfc.getSelectedFile();
				File outputFile = null;
				if (jfc.getFileFilter() instanceof FileFilterTxt) {

					if (selectedFile.getAbsolutePath().toUpperCase().endsWith((".txt").toUpperCase())) {
						outputFile = new File(selectedFile.getAbsolutePath());
					} else {
						outputFile = new File(selectedFile.getAbsolutePath() + "." + "txt");
					}
				}

				if (outputFile.exists()) {
					int res = JOptionPane.showConfirmDialog(UnifiedAccessPoint.getInstanceFrame(),
							"File exists, confirm to overlap?", "Warning", JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					if (res != JOptionPane.OK_OPTION) {
						return;
					}
				}

				EditorWriteFile task = new EditorWriteFile(inputFile, outputFile, editorDataManager);

				registerRunningTask(task);

				EGPSOutputFileChooser4TextEditor.setEGPSLastPath(outputFile.getPath());

			}
		});

	}

	public boolean canUndo() {
		return editorDataManager.getListOfTextEditActions().canUndo();
	}

	public boolean canRedo() {
		return editorDataManager.getListOfTextEditActions().canRedo();
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

			editorTextPane.getSelectEditor().setShiftDown(true);
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	public void findOrReplace() {

		new FindAndReplaceDialog(UnifiedAccessPoint.getInstanceFrame(), this);

		editorTextPane.requestFocus();

	}

	public boolean canFindOrReplace() {
		return true;
	}

	public boolean canCut() {
		return false;
	}

	public boolean canCopy() {
		return false;
	}

	public boolean canPaste() {
		return false;
	}

	public boolean canDelete() {
		return false;
	}

	public void importFile() {
		EGPSFileChooser egpsFileChooser = new EGPSFileChooser(this.getClass());
		egpsFileChooser.setMultiSelectionEnabled(true);
		int showOpenDialog = egpsFileChooser.showOpenDialog();

		if (showOpenDialog == EGPSFileChooser.APPROVE_OPTION) {
			if (editorTextPane.getText().isEmpty()) {
				// 把自己关掉
				tabbedPane.remove(this);
			}
			File[] selectedFiles = egpsFileChooser.getSelectedFiles();
			importNewFilesWorkhorse(Arrays.asList(selectedFiles));
		}
	}

	private void importNewFilesWorkhorse(List<File> data) {
		MethodsForText2Editor methodsForText2Editor = new MethodsForText2Editor();

		for (File file : data) {
			SwingUtilities.invokeLater(() -> {
				methodsForText2Editor.addNewTextEditorTab(file, false);
			});
		}
	}

	@Override
	public boolean canImport() {
		return true;
	}

	@Override
	public void importData() {
		importFile();
		invokeTheFeatureMethod(0);
	}

	@Override
	public boolean canExport() {
		return true;
	}

	@Override
	public void exportData() {
		saveViewPanelAs();
	}

	@Override
	public String[] getFeatureNames() {
		return new String[] { "Large volume text operation" };
	}

	@Override
	protected void initializeGraphics() {

	}

	@Override
	public Optional<Color> couldSetFillColor() {
		return Optional.empty();
	}

	@Override
	public Optional<Font> couldSetFont() {
		Font font = textEditorViewPort.getFont();
		return Optional.of(font);
	}

	@Override
	public Optional<Color> couldSetLineColor() {
		Color foreground = editorTextPane.getForeground();
		return Optional.of(foreground);
	}

	@Override
	public Optional<Integer> couldSetLineThickness() {
		Font font = textEditorViewPort.getFont();
		int size = font.getSize();
		return Optional.of(size);
	}

	@Override
	public void adjustFillColor(Color newCol) {
	}

	@Override
	public void adjustFillFont(Font newFont) {
		textEditorViewPort.setFont(newFont);
		repaint();
	}

	@Override
	public void adjustLineColor(Color newCol) {
		editorTextPane.setForeground(newCol);	}

	@Override
	public void adjustLineThickness(int newThickNess) {
		Font font = textEditorViewPort.getFont();
		adjustFillFont(font.deriveFont((float) (newThickNess)));
	}

	@Override
	public IInformation getModuleInfo() {
		IInformation iInformation = new IInformation() {

			@Override
			public String getWhatDataInvoked() {
				return "The data is loading from the import dialog.";
			}

			@Override
			public String getSummaryOfResults() {
				return "The functionality is powered by the eGPS software.";
			}
		};
		return iInformation;
	}

}
