package demo.floating.work;

import javax.swing.*;
import java.awt.*;

/**
 * Internal frame for Multiple Sequence Alignment visualization.
 * Provides a window for displaying multiple sequence alignments with toolbar controls.
 */
public class MSAInternalFrame extends JInternalFrame {
    private MultipleSequenceAlignmentPanel msaPanel;
    private JToolBar toolBar;
    
    /**
     * Constructor for MSAInternalFrame
     * Creates a new internal frame with multiple sequence alignment visualization capabilities
     */
    public MSAInternalFrame() {
        super("Multiple Sequence Alignment", true, true, true, true);
        initializeComponents();
        setupLayout();
        setSize(600, 400);
        setVisible(true);
        
        // Load sample data on startup
        refreshData();
    }
    
    /**
     * Initialize components including MSA panel and toolbar
     */
    private void initializeComponents() {
        msaPanel = new MultipleSequenceAlignmentPanel();
        
        // Create toolbar
        toolBar = new JToolBar("MSA Tools");
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
        add(new JScrollPane(msaPanel), BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
    }
    
    /**
     * Refresh the MSA data with new sample data
     */
    private void refreshData() {
        // Load sample data
        String[] sequences = {
            "ATCGATCGATCG",
            "ATCGATCGATCG",
            "ATCGATCGATCG",
            "ATCGATCGATCG",
            "ATCGATCGATCG"
        };
        
        String[] labels = {
            "Sequence 1",
            "Sequence 2", 
            "Sequence 3",
            "Sequence 4",
            "Sequence 5"
        };
        
        msaPanel.setSequences(sequences, labels);
        msaPanel.repaint();
    }
}