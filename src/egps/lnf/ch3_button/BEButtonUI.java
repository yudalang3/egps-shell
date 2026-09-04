package egps.lnf.ch3_button;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.text.JTextComponent;

import egps.lnf.BeautyEyeLNFHelper;
import egps.lnf.utils.BEUtils;

/**
 * JButton的UI实现类.
 */
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 一些说明 Start
//本类的实现参考了JDK1.6_u18中WindowsButtonUI的源码.
//%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 一些说明 END
public class BEButtonUI extends BasicButtonUI {

	/** The Constant xWindowsButtonUI. */
	private final static BEButtonUI xWindowsButtonUI = new BEButtonUI();

	/** The nomal color. */
	private NormalColor nomalColor = NormalColor.normal;

	/**
	 * 按钮颜色方案枚举类型。.
	 */
	public enum NormalColor {

		/** 普通灰色按钮. */
		normal,

		/** 绿色按钮. */
		green,

		/** 红色按钮. */
		red,

		/** 浅蓝色按钮. */
		lightBlue,

		/** 深蓝色按钮. */
		blue
	}

	/**
	 * Sets the normal color.
	 *
	 * @param nc the nc
	 * @return the bE button ui
	 */
	public BEButtonUI setNormalColor(NormalColor nc) {
		this.nomalColor = nc;
		return this;
	}

	/** The dashed rect gap x. */
	protected int dashedRectGapX;

	/** The dashed rect gap y. */
	protected int dashedRectGapY;

	/** The dashed rect gap width. */
	protected int dashedRectGapWidth;

	/** The dashed rect gap height. */
	protected int dashedRectGapHeight;

	/** The focus color. */
	protected Color focusColor;

	/** The defaults_initialized. */
	private boolean defaults_initialized = false;

	/**
	 * Creates the ui.
	 *
	 * @param c the c
	 * @return the component ui
	 */
	public static ComponentUI createUI(JComponent c) {
		return xWindowsButtonUI;
	}

	// ********************************
	// Defaults
	// ********************************
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicButtonUI#installDefaults(javax.swing.
	 * AbstractButton)
	 */
	protected void installDefaults(AbstractButton b) {
		super.installDefaults(b);
		b.setOpaque(false);

		// yudalang 20240314添加： 默认为不可聚焦比较好看。
		// 还是不要设置吧避免出现问题。
//		b.setFocusable(false);
		// 还是去掉了，防止出现问题
		if (!defaults_initialized) {
			String pp = getPropertyPrefix();
			dashedRectGapX = UIManager.getInt(pp + "dashedRectGapX");
			dashedRectGapY = UIManager.getInt(pp + "dashedRectGapY");
			dashedRectGapWidth = UIManager.getInt(pp + "dashedRectGapWidth");
			dashedRectGapHeight = UIManager.getInt(pp + "dashedRectGapHeight");
			focusColor = UIManager.getColor(pp + "focus");
			defaults_initialized = true;
		}
		// 特别注意奥，这里不用设置，不起作用，在UI类安装的时候设置。
		// b.setBorder(BorderFactory.createEmptyBorder(5,7,5,7));
		LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicButtonUI#uninstallDefaults(javax.swing.
	 * AbstractButton)
	 */
	protected void uninstallDefaults(AbstractButton b) {
		super.uninstallDefaults(b);
		defaults_initialized = false;
	}

	/**
	 * Gets the focus color.
	 *
	 * @return the focus color
	 */
	protected Color getFocusColor() {
		return focusColor;
	}

	// ********************************
	// Paint Methods
	// ********************************
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.basic.BasicButtonUI#paintFocus(java.awt.Graphics,
	 * javax.swing.AbstractButton, java.awt.Rectangle, java.awt.Rectangle,
	 * java.awt.Rectangle)
	 */
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect,
			Rectangle iconRect) {
		// focus painted same color as text on Basic??
		int width = b.getWidth();
		int height = b.getHeight();
		g.setColor(getFocusColor());

		// ** modified by jb2011：绘制虚线方法改成可以设置虚线步进的方法，步进设为2则更好看一点
//		BasicGraphicsUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY,
//				width - dashedRectGapWidth, height - dashedRectGapHeight);
		// 绘制虚线框
		BEUtils.drawDashedRect(g, dashedRectGapX, dashedRectGapY, width - dashedRectGapWidth,
				height - dashedRectGapHeight);
		// 绘制虚线框的半透明白色立体阴影（半透明的用处在于若隐若现的效果比纯白要来的柔和的多）
		g.setColor(new Color(255, 255, 255, 50));
		// 立体阴影就是向右下偏移一个像素实现的
		BEUtils.drawDashedRect(g, dashedRectGapX + 1, dashedRectGapY + 1, width - dashedRectGapWidth,
				height - dashedRectGapHeight);
	}

	// ********************************
	// Layout Methods
	// ********************************
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);

		/*
		 * Ensure that the width and height of the button is odd, to allow for the focus
		 * line if focus is painted
		 */
		AbstractButton b = (AbstractButton) c;
		if (d != null && b.isFocusPainted()) {
			if (d.width % 2 == 0) {
				d.width += 1;
			}
			if (d.height % 2 == 0) {
				d.height += 1;
			}
		}
		return d;
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		paintXPButtonBackground(nomalColor, g, c);
		super.paint(g, c);
	}

	/**
	 * Paint xp button background.
	 *
	 * @param nomalColor the nomal color
	 * @param g          the g
	 * @param c          the c
	 */
	private void paintXPButtonBackground(NormalColor nomalColor, Graphics g, JComponent c) {
		AbstractButton b = (AbstractButton) c;
		boolean toolbar = (b.getParent() instanceof JToolBar);
		if (!b.isContentAreaFilled()) {
			return;
		}

		ButtonModel model = b.getModel();
//		System.out.println(b.getText());
//		System.out.printf("armed %b enabled %b pressed %b rollover %b selected %b\n",model.isArmed(),model.isEnabled(),model.isPressed(),model.isRollover(),model.isSelected());
		Dimension d = c.getSize();
		int dx = 0;
		int dy = 0;
		int dw = d.width;
		int dh = d.height;

		Border border = c.getBorder();
		Insets insets;
		if (border != null) {
			// Note: The border may be compound, containing an outer
			// opaque border (supplied by the application), plus an
			// inner transparent margin border. We want to size the
			// background to fill the transparent part, but stay
			// inside the opaque part.
			insets = BEButtonUI.getOpaqueInsets(border, c);
		} else {
			insets = c.getInsets();
		}
		if (insets != null) {
			dx += insets.left;
			dy += insets.top;
			dw -= (insets.left + insets.right);
			dh -= (insets.top + insets.bottom);
		}

		if (b instanceof JToggleButton) {
			System.err.println("This is impossible: tell dev." + BEButtonUI.class);
		}

		/***************************
		 * 
		 ********************/
		if (toolbar) {
			// yudalang 有过改装
			if (model.isRollover() || model.isPressed()) {
				__Icon9Factory__.getInstance().getButtonIcon_PressedOrange().draw((Graphics2D) g, dx, dy, dw, dh);
			} else if (model.isSelected()) {
				System.err.println("This is impossible: tell dev." + BEButtonUI.class);
			} else {

			}
		} else {
			if (model.isArmed() && model.isPressed() || model.isSelected()) {
				__Icon9Factory__.getInstance().getButtonIcon_PressedOrange().draw((Graphics2D) g, dx, dy, dw, dh);
			} else if (!model.isEnabled()) {
				// yudalang 改掉了，太丑了
				g.setColor(BeautyEyeLNFHelper.commonDisabledForegroundColor);
				g.drawRoundRect(dx + 4, dy + 4, dw - 8, dh - 8, 8, 8);
//						g.drawRoundRect(dx , dy , dw, dh ,8,8);
			} else if (model.isRollover()) {
					__Icon9Factory__.getInstance().getButtonIcon_rover().draw((Graphics2D) g, dx, dy, dw, dh);
//				g.setColor(Color.decode("#E2E2E2"));
//				g.fill3DRect(dx, dy, dw, dh, true);
				
			} else {
//				if (nomalColor == NormalColor.green) {
//					__Icon9Factory__.getInstance().getButtonIcon_NormalGreen().draw((Graphics2D) g, dx, dy, dw, dh);
//				} else if (nomalColor == NormalColor.red) {
//					__Icon9Factory__.getInstance().getButtonIcon_NormalRed().draw((Graphics2D) g, dx, dy, dw, dh);
//				} else if (nomalColor == NormalColor.blue) {
//					__Icon9Factory__.getInstance().getButtonIcon_NormalBlue().draw((Graphics2D) g, dx, dy, dw, dh);
//				} else if (nomalColor == NormalColor.lightBlue) {
//					__Icon9Factory__.getInstance().getButtonIcon_NormalLightBlue().draw((Graphics2D) g, dx, dy, dw, dh);
//				} else if (nomalColor == NormalColor.red) {
//					// 红色按钮禁用状态时为更好地突出禁用状态，用深灰按钮
//					if (model.isEnabled()) {
//						__Icon9Factory__.getInstance().getButtonIcon_NormalRed().draw((Graphics2D) g, dx, dy, dw, dh);
//					} else {
//						__Icon9Factory__.getInstance().getButtonIcon_NormalGray().draw((Graphics2D) g, dx, dy, dw, dh);
//					}
//				} else
				
				// 不需要这么花里胡哨的东西，正常就灰色就好了。
					__Icon9Factory__.getInstance().getButtonIcon_NormalGray().draw((Graphics2D) g, dx, dy, dw, dh);
			}
		}
		/*************************** 以下代码由JS改造自WindowsButtonUI END ********************/
	}

	/**
	 * returns - b.getBorderInsets(c) if border is opaque - null if border is
	 * completely non-opaque - somewhere inbetween if border is compound and outside
	 * border is opaque and inside isn't
	 *
	 * @param b the b
	 * @param c the c
	 * @return the opaque insets
	 */
	private static Insets getOpaqueInsets(Border b, Component c) {
		if (b == null) {
			return null;
		}
		if (b.isBorderOpaque()) {
			return b.getBorderInsets(c);
		} else if (b instanceof CompoundBorder) {
			CompoundBorder cb = (CompoundBorder) b;
			Insets iOut = getOpaqueInsets(cb.getOutsideBorder(), c);
			if (iOut != null && iOut.equals(cb.getOutsideBorder().getBorderInsets(c))) {
				// Outside border is opaque, keep looking
				Insets iIn = getOpaqueInsets(cb.getInsideBorder(), c);
				if (iIn == null) {
					// Inside is non-opaque, use outside insets
					return iOut;
				} else {
					// Found non-opaque somewhere in the inside (which is
					// also compound).
					return new Insets(iOut.top + iIn.top, iOut.left + iIn.left, iOut.bottom + iIn.bottom,
							iOut.right + iIn.right);
				}
			} else {
				// Outside is either all non-opaque or has non-opaque
				// border inside another compound border
				return iOut;
			}
		} else {
			return null;
		}
	}

	// copy from XPStyle.XPEmptyBorder 代码没有修改
	/**
	 * The Class XPEmptyBorder.
	 */
	@SuppressWarnings("serial")
	public static class XPEmptyBorder extends EmptyBorder implements UIResource {

		/**
		 * Instantiates a new xP empty border.
		 *
		 * @param m the m
		 */
		public XPEmptyBorder(Insets m) {
			super(m.top + 2, m.left + 2, m.bottom + 2, m.right + 2);
		}

		@Override
		public Insets getBorderInsets(Component c) {
			return getBorderInsets(c, getBorderInsets());
		}

		@Override
		public Insets getBorderInsets(Component c, Insets insets) {
			insets = super.getBorderInsets(c, insets);

			Insets margin = null;
			if (c instanceof AbstractButton) {
				Insets m = ((AbstractButton) c).getMargin();
				// if this is a toolbar button then ignore getMargin()
				// and subtract the padding added by the constructor
				if (c.getParent() instanceof JToolBar && !(c instanceof JRadioButton) && !(c instanceof JCheckBox)
						&& m instanceof InsetsUIResource) {
					insets.top -= 2;
					insets.left -= 2;
					insets.bottom -= 2;
					insets.right -= 2;
				} else {
					margin = m;
				}
			} else if (c instanceof JToolBar) {
				margin = ((JToolBar) c).getMargin();
			} else if (c instanceof JTextComponent) {
				margin = ((JTextComponent) c).getMargin();
			}
			if (margin != null) {
				insets.top = margin.top + 2;
				insets.left = margin.left + 2;
				insets.bottom = margin.bottom + 2;
				insets.right = margin.right + 2;
			}
			return insets;
		}
	}
}
