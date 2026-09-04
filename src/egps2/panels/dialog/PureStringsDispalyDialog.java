package egps2.panels.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.StringJoiner;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import egps2.UnifiedAccessPoint;

/**
 * PureStringsDispalyDialog is a reusable Swing panel or dialog within eGPS.
 */
public class PureStringsDispalyDialog extends JPanel {
	public PureStringsDispalyDialog(List<String> contents, Runnable disposeEvent) {
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(20, 20, 5, 20));
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		JButton btnExit = new JButton("Exit");
		btnExit.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel.add(btnExit);
		
		JTextArea textArea = new JTextArea();
		add(new JScrollPane(textArea), BorderLayout.CENTER);
		
		btnExit.addActionListener(e -> {
			disposeEvent.run();
		});
		
		StringJoiner stringJoiner = new StringJoiner("\n");
		for (String string : contents) {
			stringJoiner.add(string);
		}
		textArea.setText(stringJoiner.toString());
		textArea.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultFont());
		

	}

}
