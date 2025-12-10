package demo.floating.work;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

/**
 * Main application panel for bioinformatics tools.
 * This class manages multiple internal frames for different bioinformatics visualizations
 * including multiple sequence alignment, MA plots, heatmaps, and image displays.
 */
public class BioinformaticsApp extends JPanel {
    private JDesktopPane desktopPane;
    private MSAInternalFrame msaFrame;
    private MAPlotInternalFrame maPlotFrame;
    private HeatmapInternalFrame heatmapFrame;
    private ImageInternalFrame imageFrame;
    
    /**
     * Constructor for BioinformaticsApp
     * Initializes components and sets up the layout
     */
    public BioinformaticsApp() {
        initializeComponents();
        setupLayout();
    }
    
    /**
     * Initialize the desktop pane and set its background color
     */
    private void initializeComponents() {
        desktopPane = new JDesktopPane();
        Color defaultBackground = UIManager.getColor("Panel.background");
        desktopPane.setBackground(defaultBackground);
    }
    
    /**
     * Display all data visualization frames
     */
    public void displayData() {
        SwingUtilities.invokeLater(() -> {
            showMSAFrame();
            showMAPlotFrame();
            showHeatmapFrame();
            showImageFrame();
        });

    }
    
    /**
     * Set up the layout for the main panel
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        add(desktopPane, BorderLayout.CENTER);
    }
    
    /**
     * Show the Multiple Sequence Alignment frame
     */
    private void showMSAFrame() {
        if (msaFrame == null || msaFrame.isClosed()) {
            msaFrame = new MSAInternalFrame();
            desktopPane.add(msaFrame);
            msaFrame.setLocation(10, 10);
        }
        msaFrame.setVisible(true);
        msaFrame.toFront();
        try {
            msaFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            // Ignore
        }
    }
    
    /**
     * Show the MA Plot frame
     */
    private void showMAPlotFrame() {
        if (maPlotFrame == null || maPlotFrame.isClosed()) {
            maPlotFrame = new MAPlotInternalFrame();
            desktopPane.add(maPlotFrame);
            maPlotFrame.setLocation(50, 50);
        }
        maPlotFrame.setVisible(true);
        maPlotFrame.toFront();
        try {
            maPlotFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            // Ignore
        }
    }
    
    /**
     * Show the Heatmap frame
     */
    private void showHeatmapFrame() {
        if (heatmapFrame == null || heatmapFrame.isClosed()) {
            heatmapFrame = new HeatmapInternalFrame();
            desktopPane.add(heatmapFrame);
            heatmapFrame.setLocation(90, 90);
        }
        heatmapFrame.setVisible(true);
        heatmapFrame.toFront();
        try {
            heatmapFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            // Ignore
        }
    }
    
    /**
     * Show the Image Display frame
     */
    private void showImageFrame() {
        if (imageFrame == null || imageFrame.isClosed()) {
            imageFrame = new ImageInternalFrame();
            desktopPane.add(imageFrame);
            imageFrame.setLocation(130, 130);
        }
        imageFrame.setVisible(true);
        imageFrame.toFront();
        try {
            imageFrame.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            // Ignore
        }
    }
    
    /**
     * Arrange windows in a grid layout
     */
    public void arrangeWindows() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) return;
        
        int frameWidth = desktopPane.getWidth() / 2;
        int frameHeight = desktopPane.getHeight() / 2;
        
        for (int i = 0; i < frames.length && i < 4; i++) {
            int x = (i % 2) * frameWidth;
            int y = (i / 2) * frameHeight;
            frames[i].setBounds(x, y, frameWidth, frameHeight);
        }
    }

    /**
     * Delete all windows from the desktop pane
     */
    public void deleteAllWindows() {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
            try {
                frame.setClosed(true); // Close the window
            } catch (PropertyVetoException e) {
                // Ignore exception, do nothing if window cannot be closed
            }
            desktopPane.remove(frame); // Remove window from desktop pane
        }
        desktopPane.repaint(); // Repaint desktop pane to update interface
    }
}