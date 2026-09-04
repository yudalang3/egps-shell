package egps2.builtin.modules.largetextedi.gui;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import egps2.builtin.modules.largetextedi.TextEditorMain;
import egps2.builtin.modules.largetextedi.actions.ListOfTextEditActions;
import egps2.builtin.modules.largetextedi.io.EditorBufferedReader;
import egps2.builtin.modules.largetextedi.model.DeleteLineAction;
import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.LineTerminator;
import egps2.builtin.modules.largetextedi.model.NewLinesAction;
import egps2.builtin.modules.largetextedi.model.OneBlockShownContent;
import egps2.builtin.modules.largetextedi.model.ReplaceAllAction;
import egps2.builtin.modules.largetextedi.model.RevisedLineAction;
import egps2.builtin.modules.largetextedi.model.TextEditAction;
import egps2.frame.gui.EGPSSwingUtil;
import egps2.modulei.RunningTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * TextEditorDataManager belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class TextEditorDataManager {

    public static final int MAX_NUM_STORED_BLOCKS = 101;
    private static final Logger log = LoggerFactory.getLogger(TextEditorDataManager.class);

    private String newline = System.getProperty("line.separator");

    private File dataFile;

    private OneBlockShownContent[] listOfStoredLines = new OneBlockShownContent[MAX_NUM_STORED_BLOCKS];

    private int trueNumOfStoredBlocks = 0;

    private ListOfTextEditActions listOfTextEditActions = new ListOfTextEditActions();

    private List<Long> offsetOfEveryTenRows = new ArrayList<Long>(1000);

    private boolean isReadLastBlock = false;

    private TextEditorMain textEditorMain;

    public TextEditorDataManager(TextEditorMain textEditorMain) {

        this.textEditorMain = textEditorMain;

    }

    /**
     * If the line was deleted, return -1;
     *
     * @param lineNumberInFile
     * @return
     */
    public int getShownLineNumber(int lineNumberInFile) {

        int size = listOfTextEditActions.size();

        int shownNum = lineNumberInFile;

        for (int i = 0; i < size; i++) {

            TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(i);

            int editAction = editActionObj.getEditAction();

            if (editAction == TextEditAction.NEW_LINE) {

                NewLinesAction editedLineObj = (NewLinesAction) editActionObj;

                int lineNumberOnShow = editedLineObj.getNewLineObj().getLineNumberOnShow();

                int afterlineNumber = editedLineObj.getAfterlineNumber();

                if (afterlineNumber < lineNumberInFile && lineNumberOnShow > 0) {

                    shownNum++;
                }

            } else if (editAction == TextEditAction.DELETE_LINE) {

                DeleteLineAction editedLineObj = (DeleteLineAction) editActionObj;

                int lineNumber = editedLineObj.getLineObj().getLineNumber();

                if (lineNumber < 0) {

                    continue;
                }
                if (lineNumber == lineNumberInFile) {
                    shownNum = -1;
                    break;
                }

                if (lineNumber < lineNumberInFile) {
                    shownNum--;
                }

            }

        }

        return shownNum;
    }

    public boolean isActionsLineNumberContainLineNumberInFile(int lineNumberInFile) {

        int size = listOfTextEditActions.size();

        for (int i = 0; i < size; i++) {

            TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(i);

            int editAction = editActionObj.getEditAction();

            if (editAction == TextEditAction.REVISED) {// 修改文件中的行时,该行数据不进行显示

                RevisedLineAction linesAction = (RevisedLineAction) editActionObj;

                LineObj revisedLineObj = linesAction.getRevisedLineObj();

                int lineNumber = revisedLineObj.getLineNumber();

                if (lineNumber == lineNumberInFile) {
                    return true;
                }

            }

        }
        return false;

    }

    /**
     * startLineNumber: 1-based, inclusive endLineNumber: 1-based, exclusive
     *
     * @param startOnShowLineNumber
     * @param endOnShowLineNumber
     * @return
     */
    protected List<LineObj> readOriginalData(int startOnShowLineNumber, int endOnShowLineNumber) {

        int numOfStoredBlocks = trueNumOfStoredBlocks;

        int textEditActionSize = listOfTextEditActions.size();

        List<LineObj> linesObj = new ArrayList<LineObj>(endOnShowLineNumber - startOnShowLineNumber);

        for (int k = textEditActionSize - 1; k >= 0; k--) {

            TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(k);

            int editAction = editActionObj.getEditAction();

            if (editAction == TextEditAction.NEW_LINE) {
                NewLinesAction newLinesAction = (NewLinesAction) editActionObj;

                int showLineNumber = newLinesAction.getNewLineObj().getLineNumberOnShow();

                if (showLineNumber < 0) {
                    continue;
                }
                // 排除新增行被编辑
                boolean isExit = false;

                for (int i = 0; i < linesObj.size(); i++) {

                    LineObj lineObj = linesObj.get(i);

                    if (showLineNumber == lineObj.getLineNumberOnShow()) {

                        isExit = true;
                        break;
                    }
                }

                if (isExit) {

                    continue;
                }

                if (showLineNumber >= startOnShowLineNumber && showLineNumber < endOnShowLineNumber) {

                    linesObj.add(newLinesAction.getNewLineObj());
                }

            } else if (editAction == TextEditAction.REVISED) {
                RevisedLineAction linesAction = (RevisedLineAction) editActionObj;

                LineObj revisedLineObj = linesAction.getRevisedLineObj();

                int showLineNumber = revisedLineObj.getLineNumberOnShow();

                if (showLineNumber >= startOnShowLineNumber && showLineNumber < endOnShowLineNumber) {

                    linesObj.add(revisedLineObj);
                }
            }
        }

        int startLineNumberInFile = getLineNumberOfFile(startOnShowLineNumber, startOnShowLineNumber);

        int endLineNumberInFile = getLineNumberOfFile(endOnShowLineNumber, endOnShowLineNumber);

        for (int i = 0; i < numOfStoredBlocks; i++) {

            OneBlockShownContent oneStoredBlock = listOfStoredLines[i];

            int blockFirstLineNumber = oneStoredBlock.getFirstLineNumber();

            int blockLastLineNumber = oneStoredBlock.getLastLineNumber();

            if (blockFirstLineNumber <= startLineNumberInFile && blockLastLineNumber >= endLineNumberInFile) {

                List<LineObj> lines = oneStoredBlock.getLineObj();

                for (int j = 0; j < lines.size(); j++) {

                    LineObj lineObj = lines.get(j);

                    int lineNumber = lineObj.getLineNumber();

                    int showLineNumber = getShownLineNumber(lineNumber);

                    if (showLineNumber < 0) {
                        continue;
                    }

                    boolean revisedLine = isActionsLineNumberContainLineNumberInFile(lineNumber);

                    if (revisedLine) {
                        continue;
                    }

                    if (lineNumber >= startLineNumberInFile && lineNumber < endLineNumberInFile) {

                        lineObj.setLineNumberOnShow(showLineNumber);

                        linesObj.add(lineObj);

                    }

                }
                return linesObj;

            } else if (blockLastLineNumber >= startLineNumberInFile && blockLastLineNumber < endLineNumberInFile) {

                List<LineObj> lines = oneStoredBlock.getLineObj();

                long lastOffset = oneStoredBlock.getBlock_offset_End();

                for (int j = 0; j < lines.size(); j++) {

                    LineObj lineObj = lines.get(j);

                    int lineNumber = lineObj.getLineNumber();

                    int showLineNumber = getShownLineNumber(lineNumber);
                    if (showLineNumber < 0) {
                        continue;
                    }
                    boolean revisedLine = isActionsLineNumberContainLineNumberInFile(lineNumber);

                    if (revisedLine) {
                        continue;
                    }

                    if (lineNumber >= startLineNumberInFile) {

                        lineObj.setLineNumberOnShow(showLineNumber);
                        linesObj.add(lineObj);
                    }

                }

                List<LineObj> partLines = readOriginalLines(dataFile, lastOffset, blockLastLineNumber,
                        blockLastLineNumber + 1, endLineNumberInFile);

                if (partLines != null) {
                    linesObj.addAll(partLines);
                }

                return linesObj;

            } else if (blockLastLineNumber < startLineNumberInFile) {

                if (i + 1 >= trueNumOfStoredBlocks) {

                    return linesObj;
                }

                OneBlockShownContent nextStoredBlock = listOfStoredLines[i + 1];

                if (nextStoredBlock == null) {
                    continue;
                }

                List<LineObj> lines = nextStoredBlock.getLineObj();

                long nextStoredBlock_Offset_End = nextStoredBlock.getBlock_offset_End();

                int nextStoredBlock_LastLineNum = nextStoredBlock.getLastLineNumber();

                if (nextStoredBlock_LastLineNum >= startLineNumberInFile) {

                    int nextStoredBlock_FirstLineNum = nextStoredBlock.getFirstLineNumber();

                    if (nextStoredBlock_FirstLineNum > startLineNumberInFile) {

                        if (nextStoredBlock_FirstLineNum >= endLineNumberInFile) {

                            long lastOffset = oneStoredBlock.getBlock_offset_End();

                            List<LineObj> partLines = readOriginalLines(dataFile, lastOffset, blockLastLineNumber,
                                    startLineNumberInFile, endLineNumberInFile);
                            if (partLines != null) {
                                linesObj.addAll(partLines);
                            }

                            return linesObj;
                        }

                        long lastOffset = oneStoredBlock.getBlock_offset_End();

                        List<LineObj> partLines = readOriginalLines(dataFile, lastOffset, blockLastLineNumber,
                                startLineNumberInFile, nextStoredBlock_FirstLineNum);

                        if (partLines != null) {
                            linesObj.addAll(partLines);
                        }

                    }

                    for (int j = 0; j < lines.size(); j++) {
                        LineObj lineObj = lines.get(j);
                        int lineNumber = lineObj.getLineNumber();
                        int showLineNumber = getShownLineNumber(lineNumber);
                        if (showLineNumber < 0) {
                            continue;
                        }
                        boolean revisedLine = isActionsLineNumberContainLineNumberInFile(lineNumber);

                        if (revisedLine) {
                            continue;
                        }

                        if (lineNumber >= startLineNumberInFile && lineNumber < endLineNumberInFile) {

                            lineObj.setLineNumberOnShow(showLineNumber);

                            linesObj.add(lineObj);
                        }
                    }

                    if (nextStoredBlock_LastLineNum < endLineNumberInFile) {
                        List<LineObj> partLines = readOriginalLines(dataFile, nextStoredBlock_Offset_End,
                                nextStoredBlock_LastLineNum, nextStoredBlock_LastLineNum + 1, endLineNumberInFile);
                        if (partLines != null) {
                            linesObj.addAll(partLines);
                        }
                    }

                    return linesObj;
                }

            }
        }

        return linesObj;

    }

    /**
     * startLineNumber: 1-based, inclusive endLineNumber: 1-based, exclusive
     *
     * @param startLineNumber
     * @param endLineNumber
     * @return
     */
    public List<LineObj> readOriginalLines(File dataFile, long offset, int numOfLinesSkipped, int startLineNumber,
                                           int endLineNumber) {

        if (numOfLinesSkipped >= startLineNumber) {
            // throw new IllegalArgumentException();
            return null;
        }
        if (startLineNumber >= endLineNumber) {
            return null;
            // throw new IllegalArgumentException();
        }
        List<LineObj> listOfLinesObj = new ArrayList<LineObj>(endLineNumber - startLineNumber);

        try {
            FileInputStream fstream = new FileInputStream(dataFile);
            DataInputStream in = new DataInputStream(fstream);
            EditorBufferedReader dataFileReader = new EditorBufferedReader(new InputStreamReader(in));

            long at = offset;
            while (at > 0) {
                long amt = fstream.skip(at);
                if (amt == -1) {
                    throw new RuntimeException(dataFile + ": unexpected EOF");
                }
                at -= amt;
            }

            int lineNumber = numOfLinesSkipped;

            String line;

            while ((line = dataFileReader.readLine()) != null) {

                // The first line after skip, it will always be false.
                if (dataFileReader.isTwoBytesLineBreak()) {
                    offset++;

                    int sizeOfLineObjs = listOfLinesObj.size();
                    if (sizeOfLineObjs > 0) {
                        LineObj lastLine = listOfLinesObj.get(sizeOfLineObjs - 1);
                        lastLine.setLineTerminator(LineTerminator.carriageReturnLineFeed);
                    }
                }

                String lineTerminator = dataFileReader.getLineTerminator();

                lineNumber++;

                int shownLineNumber = getShownLineNumber(lineNumber);
                if (shownLineNumber < 0) {
                    continue;
                }
                boolean revisedLine = isActionsLineNumberContainLineNumberInFile(lineNumber);

                if (revisedLine) {
                    continue;
                }

                if (startLineNumber <= lineNumber && lineNumber < endLineNumber) {
                    LineObj oneLineObj = new LineObj();
                    oneLineObj.setLine(line);
                    oneLineObj.setLineNumber(lineNumber);
                    oneLineObj.setLineNumberOnShow(shownLineNumber);
                    oneLineObj.setHeadOffset(offset);
                    oneLineObj.setLineTerminator(lineTerminator);

                    listOfLinesObj.add(oneLineObj);
                } else {
                    if (lineNumber >= endLineNumber)
                        break;
                }

                offset += (line.length() + 1);
            }

            dataFileReader.close();
            fstream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return listOfLinesObj;

    }

    public void clearEditActions() {
        listOfTextEditActions.clearEditActions();
    }

    public ListOfTextEditActions getListOfTextEditActions() {

        return listOfTextEditActions;
    }

    public int getMaxLength() {
        if (trueNumOfStoredBlocks == 0) {
            return 0;
        }
        int lastLineNumber = listOfStoredLines[trueNumOfStoredBlocks - 1].getLastLineNumber();

        int textEditActionSize = listOfTextEditActions.size();

        for (int i = 0; i < textEditActionSize; i++) {

            TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(i);

            int editAction = editActionObj.getEditAction();

            if (editAction == TextEditAction.NEW_LINE) {

                lastLineNumber++;
            } else if (editAction == TextEditAction.DELETE_LINE) {

                lastLineNumber--;
            }
        }
        return lastLineNumber;
    }

    public List<Long> getOffsetOfEveryTenRows() {
        return offsetOfEveryTenRows;
    }

    public void setFile(File dataFile) {
        this.dataFile = dataFile;

        ReadFile task = new ReadFile();

        textEditorMain.registerRunningTask(task);

    }

    public File getFile() {
        return dataFile;
    }

    private LineObj readFirstBlock(int lineNumber, String firstLine, EditorBufferedReader dataFileReader,
                                   long lineOffset, OneBlockShownContent[] listOfStoredLines, int blockIndex) throws IOException {

        int lineIndex = 0;

        OneBlockShownContent oneBlockShownContent = new OneBlockShownContent();

        oneBlockShownContent.setBlock_offset_Start(lineOffset);

        oneBlockShownContent
                .addLine(new LineObj(firstLine, lineNumber, lineOffset, dataFileReader.getLineTerminator()));

        lineOffset += firstLine.length() + 1;

        if (dataFileReader.isTwoBytesLineBreak()) {
            lineOffset++;
        }

        String line;

        while ((line = dataFileReader.readLine()) != null) {

            lineNumber++;
            lineIndex++;

            if (lineIndex >= 100)
                break;

            oneBlockShownContent.addLine(new LineObj(line, lineNumber, lineOffset, dataFileReader.getLineTerminator()));

            lineOffset += (line.length() + 1);

            if (dataFileReader.isTwoBytesLineBreak()) {
                lineOffset++;
            }

            if (lineNumber % 10 == 0) {
                offsetOfEveryTenRows.add(lineOffset);
            }

            oneBlockShownContent.setTrueNumberOfOneBlocks(lineIndex + 1);

            oneBlockShownContent.setBlock_offset_End(lineOffset);
        }

        if (line != null) {

            lineOffset += (line.length() + 1);

            if (dataFileReader.isTwoBytesLineBreak()) {
                lineOffset++;
            }

        }

        listOfStoredLines[blockIndex] = oneBlockShownContent;
        trueNumOfStoredBlocks++;

        LineObj lineObj = new LineObj();

        lineObj.setLine(line);
        lineObj.setLineNumber(lineNumber);
        lineObj.setHeadOffset(lineOffset);

        return lineObj;
    }

    private LineObj readOneBlock(int lineNumber, String firstLine, EditorBufferedReader dataFileReader, long lineOffset,
                                 OneBlockShownContent[] listOfStoredLines, int blockIndex, long fileSize) throws IOException {

        OneBlockShownContent oneBlockShownContent = new OneBlockShownContent();

        oneBlockShownContent.setBlock_offset_Start(lineOffset);

        LimitQueue<LineObj> lineObjLimitQueue = new LimitQueue<LineObj>(100);

        lineObjLimitQueue.offer(new LineObj(firstLine, lineNumber, lineOffset, dataFileReader.getLineTerminator()));

        lineOffset += firstLine.length() + 1;

        if (dataFileReader.isTwoBytesLineBreak()) {
            lineOffset++;
        }

        String line;

        int trueNumberOfOneBlocks = 1;

        while ((line = dataFileReader.readLine()) != null) {

            lineNumber++;

            lineObjLimitQueue.offer(new LineObj(line, lineNumber, lineOffset, dataFileReader.getLineTerminator()));

            if (trueNumberOfOneBlocks < 100) {
                trueNumberOfOneBlocks++;
            }

            lineOffset += (line.length() + 1);

            if (dataFileReader.isTwoBytesLineBreak()) {
                lineOffset++;
            }

            if (lineNumber % 10 == 0) {
                offsetOfEveryTenRows.add(lineOffset);
            }

            oneBlockShownContent.setTrueNumberOfOneBlocks(trueNumberOfOneBlocks);

            if ((double) lineOffset / (double) fileSize >= (double) blockIndex / (double) MAX_NUM_STORED_BLOCKS) {
                break;
            }

        }

        for (int i = 0; i < lineObjLimitQueue.size(); i++) {
            oneBlockShownContent.addLine(lineObjLimitQueue.get(i));
        }

        oneBlockShownContent.setBlock_offset_Start(oneBlockShownContent.getLineObj().get(0).getHeadOffset());

        oneBlockShownContent.setBlock_offset_End(lineOffset);

        if (blockIndex < 100) {
            listOfStoredLines[blockIndex] = oneBlockShownContent;
            trueNumOfStoredBlocks++;
        }

        LineObj lineObj = new LineObj();

        lineObj.setLine(line);
        lineObj.setLineNumber(lineNumber);
        lineObj.setHeadOffset(lineOffset);

        return lineObj;
    }

    private LineObj readLastBlock(int lineNumber, String firstLine, EditorBufferedReader dataFileReader,
                                  long lineOffset, OneBlockShownContent[] listOfStoredLines, int blockIndex) throws IOException {

        isReadLastBlock = true;

        OneBlockShownContent oneBlockShownContent = new OneBlockShownContent();

        oneBlockShownContent.setBlock_offset_Start(lineOffset);

        LimitQueue<LineObj> lineObjLimitQueue = new LimitQueue<LineObj>(100);

        lineObjLimitQueue.offer(new LineObj(firstLine, lineNumber, lineOffset, dataFileReader.getLineTerminator()));

        lineOffset += firstLine.length() + 1;

        if (dataFileReader.isTwoBytesLineBreak()) {
            lineOffset++;
        }

        String line;

        int trueNumberOfOneBlocks = 1;

        while ((line = dataFileReader.readLine()) != null) {

            lineNumber++;

            lineObjLimitQueue.offer(new LineObj(line, lineNumber, lineOffset, dataFileReader.getLineTerminator()));

            if (trueNumberOfOneBlocks < 100) {
                trueNumberOfOneBlocks++;
            }

            lineOffset += (line.length() + 1);

            if (dataFileReader.isTwoBytesLineBreak()) {
                lineOffset++;
            }

            if (lineNumber % 10 == 0) {
                offsetOfEveryTenRows.add(lineOffset);
            }

            oneBlockShownContent.setTrueNumberOfOneBlocks(trueNumberOfOneBlocks);
        }

        for (int i = 0; i < lineObjLimitQueue.size(); i++) {
            oneBlockShownContent.addLine(lineObjLimitQueue.get(i));
        }

        oneBlockShownContent.setBlock_offset_Start(oneBlockShownContent.getLineObj().get(0).getHeadOffset());

        lineOffset = dataFileReader.getLineTerminator() == null
                ? (dataFileReader.isTwoBytesLineBreak() ? lineOffset - 2 : lineOffset - 1)
                : lineOffset;

        oneBlockShownContent.setBlock_offset_End(lineOffset);

        listOfStoredLines[blockIndex] = oneBlockShownContent;
        trueNumOfStoredBlocks++;

        LineObj lineObj = new LineObj();

        lineObj.setLine(line);
        lineObj.setLineNumber(lineNumber);
        lineObj.setHeadOffset(lineOffset);

        return lineObj;
    }

    public OneBlockShownContent[] getListOfStoredLines() {
        return listOfStoredLines;
    }

    public int getTrueNumOfStoredBlocks() {
        return trueNumOfStoredBlocks;
    }

    /**
     * ReadFile belongs to a built-in eGPS module (loader, panel, or helper).
     */
    class ReadFile implements RunningTask {

        private FileInputStream stream;
        private EditorBufferedReader dataFileReader;
        private long lineOffset = 0;
        private int lineNumber = 0;
        private int blockIndex = 0;

        private void updateUI() {
            SwingUtilities.invokeLater(() -> {
                textEditorMain.getJScrollPane().getRowHeader().updateUI();
            });
        }

        @Override
        public int processNext() throws Exception {

            long fileSize = dataFile.length();

            String temp;
            LineObj lineObj;

            while ((temp = dataFileReader.readLine()) != null) {

                lineNumber++;

                if (blockIndex == 0) {
                    lineObj = readFirstBlock(lineNumber, temp, dataFileReader, lineOffset, listOfStoredLines,
                            blockIndex);
                    //textEditorMain.getEditorScrollBar().updateUI();
                    //    textEditorMain.getEditorScrollBar().refScrollBarData();
                    updateUI();
                } else if (blockIndex < 100) {
                    lineObj = readOneBlock(lineNumber, temp, dataFileReader, lineOffset, listOfStoredLines, blockIndex,
                            fileSize);
                    //textEdit8orMain.getEditorScrollBar().updateUI();
                    //  textEditorMain.getEditorScrollBar().refScrollBarData();
                    updateUI();
                } else {
                    lineObj = readLastBlock(lineNumber, temp, dataFileReader, lineOffset, listOfStoredLines,
                            blockIndex);
                    //textEditorMain.getEditorScrollBar().updateUI();
                    // textEditorMain.getEditorScrollBar().refScrollBarData();
                    updateUI();
                }

                blockIndex++;

                lineNumber = lineObj.getLineNumber();

                lineOffset = lineObj.getHeadOffset();
                if (!isReadLastBlock) {
                    lineOffset = dataFileReader.getLineTerminator() == null
                            ? (dataFileReader.isTwoBytesLineBreak() ? lineOffset - 2 : lineOffset - 1)
                            : lineOffset;
                }
                return blockIndex;

            }

            if (trueNumOfStoredBlocks < 1) {
                trueNumOfStoredBlocks = 1;

                OneBlockShownContent oneBlockShownContent = new OneBlockShownContent();

                oneBlockShownContent.addLine(new LineObj("", 1, 0, newline));

                listOfStoredLines[0] = oneBlockShownContent;
            }

            return PROGRESS_FINSHED;
        }

        @Override
        public void actionsBeforeStart() throws Exception {
            stream = new FileInputStream(dataFile);
            DataInputStream in = new DataInputStream(stream);
            dataFileReader = new EditorBufferedReader(new InputStreamReader(in));
        }

        @Override
        public void actionsAfterFinished() throws Exception {
            stream.close();
        }

		@Override
		public boolean isTimeCanEstimate() {
			return true;
		}
    }

    public int getLineNumberOfFile(int lineNumberInFile, int showNumber) {

        int shownLineNumber = getNextShowLineNumber(lineNumberInFile);

        if (showNumber > shownLineNumber) {

            return getLineNumberOfFile(lineNumberInFile + 1, showNumber);

        } else if (showNumber < shownLineNumber) {

            // int fileNumber = getShownLineNumber(lineNumberInFile - 1);
            int previousShownLineNumber = getLineNumberOfFile(lineNumberInFile - 1);

            if (showNumber > previousShownLineNumber) {

                return lineNumberInFile;
            } else if (showNumber == previousShownLineNumber) {

                return getLineNumberInFile(lineNumberInFile - 1);
            }

            return getLineNumberOfFile(lineNumberInFile - 1, showNumber);
        }

        return getNextLineNumber(lineNumberInFile);

    }

    public int getLineNumberOfFile(int lineNumberInFile) {

        int shownLineNumber = getShownLineNumber(lineNumberInFile);

        if (shownLineNumber < 0) {

            return getLineNumberOfFile(--lineNumberInFile);
        }

        return shownLineNumber;
    }

    public int getNextShowLineNumber(int lineNumberInFile) {

        int shownLineNumber = getShownLineNumber(lineNumberInFile);

        if (shownLineNumber < 0) {

            return getNextShowLineNumber(++lineNumberInFile);
        }

        return shownLineNumber;
    }

    public int getNextLineNumber(int lineNumberInFile) {

        int fileNumber = getShownLineNumber(lineNumberInFile);

        if (fileNumber < 0) {

            return getNextLineNumber(++lineNumberInFile);
        }

        return lineNumberInFile;
    }

    public int getLineNumberInFile(int lineNumberInFile) {

        int shownLineNumber = getShownLineNumber(lineNumberInFile);

        if (shownLineNumber < 0) {

            return getLineNumberInFile(--lineNumberInFile);
        }

        return lineNumberInFile;
    }

    public int getLineNumberOfAfterFile(int lineNumberInFile, int showNumber) {

        int shownLineNumber = getNextShowLineNumber(lineNumberInFile);

        if (showNumber > shownLineNumber) {

            int nextFileShowNumber = getNextShowLineNumber(lineNumberInFile + 1);

            if (showNumber < nextFileShowNumber) {

                return lineNumberInFile;
            }

            return getLineNumberOfAfterFile(lineNumberInFile + 1, showNumber);

        } else if (showNumber < shownLineNumber) {

            return getLineNumberOfAfterFile(lineNumberInFile - 1, showNumber);
        }

        lineNumberInFile = getLineNumberOfFile(shownLineNumber, shownLineNumber);

        return lineNumberInFile;

    }

    public LineObj getCurrentDataOfShowLine(int showNumber) {

        int numOfStoredBlocks = trueNumOfStoredBlocks;

        int textEditActionSize = listOfTextEditActions.size();

        for (int k = textEditActionSize - 1; k >= 0; k--) {

            TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(k);

            int editAction = editActionObj.getEditAction();

            if (editAction == TextEditAction.NEW_LINE) {
                NewLinesAction newLinesAction = (NewLinesAction) editActionObj;

                int showLineNumber = newLinesAction.getNewLineObj().getLineNumberOnShow();

                if (showLineNumber == showNumber) {

                    return newLinesAction.getNewLineObj();
                }

            } else if (editAction == TextEditAction.REVISED) {
                RevisedLineAction linesAction = (RevisedLineAction) editActionObj;

                LineObj revisedLineObj = linesAction.getRevisedLineObj();

                int showLineNumber = revisedLineObj.getLineNumberOnShow();

                if (showLineNumber == showNumber) {

                    return revisedLineObj;
                }

            }
        }

        int showLineNumberInFile = getLineNumberOfFile(showNumber, showNumber);

        for (int i = 0; i < numOfStoredBlocks; i++) {

            OneBlockShownContent oneStoredBlock = listOfStoredLines[i];

            int blockFirstLineNumber = oneStoredBlock.getFirstLineNumber();

            int blockLastLineNumber = oneStoredBlock.getLastLineNumber();

            if (blockFirstLineNumber <= showLineNumberInFile && blockLastLineNumber >= showLineNumberInFile) {

                List<LineObj> lines = oneStoredBlock.getLineObj();

                for (int j = 0; j < lines.size(); j++) {

                    LineObj lineObj = lines.get(j);

                    int lineNumber = lineObj.getLineNumber();

                    int showLineNumber = getShownLineNumber(lineNumber);

                    if (showLineNumber == showNumber) {

                        lineObj.setLineNumberOnShow(showLineNumber);

                        return lineObj;

                    }

                }

            } else if (blockLastLineNumber < showLineNumberInFile) {
                if (i + 1 >= trueNumOfStoredBlocks) {

                    break;
                }

                OneBlockShownContent nextStoredBlock = listOfStoredLines[i + 1];

                if (nextStoredBlock == null) {
                    continue;
                }

                int nextStoredBlock_FirstLineNum = nextStoredBlock.getFirstLineNumber();

                if (nextStoredBlock_FirstLineNum > showLineNumberInFile) {
                    long lastOffset = oneStoredBlock.getBlock_offset_End();

                    List<LineObj> partLines = readOriginalLines(dataFile, lastOffset, blockLastLineNumber,
                            blockLastLineNumber + 1, showLineNumberInFile + 1);

                    for (LineObj lineObj : partLines) {

                        if (lineObj.getLineNumberOnShow() == showNumber) {
                            return lineObj;
                        }
                    }
                }
            }
        }

        return null;

    }

    public String replaceAll(String line) {

        int editActionsSize = listOfTextEditActions.size();

        for (int i = 0; i < editActionsSize; i++) {
            TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(i);

            int editAction = editActionObj.getEditAction();

            if (editAction == TextEditAction.REPLACE_ALL) {

                ReplaceAllAction replaceAllAction = (ReplaceAllAction) editActionObj;

                boolean caseSensitive = replaceAllAction.isCaseSensitive();

                String targetString = caseSensitive ? replaceAllAction.getTargetString().toUpperCase()
                        : replaceAllAction.getTargetString();

                String tempLine = caseSensitive ? line.toUpperCase() : line;

                if (tempLine.contains(targetString)) {

                    String replaceWith = replaceAllAction.getReplaceWith();

                    String pattern = "(?i)" + replaceAllAction.getTargetString();

                    line = line.replaceAll(pattern, replaceWith);

                }
            }
        }
        return line;
    }

}
