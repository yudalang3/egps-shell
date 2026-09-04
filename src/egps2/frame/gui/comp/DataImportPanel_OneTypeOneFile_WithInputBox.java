package egps2.frame.gui.comp;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import egps2.frame.gui.handler.JTextAreaTransferHandler;
import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;

@SuppressWarnings("serial")
/**
 * DataImportPanel_OneTypeOneFile_WithInputBox supports the main eGPS window, actions, or tab management.
 */
public class DataImportPanel_OneTypeOneFile_WithInputBox extends JPanel {

	private JRadioButton radioButtonImportFiles;
	private JRadioButton radioButtonImportContent;

	private JLabel questionLabelImportFiles;
	private JLabel questionLabelImportContent;

	/**
	 * 叫做 inputFiles 是应该它可以输入一个文件，也可以拖入多个文件
	 */
//	private JTextAreaWithDefaultContent_MouseAdapter jtextAreaWithString_importFiles;
	private JTextAreaWithDefaultContent_MouseAdapter jtextAreaWithString_directContents;
	private JTextField jtextAreaWithString_importFiles;

	private Consumer<List<String>> directContentConsumer = e -> {
	};
	private Consumer<List<String>> inputFileListConsumer = e -> {
	};
	private Consumer<File> inputdirConsumer = e -> {
	};

	private JButton buttonExample4ImportContent;

	/**
	 * Create the panel.
	 */
	public DataImportPanel_OneTypeOneFile_WithInputBox(Class<?> clz) {
		setPreferredSize(new Dimension(1000, 350));

		Font globalFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

		setBorder(new EmptyBorder(6, 15, 6, 15));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 40, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		setLayout(gridBagLayout);

		ButtonGroup buttonGroup = new ButtonGroup();

		radioButtonImportContent = new JRadioButton("Import content");
		radioButtonImportContent.setFont(globalFont);
		GridBagConstraints gbc_radioButtonImportContent = new GridBagConstraints();
		gbc_radioButtonImportContent.anchor = GridBagConstraints.WEST;
		gbc_radioButtonImportContent.insets = new Insets(0, 0, 5, 5);
		gbc_radioButtonImportContent.gridx = 0;
		gbc_radioButtonImportContent.gridy = 0;
		add(radioButtonImportContent, gbc_radioButtonImportContent);
		radioButtonImportContent.setSelected(true);
		buttonGroup.add(radioButtonImportContent);
		radioButtonImportContent.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				String[] split = getDirectInputContent().split("\n");
				directContentConsumer.accept(Arrays.asList(split));
			}
		});

		questionLabelImportContent = new JLabel("");
		setTooltipContents4importContent(Arrays.asList("Direct import the content of your target format."));
		questionLabelImportContent.setIcon(EGPSShellIcons.getHelpIcon());
		GridBagConstraints gbc_questionLabelImportContent = new GridBagConstraints();
		gbc_questionLabelImportContent.insets = new Insets(0, 0, 5, 5);
		gbc_questionLabelImportContent.gridx = 1;
		gbc_questionLabelImportContent.gridy = 0;
		add(questionLabelImportContent, gbc_questionLabelImportContent);

		jtextAreaWithString_directContents = new JTextAreaWithDefaultContent_MouseAdapter();
		JTextArea textAreaOfDirecTextArea = jtextAreaWithString_directContents.getTextArea();
		GridBagConstraints gbc_jtextAreaWithString_directContents = new GridBagConstraints();
		gbc_jtextAreaWithString_directContents.gridheight = 2;
		gbc_jtextAreaWithString_directContents.insets = new Insets(0, 5, 5, 6);
		gbc_jtextAreaWithString_directContents.fill = GridBagConstraints.BOTH;
		gbc_jtextAreaWithString_directContents.gridx = 2;
		gbc_jtextAreaWithString_directContents.gridy = 0;

		add(jtextAreaWithString_directContents, gbc_jtextAreaWithString_directContents);

		buttonExample4ImportContent = new JButton("Exam.");
		buttonExample4ImportContent.setToolTipText("Click to see the examples");
		GridBagConstraints gbc_buttonImportContent = new GridBagConstraints();
		gbc_buttonImportContent.anchor = GridBagConstraints.SOUTH;
		gbc_buttonImportContent.insets = new Insets(0, 0, 5, 0);
		gbc_buttonImportContent.gridx = 3;
		gbc_buttonImportContent.gridy = 1;
		add(buttonExample4ImportContent, gbc_buttonImportContent);

		radioButtonImportFiles = new JRadioButton("Import file(s)");
		radioButtonImportFiles.setFont(globalFont);
		GridBagConstraints gbc_radioButtonImportFiles = new GridBagConstraints();
		gbc_radioButtonImportFiles.anchor = GridBagConstraints.WEST;
		gbc_radioButtonImportFiles.insets = new Insets(0, 0, 0, 5);
		gbc_radioButtonImportFiles.gridx = 0;
		gbc_radioButtonImportFiles.gridy = 2;
		add(radioButtonImportFiles, gbc_radioButtonImportFiles);
		buttonGroup.add(radioButtonImportFiles);
		radioButtonImportFiles.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				List<File> inputFile = getInputFile();
				List<String> list = new LinkedList<>();
				for (File file : inputFile) {
					list.add(file.getAbsolutePath());
				}
				inputFileListConsumer.accept(list);
			}
		});

		questionLabelImportFiles = new JLabel("");
		questionLabelImportFiles.setIcon(EGPSShellIcons.getHelpIcon());
		GridBagConstraints gbc_questionLabelImportFiles = new GridBagConstraints();
		gbc_questionLabelImportFiles.insets = new Insets(0, 0, 0, 5);
		gbc_questionLabelImportFiles.anchor = GridBagConstraints.EAST;
		gbc_questionLabelImportFiles.gridx = 1;
		gbc_questionLabelImportFiles.gridy = 2;
		setTooltipContents4importFiles(Arrays.asList("Please input file with content same with example."));
		add(questionLabelImportFiles, gbc_questionLabelImportFiles);

		jtextAreaWithString_importFiles = new JTextField("Drag file here.");
//		jtextAreaWithString_importFiles.setTextContents(Arrays.asList("Drag file here."));
		jtextAreaWithString_importFiles.setEnabled(false);
		jtextAreaWithString_importFiles.setFont(globalFont);
//		JTextArea textAreaOfFileList = jtextAreaWithString_importFiles.getTextArea();

		JTextAreaTransferHandler newHandler = new JTextAreaTransferHandler(list -> {
			StringJoiner stringJoiner = new StringJoiner("\n");
			for (File file : list) {
				stringJoiner.add(file.getAbsolutePath());
			}
			jtextAreaWithString_importFiles.setText(stringJoiner.toString());
		});

		jtextAreaWithString_importFiles.setTransferHandler(newHandler);

		GridBagConstraints gbc_jtextAreaWithString_importFiles = new GridBagConstraints();
		gbc_jtextAreaWithString_importFiles.insets = new Insets(0, 5, 0, 6);
		gbc_jtextAreaWithString_importFiles.fill = GridBagConstraints.BOTH;
		gbc_jtextAreaWithString_importFiles.gridx = 2;
		gbc_jtextAreaWithString_importFiles.gridy = 2;
		add(jtextAreaWithString_importFiles, gbc_jtextAreaWithString_importFiles);
		
				JButton button4LoadFiles = new JButton("Load");
				button4LoadFiles.setFont(globalFont);
				button4LoadFiles.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {

						EGPSFileChooser egpsFileChooser = new EGPSFileChooser(clz);
						egpsFileChooser.setMultiSelectionEnabled(true);
						int showOpenDialog = egpsFileChooser.showOpenDialog();
						if (showOpenDialog == EGPSFileChooser.APPROVE_OPTION) {
							File[] selectedFiles = egpsFileChooser.getSelectedFiles();

							StringBuilder stringBuilder = new StringBuilder();
							for (File file : selectedFiles) {
								stringBuilder.append(file.getAbsolutePath()).append("\n");
							}
							stringBuilder.deleteCharAt(stringBuilder.length() - 1);
							jtextAreaWithString_importFiles.setText(stringBuilder.toString());

							if (!radioButtonImportFiles.isSelected()) {
								radioButtonImportFiles.setSelected(true);
							}
						}
					}
				});
				
						GridBagConstraints gbc_btn_loadFiles = new GridBagConstraints();
						gbc_btn_loadFiles.gridx = 3;
						gbc_btn_loadFiles.gridy = 2;
						add(button4LoadFiles, gbc_btn_loadFiles);

		addListeners();

	}

	private void addListeners() {
		jtextAreaWithString_directContents.addDocumentListenerWhenContentChange(() -> {

			if (radioButtonImportContent.isSelected()) {
				List<String> asList = Arrays.asList(getDirectInputContent().split("\n"));
				directContentConsumer.accept(asList);
			} else {
				radioButtonImportContent.setSelected(true);
			}

		});
//		jtextAreaWithString_importFiles.addDocumentListenerWhenContentChange(() -> {
//			if (radioButtonImportFiles.isSelected()) {
//
//				String text = jtextAreaWithString_importFiles.getTextArea().getText();
//				List<String> asList = Arrays.asList(text.split("\n"));
//				inputFileListConsumer.accept(asList);
//			} else {
//				radioButtonImportFiles.setSelected(true);
//			}
//		});

	}

	public boolean isImportFiles() {
		return !radioButtonImportContent.isSelected();
	}

	public List<File> getInputFile() {

		List<File> files = new ArrayList<File>();

		if (radioButtonImportFiles.isSelected()) {
			String text = jtextAreaWithString_importFiles.getText();
			if (!text.isEmpty()) {
				String[] textLines = text.split("\n");

				for (String string : textLines) {
					files.add(new File(string));
				}
			}
		}

		return files;
	}

	public String getDirectInputContent() {
		return jtextAreaWithString_directContents.getTextArea().getText();

	}

	public void setTooltipContents4importFiles(List<String> contents) {
		StringBuilder sBuilder = new StringBuilder(8192);

		sBuilder.append("<html><body>");
		for (String string : contents) {
			sBuilder.append(string).append("<br>");
		}

		sBuilder.append("</body></html>");

		questionLabelImportFiles.setToolTipText(sBuilder.toString());
	}

	public void setContentsOfDirectArea(List<String> strs) {
		jtextAreaWithString_directContents.setTextContents(strs);

	}

	public void setTooltipContents4importContent(List<String> contents) {
		StringBuilder sBuilder = new StringBuilder(8192);

		sBuilder.append("<html><body>");
		for (String string : contents) {
			sBuilder.append(string).append("<br>");
		}

//		sBuilder.append("<b>Click to see more details.</b>");
		sBuilder.append("</body></html>");

		questionLabelImportContent.setToolTipText(sBuilder.toString());
	}

	/**
	 * 这个公共的方法可以用来检测输入的有效性。 单个文件的话 Arrays.asList(file)
	 * 
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
				SwingDialog.showErrorMSGDialog("Input error", "File: " + ff.getAbsolutePath() + "\nNot exists!");
				return false;
			}
		}

		return true;
	}

	public static boolean checkContent(String inputString) {
		if (inputString == null || inputString.isEmpty()) {
			SwingDialog.showErrorMSGDialog("Input error", "You have not input contents yet!");
			return false;
		}
		return true;
	}

	public void setDirectContentConsumer(Consumer<List<String>> directContentConsumer) {
		this.directContentConsumer = directContentConsumer;

	}

	public void setDialogContent4inputFormat(List<String> strings) {
		questionLabelImportContent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				SwingDialog.showInformationDialog(new Dimension(800, 600), strings, UnifiedAccessPoint.getInstanceFrame(),
						"Format statement");
			}
		});
	}

	public void setInputdirConsumer(Consumer<File> inputdirConsumer) {
		this.inputdirConsumer = inputdirConsumer;
	}

	public void setInputFileListConsumer(Consumer<List<String>> inputFileListConsumer) {
		this.inputFileListConsumer = inputFileListConsumer;

	}

	public JButton getButtonExample4ImportContent() {
		return buttonExample4ImportContent;
	}
}
