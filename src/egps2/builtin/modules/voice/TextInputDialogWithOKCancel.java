package egps2.builtin.modules.voice;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.alibaba.fastjson.JSONObject;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;
import egps2.panels.pref.LaunchProperty4ProgramicConfig;

@SuppressWarnings("serial")
/**
 * TextInputDialogWithOKCancel belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class TextInputDialogWithOKCancel extends JDialog {

	private final JTextArea contentPanel = new JTextArea();
	private Runnable callbackFunction;

	/**
	 * Create the dialog.
	 */
	public TextInputDialogWithOKCancel() {
		super(UnifiedAccessPoint.getInstanceFrame(), "Parameter input dialog" , true);
		
		LaunchProperty lauchProperty = UnifiedAccessPoint.getLaunchProperty();
		Font defaultFont = lauchProperty.getDefaultFont();
		getContentPane().setLayout(new BorderLayout());
		
		
		LaunchProperty4ProgramicConfig bean = new LaunchProperty4ProgramicConfig(lauchProperty);
		String jsonString = JSONObject.toJSONString(bean, true);
		contentPanel.setText(jsonString);
		contentPanel.setFont(defaultFont);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JScrollPane jScollPanel = new JScrollPane(contentPanel);
		getContentPane().add(jScollPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			{
				JButton cancelButton = new JButton(UnifiedAccessPoint.getResourceString("general.button.cancel"));
				cancelButton.addActionListener(e -> {
					this.dispose();
				});
				cancelButton.setFont(defaultFont);
				buttonPane.add(cancelButton);
			}
			
			{
				JButton okButton = new JButton(UnifiedAccessPoint.getResourceString("general.button.applyAndClose"));
				okButton.addActionListener(e -> {
					String text = contentPanel.getText();
					LaunchProperty4ProgramicConfig ret = JSONObject.parseObject(text, LaunchProperty4ProgramicConfig.class);
			        copyBean(ret, lauchProperty);
			        this.dispose();
			        if (callbackFunction != null) {
			        	callbackFunction.run();
					}
				});
				okButton.setFont(defaultFont);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		contentPanel.setCaretPosition(0);
	}
	
	public void setCallbackFunction(Runnable callbackFunction) {
		this.callbackFunction = callbackFunction;
	}

	private void copyBean(LaunchProperty4ProgramicConfig from, LaunchProperty to) {
		to.setDefaultFont(from.defaultFont);
		to.setDefaultTitleFont(from.defaultTitleFont);
		to.setSelectedTabTitleFont(from.selectedTabTitleFont);
		to.setUnSelectedTabTitleFont(from.unSelectedTabTitleFont);
		to.setMenuFistLevelFont(from.menuFistLevelFont);
		to.setMenuSecondLevelFont(from.menuSecondLevelFont);
		to.setIconHeight(from.iconHeight);
		to.setIconWidth(from.iconWidth);
		to.setTabIconHeight(from.tabIconHeight);
		to.setTabIconWidth(from.tabIconWidth);
		to.setDocumentFont(from.documentFont);
		
	}

}
