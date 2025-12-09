package demo.floating;

import egps2.UnifiedAccessPoint;
import demo.floating.work.BioinformaticsApp;
import egps2.frame.MainFrameGestureConfig;
import egps2.frame.ModuleFace;
import egps2.frame.MyFrame;
import egps2.modulei.IInformation;
import egps2.modulei.IModuleLoader;
import egps2.panels.dialog.SwingDialog;
import utils.EGPSFormatUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI class for the floating demo modules.
 * This class creates the interface for floating demo modules and manages their
 * initialization and layout within the eGPS software platform.
 * It provides functionality for arranging and deleting windows in a desktop pane.
 */
@SuppressWarnings("serial")
public class GuiMain extends ModuleFace {

	FloatingVoiceImp voiceImp;
	JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	private BioinformaticsApp bioinformaticsApp;

	/**
	 * Constructor for the GuiMain class
	 * @param moduleLoader The module loader responsible for this GUI
	 */
	protected GuiMain(IModuleLoader moduleLoader) {
		super(moduleLoader);
		setLayout(new BorderLayout());

		JLabel welcomeJLabel = new JLabel("Click import button in Toolbar to import data");
		welcomeJLabel.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont());
		welcomeJLabel.setHorizontalTextPosition(JLabel.CENTER);
		welcomeJLabel.setVerticalTextPosition(JLabel.CENTER);
		welcomeJLabel.setHorizontalAlignment(JLabel.CENTER);


		//jSplitPane.setDividerLocation(50);

		JPanel jPanel = getLeftPanel();

		jSplitPane.setLeftComponent(jPanel);
		jSplitPane.setRightComponent(welcomeJLabel);

		add(jSplitPane, BorderLayout.CENTER);

		voiceImp = new FloatingVoiceImp(this);
	}

	/**
	 * Create and configure the left panel with control buttons
	 * @return The configured left panel
	 */
	private JPanel getLeftPanel() {
		JPanel jPanel = new JPanel();
		jPanel.setBackground(Color.WHITE);
		jPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		BoxLayout boxLayout = new BoxLayout(jPanel,BoxLayout.Y_AXIS);
		jPanel.setLayout(boxLayout);
		JButton jButton1 = new JButton("Arrange all");

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		jButton1.setFont(defaultFont);
		jButton1.addActionListener(e -> {
			if (bioinformaticsApp == null){
				SwingDialog.showErrorMSGDialog("Error", "Please import data first.");
				return;
			}
			SwingUtilities.invokeLater(() -> {
				bioinformaticsApp.arrangeWindows();
			});
		});

		JButton jButton2 = new JButton("Delete all");
		jButton2.setFont(defaultFont);
		jButton2.addActionListener(e -> {
			if (bioinformaticsApp == null){
				SwingDialog.showErrorMSGDialog("Error", "Please import data first.");
				return;
			}
			SwingUtilities.invokeLater(() -> {
				bioinformaticsApp.deleteAllWindows();
			});
		});

		// Set buttons to maximum width and center alignment
		jButton1.setAlignmentX(Component.CENTER_ALIGNMENT);
		jButton2.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Set maximum button dimensions to screen width (optional)
		Dimension maxDim = new Dimension(Integer.MAX_VALUE, jButton1.getPreferredSize().height);
		jButton1.setMaximumSize(maxDim);
		jButton2.setMaximumSize(maxDim);

		jPanel.add(jButton1);
		jPanel.add(Box.createVerticalStrut(10));
		jPanel.add(jButton2);
		return jPanel;
	}

	/**
	 * Close the tab (not implemented)
	 * @return false as this operation is not supported
	 */
	@Override
	public boolean closeTab() {
		return false;
	}

	/**
	 * Change to this tab (not implemented)
	 */
	@Override
	public void changeToThisTab() {

	}

	/**
	 * Check if this module can import data
	 * @return true as this module supports data import
	 */
	@Override
	public boolean canImport() {
		return true;
	}

	/**
	 * Import data method
	 */
	@Override
	public void importData() {
		voiceImp.doUserImportAction();
	}


	/**
	 * Check if this module can export data
	 * @return true as this module supports data export
	 */
	@Override
	public boolean canExport() {
		return true;
	}

	/**
	 * Export data method (not implemented)
	 */
	@Override
	public void exportData() {
		//new SaveUtil().saveData(paintingPanel, getClass());

	}

	/**
	 * Get the feature names provided by this module
	 * @return Array of feature names
	 */
	@Override
	public String[] getFeatureNames() {
		return new String[] { "View the demo of floating VOICE" };
	}

	/**
	 * Initialize the graphics for this module
	 */
	@Override
	public void initializeGraphics() {
		MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		JToolBar jMenuBar = instanceFrame.getJtoolBar();
		Runnable callBackFunc1 = () -> {
			
		};

		String str = EGPSFormatUtil.html32Concat(5, "Click me");
		MainFrameGestureConfig config = new MainFrameGestureConfig(jMenuBar, str, callBackFunc1);
		config.setHeight(100);
		config.setWidth(200);
		instanceFrame.showGesture(config);


	}


	/**
	 * Get module information
	 * @return IInformation instance with module details
	 */
	@Override
	public IInformation getModuleInfo() {
		IInformation iInformation = new IInformation() {

			/**
			 * Get information about what data is invoked by this module
			 * @return Data information string
			 */
			@Override
			public String getWhatDataInvoked() {
				return "The data is loading from the import dialog.";
			}

			/**
			 * Get a summary of results produced by this module
			 * @return Results summary string
			 */
			@Override
			public String getSummaryOfResults() {
				return "The functionality is powered by the eGPS software.";
			}
		};
		return iInformation;
	}

	/**
	 * Add content to the GUI
	 * @param bioinformaticsApp The bioinformatics application to add
	 */
	public void addContent(BioinformaticsApp bioinformaticsApp) {
		jSplitPane.setRightComponent(bioinformaticsApp);
		this.bioinformaticsApp = bioinformaticsApp;
	}
}