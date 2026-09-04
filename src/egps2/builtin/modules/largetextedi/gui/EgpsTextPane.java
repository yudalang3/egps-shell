package egps2.builtin.modules.largetextedi.gui;

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.Position;

import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.largetextedi.TextEditorMain;
import egps2.builtin.modules.largetextedi.actions.FindAndReplaceDialog;
import egps2.builtin.modules.largetextedi.actions.GoToLineJDialog;
import egps2.builtin.modules.largetextedi.actions.ListOfTextEditActions;
import egps2.builtin.modules.largetextedi.actions.ReplaceAllJDialog;
import egps2.builtin.modules.largetextedi.listener.EgpsTextPanelKeyListener;
import egps2.builtin.modules.largetextedi.model.DeleteLineAction;
import egps2.builtin.modules.largetextedi.model.EditorCaret;
import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.NewLinesAction;
import egps2.builtin.modules.largetextedi.model.RevisedLineAction;
import egps2.builtin.modules.largetextedi.model.SelectEditor;
import egps2.builtin.modules.largetextedi.model.TextEditAction;


/**
 * Copyright (c) 2019 Chinese Academy of Sciences. All rights reserved.
 *
 * @author mhl
 * @ClassName EgpsTextPane
 * @Date Created on:2019-09-06 14:27
 */
public class EgpsTextPane extends JTextPane
        implements MouseListener, MouseWheelListener, CaretListener, MouseMotionListener, DocumentListener {

    private static final long serialVersionUID = -1437526078275519891L;
    private EgpsTextPanelKeyListener egpsTextPanelKeyListener;

    private List<LineObj> readOriginalData = new ArrayList<LineObj>(40);

    private String newline = System.getProperty("line.separator");

    private TextEditorMain textEditorMain;

    private TextEditorDataManager editorDataManager;

    private SelectEditor selectEditor;

    private TextEditorViewPort textEditorViewPort;

    private TextEditor2D textEditor2D;

    private int deleteIndex = 0; // 记录被删除对象的索引

    private EditorCaret editorCaret;

    public EditorCaret getEditorCaret() {
        return editorCaret;
    }

    private int belongingGroupIndex;

    private boolean isDeleteAcrossPages;

    private volatile ReentrantLock lock = new ReentrantLock(true);

    public EgpsTextPane(TextEditorMain textEditorMain) {

        this.textEditorMain = textEditorMain;

        this.textEditorViewPort = textEditorMain.getTextEditorViewPort();

        getDocument().addUndoableEditListener(textEditorMain.getModuleUndoRedoManager());

        this.editorDataManager = textEditorMain.getEditorDataManager();

        this.selectEditor = new SelectEditor();

        this.textEditor2D = new TextEditor2D(this);

        this.editorCaret = new EditorCaret();

        setEditorKit(new ExtendedStyledEditorKit());

        new TextKeyEvent(this);
        this.egpsTextPanelKeyListener = new EgpsTextPanelKeyListener(this);
        addKeyListener(egpsTextPanelKeyListener);

        addMouseListener(this);

        addMouseWheelListener(this);

        addCaretListener(this);

        addMouseMotionListener(this);
    }

    public void setText(int startOnShowLineNumber, int endOnShowLineNumber) throws IOException {
        lock();
        getDocument().removeDocumentListener(this);
        readOriginalData.clear();

        readOriginalData.addAll(editorDataManager.readOriginalData(startOnShowLineNumber, endOnShowLineNumber));

        int size = readOriginalData.size();

        if (size == 0) {
            setText("");
            setCaretPosition(startOnShowLineNumber, endOnShowLineNumber);
            getDocument().addDocumentListener(this);
            return;
        }

        Collections.sort(readOriginalData);

        int displayLength = size - 1;

        setFont(textEditorViewPort.getFont());

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < size; i++) {
            if (i != displayLength) {
                LineObj lineObj = readOriginalData.get(i);

                String line = lineObj.getLine();

                String lineTerminator = lineObj.getLineTerminator();

                if (lineTerminator == null) {
                    lineTerminator = newline;
                }
                builder.append(editorDataManager.replaceAll(line) + lineTerminator);

            } else {
                LineObj lineObj = readOriginalData.get(i);

                builder.append(editorDataManager.replaceAll(lineObj.getLine()));
            }
        }
        setText(builder.toString());
        setCaretPosition(startOnShowLineNumber, endOnShowLineNumber);
        getDocument().addDocumentListener(this);
        unlock();
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }

    public void setCaretPosition(int startOnShowLineNumber, int endOnShowLineNumber) {
        try {
            if (textEditor2D.isSelectionElement()) {
                requestFocus(); // 获取输入焦点
                select(textEditor2D.getSelectStartPos(), textEditor2D.getSelectEndPos());

            } else if (editorCaret.getCurrentFocusOnShowLineNumber() >= startOnShowLineNumber
                    && editorCaret.getCurrentFocusOnShowLineNumber() < endOnShowLineNumber) {
                requestFocus();
                Element defaultRootElement = getDocument().getDefaultRootElement();
                int focusOnShowLineNumber = editorCaret.getCurrentFocusOnShowLineNumber()
                        - textEditorViewPort.getStartPosition();
                Element element = defaultRootElement.getElement(focusOnShowLineNumber);
                int offset = element == null ? editorCaret.getCurrentFocusOnOffset()
                        : element.getStartOffset() + editorCaret.getCurrentFocusOnOffset();
                setCaretPosition(offset);
            } else {
                //    setCaretPosition(0);
                if (isFocusOwner()) {
                    textEditorMain.requestFocus();
                }
            }
        } catch (Exception e) {
            return;
        }

    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        Element defaultRootElement = getDocument().getDefaultRootElement();
        DocumentEvent.ElementChange change = e.getChange(defaultRootElement);
        try {
            int editorOffset = e.getOffset();
            // 从零开始索引
            int showLineNumber = defaultRootElement.getElementIndex(editorOffset);
            int actualShowLineNumber = showLineNumber + textEditorViewPort.getStartPosition();
            boolean flag = !egpsTextPanelKeyListener.isOnlyDelete() && selectEditor.getStartLineNumber() > 0;
            if (flag) {

                actualShowLineNumber = selectEditor.getStartLineNumber();

                editorOffset = selectEditor.getSelectionStart();
            }

            ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

            int belongingGroupIndex = flag ? this.belongingGroupIndex
                    : listOfTextEditActions.getNewBelongingGroupIndexAssigningValue();

            LineObj lineObj = editorDataManager.getCurrentDataOfShowLine(actualShowLineNumber);

            String showElementLine = getLine(showLineNumber);

            if (change == null) {

                if (flag && isDeleteAcrossPages) { // 在跨页选择,并且删除选择同时新增,由于界面不会跳转到选择位置的起始位置,导致数据获取不正确,因此需截取
                    /*
                     * eg: 第一页 => 11(11 2222 3333 4444 5555 6666 第二页 => 7777 8888 999)9 10101010
                     * 11111111 12121212 删除括号中数据同时按下字母"a",由于是跨页删除, 导致 结果为
                     *
                     * showElementLine = "a9" ,正确结果应为"11a9"
                     *
                     */

                    if (editorCaret.getStartPosition() == textEditorViewPort.getStartPosition()) {

                        showElementLine = showElementLine + lineObj.getLine().substring(editorOffset);
                    } else {

                        showElementLine = lineObj.getLine().substring(0, editorOffset) + showElementLine;
                    }

                    isDeleteAcrossPages = false;
                }

                updateLineObj(lineObj, actualShowLineNumber, showElementLine, belongingGroupIndex);

                int currentFocusOnOffset = showElementLine.length() - lineObj.getLine().length();

                editorCaret.setCurrentFocusOnOffset(editorCaret.getCurrentFocusOnOffset() + currentFocusOnOffset);

            } else {

                if (flag && isDeleteAcrossPages) { // 在跨页选择,并且删除选择同时新增,由于界面不会跳转到选择位置的起始位置,导致数据获取不正确,因此需截取

                    /*
                     * eg: 第一页 => 1111 22(22 3333 4444 5555 6666 第二页 => 7777 8888 999)9 10101010
                     * 11111111 12121212 删除括号中数据同时复制"abc <br/> def",由于是跨页删除, 导致 结果为
                     *
                     * showElementLine = "229 <br/>" ,正确结果应为"22abc <br/> def9";
                     *
                     */

                    if (editorCaret.getStartPosition() != textEditorViewPort.getStartPosition()) {

                        showElementLine = lineObj.getLine().substring(0, editorOffset) + showElementLine;
                    }

                    String line = lineObj.getLine();

                    int elementLength = showElementLine.length();

                    if (!line.equals(showElementLine) && elementLength != 0) {

                        updateLineObj(lineObj, actualShowLineNumber, showElementLine, belongingGroupIndex);
                    }

                    addLineObjs(change, actualShowLineNumber, belongingGroupIndex, lineObj);

                    isDeleteAcrossPages = false;

                } else {
                    String line = lineObj.getLine();

                    int elementLength = showElementLine.length();

                    int startOffset = defaultRootElement.getElement(showLineNumber).getStartOffset();

                    boolean isNotBeforTheLine = startOffset != editorOffset || line.length() == 0;

                    if (!line.equals(showElementLine) && elementLength != 0 && isNotBeforTheLine) {

                        updateLineObj(lineObj, actualShowLineNumber, showElementLine, belongingGroupIndex);
                    }

                    addLineObjs(change, showLineNumber, isNotBeforTheLine, belongingGroupIndex, lineObj);
                }

            }

        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        changedUpdate(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {

        Element defaultRootElement = getDocument().getDefaultRootElement();

        DocumentEvent.ElementChange change = e.getChange(defaultRootElement);

        EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

        int editorOffset = e.getOffset();

        ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

        int belongingGroupIndex = listOfTextEditActions.getNewBelongingGroupIndexAssigningValue();

        try {

            String selectedText = getSelectedText();

            boolean isSelect = selectedText == null || selectedText.equals("");

            // 从零开始索引
            int showLineNumber = defaultRootElement.getElementIndex(editorOffset);

//			int selectStartShowLineNumber = isSelect ? showLineNumber + textEditorViewPort.getStartPosition()
//					: selectEditor.getStartLineNumber();

            int selectStartShowLineNumber = isSelect ? showLineNumber + textEditorViewPort.getStartPosition()
                    : selectEditor.getStartLineNumber();

            LineObj lineObj = editorDataManager.getCurrentDataOfShowLine(selectStartShowLineNumber);

            String selectEditorElementLine = isSelect ? getLine(showLineNumber)
                    : selectEditor.getRemainingCharacters() == null ? "" : selectEditor.getRemainingCharacters();

            if (change == null) {
//&& !(selectedText.contains("\r") || selectedText.contains("\n"))
                updateLineObj(lineObj, selectStartShowLineNumber, selectEditorElementLine, belongingGroupIndex);

            } else {

                String line = lineObj.getLine();

                deleteIndex++;

                // 如果选择的第一行数据发生改变,并且没有被全部删除时,该行数据进行修改,而非删除

                if (!line.equals(selectEditorElementLine)) {

                    updateLineObj(lineObj, selectStartShowLineNumber, selectEditorElementLine, belongingGroupIndex);
                }

                if (isSelect) {
                    LineObj removeLineObj = editorDataManager.getCurrentDataOfShowLine(selectStartShowLineNumber + 1);

                    removeLineObj(removeLineObj, removeLineObj.getLineNumberOnShow(), belongingGroupIndex);
                } else {
                    removeLineObjs(selectStartShowLineNumber, belongingGroupIndex);
                }

                int startShowNumber = textEditorViewPort.getStartPosition();

                int endShowNumber = textEditorViewPort.getEndPosition();

                int selectEndShowLineNumber = selectEditor.getEndLineNumber();

                // 跨页删除
                if (selectStartShowLineNumber < startShowNumber || selectEndShowLineNumber > endShowNumber) {

                    if (selectStartShowLineNumber != -1) {

                        editorScrollBar.removeAdjustmentListener(editorScrollBar);

                        editorScrollBar.setValue(selectStartShowLineNumber);

                        editorScrollBar.addAdjustmentListener(editorScrollBar);

                        editorOffset = selectEditor.getSelectionStart();

                        int elementIndex = defaultRootElement.getElementIndex(editorOffset);

                        Element element = defaultRootElement.getElement(elementIndex);

                        editorCaret.setCurrentFocusOnShowLineNumber(selectStartShowLineNumber);

                        editorCaret.setCurrentFocusOnOffset(editorOffset - element.getStartOffset());

                        isDeleteAcrossPages = true;

                    }
                } else {

                    int elementIndex = defaultRootElement.getElementIndex(editorOffset);

                    Element element = defaultRootElement.getElement(elementIndex);

                    editorCaret.setCurrentFocusOnShowLineNumber(elementIndex + textEditorViewPort.getStartPosition());

                    editorCaret.setCurrentFocusOnOffset(editorOffset - element.getStartOffset());

                }
            }

        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        if (egpsTextPanelKeyListener.isOnlyDelete()) {
            changedUpdate(e);
        } else {
            editorScrollBar.removeAdjustmentListener(editorScrollBar);

            int startPosition = editorCaret.getStartPosition();

            editorScrollBar.setValue(startPosition);

            editorScrollBar.addAdjustmentListener(editorScrollBar);

            this.belongingGroupIndex = belongingGroupIndex;

            // isOnlyDelete = false;
            egpsTextPanelKeyListener.setOnlyDelete(false);
        }

    }

    @Override
    public void changedUpdate(DocumentEvent e) {

        editorCaret.setCurrentFocusReassignment(true);

        clearSelection();

        // textEditorMain.repaint();
//        SwingUtilities.invokeLater(() -> {
//            BioMainFrame.getInstance().updateMenuItems();
//        });

    }

    public void updateLineObj(LineObj lineObj, int showLineNumber, String line, int belongingGroupIndex) {

        int lineNumber = lineObj.getLineNumber();

        LineObj newLineObj = new LineObj(line, lineNumber, lineObj.getHeadOffset(), lineObj.getLineTerminator());

        newLineObj.setLineNumberOnShow(showLineNumber);

        RevisedLineAction revisedLineAction = new RevisedLineAction(newLineObj);

        revisedLineAction.setBelongingGroupIndex(belongingGroupIndex);

        ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

        if (listOfTextEditActions.size() > 0) {
            listOfTextEditActions.lineNumberPlusOneAfterShowLineNumber(revisedLineAction);
        }

        listOfTextEditActions.addTextEditActions(revisedLineAction);

    }

    ;

    /**
     * Get the characters of each line elemen
     *
     * @param lineNumberOnShow
     * @author mhl, Element lineElement
     * @Date Created on:2019-08-28 09:43
     */
    public String getLine(int lineNumberOnShow) throws BadLocationException {
        Element root = getDocument().getDefaultRootElement();

        Element lineElement = root.getElement(lineNumberOnShow);

        int lineStartOffset = lineElement.getStartOffset();

        int lineEndOffset = lineElement.getEndOffset();

        String line = lineElement.getDocument().getText(lineStartOffset, lineEndOffset - lineStartOffset);

        Pattern p = Pattern.compile("\r|\n");

        Matcher m = p.matcher(line);

        line = m.replaceAll("");

        return line;
    }

    private void removeLineObjs(int showLineNumber, int newBelongingGroupIndexAssigningValue)
            throws BadLocationException {

        int selectStartLineNumber = selectEditor.getStartLineNumber();

        if (selectEditor.isThereAnyRemaining()) {

            selectStartLineNumber += 1;
        }

        int selectEndLineNumber = selectEditor.getEndLineNumber() + 1;

        List<LineObj> readOriginalData = editorDataManager.readOriginalData(selectStartLineNumber, selectEndLineNumber);

        Collections.sort(readOriginalData);

        for (int i = readOriginalData.size() - 1; i >= 0; i--) {

            LineObj lineObj = readOriginalData.get(i);

            removeLineObj(lineObj, lineObj.getLineNumberOnShow(), newBelongingGroupIndexAssigningValue);
        }
    }

    public void removeLineObj(LineObj lineObj, int showLineNumber, int belongingGroupIndex)
            throws BadLocationException {

        LineObj newLineObj = new LineObj(lineObj.getLine(), lineObj.getLineNumber(), lineObj.getHeadOffset(),
                lineObj.getLineTerminator());

        newLineObj.setLineNumberOnShow(showLineNumber);

        DeleteLineAction deleteLineAction = new DeleteLineAction(newLineObj);

        deleteLineAction.setIndex(deleteIndex);

        deleteLineAction.setBelongingGroupIndex(belongingGroupIndex);

        ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

        if (listOfTextEditActions.size() > 0) {
            listOfTextEditActions.lineNumberPlusOneAfterShowLineNumber(deleteLineAction);
        }

        listOfTextEditActions.addTextEditActions(deleteLineAction);

    }

    private void addLineObjs(DocumentEvent.ElementChange change, int showLineNumber, int belongingGroupIndex,
                             LineObj lineObj) throws BadLocationException {

        Element[] childrenAdded = change.getChildrenAdded();

        int showLineCount = textEditorViewPort.getShowLineCount();

        LineObj previousLineObj = textEditorMain.getEditorDataManager().getCurrentDataOfShowLine(showLineNumber);

        String previousLineTerminator = previousLineObj.getLineTerminator();

        int length = 0;

        // 临时变量,用来计算当前显示行号,同时用于获取黏贴进入panel中的数据
        int tempShowLineNumber = 0;
        // 当选择的数据横跨多页并且新增的多行数据,如果 flag = true 则表示选择的数据是从后往前选择,反之从前向后选择
        // 当数据从后往前选择时,选择的<code>结尾</code>位置在当前界面中,因此显示行号需重新计算
        // 当数据从前往后选择时,选择的<code>开始</code>位置在当前界面中,因此显示行号需重新计算
        boolean flag = showLineNumber > textEditorViewPort.getStartPosition()
                || selectEditor.getSelectionStart() == textEditorViewPort.getStartPosition();

        if (flag) {

            tempShowLineNumber = showLineNumber - textEditorViewPort.getStartPosition();
        }
        String line2 = lineObj.getLine();

        int temp = 0;

        for (int i = 0; i < childrenAdded.length; i++) {
            tempShowLineNumber++;

            String line = getLine(tempShowLineNumber);

            temp = showLineNumber + tempShowLineNumber;

            if (i == childrenAdded.length - 1 && flag) {
                // 当选择的数据是从后向前截取时,需重新截取最后一行数据
                line += line2.substring(selectEditor.getSelectionStart());
            }

            length = line.length();

            if (flag) {
                temp = temp - 1;
            }
            addLineObj(temp, line, previousLineTerminator, belongingGroupIndex);

        }

        // 计算操作成功后,焦点所在行的偏移量
        if (flag) {
            length -= line2.substring(selectEditor.getSelectionStart()).length();
        } else {
            length -= line2.length() - editorCaret.getMarkOffset();
        }

        if (temp >= showLineCount) {

            EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

            editorScrollBar.removeAdjustmentListener(editorScrollBar);

            int newValue = temp - showLineCount + 1;

            editorScrollBar.setValue(newValue);

            editorScrollBar.addAdjustmentListener(editorScrollBar);
        }

        editorCaret.setCurrentFocusOnOffset(length);

        editorCaret.setCurrentFocusOnShowLineNumber(temp);

    }

    private void addLineObjs(DocumentEvent.ElementChange change, int showLineNumber, boolean isNotBeforTheLine,
                             int belongingGroupIndex, LineObj lineObj) throws BadLocationException {

        Element[] childrenAdded = change.getChildrenAdded();

        int showLineCount = textEditorViewPort.getShowLineCount();

        LineObj previousLineObj = readOriginalData.get(showLineNumber);

        String previousLineTerminator = previousLineObj.getLineTerminator();

        if (isNotBeforTheLine) {// 当前行之后或者行中添加新行

            int length = 0;

            for (int i = 0; i < childrenAdded.length; i++) {
                showLineNumber++;

                String line = getLine(showLineNumber);

                length = line.length();

                addLineObj(showLineNumber + textEditorViewPort.getStartPosition(), line, previousLineTerminator,
                        belongingGroupIndex);
            }

            if (selectEditor.getStartLineNumber() > -1) {
                // 在当前界面中,有选择数据,删除并且黏贴新的数据,因此重新计算操作成功后,焦点所在行的偏移量
                length -= lineObj.getLine().substring(selectEditor.getSelectionStart()).length();
            } else {
                // 当没有选择数据时,黏贴多行数据,计算当前焦点所在行的偏移量
                length -= lineObj.getLine().length() - editorCaret.getMarkOffset();
            }

            if (showLineNumber >= showLineCount) {

                EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

                editorScrollBar.removeAdjustmentListener(editorScrollBar);

                int value = editorScrollBar.getValue();

                int newValue = value + showLineNumber - showLineCount + 1;

                editorScrollBar.setValue(newValue);

                editorScrollBar.addAdjustmentListener(editorScrollBar);
            }

            editorCaret.setCurrentFocusOnOffset(length);

            editorCaret.setCurrentFocusOnShowLineNumber(showLineNumber + editorCaret.getStartPosition());

        } else {// 当前行之前添加新的行

            for (int i = 0; i < childrenAdded.length; i++) {

                String line = getLine(showLineNumber);

                int temp = showLineNumber + textEditorViewPort.getStartPosition();

                addLineObj(temp, line, previousLineTerminator, belongingGroupIndex);

                showLineNumber++;
            }

            String line = getLine(showLineNumber);

            int length = 0;

            if (!lineObj.getLine().equals(line)) {
                int temp = showLineNumber + textEditorViewPort.getStartPosition();

                updateLineObj(lineObj, temp, line, belongingGroupIndex);

                length = line.length() - lineObj.getLine().length();

            }

            if (showLineNumber >= showLineCount) {

                EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

                editorScrollBar.removeAdjustmentListener(editorScrollBar);

                int value = editorScrollBar.getValue();

                int newValue = value + showLineNumber - showLineCount + 1;

                editorScrollBar.setValue(newValue);

                editorScrollBar.addAdjustmentListener(editorScrollBar);
            }

            editorCaret.setCurrentFocusOnOffset(length);

            int currentFocusOnShowLineNumber = showLineNumber + textEditorViewPort.getStartPosition();

            editorCaret.setCurrentFocusOnShowLineNumber(currentFocusOnShowLineNumber);

        }
    }

    private void addLineObj(int lineNumberOnShow, String line, String previousLineTerminator, int belongingGroupIndex)
            throws BadLocationException {

        // lineNumberOnShow += editorCaret.getStartPosition();

        LineObj lineObj = new LineObj();

        lineObj.setLine(line);

        lineObj.setLineNumber(-1);

        lineObj.setLineNumberOnShow(lineNumberOnShow);

        if (previousLineTerminator == null) {
            lineObj.setLineTerminator(newline);
        } else {
            lineObj.setLineTerminator(previousLineTerminator);
        }

        // 由于新行在文件中哪一行后面追加,与上一行相同,因此lineNumberOnShow - 1

        int showLineNumber = lineNumberOnShow - 1;

        int afterlineNumber = editorDataManager.getLineNumberOfAfterFile(showLineNumber, showLineNumber);

        TextEditAction newLineAction = new NewLinesAction(afterlineNumber, lineObj);

        newLineAction.setBelongingGroupIndex(belongingGroupIndex);

        ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

        if (listOfTextEditActions.size() > 0) {
            // 先在后面加一行,然后在前面加一行,后面一行数据行号需刷新
            listOfTextEditActions.lineNumberPlusOneAfterShowLineNumber(newLineAction);
        }

        listOfTextEditActions.addTextEditActions(newLineAction);

    }

    @Override
    public void mouseDragged(MouseEvent e) {

        requestFocus();

        editorCaret.setCurrentFocusReassignment(true);

        setSelectEditor();

        Point point = e.getPoint();

        int height = getHeight();

        int y2 = point.y;

        if (y2 <= 0) {
            // 向上拖拽翻页
            EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
            if (editorScrollBar.getValue() != 1) {

                editorScrollBar.setValue(editorScrollBar.getValue() - 1);

                /// editorCaret.setStartPosition(editorScrollBar.getValue());

            } else {
                // 当显示位置为数据的开始位置时,选中选择的内容
                if (textEditor2D.isSelectionElement()) {
                    select(textEditor2D.getSelectStartPos(), textEditor2D.getSelectEndPos());
                }

            }

        } else if (y2 >= height) {
            // 向下拖拽翻页
            EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

            editorScrollBar.setValue(editorScrollBar.getValue() + 1);
        } else {
            // 鼠标在当前页面拖拽
            if (textEditor2D.isSelectionElement()) {
                select(textEditor2D.getSelectStartPos(), textEditor2D.getSelectEndPos());
            }

        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!selectEditor.isShiftDown()) {

            int dot = getCaret().getDot();

            Element root = getDocument().getDefaultRootElement();

            int lineNumber = root.getElementIndex(dot);

            Element lineElement = root.getElement(lineNumber);

            int lineStartOffset = lineElement.getStartOffset();

            editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition());

            editorCaret.setMarkOffset(dot - lineStartOffset);

            editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

        }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // textEditorMain.updateItem();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getClickCount() == 2) { // 双击选中一行数据

            String selectedText = super.getSelectedText();

            if (selectedText != null) {

                int dot = getSelectionStart();

                Element root = getDocument().getDefaultRootElement();

                int lineNumber = root.getElementIndex(dot);

                editorCaret.setMarkLineNumber(lineNumber + textEditorViewPort.getStartPosition());

                editorCaret.setMarkOffset(0);

                // editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setSelectEditor();
                    }
                });
            }
        } else if (selectEditor.isShiftDown()) { // 当Shift键是按下时,选中文本

            requestFocus();

            editorCaret.setCurrentFocusReassignment(true);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    setSelectEditor();
                }
            });

        } else {
            clearSelection();
            requestFocus();
            editorCaret.setCurrentFocusReassignment(true);
            textEditorMain.updateItem();
        }

    }

    public void setSelectEditor() {
        int dot = getCaretPosition();
        Element root = getDocument().getDefaultRootElement();
        int lineNumber = root.getElementIndex(dot);
        Element lineElement = root.getElement(lineNumber);
        int lineStartOffset = lineElement.getStartOffset();
        int lineEndOffset = lineElement.getEndOffset();
        int endLineNumber = lineNumber + textEditorViewPort.getStartPosition();
        int selectionEnd = dot - lineStartOffset;
        int selectStartLineNumber = editorCaret.getMarkLineNumber();
        int selectionStart = editorCaret.getMarkOffset();
        if (selectStartLineNumber == endLineNumber) { // 选择起始行相同
            // 在选择行相等时,判断起始偏移量,当selectionStart > selectionEnd时,表示鼠标从后往前拖拽;反之从前往后拖拽
            int start = Math.min(selectionStart, selectionEnd);
            int end = Math.max(selectionStart, selectionEnd);
            selectEditor.setSelectionStart(start);
            selectEditor.setStartLineNumber(selectStartLineNumber);
            selectEditor.setSelectionEnd(end);
            selectEditor.setEndLineNumber(endLineNumber);
        } else if (selectStartLineNumber > endLineNumber) { // 选择的起始行号大于结尾行,表示鼠标从后往前拖拽
            selectEditor.setSelectionStart(selectionEnd);
            selectEditor.setStartLineNumber(endLineNumber);
            selectEditor.setSelectionEnd(selectionStart);
            selectEditor.setEndLineNumber(selectStartLineNumber);
            editorCaret.setStartPosition(textEditorViewPort.getStartPosition());
        } else {
            // 鼠标从前往后拖拽
            selectEditor.setSelectionStart(selectionStart);
            selectEditor.setStartLineNumber(selectStartLineNumber);
            selectEditor.setSelectionEnd(selectionEnd);
            selectEditor.setEndLineNumber(endLineNumber);
        }

        if (lineStartOffset != selectEditor.getSelectionStart() || lineEndOffset != selectEditor.getSelectionEnd()) {
            selectEditor.setThereAnyRemaining(true);
            if (selectEditor.getStartLineNumber() != -1) {
                StringBuffer remainingCharacters = new StringBuffer();
                String line = editorDataManager.getCurrentDataOfShowLine(selectEditor.getStartLineNumber()).getLine();
                line = editorDataManager.replaceAll(line);
                String subString = line.substring(0, selectEditor.getSelectionStart());
                remainingCharacters.append(subString);
                String line2 = editorDataManager.getCurrentDataOfShowLine(selectEditor.getEndLineNumber()).getLine();
                line2 = editorDataManager.replaceAll(line2);
                String subString2 = line2.substring(selectEditor.getSelectionEnd());
                remainingCharacters.append(subString2);
                selectEditor.setRemainingCharacters(remainingCharacters.toString());
            }

        }
        if (textEditor2D.isSelectionElement()) {
            DefaultCaret bidiCaret = (getCaret() instanceof DefaultCaret) ? (DefaultCaret) getCaret() : null;
            int newDot = dot == textEditor2D.getSelectStartPos() ? textEditor2D.getSelectEndPos()
                    : textEditor2D.getSelectStartPos();
            getCaret().setDot(newDot);
            bidiCaret.moveDot(dot, Position.Bias.Forward);
        }

        textEditorMain.updateItem();

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void caretUpdate(CaretEvent e) {

    }


    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (editorCaret.isCurrentFocusReassignment()) {

            int dot = getCaret().getDot();

            Element defaultRootElement = getDocument().getDefaultRootElement();

            int elementIndex = defaultRootElement.getElementIndex(dot);

            Element element = defaultRootElement.getElement(elementIndex);

            editorCaret.setCurrentFocusOnShowLineNumber(elementIndex + textEditorViewPort.getStartPosition());

            editorCaret.setCurrentFocusOnOffset(dot - element.getStartOffset());

            // editorCaret.setStartPosition(textEditorViewPort.getStartPosition());

            editorCaret.setCurrentFocusReassignment(false);

        }

        if (e.getWheelRotation() == 1) {
            EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

            editorScrollBar.setValue(editorScrollBar.getValue() + 1);

        } else if (e.getWheelRotation() == -1) {
            EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

            editorScrollBar.setValue(editorScrollBar.getValue() - 1);
        }

        //  System.out.println(horizontalScrollBarValue);
    }

    public int getLineCount() {

        Element map = getDocument().getDefaultRootElement();

        return map.getElementCount();
    }

    public int getLineNumber(int offset) {

        Element root = getDocument().getDefaultRootElement();

        return root.getElementIndex(offset);
    }

    public List<LineObj> readOriginalData() {
        return this.readOriginalData;
    }

    public SelectEditor getSelectEditor() {
        return this.selectEditor;
    }

    public TextEditorViewPort getTextEditorViewPort() {

        return this.textEditorViewPort;

    }

    public void cut() {
        if (isEditable() && isEnabled()) {
            ActionMap map = getActionMap();
            Action action = null;
            if (map != null) {
                action = map.get("cut");
            }
            action.actionPerformed(
                    new ActionEvent(this, ActionEvent.ACTION_PERFORMED, (String) action.getValue(Action.NAME),
                            EventQueue.getMostRecentEventTime(), getCurrentEventModifiers()));
            clearSelection();
//            BioMainFrame.getInstance().updateMenuItems();
        }
    }

    private int getCurrentEventModifiers() {
        int modifiers = 0;
        AWTEvent currentEvent = EventQueue.getCurrentEvent();
        if (currentEvent instanceof InputEvent) {
            modifiers = ((InputEvent) currentEvent).getModifiers();
        } else if (currentEvent instanceof ActionEvent) {
            modifiers = ((ActionEvent) currentEvent).getModifiers();
        }
        return modifiers;
    }

    public void copy() {
        String selectText = getSelectedText();
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(selectText), null);
        // clearSelection();
        // requestFocus();
        // select(0, 0);
    }

    /**
     * TextKeyEvent belongs to a built-in eGPS module (loader, panel, or helper).
     */
    class TextKeyEvent {

        public TextKeyEvent(EgpsTextPane egpsTextPane) {

            InputMap inputMap = egpsTextPane.getInputMap();

            Action replaceAllClipboard = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 1298845504160768813L;

                public void actionPerformed(ActionEvent e) {

                    new ReplaceAllJDialog(UnifiedAccessPoint.getInstanceFrame(), textEditorMain);

                }
            };

            KeyStroke replaceAllKey = KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK);

            inputMap.put(replaceAllKey, replaceAllClipboard);

            Action findClipboard = new AbstractAction() {
                private static final long serialVersionUID = -7278574134721119735L;

                public void actionPerformed(ActionEvent e) {
                    new FindAndReplaceDialog(UnifiedAccessPoint.getInstanceFrame(), textEditorMain);
                }
            };

            KeyStroke findKey = KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK);

            inputMap.put(findKey, findClipboard);

            AbstractAction goToLineClipboard = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 4149676152790360904L;

                @Override
                public void actionPerformed(ActionEvent e) {

                    new GoToLineJDialog(UnifiedAccessPoint.getInstanceFrame(), textEditorMain);

                }
            };

            KeyStroke goToLineKey = KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK);

            inputMap.put(goToLineKey, goToLineClipboard);

            Action undoClipboard = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 1298845504160768813L;

                public void actionPerformed(ActionEvent e) {

                    textEditorMain.undo();
//                    BioMainFrame.getInstance().updateMenuItems();
                }
            };

            KeyStroke undoKey = KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK);

            inputMap.put(undoKey, undoClipboard);

            Action redoClipboard = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 1298845504160768813L;

                public void actionPerformed(ActionEvent e) {

                    textEditorMain.redo();

//                    BioMainFrame.getInstance().updateMenuItems();
                }
            };

            KeyStroke redoKey = KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK);

            inputMap.put(redoKey, redoClipboard);

            Action cutClipboard = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 1298845504160768813L;

                public void actionPerformed(ActionEvent e) {
                    cut();
                }
            };

            KeyStroke cutKey = KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK);

            inputMap.put(cutKey, cutClipboard);

            Action copyClipboard = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 1298845504160768813L;

                public void actionPerformed(ActionEvent e) {
                    copy();
                }
            };

            KeyStroke copyKey = KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK);

            inputMap.put(copyKey, copyClipboard);

            Action selectAllClipboard = new AbstractAction() {

                /**
                 *
                 */
                private static final long serialVersionUID = 1298845504160768813L;

                public void actionPerformed(ActionEvent e) {
                    requestFocus();
//                    editorCaret.setCurrentFocusReassignment(true);
//                    editorCaret.setMarkLineNumber(textEditorViewPort.getStartPosition());
//                    editorCaret.setMarkOffset(0);
//                    setCaretPosition(getText().length());
//                    setSelectEditor();
                }
            };

            KeyStroke selectAlllKey = KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK);

            inputMap.put(selectAlllKey, selectAllClipboard);

        }
    }

    public void setEditorOffset(int editorOffset) {
        Element defaultRootElement = getDocument().getDefaultRootElement();

        int elementIndex = defaultRootElement.getElementIndex(editorOffset);

        Element element = defaultRootElement.getElement(elementIndex);

        editorCaret.setCurrentFocusOnShowLineNumber(elementIndex + textEditorViewPort.getStartPosition());

        editorCaret.setCurrentFocusOnOffset(editorOffset - element.getStartOffset());
        // this.editorOffset = editorOffset;
    }

    @Override
    public String getSelectedText() {

        int selectStartLineNumber = selectEditor.getStartLineNumber();

        int selectEndLineNumber = selectEditor.getEndLineNumber();

        if (selectStartLineNumber == -1) {

            return null;
        }

        StringBuffer selectText = new StringBuffer();

        LineObj currentDataOfShowLineNumber = editorDataManager.getCurrentDataOfShowLine(selectStartLineNumber);

        String line = currentDataOfShowLineNumber.getLine();
        line = editorDataManager.replaceAll(line);
        if (selectStartLineNumber == selectEndLineNumber) {

//			System.out
//					.println("start :" + selectEditor.getSelectionStart() + " end :" + selectEditor.getSelectionEnd());

            int length = line.length();
            int selectionStart = selectEditor.getSelectionStart();
            int selectionEnd = selectEditor.getSelectionEnd();

            if (selectionStart > length || selectionEnd > length) {
                return "";
            }

            String subString = line.substring(selectEditor.getSelectionStart(), selectEditor.getSelectionEnd());

            selectText.append(subString);

            return selectText.toString();
        }

        int selectionStart = selectEditor.getSelectionStart() + 1;

        if (line.length() < selectionStart) {
            selectText.append(currentDataOfShowLineNumber.getLineTerminator());
        } else {
            String subString = line.substring(selectEditor.getSelectionStart());

            selectText.append(subString).append(currentDataOfShowLineNumber.getLineTerminator());

        }

        List<LineObj> readOriginalData = editorDataManager.readOriginalData(selectStartLineNumber, selectEndLineNumber);

        Collections.sort(readOriginalData);

        for (int i = 1; i < readOriginalData.size(); i++) {

            LineObj lineObj = readOriginalData.get(i);

            selectText.append(lineObj.getLine()).append(lineObj.getLineTerminator());

        }

        String line2 = editorDataManager.getCurrentDataOfShowLine(selectEndLineNumber).getLine();
        line2 = editorDataManager.replaceAll(line2);
        String subString2 = line2.substring(0, selectEditor.getSelectionEnd());

        selectText.append(subString2);

        return selectText.toString();
    }

    public void clearSelection() {

        selectEditor.clearSelection();
    }

    public TextEditorMain getTextEditorMain() {
        return textEditorMain;
    }

    public TextEditorDataManager getEditorDataManager() {
        return editorDataManager;
    }
}
