package egps2.frame.gui.comp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import egps2.frame.gui.handler.EGPSTextTransferHandler;
import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;

/**
 * DataImportPanel_OneTypeOneFile supports the main eGPS window, actions, or tab management.
 */
public class DataImportPanel_OneTypeOneFile extends JPanel {
	
	private static final long serialVersionUID = -1373164460444554116L;
	private JTextField textField_importFile;
	private JButton btnNewButton_loadFile;
	private JLabel lbl_formatStatement;

	/**
	 * Create the panel.
	 */
	public DataImportPanel_OneTypeOneFile(Class<?> clz) {
		setPreferredSize(new Dimension(350, 50));
		Font globalFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		
		setBorder(new EmptyBorder(15, 15, 15, 15));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		JLabel lblNewLabel = new JLabel("import file");
		lblNewLabel.setFont(globalFont);
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		add(lblNewLabel, gbc_lblNewLabel);
		
		lbl_formatStatement = new JLabel("");
		lbl_formatStatement.setIcon(EGPSShellIcons.getHelpIcon());
		GridBagConstraints gbc_lbl_formatStatement = new GridBagConstraints();
		gbc_lbl_formatStatement.insets = new Insets(0, 0, 0, 5);
		gbc_lbl_formatStatement.anchor = GridBagConstraints.EAST;
		gbc_lbl_formatStatement.gridx = 1;
		gbc_lbl_formatStatement.gridy = 0;
		lbl_formatStatement.setToolTipText("The file format should be fasta format!");
		add(lbl_formatStatement, gbc_lbl_formatStatement);
		
		textField_importFile = new JTextField();
		textField_importFile.setFont(globalFont);
		GridBagConstraints gbc_textField_importFile = new GridBagConstraints();
		gbc_textField_importFile.insets = new Insets(0, 0, 0, 5);
		gbc_textField_importFile.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_importFile.gridx = 2;
		gbc_textField_importFile.gridy = 0;
		add(textField_importFile, gbc_textField_importFile);
		textField_importFile.setColumns(10);
		textField_importFile.setTransferHandler(new EGPSTextTransferHandler());
		
		btnNewButton_loadFile = new JButton("load");
		btnNewButton_loadFile.setFont(globalFont);
		btnNewButton_loadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				EGPSFileChooser egpsFileChooser = new EGPSFileChooser(clz);
				int showOpenDialog = egpsFileChooser.showOpenDialog();
				if (showOpenDialog == EGPSFileChooser.APPROVE_OPTION) {
					textField_importFile.setText(egpsFileChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_loadFile = new GridBagConstraints();
		gbc_btnNewButton_loadFile.gridx = 3;
		gbc_btnNewButton_loadFile.gridy = 0;
		add(btnNewButton_loadFile, gbc_btnNewButton_loadFile);

	}

	public Optional<File> getInputFile() {
		String text = textField_importFile.getText();
		if (text.isEmpty()) {
			return Optional.empty();
		}else {
			return Optional.ofNullable(new File(text));
		}
	}

	public void setTooltipContents(List<String> contents) {
		StringBuilder sBuilder = new StringBuilder(8192);
		
		sBuilder.append("<html><body>");
		for (String string : contents) {
			sBuilder.append(string).append("<br>");
		}
		
		sBuilder.append("</body></html>");
		
		lbl_formatStatement.setToolTipText(sBuilder.toString());
	}
	public void setInputFileAndDisableLoadFile(File file) {
		textField_importFile.setText(file.getAbsolutePath());
		btnNewButton_loadFile.setEnabled(false);
	}
	
	/**
	 * 这个公共的方法可以用来检测输入的有效性。
	 * 单个文件的话 Arrays.asList(file)
	 * @param inputFiles
	 * @return
	 */
	public static boolean checkFile(List<File> inputFiles) {
		if (inputFiles == null || inputFiles.size() == 0) {
			SwingDialog.showErrorMSGDialog("Input error", "You have not input files yet!");
			return false;
		}
		
		for (File ff : inputFiles) {
			if (!ff.exists()) {
				SwingDialog.showErrorMSGDialog("Input error", "File: " + ff.getAbsolutePath()+"\nNot exists!");
				return false;
			}
		}
		
		return true;
	}
	public static boolean checkFile(Optional<File> resultFile) {
		if (!resultFile.isPresent()) {
			SwingDialog.showErrorMSGDialog("Input error", "You have not input files yet!");
			return false;
		}
		
		File ff = resultFile.get();
			if (!ff.exists()) {
				SwingDialog.showErrorMSGDialog("Input error", "File: " + ff.getAbsolutePath()+"\nNot exists!");
				return false;
			}
		
		return true;
	}

}
