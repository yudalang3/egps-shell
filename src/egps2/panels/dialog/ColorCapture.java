package egps2.panels.dialog;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * ColorCapture is a reusable Swing panel or dialog within eGPS.
 */
public class ColorCapture implements Runnable {

	final private EGPSColorChooser egpsColorChooser;
	final private String jLabelString = "Your pointed color is: ";

	public ColorCapture(EGPSColorChooser egpsColorChooser) {
		this.egpsColorChooser = egpsColorChooser;
	}

	public void run() {
		try {

			// egpsColorChooser.minFrame();

			JLabel jLabel = new JLabel(jLabelString);
			MyJPanel jPanel = new MyJPanel();

			jPanel.add(jLabel);
			// Find the screen dimension:
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			jPanel.setPreferredSize(new Dimension((int) d.getWidth(), 30));
			// Screen capture:
			Robot robot = new Robot();
			BufferedImage img = robot.createScreenCapture(new Rectangle(d));

			// Create the window with the captured image:
			JWindow jw = new JWindow();
			Container c = jw.getContentPane();
			c.setLayout(new BorderLayout());
			JLabel jl = new JLabel(new ImageIcon(img));

			jl.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON3) {
						jw.dispose();
						return;
					}

					try {
						Robot robot = new Robot();
						Color c = robot.getPixelColor(e.getX(), e.getY());

						egpsColorChooser.colorChangedAgain(c);
						// egpsColorChooser.maxFrame();
						jw.dispose();
						// System.exit(0);
					} catch (AWTException ex) {
						// ex.printStackTrace();
						jw.dispose();
					}
				}

			});

			jl.addMouseMotionListener(new MouseAdapter() {
				@Override
				public void mouseMoved(MouseEvent e) {

					try {
						Robot robot = new Robot();
						Color c = robot.getPixelColor(e.getX(), e.getY());
						jLabel.setText(jLabelString + c.toString());
						jLabel.setBackground(c);
						jLabel.setForeground(c);
						jPanel.setColor(c);
						jPanel.repaint();
					} catch (AWTException ex) {
						ex.printStackTrace();
					}
				}
			});

			c.add(jPanel, BorderLayout.SOUTH);
			c.add(jl, BorderLayout.CENTER);
			Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
			c.setCursor(cursor);
			jw.setSize(d);
			jw.setVisible(true);
		} catch (AWTException ex) {
			ex.printStackTrace();
			System.exit(2);
		}
	}
//    public static void main(String[] args) {
//    	
//    	new Thread(new ColorCapture(null)).start();
//	}
}

/**
 * MyJPanel is a reusable Swing panel or dialog within eGPS.
 */
class MyJPanel extends JPanel {

	private Color cc = Color.blue;

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(cc);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public void setColor(Color c) {
		this.cc = c;
	}
}
