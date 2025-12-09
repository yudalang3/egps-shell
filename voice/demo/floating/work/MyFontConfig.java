package demo.floating.work;

import egps2.UnifiedAccessPoint;

import java.awt.Font;

/**
 * Font configuration manager for the bioinformatics application.
 * This class provides a centralized font management system for all UI components,
 * ensuring visual consistency across the application.
 * 
 * It provides access to both default and title fonts through static constants
 * and corresponding getter methods.
 */
public class MyFontConfig {
    
    /**
     * Default font for regular UI components.
     * This font should be used for standard interface elements such as labels, buttons, and text fields.
     */
    public static final Font DEFAULT_FONT = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
    
    /**
     * Title font for headers and important text.
     * This font should be used for section headers, dialog titles, and other prominent text elements.
     */
    public static final Font TITLE_FONT = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
    
    /**
     * Retrieves the default font used for regular UI components.
     * 
     * This font is intended for standard interface elements such as labels, buttons, and text fields.
     * 
     * @return the default font for regular UI components
     */
    public static Font getDefaultFont() {
        return DEFAULT_FONT;
    }
    
    /**
     * Retrieves the title font used for headers and important text elements.
     * 
     * This font is intended for section headers, dialog titles, and other prominent text elements
     * that require visual emphasis.
     * 
     * @return the title font for headers and important text
     */
    public static Font getTitleFont() {
        return TITLE_FONT;
    }
}