package egps2.builtin.modules.largetextedi.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
/**
 * EditorJDialog belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class EditorJDialog extends JDialog {

	public EditorJDialog(JFrame owner, String title) {
		super(owner, title, true);

	}

	@Override
	protected JRootPane createRootPane() {
		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		JRootPane rootPane = new JRootPane();
		rootPane.registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				escapeKeyProc();
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		return rootPane;
	}

	/**
	 * 处理ESCAPE按键。子类可以重新覆盖该方法，实现自己的处理方式。
	 */
	protected void escapeKeyProc() {
		dispose();
	}

}
