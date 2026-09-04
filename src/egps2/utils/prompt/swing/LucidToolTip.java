package egps2.utils.prompt.swing;

import egps2.utils.RoundLineBorder;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * LucidToolTip is a custom tooltip class that provides a semi-transparent, rounded tooltip with a lucid (clear and easily perceived) appearance.
 * The term "lucid" in English means clear, transparent, or easily understood.
 */
public class LucidToolTip extends JToolTip {

    public LucidToolTip() {
        Color foreground = Color.WHITE;
        setForeground(foreground);
        setOpaque(false);
        setBorder(new CompoundBorder(new RoundLineBorder(foreground, 1, 8, 8), new EmptyBorder(2, 2, 2, 2)));
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setColor(Color.BLACK);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        g2d.dispose();
        super.paint(g);
    }
}
