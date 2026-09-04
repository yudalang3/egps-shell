package egps2.builtin.modules.lowtextedi;

import javax.swing.*;
import java.awt.*;

/**
 * DashedSeparator belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class DashedSeparator extends JPanel {
    private static final int HEIGHT = 2; // 分隔条高度
    private static final Color LINE_COLOR = UIManager.getColor("Separator.foreground"); // 默认颜色

    public DashedSeparator() {
        setPreferredSize(new Dimension(10, HEIGHT));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, HEIGHT));
        setMinimumSize(new Dimension(0, HEIGHT));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // 设置抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 创建虚线笔画
        float[] dashPattern = {10, 5}; // 虚线模式：10像素线段 + 5像素间隔
        BasicStroke dashedStroke = new BasicStroke(
                1.0f,
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f,
                dashPattern,
                0.0f
        );

        g2.setStroke(dashedStroke);
        g2.setColor(LINE_COLOR);

        // 绘制一条水平虚线
        int y = getHeight() / 2;
        g2.drawLine(0, y, getWidth(), y);
    }
}
