package egps2.panels;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.swing.JComponent;
import javax.swing.Timer;

import egps2.utils.GifDecoder;

/**
 * DynGifLabel is a reusable Swing panel or dialog within eGPS.
 */
public class DynGifLabel extends JComponent implements ActionListener {
	private static final long serialVersionUID = 45345345355L;

	// 用以刷新paint函数
	Timer refreshThread;

	private Image[] fireWorksImages;

	private int imageIndex = 0;
	
	private Runnable doWhenClick;

	/**
	 * 
	 * @param image: Sample:new ImageIcon(DynGifLabel.class
	 *               .getResource("/picture.gif")).getImage()
	 */
	public DynGifLabel(InputStream imageResource) {
		refreshThread = new Timer(100, this);
		refreshThread.start();

		GifDecoder d = new GifDecoder();
		d.read(imageResource);

		int n = d.getFrameCount();
		fireWorksImages = new Image[n];
		for (int i = 0; i < n; i++) {
			BufferedImage frame = d.getFrame(i);
			fireWorksImages[i] = frame;
		}
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (doWhenClick != null) {
					doWhenClick.run();
				}
			}
		});
	}

	@Override
	protected void paintComponent(Graphics g) {

		g.drawImage(fireWorksImages[imageIndex], 0, 0, this);

		imageIndex++;

		if (imageIndex == fireWorksImages.length) {
			imageIndex = 0;
		}

		super.paintComponent(g);
	}

	/**
	 * 隔100毫秒刷新一次
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		this.repaint();// 这里调用了Paint
	}

	public void stop() {
		refreshThread.stop();
	}
	
	
	public void setDoWhenClick(Runnable doWhenClick) {
		this.doWhenClick = doWhenClick;
	}
}
