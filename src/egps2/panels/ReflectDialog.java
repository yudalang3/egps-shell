package egps2.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import utils.EGPSGeneralUtil;
import egps2.UnifiedAccessPoint;
import egps2.panels.bugreporter.ReflectDialogContorller;

@SuppressWarnings("serial")
/**
 * ReflectDialog is a reusable Swing panel or dialog within eGPS.
 */
public class ReflectDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField_name;
	private JTextField textField_emal;
	private JTextField textField_gatherdInformation;
	private JTextField textField_platform;
	private JTextField textField_javaVersion;
	private JButton okButton;
	private JTextArea textAreaDescription;

	public ReflectDialog(Frame owner, String title, boolean modal) {
		super(owner, title, modal);
		init();
//		Dimension dimension = DialogSize.findDialogSize("ReportBugDialog");
		setResizable(true);
//		setIconImage(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				saveDialogSize();
			}

		});

		textField_javaVersion.setText(EGPSGeneralUtil.JAVA_VERSION + " | " + EGPSGeneralUtil.JAVA_VENDOR);
		textField_javaVersion.setEditable(false);
		textField_platform.setText(
				EGPSGeneralUtil.OS_NAME + " | " + EGPSGeneralUtil.OS_ARCH + " | " + EGPSGeneralUtil.OS_VERSION);
		textField_platform.setEditable(false);

	}

	private void saveDialogSize() {
//		DialogSize.saveDialogSize("ReportBugDialog",getSize());
	}

	private void init() {

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

		setBounds(100, 100, 619, 501);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		{
			JLabel lblNewLabel = new JLabel("Provide Information");
			lblNewLabel.setFont(defaultFont);
			lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
			contentPanel.add(lblNewLabel);
		}
		{
//			JScrollPane scrollPane = new JScrollPane();
//			contentPanel.add(scrollPane);
			{
				JPanel panel = new JPanel();
				panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
//				scrollPane.setViewportView(panel);
				contentPanel.add(panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[] { 0, 0, 0 };
				gbl_panel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				gbl_panel.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
				gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
						Double.MIN_VALUE };
				panel.setLayout(gbl_panel);
				{
					JTextArea textArea = new JTextArea();
					textArea.setFont(defaultFont);
					textArea.setWrapStyleWord(true);
					textArea.setLineWrap(true);
					String res = "Dear user, thanks for using eGPS and offering your feedback. We will pay special attention to all feedback you have. You can contact us by emailing to the following address: yudalang@ucas.edu.cn ;lihaipeng@sinh.ac.cn. You can also communicate with other eGPS users by join our QQ group: 550899355.\r\n"
							+ "Of course, you can use this feedback module to give us your advice or report the problems you ran into when using eGPS. If you are to report a problem, please provide a description of how the problem occurred. If possible, submit the problematic file in the file upload window. Also don't forget to leave your e-mail so that we can contact you. All your advice will help eGPS develop better! Thank you.\r\n"
							+ "";
					textArea.setText(res);
					GridBagConstraints gbc_textArea = new GridBagConstraints();
					gbc_textArea.gridwidth = 2;
					gbc_textArea.insets = new Insets(0, 0, 5, 5);
					gbc_textArea.fill = GridBagConstraints.BOTH;
					gbc_textArea.gridx = 0;
					gbc_textArea.gridy = 0;
//					JScrollPane jScrollPane = new JScrollPane(textArea);
					panel.add(textArea, gbc_textArea);
					textArea.setEditable(false);
				}
				{
					JLabel lblNewLabel_2 = new JLabel("  Contact name :");
					lblNewLabel_2.setFont(defaultFont);
					GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
					gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
					gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
					gbc_lblNewLabel_2.gridx = 0;
					gbc_lblNewLabel_2.gridy = 1;
					panel.add(lblNewLabel_2, gbc_lblNewLabel_2);
				}
				{
					textField_name = new JTextField();
					textField_name.setFont(defaultFont);
					GridBagConstraints gbc_textField_name = new GridBagConstraints();
					gbc_textField_name.insets = new Insets(0, 0, 5, 5);
					gbc_textField_name.fill = GridBagConstraints.HORIZONTAL;
					gbc_textField_name.gridx = 1;
					gbc_textField_name.gridy = 1;
					panel.add(textField_name, gbc_textField_name);
					textField_name.setColumns(10);
				}
				{
					JLabel lblNewLabel_3 = new JLabel("  E-mail address :");
					lblNewLabel_3.setFont(defaultFont);
					GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
					gbc_lblNewLabel_3.anchor = GridBagConstraints.EAST;
					gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
					gbc_lblNewLabel_3.gridx = 0;
					gbc_lblNewLabel_3.gridy = 2;
					panel.add(lblNewLabel_3, gbc_lblNewLabel_3);
				}
				{
					textField_emal = new JTextField();
					textField_emal.setFont(defaultFont);
					GridBagConstraints gbc_textField_emal = new GridBagConstraints();
					gbc_textField_emal.insets = new Insets(0, 0, 5, 5);
					gbc_textField_emal.fill = GridBagConstraints.HORIZONTAL;
					gbc_textField_emal.gridx = 1;
					gbc_textField_emal.gridy = 2;
					panel.add(textField_emal, gbc_textField_emal);
					textField_emal.setColumns(10);
				}
				{
					JLabel lblNewLabel_4 = new JLabel("  Description :");
					lblNewLabel_4.setFont(defaultFont);
					GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
					gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
					gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
					gbc_lblNewLabel_4.gridx = 0;
					gbc_lblNewLabel_4.gridy = 3;
					panel.add(lblNewLabel_4, gbc_lblNewLabel_4);
				}
				{
					textAreaDescription = new JTextArea();
					textAreaDescription.setFont(defaultFont);
					textAreaDescription.setLineWrap(true);
					GridBagConstraints gbc_textAreaDescription = new GridBagConstraints();
					gbc_textAreaDescription.gridheight = 2;
					gbc_textAreaDescription.insets = new Insets(0, 0, 5, 5);
					gbc_textAreaDescription.fill = GridBagConstraints.BOTH;
					gbc_textAreaDescription.gridx = 1;
					gbc_textAreaDescription.gridy = 3;

//					textAreaDescription.setPreferredSize(new Dimension(200, 200));
//					panel.add(textAreaDescription, gbc_textAreaDescription);
					JScrollPane jScrollPane = new JScrollPane(textAreaDescription);
					panel.add(jScrollPane, gbc_textAreaDescription);

				}
				{
					JSeparator separator = new JSeparator();
					separator.setBackground(Color.BLACK);
					GridBagConstraints gbc_separator = new GridBagConstraints();
					gbc_separator.insets = new Insets(0, 0, 5, 5);
					gbc_separator.gridx = 1;
					gbc_separator.gridy = 4;
					panel.add(separator, gbc_separator);
				}
				{
					JLabel lblNewLabel_5 = new JLabel("  Gatherd information :");
					lblNewLabel_5.setFont(defaultFont);
					GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
					gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
					gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
					gbc_lblNewLabel_5.gridx = 0;
					gbc_lblNewLabel_5.gridy = 6;
					panel.add(lblNewLabel_5, gbc_lblNewLabel_5);
				}
				{
					textField_gatherdInformation = new JTextField();
					textField_gatherdInformation.setFont(defaultFont);
					GridBagConstraints gbc_textField_gatherdInformation = new GridBagConstraints();
					gbc_textField_gatherdInformation.insets = new Insets(0, 0, 5, 5);
					gbc_textField_gatherdInformation.fill = GridBagConstraints.HORIZONTAL;
					gbc_textField_gatherdInformation.gridx = 1;
					gbc_textField_gatherdInformation.gridy = 6;
					panel.add(textField_gatherdInformation, gbc_textField_gatherdInformation);
					textField_gatherdInformation.setColumns(10);
				}
				{
					JLabel lblNewLabel_7 = new JLabel("Platform *");
					lblNewLabel_7.setFont(defaultFont);
					GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
					gbc_lblNewLabel_7.anchor = GridBagConstraints.EAST;
					gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
					gbc_lblNewLabel_7.gridx = 0;
					gbc_lblNewLabel_7.gridy = 7;
					panel.add(lblNewLabel_7, gbc_lblNewLabel_7);
				}
				{
					textField_platform = new JTextField();
					textField_platform.setFont(defaultFont);
					GridBagConstraints gbc_textField_platform = new GridBagConstraints();
					gbc_textField_platform.insets = new Insets(0, 0, 5, 5);
					gbc_textField_platform.fill = GridBagConstraints.HORIZONTAL;
					gbc_textField_platform.gridx = 1;
					gbc_textField_platform.gridy = 7;
					panel.add(textField_platform, gbc_textField_platform);
					textField_platform.setColumns(10);
				}
				{
					JLabel lblNewLabel_8 = new JLabel(" Java version :");
					lblNewLabel_8.setFont(defaultFont);
					GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
					gbc_lblNewLabel_8.anchor = GridBagConstraints.EAST;
					gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
					gbc_lblNewLabel_8.gridx = 0;
					gbc_lblNewLabel_8.gridy = 8;
					panel.add(lblNewLabel_8, gbc_lblNewLabel_8);
				}
				{
					textField_javaVersion = new JTextField();
					textField_javaVersion.setFont(defaultFont);
					GridBagConstraints gbc_textField_javaVersion = new GridBagConstraints();
					gbc_textField_javaVersion.insets = new Insets(0, 0, 5, 5);
					gbc_textField_javaVersion.fill = GridBagConstraints.HORIZONTAL;
					gbc_textField_javaVersion.gridx = 1;
					gbc_textField_javaVersion.gridy = 8;
					panel.add(textField_javaVersion, gbc_textField_javaVersion);
					textField_javaVersion.setColumns(10);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Submit");
				okButton.setFont(defaultFont);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {

						// 显示一个确认对话框，用户可以选择Yes, No或Cancel
						int userChoice = JOptionPane.showConfirmDialog(ReflectDialog.this, "<html>Do you accomplish the email for sending to eGPS development team? <ul><li>Yes to send</li><li>No to cancel and close dialog</li><li>CANCEL to continue edit", // 提示信息
								"Confirmation", // 对话框标题
								JOptionPane.YES_NO_CANCEL_OPTION);

						// 根据用户的选择执行相应操作
						if (userChoice == JOptionPane.YES_OPTION) {
							new Thread(new Runnable() {
								public void run() {
									new ReflectDialogContorller().sentEMail(getInforamtionMap());
								}

							}).start();
							disposeDialog();
						} else if (userChoice == JOptionPane.NO_OPTION) {
							disposeDialog();
						} else {
							// cancel do nothing.
						}

					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setFont(defaultFont);
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						disposeDialog();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	private void disposeDialog() {
		saveDialogSize();
		this.dispose();
	}

	private Map<String, String> getInforamtionMap() {
		Map<String, String> inforMap = new HashMap<String, String>();
		inforMap.put("name", textField_name.getText());
		inforMap.put("email", textField_emal.getText());
		inforMap.put("description", textAreaDescription.getText());
		inforMap.put("information", textField_gatherdInformation.getText());
		inforMap.put("platform", textField_platform.getText());
		inforMap.put("java version", textField_javaVersion.getText());
		// inforMap.put("attachment", textField_name.getText());
		return inforMap;
	}
}
