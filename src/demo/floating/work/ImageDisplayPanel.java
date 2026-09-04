package demo.floating.work;

import egps2.EGPSProperties;
import egps2.frame.gui.EGPSMainGuiUtil;
import egps2.panels.dialog.SwingDialog;
import egps2.utils.common.util.poi.pptx.Decoder4pptx;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

/**
 * Image display panel - for loading and displaying image files.
 * This panel provides functionality for loading images from files, zooming, and displaying
 * biological pathway illustrations.
 */
public class ImageDisplayPanel extends JPanel {
    private BufferedImage currentImage;
    private JScrollPane scrollPane;
    private ImageCanvas imageCanvas;
    private JPanel controlPanel;
    private JButton loadButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton resetZoomButton;
    private JLabel statusLabel;
    private double zoomFactor = 1.0;

    private XSLFSlide firstSlide = null;
    
    /**
     * Image canvas inner class for rendering images
     */
    private class ImageCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            EGPSMainGuiUtil.setupHighQualityRendering(g2d);
            
//            if (currentImage != null) {
//                int scaledWidth = (int) (currentImage.getWidth() * zoomFactor);
//                int scaledHeight = (int) (currentImage.getHeight() * zoomFactor);
//
//                // Center display image
//                int x = Math.max(0, (getWidth() - scaledWidth) / 2);
//                int y = Math.max(0, (getHeight() - scaledHeight) / 2);
//
//                g2d.drawImage(currentImage, x, y, scaledWidth, scaledHeight, this);
//            } else {
//                // Display placeholder
//                drawPlaceholder(g2d);
//            }
            if (firstSlide != null){
                firstSlide.draw(g2d);
            }

            g2d.dispose();
        }
        
        /**
         * Draw placeholder when no image is loaded
         * @param g2d Graphics2D object for drawing
         */
        private void drawPlaceholder(Graphics2D g2d) {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0));
            g2d.drawRect(20, 20, getWidth() - 40, getHeight() - 40);
            
            String message = "Click to load image or import data";
            g2d.setFont(MyFontConfig.getDefaultFont());
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawString(message, x, y);
        }
        
        @Override
        public Dimension getPreferredSize() {
            if (currentImage != null) {
                return new Dimension(
                    (int) (currentImage.getWidth() * zoomFactor),
                    (int) (currentImage.getHeight() * zoomFactor)
                );
            }
            return new Dimension(300, 200);
        }
    }
    
    /**
     * Constructor for ImageDisplayPanel
     * Initializes the UI components and sets up the panel layout
     */
    public ImageDisplayPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Pathway illustration"));
        setPreferredSize(new Dimension(400, 300));
         initializeUI();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        // Create image canvas
        imageCanvas = new ImageCanvas();
        imageCanvas.setBackground(Color.WHITE);
        
        // Create scroll panel
        scrollPane = new JScrollPane(imageCanvas);
        scrollPane.setPreferredSize(new Dimension(350, 220));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        // Create control panel
        createControlPanel();
        
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the control panel with buttons and status label
     */
    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Load button
        loadButton = new JButton("Load Image");
        loadButton.setFont(MyFontConfig.getDefaultFont());
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadImageFile();
            }
        });
        
        // Zoom buttons
        zoomInButton = new JButton("+");
        zoomInButton.setPreferredSize(new Dimension(30, 25));
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomIn();
            }
        });
        
        zoomOutButton = new JButton("-");
        zoomOutButton.setPreferredSize(new Dimension(30, 25));
        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        });
        
        resetZoomButton = new JButton("1:1");
        resetZoomButton.setPreferredSize(new Dimension(35, 25));
        resetZoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetZoom();
            }
        });
        
        // Status label
        statusLabel = new JLabel("No image loaded");
        statusLabel.setFont(MyFontConfig.getDefaultFont());
        
        controlPanel.add(loadButton);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        JLabel zoomLabel = new JLabel("Zoom:");
        zoomLabel.setFont(MyFontConfig.getDefaultFont());
        controlPanel.add(zoomLabel);
        controlPanel.add(zoomOutButton);
        controlPanel.add(zoomInButton);
        controlPanel.add(resetZoomButton);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(statusLabel);
        
        // Initially disable zoom buttons
        updateButtonStates();
    }
    
    /**
     * Load image file using file chooser dialog
     */
    private void loadImageFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif") || 
                       name.endsWith(".bmp");
            }
            
            @Override
            public String getDescription() {
                return "Image files (*.jpg, *.png, *.gif, *.bmp)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadImage(selectedFile);
        }
    }
    
    /**
     * Load specified image file
     * @param imageFile Image file to load
     */
    private void loadImage(File imageFile) {
        try {
            currentImage = ImageIO.read(imageFile);
            if (currentImage != null) {
                zoomFactor = 1.0;
                updateImageDisplay();
                statusLabel.setText(String.format("%s (%dx%d)", 
                    imageFile.getName(), currentImage.getWidth(), currentImage.getHeight()));
                updateButtonStates();
            } else {
                JOptionPane.showMessageDialog(this, "Unable to load image file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading image file: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Load sample image (create a simple sample image)
     */
    public void loadSampleImage() {
        // Create a sample image
        int width = 200;
        int height = 150;
        currentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = currentImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw gradient background
        GradientPaint gradient = new GradientPaint(0, 0, Color.BLUE, width, height, Color.RED);
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, width, height);
        
        // Draw some shapes
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(50, 30, 100, 90);
        
        g2d.setColor(Color.YELLOW);
        g2d.fillOval(70, 50, 20, 20);
        g2d.fillOval(110, 50, 20, 20);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawArc(80, 80, 40, 30, 0, -180);
        
        // Add text
        g2d.setFont(MyFontConfig.getTitleFont());
        g2d.setColor(Color.WHITE);
        g2d.drawString("Sample Image", 60, 130);
        
        g2d.dispose();
        
        zoomFactor = 1.0;
        updateImageDisplay();
        statusLabel.setText(String.format("Sample Image (%dx%d)", width, height));
        updateButtonStates();


        Decoder4pptx decoder4pptx = new Decoder4pptx();
        try {
            // Read from package internal resource instead of external file system
            InputStream inputStream = getClass().getResourceAsStream("/demo/floating/wnt_pathway_short.pptx");
            if (inputStream == null) {
                throw new IOException("Resource not found: /demo/floating/wnt_pathway_short.pptx");
            }
            decoder4pptx.decodeFile(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            SwingDialog.showErrorMSGDialog("Input error", "Please check the pptx file in package resources.");
        }

        firstSlide = decoder4pptx.getFirstSlide();
        Dimension pageSize = decoder4pptx.getPageSize();
        setPreferredSize(pageSize);
    }
    
    /**
     * Zoom in image
     */
    private void zoomIn() {
        if (zoomFactor < 5.0) {
            zoomFactor *= 1.2;
            updateImageDisplay();
        }
    }
    
    /**
     * Zoom out image
     */
    private void zoomOut() {
        if (zoomFactor > 0.1) {
            zoomFactor /= 1.2;
            updateImageDisplay();
        }
    }
    
    /**
     * Reset zoom to 1:1
     */
    private void resetZoom() {
        zoomFactor = 1.0;
        updateImageDisplay();
    }
    
    /**
     * Update image display
     */
    private void updateImageDisplay() {
        imageCanvas.revalidate();
        imageCanvas.repaint();
        
        // Update zoom information in status label
        if (currentImage != null) {
            String baseText = statusLabel.getText();
            if (baseText.contains(" - Zoom:")) {
                baseText = baseText.substring(0, baseText.indexOf(" - Zoom:"));
            }
            statusLabel.setText(String.format("%s - Zoom: %.0f%%", baseText, zoomFactor * 100));
        }
    }
    
    /**
     * Update button states based on current image and zoom level
     */
    private void updateButtonStates() {
        boolean hasImage = currentImage != null;
        zoomInButton.setEnabled(hasImage && zoomFactor < 5.0);
        zoomOutButton.setEnabled(hasImage && zoomFactor > 0.1);
        resetZoomButton.setEnabled(hasImage);
    }
}