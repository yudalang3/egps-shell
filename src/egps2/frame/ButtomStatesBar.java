package egps2.frame;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.tuple.Triple;
import org.jdesktop.swingx.JXStatusBar;
import org.jdesktop.swingx.JXTipOfTheDay;
import org.jdesktop.swingx.plaf.basic.BasicStatusBarUI;
import org.jdesktop.swingx.tips.TipLoader;
import org.jdesktop.swingx.tips.TipOfTheDayModel;

import egps2.UnifiedAccessPoint;
import egps2.modulei.CreditBean;
import egps2.modulei.RunningTask;

/**
 * 底部状态栏组件，显示开发团队信息、提示和进度状态。
 * Bottom status bar component displaying development team information, tips, and progress status.
 *
 * <p>此状态栏使用CardLayout实现两种显示模式的动态切换：
 * 正常模式显示开发团队信息和功能按钮，忙碌模式显示进度条。
 * This status bar uses CardLayout to dynamically switch between two display modes:
 * normal mode displays development team information and functional buttons, busy mode displays progress bar.
 *
 * <p><strong>两种显示模式：</strong>
 * Two display modes:
 * <ul>
 *   <li><b>信用信息模式 (Credit mode)</b> - 默认模式，显示：
 *     <ul>
 *       <li>实验室/团队名称按钮（可点击跳转网站）</li>
 *       <li>开发人员名单按钮（点击显示详细信息）</li>
 *       <li>每日提示按钮（显示使用技巧）</li>
 *       <li>最新版本按钮</li>
 *     </ul>
 *   </li>
 *   <li><b>进度条模式 (Progress mode)</b> - 任务运行时，显示进度条（确定或不确定）</li>
 * </ul>
 *
 * <p><strong>布局结构（信用模式）：</strong>
 * Layout structure (credit mode):
 * <pre>
 * |---------------------------------------------------------------------------|
 * | devWebButton    |  devNamesButton (FILL)  |  tipsButton | versionButton |
 * |---------------------------------------------------------------------------|
 * </pre>
 *
 * <p><strong>状态刷新机制：</strong>
 * Status refresh mechanism:
 * <ul>
 *   <li>{@link #refreshStates()} 根据当前选中模块的状态自动切换显示模式</li>
 *   <li>当模块实现 {@link ComputationalModuleFace} 且有运行任务时，切换到进度条模式</li>
 *   <li>当没有运行任务时，切换回信用信息模式并显示当前模块的开发团队信息</li>
 *   <li>Automatically switches display mode based on current selected module's status</li>
 *   <li>Switches to progress mode when module implements {@link ComputationalModuleFace} and has running task</li>
 *   <li>Switches back to credit mode when no running task, displays current module's development team info</li>
 * </ul>
 *
 * <p><strong>进度显示：</strong>
 * Progress display:
 * <ul>
 *   <li><b>确定进度：</b>显示百分比进度条（任务 {@link RunningTask#isTimeCanEstimate()} 返回true）</li>
 *   <li><b>不确定进度：</b>显示滚动动画进度条（任务无法估算时间）</li>
 * </ul>
 *
 * <p><strong>功能按钮：</strong>
 * Function buttons:
 * <ul>
 *   <li><b>devWebButton：</b>点击跳转到当前模块开发团队的网站</li>
 *   <li><b>devNamesButton：</b>点击弹出对话框显示开发人员详细名单</li>
 *   <li><b>tipsButton：</b>显示"每日提示"对话框，展示软件使用技巧</li>
 *   <li><b>versionButton：</b>检查和显示最新版本信息</li>
 * </ul>
 *
 * <p>线程安全：状态栏的刷新操作应在EDT（Event Dispatch Thread）中调用。
 * Thread safety: Status bar refresh operations should be called in EDT (Event Dispatch Thread).
 *
 * @see MyFrame
 * @see ComputationalModuleFace
 * @see RunningTask
 * @see egps2.modulei.CreditBean
 * @author eGPS Dev Team
 * @since 2.0
 */
public class ButtomStatesBar extends JPanel {

	private JXStatusBar creditJPanel;
	private JPanel progressJPanel;

	private JProgressBar jProgressBar;

	private MyFrame mainFrame;
	private boolean isCredit = true;

	/**
	 * 最左边，点击一下 跳转到网页
	 */
	private JButton devWebButton;
	/**
	 * 第二个，点一下显示开发人员
	 */
	private JButton devNamesButton;

	private Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

	private final String creditString = "first";
	private final String progressString = "second";

	private CardLayout cardLayout;

	/**
	 * 
	 * <pre>
	 * 使用CardLayout组织了两种状态：
	 * 第一种是 creditBar 状态
	 * 
	 * |---------------------------------------------------------------------------|
	 * | devWebButton    |  devNamesButton    |  tipsButton |  latestVersionButton |
	 * |---------------------------------------------------------------------------|
	 * 
	 * 第二种是：单纯一个进度条
	 * 
	 * 一个jProgressBar
	 * 
	 * </pre>
	 * 
	 */
	public ButtomStatesBar(MyFrame myFrame) {
		cardLayout = new CardLayout();
		setLayout(cardLayout);
		setPreferredSize(new Dimension(29990, 35));
		setBorder(BorderFactory.createRaisedSoftBevelBorder());

		this.mainFrame = myFrame;

		creditJPanel = new JXStatusBar();
		creditJPanel.putClientProperty(BasicStatusBarUI.AUTO_ADD_SEPARATOR, false);
		creditJPanel.setBorder(null);

		creditJPanel.add(getDevWebButton());
		JSeparator comp = new JSeparator(JSeparator.VERTICAL);
		comp.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 30));

		creditJPanel.add(comp);
		// Fill with no inserts
		JXStatusBar.Constraint c2 = new JXStatusBar.Constraint(JXStatusBar.Constraint.ResizeBehavior.FILL);
		// Fill with no inserts - will use remaining space
		creditJPanel.add(getDevNamesButton(), c2);

		creditJPanel.add(new JSeparator(JSeparator.VERTICAL));
		creditJPanel.add(getTipsButton());
		creditJPanel.add(getVersionButton());

		progressJPanel = new JPanel(new BorderLayout());
		jProgressBar = new JProgressBar();
		jProgressBar.setFont(defaultFont);
		progressJPanel.add(jProgressBar, BorderLayout.CENTER);
		jProgressBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		jProgressBar.setStringPainted(true);
		jProgressBar.setIndeterminate(false);

		setDefaultDevTeam();
		add(creditJPanel, creditString);
		add(jProgressBar, progressString);
		isCredit = true;
	}

	private void setDefaultDevTeam() {
		Triple<String, String, String> devTeam = MainFrameProperties.getDevTeam();
		setDevTeam(devTeam.getLeft(), devTeam.getMiddle());
	}

	private JButton getDevWebButton() {
		if (devWebButton != null) {
			return devWebButton;
		}

		devWebButton = new JButton();
		devWebButton.setFont(defaultFont);
		devWebButton.setFocusable(false);
		devWebButton.setToolTipText("Click to see the website of the laboratory page.");
		devWebButton.setIcon(new ImageIcon(UnifiedAccessPoint.getImageResource("statesBar/lab.png")));
		devWebButton.addActionListener(e -> {
			MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
			ModuleFace selectedModule = instanceFrame.getSelectedModule();
			if (selectedModule == null) {
				ActionsManager.getInstance().getHomePageAction().actionPerformed(e);
			} else {
				boolean desktopSupported = Desktop.isDesktopSupported();
				if (!desktopSupported) {
					return;
				}
				Desktop desktop = Desktop.getDesktop();
				try {

					CreditBean devTeam = selectedModule.getDevTeam();
					if (checkDevTeam(devTeam)) {
						return;
					}
					String right = devTeam.getWebSite();
					desktop.browse(new URI(right));
				} catch (IOException | URISyntaxException e1) {
					e1.printStackTrace();
				}
			}

		});

		return devWebButton;
	}

	public JButton getDevNamesButton() {
		if (devNamesButton != null) {
			return devNamesButton;
		}
		devNamesButton = new JButton();
		devNamesButton.setFont(defaultFont);
		devNamesButton.setFocusable(false);
		devNamesButton.addActionListener(e -> {
			String resourceString = "";
			MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
			ModuleFace selectedModule = instanceFrame.getSelectedModule();
			if (selectedModule == null) {
			} else {
				CreditBean devTeam = selectedModule.getDevTeam();
				if (checkDevTeam(devTeam)) {
					return;
				}
				String middle = devTeam.getDevelopers();
				resourceString = resourceString.concat(middle);
			}
			final URL img = UnifiedAccessPoint.getImageResource("statesBar/devTeam.jpg");

			String[] ret = { "<html><body><center>", "<img src=\"", img.toString(), "\" width=\"1024\" height=\"565\">",
					"<br>", resourceString,

					"</center><br></body></html>" };

			String message2 = MainFrameProperties.wrapStringArraysAsString(ret);

			JOptionPane.showMessageDialog(mainFrame, message2, "Development team:", JOptionPane.PLAIN_MESSAGE, null);
		});

		return devNamesButton;
	}

	private JButton getVersionButton() {
		JButton versionButton = new JButton();
		versionButton.setToolTipText("Click to see the website of this software.");
		versionButton.setText("Latest version");
		versionButton.setFont(defaultFont);
		versionButton.setFocusable(false);
		versionButton.setIcon(new ImageIcon(UnifiedAccessPoint.getImageResource("statesBar/browser.png")));
		versionButton.setHorizontalAlignment(SwingConstants.RIGHT);
		versionButton.setHorizontalTextPosition(SwingConstants.LEFT);
		return versionButton;
	}

	private JButton getTipsButton() {
		JButton tipsButton = new JButton();
		tipsButton.setToolTipText("Tips of using software.");
		tipsButton.setIcon(new ImageIcon(UnifiedAccessPoint.getImageResource("statesBar/tips.jpg")));
		tipsButton.addActionListener(e -> {

			Properties tips = new Properties();

			InputStream resourceAsStream = this.getClass().getResourceAsStream("/tipsOfDay.properties");
			try {
				tips.load(resourceAsStream);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			TipOfTheDayModel model = TipLoader.load(tips);
			JXTipOfTheDay tipOfDayComp = new JXTipOfTheDay(model);
			tipOfDayComp.setFont(defaultFont);

			int curr = UnifiedAccessPoint.getLaunchProperty().getCurrentToolTip();
			tipOfDayComp.setCurrentTip(curr);
			

			MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
			JDialog jDialog = tipOfDayComp.getUI().createDialog(instanceFrame, null);
			jDialog.setTitle("Tip of day");
			jDialog.add(tipOfDayComp, BorderLayout.CENTER);
			

			ActionListener escListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					UnifiedAccessPoint.getLaunchProperty().setCurrentToolTip(tipOfDayComp.getCurrentTip());
					jDialog.dispose();
				}
			};

			jDialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
					JComponent.WHEN_IN_FOCUSED_WINDOW);

			jDialog.setSize(500, 400);
			jDialog.setLocationRelativeTo(instanceFrame);
			jDialog.setVisible(true);

		});
		return tipsButton;
	}

	private void switch2creditBar() {

		if (isCredit) {

		} else {
			SwingUtilities.invokeLater(() -> {
				jProgressBar.setIndeterminate(false);
				cardLayout.show(this, creditString);
			});
		}

		isCredit = true;

	}

	private void switch2jprogressBar(boolean timeCanEstimate) {
		if (isCredit) {
			SwingUtilities.invokeLater(() -> {
				cardLayout.show(this, progressString);
			});

			isCredit = false;
		}

	}

	public void refreshStates() {
		ModuleFace selectedModule = mainFrame.getSelectedModule();

		RunningTask runningTask2 = getRunningTask(selectedModule);

		if (runningTask2 == null) {
			// Has running task
			mainFrame.becomeBusy(false);
			if (selectedModule == null) {
				setDefaultDevTeam();
			} else {
				setDevTeam(selectedModule);
			}
			switch2creditBar();
		} else {
			boolean timeCanEstimate = runningTask2.isTimeCanEstimate();

			switch2jprogressBar(timeCanEstimate);

			if (timeCanEstimate) {
				jProgressBar.setIndeterminate(false);
				ComputationalModuleFace computationalModuleFace = (ComputationalModuleFace) selectedModule;
				jProgressBar.setValue(computationalModuleFace.getProgressIndexIfNeeded());
			} else {
				jProgressBar.setIndeterminate(true);
			}
			mainFrame.becomeBusy(true);
		}

	}

	private RunningTask getRunningTask(ModuleFace selectedModule) {
		if (selectedModule != null && selectedModule instanceof ComputationalModuleFace) {
			ComputationalModuleFace computationalModuleFace = (ComputationalModuleFace) selectedModule;

			Optional<RunningTask> runningTask = computationalModuleFace.getRunningTask();

			if (runningTask.isPresent()) {
				RunningTask runningTask2 = runningTask.get();
				return runningTask2;
			}
		}
		return null;
	}

	private void setDevTeam(ModuleFace selectedModule) {
		CreditBean devTeam = selectedModule.getDevTeam();

		if (devTeam == null) {
			setDevTeam("Unknown", "Unknown");
		} else {
			setDevTeam(devTeam.getTeam(), devTeam.getDevelopers());
		}
	}

	private void setDevTeam(String lab, String names) {
		int maxLength = 50;
		if (lab.length() > maxLength) {
			lab = lab.substring(0, maxLength).concat("...");
		}
		devWebButton.setText(lab);

		maxLength = 100;
		if (names.length() > maxLength) {
			names = names.substring(0, maxLength).concat("...");
		}
		devNamesButton.setText(names);
	}

	/**
	 * 
	 * @param devTeam
	 * @return should stop.
	 */
	private boolean checkDevTeam(CreditBean devTeam) {
		if (devTeam == null) {
			JOptionPane.showMessageDialog(mainFrame, "Sorry, the develops has not provide develop informations.");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 这个方法只能被 MainFrame访问，用来快速更新
	 * 
	 * @return
	 */
	JProgressBar getjProgressBar() {
		return jProgressBar;
	}

	public void showTipsOnBottomStatusBar(String str) {
		if (str == null) {
			refreshStates();
		} else {
			getDevNamesButton().setText(str);
		}

	}

}
