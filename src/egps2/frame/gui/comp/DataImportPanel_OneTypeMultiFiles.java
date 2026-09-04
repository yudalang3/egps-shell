package egps2.frame.gui.comp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;
import egps2.frame.gui.EGPSMainGuiUtil;

/**
 * DataImportPanel_OneTypeMultiFiles supports the main eGPS window, actions, or tab management.
 */
public class DataImportPanel_OneTypeMultiFiles extends JPanel {
	
	private JButton btnNewButton_loadFile;
	private JRadioButton rdbtnImportFiles;
	private JRadioButton rdbtnImportDirectory;
	private JTextField textFieldDir;
	private JLabel lbl_formatStatement;
	private JList<String> list;

	/**
	 * Create the panel.
	 */
	public DataImportPanel_OneTypeMultiFiles(Class<?> clz) {
		setPreferredSize(new Dimension(680, 250));
		Font globalFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		
		setBorder(new EmptyBorder(15, 15, 15, 15));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 50, 20, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		rdbtnImportFiles = new JRadioButton("Import file(s)");
		rdbtnImportFiles.setFont(globalFont);
		GridBagConstraints gbc_rdbtnImportFiles = new GridBagConstraints();
		gbc_rdbtnImportFiles.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnImportFiles.gridx = 0;
		gbc_rdbtnImportFiles.gridy = 0;
		add(rdbtnImportFiles, gbc_rdbtnImportFiles);
		rdbtnImportFiles.setSelected(true);
		buttonGroup.add(rdbtnImportFiles);
		
		lbl_formatStatement = new JLabel("");
		lbl_formatStatement.setIcon(EGPSShellIcons.getHelpIcon());
		GridBagConstraints gbc_lbl_formatStatement = new GridBagConstraints();
		gbc_lbl_formatStatement.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_formatStatement.anchor = GridBagConstraints.EAST;
		gbc_lbl_formatStatement.gridx = 1;
		gbc_lbl_formatStatement.gridy = 0;
		lbl_formatStatement.setToolTipText("The file format should be fasta format!");
		add(lbl_formatStatement, gbc_lbl_formatStatement);
		
		btnNewButton_loadFile = new JButton("load");
		btnNewButton_loadFile.setFont(globalFont);
		btnNewButton_loadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				EGPSFileChooser egpsFileChooser = new EGPSFileChooser(clz);
				egpsFileChooser.setMultiSelectionEnabled(true);
				int showOpenDialog = egpsFileChooser.showOpenDialog();
				if (showOpenDialog == EGPSFileChooser.APPROVE_OPTION) {
					File[] selectedFiles = egpsFileChooser.getSelectedFiles();
					
					int len = selectedFiles.length;
					String[] array = new String[len];
					for (int i = 0; i < len; i++) {
						array[i] = selectedFiles[i].getAbsolutePath();
					}
					list.setListData(array);
				}
			}
		});
		
		list = new JList<String>();
		list.setEnabled(false);
		list.setFont(globalFont);
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridheight = 3;
		gbc_list.insets = new Insets(0, 5, 5, 10);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 2;
		gbc_list.gridy = 0;
		add(new JScrollPane(list), gbc_list);
		GridBagConstraints gbc_btnNewButton_loadFile = new GridBagConstraints();
		gbc_btnNewButton_loadFile.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_loadFile.gridx = 3;
		gbc_btnNewButton_loadFile.gridy = 0;
		add(btnNewButton_loadFile, gbc_btnNewButton_loadFile);
		
		
		rdbtnImportDirectory = new JRadioButton("Import directory");
		GridBagConstraints gbc_rdbtnImportDirectory = new GridBagConstraints();
		gbc_rdbtnImportDirectory.insets = new Insets(0, 0, 0, 5);
		gbc_rdbtnImportDirectory.gridx = 0;
		gbc_rdbtnImportDirectory.gridy = 3;
		add(rdbtnImportDirectory, gbc_rdbtnImportDirectory);
		buttonGroup.add(rdbtnImportDirectory);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setFont(globalFont);
		lblNewLabel.setToolTipText("If there are more than three files, please set up a directory for those files, and input the directory here.");
		lblNewLabel.setIcon(EGPSShellIcons.getHelpIcon());
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 3;
		add(lblNewLabel, gbc_lblNewLabel);
		
		textFieldDir = new JTextField();
		textFieldDir.setFont(globalFont);
		textFieldDir.setColumns(10);
		GridBagConstraints gbc_textFieldDir = new GridBagConstraints();
		gbc_textFieldDir.insets = new Insets(0, 0, 0, 5);
		gbc_textFieldDir.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldDir.gridx = 2;
		gbc_textFieldDir.gridy = 3;
		add(textFieldDir, gbc_textFieldDir);
		
		JButton btnNewButton_loadFile_1 = EGPSMainGuiUtil.getFileDirLoadingJButton(textFieldDir, this.getClass());
		btnNewButton_loadFile_1.setFont(globalFont);
		GridBagConstraints gbc_btnNewButton_loadFile_1 = new GridBagConstraints();
		gbc_btnNewButton_loadFile_1.gridx = 3;
		gbc_btnNewButton_loadFile_1.gridy = 3;
		add(btnNewButton_loadFile_1, gbc_btnNewButton_loadFile_1);

	}

	public List<File> getInputFile() {
		
		List<File> files = new ArrayList<File>();
		
		if (rdbtnImportFiles.isSelected()) {
			ListModel<String> model = list.getModel();

			int size = model.getSize();
			for(int i=0; i < size; i++){
			     String o =  model.getElementAt(i);  
			     files.add(new File(o));
			}
		}else {
			File file = new File( textFieldDir.getText());
			if (file.isDirectory() ) {
				File[] listFiles = file.listFiles();
				for (File file2 : listFiles) {
					files.add(file2);
				}
			}
		}
		
		
		return files;
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

}
