package egps2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;

import egps2.panels.SubTextPanelNode;
import egps2.panels.pref.ComponentFontPanel;
import egps2.panels.pref.DataDisplayFontPanel;
import egps2.panels.pref.DialogFontPanel;
import egps2.panels.pref.ExternalProgramSettingJPanel;
import egps2.panels.pref.HtmlDocumentPanel;
import egps2.panels.pref.IconSizePrefJPanel;
import egps2.panels.pref.InputFontPanel;
import egps2.panels.pref.LanguageSettingPanel;
import egps2.panels.pref.LookAndFeelPanel;
import egps2.panels.pref.TestDataDirPanel;
import egps2.panels.pref.TextAntiAliasPanel;

/**
 * https://docs.oracle.com/javase/tutorial/uiswing/components/tree.html
 *
 */
public class PreferencePanel extends JPanel implements TreeSelectionListener {
	private static final long serialVersionUID = 1258237703985293775L;

	/**
	 * Remember the last selected preference page within the current application run.
	 * <p>
	 * NOTE: This is intentionally NOT persisted. After application restart, it falls back
	 * to the first (default) page.
	 */
	private static int lastSelectedPreferenceRow = -1;

	private final String menuFontName = "Menu font";
	private final String tabFontName = "Tab font";
	private final String moduleFontName = "Module font";

	private JXTree tree;

	// Optionally play with line styles. Possible values are
	// "Angled" (the default), "Horizontal", and "None".
	private static boolean playWithLineStyle = false;
	private static String lineStyle = "Horizontal";

	private JScrollPane rightJScrollPane;

	private LaunchProperty launchProperty;

	public PreferencePanel(LaunchProperty launchProperty) {
		super(new BorderLayout());

		this.launchProperty = launchProperty;
		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode();
		DefaultMutableTreeNode defaultNode = createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JXTree(top, true);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		TreeSelectionModel selectionModel = tree.getSelectionModel();
		selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		Font defaultTitleFont = launchProperty.getDefaultTitleFont();
		tree.setFont(defaultTitleFont);

		tree.expandAll();
		tree.setExpandsSelectedPaths(true);

		// Restore last visited preference page (in-memory only), otherwise default to first page.
		TreePath initialPath = new TreePath(defaultNode.getPath());
		if (lastSelectedPreferenceRow >= 0 && lastSelectedPreferenceRow < tree.getRowCount()) {
			TreePath candidatePath = tree.getPathForRow(lastSelectedPreferenceRow);
			if (candidatePath != null) {
				Object last = candidatePath.getLastPathComponent();
				if (last instanceof DefaultMutableTreeNode) {
					Object userObject = ((DefaultMutableTreeNode) last).getUserObject();
					if (userObject instanceof JPanel) {
						initialPath = candidatePath;
					}
				}
			}
		}
		selectionModel.setSelectionPath(initialPath);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		JPanel pp = (JPanel) ((DefaultMutableTreeNode) initialPath.getLastPathComponent()).getUserObject();
		rightJScrollPane = new JScrollPane(pp);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setLeftComponent(treeView);
		splitPane.setRightComponent(rightJScrollPane);
		splitPane.setBorder(null);

		Dimension minimumSize = new Dimension(100, 50);
		rightJScrollPane.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(200);
		splitPane.setPreferredSize(new Dimension(700, 500));

		// Add the split pane to this panel.
		add(splitPane, BorderLayout.CENTER);

		setBorder(null);
	}

	/** Required by TreeSelectionListener interface. */
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();

		/**
		 * 有些节点没有实现 一些操作，它的返回结果就不是JPanel
		 */
		if (!(nodeInfo instanceof JPanel)) {
			return;
		}
		JPanel preferenceTreeNode = (JPanel) nodeInfo;

		if (preferenceTreeNode != null) {
			// Cache selection so next time opening Preference dialog stays at this page (until restart).
			lastSelectedPreferenceRow = tree.getRowForPath(tree.getSelectionPath());

			rightJScrollPane.setViewportView(preferenceTreeNode);
			rightJScrollPane.validate();
		}
	}

	private DefaultMutableTreeNode createNodes(DefaultMutableTreeNode top) {

		DefaultMutableTreeNode defaultViewNode = null;

		{
			// 第一个层次的东西是 Appearance
			DefaultMutableTreeNode category = new DefaultMutableTreeNode("Appearance");
			top.add(category);

			{
				SubTextPanelNode subTextPanelNode = new SubTextPanelNode("Module title font", "Module text font",
						launchProperty.getDefaultTitleFont(), launchProperty.getDefaultFont());

				subTextPanelNode.setTitle(moduleFontName);
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(subTextPanelNode, false);
				category.add(lafSetting);
				defaultViewNode = lafSetting;
			}
			{
				SubTextPanelNode subTextPanelNode = new SubTextPanelNode("First level menu font",
						"Second level menu font", launchProperty.getMenuFistLevelFont(), launchProperty.getMenuSecondLevelFont());

				subTextPanelNode.setTitle(menuFontName);
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(subTextPanelNode, false);
				category.add(lafSetting);
			}
			{
				SubTextPanelNode subTextPanelNode = new SubTextPanelNode("Selected tab font", "Unselected tab font",
						launchProperty.getSelectedTabTitleFont(), launchProperty.getUnSelectedTabTitleFont());

				subTextPanelNode.setTitle(tabFontName);
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(subTextPanelNode, false);
				category.add(lafSetting);
			}
			{
				HtmlDocumentPanel subTextPanelNode = new HtmlDocumentPanel(launchProperty);
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(subTextPanelNode, false);
				category.add(lafSetting);
			}
			// New font panels
			{
				DialogFontPanel dialogFontPanel = new DialogFontPanel(
						launchProperty.getDialogTitleFont(),
						launchProperty.getDialogContentFont(),
						launchProperty.getDialogButtonFont());
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(dialogFontPanel, false);
				category.add(lafSetting);
			}
			{
				ComponentFontPanel componentFontPanel = new ComponentFontPanel(
						launchProperty.getLabelFont(),
						launchProperty.getButtonFont(),
						launchProperty.getCheckBoxFont());
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(componentFontPanel, false);
				category.add(lafSetting);
			}
			{
				InputFontPanel inputFontPanel = new InputFontPanel(
						launchProperty.getTextFieldFont(),
						launchProperty.getTextAreaFont(),
						launchProperty.getComboBoxFont());
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(inputFontPanel, false);
				category.add(lafSetting);
			}
			{
				DataDisplayFontPanel dataDisplayFontPanel = new DataDisplayFontPanel(
						launchProperty.getTableFont(),
						launchProperty.getTableHeaderFont(),
						launchProperty.getListFont(),
						launchProperty.getTreeFont());
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(dataDisplayFontPanel, false);
				category.add(lafSetting);
			}

			{
				LookAndFeelPanel lookAndFeelPanel = new LookAndFeelPanel();
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(lookAndFeelPanel, false);
				category.add(lafSetting);
			}
			{
				IconSizePrefJPanel lookAndFeelPanel = new IconSizePrefJPanel(launchProperty);
				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(lookAndFeelPanel, false);
				category.add(lafSetting);
			}
            //去掉 ExternalProgramSettingJPanel，因为这是模块自己的事情，这些不需要再全局指定
//			{
//				ExternalProgramSettingJPanel lookAndFeelPanel = new ExternalProgramSettingJPanel(launchProperty);
//				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(lookAndFeelPanel, false);
//				category.add(lafSetting);
//			}

		}

		{
			DefaultMutableTreeNode category = new DefaultMutableTreeNode("Graphics");
			top.add(category);

			{
				TextAntiAliasPanel subTextPanelNode = new TextAntiAliasPanel();

				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(subTextPanelNode, false);
				category.add(lafSetting);
			}
			{
				LanguageSettingPanel subTextPanelNode = new LanguageSettingPanel();

				DefaultMutableTreeNode lafSetting = new DefaultMutableTreeNode(subTextPanelNode, false);
				category.add(lafSetting);
			}

		}

		{
			DefaultMutableTreeNode category = new DefaultMutableTreeNode("Storage");
			top.add(category);

			{
				TestDataDirPanel testDataDirPanel = new TestDataDirPanel(launchProperty);
				DefaultMutableTreeNode storageNode = new DefaultMutableTreeNode(testDataDirPanel, false);
				category.add(storageNode);
			}
		}

		return defaultViewNode;
	}

	public void restoreToDefaults() {
		launchProperty.setRestoreToDefault(true);
	}

	public void applyAndClose() {
		Object root = tree.getModel().getRoot();
		DefaultMutableTreeNode rootType = (DefaultMutableTreeNode) root;
		// Note: this is not the root
		DefaultMutableTreeNode firstRootChild = (DefaultMutableTreeNode) rootType.getFirstChild();
		DefaultMutableTreeNode secondRootChild = (DefaultMutableTreeNode) rootType.getChildAt(1);
		DefaultMutableTreeNode thirdRootChild = (DefaultMutableTreeNode) rootType.getChildAt(2);

		int childCount = firstRootChild.getChildCount();

		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) firstRootChild.getChildAt(i);
			Object userObject = child.getUserObject();

			if (userObject instanceof SubTextPanelNode) {
				SubTextPanelNode subPanel = (SubTextPanelNode) userObject;
				switch (subPanel.toString()) {
				case menuFontName:
					launchProperty.setMenuFistLevelFont(subPanel.getUpFont());
					launchProperty.setMenuSecondLevelFont(subPanel.getDownFont());
					break;
				case tabFontName:
					launchProperty.setSelectedTabTitleFont(subPanel.getUpFont());
					launchProperty.setUnSelectedTabTitleFont(subPanel.getDownFont());
					break;
				case moduleFontName:
					launchProperty.setDefaultTitleFont(subPanel.getUpFont());
					launchProperty.setDefaultFont(subPanel.getDownFont());
					break;
				default:
					break;
				}
			} else if (userObject instanceof IconSizePrefJPanel) {
				IconSizePrefJPanel subPanel = (IconSizePrefJPanel) userObject;
				Dimension defaultIconSize = subPanel.getDefaultIconSize();
				Dimension tabIconSize = subPanel.getTabIconSize();

				launchProperty.setIconHeight((byte) defaultIconSize.height);
				launchProperty.setIconWidth((byte) defaultIconSize.width);

				launchProperty.setTabIconHeight((byte) tabIconSize.height);
				launchProperty.setTabIconWidth((byte) tabIconSize.width);
			} else if (userObject instanceof HtmlDocumentPanel) {
				HtmlDocumentPanel subPanel = (HtmlDocumentPanel) userObject;
				Font upFont = subPanel.getUpFont();
				launchProperty.setDocumentFont(upFont);
			} else if (userObject instanceof DialogFontPanel) {
				DialogFontPanel subPanel = (DialogFontPanel) userObject;
				launchProperty.setDialogTitleFont(subPanel.getTitleFont());
				launchProperty.setDialogContentFont(subPanel.getContentFont());
				launchProperty.setDialogButtonFont(subPanel.getButtonFont());
			} else if (userObject instanceof ComponentFontPanel) {
				ComponentFontPanel subPanel = (ComponentFontPanel) userObject;
				launchProperty.setLabelFont(subPanel.getLabelFont());
				launchProperty.setButtonFont(subPanel.getButtonFont());
				launchProperty.setCheckBoxFont(subPanel.getCheckBoxFont());
			} else if (userObject instanceof InputFontPanel) {
				InputFontPanel subPanel = (InputFontPanel) userObject;
				launchProperty.setTextFieldFont(subPanel.getTextFieldFont());
				launchProperty.setTextAreaFont(subPanel.getTextAreaFont());
				launchProperty.setComboBoxFont(subPanel.getComboBoxFont());
			} else if (userObject instanceof DataDisplayFontPanel) {
				DataDisplayFontPanel subPanel = (DataDisplayFontPanel) userObject;
				launchProperty.setTableFont(subPanel.getTableFont());
				launchProperty.setTableHeaderFont(subPanel.getTableHeaderFont());
				launchProperty.setListFont(subPanel.getListFont());
				launchProperty.setTreeFont(subPanel.getTreeFont());
			} else if (userObject instanceof ExternalProgramSettingJPanel) {
				ExternalProgramSettingJPanel subPanel = (ExternalProgramSettingJPanel) userObject;
				subPanel.saveTheFile();
			}

		}

		childCount = secondRootChild.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) secondRootChild.getChildAt(i);
			Object userObject = child.getUserObject();

			if (userObject instanceof TextAntiAliasPanel) {
//				TextAntiAliasPanel subPanel = (TextAntiAliasPanel) userObject;
				UnifiedAccessPoint.getLaunchProperty().initializeAntialiasOption();
			}
		}

		// Handle Storage category (third root child)
		childCount = thirdRootChild.getChildCount();
		for (int i = 0; i < childCount; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) thirdRootChild.getChildAt(i);
			Object userObject = child.getUserObject();

			if (userObject instanceof TestDataDirPanel) {
				TestDataDirPanel subPanel = (TestDataDirPanel) userObject;
				launchProperty.setTestDataDir(subPanel.getTestDataDir());
			}
		}

		// Apply fonts to UIManager and refresh UI
		launchProperty.applyFontsToUIManager();
		SwingUtilities.updateComponentTreeUI(UnifiedAccessPoint.getInstanceFrame());

		try {
			launchProperty.saveTheProperties(false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
