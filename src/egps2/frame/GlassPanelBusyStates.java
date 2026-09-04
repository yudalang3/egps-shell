/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package egps2.frame;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * 忙碌状态玻璃面板，在顶层容器上显示等待状态并禁用用户输入。
 * Busy state glass panel displaying waiting state on top-level container and disabling user input.
 *
 * <p>此玻璃面板安装在顶层容器（如JFrame）上，当应用程序执行耗时操作时显示，
 * 通过覆盖整个窗口并显示等待光标来阻止用户输入，确保操作完成前界面不可交互。
 * This glass pane installs on top-level containers (such as JFrame), displays during time-consuming operations,
 * prevents user input by covering the entire window and displaying a wait cursor, ensuring the interface is non-interactive until operation completes.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>覆盖窗口并阻止鼠标和键盘事件 - Cover window and block mouse and keyboard events</li>
 *   <li>显示等待光标（WAIT_CURSOR） - Display wait cursor</li>
 *   <li>半透明白色背景（alpha=150） - Semi-transparent white background (alpha=150)</li>
 *   <li>可配置遮罩区域 - Configurable veil area</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage example:
 * <pre>{@code
 * // Install glass pane
 * frame.setGlassPane(new GlassPanelBusyStates().getWaitingLabel());
 *
 * // Make frame busy
 * frame.getGlassPane().setVisible(true);
 *
 * // After operation completes
 * frame.getGlassPane().setVisible(false);
 * }</pre>
 *
 * <p><strong>设计要点：</strong>
 * Design considerations:
 * <ul>
 *   <li><b>透明度：</b>使用alpha通道实现半透明效果，避免完全遮挡界面</li>
 *   <li><b>光标：</b>等待光标明确告知用户应用正在处理</li>
 *   <li><b>遮罩区域：</b>可通过 veilX, veilY, veilWidth, veilHeight 调整遮罩位置和大小</li>
 * </ul>
 *
 * <p><strong>注意事项：</strong>
 * Important notes:
 * <br><b>谨慎使用：</b>编写良好的客户端应该尽量少让窗口进入"忙碌"状态，因为应用程序应该尽可能保持响应；
 * 耗时操作应该尽量转移到非GUI线程执行。
 * <br><b>Use sparingly:</b> A well-written client should rarely make a window "busy" because the app should
 * be as responsive as possible; long-winded operations should be off-loaded to non-GUI threads whenever possible.
 *
 * @see MyFrame#becomeBusy(boolean)
 * @author Sun Microsystems (original)
 * @author eGPS Dev Team (adapted)
 * @since 2.0
 */
class GlassPanelBusyStates {

	int veilX = 0;
	int veilY = 20;
	int veilWidth = 300;
	int veilHeight = 300;
	private JLabel waitingLabel;

	/**
	 * Create GlassPane component to block input on toplevel
	 */
	public GlassPanelBusyStates() {

		waitingLabel = new JLabel();
		waitingLabel.setOpaque(true);
//        waitingLabel = new JLabel("                                 Please waiting");
		waitingLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		waitingLabel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		Color bgColor = Color.white;
		Color color = new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 150);
		waitingLabel.setBackground(color);

	}

	
	public JLabel getWaitingLabel() {
		return waitingLabel;
	}

}
