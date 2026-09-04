package egps2.builtin.modules.filemanager;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import egps2.UnifiedAccessPoint;

/**
 * FileManagerFileDetailsDialog belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class FileManagerFileDetailsDialog {

	Font font = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

	public void showCustomFileDetailsDialog(JFrame parent, File selectedValue) {
        if (selectedValue != null && selectedValue.exists()) {
            JDialog dialog = new JDialog(parent, "File Details", true);
            dialog.setLayout(new BorderLayout());

            JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 10));
            panel.setLayout(new GridLayout(0, 2, 5, 5));

            // Add file information to the panel
            addDetail(panel, "File Name:", selectedValue.getName());
            addDetail(panel, "Absolute Path:", selectedValue.getAbsolutePath());
            addDetail(panel, "File Size:", selectedValue.length() + " bytes");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            addDetail(panel, "Last Modified:", sdf.format(new Date(selectedValue.lastModified())));

            try {
                Path path = selectedValue.toPath();
                BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                addDetail(panel, "Creation Time:", sdf.format(new Date(attrs.creationTime().toMillis())));
            } catch (IOException e) {
                addDetail(panel, "Creation Time:", "Not available");
            }

            if (selectedValue.isDirectory()) {
                addDetail(panel, "Type:", "Directory");
            } else {
                addDetail(panel, "Type:", "File");
                addDetail(panel, "Readable:", String.valueOf(selectedValue.canRead()));
                addDetail(panel, "Writable:", String.valueOf(selectedValue.canWrite()));
                addDetail(panel, "Hidden:", String.valueOf(selectedValue.isHidden()));
                addDetail(panel, "Executable:", String.valueOf(selectedValue.canExecute()));
            }

            // Add close button
            JButton closeButton = new JButton("Close");
			closeButton.setFont(font);
            closeButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
			JLabel comp = new JLabel("File info.", SwingConstants.RIGHT);
			comp.setFont(font);
			panel.add(comp);
            panel.add(closeButton);

            // Add panel to the dialog
            dialog.add(panel, BorderLayout.CENTER);
			dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(parent);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(parent, "Please select a valid file or directory", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

	private void addDetail(JPanel panel, String label, String value) {
        JLabel comp = new JLabel(label, SwingConstants.RIGHT);
		comp.setFont(font);
		panel.add(comp);
        JTextArea textArea = new JTextArea(value);
		textArea.setFont(font);
		textArea.setEditable(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        panel.add(textArea);
    }
}
