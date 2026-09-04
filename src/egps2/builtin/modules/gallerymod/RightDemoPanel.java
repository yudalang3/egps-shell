package egps2.builtin.modules.gallerymod;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.jdesktop.swingx.JXTitledPanel;

import egps2.UnifiedAccessPoint;
import egps2.frame.MainFrameProperties;
import egps2.modulei.IModuleLoader;
import egps2.panels.InformationPanelFactory;


@SuppressWarnings("serial")
/**
 * RightDemoPanel belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class RightDemoPanel extends JPanel implements ActionListener {

	private JButton openModuleButton;
	JXTitledPanel jxTitledPanelUp;
	JXTitledPanel jxTitledPanelDown;

	private JSplitPane jSplitPane;

	private IModuleLoader loader;
	
	public RightDemoPanel() {

		jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		jSplitPane.setDividerSize(10);
		jSplitPane.setDividerLocation(0.5);
		jSplitPane.setDividerLocation(400);
		jSplitPane.setOneTouchExpandable(true);

		setLayout(new BorderLayout());
		add(jSplitPane, BorderLayout.CENTER);

		URL url = getClass().getResource("demo/pages/statement.html");

		JEditorPane upPanelHtml = null;
		try {
			upPanelHtml = new InformationPanelFactory().getInformationPanelFromResource(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		upPanelHtml.setEditable(false);
		JScrollPane topScrollPanel = new JScrollPane(upPanelHtml);
		topScrollPanel.setBorder(null);

		
		boolean isEnglish = UnifiedAccessPoint.getLaunchProperty().isEnglish();

		String upPanelString = null;

		if (isEnglish) {
			upPanelString = "Overview the eGPS 2.0 module document/instruction/tutorial/manual";
		} else {
			upPanelString = UnifiedAccessPoint.getResourceString("introduction.module.GrapIll.name");
		}
		
		jxTitledPanelUp = new JXTitledPanel(upPanelString, topScrollPanel);
		

		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
		jxTitledPanelUp.setTitleFont(defaultTitleFont);
		
		jxTitledPanelUp.setBorder(null);

		jSplitPane.setTopComponent(jxTitledPanelUp);

		
		
		String openButtonName = UnifiedAccessPoint.getResourceString("introduction.module.open.button");

		openModuleButton = new JButton(openButtonName);
		openModuleButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		openModuleButton.addActionListener(this);
		openModuleButton.setEnabled(false);
		openModuleButton.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultFont());
		openModuleButton.setToolTipText("Open module in another tab.");
		
		String path = null;
		if (isEnglish) {
			path = "demo/pages/introductionModule_eng.html";
		} else {
			path = "demo/pages/introductionModule.html";
		}

		JEditorPane downPanelHtml = null;
		try {
			URL resource = getClass().getResource(path);
			downPanelHtml = new InformationPanelFactory().getInformationPanelFromResource(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JScrollPane buttomScorllPanel = new JScrollPane(downPanelHtml);
		buttomScorllPanel.setBorder(null);

		String downPanelString = null;

		if (isEnglish) {
			downPanelString = "The user-centric design of the module gallery";
		} else {
			downPanelString = UnifiedAccessPoint.getResourceString("introduction.module.TextExp.name");
		}

		jxTitledPanelDown = new JXTitledPanel(downPanelString, buttomScorllPanel);

		jxTitledPanelDown.setBorder(null);
		jxTitledPanelDown.setTitleFont(defaultTitleFont);


		jSplitPane.setBottomComponent(jxTitledPanelDown);

		jxTitledPanelUp.setRightDecoration(openModuleButton);
	}

	public void loadModuleIntroduction(IModuleLoader loader) {
		openModuleButton.setEnabled(true);

		JComponent graphicalIllustration = loader.getEnglishDocument();

		if (graphicalIllustration == null) {
			jSplitPane.remove(jxTitledPanelUp);
		}else {
			jSplitPane.setTopComponent(jxTitledPanelUp);
			jxTitledPanelUp.setContentContainer(MainFrameProperties.autoWrapComponentWithScollPanel(graphicalIllustration));
		}
		
		boolean isEnglish = UnifiedAccessPoint.getLaunchProperty().isEnglish();
		if (isEnglish) {
			jSplitPane.remove(jxTitledPanelDown);
		}else {
			JComponent textExplanation = loader.getChineseDocument();
			if (textExplanation == null) {
				jSplitPane.remove(jxTitledPanelDown);
			} else {
				jSplitPane.setBottomComponent(jxTitledPanelDown);
				jxTitledPanelDown
						.setContentContainer(MainFrameProperties.autoWrapComponentWithScollPanel(textExplanation));
			}
		}


		this.loader = loader;
		jSplitPane.updateUI();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (loader != null) {
			/**
			 * "open module" 按钮触发的事件
			 */
			MainFrameProperties.loadTheModuleFromIModuleLoader(loader);
		}
	}

	public IModuleLoader getLoader() {
		return loader;
	}
}
