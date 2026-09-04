package egps2.panels.pref;

import java.awt.Font;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.google.common.base.Objects;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * LanguageSettingPanel is a reusable Swing panel or dialog within eGPS.
 */
public class LanguageSettingPanel extends JPanel {

	public LanguageSettingPanel() {

		LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
		Font defaultFont = launchProperty.getDefaultFont();
		JLabel lblNewLabel = new JLabel("Module gallery module language : ");
		lblNewLabel.setFont(defaultFont);
		add(lblNewLabel);


		String[] elements = getAvailableOptionals();
		JComboBox<String> comboBox = new JComboBox<>(elements);
		comboBox.setFont(defaultFont);
		add(comboBox);

		if (launchProperty.isEnglish()) {
			comboBox.setSelectedIndex(0);
		} else {
			comboBox.setSelectedIndex(1);
		}

		comboBox.addItemListener(item -> {
			if (item.getStateChange() == ItemEvent.SELECTED) {
				String string = item.getItem().toString();
				LaunchProperty launchProperty3 = UnifiedAccessPoint.getLaunchProperty();
				launchProperty3.setEnglish(Objects.equal("English", string));

			}
		});

	}

	@Override
	public String toString() {
		return "Language";
	}
	
	private String[] getAvailableOptionals() {
		String[] ret = new String[] {
				"English", "Chinese"
		};
		
		return ret;
	}


}
