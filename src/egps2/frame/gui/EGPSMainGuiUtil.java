package egps2.frame.gui;

import com.alibaba.fastjson.JSONObject;
import egps2.EGPSProperties;
import egps2.UnifiedAccessPoint;
import egps2.frame.MyFrame;
import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.EGPSFontChooser;
import egps2.utils.common.model.datatransfer.CallBackBehavior;
import egps2.utils.common.util.EGPSShellIcons;
import utils.storage.MapPersistence;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * EGPSMainGuiUtil supports the main eGPS window, actions, or tab management.
 */
public class EGPSMainGuiUtil {

    // Specifies the path to save dialog sizes, note that this should be for common dialogs only
    public static String DIALOG_SAVE_PATH = EGPSProperties.JSON_DIR.concat("/dialog.size.gz");

    /**
     * Retrieves the size of a dialog based on the given key.
     * Note that this should only be used for common dialogs, and the key should be carefully chosen.
     * Warning: Avoid storing too many DialogSizes in this file.
     *
     * @param key The key used to identify the dialog, typically related to the dialog type or name.
     * @return An Optional object that may contain the dialog size information (Rectangle type)
     *         associated with the given key. If no size information is found, returns an empty Optional.
     */
    public static Optional<Rectangle> getDialogSize(String key) {

        Map<String, String> str2strMap = MapPersistence.getStr2strMap(DIALOG_SAVE_PATH);
        String string = str2strMap.get(key);
        if (string == null) {
            return Optional.empty();
        } else {
            Rectangle object = JSONObject.parseObject(string, Rectangle.class);
            return Optional.of(object);
        }
    }

    /**
     * Sets the tooltip for a JComponent, allowing developers to focus on content without adding line breaks manually.
     *
     * @param jComponent The JComponent object for which to set the tooltip.
     * @param contents A list of strings that will be concatenated to form the tooltip content.
     */
    public static void setTooltipContents(JComponent jComponent, List<String> contents) {
        // Use StringBuilder to concatenate tooltip content, initialized with a capacity of 8192
        StringBuilder sBuilder = new StringBuilder(8192);

        // Start building the HTML formatted tooltip content
        sBuilder.append("<html><body>");
        // Iterate through the contents list, appending each string to the StringBuilder and adding an HTML line break
        for (String string : contents) {
            sBuilder.append(string).append("<br>");
        }

        // End the HTML formatted tooltip content
        sBuilder.append("</body></html>");

        // Set the concatenated tooltip content to the jComponent
        jComponent.setToolTipText(sBuilder.toString());
    }

    /**
     * Returns a JLabel with a tooltip and question icon.
     *
     * @param contents A list of text content strings that will be added to the JLabel.
     * @return A JLabel component with an icon and HTML formatted text.
     */
    public static JLabel getFormatStatementJLabel(List<String> contents) {
        // Create a StringBuilder to build the HTML formatted string
        StringBuilder sBuilder = new StringBuilder(8192);
        // Initialize JLabel with a help icon
        JLabel jLabel = new JLabel(EGPSShellIcons.getHelpIcon());

        // Start building the HTML formatted string for JLabel's display content
        sBuilder.append("<html><body>");
        // Iterate through the contents list, appending each string to the StringBuilder and adding an HTML line break
        for (String string : contents) {
            sBuilder.append(string).append("<br>");
        }

        // Complete the HTML string building, closing body and html tags
        sBuilder.append("</body></html>");

        // Set the JLabel text to the built HTML string
        jLabel.setText(sBuilder.toString());

        // Return the formatted JLabel component
        return jLabel;
    }

    /**
     * Creates a button with a question icon. Clicking the button will open a window to display information content.
     * This method achieves this by providing a list of content strings, making it convenient for users to access detailed information.
     *
     * @param contents A list of strings containing the content to be displayed.
     * @return A configured JButton with a question icon that opens a display window when clicked.
     */
    public static JButton getFormatStatementJbutton(List<String> contents) {
        // Call the overloaded method with default window size parameters
        return getFormatStatementJbutton(contents, new Dimension(500, 500));
    }

    /**
     * Creates a JButton to display formatted statements.
     * When clicked, this button opens a dialog displaying the provided content list in a text area.
     *
     * @param contents A list of formatted statement strings to be displayed.
     * @param dim The dimension of the dialog.
     * @return A configured JButton that triggers the display of formatted statements.
     */
    public static JButton getFormatStatementJbutton(List<String> contents, Dimension dim) {
        // Use a large capacity StringBuilder to concatenate HTML formatted strings
        StringBuilder sBuilder = new StringBuilder(8192);
        // Create a JButton with a help icon
        JButton jButton = new JButton(EGPSShellIcons.getHelpIcon());

        // Start concatenating HTML formatted strings, prepared for button tooltip
        sBuilder.append("<html><body>");
        for (String string : contents) {
            sBuilder.append(string).append("<br>");
        }
        sBuilder.append("</body></html>");

        // Temporarily commented out tooltip setting code, possibly for specific reasons
        // jButton.setToolTipText(text);

        // Add an ActionListener to the button, executing code when clicked
        jButton.addActionListener(e -> {
            // Obtain the singleton instance of MyFrame
            MyFrame instance = UnifiedAccessPoint.getInstanceFrame();
            // Create a modal dialog to display formatted statements
            JDialog jDialog = new JDialog(instance, "Format statement", true);
            // Reset StringBuilder, preparing to concatenate non-HTML formatted strings
            sBuilder.setLength(0);
            for (String string : contents) {
                sBuilder.append(string).append("\n");
            }
            // Create a text area to display formatted statements
            JTextArea jTextArea = new JTextArea(sBuilder.toString());
            // Set the text area border to an empty border with no margin
            jTextArea.setBorder(new EmptyBorder(15, 15, 15, 15));
            // Add the text area to the dialog
            jDialog.add(jTextArea);

            // Set the dialog size
            jDialog.setSize(dim);
            // Ensure the dialog is centered relative to the MyFrame instance
            jDialog.setLocationRelativeTo(instance);
            // Display the dialog
            jDialog.setVisible(true);
        });

        // Return the configured button
        return jButton;
    }

    /**
     * This method quickly creates a "Load" button for file import locations.
     * Clicking the button opens a file chooser dialog, and upon selection, the file path is set in the provided text field.
     *
     * @param textField The JTextField where the file path will be displayed.
     * @param clz The class used for context in the file chooser.
     * @return A configured JButton for file loading.
     */
    public static JButton getFileLoadingJButton(JTextField textField, Class<?> clz) {
        JButton jButton = new JButton("Load");
        jButton.setToolTipText("Click to open file chooser.");
        jButton.addActionListener(e -> {
            EGPSFileChooser egpsFileChooser = new EGPSFileChooser(clz);
            int showOpenDialog = egpsFileChooser.showOpenDialog();
            if (showOpenDialog == EGPSFileChooser.APPROVE_OPTION) {
                String absolutePath = egpsFileChooser.getSelectedFile().getAbsolutePath();
                textField.setText(absolutePath.replaceAll("\\\\", "/"));
            }
        });
        return jButton;
    }

    /**
     * This method quickly creates a "Load" button for directory import locations.
     * Clicking the button opens a directory chooser dialog, and upon selection, the directory path is set in the provided text field.
     *
     * @param textField The JTextField where the directory path will be displayed.
     * @param clz The class used for context in the file chooser.
     * @return A configured JButton for directory loading.
     */
    public static JButton getFileDirLoadingJButton(JTextField textField, Class<?> clz) {
        JButton jButton = new JButton("Load");
        jButton.setToolTipText("Click to open file chooser.");
        jButton.addActionListener(e -> {
            EGPSFileChooser egpsFileChooser = new EGPSFileChooser(clz);
            egpsFileChooser.setFileSelectionMode(EGPSFileChooser.DIRECTORIES_ONLY);
            int showOpenDialog = egpsFileChooser.showOpenDialog();
            if (showOpenDialog == EGPSFileChooser.APPROVE_OPTION) {
                String absolutePath = egpsFileChooser.getSelectedFile().getAbsolutePath();
                textField.setText(absolutePath.replaceAll("\\\\", "/"));
            }
        });
        return jButton;
    }

    /**
     * Opens a font chooser dialog. If a font is selected, it sets the font on the provided button and executes a callback behavior.
     *
     * Usage:
     * <pre>
     * fontChanged(btnNumberFont, () -> {
     *     // do something here.
     * });
     * </pre>
     *
     * @param button The JButton on which the selected font will be applied.
     * @param callBackBehavior The callback behavior to execute after a correct click.
     */
    public static void fontChanged(JButton button, CallBackBehavior callBackBehavior) {
        EGPSFontChooser fontChooser = new EGPSFontChooser();
        int result = fontChooser.showDialog(UnifiedAccessPoint.getInstanceFrame());
        if (result == EGPSFontChooser.OK_OPTION) {
            Font font = fontChooser.getSelectedFont();
            button.setFont(font);
            callBackBehavior.doAfterCorrectClick();
        }
    }

    /**
     * Registers an ActionListener on the JDialog to close it when the ESC key is pressed.
     * This makes it convenient to exit the dialog using the ESC key.
     *
     * @param dialog The JDialog to which the escape listener will be added.
     */
    public static void addEscapeListener(final JDialog dialog) {
        ActionListener escListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        };

        dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Registers an ActionListener on the JDialog to close it when the ESC key is pressed.
     * Additionally, it saves the dialog bounds using the provided StringKeySaver before closing.
     *
     * @param dialog The JDialog to which the escape listener will be added.
     * @param saver The StringKeySaver used to save the dialog bounds.
     */
    public static void addEscapeListenerAndSaveBounds(final JDialog dialog, StringKeySaver saver) {
        ActionListener escListener = new ActionListener() {

            private final StringKeySaver saverAction = saver;

            @Override
            public void actionPerformed(ActionEvent e) {
                Optional<String> shouldSave = saverAction.getKeySaver();
                if (shouldSave.isPresent()) {
                    Rectangle bounds = dialog.getBounds();

                    Map<String, String> str2strMap = MapPersistence.getStr2strMap(DIALOG_SAVE_PATH);
                    String jsonString = JSONObject.toJSONString(bounds, false);
                    str2strMap.put(shouldSave.get(), jsonString);
                    MapPersistence.storeStr2strMap(str2strMap, DIALOG_SAVE_PATH);
                }

                dialog.dispose();
            }
        };

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                escListener.actionPerformed(null);
            }
        });

        dialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }


    /**
     * Configures high-quality rendering settings for a Graphics2D object.
     *
     * @param graphics2D The Graphics2D object to be configured.
     */
    public static void setupHighQualityRendering(Graphics2D graphics2D) {
        EGPSSwingUtil.setupHighQualityRendering(graphics2D);
    }


    public static void drawStringAtCenter(Graphics2D g2d,String text) {
        Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
        g2d.setFont(defaultTitleFont.deriveFont(20f));

        Font font = g2d.getFont();
        FontMetrics fm = g2d.getFontMetrics(font);
        int textWidth  = fm.stringWidth(text);
        int textHeight = fm.getHeight();


        // ② Retrieve the current drawing area (clipping area) - usually the canvas size,
        //    如果外层做过 translate/scale 也会自动考虑
        Rectangle clip = g2d.getClipBounds();

        // ③ 计算左上角坐标，让文本中心落在 clip 中心
        int x = clip.x + (clip.width  - textWidth)  / 2;
        int y = clip.y + (clip.height - textHeight) / 2 + fm.getAscent(); // y 是 baseline

        g2d.drawString(text, x, y);
    }
    /**
     * Draws a string prompting the user to import data, using the default title font.
     *
     * @param g2d The Graphics2D object on which to draw the string.
     */
    public static void drawLetUserImportDataString(Graphics2D g2d) {
        String text = "Please click the import button in the toolbar to load data";
        drawStringAtCenter(g2d, text);
    }

}
