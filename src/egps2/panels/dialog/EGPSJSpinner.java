package egps2.panels.dialog;


import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * @author YFQ
 * @date 2019-04-24 16:15:38
 * @version 1.0
 *          <p>
 *          Description:
 *          </p>
 */
public class EGPSJSpinner extends javax.swing.JSpinner {

	private static final long serialVersionUID = 2068864312289704281L;

	public EGPSJSpinner(int currentValue, int minValue, int maxValue, int steps) {
		SpinnerNumberModel model = new SpinnerNumberModel(currentValue, minValue, maxValue, steps);

		super.setModel(model);
		// javax.swing.JSpinner jSpinner = new javax.swing.JSpinner(numberModel);
		NumberEditor editor = new NumberEditor(this, "0");
		this.setEditor(editor);
		JFormattedTextField textField = ((NumberEditor) this.getEditor()).getTextField();
		textField.setHorizontalAlignment(JTextField.LEFT);
		textField.setEditable(true);

		DefaultFormatterFactory factory = (DefaultFormatterFactory) textField.getFormatterFactory();
		NumberFormatter formatter = (NumberFormatter) factory.getDefaultFormatter();
		formatter.setAllowsInvalid(false);
	}

	public EGPSJSpinner(double currentValue, double minValue, double maxValue, double steps) {
		SpinnerNumberModel model = new SpinnerNumberModel(currentValue, minValue, maxValue, steps);
		super.setModel(model);
		NumberEditor editor = new NumberEditor(this, "0.00");
		this.setEditor(editor);
		JFormattedTextField textField = ((NumberEditor) this.getEditor()).getTextField();
		textField.setHorizontalAlignment(JTextField.LEFT);
		textField.setEditable(true);
		DefaultFormatterFactory factory = (DefaultFormatterFactory) textField.getFormatterFactory();
		NumberFormatter formatter = (NumberFormatter) factory.getDefaultFormatter();
		formatter.setAllowsInvalid(false);
	}

}
