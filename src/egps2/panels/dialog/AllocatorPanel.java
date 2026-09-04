package egps2.panels.dialog;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import egps2.UnifiedAccessPoint;

/**
 * AllocatorPanel is a reusable Swing panel or dialog within eGPS.
 */
public class AllocatorPanel extends JPanel {


	final int rectangleWidth = 20;
	final int rectangleHeight = 30;
	private Color[] colors = { Color.blue, Color.white, Color.red };
	final private int rectangleY = 50;
	final private int initializeX = 20;
	final private EGPSColorChooser egpsColorChooser;

	private JButton[] jButtons;
	private int selectedJButtonIndex = 0;
	private int realWidth;
	
	DecimalFormat df = new DecimalFormat("##.##");

	public AllocatorPanel(EGPSColorChooser egpsColorChooser) {
		this.egpsColorChooser = egpsColorChooser;
		setLayout(null);
		addFixedButtons();
	}
	public AllocatorPanel(EGPSColorChooser egpsColorChooser,Color[] colors) {
		this.colors = colors;
		this.egpsColorChooser = egpsColorChooser;
		setLayout(null);
		addFixedButtons();
	}

	private void addFixedButtons() {
		
		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		
		JButton removeColorButton = new JButton();
		removeColorButton.setEnabled(colors.length <= 2 ? false : true);
		removeColorButton.addActionListener((e) ->{
			removeColor();
			
			if (selectedJButtonIndex > colors.length - 1) {
				selectedJButtonIndex = colors.length - 1;
			}
			if (colors.length <= 2) {
				removeColorButton.setEnabled(false);
			}
			
			egpsColorChooser.updateAll();
			egpsColorChooser.repaint();
			
		});
//		button.setSize(rectangleWidth, rectangleHeigt);
		removeColorButton.setBounds(initializeX + 80, rectangleY + 50, 80, 30);
		removeColorButton.setText("Remove");
		removeColorButton.setFont(defaultFont);
		add(removeColorButton);
		
		JButton addColorbutton = new JButton();
		addColorbutton.addActionListener((e) ->{
			addColor();
			removeColorButton.setEnabled(true);
			egpsColorChooser.updateAll();
			egpsColorChooser.repaint();
		});

//		button.setSize(rectangleWidth, rectangleHeigt);
		addColorbutton.setBounds(initializeX, rectangleY + 50, 60, 30);
		addColorbutton.setText("Add");
		addColorbutton.setFont(defaultFont);
		add(addColorbutton);
		
	}

	private void removeColor() {
		int len = colors.length -1;
		Color[] ret = new Color[len];
		System.arraycopy(colors, 0, ret, 0, len);
		colors = ret;
	}

	private void addColor() {
		int len = colors.length ;
		Color[] ret = new Color[len + 1];
		System.arraycopy(colors, 0, ret, 0, len);
		ret[len] = Color.white;
		colors = ret;
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		realWidth = getWidth();
//		g2d.setColor(new Color(191, 216, 239));
		g2d.setColor(Color.lightGray);
		double width = realWidth - 200.0;
		double interval = width / (colors.length - 1);
		Rectangle2D.Double dd = new Rectangle2D.Double(initializeX, rectangleY + 10, width, 10);
		g2d.fill(dd);
		g2d.setColor(Color.black);
		g2d.drawString("Location: ",(float) (initializeX + width + 35),rectangleY + 20);
		
		
		double tt = 100.0 * selectedJButtonIndex / colors.length;
		
		g2d.drawString(df.format(tt)+" %",(float) (initializeX + width + 95),rectangleY + 20);

		reSetLocationsIntel(interval);
	}

	public void reSetLocationsIntel(double interval) {

		if (jButtons != null) {
			for (Component comp : jButtons) {
				remove(comp);
			}
			
		}
		final int length = colors.length;

		jButtons = new JButton[length];
		for (int i = 0; i < length; i++) {
			final int index = i;
			JButton button = new JButton() {
				@Override
				protected void paintComponent(Graphics g) {
					g.setColor(colors[index]);
					g.fillRect(0, 0, rectangleWidth, rectangleHeight);
					//super.paintComponent(g);
				}
			};
			button.setOpaque(true);
			button.setBackground(colors[index]);
//			button.setSize(rectangleWidth, rectangleHeigt);
			int xx = (int) (initializeX + i * interval);
			button.setBounds(xx, rectangleY, rectangleWidth, rectangleHeight);
			
			button.addActionListener((e) -> {
				
				for (int j = 0; j < length; j++) {
					JButton bb = jButtons[j];
					if (j == index) {
						bb.setBorder(new LineBorder(Color.blue, 2, true));
					}else {
						bb.setBorder(null);
					}
				}
				selectedJButtonIndex = index;
				egpsColorChooser.repaint();
			});
			jButtons[i] = button;
			add(button);
		}
		jButtons[selectedJButtonIndex].setBorder(new LineBorder(Color.blue, 2, true));
		
	}
	
	
	
	public void setSelectedButtonColor(Color color) {
		jButtons[selectedJButtonIndex].setBackground(color);
		colors[selectedJButtonIndex] = color;
	}
	
	public Color[] getColors() {
		return colors;
	}
	public float[] getDistances() {
		int length = colors.length;
		float[] ret = new float[length];
		float interval = 1.0f / (length - 1);
		for (int i = 0; i < length; i++) {
			ret[i] = i * interval;
		}
		
		return ret;
	}
	


}
