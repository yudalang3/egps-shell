package egps2.builtin.modules.largetextedi.actions;

import java.awt.Cursor;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;

import egps2.builtin.modules.largetextedi.TextEditorMain;
import egps2.builtin.modules.largetextedi.gui.EditorScrollBar;
import egps2.builtin.modules.largetextedi.gui.EgpsTextPane;
import egps2.builtin.modules.largetextedi.gui.TextEditorDataManager;
import egps2.builtin.modules.largetextedi.model.EditorCaret;
import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.NewLinesAction;
import egps2.builtin.modules.largetextedi.model.OneBlockShownContent;
import egps2.builtin.modules.largetextedi.model.RevisedLineAction;
import egps2.builtin.modules.largetextedi.model.SelectEditor;
import egps2.builtin.modules.largetextedi.model.TextEditAction;


/**
 * FindElementTask belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class FindElementTask extends Thread {

    private TextEditorMain textEditorMain;

    private EgpsTextPane egpsTextPane;

    private FindAndReplaceDialog findAndReplaceDialog;

    private TextEditorDataManager editorDataManager;

    private int blockIndex = 0;

    private int startOffset;

    public FindElementTask(FindAndReplaceDialog findAndReplaceDialog, String queryContent, int currentLineNumber,
                           int startOffset) {

        this.findAndReplaceDialog = findAndReplaceDialog;

        this.queryContent = queryContent;
        this.currentShowLineNumber = currentLineNumber;
        this.startOffset = startOffset;

        this.textEditorMain = findAndReplaceDialog.getTextEditorMain();

        this.egpsTextPane = textEditorMain.getEgpsTextPane();

        this.editorDataManager = textEditorMain.getEditorDataManager();
    }

    private int currentShowLineNumber;

    private String queryContent = "";

    private LineObj findLineObj;

    int index = 0;

    public TextEditorMain getTextEditorMain() {
        return textEditorMain;
    }

    public void setTextEditorMain(TextEditorMain textEditorMain) {
        this.textEditorMain = textEditorMain;
    }

    public EgpsTextPane getEgpsTextPane() {
        return egpsTextPane;
    }

    public void setEgpsTextPane(EgpsTextPane egpsTextPane) {
        this.egpsTextPane = egpsTextPane;
    }

    public FindAndReplaceDialog getFindAndReplaceDialog() {
        return findAndReplaceDialog;
    }

    public void setFindAndReplaceDialog(FindAndReplaceDialog findAndReplaceDialog) {
        this.findAndReplaceDialog = findAndReplaceDialog;
    }

    public TextEditorDataManager getEditorDataManager() {
        return editorDataManager;
    }

    public void setEditorDataManager(TextEditorDataManager editorDataManager) {
        this.editorDataManager = editorDataManager;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getCurrentShowLineNumber() {
        return currentShowLineNumber;
    }

    public void setCurrentShowLineNumber(int currentShowLineNumber) {
        this.currentShowLineNumber = currentShowLineNumber;
    }

    public String getQueryContent() {
        return queryContent;
    }

    public void setQueryContent(String queryContent) {
        this.queryContent = queryContent;
    }

    public LineObj getFindLineObj() {
        return findLineObj;
    }

    public void setFindLineObj(LineObj findLineObj) {
        this.findLineObj = findLineObj;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void run() {

        try {
            findAndReplaceDialog.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            findAndReplaceDialog.getNotFound().setVisible(false);

            EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();

            int maximum = editorScrollBar.getMaximum();

            int trueNumOfStoredBlocks = editorDataManager.getTrueNumOfStoredBlocks();

            while (blockIndex < trueNumOfStoredBlocks) {

                OneBlockShownContent[] listOfStoredLines = editorDataManager.getListOfStoredLines();

                OneBlockShownContent oneStoredBlock = listOfStoredLines[blockIndex];

                int blockLastLineNumber = oneStoredBlock.getLastLineNumber();

                int blockLastShowLineNumber = editorDataManager.getShownLineNumber(blockLastLineNumber);

                if (blockLastShowLineNumber > currentShowLineNumber) {

                    List<LineObj> lineObjs = oneStoredBlock.getLineObj();

                    ExitSituation exitSituation = findLineObj(lineObjs, maximum);

                    if (exitSituation == ExitSituation.FORCIBLYEXIT || exitSituation == ExitSituation.MATCHEDEXIT) {

                        break;
                    }

                    if (maximum == currentShowLineNumber) {
                        if (findAndReplaceDialog.getWrapSearch().isSelected()) {
                            index++;
                            currentShowLineNumber = 1;
                            blockIndex = 0;
                            continue;
                        } else {
                            break;
                        }
                    }
                }

                if (blockIndex + 1 == trueNumOfStoredBlocks) {
                    // 当blockIndex + 1 == trueNumOfStoredBlocks时,表示最后一个Block;
                    // 由于设计之初,在最后一个Block后面没有文件内容了,因此不需要去文件中读取内容,但是可以有Actions
                    continue;
                }
                OneBlockShownContent nextStoredBlock = listOfStoredLines[blockIndex + 1];

                int nextStoredBlock_FirstLineNumber = nextStoredBlock.getFirstLineNumber();

                int nextStoredBlock_ShowFirstLineNum = editorDataManager
                        .getShownLineNumber(nextStoredBlock_FirstLineNumber);

                // blockLastShowLineNumber + 1 == nextStoredBlock_ShowFirstLineNum 表示进入下一个Block
                if (nextStoredBlock == null || blockLastShowLineNumber + 1 == nextStoredBlock_ShowFirstLineNum) {
                    blockIndex++;
                    continue;
                }

                if (nextStoredBlock_ShowFirstLineNum > currentShowLineNumber) {

                    long lastOffset = oneStoredBlock.getBlock_offset_End();

                    List<LineObj> partLines = editorDataManager.readOriginalLines(editorDataManager.getFile(),
                            lastOffset, blockLastLineNumber, blockLastLineNumber + 1, nextStoredBlock_FirstLineNumber);

                    Collections.sort(partLines);

                    ExitSituation exitSituation = findLineObj(partLines, maximum);

                    if (exitSituation == ExitSituation.FORCIBLYEXIT || exitSituation == ExitSituation.MATCHEDEXIT) {

                        break;
                    }

                }
                blockIndex++;

            }

            findAndReplaceDialog.setCursor(Cursor.getDefaultCursor());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isMatcher(String regex, CharSequence input, int currentLineNumber, int startOffset) {

        JCheckBox wholeWord = findAndReplaceDialog.getWholeWord();

        String patternStrin = wholeWord.isSelected() && wholeWord.isEnabled() ? "\\b(" + regex + ")\\b" : regex;

        Pattern pattern = Pattern.compile(patternStrin);

        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {

            int indexOf = startOffset + matcher.start();

            SelectEditor selectEditor = egpsTextPane.getSelectEditor();

            selectEditor.setSelectionStart(indexOf);

            selectEditor.setSelectionEnd(startOffset + matcher.end());

            selectEditor.setStartLineNumber(currentLineNumber);

            selectEditor.setEndLineNumber(currentLineNumber);

            return true;
        }

        return false;
    }

    private boolean setSelectElement(String queryContent, String line, int currentLineNumber, int startOffset) {

        if (line.contains(queryContent)) {

            int length = queryContent.length();

            int indexOf = line.indexOf(queryContent) + startOffset;

            SelectEditor selectEditor = egpsTextPane.getSelectEditor();

            EditorCaret editorCaret = egpsTextPane.getEditorCaret();

            selectEditor.setSelectionStart(indexOf);

            selectEditor.setSelectionEnd(indexOf + length);

            selectEditor.setStartLineNumber(currentLineNumber);

            selectEditor.setEndLineNumber(currentLineNumber);

            editorCaret.setStartPosition(currentLineNumber);

            return true;
        }
        return false;

    }

    private ExitSituation findLineObj(List<LineObj> lineObjs, int maximum) throws IOException {

        for (int i = 0; i < lineObjs.size(); i++) {

            if (findAndReplaceDialog.isStop()) {

                findAndReplaceDialog.setStop(false);

                return ExitSituation.FORCIBLYEXIT;
            }
            if (index == maximum) {// End of a loop, no query data found
                findAndReplaceDialog.getNotFound().setVisible(true);

                findAndReplaceDialog.getReplaceOrFindButton().setEnabled(false);

                findAndReplaceDialog.getReplaceButton().setEnabled(false);

                return ExitSituation.FORCIBLYEXIT;
            }

            //判断Actions中是否包含于当前行号相同的元素
            LineObj lineObj = findActionsLineObj(currentShowLineNumber);

            if (lineObj != null) {

                String line = editorDataManager.replaceAll(lineObj.getLine()).substring(startOffset);

                JCheckBox caseSensitive = findAndReplaceDialog.getCaseSensitive();

                line = caseSensitive.isSelected() ? line : line.toLowerCase();

                queryContent = caseSensitive.isSelected() ? queryContent : queryContent.toLowerCase();

                boolean flag = (findAndReplaceDialog.getRegularExpressions().isSelected()
                        || findAndReplaceDialog.getWholeWord().isSelected())
                        ? isMatcher(queryContent, line, currentShowLineNumber, startOffset)
                        : setSelectElement(queryContent, line, currentShowLineNumber, startOffset);

                if (flag) {
                    this.findLineObj = lineObj;
                    findAndReplaceDialog.getReplaceOrFindButton().setEnabled(true);

                    findAndReplaceDialog.getReplaceButton().setEnabled(true);
                    return ExitSituation.MATCHEDEXIT;
                }
                index++;
                currentShowLineNumber++;
                startOffset = 0;
                continue;

            }
            lineObj = lineObjs.get(i);

            int lineNumber = lineObj.getLineNumber();

            int showLineNumber = editorDataManager.getShownLineNumber(lineNumber);

            if (showLineNumber < 0 || showLineNumber < currentShowLineNumber) {

                continue;
            }
            String s = editorDataManager.replaceAll(lineObj.getLine());
            if (s.length() < startOffset) {
                continue;
            }
            String line = s.substring(startOffset);

            JCheckBox caseSensitive = findAndReplaceDialog.getCaseSensitive();

            line = caseSensitive.isSelected() ? line : line.toLowerCase();

            queryContent = caseSensitive.isSelected() ? queryContent : queryContent.toLowerCase();

            boolean flag = (findAndReplaceDialog.getRegularExpressions().isSelected()
                    || findAndReplaceDialog.getWholeWord().isSelected())
                    ? isMatcher(queryContent, line, currentShowLineNumber, startOffset)
                    : setSelectElement(queryContent, line, currentShowLineNumber, startOffset);
            if (flag) {
                this.findLineObj = lineObj;
                findAndReplaceDialog.getReplaceOrFindButton().setEnabled(true);

                findAndReplaceDialog.getReplaceButton().setEnabled(true);
                return ExitSituation.MATCHEDEXIT;
            }

            index++;
            currentShowLineNumber++;
            startOffset = 0;

        }
        return ExitSituation.NOTMATCHEDEXIT;
    }

    private LineObj findActionsLineObj(int currentShowLineNumber) throws IOException {

        ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

        int textEditActionSize = listOfTextEditActions.size();

        for (int k = textEditActionSize - 1; k >= 0; k--) {

            TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(k);

            int editAction = editActionObj.getEditAction();

            if (editAction == TextEditAction.REVISED) {
                RevisedLineAction linesAction = (RevisedLineAction) editActionObj;

                LineObj revisedLineObj = linesAction.getRevisedLineObj();

                int showLineNumber = revisedLineObj.getLineNumberOnShow();

                if (currentShowLineNumber == showLineNumber) {

                    LineObj newLineObj = linesAction.getRevisedLineObj();

                    return newLineObj;
                }

            } else if (editAction == TextEditAction.NEW_LINE) {

                NewLinesAction newLinesAction = (NewLinesAction) editActionObj;

                int showLineNumber = newLinesAction.getNewLineObj().getLineNumberOnShow();

                if (currentShowLineNumber == showLineNumber) {

                    LineObj newLineObj = newLinesAction.getNewLineObj();

                    return newLineObj;
                }

            }
        }
        return null;
    }

}
