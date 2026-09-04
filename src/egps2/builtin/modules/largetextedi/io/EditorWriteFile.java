package egps2.builtin.modules.largetextedi.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import egps2.panels.dialog.SwingDialog;
import egps2.builtin.modules.largetextedi.actions.ListOfTextEditActions;
import egps2.builtin.modules.largetextedi.gui.TextEditorDataManager;
import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.NewLinesAction;
import egps2.builtin.modules.largetextedi.model.OneBlockShownContent;
import egps2.builtin.modules.largetextedi.model.RevisedLineAction;
import egps2.builtin.modules.largetextedi.model.TextEditAction;
import egps2.modulei.RunningTask;

/**
 * EditorWriteFile belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class EditorWriteFile implements RunningTask {

	private String newline = System.getProperty("line.separator");

	private File saveFile;

	private BufferedWriter writer;

	private long indexNumber = 1;

	private int blockIndex = 0;

	private TextEditorDataManager editorDataManager;

	private File inputFile;

	public EditorWriteFile(File inputFile, File saveFile, TextEditorDataManager editorDataManager) {

		this.inputFile = inputFile;

		this.saveFile = saveFile;

		this.editorDataManager = editorDataManager;
	}

	@Override
	public int processNext() throws Exception {
		int trueNumOfStoredBlocks = editorDataManager.getTrueNumOfStoredBlocks();
		while (blockIndex < trueNumOfStoredBlocks) {
			OneBlockShownContent[] listOfStoredLines = editorDataManager.getListOfStoredLines();

			OneBlockShownContent oneStoredBlock = listOfStoredLines[blockIndex];

			int blockLastLineNumber = oneStoredBlock.getLastLineNumber();

			int blockLastShowLineNumber = editorDataManager.getShownLineNumber(blockLastLineNumber);

			List<LineObj> lines = oneStoredBlock.getLineObj();

			for (int j = 0; j < lines.size(); j++) {
				boolean writeAction = writeAction(writer);

				if (writeAction) {
					indexNumber++;

					while (true) {
						writeAction = writeAction(writer);

						if (!writeAction) {
							break;
						}
						indexNumber++;

					}

				}

				LineObj lineObj = lines.get(j);

				int lineNumber = lineObj.getLineNumber();

				int showLineNumber = editorDataManager.getShownLineNumber(lineNumber);

				if (showLineNumber < 0) {

					continue;
				}
				boolean revisedLine = editorDataManager.isActionsLineNumberContainLineNumberInFile(lineNumber);

				if (revisedLine) {

					continue;
				}

				if (lineObj.getLineTerminator() != null) {
					writer.write(editorDataManager.replaceAll(lineObj.getLine()) + lineObj.getLineTerminator());
				} else {
					writer.write(editorDataManager.replaceAll(lineObj.getLine()) + newline);
				}

				indexNumber++;

			}

			if (blockIndex + 1 == trueNumOfStoredBlocks) {

				blockIndex++;

				boolean writeAction = writeAction(writer);

				if (writeAction) {
					indexNumber++;

					while (true) {
						writeAction = writeAction(writer);

						if (!writeAction) {
							break;
						}
						indexNumber++;

					}

				}
				return PROGRESS_FINSHED;
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

			long lastOffset = oneStoredBlock.getBlock_offset_End();

			List<LineObj> partLines = editorDataManager.readOriginalLines(inputFile, lastOffset, blockLastLineNumber,
					blockLastLineNumber + 1, nextStoredBlock_FirstLineNumber);

			Collections.sort(partLines);

			for (LineObj lineObj : partLines) {

				boolean writeAction = writeAction(writer);

				if (writeAction) {

					indexNumber++;

					while (true) {
						writeAction = writeAction(writer);

						if (!writeAction) {

							break;
						}

						indexNumber++;
					}
				}

				String lineTerminator = lineObj.getLineTerminator();

				if (lineTerminator != null) {
					writer.write(editorDataManager.replaceAll(lineObj.getLine()) + lineTerminator);
				} else {
					writer.write(editorDataManager.replaceAll(lineObj.getLine()) + newline);
				}
				indexNumber++;

			}

			blockIndex++;

			return blockIndex;
		}

		return PROGRESS_FINSHED;
	}

	@Override
	public void actionsBeforeStart() throws Exception {

		writer = new BufferedWriter(new FileWriter(saveFile));
	}

	@Override
	public void actionsAfterFinished() throws Exception {
		writer.close();
		SwingDialog.showInfoMSGDialog("Information", "Output successfully !");

	}

	private boolean writeAction(BufferedWriter writer) throws IOException {

		ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

		int textEditActionSize = listOfTextEditActions.size();

		for (int k = textEditActionSize - 1; k >= 0; k--) {

			TextEditAction editActionObj = listOfTextEditActions.getEditedLineObj(k);

			int editAction = editActionObj.getEditAction();

			if (editAction == TextEditAction.REVISED) {
				RevisedLineAction linesAction = (RevisedLineAction) editActionObj;

				LineObj revisedLineObj = linesAction.getRevisedLineObj();

				int showLineNumber = revisedLineObj.getLineNumberOnShow();

				if (indexNumber == showLineNumber) {

					LineObj newLineObj = linesAction.getRevisedLineObj();

					String lineTerminator = newLineObj.getLineTerminator() == null ? ""
							: newLineObj.getLineTerminator();

					writer.write(editorDataManager.replaceAll(newLineObj.getLine()) + lineTerminator);
					return true;
				}

			} else if (editAction == TextEditAction.NEW_LINE) {

				NewLinesAction newLinesAction = (NewLinesAction) editActionObj;

				int showLineNumber = newLinesAction.getNewLineObj().getLineNumberOnShow();

				if (indexNumber == showLineNumber) {

					LineObj newLineObj = newLinesAction.getNewLineObj();

					String lineTerminator = newLineObj.getLineTerminator() == null ? ""
							: newLineObj.getLineTerminator();

					writer.write(editorDataManager.replaceAll(newLineObj.getLine()) + lineTerminator);
					return true;
				}

			}
		}
		return false;
	}

	@Override
	public boolean isTimeCanEstimate() {
		return true;
	}

}
