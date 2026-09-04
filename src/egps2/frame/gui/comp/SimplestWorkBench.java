package egps2.frame.gui.comp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.tuple.Pair;

import egps2.EGPSProperties;
import egps2.UnifiedAccessPoint;

/**
 * SimplestWorkBench supports the main eGPS window, actions, or tab management.
 */
public class SimplestWorkBench extends JPanel {
	private static final long serialVersionUID = -3074319452683615608L;

	protected JTextArea bottomTextarea;
	protected JTextArea topTextArea;

	private MouseAdapter mouseListener;

	/**
	 * Create the panel.
	 */
	public SimplestWorkBench() {
		setBackground(Color.WHITE);

		Font globalFont = getDefaultFont();

		TitledBorder border = new TitledBorder(new EmptyBorder(20, 6, 6, 6), "Work bench", TitledBorder.LEADING,
				TitledBorder.TOP, globalFont.deriveFont(Font.BOLD), null);
		setBorder(border);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		topTextArea = new JTextArea();
		topTextArea.setBackground(Color.WHITE);
		topTextArea.setFont(globalFont);
		add(new JScrollPane(topTextArea));

		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(0, 4, 4, 4));
		panel.setMaximumSize(new Dimension(32767, 120));
		add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JButton executeButton = new JButton("Execute");
		executeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actions4changeForegroundColor();
				new Thread(() -> {
					String text = topTextArea.getText();
					String[] split = text.split("\n", -2);
					execute(split);
				}).start();
			}
		});
		panel.add(executeButton, BorderLayout.EAST);
		executeButton.setFont(globalFont);

		JButton exampleButton = new JButton("Example");
		exampleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setExampleCase();
			}
		});
		panel.add(exampleButton, BorderLayout.WEST);
		exampleButton.setFont(globalFont);

		bottomTextarea = new JTextArea();
		bottomTextarea.setFont(globalFont);
		add(new JScrollPane(bottomTextarea));

		topTextArea.setAutoscrolls(true);
		bottomTextarea.setAutoscrolls(true);

		topTextArea.setForeground(Color.lightGray);
		bottomTextarea.setForeground(Color.lightGray);

		mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actions4changeForegroundColor();
			}
		};
		topTextArea.addMouseListener(mouseListener);

		setExampleCase();
	}

	protected Font getDefaultFont() {
		Font globalFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		return globalFont;
	}

	private void setExampleCase() {
		Pair<String, String> giveExample = giveExample();

		topTextArea.setText(giveExample.getLeft());
		bottomTextarea.setText(giveExample.getRight());
	}

	private void actions4changeForegroundColor() {
		topTextArea.setForeground(Color.black);
		bottomTextarea.setForeground(Color.black);
		topTextArea.removeMouseListener(mouseListener);
	}

	protected Pair<String, String> giveExample() {
		Pair<String, String> ret = Pair.of("This is input example.", "This is result example.");
		return ret;
	}

	private void execute(String[] inputStrings) {
		List<String> ret = new ArrayList<>(inputStrings.length);
		for (String string : inputStrings) {
			if (string.startsWith("#")) {
				continue;
			}

			if (string.isEmpty()) {
				continue;
			}

			ret.add(string);
		}

		handle(ret);
	}

	protected void handle(List<String> inputStrings) {

	}

	public void setText4TopTextArea(List<String> list) {
		StringBuilder specificationHeader = EGPSProperties.getSpecificationHeader();
		for (String string : list) {
			specificationHeader.append(string).append("\n");
		}

		topTextArea.setText(specificationHeader.toString());
		actions4changeForegroundColor();
	}

}
