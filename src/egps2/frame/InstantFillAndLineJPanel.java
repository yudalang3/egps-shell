package egps2.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

import graphic.engine.guibean.ColorIcon;
import egps2.panels.dialog.EGPSFontChooser;
import egps2.UnifiedAccessPoint;
import egps2.modulei.AdjusterFillAndLine;
import net.miginfocom.swing.MigLayout;

/**
 * 即时填充和线条样式调整面板，提供颜色、字体和线条粗细的交互式控件。
 * Instant fill and line style adjustment panel providing interactive controls for color, font, and line thickness.
 *
 * <p>此面板为实现了 {@link egps2.modulei.AdjusterFillAndLine} 接口的模块提供图形样式的实时调整功能。
 * 用户可以即时修改选中对象的填充颜色、字体、线条颜色和粗细。
 * This panel provides real-time graphic style adjustment functionality for modules implementing {@link egps2.modulei.AdjusterFillAndLine} interface.
 * Users can instantly modify fill color, font, line color, and thickness of selected objects.
 *
 * <p><strong>功能控件（两部分）：</strong>
 * Function controls (two sections):
 * <ul>
 *   <li><b>填充 (Fill)：</b>
 *     <ul>
 *       <li>Color（颜色）: 颜色选择器按钮，点击弹出系统颜色对话框</li>
 *       <li>Font（字体）: 字体选择按钮，使用 {@link egps2.panels.dialog.EGPSFontChooser} 对话框</li>
 *     </ul>
 *   </li>
 *   <li><b>线条 (Line)：</b>
 *     <ul>
 *       <li>Color（颜色）: 颜色选择器按钮</li>
 *       <li>Thickness（粗细）: 微调器，范围1-15像素</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><strong>颜色图标显示：</strong>
 * Color icon display:
 * <br>颜色按钮使用 {@link graphic.engine.guibean.ColorIcon} 显示当前选中的颜色，
 * 提供直观的可视反馈。点击按钮弹出 {@link JColorChooser} 进行颜色选择。
 * Color buttons use {@link graphic.engine.guibean.ColorIcon} to display currently selected color,
 * providing intuitive visual feedback. Clicking buttons opens {@link JColorChooser} for color selection.
 *
 * <p><strong>动态启用机制：</strong>
 * Dynamic enabling mechanism:
 * <br>通过 {@link #refreshStates()} 方法根据当前选中模块的能力动态启用/禁用并初始化控件：
 * <ul>
 *   <li>仅当模块实现 {@link egps2.modulei.AdjusterFillAndLine} 时控件才可用</li>
 *   <li>各控件根据模块的 {@code couldSetFillColor()}, {@code couldSetFont()},
 *       {@code couldSetLineColor()}, {@code couldSetLineThickness()} 返回值独立启用</li>
 *   <li>控件启用时自动显示模块当前的样式值</li>
 * </ul>
 *
 * <p><strong>即时调整：</strong>
 * Instant adjustment:
 * <br>所有控件值变化立即触发模块的相应调整方法：
 * <ul>
 *   <li>填充颜色 → {@link egps2.modulei.AdjusterFillAndLine#adjustFillColor(Color)}</li>
 *   <li>字体 → {@link egps2.modulei.AdjusterFillAndLine#adjustFillFont(Font)}</li>
 *   <li>线条颜色 → {@link egps2.modulei.AdjusterFillAndLine#adjustLineColor(Color)}</li>
 *   <li>线条粗细 → {@link egps2.modulei.AdjusterFillAndLine#adjustLineThickness(int)}</li>
 * </ul>
 *
 * <p><strong>布局：</strong>
 * Layout:
 * <br>使用 MigLayout 实现清晰的标签-按钮两列布局，中间带分隔符。
 * Uses MigLayout for clear label-button two-column layout with separator.
 *
 * @see egps2.modulei.AdjusterFillAndLine
 * @see egps2.panels.dialog.EGPSFontChooser
 * @see graphic.engine.guibean.ColorIcon
 * @author eGPS Dev Team
 * @since 2.0
 */
@SuppressWarnings("serial")
public class InstantFillAndLineJPanel extends JPanel {
	private JButton btnLineColor;
	private JButton btnFillFont;
	private JButton btnFillColor;
	private MyFrame instanceFrame;
	private ChangeListener spinnerLineThickNessListener;
	private ColorIcon btnLineColorIcon;
	private ColorIcon btnFillColorIcon;
    JSpinner spinnerLineThickness;
	
	public InstantFillAndLineJPanel() {

		setBorder(new EmptyBorder(6, 6, 6, 6));
		setLayout(new MigLayout("", "[grow][]", "[][][][][][][][]"));

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();

		JLabel lblSizeTitle = new JLabel("Fill :");
		lblSizeTitle.setFont(defaultTitleFont);
		add(lblSizeTitle, "cell 0 0 2 1");

		JLabel lblFillColor = new JLabel("Color");
		lblFillColor.setFont(defaultFont);
		add(lblFillColor, "cell 0 1");

		btnFillColorIcon = new ColorIcon(Color.lightGray);
		btnFillColor = new JButton(btnFillColorIcon);
		btnFillColor.setFocusable(false);
		add(btnFillColor, "cell 1 1,grow");
		btnFillColor.addActionListener(e -> {
			Color iniColor = btnFillColorIcon.getColor();
			MyFrame parent = UnifiedAccessPoint.getInstanceFrame();
			Color showDialog = JColorChooser.showDialog(parent, "Choose new color", iniColor);
			if (showDialog != null) {
				MyFrame mainFrame = getMainFrame();
				ModuleFace selectedModule = mainFrame.getSelectedModule();
				AdjusterFillAndLine liner = (AdjusterFillAndLine) selectedModule;
				btnFillColorIcon.setColor(showDialog);
				liner.adjustFillColor(showDialog);
			}

		});

		JLabel lblFillFont = new JLabel("Font");
		lblFillFont.setFont(defaultFont);
		add(lblFillFont, "cell 0 2");

		btnFillFont = new JButton("Set");
		btnFillFont.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EGPSFontChooser fontChooser = new EGPSFontChooser(btnFillFont.getFont());
				MyFrame mainFrame = getMainFrame();
				int result = fontChooser.showDialog(mainFrame);
				if (result == EGPSFontChooser.OK_OPTION) {
					Font font = fontChooser.getSelectedFont();
					btnFillFont.setFont(font);
					ModuleFace selectedModule = mainFrame.getSelectedModule();
					AdjusterFillAndLine liner = (AdjusterFillAndLine) selectedModule;
					liner.adjustFillFont(font);
				}
			}
		});
//		btnFillFont.setBorder(BorderFactory.createEmptyBorder(4,0,4,0));
		btnFillFont.setFocusable(false);
		btnFillFont.setFont(defaultTitleFont);
		add(btnFillFont, "cell 1 2,grow");

		JSeparator separator = new JSeparator();
		add(separator, "cell 0 4 2 1,growx");

		JLabel lblPositionTitle = new JLabel("Line :");
		lblPositionTitle.setFont(defaultTitleFont);
		add(lblPositionTitle, "cell 0 5 2 1");

		JLabel lblLineColor = new JLabel("Color");
		lblLineColor.setFont(defaultFont);
		add(lblLineColor, "cell 0 6");

		btnLineColorIcon = new ColorIcon(Color.lightGray);
		btnLineColor = new JButton(btnLineColorIcon);
//		btnLineColor.setBorder(BorderFactory.createEmptyBorder(4,0,4,0));
		btnLineColor.addActionListener(e -> {
			
			Color iniColor = btnLineColorIcon.getColor();
			MyFrame parent = UnifiedAccessPoint.getInstanceFrame();
			Color showDialog = JColorChooser.showDialog(parent, "Choose new color", iniColor);
			if (showDialog != null) {
				MyFrame mainFrame = getMainFrame();
				ModuleFace selectedModule = mainFrame.getSelectedModule();
				AdjusterFillAndLine liner = (AdjusterFillAndLine) selectedModule;
				btnLineColorIcon.setColor(showDialog);
				liner.adjustLineColor(showDialog);
			}

		});
		btnLineColor.setFocusable(false);
		btnLineColor.setFont(defaultFont);
		add(btnLineColor, "cell 1 6,grow");

		JLabel lblLineThickness = new JLabel("Thickness");
		lblLineThickness.setFont(defaultFont);
		add(lblLineThickness, "cell 0 7");

		spinnerLineThickness = new JSpinner();
		spinnerLineThickNessListener = e -> {
			int value = (int) spinnerLineThickness.getValue();
			MyFrame mainFrame = getMainFrame();
			ModuleFace selectedModule = mainFrame.getSelectedModule();
			AdjusterFillAndLine liner = (AdjusterFillAndLine) selectedModule;
			liner.adjustLineThickness(value);
		};
		spinnerLineThickness.addChangeListener(spinnerLineThickNessListener);

		spinnerLineThickness.setModel(new SpinnerNumberModel(1, 1, 15, 1));
		spinnerLineThickness.setFont(defaultFont);
		add(spinnerLineThickness, "cell 1 7,grow");
	}

	public void refreshStates() {
		MyFrame mainFrame = getMainFrame();
		ModuleFace selectedModule = mainFrame.getSelectedModule();

		if (selectedModule instanceof AdjusterFillAndLine) {
			setEnableStates((AdjusterFillAndLine) selectedModule);
		} else {
			spinnerLineThickness.setEnabled(false);
			btnLineColor.setEnabled(false);
			btnFillFont.setEnabled(false);
			btnFillColor.setEnabled(false);
		}
	}

	private void setEnableStates(AdjusterFillAndLine selectedModule) {

		Optional<Integer> couldSetLineThickness = selectedModule.couldSetLineThickness();
		spinnerLineThickness.setEnabled(couldSetLineThickness.isPresent());
		if (couldSetLineThickness.isPresent()) {
			spinnerLineThickness.removeChangeListener(spinnerLineThickNessListener);
			spinnerLineThickness.setValue(couldSetLineThickness.get());
			spinnerLineThickness.addChangeListener(spinnerLineThickNessListener);
		}
		
		Optional<Color> couldSetLineColor = selectedModule.couldSetLineColor();
		btnLineColor.setEnabled(couldSetLineColor.isPresent());
		if (couldSetLineColor.isPresent()) {
			btnLineColorIcon.setColor(couldSetLineColor.get());
			btnLineColor.repaint();
		}
		
		Optional<Font> couldSetFont = selectedModule.couldSetFont();
		btnFillFont.setEnabled(couldSetFont.isPresent());
		if (couldSetFont.isPresent()) {
			btnFillFont.setFont(couldSetFont.get());
		}
		
		
		Optional<Color> couldSetFillColor = selectedModule.couldSetFillColor();
		btnFillColor.setEnabled(couldSetFillColor.isPresent());
		if (couldSetFillColor.isPresent()) {
			btnFillColorIcon.setColor(couldSetFillColor.get());
			btnFillColor.repaint();;
		}

	}

	private MyFrame getMainFrame() {
		if (instanceFrame == null) {
			instanceFrame = UnifiedAccessPoint.getInstanceFrame();
		}
		return instanceFrame;
	}
}
