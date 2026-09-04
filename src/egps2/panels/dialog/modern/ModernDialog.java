package egps2.panels.dialog.modern;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;
import egps2.frame.gui.EGPSMainGuiUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Simple message dialog using standard Swing components.
 *
 * @author yudalang
 * @since 2.2
 */
public class ModernDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public enum DialogResult {
        OK, CANCEL, YES, NO, CLOSED
    }

    private DialogResult result = DialogResult.CLOSED;
    private JPanel buttonPanel;
    private ModernDialogType dialogType = ModernDialogType.INFO;
    private String dialogTitle;
    private String dialogMessage;

    private ModernDialog(Window owner) {
        super(owner, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        EGPSMainGuiUtil.addEscapeListener(this);
    }

    private void buildUI() {
        setTitle(dialogTitle != null ? dialogTitle : dialogType.getDefaultTitle());

        // Get fonts from LaunchProperty
        LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
        Font titleFont = launchProperty.getDefaultTitleFont();
        Font contentFont = launchProperty.getDialogContentFont();
        Font buttonFont = launchProperty.getDialogButtonFont();

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title label
        if (dialogTitle != null && !dialogTitle.isEmpty()) {
            JLabel titleLabel = new JLabel(dialogTitle);
            titleLabel.setFont(titleFont);
            mainPanel.add(titleLabel, BorderLayout.NORTH);
        }

        // Icon and message panel
        JPanel contentPanel = new JPanel(new BorderLayout(15, 0));

        // Icon (use standard JOptionPane icons)
        JLabel iconLabel = new JLabel(dialogType.getStandardIcon());
        iconLabel.setVerticalAlignment(SwingConstants.TOP);
        contentPanel.add(iconLabel, BorderLayout.WEST);

        // Message
        JTextArea messageArea = new JTextArea(dialogMessage != null ? dialogMessage : "");
        messageArea.setFont(contentFont);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(mainPanel.getBackground());
        messageArea.setBorder(new EmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(400, 120));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Buttons
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        pack();
        setMinimumSize(new Dimension(450, 200));
    }

    private void addButton(String text, ActionListener action, DialogResult dialogResult) {
        LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
        Font buttonFont = launchProperty.getDialogButtonFont();

        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setPreferredSize(new Dimension(100, 35));
        button.addActionListener(e -> {
            result = dialogResult;
            if (action != null) {
                action.actionPerformed(e);
            }
            dispose();
        });
        buttonPanel.add(button);
    }

    public DialogResult getResult() {
        return result;
    }

    public DialogResult showAndGetResult() {
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }

    // ==================== Static Methods ====================

    public static void showInfo(String message) {
        showInfo(null, message);
    }

    public static void showInfo(String title, String message) {
        SwingUtilities.invokeLater(() -> builder().type(ModernDialogType.INFO).title(title).message(message).okButton().show());
    }

    public static void showSuccess(String message) {
        showSuccess(null, message);
    }

    public static void showSuccess(String title, String message) {
        SwingUtilities.invokeLater(() -> builder().type(ModernDialogType.SUCCESS).title(title).message(message).okButton().show());
    }

    public static void showError(String message) {
        showError(null, message);
    }

    public static void showError(String title, String message) {
        SwingUtilities.invokeLater(() -> builder().type(ModernDialogType.ERROR).title(title).message(message).okButton().show());
    }

    public static void showWarning(String message) {
        showWarning(null, message);
    }

    public static void showWarning(String title, String message) {
        SwingUtilities.invokeLater(() -> builder().type(ModernDialogType.WARNING).title(title).message(message).okButton().show());
    }

    public static boolean showConfirm(String message) {
        return showConfirm("Confirm", message);
    }

    public static boolean showConfirm(String title, String message) {
        ModernDialog dialog = builder()
            .type(ModernDialogType.QUESTION)
            .title(title)
            .message(message)
            .yesNoButtons()
            .build();
        dialog.setLocationRelativeTo(dialog.getOwner());
        dialog.setVisible(true);
        return dialog.getResult() == DialogResult.YES;
    }

    public static Builder builder() {
        return new Builder();
    }

    // ==================== Builder ====================

    public static class Builder {
        private Window owner;
        private ModernDialogType type = ModernDialogType.INFO;
        private String title;
        private String message;
        private java.util.List<ButtonConfig> buttons = new java.util.ArrayList<>();

        private static class ButtonConfig {
            String text;
            ActionListener action;
            DialogResult result;
            ButtonConfig(String text, ActionListener action, DialogResult result) {
                this.text = text;
                this.action = action;
                this.result = result;
            }
        }

        public Builder owner(Window owner) { this.owner = owner; return this; }
        public Builder type(ModernDialogType type) { this.type = type; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }

        public Builder okButton() { return okButton(null); }
        public Builder okButton(ActionListener action) {
            buttons.add(new ButtonConfig("OK", action, DialogResult.OK));
            return this;
        }

        public Builder yesNoButtons() {
            buttons.add(new ButtonConfig("Yes", null, DialogResult.YES));
            buttons.add(new ButtonConfig("No", null, DialogResult.NO));
            return this;
        }

        public Builder primaryButton(String text, ActionListener action) {
            buttons.add(new ButtonConfig(text, action, DialogResult.OK));
            return this;
        }

        public Builder secondaryButton(String text, ActionListener action) {
            buttons.add(new ButtonConfig(text, action, DialogResult.CANCEL));
            return this;
        }

        public ModernDialog build() {
            if (owner == null) {
                if (UnifiedAccessPoint.isGULaunched()) {
                    Window mainFrame = UnifiedAccessPoint.getInstanceFrame();
                    if (mainFrame != null && mainFrame.isVisible()) {
                        owner = mainFrame;
                    }
                }
                if (owner == null) {
                    owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow();
                }
            }

            ModernDialog dialog = new ModernDialog(owner);
            dialog.dialogType = type;
            dialog.dialogTitle = title;
            dialog.dialogMessage = message;
            dialog.buildUI();

            if (buttons.isEmpty()) {
                dialog.addButton("OK", null, DialogResult.OK);
            } else {
                for (ButtonConfig config : buttons) {
                    dialog.addButton(config.text, config.action, config.result);
                }
            }
            return dialog;
        }

        public void show() {
            ModernDialog dialog = build();
            dialog.setLocationRelativeTo(dialog.getOwner());
            dialog.setVisible(true);
        }
    }
}
