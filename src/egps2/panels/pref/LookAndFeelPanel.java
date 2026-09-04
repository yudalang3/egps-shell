package egps2.panels.pref;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ItemEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * LookAndFeelPanel is a reusable Swing panel or dialog within eGPS.
 */
public class LookAndFeelPanel extends JPanel {

	public LookAndFeelPanel() {

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		JLabel lblNewLabel = new JLabel("Look and feel : ");
		lblNewLabel.setFont(defaultFont);
		add(lblNewLabel);

		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();

		String[] elements = new String[installedLookAndFeels.length];
		int index = 0;
		for (LookAndFeelInfo string : installedLookAndFeels) {
			elements[index] = string.getClassName();
			index++;
		}
		JComboBox<String> comboBox = new JComboBox<>(elements);
		comboBox.setFont(defaultFont);
		add(comboBox);

		comboBox.addItemListener(item -> {
			if (item.getStateChange() == ItemEvent.SELECTED) {
				String string = item.getItem().toString();
				action4changeLAF(string);
			}
		});

	}

	@Override
	public String toString() {
		return "Look and feel";
	}

	private void action4changeLAF(String string) {

		// GUI already instantiated, where myframe
		// is top-level frame

		JFrame myframe = UnifiedAccessPoint.getInstanceFrame();
		try {

			UIManager.setLookAndFeel(string);

			myframe.setCursor(

					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

			SwingUtilities.updateComponentTreeUI(myframe);

			myframe.validate();

		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} finally {

			myframe.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

}
