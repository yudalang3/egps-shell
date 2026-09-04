package egps2.frame;

import java.awt.BorderLayout;
import java.util.InputMismatchException;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * 主框架手势动画配置Bean类，用于配置和显示界面引导手势提示。
 * Main frame gesture animation configuration bean class for configuring and displaying UI guidance gesture hints.
 *
 * <p>此类封装了手势提示的所有显示参数，包括提示内容、位置、尺寸和回调动作。
 * 手势提示通常用于引导用户关注重要功能或新版本更新等场景。
 * This class encapsulates all display parameters for gesture hints, including hint content, position, size, and callback actions.
 * Gesture hints are typically used to guide users' attention to important features or new version updates.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>配置手势组件和提示内容 - Configure gesture component and hint content</li>
 *   <li>设置提示气泡的位置和尺寸 - Set hint balloon position and size</li>
 *   <li>自动计算气泡尖端方向 - Automatically calculate balloon tip direction</li>
 *   <li>支持HTML格式的提示文本 - Support HTML-formatted hint text</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
 * MainFrameGestureConfig config = new MainFrameGestureConfig(
 *     downLoadButton,
 *     "<html><blockquote>New version available.</blockquote></html>",
 *     downloadAction
 * );
 * config.setHeight(200);
 * config.setWidth(300);
 * config.setClickmeGestureLocation(BorderLayout.EAST);
 * instanceFrame.showGesture(config);
 * }</pre>
 *
 * <p><strong>位置映射：</strong>
 * Position mapping:
 * <ul>
 *   <li><b>BorderLayout.NORTH</b> → 气泡尖端朝上 (balloon tip points up)</li>
 *   <li><b>BorderLayout.SOUTH</b> → 气泡尖端朝下 (balloon tip points down)</li>
 *   <li><b>BorderLayout.WEST</b> → 气泡尖端朝左 (balloon tip points left)</li>
 *   <li><b>BorderLayout.EAST</b> → 气泡尖端朝右 (balloon tip points right)</li>
 * </ul>
 *
 * @implSpec 这是一个纯配置Bean类，包含位置、尺寸和内容属性的getter/setter方法。
 *           This is a pure configuration bean class containing getter/setter methods for position, size, and content properties.
 *
 * @see MyFrame#showGesture(MainFrameGestureConfig)
 * @see GlassPanelGesture
 * @see HintManager.BalloonBorder
 * @author eGPS Dev Team
 * @since 2.1
 */
public class MainFrameGestureConfig {

	JComponent jComponent;
	/**
	 * Html content
	 */
	String content;

	/**
	 * The call back function
	 */
	Runnable callBackFunc;

	/**
	 * Content JLabel height
	 */
	int height;
	/**
	 * Content JLabel width
	 */
	int width;

	String clickmeGestureLocation = BorderLayout.WEST;

	int balloonBorderTipLocation = SwingConstants.LEFT;

	public MainFrameGestureConfig(JComponent jComponent, String content, Runnable callBackFunc) {
		super();
		this.jComponent = jComponent;
		this.content = content;
		this.callBackFunc = callBackFunc;
	}

	public void setClickmeGestureLocation(String clickmeGestureLocation) {
		this.clickmeGestureLocation = clickmeGestureLocation;
		switch (clickmeGestureLocation) {
		case BorderLayout.SOUTH:
			balloonBorderTipLocation = SwingConstants.BOTTOM;
			break;
		case BorderLayout.WEST:
			balloonBorderTipLocation = SwingConstants.LEFT;
			break;
		case BorderLayout.EAST:
			balloonBorderTipLocation = SwingConstants.RIGHT;
			break;
		case BorderLayout.NORTH:
			balloonBorderTipLocation = SwingConstants.TOP;
			break;
		default:
			throw new InputMismatchException("Please set the right location.");
		}
	}

	public int getBalloonBorderTipLocation() {
		return balloonBorderTipLocation;
	}

	public JComponent getjComponent() {
		return jComponent;
	}

	public void setjComponent(JComponent jComponent) {
		this.jComponent = jComponent;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Runnable getCallBackFunc() {
		return callBackFunc;
	}

	public void setCallBackFunc(Runnable callBackFunc) {
		this.callBackFunc = callBackFunc;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getClickmeGestureLocation() {
		return clickmeGestureLocation;
	}

}
