package egps2.panels.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import egps2.UnifiedAccessPoint;
import egps2.frame.MyFrame;
import egps2.frame.gui.EGPSMainGuiUtil;
import egps2.panels.dialog.modern.ModernDialog;
import egps2.panels.dialog.modern.ModernDialogType;

/**
 * Utility class for showing various dialog types.
 * All dialogs use ModernDialog for consistent styling.
 *
 *   | 类型    | 无 owner                         | 有 owner                                |
 *   |---------|----------------------------------|-----------------------------------------|
 *   | Info    | showInfoMSGDialog(title, msg)    | showInfoMSGDialog(owner, title, msg)    |
 *   | Success | showSuccessMSGDialog(title, msg) | showSuccessMSGDialog(owner, title, msg) |
 *   | Warning | showWarningMSGDialog(title, msg) | showWarningMSGDialog(owner, title, msg) |
 *   | Error   | showErrorMSGDialog(title, msg)   | showErrorMSGDialog(owner, title, msg)   |
 *   | Confirm | showConfirmDialog(title, msg)    | showConfirmDialog(owner, title, msg)    |
 *   | Generic | showMSGDialog(title, msg, type)  | showMSGDialog(owner, title, msg, type)  |
 *
 * @author yudalang
 * @since 2018-11-23, 2026-01-04
 * @see ModernDialog
 */
public class SwingDialog {

	// ==================== Info ====================

	public static void showInfoMSGDialog(String title, String message) {
		showDialogInternal(null, title, message, ModernDialogType.INFO);
	}

	public static void showInfoMSGDialog(Window owner, String title, String message) {
		showDialogInternal(owner, title, message, ModernDialogType.INFO);
	}

	// ==================== Success ====================

	public static void showSuccessMSGDialog(String title, String message) {
		showDialogInternal(null, title, message, ModernDialogType.SUCCESS);
	}

	public static void showSuccessMSGDialog(Window owner, String title, String message) {
		showDialogInternal(owner, title, message, ModernDialogType.SUCCESS);
	}

	// ==================== Warning ====================

	public static void showWarningMSGDialog(String title, String message) {
		showDialogInternal(null, title, message, ModernDialogType.WARNING);
	}

	public static void showWarningMSGDialog(Window owner, String title, String message) {
		showDialogInternal(owner, title, message, ModernDialogType.WARNING);
	}

	// ==================== Error ====================

	public static void showErrorMSGDialog(String title, String message) {
		showDialogInternal(null, title, message, ModernDialogType.ERROR);
	}

	public static void showErrorMSGDialog(Window owner, String title, String message) {
		showDialogInternal(owner, title, message, ModernDialogType.ERROR);
	}

	// ==================== Confirm ====================

	public static boolean showConfirmDialog(String title, String message) {
		return ModernDialog.showConfirm(title, message);
	}

	public static boolean showConfirmDialog(Window owner, String title, String message) {
		return ModernDialog.builder()
				.owner(owner)
				.type(ModernDialogType.QUESTION)
				.title(title)
				.message(message)
				.yesNoButtons()
				.build()
				.showAndGetResult() == ModernDialog.DialogResult.YES;
	}

	// ==================== Generic (for legacy JOptionPane type) ====================

	public static void showMSGDialog(String title, String message, int messageType) {
		showDialogInternal(null, title, message, mapMessageType(messageType));
	}

	public static void showMSGDialog(Window owner, String title, String message, int messageType) {
		showDialogInternal(owner, title, message, mapMessageType(messageType));
	}

	// ==================== Convenience ====================

	public static void successExportDataDialog() {
		String title = UnifiedAccessPoint.getResourceString("dialog.info");
		String msg = UnifiedAccessPoint.getResourceString("dialog.msg.export.finish");
		showSuccessMSGDialog(title, msg);
	}

	public static void successFinishActionDialog() {
		String title = UnifiedAccessPoint.getResourceString("dialog.info");
		String msg = UnifiedAccessPoint.getResourceString("dialog.msg.finish");
		showSuccessMSGDialog(title, msg);
	}

	// ==================== Builder ====================

	public static ModernDialog.Builder builder() {
		return ModernDialog.builder();
	}

	// ==================== Special Dialogs ====================

	public static void showInformationDialog(Dimension dim, List<String> content, JFrame jFrame, String title) {
		JDialog jDialog = new JDialog(jFrame, title, true);
		jDialog.setSize(dim);
		jDialog.setLocationRelativeTo(jFrame);
		PureStringsDispalyDialog area4Dialog = new PureStringsDispalyDialog(content, jDialog::dispose);
		jDialog.add(area4Dialog);
		jDialog.setVisible(true);
	}

	public static JDialog QuickWrapperJCompWithDialog(JComponent panel, String title, int w, int h) {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		JDialog jDialog = new JDialog(instanceFrame);
		jDialog.setTitle(title);
		jDialog.add(panel, BorderLayout.CENTER);
		EGPSMainGuiUtil.addEscapeListener(jDialog);
		jDialog.setSize(w, h);
		jDialog.setLocationRelativeTo(instanceFrame);
		jDialog.setVisible(true);
		return jDialog;
	}

	public static JDialog QuickWrapperJCompWithDialog(JComponent panel, String title) {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		JDialog jDialog = new JDialog(instanceFrame);
		jDialog.setTitle(title);
		jDialog.add(panel, BorderLayout.CENTER);
		EGPSMainGuiUtil.addEscapeListener(jDialog);
		jDialog.pack();
		jDialog.setLocationRelativeTo(instanceFrame);
		jDialog.setVisible(true);
		return jDialog;
	}

	public static Optional<String> showInputDialog(Component parent, String initialString) {
		String result = (String) JOptionPane.showInputDialog(
				parent, "Please input the content", "String input",
				JOptionPane.PLAIN_MESSAGE, null, null, initialString);
		if (result == null || result.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(result);
	}

	/**
	 * @deprecated Use {@link #showConfirmDialog(String, String)} instead
	 */
	@Deprecated
	public static boolean showMSGDialog(Component component, String messageConext, String title, Object[] options) {
		int res = JOptionPane.showOptionDialog(component, messageConext, title, JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		return res == JOptionPane.YES_OPTION;
	}

	// ==================== Private ====================

	private static void showDialogInternal(Window owner, String title, String message, ModernDialogType type) {
		SwingUtilities.invokeLater(() -> {
			ModernDialog.Builder builder = ModernDialog.builder()
					.type(type)
					.title(title)
					.message(message)
					.okButton();
			if (owner != null) {
				builder.owner(owner);
			}
			builder.show();
		});
	}

	private static ModernDialogType mapMessageType(int jOptionPaneType) {
		switch (jOptionPaneType) {
			case JOptionPane.ERROR_MESSAGE:   return ModernDialogType.ERROR;
			case JOptionPane.WARNING_MESSAGE: return ModernDialogType.WARNING;
			case JOptionPane.QUESTION_MESSAGE: return ModernDialogType.QUESTION;
			default: return ModernDialogType.INFO;
		}
	}
}
