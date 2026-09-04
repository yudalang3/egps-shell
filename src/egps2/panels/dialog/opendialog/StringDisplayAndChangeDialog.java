/**
 * 
 */
package egps2.panels.dialog.opendialog;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;

/**
 * StringDisplayAndChangeDialog is a reusable Swing panel or dialog within eGPS.
 */
public class StringDisplayAndChangeDialog extends JDialog {
	private JTextPane jTextField;
	
	CallBackTransferBehevior callBackBehavior;
	
	public StringDisplayAndChangeDialog(CallBackTransferBehevior callBackBehavior) {
		super(UnifiedAccessPoint.getInstanceFrame(), "String content", true);
		setSize(250, 100);
		setLocationRelativeTo(UnifiedAccessPoint.getInstanceFrame());
		setContentPane(getJContentPane());
		setResizable(true);
		setIconImage(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());
		
		this.callBackBehavior = callBackBehavior;
	}

	private JPanel getJContentPane() {
		JPanel jPanel = new JPanel(new BorderLayout());
		jTextField = new JTextPane();
		
		jPanel.add(new JScrollPane(jTextField),BorderLayout.CENTER);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom,BoxLayout.X_AXIS));
		JButton apply = new JButton("Change content");
		bottom.add(apply);
		apply.addActionListener( e ->{
			if (callBackBehavior != null) {
				callBackBehavior.doAfterCorrectClick(this.getText());
			}
			
			this.dispose();
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e ->{
			this.dispose();
		});
		bottom.add(cancel);
		jPanel.add(bottom,BorderLayout.SOUTH);
		
		return jPanel;
	}

	public void setText(String ss) {
		jTextField.setText(ss);
	}
	
	public String getText() {
		return jTextField.getText();
	}
	
}
