package demo.floating.work;

import javax.swing.*;
import java.awt.*;

/**
 * Internal frame for MA Plot visualization.
 * Provides a window for displaying MA plots with toolbar controls.
 */
public class MAPlotInternalFrame extends JInternalFrame {
    private MAPlotPanel maPlotPanel;
    private JToolBar toolBar;
    
    /**
     * Constructor for MAPlotInternalFrame
     * Creates a new internal frame with MA plot visualization capabilities
     */
    public MAPlotInternalFrame() {
        super("MA Plot", true, true, true, true);
        initializeComponents();
        setupLayout();
        setSize(600, 400);
        setVisible(true);
        
        // Load sample data on startup
        refreshData();
    }
    
    /**
     * Initialize components including MA plot panel and toolbar
     */
    private void initializeComponents() {
        maPlotPanel = new MAPlotPanel();
        
        // Create toolbar
        toolBar = new JToolBar("MA Plot Tools");
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
        add(new JScrollPane(maPlotPanel), BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh the MA plot data with new sample data
     */
    private void refreshData() {
        // Generate sample data
        int numGenes = 1000;
        double[] logFC = new double[numGenes];
        double[] avgExpression = new double[numGenes];
        boolean[] significant = new boolean[numGenes];
        
        for (int i = 0; i < numGenes; i++) {
            logFC[i] = (Math.random() - 0.5) * 8; // Range: -4 to 4
            avgExpression[i] = Math.random() * 15 + 1; // Range: 1 to 16
            significant[i] = Math.abs(logFC[i]) > 1.5 && Math.random() > 0.7;
        }
        
        maPlotPanel.setData(logFC, avgExpression, significant);
        maPlotPanel.repaint();
    }
}