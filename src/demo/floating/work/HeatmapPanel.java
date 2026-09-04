package demo.floating.work;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Random;

/**
 * Heatmap panel - for displaying gene expression or other numerical data heatmaps.
 * This panel provides visualization capabilities for 2D numerical data with interactive features
 * such as tooltips and color scaling.
 */
public class HeatmapPanel extends JPanel implements MouseMotionListener {
    private double[][] data;
    private String[] rowLabels;
    private String[] columnLabels;
    private double minValue, maxValue;
    private final int MARGIN = 60;
    private final int CELL_SIZE = 20;
    private Point mousePosition;
    
    /**
     * Constructor for HeatmapPanel
     * Initializes the UI and adds mouse motion listener
     */
    public HeatmapPanel() {
        initializeUI();
        addMouseMotionListener(this);
    }
    
    /**
     * Initialize the user interface
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        setBackground(Color.WHITE);
    }
    
    /**
     * Set data for heatmap
     * @param data 2D data array
     * @param rowLabels row labels
     * @param columnLabels column labels
     */
    public void setData(double[][] data, String[] rowLabels, String[] columnLabels) {
        this.data = data;
        this.rowLabels = rowLabels;
        this.columnLabels = columnLabels;
        
        if (data != null && data.length > 0 && data[0].length > 0) {
            // Calculate min and max values
            minValue = maxValue = data[0][0];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    minValue = Math.min(minValue, data[i][j]);
                    maxValue = Math.max(maxValue, data[i][j]);
                }
            }
        }
        
        repaint();
    }
    
    /**
     * Load sample data for demonstration
     */
    public void loadSampleData() {
        // Create sample data: gene expression matrix
        int rows = 15; // number of genes
        int cols = 8;  // number of samples
        
        data = new double[rows][cols];
        rowLabels = new String[rows];
        columnLabels = new String[cols];
        
        Random random = new Random(42);
        
        // Generate row labels (gene names)
        for (int i = 0; i < rows; i++) {
            rowLabels[i] = "Gene" + (i + 1);
        }
        
        // Generate column labels (sample names)
        for (int j = 0; j < cols; j++) {
            columnLabels[j] = "Sample" + (j + 1);
        }
        
        // Generate simulated expression data
        minValue = Double.MAX_VALUE;
        maxValue = Double.MIN_VALUE;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Create some patterns: certain genes highly expressed in certain samples
                double baseValue = random.nextGaussian() * 2;
                
                // Add some biologically relevant patterns
                if (i < 5 && j < 4) {
                    // First 5 genes highly expressed in first 4 samples
                    baseValue += 3;
                } else if (i >= 10 && j >= 4) {
                    // Last 5 genes highly expressed in last 4 samples
                    baseValue += 2.5;
                } else if (i >= 5 && i < 10) {
                    // Middle genes have relatively stable expression
                    baseValue += 1;
                }
                
                data[i][j] = baseValue;
                minValue = Math.min(minValue, baseValue);
                maxValue = Math.max(maxValue, baseValue);
            }
        }
        
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (data == null) {
            drawNoDataMessage(g2d);
            g2d.dispose();
            return;
        }
        
        drawHeatmap(g2d);
        drawLabels(g2d);
        drawColorScale(g2d);
        drawLegend(g2d);
        drawTooltip(g2d);
        
        g2d.dispose();
    }
    
    /**
     * Draw "No Data" message when no data is available
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
     * Draw the main heatmap
     * @param g2d Graphics2D object for drawing
     */
    private void drawHeatmap(Graphics2D g2d) {
        int startX = MARGIN + 80; // Space for row labels
        int startY = MARGIN;
        
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                int x = startX + j * CELL_SIZE;
                int y = startY + i * CELL_SIZE;
                
                // Calculate color
                Color cellColor = getColorForValue(data[i][j]);
                g2d.setColor(cellColor);
                g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                
                // Draw border
                g2d.setColor(Color.LIGHT_GRAY);
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }
    }
    
    /**
     * Calculate color based on value
     * @param value Data value to map to a color
     * @return Color corresponding to the value
     */
    private Color getColorForValue(double value) {
        // Normalize value to [0, 1] range
        double normalized = (value - minValue) / (maxValue - minValue);
        
        // Use blue-white-red color spectrum
        if (normalized < 0.5) {
            // Blue to white
            float ratio = (float) (normalized * 2);
            int blue = 255;
            int green = (int) (255 * ratio);
            int red = (int) (255 * ratio);
            return new Color(red, green, blue);
        } else {
            // White to red
            float ratio = (float) ((normalized - 0.5) * 2);
            int red = 255;
            int green = (int) (255 * (1 - ratio));
            int blue = (int) (255 * (1 - ratio));
            return new Color(red, green, blue);
        }
    }
    
    /**
     * Draw labels for rows and columns
     * @param g2d Graphics2D object for drawing
     */
    private void drawLabels(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        
        int startX = MARGIN + 80;
        int startY = MARGIN;
        
        // Draw column labels (sample names)
        for (int j = 0; j < columnLabels.length; j++) {
            int x = startX + j * CELL_SIZE + CELL_SIZE / 2;
            int y = startY - 5;
            
            // Rotate text
            Graphics2D g2dRotated = (Graphics2D) g2d.create();
            g2dRotated.rotate(-Math.PI / 4, x, y);
            FontMetrics fm = g2dRotated.getFontMetrics();
            g2dRotated.drawString(columnLabels[j], x - fm.stringWidth(columnLabels[j]) / 2, y);
            g2dRotated.dispose();
        }
        
        // Draw row labels (gene names)
        for (int i = 0; i < rowLabels.length; i++) {
            int x = startX - 5;
            int y = startY + i * CELL_SIZE + CELL_SIZE / 2 + 3;
            
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(rowLabels[i], x - fm.stringWidth(rowLabels[i]), y);
        }
    }
    
    /**
     * Draw color scale legend
     * @param g2d Graphics2D object for drawing
     */
    private void drawColorScale(Graphics2D g2d) {
        int scaleX = getWidth() - 40;
        int scaleY = MARGIN;
        int scaleHeight = 100;
        int scaleWidth = 15;
        
        // Draw color bar
        for (int i = 0; i < scaleHeight; i++) {
            double value = minValue + (maxValue - minValue) * (1.0 - (double) i / scaleHeight);
            Color color = getColorForValue(value);
            g2d.setColor(color);
            g2d.fillRect(scaleX, scaleY + i, scaleWidth, 1);
        }
        
        // Draw border
        g2d.setColor(Color.BLACK);
        g2d.drawRect(scaleX, scaleY, scaleWidth, scaleHeight);
        
        // Draw scale labels
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 9));
        String maxLabel = String.format("%.1f", maxValue);
        String minLabel = String.format("%.1f", minValue);
        String midLabel = String.format("%.1f", (maxValue + minValue) / 2);
        
        g2d.drawString(maxLabel, scaleX + scaleWidth + 2, scaleY + 5);
        g2d.drawString(midLabel, scaleX + scaleWidth + 2, scaleY + scaleHeight / 2 + 3);
        g2d.drawString(minLabel, scaleX + scaleWidth + 2, scaleY + scaleHeight + 5);
    }
    
    /**
     * Draw tooltip on mouse hover
     * @param g2d Graphics2D object for drawing
     */
    private void drawTooltip(Graphics2D g2d) {
        if (mousePosition == null || data == null) return;
        
        int startX = MARGIN + 80;
        int startY = MARGIN;
        
        // Calculate the cell where the mouse is located
        int col = (mousePosition.x - startX) / CELL_SIZE;
        int row = (mousePosition.y - startY) / CELL_SIZE;
        
        if (row >= 0 && row < data.length && col >= 0 && col < data[0].length) {
            String tooltip = String.format("%s - %s: %.2f", 
                rowLabels[row], columnLabels[col], data[row][col]);
            
            // Draw tooltip background
            FontMetrics fm = g2d.getFontMetrics();
            int tooltipWidth = fm.stringWidth(tooltip) + 10;
            int tooltipHeight = fm.getHeight() + 5;
            
            int tooltipX = mousePosition.x + 10;
            int tooltipY = mousePosition.y - 10;
            
            // Ensure tooltip is within panel bounds
            if (tooltipX + tooltipWidth > getWidth()) {
                tooltipX = mousePosition.x - tooltipWidth - 10;
            }
            if (tooltipY - tooltipHeight < 0) {
                tooltipY = mousePosition.y + 20;
            }
            
            g2d.setColor(new Color(255, 255, 200, 230));
            g2d.fillRect(tooltipX, tooltipY - tooltipHeight, tooltipWidth, tooltipHeight);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(tooltipX, tooltipY - tooltipHeight, tooltipWidth, tooltipHeight);
            g2d.drawString(tooltip, tooltipX + 5, tooltipY - 5);
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        // Not implemented
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = e.getPoint();
        repaint();
    }
    
    /**
     * Draw legend for the heatmap
     * @param g2d Graphics2D object for drawing
     */
    private void drawLegend(Graphics2D g2d) {
        int width = getWidth();
        int legendX = width - 120;
        int legendY = 20;
        
        // Draw legend background
        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRect(legendX - 5, legendY - 5, 110, 60);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(legendX - 5, legendY - 5, 110, 60);
        
        // Set font
        g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        // Color bar
        int colorBarWidth = 80;
        int colorBarHeight = 10;
        for (int i = 0; i < colorBarWidth; i++) {
            double ratio = (double) i / colorBarWidth;
            Color color = getColorForValue(minValue + ratio * (maxValue - minValue));
            g2d.setColor(color);
            g2d.fillRect(legendX + i, legendY + 10, 1, colorBarHeight);
        }
        
        // Labels
        g2d.setColor(Color.BLACK);
        g2d.drawString("Low", legendX, legendY + 35);
        g2d.drawString("High", legendX + 60, legendY + 35);
        g2d.drawString("Expression", legendX + 20, legendY + 50);
    }
}