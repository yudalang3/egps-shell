package egps2.builtin.modules.largetextedi.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import egps2.builtin.modules.largetextedi.TextEditorMain;
import egps2.builtin.modules.largetextedi.actions.ListOfTextEditActions;
import egps2.builtin.modules.largetextedi.gui.EditorScrollBar;
import egps2.builtin.modules.largetextedi.gui.EgpsTextPane;
import egps2.builtin.modules.largetextedi.gui.TextEditorDataManager;
import egps2.builtin.modules.largetextedi.gui.TextEditorViewPort;
import egps2.builtin.modules.largetextedi.model.EditorCaret;
import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.SelectEditor;


/**
 * EgpsTextPanelKeyListener belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class EgpsTextPanelKeyListener implements KeyListener {
	private SelectEditor selectEditor;
	private EditorCaret editorCaret;
	private TextEditorViewPort textEditorViewPort;
	private EgpsTextPane egpsTextPane;
	private TextEditorDataManager editorDataManager;
	private boolean isOnlyDelete; // 可能会是选中的同时,进行删除
	private TextEditorMain textEditorMain;

	public EgpsTextPanelKeyListener(EgpsTextPane egpsTextPane) {
		this.egpsTextPane = egpsTextPane;
		this.textEditorMain = egpsTextPane.getTextEditorMain();
		this.editorDataManager = egpsTextPane.getEditorDataManager();
		this.textEditorViewPort = egpsTextPane.getTextEditorViewPort();
		this.editorCaret = egpsTextPane.getEditorCaret();
		this.selectEditor = egpsTextPane.getSelectEditor();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

			selectEditor.setShiftDown(false);

			editorCaret.setCurrentFocusReassignment(true);
		}

	}

	public void backSpaceDeleteAction(int dot, Element defaultRootElement, String selectedText) {

		if (dot == 0 && (selectedText == null || "".equals(selectedText))) {
			egpsTextPane.clearSelection();
			int selectStartShowLineNumber = textEditorViewPort.getStartPosition();
			// 删除当前界面的第一行,但是这个第一行不是整个整个文件中的第一行
			if (selectStartShowLineNumber != 1) {
				try {
					int showLineNumber = defaultRootElement.getElementIndex(dot);

					LineObj lineObj = egpsTextPane.getEditorDataManager()
							.getCurrentDataOfShowLine(selectStartShowLineNumber - 1);

					String line = lineObj.getLine();

					String selectEditorElementLine = line + egpsTextPane.getLine(showLineNumber);

					ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

					int belongingGroupIndex = listOfTextEditActions.getNewBelongingGroupIndexAssigningValue();

					egpsTextPane.updateLineObj(lineObj, selectStartShowLineNumber - 1, selectEditorElementLine,
							belongingGroupIndex);

					LineObj removeLineObj = editorDataManager.getCurrentDataOfShowLine(selectStartShowLineNumber);

					egpsTextPane.removeLineObj(removeLineObj, removeLineObj.getLineNumberOnShow(), belongingGroupIndex);
					editorCaret.setCurrentFocusOnShowLineNumber(selectStartShowLineNumber - 1);
					editorCaret.setCurrentFocusOnOffset(line.length());
					EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
					editorScrollBar.setValue(editorScrollBar.getValue() - 1);
					egpsTextPane.getDocument().removeDocumentListener(egpsTextPane);

				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}

		isOnlyDelete = true;
	}

	public void deleteAction(int dot, Element defaultRootElement, String selectedText) {

		//// 删除当前界面的最后一行,但是这个最后一行不是整个整个文件中的最后一行
		if (dot == defaultRootElement.getEndOffset() - 1 && (selectedText == null || "".equals(selectedText))) {
			egpsTextPane.clearSelection();
			TextEditorDataManager editorDataManager = textEditorMain.getEditorDataManager();

			int lastLineNumber = editorDataManager.getMaxLength() + 1;
			int endShowLineNumber = textEditorViewPort.getEndPosition();

			if (endShowLineNumber != lastLineNumber) {
				try {
					LineObj lineObj = editorDataManager.getCurrentDataOfShowLine(endShowLineNumber - 1);
					LineObj lineObj1 = editorDataManager.getCurrentDataOfShowLine(endShowLineNumber);
					String line = lineObj.getLine();
					String selectEditorElementLine = line + lineObj1.getLine();
					ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();
					int belongingGroupIndex = listOfTextEditActions.getNewBelongingGroupIndexAssigningValue();
					egpsTextPane.updateLineObj(lineObj, endShowLineNumber - 1, selectEditorElementLine,
							belongingGroupIndex);
					LineObj removeLineObj = editorDataManager.getCurrentDataOfShowLine(endShowLineNumber);
					egpsTextPane.removeLineObj(removeLineObj, removeLineObj.getLineNumberOnShow(), belongingGroupIndex);
					editorCaret.setCurrentFocusOnShowLineNumber(endShowLineNumber - 1);
					editorCaret.setCurrentFocusOnOffset(line.length());
					EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
					editorScrollBar.actionsAfterValueChanged();
					egpsTextPane.getDocument().removeDocumentListener(egpsTextPane);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}
		isOnlyDelete = true;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int dot = egpsTextPane.getCaret().getDot();
		Element root = egpsTextPane.getDocument().getDefaultRootElement();
		String selectedText = egpsTextPane.getSelectedText();
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			backSpaceDeleteAction(dot, root, selectedText);
		}
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			deleteAction(dot, root, selectedText);
		}
		if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
			selectEditor.setShiftDown(true);
		}
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			enterAction(dot, root);
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			downAction(dot, root);

		}

		if (e.getKeyCode() == KeyEvent.VK_UP) {
			upAction(dot, root);

		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			leftAction(dot, root);

		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			rightAction(dot, root);
		}
		if (e.getKeyCode() == KeyEvent.VK_HOME) {
			homeAction(dot, root);
		}
		if (e.getKeyCode() == KeyEvent.VK_END) {
			endAction(dot, root);
		}
	}

	/**
	 * 键盘点击向右移动位置
	 *
	 * @Author: mhl
	 */
	private void rightAction(int dot, Element root) {
		int lineNumber = root.getElementIndex(dot);
		int rowNumberDisplay = textEditorViewPort.getShowLineCount();
		Element lineElement = root.getElement(lineNumber);
		int endOffset1 = lineElement.getEndOffset();
		if (lineNumber + 1 >= rowNumberDisplay && endOffset1 == dot + 1) {
			editorCaret.setCurrentFocusOnShowLineNumber(lineNumber + textEditorViewPort.getStartPosition() + 1);
			editorCaret.setCurrentFocusOnOffset(0);
			EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
			editorScrollBar.setValue(editorScrollBar.getValue() + 1);
			if (!selectEditor.isShiftDown()) {
				editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition());
				editorCaret.setMarkOffset(0);
				editorCaret.setStartPosition(textEditorViewPort.getStartPosition());
				egpsTextPane.clearSelection();
			} else {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						egpsTextPane.setSelectEditor();
					}
				});
			}

			textEditorMain.updateItem();

		} else if (!selectEditor.isShiftDown()) {

			int lineStartOffset = lineElement.getStartOffset();

			int newDot = dot + 1;

			int lineNumber1 = root.getElementIndex(newDot);

			int selectStartLineNumber = lineNumber == lineNumber1 ? lineNumber + textEditorViewPort.getStartPosition()
					: lineNumber1 + textEditorViewPort.getStartPosition();

			editorCaret.setMarkLineNumber(selectStartLineNumber);

			int selectionStart = lineNumber == lineNumber1 ? newDot - lineStartOffset : 0;

			editorCaret.setMarkOffset(selectionStart);

			editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

			egpsTextPane.clearSelection();

			textEditorMain.updateItem();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					egpsTextPane.setSelectEditor();
				}
			});

		}
	}

	/**
	 * 向左移动焦点
	 *
	 * @Author: mhl
	 */
	private void leftAction(int dot, Element root) {
		int lineNumber = root.getElementIndex(dot);
		int showLineNumber = lineNumber + textEditorViewPort.getStartPosition();
		if (showLineNumber == 1 && dot == 0) {
			return;
		}
		if (dot == 0) {
			EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
			int value = editorScrollBar.getValue();
			editorScrollBar.setValue(value - 1);
			Element lineElement = root.getElement(0);
			int endOffset = lineElement.getEndOffset();
			egpsTextPane.requestFocus();
			egpsTextPane.setCaretPosition(endOffset);
		}

		if (!selectEditor.isShiftDown()) {
			Element lineElement = root.getElement(lineNumber);
			int lineStartOffset = lineElement.getStartOffset();
			int newDot = dot - 1;
			int lineNumber1 = root.getElementIndex(newDot);
			Element lineElement1 = root.getElement(lineNumber1);
			int selectStartLineNumber = lineNumber == lineNumber1 ? lineNumber + textEditorViewPort.getStartPosition()
					: lineNumber1 + textEditorViewPort.getStartPosition();
			editorCaret.setMarkLineNumber(selectStartLineNumber);
			int selectionStart = lineNumber == lineNumber1 ? newDot - lineStartOffset
					: newDot - lineElement1.getStartOffset();
			editorCaret.setMarkOffset(selectionStart);

			editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

			egpsTextPane.clearSelection();

			textEditorMain.updateItem();
		} else {
			SwingUtilities.invokeLater(() -> {
				egpsTextPane.setSelectEditor();
			});
		}
	}

	/**
	 * 键盘点击向上移动位置
	 *
	 * @Author: mhl
	 */
	private void upAction(int dot, Element root) {
		int elementIndex = root.getElementIndex(dot);
		if (elementIndex == 0) { // 判断是否当前焦点在当前界面的第一行
			EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
			int value = editorScrollBar.getValue(); // 当前文档的行号
			if (value < 1) {
				return;
			}
			editorCaret.setCurrentFocusOnShowLineNumber(textEditorViewPort.getStartPosition());
			editorCaret.setCurrentFocusOnOffset(dot);
			editorScrollBar.setValue(value - 1);
			Element root1 = egpsTextPane.getDocument().getDefaultRootElement();
			Element lineElement = root1.getElement(elementIndex);
			int endOffset = lineElement.getEndOffset();
			egpsTextPane.requestFocus();
			int position = dot > endOffset ? endOffset - 1 : dot;
			egpsTextPane.setCaretPosition(position);
		}

		if (!selectEditor.isShiftDown()) {
			int lineNumber = root.getElementIndex(dot);
			Element lineElement = root.getElement(lineNumber);
			int lineStartOffset = lineElement.getStartOffset();
			editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition() - 1);
			editorCaret.setMarkOffset(dot - lineStartOffset);
			editorCaret.setStartPosition(textEditorViewPort.getStartPosition());
			egpsTextPane.clearSelection();
			textEditorMain.updateItem();
		} else {
			SwingUtilities.invokeLater(() -> {
				egpsTextPane.setSelectEditor();
			});
		}
	}

	/**
	 * 键盘点击向下移动位置
	 *
	 * @Author: mhl
	 */
	private void downAction(int dot, Element root) {
		int rowNumberDisplay = textEditorViewPort.getShowLineCount();
		int elementIndex = root.getElementIndex(dot);
		if (elementIndex + 1 >= rowNumberDisplay) { // 当前焦点在当前界面的最后一行,当按下向下键时,进行翻页
			Element element = root.getElement(elementIndex);
			editorCaret.setCurrentFocusOnShowLineNumber(elementIndex + textEditorViewPort.getStartPosition() + 1);
			editorCaret.setCurrentFocusOnOffset(dot - element.getStartOffset());
			EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
			editorScrollBar.setValue(editorScrollBar.getValue() + 1);
			Element newElement = root.getElement(elementIndex);
			int lineStartOffset = newElement.getStartOffset();
			int endOffset = newElement.getEndOffset();
			int newOffset = lineStartOffset + editorCaret.getCurrentFocusOnOffset();
			egpsTextPane.requestFocus();
			int position = newOffset > endOffset ? endOffset - 1 : newOffset;
			egpsTextPane.setCaretPosition(position);
		}

		if (!selectEditor.isShiftDown()) {

			int dot1 = egpsTextPane.getCaret().getDot();

			Element root1 = egpsTextPane.getDocument().getDefaultRootElement();

			int lineNumber = root1.getElementIndex(dot1);

			Element lineElement = root1.getElement(lineNumber);

			int lineStartOffset = lineElement.getStartOffset();

			editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition() + 1);

			editorCaret.setMarkOffset(dot - lineStartOffset);

			editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

			egpsTextPane.clearSelection();

			textEditorMain.updateItem();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					egpsTextPane.setSelectEditor();
				}
			});

		}
	}

	/**
	 * 
	 * 回车键对文档新增
	 *
	 * @Author: mhl
	 */
	private void enterAction(int dot, Element root) {
		int lineNumber = root.getElementIndex(dot);

		Element lineElement = root.getElement(lineNumber);

		int lineStartOffset = lineElement.getStartOffset();

		editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition() + 1);

		editorCaret.setMarkOffset(dot - lineStartOffset);

		editorCaret.setStartPosition(textEditorViewPort.getStartPosition());
	}

	private void homeAction(int dot, Element root) {

		if (!selectEditor.isShiftDown()) {

			int dot1 = egpsTextPane.getCaret().getDot();

			Element root1 = egpsTextPane.getDocument().getDefaultRootElement();

			int lineNumber = root1.getElementIndex(dot1);

			Element lineElement = root1.getElement(lineNumber);

			int lineStartOffset = lineElement.getStartOffset();

			editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition() + 1);

			editorCaret.setMarkOffset(dot - lineStartOffset);

			editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

			egpsTextPane.clearSelection();

			textEditorMain.updateItem();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					egpsTextPane.setSelectEditor();
				}
			});

		}
	}

	private void endAction(int dot, Element root) {

		if (!selectEditor.isShiftDown()) {

			int dot1 = egpsTextPane.getCaret().getDot();

			Element root1 = egpsTextPane.getDocument().getDefaultRootElement();

			int lineNumber = root1.getElementIndex(dot1);

			Element lineElement = root1.getElement(lineNumber);

			int lineStartOffset = lineElement.getStartOffset();

			editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition() + 1);

			editorCaret.setMarkOffset(dot - lineStartOffset);

			editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

			egpsTextPane.clearSelection();

			textEditorMain.updateItem();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					egpsTextPane.setSelectEditor();
				}
			});

		}
	}

	public boolean isOnlyDelete() {
		return isOnlyDelete;
	}

	public void setOnlyDelete(boolean onlyDelete) {
		isOnlyDelete = onlyDelete;
	}
}
