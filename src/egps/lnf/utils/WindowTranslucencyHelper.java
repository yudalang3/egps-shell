/*
 * Copyright (C) 2015 Jack Jiang(cngeeker.com) The BeautyEye Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/beautyeye
 * Version 3.6
 * 
 * Jack Jiang PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * WindowTranslucencyHelper.java at 2015-2-1 20:25:40, original version by Jack Jiang.
 * You can contact author with jb2011@163.com.
 */
package egps.lnf.utils;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;

import egps.lnf.BeautyEyeLNFHelper;

//* 关于java支持窗口透明的详细信息请见：http://docs.oracle.com/javase/tutorial/uiswing/misc/trans_shaped_windows.html#uniform

//* 关于java1.6.0_10里的窗口透明存在一个BUG：
//* BUG出的错误：Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException: Width (0) and height (0) cannot be <= 0
//* 官方BUG ID ：6750920，地址：http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6750920
//* 该BUG被解决于:java1.6.0_12，realease note地址：http://www.oracle.com/technetwork/java/javase/6u12-137788.html
/**
 * The Class WindowTranslucencyHelper.
 */
public class WindowTranslucencyHelper {
	private final static String UN_WINDOWS_SORRY = "I'm sorry, the Linux platform does not support transparency"
			+ ", please pay attention to the next version of BeautyEye.";

	/**
	 * Checks if is translucency supported.
	 *
	 * @return true, if is translucency supported
	 * @see <code>GraphicsDevice.isWindowTranslucencySupported(TRANSLUCENT)</code>
	 *      at JDK1.7 or later
	 * @see <code>com.sun.awt.AWTUtilities.isTranslucencySupported(AWTUtilities.Translucency.TRANSLUCENT)</code>
	 *      at JDK1.6_u10 or later
	 * @author Jack Jiang at 2013-03-20 19:00
	 * @since 3.5
	 */
	public static boolean isTranslucencySupported() {
		boolean isTranslucencySupported = false;
		try {
			// * Implemention at JDK1.7 and after
			// Determine if the GraphicsDevice supports translucency.
			// GraphicsEnvironment ge =
			// GraphicsEnvironment.getLocalGraphicsEnvironment();
			// GraphicsDevice gd = ge.getDefaultScreenDevice();
			// return gd.isWindowTranslucencySupported(TRANSLUCENT)

			// * Implements at JDK1.7 and after in reflect
			// Determine if the GraphicsDevice supports translucency.
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gd = ge.getDefaultScreenDevice();
			//
			Class _WindowTranslucency = Class.forName("java.awt.GraphicsDevice$WindowTranslucency");
			isTranslucencySupported = ((Boolean) (ReflectHelper.invokeMethod(GraphicsDevice.class, gd,
					"isWindowTranslucencySupported", new Class[] { _WindowTranslucency },
					new Object[] { Enum.valueOf(_WindowTranslucency, "TRANSLUCENT") }))).booleanValue();
		} catch (Exception e) {
				e.printStackTrace();
			LogHelper.debug("Exception at WindowTranslucencyHelper.isTranslucencySupported()," + e.getMessage());
		}

		return isTranslucencySupported;
	}

	/**
	 * Sets the opacity.
	 *
	 * @param w       the w
	 * @param opacity the opacity
	 */
	public static void setOpacity(Window w, float opacity) {
//		//## Fix for: Issue BELNF-5, 目前因Jack Jiang手头没有Linux等测试环境，目前就暂时先让这
//		//## 些平台不支持窗口透明吧，起码先把BE LNF跑起来再说，此问题以后再彻底解决
//		if(!Platform.isWindows())
//		{
//			System.out.println(UN_WINDOWS_SORRY);
//			return;
//		}

		try {
			if (!isTranslucencySupported()) {
				LogHelper.debug("Your OS can't supported translucency.");
				return;
			}

			// 1.7.0及以后版本
			ReflectHelper.invokeMethod(Window.class, w, "setOpacity", new Class[] { float.class },
					new Object[] { opacity });
		} catch (Exception e) {
			System.err.println("您的JRE版本不支持每像素半透明(需jre1.6_u12及以上版本)，BeautyEye外观将不能达到最佳视觉效果哦." + e.getMessage());
		}
	}

	/**
	 * Sets the window opaque.
	 *
	 * @param w      the w
	 * @param opaque the opaque
	 */
	public static void setWindowOpaque(Window w, boolean opaque) {
//		//## Fix for: Issue BELNF-5, 目前因Jack Jiang手头没有Linux等测试环境，目前就暂时先让这
//		//## 些平台不支持窗口透明吧，起码先把BE LNF跑起来再说，此问题以后再彻底解决
//		if(!Platform.isWindows())
//		{
//			System.out.println(UN_WINDOWS_SORRY);
//			return;
//		}

		try {
//			com.sun.awt.AWTUtilities.setWindowOpaque(w, opaque);
			// 1.6.0_u12及以后版本
			if (!isTranslucencySupported()) {
				LogHelper.debug("Your OS can't supported translucency.");
				return;
			}

//					if(isWindowTranslucencySupported())
			Color bgc = w.getBackground();
			// * 2012-09-22 由Jack Jiang注释：在群友机器上（win7+java1.7.0.1）的生产系统下
			// * 下使用BeautyEye有时w.getBackground()返回值是null，但为什么返回是null，Jack 没
			// * 有测出来（Jack测试都是正常的），暂且认为是其系统代码有问题吧，在此容错一下
			if (bgc == null)
				bgc = Color.black;// 暂不知道用此黑色作为容错值合不合适
			Color newBgn = new Color(bgc.getRed(), bgc.getGreen(), bgc.getBlue(), opaque ? 255 : 0);
			w.setBackground(newBgn);
		} catch (Exception e) {
				e.printStackTrace();
			LogHelper.debug("您的JRE版本不支持窗口透明(需jre1.6_u12及以上版本)，BeautyEye外观将不能达到最佳视觉效果哦." + e.getMessage());
		}
	}

}
