package egps2.panels.dialog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.jidesoft.swing.JideSwingUtilities;

/**
 * GradientPanel is a reusable Swing panel or dialog within eGPS.
 */
public class GradientPanel extends JPanel {
	private static final long serialVersionUID = 2069032084498541247L;

	private float[] dist;
	private Color[] colors;

	public GradientPanel() {
		dist = new float[] { 0.0f, 0.5f, 1.0f };
		colors = new Color[] { Color.blue, Color.white, Color.red};
	}
	public GradientPanel(float[] dist,Color[] colors) {
		this.dist = dist;
		this.colors = colors;
	}

	public void reSetSchame(float[] dist, Color[] colors) {
		this.dist = dist;
		this.colors = colors;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		float x = 10;
		float y = 20;
		float legendWidth = getWidth() - 20 ;
		float legendHeight = getHeight() - 25;

		Paint linearGradientPaint = JideSwingUtilities.getLinearGradientPaint(x, y, x + legendWidth, y + legendHeight,
				dist, colors);
		g2d.setPaint(linearGradientPaint);
		g2d.fill(new Rectangle2D.Float(x, y, legendWidth, legendHeight));
	}
}
