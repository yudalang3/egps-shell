package demo.dockable;

import com.google.common.collect.Lists;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice;
import egps2.frame.ComputationalModuleFace;
import egps2.frame.gui.EGPSCustomTabbedPaneUI;
import egps2.modulei.IInformation;
import egps2.modulei.IModuleLoader;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main GUI class for the dockable demo modules.
 * This class creates the tabbed interface for various dockable demo modules
 * and manages their initialization and layout within the eGPS software platform.
 */
@SuppressWarnings("serial")
public class GuiMain extends ComputationalModuleFace {

	List<DockableTabModuleFaceOfVoice> listOfSubTabs = Lists.newArrayList();

	JTabbedPane jTabbedPane = new JTabbedPane(JTabbedPane.LEFT);

	/**
	 * Constructor for the GuiMain class
	 * @param moduleLoader The module loader responsible for this GUI
	 */
	protected GuiMain(IModuleLoader moduleLoader) {
		super(moduleLoader);
		setLayout(new BorderLayout());

		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();


		jTabbedPane.setFont(defaultTitleFont);


		initPanels(jTabbedPane);
		jTabbedPane.setUI(new EGPSCustomTabbedPaneUI());

		add(jTabbedPane);
	}

	/**
	 * Initialize all sub-panel modules and add them to the tabbed pane
	 * @param jTabbedPane The tabbed pane to which panels will be added
	 */
	private void initPanels(JTabbedPane jTabbedPane) {
		{
			LargeTextGeneratorSubTab panel = new LargeTextGeneratorSubTab(this);
			listOfSubTabs.add(panel);
			jTabbedPane.addTab(panel.getTabName(), null, panel, panel.getShortDescription());
		}
		{
			SimpleAlignmentSimulator panel = new SimpleAlignmentSimulator(this);
			listOfSubTabs.add(panel);
			jTabbedPane.addTab(panel.getTabName(), null, panel, panel.getShortDescription());
		}
		{
			SimpleExpressionProducer panel = new SimpleExpressionProducer(this);
			listOfSubTabs.add(panel);
			jTabbedPane.addTab(panel.getTabName(), null, panel, panel.getShortDescription());
		}
		{
			GroupwiseStatisticalTest panel = new GroupwiseStatisticalTest(this);
			listOfSubTabs.add(panel);
			jTabbedPane.addTab(panel.getTabName(), null, panel, panel.getShortDescription());
		}
		{
			AdvGroupwiseStatisticalTest panel = new AdvGroupwiseStatisticalTest(this);
			listOfSubTabs.add(panel);
			jTabbedPane.addTab(panel.getTabName(), null, panel, panel.getShortDescription());
		}
		{
			BiologicalPathwayEnrichment panel = new BiologicalPathwayEnrichment(this);
			listOfSubTabs.add(panel);
			jTabbedPane.addTab(panel.getTabName(), null, panel, panel.getShortDescription());
		}
	}

	/**
	 * Check if this module can import data
	 * @return false as this module does not support data import
	 */
	@Override
	public boolean canImport() {
		return false;
	}

	/**
	 * Import data method (not implemented)
	 */
	@Override
	public void importData() {

	}

	/**
	 * Check if this module can export data
	 * @return false as this module does not support data export
	 */
	@Override
	public boolean canExport() {
		return false;
	}

	/**
	 * Export data method (not implemented)
	 */
	@Override
	public void exportData() {

	}

	/**
	 * Get the feature names provided by this module
	 * @return Array of feature names
	 */
	@Override
	public String[] getFeatureNames() {
		return new String[] { "Download the annotation file from the Ensembl ftp." };
	}

	/**
	 * Initialize the graphics for all sub-modules
	 */
	@Override
	protected void initializeGraphics() {
		for (DockableTabModuleFaceOfVoice diyToolSubTabModuleFace : listOfSubTabs) {
			diyToolSubTabModuleFace.initializeGraphics();
		}

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

}