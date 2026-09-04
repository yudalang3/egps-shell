/*
 * Copyright 2020 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package egps2.frame;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.formdev.flatlaf.ui.FlatDropShadowBorder;
import com.formdev.flatlaf.ui.FlatEmptyBorder;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;

import egps2.UnifiedAccessPoint;

/**
 * 提示气泡管理器，负责在界面中显示带箭头的气泡提示框。
 * Hint balloon manager responsible for displaying balloon hint boxes with arrows in the interface.
 *
 * <p>此管理器基于FlatLaf Look & Feel，提供美观的气泡提示功能，用于向用户展示使用建议、功能说明或提示信息。
 * 气泡提示会显示在指定组件的附近，带有指向该组件的箭头。
 * This manager is based on FlatLaf Look & Feel, providing beautiful balloon hint functionality for showing usage suggestions,
 * feature descriptions, or tip information to users. Balloon hints display near specified components with arrows pointing to them.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>在组件周围显示气泡提示 - Display balloon hints around components</li>
 *   <li>支持四个方向的箭头指向 - Support arrow pointing in four directions</li>
 *   <li>支持HTML 3.2格式的提示内容 - Support HTML 3.2 formatted hint content</li>
 *   <li>支持链式提示显示 - Support chained hint display</li>
 *   <li>自动处理玻璃面板显示 - Automatically handle glass pane display</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * // Create hint chain (from inner to outer)
 * Hint fontMenuHint = new Hint(
 *     "Use 'Font' menu to increase/decrease font size or try different fonts.",
 *     fontMenu,
 *     SwingConstants.BOTTOM,
 *     null
 * );
 *
 * Hint optionsMenuHint = new Hint(
 *     "Use 'Options' menu to try out various FlatLaf options.",
 *     optionsMenu,
 *     SwingConstants.BOTTOM,
 *     fontMenuHint  // Next hint to show after this one
 * );
 *
 * // Show first hint
 * HintManager.showHint(optionsMenuHint);
 * }</pre>
 *
 * <p><strong>设计要点：</strong>
 * Design considerations:
 * <ul>
 *   <li>提示显示在 JLayeredPane.POPUP_LAYER 层 - Hints display on JLayeredPane.POPUP_LAYER</li>
 *   <li>点击提示面板会消费鼠标事件 - Clicking hint panel consumes mouse events</li>
 *   <li>提供"知道了"和"已了解"两个操作按钮 - Provides "Got it" and "Already known" action buttons</li>
 *   <li>支持链式提示队列 - Supports chained hint queue</li>
 * </ul>
 *
 * <p><strong>视觉效果：</strong>
 * Visual effects:
 * <ul>
 *   <li>带阴影的圆角气泡边框 - Rounded balloon border with shadow</li>
 *   <li>半透明白色背景 - Semi-transparent white background</li>
 *   <li>蓝色边框和箭头 - Blue border and arrow</li>
 *   <li>可滚动的内容区域 - Scrollable content area</li>
 * </ul>
 *
 * @see HintManager.Hint
 * @see HintManager.BalloonBorder
 * @author Karl Tauber (original)
 * @author eGPS Dev Team (adapted)
 * @since 2.0
 */
public class HintManager {

	static void showHint(Hint hint) {
		HintPanel hintPanel = new HintPanel(hint);
		hintPanel.showHint();
	}

	// ---- class HintPanel ----------------------------------------------------

	/**
	 * Hint supports the main eGPS window, actions, or tab management.
	 */
	public static class Hint {
		private final String message;
		private final Component owner;
		private final int position;
		Hint nextHint;

		/**
		 * 添加Hint的构造函数
		 * 
		 * @param message  要显示的字符串，支持Html 3.2
		 * @param owner    在哪个组件上显示
		 * @param position Text显示的位置，也就是Text相对于owner的位置
		 */
		public Hint(String message, Component owner, int position) {
			Objects.requireNonNull(message);
			Objects.requireNonNull(owner);

			this.message = message;
			this.owner = owner;
			this.position = position;
		}
	}

	// ---- class HintPanel ----------------------------------------------------

	@SuppressWarnings("serial")
	/**
	 * HintPanel supports the main eGPS window, actions, or tab management.
	 */
	private static class HintPanel extends JPanel {
		final Dimension size = new Dimension(380, 170);
		private final Hint hint;

		private JPanel popup;

		private HintPanel(Hint hint) {
			this.hint = hint;

			initComponents();

			setOpaque(false);
			updateBalloonBorder();

			hintLabel.setText(hint.message);

			// grab all mouse events to avoid that components overlapped
			// by the hint panel receive them
			// 因为底下的面板就是 JFrame中的Content 面板，所以需要把这个鼠标点击按钮给消化掉。
			addMouseListener(new MouseAdapter() {});
		}

		@Override
		public void updateUI() {
			super.updateUI();

			if (hint != null)
				updateBalloonBorder();
		}

		private void updateBalloonBorder() {
			int direction;
			switch (hint.position) {
			case SwingConstants.LEFT:
				direction = SwingConstants.RIGHT;
				break;
			case SwingConstants.TOP:
				direction = SwingConstants.BOTTOM;
				break;
			case SwingConstants.RIGHT:
				direction = SwingConstants.LEFT;
				break;
			case SwingConstants.BOTTOM:
				direction = SwingConstants.TOP;
				break;
			default:
				throw new IllegalArgumentException();
			}

			setBorder(new BalloonBorder(direction, Color.blue));
		}

		void showHint() {
			JRootPane rootPane = SwingUtilities.getRootPane(hint.owner);
			if (rootPane == null)
				return;

			JLayeredPane layeredPane = rootPane.getLayeredPane();

			// create a popup panel that has a drop shadow
			popup = new JPanel(new BorderLayout()) {
				@Override
				public void updateUI() {
					super.updateUI();

					// use invokeLater because at this time the UI delegates
					// of child components are not yet updated
					EventQueue.invokeLater(() -> {
						validate();
						setSize(getPreferredSize());
					});
				}
			};
			popup.setOpaque(false);
			popup.add(this);

			// calculate x/y location for hint popup
			Point pt = SwingUtilities.convertPoint(hint.owner, 0, 0, layeredPane);

			int x = pt.x;
			int y = pt.y;
			int gap = UIScale.scale(6);

			
			setPreferredSize(size);

			switch (hint.position) {
			case SwingConstants.LEFT:
				x -= size.width + gap;
				break;

			case SwingConstants.TOP:
				y -= size.height + gap;
				break;

			case SwingConstants.RIGHT:
				x += hint.owner.getWidth() + gap;
				break;

			case SwingConstants.BOTTOM:
				y += hint.owner.getHeight() + gap;
				break;
			}

			// set hint popup size and show it
			popup.setBounds(x, y, size.width, size.height);

			layeredPane.add(popup, JLayeredPane.POPUP_LAYER);
		}

		void hideHint() {
			if (popup != null) {
				Container parent = popup.getParent();
				if (parent != null) {
					parent.remove(popup);
					parent.repaint(popup.getX(), popup.getY(), popup.getWidth(), popup.getHeight());
				}
			}

		}

		private void gotIt() {
			// hide hint
			hideHint();

			// remember that user closed the hint

			// show next hint (if any)
			if (hint.nextHint != null)
				HintManager.showHint(hint.nextHint);
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			hintLabel = new JLabel();
			hintLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
			gotItButton = new JButton();

			// ======== this ========
			setLayout(new BorderLayout());

			// ---- hintLabel ----
			// yudalang 20240227加入scrollPanel防止在不同屏幕电脑前显示不完全。
			JScrollPane jScrollPane = new JScrollPane(hintLabel);
			add(jScrollPane, BorderLayout.CENTER);

			// ---- gotItButton ----
			String resourceString = UnifiedAccessPoint.getResourceString("Application.hint.gotit.button");
			gotItButton.setText(resourceString);
			gotItButton.setFocusable(false);
			gotItButton.addActionListener(e -> gotIt());

			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				add(buttonPane, BorderLayout.SOUTH);

				{
					String str = UnifiedAccessPoint.getResourceString("Application.hint.alreadyknown.button");
					JButton jButton = new JButton(str);
					jButton.setFocusable(false);
					jButton.addActionListener(e -> {
						//popup.setVisible(false);
						hideHint();
					});
					buttonPane.add(jButton);
				}
				buttonPane.add(gotItButton);
			}

			// JFormDesigner - End of component initialization //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY //GEN-BEGIN:variables
		private JLabel hintLabel;
		private JButton gotItButton;
		// JFormDesigner - End of variables declaration //GEN-END:variables
	}

	// ---- class BalloonBorder ------------------------------------------------
	@SuppressWarnings("serial")
	/**
	 * BalloonBorder supports the main eGPS window, actions, or tab management.
	 */
	public static class BalloonBorder extends FlatEmptyBorder {
		private static int ARC = 8;
		private static int ARROW_XY = 16;
		private static int ARROW_SIZE = 8;
		private static int SHADOW_SIZE = 6;
		private static int SHADOW_TOP_SIZE = 3;
		private static int SHADOW_SIZE2 = SHADOW_SIZE + 2;

		private final int direction;
		private final Color borderColor;
		Color c1 = new Color(255, 255, 255, 15);

		private final Border shadowBorder;

		public BalloonBorder(int direction, Color borderColor) {
			super(1 + SHADOW_TOP_SIZE, 1 + SHADOW_SIZE, 1 + SHADOW_SIZE, 1 + SHADOW_SIZE);

			this.direction = direction;
			this.borderColor = borderColor;

			switch (direction) {
			case SwingConstants.LEFT:
				left += ARROW_SIZE;
				break;
			case SwingConstants.TOP:
				top += ARROW_SIZE;
				break;
			case SwingConstants.RIGHT:
				right += ARROW_SIZE;
				break;
			case SwingConstants.BOTTOM:
				bottom += ARROW_SIZE;
				break;
			}

			shadowBorder = new FlatDropShadowBorder(Color.gray,
					new Insets(SHADOW_SIZE2, SHADOW_SIZE2, SHADOW_SIZE2, SHADOW_SIZE2), 0.5f);
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D) g.create();
			try {
				FlatUIUtils.setRenderingHints(g2);
				g2.translate(x, y);

				// shadow coordinates
				int sx = 0;
				int sy = 0;
				int sw = width;
				int sh = height;
				int arrowSize = UIScale.scale(ARROW_SIZE);
				switch (direction) {
				case SwingConstants.LEFT:
					sx += arrowSize;
					sw -= arrowSize;
					break;
				case SwingConstants.TOP:
					sy += arrowSize;
					sh -= arrowSize;
					break;
				case SwingConstants.RIGHT:
					sw -= arrowSize;
					break;
				case SwingConstants.BOTTOM:
					sh -= arrowSize;
					break;
				}

				// paint shadow
				if (shadowBorder != null)
					shadowBorder.paintBorder(c, g2, sx, sy, sw, sh);

				// create balloon shape
				int bx = UIScale.scale(SHADOW_SIZE);
				int by = UIScale.scale(SHADOW_TOP_SIZE);
				int bw = width - UIScale.scale(SHADOW_SIZE + SHADOW_SIZE);
				int bh = height - UIScale.scale(SHADOW_TOP_SIZE + SHADOW_SIZE);
				g2.translate(bx, by);
				Shape shape = createBalloonShape(bw, bh);

				// fill balloon background
				//g2.setColor(c.getBackground());

				g2.setColor(c1);
				g2.fill(shape);

				// paint balloon border
				g2.setColor(borderColor);
				g2.setStroke(new BasicStroke(UIScale.scale(1f)));
				g2.draw(shape);
			} finally {
				g2.dispose();
			}
		}

		private Shape createBalloonShape(int width, int height) {
			int arc = UIScale.scale(ARC);
			int xy = UIScale.scale(ARROW_XY);
			int awh = UIScale.scale(ARROW_SIZE);

			Shape rect;
			Shape arrow;
			switch (direction) {
			case SwingConstants.LEFT:
				rect = new RoundRectangle2D.Float(awh, 0, width - 1 - awh, height - 1, arc, arc);
				arrow = FlatUIUtils.createPath(awh, xy, 0, xy + awh, awh, xy + awh + awh);
				break;

			case SwingConstants.TOP:
				rect = new RoundRectangle2D.Float(0, awh, width - 1, height - 1 - awh, arc, arc);
				arrow = FlatUIUtils.createPath(xy, awh, xy + awh, 0, xy + awh + awh, awh);
				break;

			case SwingConstants.RIGHT:
				rect = new RoundRectangle2D.Float(0, 0, width - 1 - awh, height - 1, arc, arc);
				int x = width - 1 - awh;
				arrow = FlatUIUtils.createPath(x, xy, x + awh, xy + awh, x, xy + awh + awh);
				break;

			case SwingConstants.BOTTOM:
				rect = new RoundRectangle2D.Float(0, 0, width - 1, height - 1 - awh, arc, arc);
				int y = height - 1 - awh;
				arrow = FlatUIUtils.createPath(xy, y, xy + awh, y + awh, xy + awh + awh, y);
				break;

			default:
				throw new RuntimeException();
			}

			Area area = new Area(rect);
			area.add(new Area(arrow));
			return area;
		}
	}

	public static void main(String[] args) {
		// 一个如何使用的例子

//		private void showHints() {
//			Hint fontMenuHint = new Hint(
//					"Use 'Font' menu to increase/decrease font size or try different fonts.", 
//					fontMenu,
//					SwingConstants.BOTTOM, "hint.fontMenu", null);
//
//			Hint optionsMenuHint = new Hint(
//					"Use 'Options' menu to try out various FlatLaf options.", 
//					optionsMenu,
//					SwingConstants.BOTTOM, "hint.optionsMenu", 
//					fontMenuHint);
//
//			Hint themesHint = new Hint(
//					"Use 'Themes' list to try out various themes.", 
//					themesPanel, 
//					SwingConstants.LEFT,
//					"hint.themesPanel", 
//					optionsMenuHint);
//
//			HintManager.showHint(themesHint);
//		}
//
//		private void clearHints() {
//			HintManager.hideAllHints();
//
//			Preferences state = DemoPrefs.getState();
//			state.remove("hint.fontMenu");
//			state.remove("hint.optionsMenu");
//			state.remove("hint.themesPanel");
//		}

	}
}
