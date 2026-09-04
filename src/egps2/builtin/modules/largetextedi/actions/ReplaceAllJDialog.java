package egps2.builtin.modules.largetextedi.actions;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.largetextedi.TextEditorMain;
import egps2.builtin.modules.largetextedi.gui.TextEditorDataManager;
import egps2.builtin.modules.largetextedi.model.ReplaceAllAction;
import egps2.builtin.modules.largetextedi.util.EditorJDialog;

/**
 * Copyright (c) 2019 Chinese Academy of Sciences. All rights reserved.
 * 
 * @ClassName ReplaceAllJDialog
 * 
 * @author mhl
 * 
 * @Date Created on:2019-09-06 14:26
 * 
 */
public class ReplaceAllJDialog extends EditorJDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5406234864870401387L;

	private static final String TILTLE = "Replace All";

	private Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

	private TextEditorMain textEditorMain;

	private JPanel contentPane;

	private JTextField replaceField;

	private JTextField findField;

	public ReplaceAllJDialog(JFrame owner, TextEditorMain textEditorMain) {
		super(owner, TILTLE);
		this.textEditorMain = textEditorMain;
		setSize(400, 230);
		setLocationRelativeTo(owner);
		setContentPane(getMainPanel());
		setIconImage(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JPanel getMainPanel() {

		if (contentPane == null) {

			contentPane = new JPanel(new GridBagLayout());

			GridBagConstraints gridBagConstraints = new GridBagConstraints();

			gridBagConstraints.insets = new Insets(5, 5, 5, 5);

			gridBagConstraints.anchor = GridBagConstraints.WEST;

			JLabel find = new JLabel("Find:");
			find.setFont(defaultFont);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			contentPane.add(find, gridBagConstraints);

			findField = new JTextField(24);
			findField.setFont(defaultFont);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;

			// findField.setText(textEditorMain.getEgpsTextArea().getSelectedText());

			contentPane.add(findField, gridBagConstraints);

			JLabel replace = new JLabel("Replace with:");
			replace.setFont(defaultFont);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 1;
			contentPane.add(replace, gridBagConstraints);

			replaceField = new JTextField(24);
			replaceField.setFont(defaultFont);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 1;
			contentPane.add(replaceField, gridBagConstraints);

			JPanel buttonJPanel = new JPanel(new GridBagLayout());

			gridBagConstraints.anchor = GridBagConstraints.EAST;

			JButton replaceAll = new JButton("Replace All");
			getRootPane().setDefaultButton(replaceAll);
			replaceAll.addActionListener(this);
			replaceAll.setFont(defaultFont);
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			replaceAll.setEnabled(false);
			findField.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					changedUpdate(e);
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					changedUpdate(e);

				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					if (findField.getText().length() <= 0) {
						replaceAll.setEnabled(false);
					} else {
						replaceAll.setEnabled(true);
					}

				}
			});
			findField.setText(textEditorMain.getEgpsTextPane().getSelectedText());

			buttonJPanel.add(replaceAll, gridBagConstraints);

			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(this);
			cancel.setFont(defaultFont);
			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 0;
			buttonJPanel.add(cancel, gridBagConstraints);

			gridBagConstraints.gridx = 1;
			gridBagConstraints.gridy = 2;
			contentPane.add(buttonJPanel, gridBagConstraints);

		}

		return contentPane;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		if (actionCommand.equals("Replace All")) {// 判断是不是确定按钮被点击

			String targetString = findField.getText();

			String replaceWith = replaceField.getText();

			ReplaceAllAction replaceAllAction = new ReplaceAllAction(targetString, replaceWith, false);

			replaceAllAction.setBelongingGroupIndex(textEditorMain.getEditorDataManager().getListOfTextEditActions()
					.getNewBelongingGroupIndexAssigningValue());

			TextEditorDataManager editorDataManager = this.textEditorMain.getEditorDataManager();

			ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

			listOfTextEditActions.addTextEditActions(replaceAllAction);
			this.textEditorMain.getEgpsTextPane().clearSelection();
			this.textEditorMain.getEditorScrollBar().actionsAfterValueChanged();

		} else if (actionCommand.equals("Cancel")) {
			// this.setVisible(false);// 对话框不可见
			this.dispose();// 对话框销毁
		}
//		BioMainFrame.getInstance().updateMenuItems();
	}

}
