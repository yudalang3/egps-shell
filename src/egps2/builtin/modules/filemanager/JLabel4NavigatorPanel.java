package egps2.builtin.modules.filemanager;

import egps2.EGPSProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * JLabel4NavigatorPanel belongs to a built-in eGPS module (loader, panel, or helper).
 * Enhanced with modern macOS Finder-style animations and visual effects.
 */
public class JLabel4NavigatorPanel extends JLabel {

    // macOS Finder-inspired colors
    private static final Color NORMAL_BG = new Color(255, 255, 255);
    private static final Color HOVER_BG = new Color(245, 245, 247);
    private static final Color SELECTED_BG = new Color(0, 122, 255);  // macOS blue
    private static final Color SELECTED_TEXT = Color.WHITE;
    private static final Color NORMAL_TEXT = Color.BLACK;

    private boolean selected = false;
    private boolean hovered = false;

    // Animation support
    private Timer animationTimer;
    private float currentAlpha = 0.0f;
    private static final int ANIMATION_DURATION = 150; // milliseconds
    private static final int ANIMATION_STEPS = 15;

    public JLabel4NavigatorPanel() {
        super();
        setOpaque(false);  // We'll handle custom painting
        setBorder(new EmptyBorder(6, 12, 6, 12));  // macOS-style padding
        setIconTextGap(8);  // Space between icon and text

        setupMouseListeners();
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) {
                    hovered = true;
                    animateToState(true);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) {
                    hovered = false;
                    animateToState(false);
                }
            }
        });
    }

    private void animateToState(boolean toHovered) {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        float targetAlpha = toHovered ? 1.0f : 0.0f;
        float startAlpha = currentAlpha;
        float deltaAlpha = (targetAlpha - startAlpha) / ANIMATION_STEPS;
        int delay = ANIMATION_DURATION / ANIMATION_STEPS;

        animationTimer = new Timer(delay, null);
        animationTimer.addActionListener(e -> {
            currentAlpha += deltaAlpha;

            if ((deltaAlpha > 0 && currentAlpha >= targetAlpha) ||
                (deltaAlpha < 0 && currentAlpha <= targetAlpha)) {
                currentAlpha = targetAlpha;
                animationTimer.stop();
            }

            repaint();
        });
        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();
        int arc = 8;  // Rounded corner radius

        // Draw background with rounded corners
        if (selected) {
            g2d.setColor(SELECTED_BG);
            g2d.fill(new RoundRectangle2D.Float(2, 2, width - 4, height - 4, arc, arc));
            setForeground(SELECTED_TEXT);
        } else if (hovered && currentAlpha > 0) {
            // Blend between normal and hover color
            Color blendedColor = blendColors(NORMAL_BG, HOVER_BG, currentAlpha);
            g2d.setColor(blendedColor);
            g2d.fill(new RoundRectangle2D.Float(2, 2, width - 4, height - 4, arc, arc));
            setForeground(NORMAL_TEXT);
        } else {
            g2d.setColor(NORMAL_BG);
            g2d.fillRect(0, 0, width, height);
            setForeground(NORMAL_TEXT);
        }

        g2d.dispose();
        super.paintComponent(g);
    }

    private Color blendColors(Color c1, Color c2, float ratio) {
        ratio = Math.max(0, Math.min(1, ratio));
        int r = (int) (c1.getRed() * (1 - ratio) + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * (1 - ratio) + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * (1 - ratio) + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension pref = getPreferredSize();
        return new Dimension(Short.MAX_VALUE, pref.height);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        this.hovered = false;

        if (selected) {
            currentAlpha = 0.0f;
        }

        repaint();
        Container parent = getParent();
        if (parent != null) {
            parent.repaint();
        }
    }
}
