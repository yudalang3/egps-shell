package egps2.plugin.fastmodtem.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

/**
 * SimpleLeftControlPanel supports the plugin/template system for extending eGPS.
 */
public class SimpleLeftControlPanel extends JPanel{
	private static final long serialVersionUID = 6360716192941698962L;
	private JButton analysisButton;

	public SimpleLeftControlPanel() {
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(500, 850));
		
		JXTaskPaneContainer jxTaskPaneContainer = new JXTaskPaneContainer();
		jxTaskPaneContainer.setBackground(Color.WHITE);
		jxTaskPaneContainer.setBackgroundPainter(null);
		
		
		addJXTaskPanes(jxTaskPaneContainer);
		
		JXTaskPane jfJxTaskPane1 = new JXTaskPane();
		jfJxTaskPane1.setAlignmentX(Component.LEFT_ALIGNMENT);
		jfJxTaskPane1.setTitle("Parameters : ");
		
		
		
		ParametersPanel parametersPanel = new ParametersPanel();
		jfJxTaskPane1.add(parametersPanel);
		jxTaskPaneContainer.add(jfJxTaskPane1);
		
		
		analysisButton = new JButton("Run & Save data");
		analysisButton.addActionListener( e ->{
			/**
			 * You can complete code here
			 */
		});
		jxTaskPaneContainer.add(analysisButton);
		
		
		add(jxTaskPaneContainer,BorderLayout.CENTER);
	}
	



	private void addJXTaskPanes(JXTaskPaneContainer jxTaskPaneContainer) {
		
	}




	public void enableAllGUIComponents(boolean b) {
		analysisButton.setEnabled(b);
	}

}
