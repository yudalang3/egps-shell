package demo.floating.work;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Bioinformatics internal frame containing an import button and four visualization panels.
 * This frame provides a comprehensive interface for bioinformatics data visualization
 * including multiple sequence alignment, MA plots, heatmaps, and image displays.
 */
public class BioinformaticsInternalFrame extends JInternalFrame {
    private JButton importButton;
    private JPanel mainPanel;
    private JPanel visualizationContainer;
    
    // Four visualization panels
    private MultipleSequenceAlignmentPanel msaPanel;
    private MAPlotPanel maPlotPanel;
    private HeatmapPanel heatmapPanel;
    private ImageDisplayPanel imagePanel;
    
    /**
     * Constructor for BioinformaticsInternalFrame
     * Creates a new internal frame with the title "Bioinformatics Data Visualization"
     */
    public BioinformaticsInternalFrame() {
        super("Bioinformatics Data Visualization", true, true, true, true);
        initializeUI();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setSize(1000, 700);
        setLocation(50, 50);
        setVisible(true);
        
        mainPanel = new JPanel(new BorderLayout());
        
        // Create top panel containing the import button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        importButton = new JButton("Import Data");
        importButton.setPreferredSize(new Dimension(120, 30));
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showVisualizationPanels();
            }
        });
        
        topPanel.add(new JLabel("Operation Panel: "));
        topPanel.add(importButton);
        topPanel.setBorder(BorderFactory.createEtchedBorder());
        
        // Create visualization container panel
        visualizationContainer = new JPanel();
        visualizationContainer.setLayout(new GridLayout(2, 2, 10, 10));
        visualizationContainer.setBorder(BorderFactory.createTitledBorder("Data Visualization Area"));
        visualizationContainer.setVisible(false); // Initially hidden
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(visualizationContainer, BorderLayout.CENTER);
        
        add(mainPanel);
        
        // Initialize visualization panels
        initializeVisualizationPanels();
    }
    
    /**
     * Initialize the four visualization panels
     */
    private void initializeVisualizationPanels() {
        msaPanel = new MultipleSequenceAlignmentPanel();
        maPlotPanel = new MAPlotPanel();
        heatmapPanel = new HeatmapPanel();
        imagePanel = new ImageDisplayPanel();
    }
    
    /**
     * Display the four visualization panels
     */
    public void showVisualizationPanels() {
        visualizationContainer.removeAll();
        
        // Add four panels to the 2x2 grid
        visualizationContainer.add(msaPanel);
        visualizationContainer.add(maPlotPanel);
        visualizationContainer.add(heatmapPanel);
        visualizationContainer.add(imagePanel);
        
        visualizationContainer.setVisible(true);
        visualizationContainer.revalidate();
        visualizationContainer.repaint();
        
        // Update button status
        importButton.setText("Re-import");
        
        // Trigger data loading in each panel
        loadSampleData();
    }
    
    /**
     * Load sample data into each panel
     */
    private void loadSampleData() {
        // Load multiple sequence alignment sample data
        String[] sequences = {
            "ATCGATCGATCG",
            "ATCGATCGATCG",
            "ATCGATCGATCG",
            "ATCGATCGATCG"
        };
        msaPanel.setSequences(sequences, null);
        
        // Load MA plot sample data
        maPlotPanel.loadSampleData();
        
        // Load heatmap sample data
        heatmapPanel.loadSampleData();
        
        // Load sample image
        imagePanel.loadSampleImage();
    }
}