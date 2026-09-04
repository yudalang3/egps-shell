package egps2.builtin.modules.lowtextedi;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;

/**
 * Editor belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class Editor implements DocumentListener {

    JEditorPane textPane;
    private boolean changed = false;
    private File file;

    private JSplitPane main;

    JScrollPane jScrollPane;

    public Editor(String str) {
        textPane = new JEditorPane("text/plain", str);
        textPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

        textPane.setFocusable(true);
        textPane.setFont(defaultFont);
        textPane.getDocument().addDocumentListener(this);

        CtrlPanel ctrlPanel = new CtrlPanel();
        ctrlPanel.setEditorPanel(textPane);

        main = new JSplitPane();
        main.setLeftComponent(ctrlPanel);

        jScrollPane = new JScrollPane(textPane);
        main.setRightComponent(jScrollPane);
        main.setOneTouchExpandable(true);

    }

    public JComponent getMain() {
        return main;
    }


    public void loadFile() {

        EGPSFileChooser dialog = new EGPSFileChooser(getClass());
        dialog.setMultiSelectionEnabled(false);
        try {
            int result = dialog.showOpenDialog(UnifiedAccessPoint.getInstanceFrame());
            if (result == JFileChooser.CANCEL_OPTION)
                return;
            if (result == JFileChooser.APPROVE_OPTION) {
                if (changed) {
					//询问用户是否真的要放弃
                    boolean confirm = SwingDialog.showMSGDialog(UnifiedAccessPoint.getInstanceFrame(),
                            "Current content has been modified. Are you sure you want to discard changes and load a new file?",
                            "Confirm Discard Changes",
                            new Object[]{"Yes", "No"});

                    if (!confirm) {
                        return;
                    }
                }
                file = dialog.getSelectedFile();

                // 获取文件大小，单位为字节
                long fileSizeInBytes = file.length();
                // 将文件大小转换为 MB
                long fileSizeInMB = fileSizeInBytes / (1024 * 1024);

                // 判断文件是否大于 20MB
                if (fileSizeInMB > 20) {
                    SwingDialog.showInfoMSGDialog("File too big",
                            "Please use large text view module to load file.\nFile too big.");
                    return;
                }

                textPane.setText(readFile(file));
                changed = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String readFile(File file) {
        StringBuilder result = new StringBuilder();
        try (FileReader fr = new FileReader(file); BufferedReader reader = new BufferedReader(fr);) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Cannot read file !", "Error !", JOptionPane.ERROR_MESSAGE);
        }
        return result.toString();
    }

    public String saveAs(ImportDataInfo importDataInfo) {
        Optional<File> inputFile = importDataInfo.getInputFile();
        String defaultPath;
        if (inputFile.isPresent()) {
            defaultPath = inputFile.get().getAbsolutePath();
        } else {
            defaultPath = System.getProperty("user.home");
        }
        JFileChooser dialog = new JFileChooser(defaultPath);
        dialog.setDialogTitle("Save as");
        int result = dialog.showSaveDialog(UnifiedAccessPoint.getInstanceFrame());
        if (result != JFileChooser.APPROVE_OPTION)
            return null;
        file = dialog.getSelectedFile();
        String retText = null;
        try (PrintWriter writer = new PrintWriter(file);) {
            retText = textPane.getText();
            writer.write(retText);
            changed = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return retText;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        changed = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        changed = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        changed = true;
    }

    public String getText() {
        return textPane.getText();
    }

    public void focusMe() {
        SwingUtilities.invokeLater(() -> {
            // 确保父容器也是可见的
            if (main.getParent() != null) {
                main.getParent().setVisible(true);
            }
            main.setVisible(true);
            jScrollPane.setVisible(true);
            // 先让父容器获得焦点
            main.requestFocusInWindow();
            // 然后让textPane获得焦点
            textPane.requestFocusInWindow();

            // 设置插入符
            textPane.setCaretPosition(0);
            textPane.getCaret().setVisible(true);

            // 滚动到顶部
            textPane.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        });
    }
}
