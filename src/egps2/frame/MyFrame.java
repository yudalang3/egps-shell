package egps2.frame;

/**
 * eGPS主应用程序窗口，协调所有模块标签页、工具栏、菜单栏和状态栏的核心框架类。
 * Main application window of eGPS that coordinates all module tabs, toolbar, menu bar, and status bar.
 *
 * <p>此类是整个eGPS框架的中心枢纽，负责：
 * This class is the central hub of the entire eGPS framework, responsible for:
 * <ul>
 *   <li>管理多个模块标签页的生命周期 - Managing lifecycle of multiple module tabs</li>
 *   <li>提供统一的菜单栏和工具栏 - Providing unified menu bar and toolbar</li>
 *   <li>显示底部状态栏（进度、线程状态等）- Displaying bottom status bar (progress, thread status, etc.)</li>
 *   <li>处理拖放操作和手势识别 - Handling drag-and-drop operations and gesture recognition</li>
 *   <li>管理玻璃面板覆盖层（忙碌指示器、成就通知等）- Managing glass pane overlays (busy indicator, achievement notifications, etc.)</li>
 *   <li>提供模块间通信和数据共享机制 - Providing inter-module communication and data sharing mechanisms</li>
 * </ul>
 *
 * <p><strong>架构特点：</strong>
 * Architectural features:
 * <ul>
 *   <li><b>单例模式：</b>通过 {@link egps2.UnifiedAccessPoint} 获取唯一实例</li>
 *   <li><b>模块化设计：</b>支持动态加载和卸载模块</li>
 *   <li><b>标签页管理：</b>使用 {@link com.jidesoft.swing.JideTabbedPane} 实现高级标签页功能</li>
 *   <li><b>拖放支持：</b>支持文件拖放到标签页，自动导入数据</li>
 *   <li><b>Singleton pattern:</b> Obtain unique instance via {@link egps2.UnifiedAccessPoint}</li>
 *   <li><b>Modular design:</b> Supports dynamic loading and unloading of modules</li>
 *   <li><b>Tab management:</b> Uses {@link com.jidesoft.swing.JideTabbedPane} for advanced tab features</li>
 *   <li><b>Drag-and-drop support:</b> Supports file drag-and-drop to tabs, automatic data import</li>
 * </ul>
 *
 * <p><strong>主要组件：</strong>
 * Main components:
 * <ul>
 *   <li><b>标签面板 (Tabbed Pane)：</b>承载所有模块的UI面板</li>
 *   <li><b>菜单栏 (Menu Bar)：</b>提供File、Edit、Tools、Help等菜单</li>
 *   <li><b>工具栏 (Tool Bar)：</b>提供快捷操作按钮（导入、导出、停止等）</li>
 *   <li><b>状态栏 (Status Bar)：</b>显示进度、线程状态、消息提示</li>
 *   <li><b>右侧面板 (Right Panel)：</b>图形属性调整面板（填充、线条、大小等）</li>
 *   <li><b>玻璃面板 (Glass Pane)：</b>覆盖层效果（忙碌状态、手势反馈、成就通知）</li>
 * </ul>
 *
 * <p><strong>生命周期：</strong>
 * Lifecycle:
 * <ol>
 *   <li>初始化：由 {@link egps2.Launcher} 在应用启动时创建</li>
 *   <li>模块加载：通过 {@link egps2.UnifiedAccessPoint#loadTheModuleFromIModuleLoader} 动态加载模块</li>
 *   <li>标签切换：用户切换标签时调用 {@link egps2.modulei.IModuleFace#changeToThisTab()}</li>
 *   <li>标签关闭：关闭标签时调用 {@link egps2.modulei.IModuleFace#closeTab()}</li>
 *   <li>应用退出：保存用户配置和历史记录</li>
 * </ol>
 *
 * <p><strong>关键方法（部分）：</strong>
 * Key methods (partial):
 * <ul>
 *   <li>{@code addModuleTab(ModuleFace)} - 添加新模块标签页</li>
 *   <li>{@code removeModuleTab(int)} - 移除指定索引的标签页</li>
 *   <li>{@code refreshRightGraphicPropertiesPanel()} - 刷新右侧图形属性面板</li>
 *   <li>{@code showBusyGlass(boolean)} - 显示/隐藏忙碌指示器</li>
 *   <li>{@code updateStatusBar(String)} - 更新状态栏消息</li>
 * </ul>
 *
 * <p>线程模型：
 * Thread model:
 * <br>主窗口的所有UI操作必须在EDT（Event Dispatch Thread）中执行。
 * 计算密集型任务应在后台线程中执行，并通过 {@link javax.swing.SwingUtilities#invokeLater} 更新UI。
 * All UI operations on the main window must be executed in EDT (Event Dispatch Thread).
 * Computation-intensive tasks should execute in background threads and update UI via {@link javax.swing.SwingUtilities#invokeLater}.
 *
 * <p>大小：此类约68KB，包含约1800行代码，是框架中最大的类之一。
 * Size: This class is approximately 68KB, containing about 1800 lines of code, one of the largest classes in the framework.
 *
 * @see egps2.UnifiedAccessPoint
 * @see egps2.frame.ModuleFace
 * @see egps2.frame.ActionsManager
 * @see egps2.modulei.IModuleLoader
 * @author eGPS Dev Team
 * @since 2.0
 */
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang3.tuple.Triple;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXCollapsiblePane.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jidesoft.swing.JideTabbedPane;
import com.raven.swing.ButtonBadges;

import egps2.frame.gui.comp.DraggableTabbedPane;
import utils.EGPSFileUtil;
import egps2.utils.common.util.EGPSShellIcons;
import egps2.utils.EGPSIconUtil;
import egps2.LaunchProperty;
import egps2.Launcher;
import egps2.UnifiedAccessPoint;
import egps2.frame.HintManager.Hint;
import egps2.builtin.modules.gallerymod.IndependentModuleLoader;
import egps2.builtin.modules.gallerymod.IntroMain;
import egps2.modulei.AdjusterFillAndLine;
import egps2.modulei.AdjusterSizeAndPosition;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.panels.graphicpro.GraphicPropertiesPanel;
import egps2.plugin.manager.PluginOperation;
import egps2.utils.GlassPanelPrompt;

@SuppressWarnings("serial")
/**
 * MyFrame supports the main eGPS window, actions, or tab management.
 */
public class MyFrame extends JFrame {
	private static final Logger logger = LoggerFactory.getLogger(MyFrame.class);

	private ActionsManager actionsManager = ActionsManager.getInstance();
	private JideTabbedPane jTabbedPane;

	private ButtomStatesBar buttomStatesBar;

	private JComponent glassPane;

	private JToolBar jtoolBar;

	private IModuleLoader[] existedLoaders;

	private JXCollapsiblePane collapsiblePanel4graphicsPropertiesPanel;

	private IndependentModuleLoader introductionModuleLoader = new IndependentModuleLoader();

	private GlassPanelBusyStates glassPanelBusyStates = new GlassPanelBusyStates();

	private GraphicPropertiesPanel rightGraphicsPropertiesPanel;

	/**
	 * 因为这个Hint里面可以存上一个关联的Hint所以可以只存最后一个实例，如果这个值不为空， 那么就展示它。
	 */
	private Hint lastHint2invoke = null;

	GlassPanelPrompt promptPanel = new GlassPanelPrompt(this);

	private String introModuleName;

	protected Point location;
	protected boolean dragging;
	/** A global map variable to stroe already opened dragTabbedPanes */
	private Map<String, DraggableTabbedPane> saveDragTabbedPanes = new HashMap<>();


	/**
	 * 
	 * An adapter to locate the drop position if drag has been initialized!
	 * 
	 * @author yudalang
	 */
	private class EGPSDropTargetAdapter extends DropTargetAdapter {

		@Override
		public void drop(DropTargetDropEvent dtde) {
		}

		@Override
		public void dragOver(final DropTargetDragEvent e) {
			if (dragging) {
				location = e.getLocation();
			}
		}

	}


	/**
	 * 
	 * The main frame consist of following elements:
	 * 
	 * <pre>
	 * 		
	 * 首先是垂直上的构造
	 * 
	 * 最顶上的GlassPanel被覆盖了。
	 *
	 *
	 * Shell Panel
	 *
	 *     |---------------------------------------|
	 *     |                 JToolBar              |
	 *     |---------------------------------------|
	 *     |                         |             |
	 *     |                         | History     |
	 *     |                         |             |  Content Panel: JtabbedPanel and History Panel
	 *     |                         |             |
	 *     |---------------------------------------|
	 *     |              StatesBar                |
	 *     |---------------------------------------|
	 *
	 * Shell 之上是 JMenu
	 * 
	 * </pre>
	 * 
	 * 为什么这个构造函数是 protected?因为这个类的初始化只能被 UniSoftInstance处理。开发者不希望用户来创建这个类。
	 */
	protected MyFrame() {
		super("evolutionary Genotype-Phenotype Systems (eGPS)"); // set JFrame title!
		List<Image> listOfIcons = new ArrayList<Image>(2);
		Image smallImage = EGPSShellIcons.get("eGPS_logo16x16.png").getImage();
		listOfIcons.add(smallImage);
		Image biggerImage = EGPSShellIcons.get("eGPS_logo32x32.png").getImage();
		listOfIcons.add(biggerImage);
		Image image = EGPSShellIcons.get("eGPS_logo72x72.png").getImage();
		listOfIcons.add(image);

		// eGPS_logo20x20
		super.setIconImages(listOfIcons);
		logger.trace("Rdebug   MyFrame 218...");
		existedLoaders = MainFrameProperties.getExistedLoaders();
		logger.trace("Rdebug   MyFrame 220...");
		JPanel shellPanel = new JPanel(new BorderLayout());
		add(shellPanel);

		// JTool bar
		JToolBar toggleButtonToolBar = configToolbar();
		shellPanel.add(toggleButtonToolBar, BorderLayout.NORTH);
		logger.trace("Rdebug   MyFrame 227...");
		// Content Panel
		JPanel contentPanel = getMiddleContentPanel();
		logger.trace("Rdebug   MyFrame 230...");
		shellPanel.add(contentPanel, BorderLayout.CENTER);

		// States bar
		JPanel statesBar = configStatesBar();
		shellPanel.add(statesBar, BorderLayout.SOUTH);
		logger.trace("Rdebug   MyFrame 236...");
		// JMenu 菜单栏； 之前我记得有个坑，就是 这个JMenuBar的菜单的顺序很最终的界面有关系
		setJMenuBar(createMenus());
		logger.trace("Rdebug   MyFrame 238...");
		glassPane = new JComponent() {
			private static final long serialVersionUID = -5574747928831808726L;
		};

		glassPane.setLayout(null);
		glassPane.setVisible(false);
		setGlassPane(glassPane);
		logger.trace("Rdebug   MyFrame 246...");
		// 为什么不用windowAdaptor呢，因为我非常奇怪，点击关闭按钮之后调用的是windowClosing而不是closed.
		// 根据WindowEvent中的注释，它说是 调用dispose方法之后会调用这个方法。如果有时间可以调试，但是现在姑且相信这个说明吧。
		// 退一步想，如果不是API的声明说的这样，那又如何呢？
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				MyFrame.this.exitSoftware();
			}
		});

	}

	public void appendOneHint(Hint hint) {
		hint.nextHint = lastHint2invoke;
		lastHint2invoke = hint;
	}

	public void showHints() {
		if (lastHint2invoke == null) {
			return;
		}

		HintManager.showHint(lastHint2invoke);

		lastHint2invoke = null;
	}

	/**
	 * 上面是 Toolbar 下面是 Status bar，所以这里是 Middle
	 * 
	 * @return
	 */
	JPanel getMiddleContentPanel() {
		collapsiblePanel4graphicsPropertiesPanel = new JXCollapsiblePane(Direction.RIGHT);
		collapsiblePanel4graphicsPropertiesPanel.setCollapsed(true);
		collapsiblePanel4graphicsPropertiesPanel.setAnimated(false);
		// JXCollapsiblePane can be used like any other container
		collapsiblePanel4graphicsPropertiesPanel.setLayout(new BorderLayout());

		String str = UnifiedAccessPoint.getResourceString("turnToHistory.module.search.name");

		TitledBorder border = new TitledBorder("Shape Formatting");
		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
		border.setTitleFont(defaultTitleFont);
		border.setTitleFont(UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont());
		collapsiblePanel4graphicsPropertiesPanel.setBorder(border);

		rightGraphicsPropertiesPanel = new GraphicPropertiesPanel();
		JScrollPane jScrollPane = new JScrollPane(rightGraphicsPropertiesPanel);
		jScrollPane.setBorder(null);
		collapsiblePanel4graphicsPropertiesPanel.add(jScrollPane, BorderLayout.CENTER);

		// Show/hide the "Controls"
		Action toggleAction = collapsiblePanel4graphicsPropertiesPanel.getActionMap()
				.get(JXCollapsiblePane.TOGGLE_ACTION);
		// use the collapse/expand icons from the JTree UI
//		InputStream resource = getClass().getResourceAsStream("/images/toolbar/graphicsProperties.svg");
//		URL resourceURL = getClass().getResource("/images/toolbar/graphicsProperties.svg");
//		ImageIcon imageIcon = null;
//		imageIcon = EGPSIconUtil.getIconFromSVGByPath(resourceURL, 20, 20);

		InputStream resource = getClass().getResourceAsStream("/images/toolbar/graphicsProperties.svg");
		ImageIcon imageIcon = null;
		try {
			imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		toggleAction.putValue(Action.NAME, "");

		toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, imageIcon);
		toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, imageIcon);
//		toggleAction.putValue(JXCollapsiblePane.COLLAPSE_ICON, UIManager.getIcon("Tree.expandedIcon"));
//		toggleAction.putValue(JXCollapsiblePane.EXPAND_ICON, UIManager.getIcon("Tree.collapsedIcon"));

		JPanel verticalMiddleContentPanel = new JPanel(new BorderLayout());

		JButton graphicProperties = new JButton(imageIcon);
		graphicProperties.setFocusable(false);
		graphicProperties.addActionListener(e -> {
			boolean collapsed = collapsiblePanel4graphicsPropertiesPanel.isCollapsed();
			logger.info("Collapsed: {} to {} ", collapsed, !collapsed);
			collapsiblePanel4graphicsPropertiesPanel.setCollapsed(!collapsed);
		});

		str = UnifiedAccessPoint.getResourceString("turnToHistory.module.button.tip");

		graphicProperties.setToolTipText(str);
		graphicProperties.setFocusable(false);
		graphicProperties.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

		initializeJTabbedPanel(graphicProperties);

		jTabbedPane.setTabTrailingComponent(graphicProperties);

		if (UnifiedAccessPoint.isFirstTimeLaunched()) {
			// 注册一个 Hint 事件
			String resourceString = UnifiedAccessPoint.getResourceString("Application.hint.graphicPro.button");

			Hint firstHint = new Hint(resourceString, graphicProperties, SwingConstants.LEFT);
			appendOneHint(firstHint);
		}

		verticalMiddleContentPanel.add(jTabbedPane, BorderLayout.CENTER);
		verticalMiddleContentPanel.add(collapsiblePanel4graphicsPropertiesPanel, BorderLayout.EAST);

		verticalMiddleContentPanel.setBorder(BorderFactory.createEmptyBorder());
		return verticalMiddleContentPanel;
	}

	private void initializeJTabbedPanel(JComponent historyButton) {
		jTabbedPane = new JideTabbedPane(JideTabbedPane.TOP);
		jTabbedPane.setTabShape(JideTabbedPane.SHAPE_OFFICE2003);
		jTabbedPane.setTabColorProvider(JideTabbedPane.ONENOTE_COLOR_PROVIDER);
		jTabbedPane.setTabEditingAllowed(true);

		LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
		jTabbedPane.setFont(launchProperty.getUnSelectedTabTitleFont());
		jTabbedPane.setSelectedTabFont(launchProperty.getSelectedTabTitleFont());
		jTabbedPane.setShowCloseButtonOnTab(true);
		jTabbedPane.setBoldActiveTab(true);

		/**
		 * 重新编写点击关闭按钮的事件，因为每个模块在关闭的时候有可能正在运行任务
		 */
		jTabbedPane.setCloseAction(new AbstractAction() {
			private static final long serialVersionUID = 7394046177767901038L;

			@Override
			public void actionPerformed(ActionEvent e) {
				ModuleFace source = (ModuleFace) e.getSource();
				closeATabInTabbedPanel(source);
			}

		});
		jTabbedPane.addChangeListener(e -> {
			ModuleFace selectedModule = getSelectedModule();

			if (selectedModule != null) {
				selectedModule.changeToThisTab();
			}

			// 配置Graphics面板
			if (selectedModule instanceof AdjusterSizeAndPosition || selectedModule instanceof AdjusterFillAndLine) {
				collapsiblePanel4graphicsPropertiesPanel.setCollapsed(false);
			} else {
				collapsiblePanel4graphicsPropertiesPanel.setCollapsed(true);
			}

			// 这个按钮必须在有元素的情况下才能正常使用。
			historyButton.setEnabled(jTabbedPane.getTabCount() != 0);
			refreshAllActionsInToolbarPlusStatesBar();
			refreshRightGraphicPropertiesPanel();

		});

		final DragSourceListener dsl = new DragSourceListener() {

			public void dragEnter(DragSourceDragEvent e) {
				// Called when the user is dragging this drag source and enters the drop target
				if (jTabbedPane.getTitleAt(jTabbedPane.getSelectedIndex()).equals(introModuleName)) {
					// Set the mouse pointer gesture if user drag the First Data Panel
					// gesture should be indicate no where to drop. i.e. can't drag & drop
					e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
				} else {
					// other analysis panel can drop!
					e.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
				}

			}

			public void dragExit(DragSourceEvent e) {
				// Called when the user is dragging this drag source and leaves the drop target
				if (location == null) {
					e.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
				}

			}

			public void dragOver(DragSourceDragEvent e) {
				// Called when the user is dragging this drag source and moves over the drop
				// target
				location = e.getLocation();

			}

			public void dragDropEnd(DragSourceDropEvent e) {

				if (location != null
						&& !jTabbedPane.getTitleAt(jTabbedPane.getSelectedIndex()).equals(introModuleName)) {
					Component selectedComponent = jTabbedPane.getSelectedComponent();
					String titleAt = jTabbedPane.getTitleAt(jTabbedPane.getSelectedIndex());
					// Generate a new JFrame
					DraggableTabbedPane dragTab = new DraggableTabbedPane(titleAt, selectedComponent, jTabbedPane);
					// flipEnabledOnAllButtons(false, dragTab);

					Dimension screenSize = getToolkit().getScreenSize();
					dragTab.setSize((int) (screenSize.getWidth() * 0.4), (int) (screenSize.getHeight() * 0.5));
					// Get mouse position
					Point p = MouseInfo.getPointerInfo().getLocation();
					// Set this JFrame position
					dragTab.setLocation(p.x - (int) (screenSize.getWidth() * 0.2), p.y);
					dragTab.setVisible(true);
					getSaveDragTabbedPanes().put(titleAt, dragTab);

				}
				location = null;
				dragging = false;
			}

			public void dropActionChanged(DragSourceDragEvent e) {
				// Called when the user changes the drag action between copy or move
			}
		};

		final DragGestureListener dgl = new DragGestureListener() {
			public void dragGestureRecognized(DragGestureEvent e) {

				Point tabPt = e.getDragOrigin();
				int dragTabIndex = jTabbedPane.indexAtLocation(tabPt.x, tabPt.y);
				if (dragTabIndex < 0) {
					return;
				}

				e.startDrag(DragSource.DefaultMoveDrop,
						Toolkit.getDefaultToolkit().getSystemClipboard().getContents((Object) null), dsl);
				dragging = true;
			}
		};

		new DropTarget(jTabbedPane, DnDConstants.ACTION_COPY_OR_MOVE, new EGPSDropTargetAdapter(), true);
		new DragSource().createDefaultDragGestureRecognizer(jTabbedPane, DnDConstants.ACTION_COPY_OR_MOVE, dgl);

		// Add right pop menu!
		jTabbedPane.addMouseListener(new TabbedPaneMouseAdapter(jTabbedPane));

		SwingUtilities.invokeLater(() -> {
			launchIntroductionPanel();
		});

		jTabbedPane.setBorder(BorderFactory.createEmptyBorder());
		jTabbedPane.setRequestFocusEnabled(false);
		jTabbedPane.setFocusable(false);
	}

	private Map<String, DraggableTabbedPane> getSaveDragTabbedPanes() {
		return saveDragTabbedPanes;
	}

	private void removeDragTabbedPane(String tabTitle) {
		saveDragTabbedPanes.remove(tabTitle);

	}

	void closeATabInTabbedPanel(ModuleFace source) {
		boolean closeTab = source.moduleExisted();
		if (closeTab) {
			String title = UnifiedAccessPoint.getResourceString("mainframe.close.confirm.title");
			String msg = UnifiedAccessPoint.getResourceString("mainframe.close.confirm.msg");
			int showConfirmDialog = JOptionPane.showConfirmDialog(MyFrame.this,msg
					, title,
					JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

			if (showConfirmDialog == JOptionPane.YES_OPTION) {
				jTabbedPane.remove(source);

				/**
				 * 如果是计算模块，要记得关闭
				 */
				if (source instanceof ComputationalModuleFace) {
					ComputationalModuleFace cFace = (ComputationalModuleFace) source;
					cFace.stopRunningTask();
				}
			}
		} else {
			jTabbedPane.remove(source);
		}
	}

	void launchIntroductionPanel() {
		int tabCount = jTabbedPane.getTabCount();
		boolean shouldInitialize = false;
		if (tabCount > 0) {
			Component componentAt = jTabbedPane.getComponentAt(0);
			if (componentAt instanceof IntroMain) {
				Component selectedComponent = jTabbedPane.getSelectedComponent();
				if (selectedComponent == componentAt){
					prompt("Already in the module gallery.");
				}else {
					jTabbedPane.setSelectedIndex(0);
				}
			} else {
				shouldInitialize = true;
			}
		} else {
			shouldInitialize = true;
		}

		if (shouldInitialize) {

			ModuleFace introModuleFace = introductionModuleLoader.getFace();
			IconBean iconBean = introductionModuleLoader.getIcon();

			Icon icon = null;
			try {
				icon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresizeWithTabIcon(iconBean.getInputStream(),
						iconBean.isSVG());
			} catch (IOException e) {
				e.printStackTrace();
			}

			introModuleName = UnifiedAccessPoint.getResourceString("Default.introduction.module.name");
			String str2 = UnifiedAccessPoint.getResourceString("Default.introduction.module.tip");

			jTabbedPane.insertTab(introModuleName, icon, introModuleFace, str2, 0);
			jTabbedPane.setSelectedIndex(0);
		}
	}

	JPanel configStatesBar() {
		buttomStatesBar = new ButtomStatesBar(this);

		if (UnifiedAccessPoint.isFirstTimeLaunched()) {
			// 注册一个 Hint 事件
			String resourceString = UnifiedAccessPoint.getResourceString("Application.hint.buttomStatesBar");
			Hint firstHint = new Hint(resourceString, buttomStatesBar, SwingConstants.TOP);
			appendOneHint(firstHint);
		}

		return buttomStatesBar;
	}

	JToolBar configToolbar() {

		jtoolBar = new JToolBar();
		jtoolBar.setFloatable(false);

		ActionSearch actionSearch = actionsManager.getActionSearch();
		List<AdjustedSoftAction> dataActions = actionsManager.getDataActions();
		List<Action> optionActions = actionsManager.getOptionActions();
		List<AdjustedSoftAction> threadActions = actionsManager.getThreadActions();

		for (Action action : dataActions) {
			jtoolBar.add(action);
		}
		//  separator
		jtoolBar.addSeparator();
		int count = 0;
		JButton informationButton = null;
		for (Action action : optionActions) {
			count++;
			JButton jButton = jtoolBar.add(action);
			if (count == 2) {
				informationButton = jButton;
			}
		}

		if (UnifiedAccessPoint.isFirstTimeLaunched()) {
			// 注册一个 Hint 事件
			String resourceString = UnifiedAccessPoint.getResourceString("Application.hint.info.button");
			Hint firstHint = new Hint(resourceString, informationButton, SwingConstants.BOTTOM);
			appendOneHint(firstHint);
		}
		//  separator
		jtoolBar.addSeparator();
		// 现在Thread Action只有暂停
		for (Action action : threadActions) {
			jtoolBar.add(action);
		}

		// toggleButtonToolBar.add(new ActionExit());

		int width2 = UnifiedAccessPoint.getLaunchProperty().getWidth();

		// The value 400 is adjusted for the width of the search Icon, the content is an Icon
		jtoolBar.add(Box.createHorizontalStrut(width2 / 2 - 400));
		jtoolBar.add(actionSearch);

		Border createEmptyBorder = BorderFactory.createEmptyBorder(0, 4, 0, 4);
		jtoolBar.setBorder(createEmptyBorder);
		jtoolBar.setBorderPainted(false);
		jtoolBar.setRollover(true);
		// 到最右边了
		jtoolBar.add(Box.createHorizontalGlue());


		MainFrameProperties.configAdditionalITools(jtoolBar);

		jtoolBar.add(actionsManager.getActionPreference());
		return jtoolBar;
	}


	JMenuBar createMenus() {
		// ***** create the menubar ****
		JMenuBar menuBar = new JMenuBar();
		LaunchProperty lauchProperty = UnifiedAccessPoint.getLaunchProperty();
		Font menuFistLevelFont = lauchProperty.getMenuFistLevelFont();
		Font menuSecondLevelFont = lauchProperty.getMenuSecondLevelFont();
		// ***** create File menu
		{
			JMenu fileMenu = menuBar.add(new JMenu(getString("FileMenu.label")));
			fileMenu.setToolTipText(getString("FileMenu.tooltip"));
			fileMenu.setFont(menuFistLevelFont);

			List<AdjustedSoftAction> dataActions = actionsManager.getDataActions();
			for (Action action : dataActions) {
				JMenuItem jMenuItem = createJMenuItem(action);
				jMenuItem.setFont(menuSecondLevelFont);
				fileMenu.add(jMenuItem);
			}
			fileMenu.addSeparator();
			JMenuItem jMenuItem = createJMenuItem(actionsManager.getActionExit());
			jMenuItem.setFont(menuSecondLevelFont);
			fileMenu.add(jMenuItem);
		}
		// ***** create mainframe core menu
		{
			JMenu fileMenu = menuBar.add(new JMenu(getString("MainframeCoreMenu.label")));
			fileMenu.setToolTipText(getString("MainframeCoreMenu.tooltip"));
			fileMenu.setFont(menuFistLevelFont);

            try {
                MainFrameProperties.loadInternalCoreModules(existedLoaders, fileMenu, this);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            //fileMenu.addSeparator();
		}

		// ***** create iTools menu
		{
			JMenu fileMenu = menuBar.add(new JMenu(getString("IToolsMenu.label")));
			fileMenu.setFont(menuFistLevelFont);
			fileMenu.setToolTipText(getString("IToolsMenu.tooltip"));

			MainFrameProperties.loadIndependentTools(existedLoaders, fileMenu, this);

			if (UnifiedAccessPoint.isFirstTimeLaunched()) {
				// 注册一个 Hint 事件
				String resourceString = UnifiedAccessPoint.getResourceString("Application.hint.itools.button");
				Hint firstHint = new Hint(resourceString, fileMenu, SwingConstants.BOTTOM);
				appendOneHint(firstHint);
			}
			logger.trace("Rdebug   MyFrame createMenus 682...");
		}
		// ***** create Plugins menu
		initializePluginsMenu(menuBar, lauchProperty, menuFistLevelFont, menuSecondLevelFont);
		// ***** options Menu
		{
			JMenu fileMenu = menuBar.add(new JMenu(getString("OptionsMenu.label")));
			fileMenu.setFont(menuFistLevelFont);

			List<Action> dataActions = actionsManager.getOptionActions();
			for (Action action : dataActions) {
				JMenuItem jMenuItem = createJMenuItem(action);
				jMenuItem.setFont(menuSecondLevelFont);
				fileMenu.add(jMenuItem);
			}
			fileMenu.addSeparator();

			JMenuItem preferenceItem = createJMenuItem(actionsManager.getActionPreference());
			preferenceItem.setFont(menuSecondLevelFont);
			fileMenu.add(preferenceItem);

			JMenuItem launchLastModuleItem = createJMenuItem(actionsManager.getActionLaunchLastModule());
			launchLastModuleItem.setFont(menuSecondLevelFont);
			fileMenu.add(launchLastModuleItem);
		}
		logger.trace("Rdebug   MyFrame createMenus 702...");
		// ****** Helps Menu
		{
			JMenu fileMenu = menuBar.add(new JMenu(getString("HelpMenu.label")));
			fileMenu.setFont(menuFistLevelFont);

			List<Action> dataActions = actionsManager.getAboutActions();
			for (Action action : dataActions) {
				JMenuItem jMenuItem = createJMenuItem(action);
				jMenuItem.setFont(menuSecondLevelFont);
				fileMenu.add(jMenuItem);
			}
		}
		logger.trace("Rdebug   MyFrame createMenus 715...");
		return menuBar;

	}

	private void initializePluginsMenu(JMenuBar menuBar, LaunchProperty lauchProperty, Font menuFistLevelFont,
			Font menuSecondLevelFont) {
		JMenu pluginsMenus = menuBar.add(new JMenu(getString("Plugins.label")));
		pluginsMenus.setFont(menuFistLevelFont);
		pluginsMenus.setToolTipText(getString("Plugins.tooltip"));
		logger.trace("Rdebug   MyFrame initializePluginsMenu 725...");

		if (Launcher.isLaunchFromR) {
			return;
		}
		UnifiedAccessPoint.registerActionAfterMainFrame(() -> {
			new PluginOperation().configMenu(pluginsMenus);
		});

		logger.trace("Rdebug   MyFrame initializePluginsMenu 732...");
		if (UnifiedAccessPoint.isFirstTimeLaunched()) {
			// 注册一个 Hint 事件
			String resourceString = UnifiedAccessPoint.getResourceString("Application.hint.plugin.button");
			Hint firstHint = new Hint(resourceString, pluginsMenus, SwingConstants.BOTTOM);
			appendOneHint(firstHint);
		}

	}

	private JMenuItem createJMenuItem(Action action) {
		JMenuItem jMenuItem = new JMenuItem(action);
		jMenuItem.setBorder(null);
		jMenuItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return jMenuItem;
	}

	private String getString(String string) {
		return MyResourceBundle.getString(string);
	}

	/**
	 * 这个方法声明为 Default类型，表示其实不需要被这个包意外的类调用。 也就是说只有这个主框架是需要调用这个方法的，其它类都不需要。
	 * 特别是模块类，它可以从ModuleFace这个类的一个继承方法来访问当前选中的模块
	 * 
	 * @return
	 */
	ModuleFace getSelectedModule() {
		Component selectedComponent = jTabbedPane.getSelectedComponent();
		ModuleFace selectedFace = (ModuleFace) selectedComponent;
		return selectedFace;
	}

	public void refreshAllActionsInToolbarPlusStatesBar() {

		SwingUtilities.invokeLater(() -> {

			ModuleFace selectedModule = getSelectedModule();
			List<AdjustedSoftAction> dataActions = actionsManager.getDataActions();

			for (AdjustedSoftAction adjustedSoftAction : dataActions) {
				adjustedSoftAction.setEnableStates(selectedModule);
			}
			List<AdjustedSoftAction> threadActions = actionsManager.getThreadActions();
			for (AdjustedSoftAction adjustedSoftAction : threadActions) {
				adjustedSoftAction.setEnableStates(selectedModule);
			}

			Triple<ActionHelp, ActionInformation, ActionStatics> tripleTools = actionsManager.getTripleTools();
			tripleTools.getLeft().setEnableStates(selectedModule);
			tripleTools.getMiddle().setEnableStates(selectedModule);
			tripleTools.getRight().setEnableStates(selectedModule);

			buttomStatesBar.refreshStates();
		});

	}

	private void configGlassPanel() {
		boolean visible = glassPane.isVisible();
		if (visible) {
			glassPane.revalidate();
		} else {
			glassPane.setVisible(true);
		}

	}

	void removeCompoentInGlassPanel(JComponent component) {
        if (glassPane == null) {
			return;
		}
		glassPane.remove(component);

		int componentCount = glassPane.getComponentCount();
		if (componentCount == 0) {
			glassPane.setVisible(false);
		}
	}

	/**
	 * If str is null, show the develop team.
	 * 
	 * @param str
	 */
	public void showTipsOnBottomStatusBar(String str) {
		SwingUtilities.invokeLater(() -> {
			buttomStatesBar.showTipsOnBottomStatusBar(str);
		});
	}

	public void prompt(String string) {
		SwingUtilities.invokeLater(() -> {
			promptPanel.prompt(string, 5000);
			configGlassPanel();
		});
	}

	public void reachOneAchievements(String title, String contentString) {

		Objects.requireNonNull(title);
		Objects.requireNonNull(contentString);

		int width2 = getWidth();
		int height2 = getHeight();

		GlassPanelAchievement achievementPanel = new GlassPanelAchievement(this);
		achievementPanel.setTitle(title);
		achievementPanel.setContent(contentString);

		achievementPanel.setBounds(width2 / 2 - 350, height2 / 2 - 200, 350, 350);

		glassPane.add(achievementPanel);

		configGlassPanel();
	}

	/**
	 * 更新EGPS主框架的Gesture机制：支持不设置提示语，提示语边框可以上下左右自己设置，提示语相对于按钮的位置可以自己设置。这个Gesture方框可以设置大小。
	 *
	 * @param config 包含要显示的Gesture配置的MainFrameGestureConfig对象
	 */
	public void showGesture(MainFrameGestureConfig config) {

		JComponent getjComponent = config.getjComponent();
		Point location = getjComponent.getLocation();
//		logger.trace(jtoolBar.getHeight());
		Point convertPoint = SwingUtilities.convertPoint(getjComponent, 0, 0, glassPane);
//		logger.trace(location);
//		logger.trace(convertPoint);

		GlassPanelGesture gesturePanel = new GlassPanelGesture(this, config);

		String clickmeGestureLocation = config.getClickmeGestureLocation();
		switch (clickmeGestureLocation) {
//		case BorderLayout.WEST:
//			
//			break;
		case BorderLayout.EAST:
			gesturePanel.setBounds(convertPoint.x - config.width, convertPoint.y, config.width, config.height);
			break;
//		case BorderLayout.SOUTH:
//			break;
		default:
			gesturePanel.setBounds(convertPoint.x, convertPoint.y, config.width, config.height);
			break;
		}

		glassPane.add(gesturePanel);

		configGlassPanel();
	}

	public void becomeBusy(boolean busy) {

		if (busy) {
			ModuleFace selectedModule = getSelectedModule();
			Rectangle bounds = selectedModule.getBounds();

			Rectangle convertRectangle = SwingUtilities.convertRectangle(selectedModule, bounds, getContentPane());

			JLabel waitingLabel = glassPanelBusyStates.getWaitingLabel();
			waitingLabel.setBounds(convertRectangle);

			glassPane.add(waitingLabel);

			configGlassPanel();
		} else {
			removeCompoentInGlassPanel(glassPanelBusyStates.getWaitingLabel());
		}

		/*
		 * Rectangle bounds = null; JRootPane rootPane2 = getRootPane(); int totalHeight
		 * = rootPane2.getBounds().height;
		 * 
		 * bounds = getSelectedModule().getBounds();
		 * 
		 * Rectangle jmenuBar = getJMenuBar().getBounds(); final int
		 * heightOfMenuBarPlusContentPane = jmenuBar.height +
		 * getContentPane().getBounds().height; final int heightOfTitleBar = 25;
		 * 
		 * // 注意 Glass Panel的 （0，0） 位置是包含了最上面的窗体的标题的 glassPane.veilX = bounds.x;
		 * glassPane.veilY = jmenuBar.y + jmenuBar.height + jtoolBar.getBounds().height
		 * + historyButton.getBounds().height; glassPane.veilWidth = bounds.width;
		 * glassPane.veilHeight = bounds.height;
		 * 
		 * glassPane.updateWaitingLabel();
		 * 
		 * glassPane.setVisible(busy);
		 */
	}

	/**
	 * 执行软件退出过程，在退出之前会进行一些事情，所以需要有单独这个过程
	 */
	public void exitSoftware() {

		// check every module, if the module has important information, such as runnable
		// task or graphics
		int tabCount = jTabbedPane.getTabCount();

		boolean shouldAskUsers = false;
		for (int i = 0; i < tabCount; i++) {

			ModuleFace tabComponentAt = (ModuleFace) jTabbedPane.getComponentAt(i);
			boolean closeTab = tabComponentAt.moduleExisted();
			if (closeTab) {
				shouldAskUsers = true;
				break;
			}
		}

		boolean stillExist = true;
		if (shouldAskUsers) {
			int showConfirmDialog = JOptionPane.showConfirmDialog(this, getString("FileMenu.exit.content"));
			if (showConfirmDialog == JOptionPane.YES_OPTION) {
			} else {
				stillExist = false;
			}
		} else {

		}

		if (stillExist) {
			LaunchProperty lauchProperty = UnifiedAccessPoint.getLaunchProperty();

			try {
				if (lauchProperty.isRestoreToDefault()) {
					lauchProperty.saveTheProperties(true);
				} else {
					lauchProperty.setLaunchTimes(lauchProperty.getLaunchTimes() + 1);

					Dimension size = getSize();
					Point locationOnScreen = getLocationOnScreen();
					lauchProperty.setLocationX(locationOnScreen.x);
					lauchProperty.setLocationY(locationOnScreen.y);
					lauchProperty.setWidth(size.width);
					lauchProperty.setHeight(size.height);

					lauchProperty.saveTheProperties(false);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			this.dispose();// exist to dispose;
		}

	}

	/**
	 * 一个加载 模块的统一接口。
	 * 
	 * @param name
	 * @param icon
	 * @param face
	 * @param shortDescription
	 */
	public void addTab2mainTabbedPanel(String name, Icon icon, ModuleFace face, String shortDescription) {
		SwingUtilities.invokeLater(() -> {
			jTabbedPane.addTab(name, icon, face, shortDescription);
			jTabbedPane.setSelectedComponent(face);

			SwingUtilities.invokeLater(() -> {face.initializeModuleFaceGUI();face.requestFocusInWindow();});
		});

	}

	public void onlyRefreshButtomStatesBar(ModuleFace face, String string, int value) {
		Objects.requireNonNull(face);
		if (getSelectedModule() == face) {
			JProgressBar getjProgressBar = buttomStatesBar.getjProgressBar();
			getjProgressBar.setValue(value);
			if (string != null) {
				getjProgressBar.setString(string);
			}

			buttomStatesBar.refreshStates();
		}
	}

	public void refreshRightGraphicPropertiesPanel() {
		rightGraphicsPropertiesPanel.refreshStates();
	}

	public JideTabbedPane getJTabbedPanel() {
		return jTabbedPane;
	}

	public JToolBar getJtoolBar() {
		return jtoolBar;
	}
}
