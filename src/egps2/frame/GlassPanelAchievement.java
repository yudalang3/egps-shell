package egps2.frame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;
import egps2.utils.GifDecoder;

/**
 * 成就动画玻璃面板，在主窗口玻璃层显示庆祝动画和成就提示。
 * Achievement animation glass panel displaying celebration animation and achievement hints on main window glass layer.
 *
 * <p>此组件在用户达成特定成就时（如启动次数里程碑）显示在主窗口的玻璃面板层，
 * 通过烟花动画、星星移动和文本提示营造庆祝氛围。
 * This component displays on the main window's glass pane layer when users achieve specific milestones (such as launch count milestones),
 * creating celebration atmosphere through fireworks animation, moving stars, and text hints.
 *
 * <p><strong>视觉元素：</strong>
 * Visual elements:
 * <ul>
 *   <li><b>烟花动画：</b>循环播放的GIF烟花特效，自动切换帧（约100ms间隔）</li>
 *   <li><b>移动星星：</b>3颗不同尺寸的星星图标（24/22/20像素），从左向右水平滚动</li>
 *   <li><b>装饰边框：</b>固定的边框图片装饰</li>
 *   <li><b>文本内容：</b>包含标题和内容两部分
 *     <ul>
 *       <li>title: 标题文本，默认"Congratulations !" </li>
 *       <li>content: 成就内容，如"You have launched 9982 times."</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <p><strong>动画机制：</strong>
 * Animation mechanism:
 * <ul>
 *   <li>使用 {@link Timer} 每100ms触发一次重绘</li>
 *   <li>自动切换烟花帧（循环播放GIF）</li>
 *   <li>星星X坐标每帧增加10像素，到达200后重置为50</li>
 *   <li>2.5秒后自动停止并移除（防止过度打扰用户）</li>
 * </ul>
 *
 * <p><strong>交互行为：</strong>
 * Interaction behavior:
 * <ul>
 *   <li>可拖动：用户可以拖动整个面板到窗口内任意位置</li>
 *   <li>点击关闭：点击面板任意位置立即停止动画并移除</li>
 *   <li>自动关闭：2.5秒后自动停止</li>
 * </ul>
 *
 * <p><strong>生命周期：</strong>
 * Lifecycle:
 * <ol>
 *   <li>创建：构造时解码GIF动画帧，加载星星和边框图片</li>
 *   <li>显示：添加到主窗口的玻璃面板</li>
 *   <li>动画：Timer驱动动画播放</li>
 *   <li>清理：调用 {@link #stop()} 停止Timer，从玻璃面板移除</li>
 * </ol>
 *
 * <p><strong>字体：</strong>
 * Fonts:
 * <ul>
 *   <li>titleFont: 标题字体，20pt</li>
 *   <li>contentFont: 内容字体，16pt</li>
 * </ul>
 *
 * @see MyFrame
 * @see egps2.utils.GifDecoder
 * @author eGPS Dev Team
 * @since 2.0
 */
class GlassPanelAchievement extends JComponent implements ActionListener {
	private final Font titleFont;
	private final Font contentFont;
	private String content = "You have lauched 9982 times.";
	private String title = "Title";
	private final Image border;
	private final Image[] fireWorksImages;
	private final Image[] starImages;
	private int imageIndex = 0;
	private int StartXX = 50;
	private Timer timer;
	private MyFrame myFrame;
	
	int timeOfMs = 0;

	public GlassPanelAchievement(MyFrame myFrame) {
		this.myFrame = myFrame;
		ImageIcon imageIcon = new ImageIcon(UnifiedAccessPoint.getImageResource("miscellaneous/border.png"));
		border = imageIcon.getImage();

		InputStream imageResource = UnifiedAccessPoint.getImageResourceAsStream("miscellaneous/fireworks.gif");

		Image star1 = new ImageIcon(UnifiedAccessPoint.getImageResource("miscellaneous/Star24.png")).getImage();
		Image star2 = new ImageIcon(UnifiedAccessPoint.getImageResource("miscellaneous/Star22.png")).getImage();
		Image star3 = new ImageIcon(UnifiedAccessPoint.getImageResource("miscellaneous/Star20.png")).getImage();

		starImages = new Image[3];
		starImages[2] = star1;
		starImages[1] = star2;
		starImages[0] = star3;

		GifDecoder d = new GifDecoder();
		d.read(imageResource);

		int n = d.getFrameCount();
		fireWorksImages = new Image[n];
		for (int i = 0; i < n; i++) {
			BufferedImage frame = d.getFrame(i);
			fireWorksImages[i] = frame;
		}

		MouseAdapter ad = new MouseAdapter() {

			private int prevX = 0, prevY = 0;

			@Override
			public void mouseClicked(MouseEvent e) {
				stop();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				MouseEvent convertMouseEvent = SwingUtilities.convertMouseEvent(GlassPanelAchievement.this, e,
						myFrame.getGlassPane());
				Point point = convertMouseEvent.getPoint();
				prevX = point.x;
				prevY = point.y;
			}

			@Override
			public void mouseDragged(MouseEvent e) {

				MouseEvent convertMouseEvent = SwingUtilities.convertMouseEvent(GlassPanelAchievement.this, e,
						myFrame.getGlassPane());
				Point point = convertMouseEvent.getPoint();

				int moveX = point.x - prevX;
				int moveY = point.y - prevY;

				Point location = getLocation();
				location.x = location.x + moveX;
				location.y = location.y + moveY;

				setLocation(location);
				prevX = point.x;
				prevY = point.y;

				repaint();
			}
		};
		addMouseListener(ad);
		addMouseMotionListener(ad);

		timer = new Timer(100, this);
		timer.start();

		LaunchProperty lauchProperty = UnifiedAccessPoint.getLaunchProperty();
		titleFont = lauchProperty.getDefaultTitleFont().deriveFont(20f);
		contentFont = titleFont.deriveFont(16f);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

//		Image createImage = this.createImage(getWidth(), getHeight());
//		Graphics graphics = createImage.getGraphics();

		Graphics2D graphics = (Graphics2D) g;

		graphics.setColor(Color.blue);
		graphics.setFont(titleFont);
		graphics.drawString(title, 80, 160);
		graphics.setFont(contentFont);
		graphics.drawString(content, 80, 200);

		graphics.drawImage(border, 10, 10, this);
		graphics.drawImage(fireWorksImages[imageIndex], 0, 0, this);

		for (int i = 0; i < 3; i++) {
			graphics.drawImage(starImages[i], StartXX + i * 30, getHeight() - 60, this);
		}

//		g.drawImage(createImage , 0, 0, this);
	}

	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		imageIndex++;

		if (imageIndex == fireWorksImages.length) {
			imageIndex = 0;
		}

		StartXX += 10;

		if (StartXX > 200) {
			StartXX = 50;
		}

		try {
			Thread.sleep(100);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		repaint();

		timeOfMs += 100;

		if (timeOfMs > 2500) {
			stop();
		}

	}

	public void stop() {
		timer.stop();
		myFrame.removeCompoentInGlassPanel(this);
	}

}
