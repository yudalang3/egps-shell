package egps2.utils.prompt.swing;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
/**
 * ImageCheckBox provides shared utility logic for eGPS modules and UI.
 */
public class ImageCheckBox extends JCheckBox {

    public ImageCheckBox(ImageIcon icon) {
        super(icon);
        setOpaque(false);
    }

    @Override
    public void paintImmediately(int x, int y, int w, int h) {
    }
}
