package egps2.panels.graphicpro;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InputMismatchException;

import egps2.EGPSProperties;

/**
 * CustomizeFontEnum is a reusable Swing panel or dialog within eGPS.
 */
public enum CustomizeFontEnum {
    COUSINEREGULARFONTFAMILY("Cousine", "Cousine-Regular-1.ttf");

    private String fontFamily;
    private String fontFileName;

    CustomizeFontEnum(String fontFamily, String fontFileName) {
        this.fontFamily = fontFamily;
        this.fontFileName = fontFileName;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public Font getCousineDefinedFont(int fontStyle, int fontSize) {
		String propertiesDir = EGPSProperties.PROPERTIES_DIR;
		String filepath = propertiesDir + "/font/" + fontFileName;
        Font font = null;
        File file = new File(filepath);

		if (!file.exists()) {
			throw new InputMismatchException("The file is not exists: ".concat(file.toString()));
		}

        try {
            font = Font.createFont(Font.TRUETYPE_FONT, file);
            font = font.deriveFont(fontStyle, fontSize);
        } catch (FontFormatException e) {
            return null;
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return font;
    }
}
