package egps2.builtin.modules.bonus.modules;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicGraphicsUtils;

/**
 * BasicGraphicsUtilsTest belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class BasicGraphicsUtilsTest extends JFrame {

    public BasicGraphicsUtilsTest() {
        setTitle("BasicGraphicsUtils Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
				testDrawEtchedRect(g);
				testDrawGroove(g);
//				testDrawBezel(g);
//				testDrawString(g);
//				testDrawDashedRect(g);
//				testGetPreferredButtonSize(g);
            }
        };
        add(panel);
    }

    private void testDrawEtchedRect(Graphics g) {
        BasicGraphicsUtils.drawEtchedRect(g, 10, 10, 100, 50,
                Color.GRAY, Color.DARK_GRAY, Color.WHITE, Color.LIGHT_GRAY);
        Insets insets = BasicGraphicsUtils.getEtchedInsets();
        System.out.println("Etched Insets: " + insets);
    }

    private void testDrawGroove(Graphics g) {
		BasicGraphicsUtils.drawGroove(g, 10, 70, 500, 250,
                Color.GRAY, Color.LIGHT_GRAY);
        Insets insets = BasicGraphicsUtils.getGrooveInsets();
        System.out.println("Groove Insets: " + insets);
    }

    private void testDrawBezel(Graphics g) {
        BasicGraphicsUtils.drawBezel(g, 10, 130, 100, 50,
                true, true, Color.GRAY, Color.DARK_GRAY, Color.WHITE, Color.LIGHT_GRAY);
        BasicGraphicsUtils.drawBezel(g, 10, 190, 100, 50,
                false, true, Color.GRAY, Color.DARK_GRAY, Color.WHITE, Color.LIGHT_GRAY);
        BasicGraphicsUtils.drawBezel(g, 10, 250, 100, 50,
                true, false, Color.GRAY, Color.DARK_GRAY, Color.WHITE, Color.LIGHT_GRAY);
        BasicGraphicsUtils.drawBezel(g, 10, 310, 100, 50,
                false, false, Color.GRAY, Color.DARK_GRAY, Color.WHITE, Color.LIGHT_GRAY);
    }

    private void testDrawString(Graphics g) {
        BasicGraphicsUtils.drawString(g, "Underlined 'n'", 'n', 120, 40);
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, "Underlined 'r'", 2, 120, 60);
    }

    private void testDrawDashedRect(Graphics g) {
        BasicGraphicsUtils.drawDashedRect(g, 120, 100, 100, 50);
    }

    private void testGetPreferredButtonSize(Graphics g) {
        AbstractButton button = new JButton("Test Button");
        Dimension size = BasicGraphicsUtils.getPreferredButtonSize(button, 4);
        System.out.println("Preferred Button Size: " + size);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BasicGraphicsUtilsTest().setVisible(true);
            }
        });
    }
}
