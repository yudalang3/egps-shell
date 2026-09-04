package egps2.utils.prompt.swing;

import javax.swing.JSlider;

@SuppressWarnings("serial")
/**
 * GameSlider provides shared utility logic for eGPS modules and UI.
 */
public class GameSlider extends JSlider {

    public GameSlider() {
        super();
        setDoubleBuffered(true);
    }

    public boolean isRequestFocusEnabled() {
        setValueIsAdjusting(true);
        repaint();
        return super.isRequestFocusEnabled();
    }

    public void setHideThumb(boolean hide) {
        ((GameSliderUI) getUI()).setHideThumb(hide);
    }
}
