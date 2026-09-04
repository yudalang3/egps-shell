package egps2.builtin.modules.largetextedi.gui;

import java.util.List;

import javax.swing.text.Element;

import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.SelectEditor;

/**
 * TextEditor2D belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class TextEditor2D {

    private EgpsTextPane egpsTextPane;

    public TextEditor2D(EgpsTextPane egpsTextPane) {

        this.egpsTextPane = egpsTextPane;
    }

    private int selectStartPos;

    private int selectEndPos;

    public boolean isSelectionElement() {

        boolean isSelectionElement = false;

        List<LineObj> readOriginalData = egpsTextPane.readOriginalData();

        SelectEditor selectEditor = egpsTextPane.getSelectEditor();

        TextEditorViewPort textEditorViewPort = egpsTextPane.getTextEditorViewPort();

        int startLineNumber = selectEditor.getStartLineNumber();

        int endLineNumber = selectEditor.getEndLineNumber();

        if (startLineNumber < 1 || endLineNumber < 1 || readOriginalData.size() < 1) {
            return isSelectionElement;
        }

        int startLineNumberOnShow = readOriginalData.get(0).getLineNumberOnShow();

        int size = readOriginalData.size() - 1;

        int endLineNumberOnShow = readOriginalData.get(size).getLineNumberOnShow();

        if (startLineNumber < startLineNumberOnShow && endLineNumber >= startLineNumberOnShow
                && endLineNumber <= endLineNumberOnShow) {

            Element root = egpsTextPane.getDocument().getDefaultRootElement();

            selectStartPos = root.getStartOffset();

            int lineNumber = endLineNumber - textEditorViewPort.getStartPosition();

            Element lineElement = root.getElement(lineNumber);

            int lineEndtOffset = lineElement.getStartOffset();

            selectEndPos = lineEndtOffset + selectEditor.getSelectionEnd();

            isSelectionElement = true;

        } else if (startLineNumber >= startLineNumberOnShow && endLineNumber <= endLineNumberOnShow) {

            Element root = egpsTextPane.getDocument().getDefaultRootElement();

            int lineNumber = startLineNumber - textEditorViewPort.getStartPosition();

            Element lineElement = root.getElement(lineNumber);

            int lineStartOffset = lineElement.getStartOffset();

            selectStartPos = lineStartOffset + selectEditor.getSelectionStart();

            lineNumber = endLineNumber - textEditorViewPort.getStartPosition();

            lineElement = root.getElement(lineNumber);

            int lineEndtOffset = lineElement.getStartOffset();

            selectEndPos = lineEndtOffset + selectEditor.getSelectionEnd();

            isSelectionElement = true;

        } else if (startLineNumber < startLineNumberOnShow && endLineNumber > endLineNumberOnShow) {
            Element root = egpsTextPane.getDocument().getDefaultRootElement();

            selectStartPos = root.getStartOffset();

            selectEndPos = root.getEndOffset();

            isSelectionElement = true;
        } else if (startLineNumber >= startLineNumberOnShow && startLineNumber <= endLineNumberOnShow
                && endLineNumber > endLineNumberOnShow) {
            Element root = egpsTextPane.getDocument().getDefaultRootElement();

            int lineNumber = startLineNumber - textEditorViewPort.getStartPosition();

            Element lineElement = root.getElement(lineNumber);

            int lineStartOffset = lineElement.getStartOffset();

            selectStartPos = lineStartOffset + selectEditor.getSelectionStart();

            selectEndPos = root.getEndOffset();

            isSelectionElement = true;
        } else {
            isSelectionElement = false;
        }

        return isSelectionElement;

    }

    public int getSelectStartPos() {
        return this.selectStartPos;
    }

    public int getSelectEndPos() {
        return this.selectEndPos;
    }
}
