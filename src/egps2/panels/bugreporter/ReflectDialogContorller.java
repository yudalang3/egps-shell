package egps2.panels.bugreporter;

import java.util.Map;

import egps2.panels.dialog.SwingDialog;

/**
 * ReflectDialogContorller is a reusable Swing panel or dialog within eGPS.
 */
public class ReflectDialogContorller {

	public void sentEMail(Map<String,String> inforMap) {
		MailUtil.sentEmailWithAnnomusHost(inforMap,"yudalang@ucas.edu.cn");
		
		
		SwingDialog.showInfoMSGDialog("Send successful", "Your message has successfully send to the developers mail, thank you for fead back.");
	}

}
