package egps2.builtin.modules.largetextedi.gui;

import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;

import egps2.builtin.modules.largetextedi.TextEditorMain;


/**
 * EditorScrollBar belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class EditorScrollBar extends JScrollBar implements AdjustmentListener {

    /**
     *
     */
    private static final long serialVersionUID = -3161358810655542454L;

    private TextEditorMain textEditorMain;

    private TextEditorViewPort textEditorViewPort;

    private int showLineCount;

    public EditorScrollBar(TextEditorMain textEditorMain) {

        this.textEditorMain = textEditorMain;
        this.textEditorViewPort = textEditorMain.getTextEditorViewPort();
        // setMinimum(scrollBarMinValue);
        // setMaximum(scrollBarMaxValue);

        setValue(1);

        setUnitIncrement(1);

        setBlockIncrement(10);

        addAdjustmentListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {

        refProperty();

        setScrollValues();

        super.paintComponent(g);
    }


    /**
     * 在初始加载数据时,刷新滚动条
     *
     * @Author: mhl
     */
    public void refScrollBarData() {

        refProperty();

        setScrollValues();
    }


    public void refProperty() {

        JComponent panel = textEditorMain.getViewPanel();

        int height = panel.getParent().getHeight();

        int startPosition = getValue();

        int lineHeight = textEditorViewPort.getLineHeight();

        showLineCount = (int) Math.floor(height / lineHeight);

        int endPosition = showLineCount + startPosition;

        TextEditorDataManager editorDataManager = textEditorMain.getEditorDataManager();

        int lastLineNumber = editorDataManager.getMaxLength() + 1;

        if (endPosition > lastLineNumber) {
            endPosition = lastLineNumber;
        }

        textEditorViewPort.setStartPosition(startPosition);

        textEditorViewPort.setEndPosition(endPosition);

    }

    public int getEveryPageShowLineNumberMaxCount() {

        return showLineCount;
    }

    public void setScrollValues() {
        int startPosition = textEditorViewPort.getStartPosition();

        TextEditorDataManager editorDataManager = textEditorMain.getEditorDataManager();

        int lastLineNumber = editorDataManager.getMaxLength() + 1;

        int showLineCount = textEditorViewPort.getShowLineCount();

        if (showLineCount > lastLineNumber) {
            showLineCount = lastLineNumber;
        }
        if ((showLineCount + startPosition) > lastLineNumber) {
            startPosition = lastLineNumber - showLineCount;
        }
        if (startPosition < 1) {
            startPosition = 1;
        }
        setValues(startPosition, showLineCount, 1, lastLineNumber);
    }


    @Override
    public void adjustmentValueChanged(AdjustmentEvent e) {
        actionsAfterValueChanged();
    }


    public void actionsAfterValueChanged() {
        new Thread(() -> {
            refProperty();
            int startPosition = textEditorViewPort.getStartPosition();
            int endPosition = textEditorViewPort.getEndPosition();
            if (startPosition > endPosition) {
                return;
            }
            SwingUtilities.invokeLater(() -> {
                try {
                    int horizontalScrollBarValue = textEditorMain.getHorizontalScrollBarValue();
                    Thread thread = new Thread(() -> {
                        try {
                            textEditorMain.getEgpsTextPane().setText(startPosition, endPosition);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }, "setText");
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    textEditorMain.repaint(); //刷新行号
                    JScrollBar horizontalScrollBar = textEditorMain.getJScrollPane().getHorizontalScrollBar();
                    try {
                        horizontalScrollBar.setValue(horizontalScrollBarValue);
                    } catch (Exception e) {
                        horizontalScrollBar.setValue(0); //若有异常,则设置横向滚动条值为零
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            });

        }, "EditorScrollBar actionsAfterValueChanged").start();
    }
}
