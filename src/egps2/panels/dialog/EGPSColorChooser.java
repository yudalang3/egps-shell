package egps2.panels.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import egps2.UnifiedAccessPoint;
import egps2.utils.common.model.datatransfer.CallBackBehavior;

/**
 * 相对于EGPSColorChooser： 1. 添加了对渐变颜色的支持
 */
public class EGPSColorChooser extends JPanel implements ChangeListener {

	private static final long serialVersionUID = 7053186870243617595L;

	protected JColorChooser tcc;

	protected final int intalnalWidth = 600;
	protected final Font titleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
	protected final Font defauotFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

	protected float[] initializedDist = new float[] { 0.0f, 0.5f, 1.0f };
	protected Color[] initializedColors = new Color[] { Color.blue, Color.white, Color.red };

	protected JTextField pickedColorJLabel;
	private JDialog jDialog;
	private JButton inputJButton;

	protected CallBackBehavior calBackInstance;

	public EGPSColorChooser(JDialog jDialog) {
		this();
		this.jDialog = jDialog;
	}

	public EGPSColorChooser() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(intalnalWidth, 650));
		setColorChooser();
		setButtomButtons();
	}

	protected void setButtomButtons() {
		Border eBorder = BorderFactory.createEmptyBorder(10, 10, 10, 20);

		JPanel jPanel = new JPanel();
		jPanel.setBorder(eBorder);
		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.LINE_AXIS));

		JButton jButton = new JButton("Screen color picker");
		jButton.setFont(defauotFont);
		jButton.addActionListener((e) -> {
			ColorCapture colorCapture = new ColorCapture(this);

			SwingUtilities.invokeLater(() -> {
				colorCapture.run();
			});
		});

		jPanel.add(jButton);
		pickedColorJLabel = new JTextField();
		pickedColorJLabel.setFont(defauotFont);
		pickedColorJLabel.setPreferredSize(new Dimension(50, 20));
		pickedColorJLabel.setSize(50, 20);

		pickedColorJLabel.setBackground(Color.white);
		pickedColorJLabel.setEditable(false);
		jPanel.add(pickedColorJLabel);

		jPanel.add(Box.createHorizontalGlue());
		JButton jButton2 = new JButton("OK");
		jButton2.setFont(defauotFont);
		jButton2.addActionListener((e) -> {
			Color c = tcc.getColor();
			inputJButton.setBackground(c);
			inputJButton.setForeground(c);

			if (calBackInstance != null) {
				calBackInstance.doAfterCorrectClick();
			}
			jDialog.dispose();
		});
		jPanel.add(jButton2);
		JButton jButton3 = new JButton("Cancel");
		jButton3.setFont(defauotFont);
		jButton3.addActionListener((e) -> {
			jDialog.dispose();
		});
		jPanel.add(jButton3);
		add(jPanel, BorderLayout.SOUTH);
	}

	protected void setColorChooser() {
		// Set up color chooser for setting text color
		tcc = new JColorChooser(Color.white);
		tcc.getSelectionModel().addChangeListener(this);
		TitledBorder border = BorderFactory.createTitledBorder("Choose Text Color");
		border.setTitleFont(titleFont);
		tcc.setBorder(border);

		add(tcc, BorderLayout.CENTER);
	}

	public void stateChanged(ChangeEvent e) {
		Color newColor = tcc.getColor();
		colorChangedAgain(newColor);
	}

	public void colorChangedAgain(Color c) {
		tcc.setColor(c);
		pickedColorJLabel
				.setText("[r=" + c.getRed() + ",g=" + c.getGreen() + ",b=" + c.getBlue() + ",a=" + c.getAlpha() + "]");
		updateAll();

	}

	public void updateAll() {
		updateUI();
	}

	public void setJbutton(JButton jButton) {
		this.inputJButton = jButton;

	}

	public void setCallBackInstance(CallBackBehavior cal) {
		calBackInstance = cal;
	}

}
