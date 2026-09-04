package egps2.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.google.common.base.Strings;

import egps2.UnifiedAccessPoint;
import egps2.frame.HintManager.BalloonBorder;
import egps2.panels.DynGifLabel;

/**
 * 手势动画玻璃面板，在主窗口玻璃层显示"点击我"动画和提示气泡。
 * Gesture animation glass panel displaying "click me" animation and hint balloon on main window glass layer.
 *
 * <p>此组件显示在主窗口的玻璃面板层（Glass Pane），用于引导用户关注特定功能或重要更新。
 * 它包含一个GIF动画手势图标和一个可选的文本提示气泡。
 * This component displays on the main window's glass pane layer, used to guide users' attention to specific features or important updates.
 * It contains a GIF animation gesture icon and an optional text hint balloon.
 *
 * <p><strong>组件构成：</strong>
 * Component composition:
 * <ul>
 *   <li><b>手势动画：</b>循环播放的"点击我" GIF动画，默认尺寸50x50像素</li>
 *   <li><b>提示气泡：</b>带箭头的气泡文本框，显示HTML格式的提示内容</li>
 *   <li><b>回调动作：</b>用户点击后执行的可选回调函数</li>
 * </ul>
 *
 * <p><strong>显示位置：</strong>
 * Display position:
 * <br>手势动画和提示气泡的相对位置由 {@link MainFrameGestureConfig#clickmeGestureLocation} 决定：
 * <ul>
 *   <li><b>BorderLayout.NORTH</b> - 动画在上，气泡在下</li>
 *   <li><b>BorderLayout.SOUTH</b> - 动画在下，气泡在上</li>
 *   <li><b>BorderLayout.WEST</b> - 动画在左，气泡在右</li>
 *   <li><b>BorderLayout.EAST</b> - 动画在右，气泡在左</li>
 * </ul>
 *
 * <p><strong>交互行为：</strong>
 * Interaction behavior:
 * <ul>
 *   <li>点击动画图标：停止动画，移除玻璃面板组件，触发回调</li>
 *   <li>气泡内容支持HTML格式，可包含文本、链接等</li>
 *   <li>回调在EDT线程中执行</li>
 * </ul>
 *
 * <p><strong>生命周期：</strong>
 * Lifecycle:
 * <ol>
 *   <li>创建：通过 {@link MainFrameGestureConfig} 配置参数</li>
 *   <li>显示：添加到主窗口的玻璃面板</li>
 *   <li>交互：用户点击或程序调用 {@link #stop()}</li>
 *   <li>清理：停止GIF动画，从玻璃面板移除，执行回调</li>
 * </ol>
 *
 * @see MainFrameGestureConfig
 * @see MyFrame#showGesture(MainFrameGestureConfig)
 * @see HintManager.BalloonBorder
 * @see egps2.panels.DynGifLabel
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
class GlassPanelGesture extends JComponent {

	private DynGifLabel gesture;
	private Runnable callBackFunc;
    private MyFrame myFrame;

	public GlassPanelGesture(MyFrame myFrame, MainFrameGestureConfig config) {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

		this.myFrame = myFrame;
		this.callBackFunc = config.callBackFunc;

		gesture = new DynGifLabel(UnifiedAccessPoint.getImageResourceAsStream("miscellaneous/clickme.gif"));
		gesture.setPreferredSize(new Dimension(50, 50));
		gesture.setDoWhenClick(() -> {
			stop();
		});
		add(gesture, config.clickmeGestureLocation);

		boolean nullOrEmpty = Strings.isNullOrEmpty(config.content);
		if (!nullOrEmpty) {
			JLabel contentJLabel = new JLabel();
			contentJLabel.setHorizontalAlignment(JLabel.CENTER);
			contentJLabel.setOpaque(true);
			//contentJLabel.setBackground(new Color(255, 255, 255, 15));
			contentJLabel.setForeground(Color.BLACK);
			
			contentJLabel.setBorder(new BalloonBorder(config.balloonBorderTipLocation, Color.blue));
			contentJLabel.setText(config.content);

			add(contentJLabel, BorderLayout.CENTER);
		}
	}

	public void stop() {
		gesture.stop();
		myFrame.removeCompoentInGlassPanel(this);

		SwingUtilities.invokeLater(() -> {
			if (callBackFunc != null) {
				callBackFunc.run();
			}
		});
	}


}
