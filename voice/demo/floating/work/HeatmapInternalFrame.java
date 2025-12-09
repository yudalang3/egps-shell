package demo.floating.work;

import javax.swing.*;
import java.awt.*;

/**
 * Internal frame for Heatmap visualization.
 * Provides a window for displaying heatmap data with toolbar controls.
 */
public class HeatmapInternalFrame extends JInternalFrame {
    private HeatmapPanel heatmapPanel;
    private JToolBar toolBar;
    
    /**
     * Constructor for HeatmapInternalFrame
     * Creates a new internal frame with heatmap visualization capabilities
     */
    public HeatmapInternalFrame() {
        super("Heatmap", true, true, true, true);
        initializeComponents();
        setupLayout();
        setSize(600, 400);
        setVisible(true);
        
        // Load sample data on startup
        refreshData();
    }
    
    /**
     * Initialize components including heatmap panel and toolbar
     */
    private void initializeComponents() {
        heatmapPanel = new HeatmapPanel();
        
        // Create toolbar
        toolBar = new JToolBar("Heatmap Tools");
        toolBar.setFloatable(false);
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(MyFontConfig.getDefaultFont());
        refreshButton.addActionListener(e -> refreshData());
        
        toolBar.add(refreshButton);
    }
    
    /**
     * Set up the layout for the internal frame
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(new JScrollPane(heatmapPanel), BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh the heatmap data with new sample data
     */
    private void refreshData() {
        // Generate sample data
        int rows = 20;
        int cols = 15;
        double[][] data = new double[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i][j] = Math.random() * 10 - 5; // Range: -5 to 5
            }
        }
        
        String[] rowLabels = new String[rows];
        String[] colLabels = new String[cols];
        
        for (int i = 0; i < rows; i++) {
            rowLabels[i] = "Gene " + (i + 1);
        }
        
        for (int j = 0; j < cols; j++) {
            colLabels[j] = "Sample " + (j + 1);
        }
        
        heatmapPanel.setData(data, rowLabels, colLabels);
        heatmapPanel.repaint();
    }
}