package egps2.frame;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import egps2.UnifiedAccessPoint;
import egps2.modulei.AdjusterSizeAndPosition;
import net.miginfocom.swing.MigLayout;

/**
 * 即时尺寸和位置调整面板，提供交互式旋转、缩放和定位控件。
 * Instant size and position adjustment panel providing interactive rotation, scaling, and positioning controls.
 *
 * <p>此面板通过多个微调器（Spinner）控件，为实现了 {@link egps2.modulei.AdjusterSizeAndPosition} 接口的模块
 * 提供即时可视化调整功能。用户可以实时调整选中对象的尺寸、位置和旋转角度。
 * This panel provides instant visual adjustment functionality for modules implementing {@link egps2.modulei.AdjusterSizeAndPosition} interface
 * through multiple spinner controls. Users can adjust size, position, and rotation angle of selected objects in real-time.
 *
 * <p><strong>功能控件（两部分）：</strong>
 * Function controls (two sections):
 * <ul>
 *   <li><b>尺寸调整 (Size)：</b>
 *     <ul>
 *       <li>Height（高度）: 1-1000像素</li>
 *       <li>Width（宽度）: 1-1000像素</li>
 *       <li>Rotation（旋转）: 1-360度</li>
 *     </ul>
 *   </li>
 *   <li><b>位置调整 (Position)：</b>
 *     <ul>
 *       <li>Horizontal（水平位置）: 1-1000像素</li>
 *       <li>Vertical（垂直位置）: 1-1000像素</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><strong>动态启用机制：</strong>
 * Dynamic enabling mechanism:
 * <br>通过 {@link #refreshStates()} 方法根据当前选中模块的能力动态启用/禁用控件：
 * <ul>
 *   <li>仅当模块实现 {@link egps2.modulei.AdjusterSizeAndPosition} 时控件才可用</li>
 *   <li>各控件根据模块的 {@code couldAdjustSize()}, {@code couldAdjustPosition()}, {@code couldRotation()} 返回值独立启用</li>
 *   <li>Controls are only available when module implements {@link egps2.modulei.AdjusterSizeAndPosition}</li>
 *   <li>Each control is independently enabled based on module's capability methods</li>
 * </ul>
 *
 * <p><strong>即时调整：</strong>
 * Instant adjustment:
 * <br>实现了 {@link ChangeListener}，所有微调器值变化立即触发模块的相应调整方法：
 * <ul>
 *   <li>尺寸变化 → {@link egps2.modulei.AdjusterSizeAndPosition#adjustSize(int, int)}</li>
 *   <li>位置变化 → {@link egps2.modulei.AdjusterSizeAndPosition#adjustPosition(int, int)}</li>
 *   <li>旋转变化 → {@link egps2.modulei.AdjusterSizeAndPosition#adjustRotation(int)}</li>
 * </ul>
 *
 * <p><strong>布局：</strong>
 * Layout:
 * <br>使用 MigLayout 实现清晰的标签-输入框两列布局，中间带分隔符。
 * Uses MigLayout for clear label-input two-column layout with separator.
 *
 * @see egps2.modulei.AdjusterSizeAndPosition
 * @see MyFrame
 * @author eGPS Dev Team
 * @since 2.0
 */
@SuppressWarnings("serial")
public class InstantSizeAndPositionJPanel extends JPanel implements ChangeListener {
	private JSpinner spinnerPositionHorizontal;
	private JSpinner spinnerSizeRotation;
	private JSpinner spinnerSizeWidth;
	private JSpinner spinnerSizeHeight;
	private JSpinner spinnerPositionVertical;
	private MyFrame instanceFrame;
	
	public InstantSizeAndPositionJPanel() {

		setBorder(new EmptyBorder(6, 6, 6, 6));
		setLayout(new MigLayout("", "[][grow][]", "[][][][][][][][][]"));

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();

		JLabel lblSizeTitle = new JLabel("Size :");
		lblSizeTitle.setFont(defaultTitleFont);
		add(lblSizeTitle, "cell 0 0 3 1");

		JLabel lblHeight = new JLabel("Height");
		lblHeight.setFont(defaultFont);
		add(lblHeight, "cell 0 1");

		spinnerSizeHeight = new JSpinner();
		spinnerSizeHeight.addChangeListener(this);
		spinnerSizeHeight.setModel(new SpinnerNumberModel(60, 1, 1000, 1));
		spinnerSizeHeight.setFont(defaultFont);
		add(spinnerSizeHeight, "cell 2 1");

		JLabel lblWidth = new JLabel("Width");
		lblWidth.setFont(defaultFont);
		add(lblWidth, "cell 0 2");

		spinnerSizeWidth = new JSpinner();
		spinnerSizeWidth.addChangeListener(this);
		spinnerSizeWidth.setModel(new SpinnerNumberModel(60, 1, 1000, 1));
		spinnerSizeWidth.setFont(defaultFont);
		add(spinnerSizeWidth, "cell 2 2");

		JLabel lblRotation = new JLabel("Rotation");
		lblRotation.setFont(defaultFont);
		add(lblRotation, "cell 0 3,alignx left");

		spinnerSizeRotation = new JSpinner();
		spinnerSizeRotation.addChangeListener(this);
		spinnerSizeRotation.setModel(new SpinnerNumberModel(60, 1, 360, 1));
		spinnerSizeRotation.setFont(defaultFont);
		add(spinnerSizeRotation, "cell 2 3,growx");

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 5 3 1,growx");

		JLabel lblPositionTitle = new JLabel("Position :");
		lblPositionTitle.setFont(defaultTitleFont);
		add(lblPositionTitle, "cell 0 6 3 1");

		JLabel lblHorizontal = new JLabel("Horizontal");
		lblHorizontal.setFont(defaultFont);
		add(lblHorizontal, "cell 0 7");

		spinnerPositionHorizontal = new JSpinner();
		spinnerPositionHorizontal.addChangeListener(this);
		spinnerPositionHorizontal.setModel(new SpinnerNumberModel(60, 1, 1000, 1));
		spinnerPositionHorizontal.setFont(defaultFont);
		add(spinnerPositionHorizontal, "cell 2 7");

		JLabel lblVertical = new JLabel("Vertical");
		lblVertical.setFont(defaultFont);
		add(lblVertical, "cell 0 8");

		spinnerPositionVertical = new JSpinner();
		spinnerPositionVertical.addChangeListener(this);
		spinnerPositionVertical.setModel(new SpinnerNumberModel(60, 1, 1000, 1));
		spinnerPositionVertical.setFont(defaultFont);
		add(spinnerPositionVertical, "cell 2 8");
	}

	public void refreshStates() {
		MyFrame mainFrame = getMainFrame();
		ModuleFace selectedModule = mainFrame.getSelectedModule();

		if (selectedModule instanceof AdjusterSizeAndPosition) {
			setEnableStates((AdjusterSizeAndPosition) selectedModule);
		} else {
			spinnerPositionVertical.setEnabled(false);
			spinnerPositionHorizontal.setEnabled(false);
			spinnerSizeRotation.setEnabled(false);
			spinnerSizeWidth.setEnabled(false);
			spinnerSizeHeight.setEnabled(false);
		}

	}

	private void setEnableStates(AdjusterSizeAndPosition selectedModule) {
		spinnerPositionVertical.setEnabled(selectedModule.couldAdjustPosition().isPresent());
		spinnerPositionHorizontal.setEnabled(selectedModule.couldAdjustPosition().isPresent());

		spinnerSizeWidth.setEnabled(selectedModule.couldAdjustSize().isPresent());
		spinnerSizeHeight.setEnabled(selectedModule.couldAdjustSize().isPresent());
		spinnerSizeRotation.setEnabled(selectedModule.couldRotation().isPresent());

	}

	private MyFrame getMainFrame() {
		if (instanceFrame == null) {
			instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		}
		return instanceFrame;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		MyFrame mainFrame = getMainFrame();
		ModuleFace selectedModule = mainFrame.getSelectedModule();
		AdjusterSizeAndPosition adjuster = (AdjusterSizeAndPosition) selectedModule;
		
		Object source = e.getSource();
		if (source == spinnerPositionVertical || source == spinnerPositionHorizontal) {
			int  posiVerti = (int) spinnerPositionVertical.getValue();
			int  posiHori = (int) spinnerPositionHorizontal.getValue();
			adjuster.adjustPosition(posiHori, posiVerti);
		}else if (source == spinnerSizeRotation) {
			Object value = spinnerSizeRotation.getValue();
			adjuster.adjustRotation((int) value);
		}else {
			int width = (int) spinnerSizeWidth.getValue();
			int height = (int) spinnerSizeHeight.getValue();
			adjuster.adjustSize(width, height);
		}
		
	}

}
