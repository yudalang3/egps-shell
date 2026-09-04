/**
 * 
 */
package egps2.panels.dialog.opendialog;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import egps2.utils.common.util.EGPSShellIcons;
import egps2.UnifiedAccessPoint;

/**
 * StringSearchDialog is a reusable Swing panel or dialog within eGPS.
 */
public class StringSearchDialog extends JDialog {
	private JTextPane jTextField;
	
	CallBackTransferBehevior callBackBehevior;
	
	public StringSearchDialog(CallBackTransferBehevior callBackBehevior) {
		super(UnifiedAccessPoint.getInstanceFrame(), "Search dialog", true);
		setSize(400, 200);
		setLocationRelativeTo(UnifiedAccessPoint.getInstanceFrame());
		setContentPane(getJContentPane());
		setResizable(true);
		setIconImage(EGPSShellIcons.get("eGPS_logo16x16.png").getImage());
		
		this.callBackBehevior = callBackBehevior;
	}

	private JPanel getJContentPane() {
		JPanel jPanel = new JPanel(new BorderLayout());
		jTextField = new JTextPane();
		
		jPanel.add(new JScrollPane(jTextField),BorderLayout.CENTER);
		
		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom,BoxLayout.X_AXIS));
		JButton apply = new JButton("Search");
		bottom.add(apply);
		apply.addActionListener( e ->{
			if (callBackBehevior != null) {
				callBackBehevior.doAfterCorrectClick(this.getText());
			}
			
			this.dispose();
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(e ->{
			this.dispose();
		});
		
		bottom.add(Box.createHorizontalGlue());
		bottom.add(cancel);
		jPanel.add(bottom,BorderLayout.SOUTH);
		
		jPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 5, 20));
		
		return jPanel;
	}

	public void setText(String ss) {
		jTextField.setText(ss);
	}
	
	public String getText() {
		return jTextField.getText();
	}
	
}
