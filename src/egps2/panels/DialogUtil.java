package egps2.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import egps2.UnifiedAccessPoint;
import egps2.frame.MyFrame;

/**
 * DialogUtil is a reusable Swing panel or dialog within eGPS.
 */
public class DialogUtil {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            List<String> stringList = Arrays.asList("Line 1", "Line 2", "Line 3", "Line 4");

            showDialog(stringList);
        });
    }

    public static void showDialog(List<String> stringList) {
        JTextArea textArea = new JTextArea();
        for (String line : stringList) {
            textArea.append(line);
            textArea.append("\n");
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
//        scrollPane.setBorder(null);

        MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
        JDialog dialog = new JDialog(instanceFrame,true);
        dialog.setTitle("Text Content Dialog");
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(scrollPane);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.getContentPane().add(closeButton, "South");

        
        
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(instanceFrame);
        dialog.setVisible(true);
    }
}
