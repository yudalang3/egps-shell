package egps2.frame.gui;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

/**
 * EGPSCustomTabbedPaneUI supports the main eGPS window, actions, or tab management.
 */
public class EGPSCustomTabbedPaneUI extends BasicTabbedPaneUI {

	private Color selectedTabColor = new Color(100, 150, 255);
	private Color unselectedTabColor = new Color(245, 245, 245);
	private Color hoverTabColor = new Color(150, 200, 255);
	private Color borderColor = new Color(150, 150, 150);

	private boolean textAlignmentCenter = false;

    @Override
    protected void installDefaults() {
        super.installDefaults();
		tabAreaInsets.right = 20;
    }

	// 设置标签的边距
    @Override
	protected Insets getTabInsets(int tabPlacement, int tabIndex) {
		return new Insets(10, 20, 10, 20); // 增加标签的上下左右边距
    }

	// 绘制标签背景
    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected) {
			g2d.setColor(selectedTabColor);
        } else {
			g2d.setColor(unselectedTabColor);
        }

		// 圆角矩形标签
		g2d.fillRoundRect(x, y, w, h, 15, 15);
	}

	// 绘制标签边框
	@Override
	protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h,
			boolean isSelected) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.setColor(borderColor);
		g2d.drawRoundRect(x, y, w, h, 15, 15);
    }

	// 绘制标签文本
    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics, int tabIndex, String title, Rectangle textRect, boolean isSelected) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(font);
		if (isSelected) {
			g2d.setColor(Color.WHITE);
        } else {
			g2d.setColor(Color.DARK_GRAY);
        }


		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR);
		int textX = 32;
		if (textAlignmentCenter){
			textX = textRect.x;
		}
		g2d.drawString(title, textX, textRect.y + metrics.getAscent());
    }

	// 自定义内容面板的边框绘制
    @Override
	protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
		// 使内容面板无边框
    }

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("Custom TabbedPane Example");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JTabbedPane tabbedPane = new JTabbedPane();
			tabbedPane.setUI(new EGPSCustomTabbedPaneUI());

			// 添加一些面板
			tabbedPane.addTab("Tab 1", new JLabel("Content for Tab 1"));
			tabbedPane.addTab("Tab 2", new JLabel("Content for Tab 2"));
			tabbedPane.addTab("Tab 3", new JLabel("Content for Tab 3"));

			frame.add(tabbedPane);
			frame.setSize(400, 300);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
    }
}
