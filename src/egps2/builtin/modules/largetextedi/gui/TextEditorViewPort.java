package egps2.builtin.modules.largetextedi.gui;

import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;

import egps2.UnifiedAccessPoint;

/**
 * TextEditorViewPort belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class TextEditorViewPort {
    protected Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
    private int startPosition = -1;
    private int endPosition = -1;
    private int lineHeight;
    private Font font;


    public TextEditorViewPort() {

        setFont(defaultFont);
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {

        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        Container c = new Container();
        FontMetrics fm = c.getFontMetrics(this.font);
        setLineHeight(fm.getHeight());
    }

    public int getShowLineCount() {

        if (this.startPosition > this.endPosition) {

            return 0;
        }
        return this.endPosition - this.startPosition;

    }

}
