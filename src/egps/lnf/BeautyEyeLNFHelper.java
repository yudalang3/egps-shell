/*
 * Copyright (C) 2015 Jack Jiang(cngeeker.com) The BeautyEye Project. 
 * All rights reserved.
 * Project URL:https://github.com/JackJiang2011/beautyeye
 * Version 3.6
 * 
 * Jack Jiang PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 * BeautyEyeLNFHelper.java at 2015-2-1 20:25:40, original version by Jack Jiang.
 * You can contact author with jb2011@163.com.
 */
package egps.lnf;

import java.awt.Color;

/**
 * <p>
 * BeautyEye Swing外观实现方案 - L&F核心辅助类.<br>
 * <p>
 * 项目托管地址：https://github.com/JackJiang2011/beautyeye
 * 
 * @author Jack Jiang(jb2011@163.com), 2012-05
 * @version 1.0
 */
public class BeautyEyeLNFHelper
{
	/** 
	 * 颜色全局变量：正常情况下的窗口文本颜色.
	 * <p>
	 * 你可设置本变量，也可直接通过{@code UIManager.put("activeCaptionText",new ColorUIResource(c))}和
	 * {@code UIManager.put("inactiveCaptionText",new ColorUIResource(c))}来实现窗口文本颜色的改变.
	 * <p>
	 * 窗体不活动(inactivite)时的颜色将据此自动计算出来，无需额外设置.
	 * 默认是黑色（new Color(0,0,0)）. */
	public static Color activeCaptionTextColor = new Color(0,0,0);//黑色
	
	/** 
	 * 颜色全局变量：多数组件的背景色.
	 * <p>
	 * 你可设置本变量，也可直接通过各自的UIManager属性来改变它们.
	 * <p>
	 * 默认是浅灰色（new Color(250,250,250)）. 
	 * @since 3.2 */
	public static Color commonBackgroundColor = new Color(250,250,250);//240,240,240); //248,248,248);//255,255,255);//
	/** 
	 * 颜色全局变量：多数组件的前景色（文本颜色）.
	 * <p>
	 * 你可设置本变量，也可直接通过各自的UIManager属性来改变它们.
	 * <p>
	 * 默认是深灰色（new Color(60,60,60)）. 
	 * @since 3.2 */
	public static Color commonForegroundColor = new Color(60,60,60);//102,102,102);
	/** 
	 * 颜色全局变量：某些组件的焦点边框颜色.
	 * 当前主要用于按钮等焦点边框的绘制颜色.
	 * <p>
	 * 你可设置本变量，也可直接通过各自的UIManager属性来改变它们.
	 * <p>
	 * 默认是浅灰色（new Color(250,250,250)）. 
	 * @since 3.2 */
	public static Color commonFocusedBorderColor = new Color(162,162,162);
	/** 
	 * 颜色全局变量：某些组件被禁用时的文本颜色.
	 * 当前主要用于菜单项中.
	 * <p>
	 * 你可设置本变量，也可直接通过各自的UIManager属性来改变它们.
	 * <p>
	 * 默认是浅灰色（new Color(172,168,153)）. 
	 * @since 3.2 */
	public static Color commonDisabledForegroundColor = new Color(172,168,153);
	/** 
	 * 颜色全局变量：多数组件中文本被选中时的背景色.当前主要用于各文本组件等.
	 * <p>
	 * 你可设置本变量，也可直接通过各自的UIManager属性来改变它们.
	 * <p>
	 * 默认是深灰色（new Color(2,129,216)）. 
	 * @since 3.2 */
	public static Color commonSelectionBackgroundColor = new Color(2,129,216);//78,155,193));//58,135,173));//235,217,147));//new Color(255,237,167));
	/** 
	 * 颜色全局变量：多数组件中文本被选中时的前景色（文本颜色）.当前主要用于各文本组件、菜单项等.
	 * <p>
	 * 你可设置本变量，也可直接通过各自的UIManager属性来改变它们.
	 * <p>
	 * 默认是深灰色（new Color(255,255,255)）. 
	 * @since 3.2 */
	public static Color commonSelectionForegroundColor = new Color(255,255,255);
	
	/**
	 * 开关量：用于默认设置或不设置窗口（Frame及其子类）的设置此窗体的最大化边界.
	 * <p>
	 * 此开关量是它是为了解决这样一个问题 ：<br>
	 * 当不使用操作系统的窗口装饰（即使用完全自定义的窗口标题、边框）时，在windows上最
	 * 大化窗口时将会全屏显示从而覆盖了下方的任务栏（task bar），这个问题 据说自2002年
	 * 就已存在，SUN一直未解决或者根本不认为是bug。目前的解决方案是当本变量是true时则
	 * 默认为每一个窗体设置最大化边界，否则保持系统默认。不过这样设置并非完美方案：一旦
	 * 设置了最大边界，则此后无论Task Bar再怎么调 整大小，比如被hide了，则窗体永远是设
	 * 置时的最大边界，不过目前也只能这么折中解决了，因为窗体最大化事件处理并非L&F中实现
	 * ，暂未找到其它更好的方法。
	 * <p>
	 * 默认true，即表示默认开启此设置.
	 * 
	 * @since 3.2
	 * @see javax.swing.JFrame#setMaximizedBounds(java.awt.Rectangle)
	 */
	public static boolean setMaximizedBoundForFrame = true;
	
	/**
	 * BeautyEye LNF 的窗口边框样式.
	 */
	public enum FrameBorderStyle
	{
		
		/** 使用本地系统的窗口装饰样式（本样式将能带来最佳性能，使用操作系统默认窗口样式）. */
		osLookAndFeelDecorated,
		
		/** 使用类似于MacOSX的强烈立体感半透明阴影边框（本样式性能尚可，视觉效果最佳）. */
		translucencyAppleLike,
		
		/** 使用不太强烈立体感半透明阴影边框（本样式性能尚可，视觉效果较soft）. */
		translucencySmallShadow,
		/** 使用不透明的普通边框（这是本LNF在Java1.5版默认使用的样式，因为java1.5不支持窗口透明） */
		generalNoTranslucencyShadow
	}
	
	/**
	 * <b>开发者暂时无需关注此接口.</b>
	 * <p>
	 * 实现了此接口的UI类意味着用户可以通过自行设置诸如border等，来
	 * 取消默认的NainePatch图实现的边框填充、背景填充等，具体设置哪
	 * 些东西可以取消默认的NinePatch图填充的方式详见各自的类注释。
	 */
	public interface __UseParentPaintSurported
	{
		
		/**
		 * 是否使用父类的绘制实现方法，true表示是.
		 * <p>
		 * 因为在BE LNF中，进度条和背景都是使用N9图，比如没法通过设置JProgressBar的背景色和前景
		 * 色来控制进度条的颜色，本方法的目的就是当用户设置了进度条的Background或Foreground
		 * 时告之本实现类不使用BE LNF中默认的N9图填充绘制而改用父类中的方法（父类中的方法
		 * 就可以支持颜色的设置罗，只是丑点，但总归是能适应用户的需求场景要求，其实用户完全可以
		 * 通过JProgressBar.setUI(new MetalProgressBar())方式来自定义进度的UI哦）.
		 *
		 * @return true, if is use parent paint
		 */
		boolean isUseParentPaint();
	}
}
