package egps2.panels.pref;

import java.awt.Font;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * TextAntiAliasPanel is a reusable Swing panel or dialog within eGPS.
 */
public class TextAntiAliasPanel extends JPanel {

	public TextAntiAliasPanel() {

		LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
		Font defaultFont = launchProperty.getDefaultFont();
		JLabel lblNewLabel = new JLabel("Text antialiasing : ");
		lblNewLabel.setFont(defaultFont);
		add(lblNewLabel);


		String[] elements = getAvaliableOptionals();
		JComboBox<String> comboBox = new JComboBox<>(elements);
		comboBox.setFont(defaultFont);
		add(comboBox);

		comboBox.setSelectedItem(launchProperty.getTextAntiAliasString());

		comboBox.addItemListener(item -> {
			if (item.getStateChange() == ItemEvent.SELECTED) {
				String string = item.getItem().toString();
				LaunchProperty launchProperty3 = UnifiedAccessPoint.getLaunchProperty();
				launchProperty3.setTextAntiAliasString(string);

			}
		});

	}

	@Override
	public String toString() {
		return "Text antialiasing";
	}
	
	private String[] getAvaliableOptionals() {
		String[] ret = new String[] {
				"TEXT_ANTIALIAS_ON",
				"TEXT_ANTIALIAS_OFF",
				"TEXT_ANTIALIAS_DEFAULT",
				"TEXT_ANTIALIAS_GASP",
				"TEXT_ANTIALIAS_LCD_HRGB",
				"TEXT_ANTIALIAS_LCD_HBGR",
				"TEXT_ANTIALIAS_LCD_VRGB",
				"TEXT_ANTIALIAS_LCD_VBGR"
		};
		
		return ret;
	}


}
