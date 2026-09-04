package egps2.panels.reusablecom;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import graphic.engine.guibean.ColorIcon;
import egps2.UnifiedAccessPoint;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
/**
 * InstantStatusPanel is a reusable Swing panel or dialog within eGPS.
 */
public class InstantStatusPanel extends JPanel {

	protected ColorIcon colorIcon;
	protected JButton buttonCurrentFont;
	protected JButton buttonCurrentColor;

	public InstantStatusPanel() {
		this(true, true);
	}

	/**
	 * Create the panel.
	 */
	public InstantStatusPanel(boolean hasFont, boolean hasCol) {
		setLayout(new MigLayout("", "[][grow]", "[][]"));
		setBorder(new EmptyBorder(6, 6, 6, 6));

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

		if (hasCol) {

			JLabel lblCurrentColor = new JLabel("Current color:");
			lblCurrentColor.setFont(defaultFont);
			add(lblCurrentColor, "cell 0 0");

			buttonCurrentColor = new JButton();
			buttonCurrentColor.setFocusable(false);

			colorIcon = new ColorIcon(Color.red, 20, 16);
			buttonCurrentColor.setIcon(colorIcon);
			add(buttonCurrentColor, "cell 1 0,growx");

		}

		if (hasFont) {

			JLabel lblCurrentfont = new JLabel("Current font:");
			lblCurrentfont.setFont(defaultFont);
			add(lblCurrentfont, "cell 0 1");

			buttonCurrentFont = new JButton("Change Font");

			buttonCurrentFont.setFocusable(false);
			add(buttonCurrentFont, "cell 1 1,growx");

		}
		setNoSelection();

	}

	protected void setNoSelection() {
		if (buttonCurrentColor != null) {
			buttonCurrentColor.setEnabled(false);
			buttonCurrentColor.setToolTipText("Please select the visual object.");
		}
		if (buttonCurrentFont != null) {
			buttonCurrentFont.setEnabled(false);
			buttonCurrentFont.setToolTipText("Please select the visual object.");
		}
	}

}
