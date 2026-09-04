package demo.floating.work;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * MA Plot panel - for gene expression data comparison visualization
 * M = log2(A/B), A = (log2(A) + log2(B))/2
 * 
 * This panel displays an MA plot, which is a common visualization in gene expression analysis.
 * The plot shows the log fold change (M) on the y-axis versus the average expression (A) on the x-axis.
 * This visualization helps identify genes with significant differential expression.
 */
public class MAPlotPanel extends JPanel {
    private List<MAPoint> dataPoints;
    private double minM, maxM, minA, maxA;
    private final int MARGIN = 50;
    
    /**
     * Data structure for MA plot points
     */
    private static class MAPoint {
        double m; // log fold change
        double a; // average expression
        boolean significant; // significant difference
        
        /**
         * Constructor for MAPoint
         * @param m log fold change value
         * @param a average expression value
         * @param significant whether the point represents a significant difference
         */
        MAPoint(double m, double a, boolean significant) {
            this.m = m;
            this.a = a;
            this.significant = significant;
        }
    }
    
    /**
     * Constructor for MAPlotPanel
     */
    public MAPlotPanel() {
        initializeUI();
        dataPoints = new ArrayList<>();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.WHITE);
    }
    
    /**
     * Set data for MA plot
     * @param logFC log fold change values
     * @param avgExpression average expression values
     * @param significant significance flags
     */
    public void setData(double[] logFC, double[] avgExpression, boolean[] significant) {
        dataPoints.clear();
        
        if (logFC != null && avgExpression != null && significant != null) {
            int length = Math.min(Math.min(logFC.length, avgExpression.length), significant.length);
            
            for (int i = 0; i < length; i++) {
                dataPoints.add(new MAPoint(logFC[i], avgExpression[i], significant[i]));
            }
            
            calculateBounds();
        }
        
        repaint();
    }
    
    /**
     * Load sample data - generate realistic MA plot distribution
     */
    public void loadSampleData() {
        dataPoints.clear();
        Random random = new Random(42); // Fixed seed for reproducible results
        
        // Generate simulated gene expression data with realistic MA plot characteristics
        for (int i = 0; i < 2000; i++) {
            // A value (average expression) - using a wide distribution
            double a = Math.max(0, random.nextGaussian() * 3 + 6); // Mean 6, std 3, min 0
            
            // M value (log fold change) - creating funnel distribution
            double m;
            boolean significant = false;
            
            // Adjust M value variance based on A value (funnel effect)
            // Low expression genes (small A values) have higher technical noise
            double variance = Math.max(0.1, 2.0 / (1 + a * 0.3));
            
            if (random.nextDouble() < 0.05) { // 5% of genes are significantly upregulated
                m = Math.abs(random.nextGaussian() * 0.8) + 1.5 + random.nextGaussian() * variance;
                significant = true;
            } else if (random.nextDouble() < 0.05) { // 5% of genes are significantly downregulated
                m = -(Math.abs(random.nextGaussian() * 0.8) + 1.5) + random.nextGaussian() * variance;
                significant = true;
            } else { // 90% of genes show no significant difference, clustered around M=0
                m = random.nextGaussian() * variance * 0.5;
                significant = false;
            }
            
            // Add some outliers (technical noise)
            if (random.nextDouble() < 0.02 && a < 3) {
                m += random.nextGaussian() * 3; // Technical noise for low expression genes
            }
            
            dataPoints.add(new MAPoint(m, a, significant));
        }
        
        calculateBounds();
        repaint();
    }
    
    /**
     * Calculate data boundaries for proper scaling
     */
    private void calculateBounds() {
        if (dataPoints.isEmpty()) return;
        
        minM = maxM = dataPoints.get(0).m;
        minA = maxA = dataPoints.get(0).a;
        
        for (MAPoint point : dataPoints) {
            minM = Math.min(minM, point.m);
            maxM = Math.max(maxM, point.m);
            minA = Math.min(minA, point.a);
            maxA = Math.max(maxA, point.a);
        }
        
        // Add some margins
        double mRange = maxM - minM;
        double aRange = maxA - minA;
        minM -= mRange * 0.1;
        maxM += mRange * 0.1;
        minA -= aRange * 0.1;
        maxA += aRange * 0.1;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (dataPoints.isEmpty()) {
            drawNoDataMessage(g2d);
            g2d.dispose();
            return;
        }
        
        drawAxes(g2d);
        drawDataPoints(g2d);
        drawThresholdLines(g2d);
        drawLegend(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draw no data message when no data is available
     * @param g2d Graphics2D object for drawing
     */
    private void drawNoDataMessage(Graphics2D g2d) {
        String message = "Click import button to load data";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        g2d.setColor(Color.GRAY);
        g2d.drawString(message, x, y);
    }
    
    /**
     * Draw coordinate axes
     * @param g2d Graphics2D object for drawing
     */
    private void drawAxes(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1));
        
        // X-axis (A - average expression)
        g2d.drawLine(MARGIN, height - MARGIN, width - MARGIN, height - MARGIN);
        
        // Y-axis (M - log fold change)
        g2d.drawLine(MARGIN, MARGIN, MARGIN, height - MARGIN);
        
        // Draw axis labels
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        
        // X-axis label
        String xLabel = "A (Average Expression)";
        FontMetrics fm = g2d.getFontMetrics();
        int xLabelX = (width - fm.stringWidth(xLabel)) / 2;
        g2d.drawString(xLabel, xLabelX, height - 10);
        
        // Y-axis label (rotated 90 degrees)
        Graphics2D g2dRotated = (Graphics2D) g2d.create();
        g2dRotated.rotate(-Math.PI / 2, 15, height / 2);
        String yLabel = "M (Log2 Fold Change)";
        int yLabelX = -fm.stringWidth(yLabel) / 2;
        g2dRotated.drawString(yLabel, yLabelX, 0);
        g2dRotated.dispose();
        
        // Draw ticks
        drawTicks(g2d);
    }
    
    /**
     * Draw axis ticks and labels
     * @param g2d Graphics2D object for drawing
     */
    private void drawTicks(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        g2d.setColor(Color.GRAY);
        
        // X-axis ticks
        for (int i = 0; i <= 5; i++) {
            double aValue = minA + (maxA - minA) * i / 5.0;
            int x = MARGIN + (width - 2 * MARGIN) * i / 5;
            g2d.drawLine(x, height - MARGIN, x, height - MARGIN + 5);
            g2d.drawString(String.format("%.1f", aValue), x - 10, height - MARGIN + 20);
        }
        
        // Y-axis ticks
        for (int i = 0; i <= 5; i++) {
            double mValue = minM + (maxM - minM) * i / 5.0;
            int y = height - MARGIN - (height - 2 * MARGIN) * i / 5;
            g2d.drawLine(MARGIN - 5, y, MARGIN, y);
            g2d.drawString(String.format("%.1f", mValue), 5, y + 3);
        }
    }
    
    /**
     * Draw data points as arrows
     * @param g2d Graphics2D object for drawing
     */
    private void drawDataPoints(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        for (MAPoint point : dataPoints) {
            // Calculate screen coordinates
            int x = MARGIN + (int) ((point.a - minA) / (maxA - minA) * (width - 2 * MARGIN));
            int y = height - MARGIN - (int) ((point.m - minM) / (maxM - minM) * (height - 2 * MARGIN));
            
            // Set color based on significance and direction
            if (point.significant) {
                if (point.m > 0) {
                    g2d.setColor(new Color(255, 100, 100, 180)); // Red - upregulated
                } else {
                    g2d.setColor(new Color(100, 100, 255, 180)); // Blue - downregulated
                }
            } else {
                g2d.setColor(new Color(150, 150, 150, 100)); // Gray - no significant difference
            }
            
            // Draw arrow
            drawArrow(g2d, x, y, point.m > 0);
        }
    }
    
    /**
     * Draw an arrow at the specified position
     * @param g2d Graphics2D object for drawing
     * @param x X-coordinate
     * @param y Y-coordinate
     * @param upward Whether the arrow should point upward
     */
    private void drawArrow(Graphics2D g2d, int x, int y, boolean upward) {
        int[] xPoints, yPoints;
        
        if (upward) {
            // Upward arrow
            xPoints = new int[]{x, x - 3, x + 3};
            yPoints = new int[]{y - 3, y + 2, y + 2};
        } else {
            // Downward arrow
            xPoints = new int[]{x, x - 3, x + 3};
            yPoints = new int[]{y + 3, y - 2, y - 2};
        }
        
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
    
    /**
     * Draw threshold lines
     * @param g2d Graphics2D object for drawing
     */
    private void drawThresholdLines(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();
        
        g2d.setColor(new Color(255, 0, 0, 100));
        g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
        
        // Draw M = 0 line (no change)
        if (minM <= 0 && maxM >= 0) {
            int y = height - MARGIN - (int) ((0 - minM) / (maxM - minM) * (height - 2 * MARGIN));
            g2d.drawLine(MARGIN, y, width - MARGIN, y);
        }
        
        // Draw significance threshold lines (M = ±1.5)
        double[] thresholds = {1.5, -1.5};
        for (double threshold : thresholds) {
            if (minM <= threshold && maxM >= threshold) {
                int y = height - MARGIN - (int) ((threshold - minM) / (maxM - minM) * (height - 2 * MARGIN));
                g2d.drawLine(MARGIN, y, width - MARGIN, y);
            }
        }
    }
    
    /**
     * Draw legend
     * @param g2d Graphics2D object for drawing
     */
    private void drawLegend(Graphics2D g2d) {
        int width = getWidth();
        int legendX = width - 150;
        int legendY = 20;
        
        // Draw legend background
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRect(legendX - 5, legendY - 5, 140, 80);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX - 5, legendY - 5, 140, 80);
        
        // Set font
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        // Upregulated genes
        g2d.setColor(new Color(255, 100, 100));
        drawArrow(g2d, legendX + 10, legendY + 10, true);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Upregulated", legendX + 20, legendY + 15);
        
        // Downregulated genes
        g2d.setColor(new Color(100, 100, 255));
        drawArrow(g2d, legendX + 10, legendY + 30, false);
        g2d.setColor(Color.BLACK);
        g2d.drawString("Downregulated", legendX + 20, legendY + 35);
        
        // No significant difference
        g2d.setColor(new Color(150, 150, 150));
        drawArrow(g2d, legendX + 10, legendY + 50, true);
        g2d.setColor(Color.BLACK);
        g2d.drawString("No significant", legendX + 20, legendY + 55);
        g2d.drawString("difference", legendX + 20, legendY + 68);
    }
}