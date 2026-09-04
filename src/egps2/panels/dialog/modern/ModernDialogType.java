package egps2.panels.dialog.modern;

import javax.swing.*;

/**
 * Enum defining dialog types with standard Swing icons.
 *
 * @author yudalang
 * @since 2.2
 */
public enum ModernDialogType {

    INFO("Information", JOptionPane.INFORMATION_MESSAGE),
    SUCCESS("Success", JOptionPane.INFORMATION_MESSAGE),
    WARNING("Warning", JOptionPane.WARNING_MESSAGE),
    ERROR("Error", JOptionPane.ERROR_MESSAGE),
    QUESTION("Question", JOptionPane.QUESTION_MESSAGE);

    private final String defaultTitle;
    private final int jOptionPaneType;

    ModernDialogType(String defaultTitle, int jOptionPaneType) {
        this.defaultTitle = defaultTitle;
        this.jOptionPaneType = jOptionPaneType;
    }

    public String getDefaultTitle() {
        return defaultTitle;
    }

    /**
     * Gets the standard JOptionPane icon for this dialog type.
     */
    public Icon getStandardIcon() {
        return UIManager.getIcon(getIconKey());
    }

    private String getIconKey() {
        switch (jOptionPaneType) {
            case JOptionPane.ERROR_MESSAGE:
                return "OptionPane.errorIcon";
            case JOptionPane.WARNING_MESSAGE:
                return "OptionPane.warningIcon";
            case JOptionPane.QUESTION_MESSAGE:
                return "OptionPane.questionIcon";
            default:
                return "OptionPane.informationIcon";
        }
    }
}
