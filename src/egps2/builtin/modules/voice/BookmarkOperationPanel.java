package egps2.builtin.modules.voice;

import java.awt.*;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;
import egps2.utils.EGPSIconUtil;
import graphic.engine.guirelated.ReadOnlyComboBox;

@SuppressWarnings("serial")
/**
 * BookmarkOperationPanel belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class BookmarkOperationPanel extends JPanel {
    private final ReadOnlyComboBox<String> readOnlyComboBox;
    private VersatileOpenInputClickAbstractGuiBase importHandler;

    private final JButton recordJButtonCreateBook;
    private JTextField userInputTextField;
    private boolean documentListenerEnabled = true;

    // Link indicator (replaces ToggleButton)
    private JLabel linkIndicator;
    private ImageIcon linkedIcon;
    private ImageIcon notLinkedIcon;
    private boolean isLinked = false;

    private final String linkedOnButtonText  = "Save & Duplicated node";
    private final String linkedOffButtonText = "Save script to new node";

    private final Border myBorder = BorderFactory.createDashedBorder(Color.black, 1,4,4, true);


    DocumentListener operationPanelTextFieldDocumentListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            updateNode();
        }

        public void removeUpdate(DocumentEvent e) {
            updateNode();
        }

        public void changedUpdate(DocumentEvent e) {
            updateNode();
        }

        private void updateNode() {
            if (!documentListenerEnabled) {
                return;
            }
            if (isLinked) {
                String string1 = getUserInputText4bookmark();
                importHandler.getEventUniformlyProcessor().operationPanelOActions.textFiledContentChanged(string1);
            }
        }
    };


    private EditScriptState editScriptState = EditScriptState.COPY_ON_ENTRY;

    /**
     * Create the panel.
     */
    public BookmarkOperationPanel(VersatileOpenInputClickAbstractGuiBase importHandler) {
        this.importHandler = importHandler;
        setBorder(new EmptyBorder(12, 15, 5, 15));

        Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
        GridBagLayout gridBagLayout = new GridBagLayout();
        setLayout(gridBagLayout);

        // Load link indicator icons
        linkedIcon = new ImageIcon(getClass().getResource("images/linked.png"));
        notLinkedIcon = new ImageIcon(getClass().getResource("images/not_linked.png"));

        // Script status at the beginning (gridx = 0, 1)
        {
            String htmlContent = """
                    <html>The indicator of the input area (script) states.
                    </html>
                    """;
            GridBagConstraints gbc_statusLabel = new GridBagConstraints();
            gbc_statusLabel.anchor = GridBagConstraints.WEST;
            gbc_statusLabel.insets = new Insets(0, 0, 5, 5);
            gbc_statusLabel.gridx = 0;
            gbc_statusLabel.gridy = 0;

            final String[] editScriptStates = EditScriptState.getDescriptionsByIndex();
            readOnlyComboBox = new ReadOnlyComboBox<>(editScriptStates, 1);
            readOnlyComboBox.setToolTipText(htmlContent);

            JLabel statusLabel = new JLabel("Script status :");
            add(statusLabel, gbc_statusLabel);

            GridBagConstraints gbc_comboBox = new GridBagConstraints();
            gbc_comboBox.insets = new Insets(0, 0, 5, 10);
            gbc_comboBox.gridx = 1;
            gbc_comboBox.gridy = 0;
            add(readOnlyComboBox, gbc_comboBox);
        }

        JLabel lblNewLabel = new JLabel(" Name :");
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.WEST;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 2;
        gbc_lblNewLabel.gridy = 0;
        add(lblNewLabel, gbc_lblNewLabel);

        userInputTextField = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.gridwidth = 1;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.fill = GridBagConstraints.BOTH;
        // 重要：扩展比例，1 表示会分配到额外空间
        gbc_textField.weightx = 1.0;
        gbc_textField.weighty = 1.0;
        gbc_textField.gridx = 3;
        gbc_textField.gridy = 0;
        add(userInputTextField, gbc_textField);
        userInputTextField.setColumns(20);

        {
            InputStream tutorialIcon = getClass().getResourceAsStream("images/reset.svg");
            ImageIcon iconFromSVGByStream = EGPSIconUtil.getIconFromSVGByStream(tutorialIcon, 16, 16);
            JButton resetTimeButton = new JButton(iconFromSVGByStream);
            resetTimeButton.setToolTipText("Reset bookmark node name as the current time");
            resetTimeButton.addActionListener(e -> {
                String currentTime = importHandler.dateTimeOperator.getCurrentTime();
                userInputTextField.setText(currentTime);
            });
            resetTimeButton.setFocusable(false);
            GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
            gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 5);
            gbc_btnNewButton_2.gridx = 4;
            gbc_btnNewButton_2.gridy = 0;
            add(resetTimeButton, gbc_btnNewButton_2);
        }

        recordJButtonCreateBook = new JButton(linkedOnButtonText);

        recordJButtonCreateBook.setToolTipText("<html>Save the input content as a record in Bookmark<br>Create a new linked node");
        recordJButtonCreateBook.addActionListener(e -> {
            if (checkText()) {
                String name = userInputTextField.getText();
                importHandler.getEventUniformlyProcessor().operationPanelOActions.saveAndCreateButtonClicked(name);

            }
        });
        GridBagConstraints gbc_buttonCreateBook = new GridBagConstraints();
        gbc_buttonCreateBook.insets = new Insets(0, 0, 5, 5);
        gbc_buttonCreateBook.anchor = GridBagConstraints.WEST;
        gbc_buttonCreateBook.gridx = 5;
        gbc_buttonCreateBook.gridy = 0;
        add(recordJButtonCreateBook, gbc_buttonCreateBook);

        // Link indicator (read-only, replaces ToggleButton)
        linkIndicator = new JLabel(notLinkedIcon);
        String linkIndicatorTooltip = """
                <html><b>Connection Status</b><br><br>
                <b>Blue chain:</b> Connected to a saved bookmark - changes will be auto-saved<br>
                <b>Gray chain:</b> Viewing an example (read-only) - create a copy to save changes
                </html>
                """;
        linkIndicator.setToolTipText(linkIndicatorTooltip);
        linkIndicator.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        GridBagConstraints gbc_linkIndicator = new GridBagConstraints();
        gbc_linkIndicator.insets = new Insets(0, 0, 5, 6);
        gbc_linkIndicator.gridx = 6;
        gbc_linkIndicator.gridy = 0;
        add(linkIndicator, gbc_linkIndicator);

        Component[] components = getComponents();
        for (Component component : components) {
            component.setFont(defaultFont);
        }
        userInputTextField.getDocument().addDocumentListener(operationPanelTextFieldDocumentListener);
    }

    /**
     * Update the link indicator state
     * @param linked true if connected to a user bookmark, false if viewing an example
     */
    public void updateLinkIndicator(boolean linked) {
        this.isLinked = linked;
        if (linked) {
            linkIndicator.setIcon(linkedIcon);
            recordJButtonCreateBook.setText(linkedOnButtonText);
            userInputTextField.setEnabled(true);  // Can edit when linked
        } else {
            linkIndicator.setIcon(notLinkedIcon);
            recordJButtonCreateBook.setText(linkedOffButtonText);
            userInputTextField.setEnabled(false); // Cannot edit when viewing Example
        }
    }

    void action4toggleButtonSelected() throws Exception {
        String userInputText4bookmark = getUserInputText4bookmark();
        if (userInputText4bookmark.isBlank()) {
            userInputText4bookmark = importHandler.dateTimeOperator.getCurrentTime();
        }
        updateJTextFieldContentIfPossible(userInputText4bookmark);
        importHandler.getEventUniformlyProcessor().operationPanelOActions.linkedButtonChanged(true);
    }

    public void updateJTextFieldContent(String str) {
        if (SwingUtilities.isEventDispatchThread()) {
            documentListenerEnabled = false;
            userInputTextField.setText(str);
            documentListenerEnabled = true;
        } else {
            SwingUtilities.invokeLater(() -> {
                documentListenerEnabled = false;
                userInputTextField.setText(str);
                documentListenerEnabled = true;
            });
        }
    }

    public boolean shouldAutoSaveBookmark() {
        return isLinked;
    }

    /**
     * 当用户从 JTree点击历史记录，从而填充输入框内容的时候。需要把auto save关闭。
     * @return
     */
    public void setAutoSaveBookmark2false() {
        updateLinkIndicator(false);
    }

    public void setAutoSaveBookmark2true() {
        updateLinkIndicator(true);
    }

    private boolean checkText() {
        if (getUserInputText4bookmark().isEmpty()) {
            SwingDialog.showErrorMSGDialog("Input error", "Please input the bookmark name.");
            return false;
        }

        return true;
    }

    public String getUserInputText4bookmark() {
        return userInputTextField.getText().trim();
    }

    /**
     * 因为是根据日期来的，所以要看看，是否需要更新
     * 重置名称为日期
     * @param str
     */
    public void updateJTextFieldContentIfPossible(String str) {
        if (str == null) {
            str = getUserInputText4bookmark();
        }
        if (importHandler.dateTimeOperator.isStillUseProgramGeneratedString(str)) {
            updateJTextFieldContent(importHandler.dateTimeOperator.getCurrentTime());
        }
    }

    public JTextField getUserInputTextField() {
        return userInputTextField;
    }

    public JButton getRecordJButtonCreateBook() {
        return recordJButtonCreateBook;
    }

    public JLabel getLinkIndicator() {
        return linkIndicator;
    }

    public boolean isLinked() {
        return isLinked;
    }

    public EditScriptState getInputAreaState() {
        return editScriptState;
    }

    public void setInputAreaState(EditScriptState retState) {
        this.editScriptState = retState;
        readOnlyComboBox.setLockedIndex(retState.getIndex());
    }
}
