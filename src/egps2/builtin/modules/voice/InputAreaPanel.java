package egps2.builtin.modules.voice;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.raven.swing.Button;
import com.raven.swing.ButtonBadges;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import egps2.utils.common.util.EGPSShellIcons;
import egps2.utils.EGPSIconUtil;
import egps2.frame.gui.comp.EGPSJTextArea;
import egps2.UnifiedAccessPoint;
import egps2.frame.MainFrameProperties;
import egps2.frame.MyFrame;

/**
 * InputAreaPanel is a JPanel that provides a user interface for importing
 * data. Users can either input the data directly as text or select files from
 * which the data will be read. This panel supports both methods and provides
 * various UI components for these tasks.
 *
 * <p>
 * This class is part of the edu.sinh.beauty.unisoft.module.voice package.
 * </p>
 *
 * <p>
 * It includes the following features:
 * </p>
 * <ul>
 * <li>Radio buttons to select between importing content directly or importing
 * from files.</li>
 * <li>Text area for inputting content directly.</li>
 * <li>Text field for inputting file paths.</li>
 * <li>Buttons and other UI components to enhance user interaction.</li>
 * </ul>
 *
 * <p>
 * The class also includes methods for handling user inputs, validating inputs,
 * and setting tooltips.
 * </p>
 *
 * <p>
 * <b>Note:</b> This class requires various external libraries such as Apache
 * Commons IO and custom GUI components.
 * </p>
 *
 * <p>
 * <b>Author:</b> yudal
 * </p>
 *
 * @since 1.0
 */
class InputAreaPanel extends JPanel {
    private final VersatileOpenInputClickAbstractGuiBase voiceImportHandler;

    private final JTextArea jTextAreaDirectImport;

    private final ButtonBadges exampleButtonBadges;

    DocumentListener jTextAreaDirectImportDocumentListener = new DocumentListener() {
        public void insertUpdate(DocumentEvent e) {
            updateNode();
        }

        public void removeUpdate(DocumentEvent e) {
            updateNode();
        }

        /**
         * ⚠️ changedUpdate(DocumentEvent e)
         * 通常只有在 富文本（StyledDocument） 中，属性（如字体、颜色）发生变化 时才会触发。
         *
         * 对普通的 JTextArea，该方法通常不会被调用。
         *
         * JTextArea 使用的是 PlainDocument，而 changedUpdate() 默认不触发。
         *
         * 如果你在用 JTextPane 或 JEditorPane（带样式的），改变文本样式时可能触发此方法。
         * @param e the document event
         */
        public void changedUpdate(DocumentEvent e) {
            updateNode();
        }

        private void updateNode() {
            voiceImportHandler.getEventUniformlyProcessor().inputDataAreaPanelActions.userEditContent();
        }
    };

    /**
     * Constructs a new InputAreaPanel.
     *
     * @param versatileOpenInputClickAbstractGuiBase
     */
    public InputAreaPanel(VersatileOpenInputClickAbstractGuiBase versatileOpenInputClickAbstractGuiBase) {
        this.voiceImportHandler = versatileOpenInputClickAbstractGuiBase;
        Font globalFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

        setBorder(new EmptyBorder(8, 6, 8, 8));
        setLayout(new BorderLayout(10, 10));


        Button questionLabelImportContent = new Button();
        questionLabelImportContent.setIcon(EGPSShellIcons.getHelpIcon());
        questionLabelImportContent.setToolTipText("Click for input format help");
        addHelpButtonListener(questionLabelImportContent, globalFont);


        jTextAreaDirectImport = new EGPSJTextArea();
        jTextAreaDirectImport.setFont(globalFont);

        JScrollPane jScrollPane = new JScrollPane(jTextAreaDirectImport);
        add(jScrollPane, BorderLayout.CENTER);

        JToggleButton checkBox_wrapLine = new JToggleButton("");
        checkBox_wrapLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkBox_wrapLine.setSelected(true);
        checkBox_wrapLine.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        String str = UnifiedAccessPoint.getResourceString("voice.module.tip.wrapline");

        checkBox_wrapLine.setToolTipText(str);
        checkBox_wrapLine.setFocusable(false);
        {
            InputStream tutorialIcon = EGPSShellIcons.getVectorGraphResourceAsStream("WordWrap.svg");
            ImageIcon iconFromSVGByStream = EGPSIconUtil.getIconFromSVGByStream(tutorialIcon, 20, 20);
            checkBox_wrapLine.setIcon(iconFromSVGByStream);
        }

        exampleButtonBadges = new ButtonBadges();
        exampleButtonBadges.setAlignmentX(Component.CENTER_ALIGNMENT);
        int numberOfExamples = voiceImportHandler.getNumberOfExamples();
        /**
         * 所以一定要先设置这个数量才行，如果GUI已经出现了，再设置就晚了。
         */
        exampleButtonBadges.setBadges(numberOfExamples);
        exampleButtonBadges.setToolTipText(
                "<html>Click to see the example provided by the module.<br><br>The number showed right head is the example count.<br>This module provides "
                        + numberOfExamples + " examples.");

        exampleButtonBadges.addActionListener(e -> {
            voiceImportHandler.getEventUniformlyProcessor().inputDataAreaPanelActions.userClickTurnToExamples();
        });

        InputStream tutorialIcon = getClass().getResourceAsStream("images/tutorial.svg");
        ImageIcon iconFromSVGByStream = EGPSIconUtil.getIconFromSVGByStream(tutorialIcon, 20, 20);
        exampleButtonBadges.setIcon(iconFromSVGByStream);


        Button focusButton = new Button();
        focusButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        focusButton.setToolTipText("Trigger focus mode.");
        tutorialIcon = getClass().getResourceAsStream("images/focus.svg");
        iconFromSVGByStream = EGPSIconUtil.getIconFromSVGByStream(tutorialIcon, 20, 20);
        focusButton.setIcon(iconFromSVGByStream);
        addFocusButtonListener(focusButton, globalFont);

        JButton button4LoadFiles = new JButton("Load");
        button4LoadFiles.setFont(globalFont);
        addListener4loadFileButton(versatileOpenInputClickAbstractGuiBase.getClass(), button4LoadFiles);

//        JPanel jPanel = new JPanel();
//        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
//        jPanel.setBorder(BorderFactory.createEmptyBorder());
//        jPanel.add(checkBox_wrapLine);
//        Component verticalStrut = Box.createVerticalStrut(12);
//        jPanel.add(verticalStrut);
//        jPanel.add(focusButton);
//        jPanel.add(questionLabelImportContent);
//        jPanel.add(Box.createVerticalGlue());
//        jPanel.add(exampleButtonBadges);
//        {
//        jPanel.add(button4LoadFiles);
//        }
        // 使用 GridBagLayout 获得更好的控制
        JPanel jPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 2, 0);

        int gridy = 0;
        gbc.gridy = gridy++; jPanel.add(checkBox_wrapLine, gbc);
        gbc.gridy = gridy++; jPanel.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy = gridy++; jPanel.add(questionLabelImportContent, gbc);
        gbc.gridy = gridy++; jPanel.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy = gridy++; jPanel.add(focusButton, gbc);
        gbc.gridy = gridy++; gbc.weighty = 1.0; jPanel.add(Box.createVerticalGlue(), gbc);
        gbc.gridy = gridy++; gbc.weighty = 0; jPanel.add(exampleButtonBadges, gbc);
        gbc.insets = new Insets(2, 0, 4, 0);
        gbc.gridy = gridy++; jPanel.add(button4LoadFiles, gbc);
        add(jPanel, BorderLayout.EAST);

        checkBox_wrapLine.addActionListener(e -> {
            jTextAreaDirectImport.setLineWrap(checkBox_wrapLine.isSelected());
        });


        addListeners();

    }

    private void addListener4loadFileButton(Class<?> clz, JButton button4LoadFiles) {
        button4LoadFiles.addActionListener(e -> {
            EGPSFileChooser egpsFileChooser = new EGPSFileChooser(clz);
            egpsFileChooser.setMultiSelectionEnabled(false);
            int showOpenDialog = egpsFileChooser.showOpenDialog();
            if (showOpenDialog != EGPSFileChooser.APPROVE_OPTION) {
            }
            File selectedFile = egpsFileChooser.getSelectedFile();
            try {
                String str = FileUtils.readFileToString(selectedFile, StandardCharsets.UTF_8);
                jTextAreaDirectImport.setText(str);
                // 会触发监听
            } catch (IOException ex) {
                SwingDialog.showErrorMSGDialog("Import error", "Error loading the file: ".concat(ex.getMessage()));
            }
        });
    }

    public ButtonBadges getExampleButtonBadges() {
        return exampleButtonBadges;
    }

    private void addFocusButtonListener(Button focusButton, Font globalFont) {
        focusButton.addActionListener(e -> {
            JTextArea jTextArea = new EGPSJTextArea();
            jTextArea.setText(this.jTextAreaDirectImport.getText());
            jTextArea.setCaretPosition(0);  // Set cursor to beginning
            jTextArea.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            jTextArea.setFont(globalFont);

            // 添加双击选择功能
            jTextArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent ev) {
                    if (ev.getClickCount() == 2) {
                        selectParameterPartInTextArea(jTextArea);
                    }
                }
            });

            JComponent jcp = MainFrameProperties.autoWrapComponentWithScollPanel(jTextArea);

            String title = "Focus on input content";
            MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
            JDialog jDialog = new JDialog(instanceFrame, true);
            jDialog.setTitle(title);
            jDialog.add(jcp, BorderLayout.CENTER);

            ActionListener escListener = new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    updateJTextAreaDirectImport(jTextArea.getText());
                    jDialog.dispose();
                }
            };

            jDialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);
            jDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    updateJTextAreaDirectImport(jTextArea.getText());
                }
            });

            Component rootPane = SwingUtilities.getRoot(InputAreaPanel.this);
            Dimension parentDim = rootPane.getSize();
            jDialog.setSize(parentDim);
            jDialog.setLocationRelativeTo(instanceFrame);
            jDialog.setVisible(true);

        });
    }

    private void addHelpButtonListener(Button helpButton, Font globalFont) {
        helpButton.addActionListener(e -> {
            InputStream resourceAsStream = getClass().getResourceAsStream("inputContent.html");
            String htmlContent = "";
            try {
                htmlContent = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                ex.printStackTrace();
                htmlContent = "<html><body><p>Failed to load help content.</p></body></html>";
            }

            MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
            JDialog jDialog = new JDialog(instanceFrame, "Input Format Help", true);

            javax.swing.JEditorPane editorPane = new javax.swing.JEditorPane();
            editorPane.setContentType("text/html");
            editorPane.setText(htmlContent);
            editorPane.setEditable(false);
            editorPane.setCaretPosition(0);
            editorPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JScrollPane scrollPane = new JScrollPane(editorPane);
            jDialog.add(scrollPane, BorderLayout.CENTER);

            // ESC to close
            jDialog.getRootPane().registerKeyboardAction(
                    ev -> jDialog.dispose(),
                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW
            );

            jDialog.setSize(800, 550);
            jDialog.setLocationRelativeTo(instanceFrame);
            jDialog.setVisible(true);
        });
    }

    private void addListeners() {
        jTextAreaDirectImport.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // 双击选择参数名或参数值
                if (e.getClickCount() == 2) {
                    selectParameterPart();
                    return;  // 阻止默认的双击选中单词行为
                }

                // Ctrl+Click 打开文件选择器
                if (e.isControlDown()) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(jTextAreaDirectImport);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        jTextAreaDirectImport.insert(selectedFile.getAbsolutePath(),
                                jTextAreaDirectImport.getCaretPosition());
                    }
                }

            }
        });
        jTextAreaDirectImport.getDocument().addDocumentListener(jTextAreaDirectImportDocumentListener);
    }

    /**
     * 双击智能选择参数名或参数值。
     *
     * 对于 "$param1=value1" 格式的行：
     * - 双击 "$param1" 部分 → 选中 "param1"（从 $ 后到 = 前，不包含 $）
     * - 双击 "value1" 部分 → 选中 "value1"（从 = 后到 \n 或行尾）
     */
    private void selectParameterPart() {
        int pos = jTextAreaDirectImport.getCaretPosition();
        String text = jTextAreaDirectImport.getText();

        if (text.isEmpty() || pos < 0 || pos > text.length()) return;

        // 找到当前行的边界
        int lineStart = pos;
        while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
            lineStart--;
        }

        int lineEnd = pos;
        while (lineEnd < text.length() && text.charAt(lineEnd) != '\n') {
            lineEnd++;
        }

        // 找到当前行中 = 的位置
        int equalsPos = -1;
        for (int i = lineStart; i < lineEnd; i++) {
            if (text.charAt(i) == '=') {
                equalsPos = i;
                break;
            }
        }

        // 找到 $ 符号的位置（如果存在）
        int dollarPos = -1;
        for (int i = lineStart; i < lineEnd; i++) {
            if (text.charAt(i) == '$') {
                dollarPos = i;
                break;
            }
        }

        int start, end;
        if (equalsPos == -1) {
            // 行中没有 =，选中整行
            start = lineStart;
            end = lineEnd;
        } else if (pos <= equalsPos) {
            // 光标在 = 前面或在 = 上，选中参数名部分（不包含 $）
            if (dollarPos != -1 && dollarPos < equalsPos) {
                start = dollarPos + 1;  // 从 $ 后面开始
            } else {
                start = lineStart;
            }
            end = equalsPos;
        } else {
            // 光标在 = 后面，选中参数值部分
            start = equalsPos + 1;
            end = lineEnd;
        }

        // 设置选择范围
        if (start < end) {
            jTextAreaDirectImport.setSelectionStart(start);
            jTextAreaDirectImport.setSelectionEnd(end);
        }
    }

    /**
     * 双击智能选择参数名或参数值（用于 Focus mode 对话框中的 JTextArea）
     */
    private void selectParameterPartInTextArea(JTextArea textArea) {
        int pos = textArea.getCaretPosition();
        String text = textArea.getText();

        if (text.isEmpty() || pos < 0 || pos > text.length()) return;

        // 找到当前行的边界
        int lineStart = pos;
        while (lineStart > 0 && text.charAt(lineStart - 1) != '\n') {
            lineStart--;
        }

        int lineEnd = pos;
        while (lineEnd < text.length() && text.charAt(lineEnd) != '\n') {
            lineEnd++;
        }

        // 找到当前行中 = 的位置
        int equalsPos = -1;
        for (int i = lineStart; i < lineEnd; i++) {
            if (text.charAt(i) == '=') {
                equalsPos = i;
                break;
            }
        }

        // 找到 $ 符号的位置（如果存在）
        int dollarPos = -1;
        for (int i = lineStart; i < lineEnd; i++) {
            if (text.charAt(i) == '$') {
                dollarPos = i;
                break;
            }
        }

        int start, end;
        if (equalsPos == -1) {
            // 行中没有 =，选中整行
            start = lineStart;
            end = lineEnd;
        } else if (pos <= equalsPos) {
            // 光标在 = 前面或在 = 上，选中参数名部分（不包含 $）
            if (dollarPos != -1 && dollarPos < equalsPos) {
                start = dollarPos + 1;  // 从 $ 后面开始
            } else {
                start = lineStart;
            }
            end = equalsPos;
        } else {
            // 光标在 = 后面，选中参数值部分
            start = equalsPos + 1;
            end = lineEnd;
        }

        // 设置选择范围
        if (start < end) {
            textArea.setSelectionStart(start);
            textArea.setSelectionEnd(end);
        }
    }


    /**
     * The input content may be a file or a string
     * @return
     * @throws IOException
     */
    public String getInputContent() throws IOException {
        String ret = jTextAreaDirectImport.getText();
        if (ret.trim().isBlank()) {
            throw new IOException("Input content is empty");
        }
        ;

        return ret;
    }

    /**
     * @return 如果输入的内容不为空返回true，否则返回false
     */
    public boolean checkInput() {
        String text = jTextAreaDirectImport.getText();
        if (text.isEmpty()) {
            SwingDialog.showErrorMSGDialog("Input error", "You have not input contents yet!");
            return false;
        }

        return true;
    }

    /**
     * This is only for get the JComponent instance, but not for assign the value.
     * Please use the method updateJTextAreaDirectImport(String text) to assign the
     * @return
     */
    public JTextArea getJTextAreaDirectImport() {
        return jTextAreaDirectImport;
    }

    public void updateJTextAreaDirectImport(String text) {
        jTextAreaDirectImport.getDocument().removeDocumentListener(jTextAreaDirectImportDocumentListener);
        jTextAreaDirectImport.setText(text);
        jTextAreaDirectImport.setCaretPosition(0);
        jTextAreaDirectImport.getDocument().addDocumentListener(jTextAreaDirectImportDocumentListener);
    }


}
