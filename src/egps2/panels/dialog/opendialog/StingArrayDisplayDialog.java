/**
 * 
 */
package egps2.panels.dialog.opendialog;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;

/**
 * StingArrayDisplayDialog is a reusable Swing panel or dialog within eGPS.
 */
public class StingArrayDisplayDialog extends JDialog {

	private JTextPane jTextField;
	
	public StingArrayDisplayDialog() {
		super(UnifiedAccessPoint.getInstanceFrame(), "String elements", true);
		setSize(180, 550);
		setLocationRelativeTo(UnifiedAccessPoint.getInstanceFrame());
		setContentPane(getJContentPane());
		setResizable(true);
		setIconImage(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());
	}

	private JScrollPane getJContentPane() {
		jTextField = new JTextPane();
		jTextField.setEditable(false);
		return new JScrollPane(jTextField);
	}

	public void setText(String ss) {
		jTextField.setText(ss);
	}
	
}
