package egps2.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Timer;

import egps2.UnifiedAccessPoint;
import egps2.frame.MyFrame;
import egps2.utils.prompt.swing.Label;

/**
 * GlassPanelPrompt provides shared utility logic for eGPS modules and UI.
 */
public class GlassPanelPrompt {
	private final ImageIcon icon;
	private volatile int currentSizeOfPrompt = 0;

	private int offsetX = 500;
	private int offsetY = 250;
	private MyFrame myFrame;

	public GlassPanelPrompt(MyFrame myFrame) {
		URL imageResource = UnifiedAccessPoint.getImageResource("miscellaneous/bell.png");
		icon = new ImageIcon(imageResource);
		this.myFrame = myFrame;
	}

	public void prompt(String text, long delay) {
		final PromptLabel stringLabel = new PromptLabel(text);
		Label iconLabel = new Label(null, icon, 0);
		int offset = currentSizeOfPrompt * 25;

		int xx = offsetX + offset;
		iconLabel.setBounds(xx, offsetY + offset, icon.getIconWidth(), icon.getIconHeight());

		Font font = stringLabel.getFont();
		FontMetrics fontMetrics = stringLabel.getFontMetrics(font);

		int stringWidth = fontMetrics.stringWidth(text) + 30;
		stringLabel.setBounds(xx + icon.getIconWidth() + 5, offsetY + offset - 8, stringWidth,
				fontMetrics.getHeight() + 15);

		JComponent jComponent = (JComponent) myFrame.getGlassPane();
		jComponent.add(stringLabel, 0);
		jComponent.add(iconLabel, 0);

		currentSizeOfPrompt++;

		ActionListener task = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jComponent.remove(stringLabel);
				jComponent.remove(iconLabel);
				currentSizeOfPrompt--;

				boolean empty = currentSizeOfPrompt == 0;
				if (empty) {
					jComponent.repaint();

					int componentCount = jComponent.getComponentCount();
					if (componentCount == 0) {
						jComponent.setVisible(false);
					}
				}

			}

		};

		Timer timer = new Timer((int) delay, task);
		timer.setRepeats(false);
		timer.start();

	}

}
