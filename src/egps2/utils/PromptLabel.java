package egps2.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

/**
 * 提示框
 *
 */
public class PromptLabel extends JLabel {
	private static Color fillColor = new Color(194, 227, 246);

	public PromptLabel(String text) {
		super(text);
		setBounds(0, 0, 320, 36);
		setForeground(Color.black);
		setBorder(new CompoundBorder(new RoundLineBorder(Color.blue, 1, 8, 8), new EmptyBorder(10, 10, 10, 10)));
		setIgnoreRepaint(false);
		setFocusable(false);
		setToolTipText(null);
//        ComponentMove componentMove = new ComponentMove();
//        this.addMouseListener(componentMove);
//        this.addMouseMotionListener(componentMove);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(fillColor);
		g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		g2d.dispose();

		super.paint(g);
	}
}
