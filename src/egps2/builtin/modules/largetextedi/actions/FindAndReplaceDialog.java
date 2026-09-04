package egps2.builtin.modules.largetextedi.actions;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.largetextedi.TextEditorMain;
import egps2.builtin.modules.largetextedi.gui.EditorScrollBar;
import egps2.builtin.modules.largetextedi.gui.EgpsTextPane;
import egps2.builtin.modules.largetextedi.gui.TextEditorDataManager;
import egps2.builtin.modules.largetextedi.gui.TextEditorViewPort;
import egps2.builtin.modules.largetextedi.model.EditorCaret;
import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.ReplaceAllAction;
import egps2.builtin.modules.largetextedi.model.SelectEditor;
import egps2.builtin.modules.largetextedi.util.EditorJDialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates and open the template
 * in the editor.
 */
/**
 * FindAndReplaceDialog belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class FindAndReplaceDialog extends EditorJDialog implements ActionListener {

    /**
     *
     */
    private static final long serialVersionUID = -2491047383880965206L;

    private static final String TILTLE = "Find/Replace";

    private Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

    private final String FIND = "Find";
    private final String REPLACEORFIND = "Replace/Find";
    private final String REPLACE = "Replace";
    private final String REPLACEALL = "Replace All";
    private final String CANCEL = "Cancel";

    private TextEditorMain textEditorMain;

    private EgpsTextPane egpsTextPane;

    private JPanel contentPane;

    private JTextField findField;

    private JTextField replaceField;

    private JCheckBox caseSensitive;

    private JCheckBox wrapSearch;

    private JCheckBox wholeWord;

    private JCheckBox incremental;

    private JCheckBox regularExpressions;

    private JButton findButton;

    private JButton replaceOrFindButton;

    private JButton replaceButton;

    private JButton replaceAllButton;

    private JButton cancelButton;

    private JLabel notFound;

    public FindAndReplaceDialog(JFrame owner, TextEditorMain textEditorMain) {
        super(owner, TILTLE);
        this.textEditorMain = textEditorMain;

        this.egpsTextPane = textEditorMain.getEgpsTextPane();
        JPanel mainPanel = getMainPanel();
		setContentPane(mainPanel);

        setIconImage(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setSize(480, 400);
        
        setLocationRelativeTo(owner);
        setVisible(true);

    }

    public JButton getReplaceOrFindButton() {
        return replaceOrFindButton;
    }

    public void setReplaceOrFindButton(JButton replaceOrFindButton) {
        this.replaceOrFindButton = replaceOrFindButton;
    }

    public JButton getReplaceButton() {
        return replaceButton;
    }

    public void setReplaceButton(JButton replaceButton) {
        this.replaceButton = replaceButton;
    }

    public JButton getReplaceAllButton() {
        return replaceAllButton;
    }

    public void setReplaceAllButton(JButton replaceAllButton) {
        this.replaceAllButton = replaceAllButton;
    }

    public JLabel getNotFound() {
        return notFound;
    }

    public void setNotFound(JLabel notFound) {
        this.notFound = notFound;
    }

    public TextEditorMain getTextEditorMain() {
        return textEditorMain;
    }

    public JTextField getFindField() {
        return findField;
    }

    public void setFindField(JTextField findField) {
        this.findField = findField;
    }

    public JCheckBox getCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(JCheckBox caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public JCheckBox getWrapSearch() {
        return wrapSearch;
    }

    public void setWrapSearch(JCheckBox wrapSearch) {
        this.wrapSearch = wrapSearch;
    }

    public JCheckBox getWholeWord() {
        return wholeWord;
    }

    public void setWholeWord(JCheckBox wholeWord) {
        this.wholeWord = wholeWord;
    }

    public JCheckBox getIncremental() {
        return incremental;
    }

    public void setIncremental(JCheckBox incremental) {
        this.incremental = incremental;
    }

    public JCheckBox getRegularExpressions() {
        return regularExpressions;
    }

    public void setRegularExpressions(JCheckBox regularExpressions) {
        this.regularExpressions = regularExpressions;
    }

    private JPanel getMainPanel() {

        if (contentPane == null) {

            contentPane = new JPanel(null);

            JLabel find = new JLabel("Find:");
            find.setFont(defaultFont);
            find.setBounds(25, 10, 30, 25);
            contentPane.add(find);

            findField = new JTextField(24);
            findField.setFont(defaultFont);

            findField.setBounds(120, 10, 257, 25);
            contentPane.add(findField);

            JLabel replace = new JLabel("Replace with:");
            replace.setFont(defaultFont);
            replace.setBounds(25, 40, 90, 25);
            contentPane.add(replace);

            replaceField = new JTextField(24);
            replaceField.setFont(defaultFont);
            replaceField.setBounds(120, 40, 257, 25);
            contentPane.add(replaceField);

            JPanel optionsPanel = getOptionsPanel();
            optionsPanel.setBounds(20, 70, 360, 115);
            contentPane.add(optionsPanel);

            notFound = new JLabel("String Not Found");
            notFound.setFont(defaultFont);
            notFound.setVisible(false);

            notFound.setBounds(10, 260, 100, 40);
            contentPane.add(notFound);

            JPanel buttonPanel = getButtonPanel();
            buttonPanel.setBounds(105, 200, 300, 100);
            contentPane.add(buttonPanel);

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

                    if (incremental.isSelected() && incremental.isEnabled()) {
                        lock.lock();
                        findElement();
                        lock.unlock();
                        // }).start();
                    }

                    String queryContent = findField.getText();

                    if (queryContent.length() <= 0) {
                        setEnabledButton(false);
                    } else {
                        String selectedText = egpsTextPane.getSelectedText();
                        if (!queryContent.equalsIgnoreCase(selectedText)) {
                            replaceOrFindButton.setEnabled(false);
                            replaceButton.setEnabled(false);
                        }
                        findButton.setEnabled(true);
                        replaceAllButton.setEnabled(true);
                    }
                }
            });

            String selectedText = textEditorMain.getEgpsTextPane().getSelectedText();

            if (selectedText != null && !selectedText.equals("")) {
                findField.setText(selectedText);
            } else {
                replaceOrFindButton.setEnabled(false);

                replaceButton.setEnabled(false);
            }

        }

        return contentPane;

    }

    private JPanel getOptionsPanel() {

        JPanel optionsPanel = new JPanel(null);
        optionsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.lightGray, 1),
                "Options", TitledBorder.LEFT, TitledBorder.TOP, defaultFont));

        caseSensitive = new JCheckBox("Case sensitive");
        caseSensitive.setFocusable(false);
        caseSensitive.setFont(defaultFont);
        caseSensitive.setBounds(10, 15, 120, 30);
        optionsPanel.add(caseSensitive);

        wrapSearch = new JCheckBox("Wrap search");
        wrapSearch.setFocusable(false);
        wrapSearch.setFont(defaultFont);
        wrapSearch.setSelected(true);
        wrapSearch.setBounds(130, 15, 120, 30);
        optionsPanel.add(wrapSearch);

        wholeWord = new JCheckBox("Whole word");
        wholeWord.setFocusable(false);
        wholeWord.setFont(defaultFont);
        wholeWord.setBounds(10, 45, 100, 30);
        optionsPanel.add(wholeWord);

        incremental = new JCheckBox("Incremental");
        incremental.setFocusable(false);
        incremental.setFont(defaultFont);
        incremental.setBounds(130, 45, 100, 30);
        optionsPanel.add(incremental);

        regularExpressions = new JCheckBox("Regular expressions");
        regularExpressions.setFocusable(false);
        regularExpressions.setFont(defaultFont);
        regularExpressions.setBounds(10, 75, 150, 30);
        optionsPanel.add(regularExpressions);

        regularExpressions.addChangeListener(e -> {

            if (regularExpressions.isSelected()) {

                wholeWord.setEnabled(false);

                incremental.setEnabled(false);
            } else {
                wholeWord.setEnabled(true);

                incremental.setEnabled(true);
            }

        });

        return optionsPanel;
    }

    private JPanel getButtonPanel() {

        JPanel buttonPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.insets = new Insets(0, 0, 5, 5);

        gridBagConstraints.anchor = GridBagConstraints.EAST;

        findButton = new JButton(FIND);

        findButton.setEnabled(false);

        findButton.setPreferredSize(new Dimension(120, 23));

        getRootPane().setDefaultButton(findButton);

        findButton.addActionListener(this);

        findButton.setFont(defaultFont);

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 0;

        buttonPanel.add(findButton, gridBagConstraints);

        replaceOrFindButton = new JButton(REPLACEORFIND);

        replaceOrFindButton.setPreferredSize(new Dimension(120, 23));

        replaceOrFindButton.addActionListener(this);

        replaceOrFindButton.setFont(defaultFont);

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 0;

        buttonPanel.add(replaceOrFindButton, gridBagConstraints);

        replaceButton = new JButton(REPLACE);

        replaceButton.setPreferredSize(new Dimension(120, 23));

        replaceButton.addActionListener(this);

        replaceButton.setFont(defaultFont);

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 1;

        buttonPanel.add(replaceButton, gridBagConstraints);

        replaceAllButton = new JButton(REPLACEALL);
        replaceAllButton.setEnabled(false);
        replaceAllButton.setPreferredSize(new Dimension(120, 23));

        replaceAllButton.addActionListener(this);

        replaceAllButton.setFont(defaultFont);

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 1;

        buttonPanel.add(replaceAllButton, gridBagConstraints);

        cancelButton = new JButton(CANCEL);

        cancelButton.addActionListener(this);

        cancelButton.setPreferredSize(new Dimension(120, 23));

        cancelButton.addActionListener(this);

        cancelButton.setFont(defaultFont);

        gridBagConstraints.gridx = 1;

        gridBagConstraints.gridy = 2;

        buttonPanel.add(cancelButton, gridBagConstraints);

        return buttonPanel;
    }

    public volatile ReentrantLock lock = new ReentrantLock();

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();

        setCursor(new Cursor(Cursor.WAIT_CURSOR));

        if (actionCommand.equalsIgnoreCase(FIND)) {
            new Thread(() -> {
                try {
                    egpsTextPane.lock();
                    //   setEnabledButton(false);
                    findElement();
                    setCursor(Cursor.getDefaultCursor());
//                    BioMainFrame.getInstance().updateMenuItems();
                    textEditorMain.updateItem();
                    egpsTextPane.requestFocus();
                    egpsTextPane.unlock();
                } catch (Exception e1) {
                    return;
                }

                ///  setEnabledButton(true);
            }, String.valueOf("editor find")).start();
        }

        if (actionCommand.equalsIgnoreCase(REPLACEORFIND)) {
            new Thread(() -> {
                lock.lock();
                setEnabledButton(false);
                String replaceText = replaceField.getText();
                String selectedText = egpsTextPane.getSelectedText();
                String text = findField.getText();
                if (text != null && !"".equals(text)) {
                    if (selectedText.equalsIgnoreCase(text)) {
                        egpsTextPane.replaceSelection(replaceText);
                        egpsTextPane.clearSelection();
                        findElement();
                        setCursor(Cursor.getDefaultCursor());
//                        BioMainFrame.getInstance().updateMenuItems();
                        textEditorMain.updateItem();
                        egpsTextPane.requestFocus();
                    }
                }
                lock.unlock();
                setEnabledButton(true);
            }, String.valueOf("editor Replace/Find")).start();

        }

        if (actionCommand.equalsIgnoreCase(REPLACE)) {

            // new Thread(() -> {
            String replaceText = replaceField.getText();

            SelectEditor selectEditor = egpsTextPane.getSelectEditor();

            EditorCaret editorCaret = egpsTextPane.getEditorCaret();

            int selectionStart = selectEditor.getSelectionStart();

            int startLineNumber = selectEditor.getStartLineNumber();

            int endLineNumber = selectEditor.getEndLineNumber();

            int selectionStart2 = egpsTextPane.getSelectionStart();

            egpsTextPane.replaceSelection(replaceText);

            egpsTextPane.clearSelection();

            egpsTextPane.select(selectionStart2, selectionStart2 + replaceText.length());

            selectEditor.setSelectionStart(selectionStart);

            selectEditor.setSelectionEnd(selectionStart + replaceText.length());

            selectEditor.setStartLineNumber(startLineNumber);

            selectEditor.setEndLineNumber(endLineNumber);

            editorCaret.setStartPosition(textEditorMain.getTextEditorViewPort().getStartPosition());
            setCursor(Cursor.getDefaultCursor());

//            BioMainFrame.getInstance().updateMenuItems();

            textEditorMain.updateItem();

            egpsTextPane.requestFocus();
            // }).start();
        }

        if (actionCommand.equalsIgnoreCase(REPLACEALL)) {

            new Thread(() -> {
                lock.lock();
                setEnabledButton(false);
                replaceAll();
                setCursor(Cursor.getDefaultCursor());
//                BioMainFrame.getInstance().updateMenuItems();
                textEditorMain.updateItem();
                egpsTextPane.requestFocus();
                lock.unlock();
                setEnabledButton(true);
            }, String.valueOf("editor ReplaceAll")).start();
        }

        if (actionCommand.equalsIgnoreCase(CANCEL)) {
            new Thread(() -> {
                isStop = true;
                this.dispose();// 对话框销毁
                setCursor(Cursor.getDefaultCursor());
//                BioMainFrame.getInstance().updateMenuItems();
                textEditorMain.updateItem();
                egpsTextPane.requestFocus();
            }, String.valueOf("editor cancel")).start();
        }
    }

    private boolean isStop;

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean isStop) {
        this.isStop = isStop;
    }

    private void findElement() {
        Element root = egpsTextPane.getDocument().getDefaultRootElement();
        int dot = egpsTextPane.getCaret().getDot();
        int lineNumber = root.getElementIndex(dot);
        TextEditorViewPort textEditorViewPort = textEditorMain.getTextEditorViewPort();
        int startOffset = dot - root.getElement(lineNumber).getStartOffset();
        int startPosition = textEditorViewPort.getStartPosition();
        int currentLineNumber = startPosition + lineNumber;
        String queryContent = findField.getText();
        FindElementTask task = new FindElementTask(this, queryContent, currentLineNumber, startOffset);
        task.start();
        try {
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LineObj findLineObj = task.getFindLineObj();
        if (findLineObj != null) {
            EditorScrollBar editorScrollBar = textEditorMain.getEditorScrollBar();
            int currentShowLineNumber = task.getCurrentShowLineNumber();
            editorScrollBar.setValue(currentShowLineNumber);
            Element element = root.getElement(currentShowLineNumber - textEditorViewPort.getStartPosition());
            if (element == null) {
                return;
            }
            SelectEditor selectEditor = egpsTextPane.getSelectEditor();
            int slectStartOffset = element.getStartOffset() + selectEditor.getSelectionStart();
            egpsTextPane.select(slectStartOffset, slectStartOffset + queryContent.length());
            dot = egpsTextPane.getCaret().getDot();
            startOffset = dot - element.getStartOffset();
            if (startOffset != selectEditor.getSelectionStart() || startOffset != selectEditor.getSelectionEnd()) {
                TextEditorDataManager editorDataManager = textEditorMain.getEditorDataManager();
                selectEditor.setThereAnyRemaining(true);

                if (selectEditor.getStartLineNumber() != -1) {
                    StringBuffer remainingCharacters = new StringBuffer();

                    String line1 = editorDataManager.getCurrentDataOfShowLine(selectEditor.getStartLineNumber())
                            .getLine();
                    line1 = editorDataManager.replaceAll(line1);
                    String subString = line1.substring(0, selectEditor.getSelectionStart());

                    remainingCharacters.append(subString);

                    String line2 = editorDataManager.getCurrentDataOfShowLine(selectEditor.getEndLineNumber())
                            .getLine();
                    line2 = editorDataManager.replaceAll(line2);
                    int selectionEnd = selectEditor.getSelectionEnd();
                    if (line2.length() < selectionEnd) {
                        return;
                    }
                    String subString2 = line2.substring(selectionEnd);

                    remainingCharacters.append(subString2);

                    selectEditor.setRemainingCharacters(remainingCharacters.toString());

                }
            }
        } else {
            getNotFound().setVisible(true);
            getReplaceOrFindButton().setEnabled(false);
            getReplaceButton().setEnabled(false);
        }

    }

    private boolean nextFind(String queryContent, int startOffset, int currentLineNumber, Element root,
                             TextEditorViewPort textEditorViewPort, EditorScrollBar editorScrollBar) {

        TextEditorDataManager editorDataManager = textEditorMain.getEditorDataManager();

        LineObj currentDataOfShowLine = editorDataManager.getCurrentDataOfShowLine(currentLineNumber);

        String line = editorDataManager.replaceAll(currentDataOfShowLine.getLine()).substring(startOffset);

        line = caseSensitive.isSelected() ? line : line.toLowerCase();

        queryContent = caseSensitive.isSelected() ? queryContent : queryContent.toLowerCase();
        System.out.println(currentLineNumber);
        return (regularExpressions.isSelected() || wholeWord.isSelected())
                ? isMatcher(queryContent, line, currentLineNumber, startOffset, root, textEditorViewPort,
                editorScrollBar)
                : setSelectElement(queryContent, line, currentLineNumber, startOffset, root, textEditorViewPort,
                editorScrollBar);

    }

    private boolean isMatcher(String regex, CharSequence input, int currentLineNumber, int startOffset, Element root,
                              TextEditorViewPort textEditorViewPort, EditorScrollBar editorScrollBar) {

        String patternStrin = wholeWord.isSelected() && !wholeWord.isEnabled() ? "\\b(" + regex + ")\\b" : regex;

        Pattern pattern = Pattern.compile(patternStrin);

        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {

            editorScrollBar.setValue(currentLineNumber);

            int indexOf = startOffset + matcher.start();

            SelectEditor selectEditor = egpsTextPane.getSelectEditor();

            selectEditor.setSelectionStart(indexOf);

            selectEditor.setSelectionEnd(startOffset + matcher.end());

            selectEditor.setStartLineNumber(currentLineNumber);

            selectEditor.setEndLineNumber(currentLineNumber);

            Element element = root.getElement(currentLineNumber - textEditorViewPort.getStartPosition());

            int selectStartOffset = element.getStartOffset() + indexOf;

            egpsTextPane.select(selectStartOffset, selectStartOffset + matcher.group().length());

            if (startOffset != selectEditor.getSelectionStart() || startOffset != selectEditor.getSelectionEnd()) {
                TextEditorDataManager editorDataManager = textEditorMain.getEditorDataManager();
                selectEditor.setThereAnyRemaining(true);

                if (selectEditor.getStartLineNumber() != -1) {
                    StringBuffer remainingCharacters = new StringBuffer();

                    String line1 = editorDataManager.getCurrentDataOfShowLine(selectEditor.getStartLineNumber())
                            .getLine();
                    line1 = editorDataManager.replaceAll(line1);
                    String subString = line1.substring(0, selectEditor.getSelectionStart());

                    remainingCharacters.append(subString);

                    String line2 = editorDataManager.getCurrentDataOfShowLine(selectEditor.getEndLineNumber())
                            .getLine();
                    line2 = editorDataManager.replaceAll(line2);
                    String subString2 = line2.substring(selectEditor.getSelectionEnd());

                    remainingCharacters.append(subString2);

                    selectEditor.setRemainingCharacters(remainingCharacters.toString());

                }

            }
            return true;
        }

        return false;
    }

    private boolean setSelectElement(String queryContent, String line, int currentLineNumber, int startOffset,
                                     Element root, TextEditorViewPort textEditorViewPort, EditorScrollBar editorScrollBar) {

        if (line.contains(queryContent)) {

            editorScrollBar.setValue(currentLineNumber);

            int length = queryContent.length();

            int indexOf = line.indexOf(queryContent) + startOffset;

            SelectEditor selectEditor = egpsTextPane.getSelectEditor();

            selectEditor.setSelectionStart(indexOf);

            selectEditor.setSelectionEnd(indexOf + length);

            selectEditor.setStartLineNumber(currentLineNumber);

            selectEditor.setEndLineNumber(currentLineNumber);

            Element element = root.getElement(currentLineNumber - textEditorViewPort.getStartPosition());

            int slectStartOffset = element.getStartOffset() + indexOf;

            egpsTextPane.select(slectStartOffset, slectStartOffset + length);

            if (startOffset != selectEditor.getSelectionStart() || startOffset != selectEditor.getSelectionEnd()) {
                TextEditorDataManager editorDataManager = textEditorMain.getEditorDataManager();
                selectEditor.setThereAnyRemaining(true);

                if (selectEditor.getStartLineNumber() != -1) {
                    StringBuffer remainingCharacters = new StringBuffer();

                    String line1 = editorDataManager.getCurrentDataOfShowLine(selectEditor.getStartLineNumber())
                            .getLine();
                    line1 = editorDataManager.replaceAll(line1);
                    String subString = line1.substring(0, selectEditor.getSelectionStart());

                    remainingCharacters.append(subString);

                    String line2 = editorDataManager.getCurrentDataOfShowLine(selectEditor.getEndLineNumber())
                            .getLine();
                    line2 = editorDataManager.replaceAll(line2);
                    String subString2 = line2.substring(selectEditor.getSelectionEnd());

                    remainingCharacters.append(subString2);

                    selectEditor.setRemainingCharacters(remainingCharacters.toString());

                }
            }

            return true;
        }
        return false;

    }

    private void replaceAll() {

        String targetString = findField.getText();

        String replaceWith = replaceField.getText();

        ReplaceAllAction replaceAllAction = new ReplaceAllAction(targetString, replaceWith, caseSensitive.isSelected());

        replaceAllAction.setBelongingGroupIndex(textEditorMain.getEditorDataManager().getListOfTextEditActions()
                .getNewBelongingGroupIndexAssigningValue());

        TextEditorDataManager editorDataManager = this.textEditorMain.getEditorDataManager();

        ListOfTextEditActions listOfTextEditActions = editorDataManager.getListOfTextEditActions();

        listOfTextEditActions.addTextEditActions(replaceAllAction);

        this.textEditorMain.getEgpsTextPane().clearSelection();

        this.textEditorMain.getEditorScrollBar().actionsAfterValueChanged();
    }

    public void setEnabledButton(boolean enabled) {
        findButton.setEnabled(enabled);
        replaceOrFindButton.setEnabled(enabled);
        replaceButton.setEnabled(enabled);
        replaceAllButton.setEnabled(enabled);
    }
}
