package egps2.frame.gui.comp.toggle.toggle;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import egps2.frame.gui.comp.toggle.swing.shadow.ShadowRenderer;

/**
 * 
 * <h1>目的</h1>
 * <p>
 * 
 * 一个现代化的UI
 * </p>
 * 
 * <h1>输入/输出</h1>
 * 
 * <p>
 * 没有输入
 * </p>
 * 
 * <h1>使用方法</h1>
 * 
 * <blockquote> Dimension dimension = new Dimension(120, 25); checkBoxShowRoot =
 * new ToggleButton(); checkBoxShowRoot.setIconDrawWidth(40);
 * checkBoxShowRoot.setForeground(new java.awt.Color(40, 139, 236));
 * checkBoxShowRoot.setText("Show root");
 * 
 * checkBoxShowRoot.setBorder(BorderFactory.createEmptyBorder());
 * checkBoxShowRoot.setPreferredSize(dimension);
 * checkBoxShowRoot.setFocusable(false);
 * checkBoxShowRoot.setToolTipText("Whether show root in the graph.");
 * checkBoxShowRoot.setFont(globalFont); </blockquote>
 * 
 * <h1>注意点</h1>
 * <ol>
 * <li>在调用主要方法 <code>doIt()</code>时不要忘记设置一些属性</li>
 * <li>现在只能当做脚本来用，如果要用的话注意设置 <code>setter</code>方法等</li>
 * </ol>
 * 
 * @implSpec 就是一笔一划绘制的
 * 
 * @author yudal
 *
 */
@SuppressWarnings("serial")
public class ToggleButton extends JComponent {

	private Animator animator;
	private float animate;
	private boolean selected;
	private boolean mousePress;
	private boolean mouseHover;
	private BufferedImage imageShadow;
	private final Insets shadowSize = new Insets(2, 5, 8, 5);
	private final List<ToggleListener> events = new ArrayList<>();
	private String text;
	private int iconDrawWidth = 60;

	public ToggleButton() {
		init();
		initAnimator();
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		if (this.selected != selected) {
			this.selected = selected;
			if (selected) {
				animate = 1f;
			} else {
				animate = 0;
			}
			repaint();
		}
	}

	public void setSelected(boolean selected, boolean animated) {
		if (this.selected != selected) {
			this.selected = selected;
			runEventSelected();
			if (animated) {
				start(selected);
			} else {
				if (selected) {
					animate = 1f;
				} else {
					animate = 0;
				}
				repaint();
			}
		}
	}

	public void addEventToggleSelected(ToggleListener event) {
		this.events.add(event);
	}
	public void clearEventToggleSelected() {
		this.events.clear();
	}

	private void initAnimator() {
		animator = new Animator(350, new TimingTargetAdapter() {
			@Override
			public void timingEvent(float fraction) {
				if (isSelected()) {
					animate = fraction;
				} else {
					animate = 1f - fraction;
				}
				repaint();
				runEventAnimated();
			}
		});
		animator.setResolution(1);
	}

	private void init() {
		setPreferredSize(new Dimension(80, 35));
		setBackground(new Color(255, 255, 255));
		setForeground(new Color(156, 63, 243));
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				mouseHover = true;
			}

			@Override
			public void mouseExited(MouseEvent e) {
				mouseHover = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					mousePress = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (mousePress && mouseHover) {
						setSelected(!isSelected(), true);
					}
					mousePress = false;
				}
			}
		});
	}

	private void start(boolean selected) {
		if (animator.isRunning()) {
			float f = animator.getTimingFraction();
			animator.stop();
			animator.setStartFraction(1f - f);
		} else {
			animator.setStartFraction(0);
		}
		this.selected = selected;
		animator.start();
	}

	private void runEventSelected() {
		for (ToggleListener event : events) {
			event.onSelected(selected);
		}
	}

	private void runEventAnimated() {
		for (ToggleListener event : events) {
			event.onAnimated(animate);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		int wholeHeight = getHeight();
		int wholeWidth = getWidth();
		if (isOpaque()) {
			g2.setColor(getBackground());
			g2.fill(new Rectangle(0, 0, wholeWidth, wholeHeight));
		}
		if (iconDrawWidth > wholeWidth) {
			iconDrawWidth = wholeWidth;
		}
		double width = iconDrawWidth - (shadowSize.left + shadowSize.right);
		double height = wholeHeight - (shadowSize.top + shadowSize.bottom);
		double h = height * 0.7;
		double x = shadowSize.left;
		double y = shadowSize.top + (height - h) / 2;

		if (text != null) {
			g2.setColor(Color.black);
			FontMetrics fontMetrics = g2.getFontMetrics();
			int leading = fontMetrics.getLeading();
			int ascent = fontMetrics.getAscent();
			int height2 = fontMetrics.getHeight();
			g2.drawString(text, iconDrawWidth + 15, (wholeHeight + leading) / 2);
		}

		// Create background unselected color
		g2.setColor(new Color(210, 210, 210));
		g2.fill(new RoundRectangle2D.Double(x, y, width, h, h, h));
		// Create background selected color
		// Show background selected color 50% when selected
		g2.setColor(getForeground());
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animate * 0.5f));
		g2.fill(new RoundRectangle2D.Double(x, y, width, h, h, h));
		// Create image shadow
		// And removed shadow image opacity 50% when selected
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - animate * 0.5f));
		double location = shadowSize.left + (width - height) * animate;
		g2.drawImage(imageShadow, (int) location - shadowSize.left, 0, null);
		// Create ellipse unselected color
		g2.setColor(new Color(255, 255, 255));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - animate));
		Area area = new Area(new Ellipse2D.Double(location, shadowSize.top, height, height));
		g2.fill(area);
		// Create ellipse selected color
		g2.setColor(getForeground());
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, animate));
		g2.fill(area);

		g2.dispose();

		super.paintComponent(g);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		super.setBounds(x, y, width, height);
		createImageShadow();
	}

	private void createImageShadow() {
		int height = getHeight();
		imageShadow = new BufferedImage(height, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = imageShadow.createGraphics();
		g2.drawImage(createShadow(height), 0, 0, null);
		g2.dispose();
	}

	private BufferedImage createShadow(int size) {
		int width = size - (shadowSize.left + shadowSize.right);
		int height = size - (shadowSize.top + shadowSize.bottom);

		if (width < 0 || height < 0) {
			// nothing to do
			width = 10;
			height = 10;
		} else {

		}

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.fill(new Ellipse2D.Double(0, 0, width, height));
		g2.dispose();
		return new ShadowRenderer(5, 0.5f, new Color(50, 50, 50)).createShadow(img);
	}

	public void setText(String string) {
		this.text = string;

	}

	public void setIconDrawWidth(int iconDrawWidth) {
		this.iconDrawWidth = iconDrawWidth;
	}
}
