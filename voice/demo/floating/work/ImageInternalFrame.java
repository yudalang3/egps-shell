package demo.floating.work;

import javax.swing.*;
import java.awt.*;

/**
 * Internal frame for Image Display visualization.
 * Provides a window for displaying images with toolbar controls.
 */
public class ImageInternalFrame extends JInternalFrame {
    private ImageDisplayPanel imagePanel;
    private JToolBar toolBar;
    
    /**
     * Constructor for ImageInternalFrame
     * Creates a new internal frame with image display capabilities
     */
    public ImageInternalFrame() {
        super("Image Display", true, true, true, true);
        initializeComponents();
        setupLayout();
        setSize(600, 400);
        setVisible(true);
        
        // Load sample image on startup
        loadSampleImage();
    }
    
    /**
     * Initialize components including image panel and toolbar
     */
    private void initializeComponents() {
        imagePanel = new ImageDisplayPanel();
        
        // Create toolbar
        toolBar = new JToolBar("Image Tools");
        toolBar.setFloatable(false);
        
        JButton sampleButton = new JButton("Load Sample");
        sampleButton.setFont(MyFontConfig.getDefaultFont());
        sampleButton.addActionListener(e -> loadSampleImage());
        
        toolBar.add(sampleButton);
    }
    
    /**
     * Set up the layout for the internal frame
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(new JScrollPane(imagePanel), BorderLayout.CENTER);
        add(toolBar, BorderLayout.SOUTH);
    }
    
    /**
     * Load sample image for demonstration
     */
    private void loadSampleImage() {
        imagePanel.loadSampleImage();
        imagePanel.repaint();
    }
}