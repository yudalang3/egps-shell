package egps2.builtin.modules.gallerymod;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import egps2.panels.InformationPanelFactory;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.jidesoft.swing.JideSplitPane;

import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;
import egps2.frame.ModuleFace;
import egps2.modulei.IInformation;
import egps2.modulei.IModuleLoader;
import egps2.modulei.ModuleClassification;

@SuppressWarnings("serial")
/**
 * IntroMain belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class IntroMain extends ModuleFace {

	private Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
	private RightDemoPanel rightDemoPanel;
	private DemoButtonsOrganizer demoOrganizer;
	private JXTaskPaneContainer taskPaneContainer;
	private final String currentCategory = "currentCategory.name";

	public IntroMain(IModuleLoader moduleLoader) {
		super(moduleLoader);
		setLayout(new BorderLayout());

		try {
			demoOrganizer = new DemoButtonsOrganizer();
		} catch (IOException e) {
			e.printStackTrace();
		}

		add(getMainSplitPane(), BorderLayout.CENTER);

		setBorder(null);
	}

	private JSplitPane getMainSplitPane() {
		JSplitPane mainSplitPane = new JSplitPane(JideSplitPane.HORIZONTAL_SPLIT);

		mainSplitPane.setDividerLocation(290);
		mainSplitPane.setLeftComponent(new JScrollPane(getTaskPanelContainer()));
		mainSplitPane.setOneTouchExpandable(true);

		rightDemoPanel = new RightDemoPanel();

		demoOrganizer.setRightDemoPanel(rightDemoPanel);
		mainSplitPane.setRightComponent(rightDemoPanel);

		mainSplitPane.setBorder(null);
		return mainSplitPane;
	}

	private JXTaskPaneContainer getTaskPanelContainer() {
		if (taskPaneContainer == null) {

			taskPaneContainer = new JXTaskPaneContainer();

			taskPaneContainer.setBackground(Color.WHITE);
			taskPaneContainer.setBackgroundPainter(null);

			addJXTaskPanels(taskPaneContainer);
			taskPaneContainer.setBorder(null);

		}
		return taskPaneContainer;
	}

	private void addJXTaskPanels(JXTaskPaneContainer taskPaneContainer) {
		JXPanel jxPanel = new JXPanel(new BorderLayout(15, 5));

		JLabel jLabel = new JLabel(" Module gallery");
		jLabel.setOpaque(true);
		jxPanel.add(jLabel, BorderLayout.NORTH);
		jLabel.setFont(defaultFont.deriveFont(Font.BOLD, 28f));
		jLabel.setForeground(new Color(53, 79, 105));
		jLabel.setBackground(new Color(176, 196, 210));

		ModuleClassification[] values = ModuleClassification.values();
		String[] classNames = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			classNames[i] = values[i].getCategory();
		}
		JComboBox<String> combBox = new JComboBox<>(classNames);
		combBox.setFocusable(false);
		
		combBox.setFont(defaultFont);
		jxPanel.add(combBox, BorderLayout.CENTER);
		JLabel viewJLabel = new JLabel("  View");
		jxPanel.add(viewJLabel, BorderLayout.WEST);
		viewJLabel.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont());
		jxPanel.setBorder(null);

		Map<String, Integer> str2numberMap = demoOrganizer.getStr2numberMap();

		Integer integer = str2numberMap.get(currentCategory);
		if (integer == null) {
			integer = 0;
		}
		combBox.setSelectedIndex(integer);

		loadingModuleByDiffCategory(jxPanel, taskPaneContainer,
				ModuleClassification.getClassificationAccordingToIndex(integer));
		combBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					int selectedIndex = combBox.getSelectedIndex();
					ModuleClassification cat = ModuleClassification.getClassificationAccordingToIndex(selectedIndex);

					loadingModuleByDiffCategory(jxPanel, taskPaneContainer, cat);
					demoOrganizer.getStr2numberMap().put(currentCategory, selectedIndex);
				}

			}
		});

	}

	private void loadingModuleByDiffCategory(JXPanel jxPanel, JXTaskPaneContainer taskPaneContainer,
			ModuleClassification cat) {

		loadingModuleGuidesByCat(jxPanel, taskPaneContainer, cat);

	}

	public void loadingModuleGuidesByCat(JXPanel titlePanel, JXTaskPaneContainer taskPaneContainer,
			ModuleClassification cat) {
		taskPaneContainer.removeAll();
		taskPaneContainer.add(titlePanel);
		demoOrganizer.configModulesByCat(taskPaneContainer, cat);
		taskPaneContainer.revalidate();
	}


	@Override
	public boolean canImport() {
		return false;
	}

	@Override
	public void importData() {

	}

	@Override
	public boolean canExport() {
		return true;
	}

	@Override
	public void exportData() {
		SwingDialog.showInfoMSGDialog("Information", "Nothing happened, for visualization.");
	}

	@Override
	public void changeToThisTab() {

	}

	@Override
	public void initializeGraphics() {

	}

	@Override
	public boolean closeTab() {
		// 最后退出的时候记录一下 TaskPanel的 collapse状态
		demoOrganizer.doCloseModuleAction(getTaskPanelContainer());
		return false;
	}

	@Override
	public String[] getFeatureNames() {
		return null;
	}

	@Override
	public IInformation getModuleInfo() {
		IInformation infor = new IInformation() {

			@Override
			public String getHowModuleLaunch() {
				return new String("The software is launched by the user.");
			}

			@Override
			public String getWhatDataInvoked() {
				return new String("This is the introduction module, does not invoke any data.");
			}

			@Override
			public String getHowUserOperates() {
				return new String("This module does not record the user operates.");
			}

			@Override
			public String getSummaryOfResults() {
				return new String("No computational results in this module.");
			}

		};
		return infor;
	}

	@Override
	public JComponent getEnglishDocument() {
		IModuleLoader loader = demoOrganizer.rightDemoPanel.getLoader();
		if (loader == null) {
			URL resource = getClass().getResource("manual_en.html");
			if (resource == null) {
				return null;
			}
			try {
				return new InformationPanelFactory().getInformationPanelFromResource(resource);
			} catch (IOException e) {
				return null;
			}
		}
		JComponent englishDocument = loader.getEnglishDocument();
		return englishDocument;
	}

	@Override
	public JComponent getChineseDocument() {
		IModuleLoader loader = demoOrganizer.rightDemoPanel.getLoader();
		if (loader == null) {
			URL resource = getClass().getResource("manual_zh.html");
			if (resource == null) {
				return null;
			}
			try {
				return new InformationPanelFactory().getInformationPanelFromResource(resource);
			} catch (IOException e) {
				return null;
			}
		}
		JComponent document = loader.getChineseDocument();
		return document;
	}
}
