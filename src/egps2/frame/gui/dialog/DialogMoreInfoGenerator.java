package egps2.frame.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import egps2.UnifiedAccessPoint;
import egps2.frame.MyFrame;
import egps2.panels.JIMSendTextPane;

/**
 * DialogMoreInfoGenerator supports the main eGPS window, actions, or tab management.
 */
public class DialogMoreInfoGenerator extends JDialog {

	protected StringBuilder sBuilder;
	protected JTextPane jTextPane;

	public DialogMoreInfoGenerator(List<String> contentLists) {
		super(UnifiedAccessPoint.getInstanceFrame(), true);
		
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		contentPane.add(getContentPanel(contentLists), BorderLayout.CENTER);

		JPanel buttonJPanel = new JPanel();
		buttonJPanel.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		buttonJPanel.setLayout(new BoxLayout(buttonJPanel, BoxLayout.X_AXIS));

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		JButton oKButton = new JButton("OK");
		oKButton.setFont(defaultFont);
		buttonJPanel.add(Box.createHorizontalGlue());
		buttonJPanel.add(oKButton);
		contentPane.add(buttonJPanel, BorderLayout.SOUTH);


		oKButton.addActionListener(e -> setVisible(false));

		setSize(600, 600);
		setLocationRelativeTo(UnifiedAccessPoint.getInstanceFrame());

		jTextPane.setFont(defaultFont);
		sBuilder.setLength(0);
		for (String string : contentLists) {
			sBuilder.append(string).append("\n");
		}
		jTextPane.setText(sBuilder.toString());

	}

	public DialogMoreInfoGenerator(MyFrame instanceFrame, boolean b) {
		super(instanceFrame, b);
	}

	protected Component getContentPanel(List<String> contentLists) {

		Font globalFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		sBuilder = new StringBuilder(1024);

		jTextPane = new JIMSendTextPane();

		for (String string : contentLists) {
			sBuilder.append(string).append("\n");
		}
		jTextPane.setText(sBuilder.toString());
		jTextPane.setFont(globalFont);

		JScrollPane jScrollPane = new JScrollPane(jTextPane);
		jScrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		jScrollPane.setBackground(Color.white);
		return jScrollPane;
	}

}
