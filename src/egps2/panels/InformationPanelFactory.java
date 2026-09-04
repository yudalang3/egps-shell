package egps2.panels;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jdesktop.swingx.JXEditorPane;

import egps2.UnifiedAccessPoint;

/**
 * 
 * <h1>目的</h1>
 * <p>
 *  
 * 在 eGPS中直接加载html内容
 *  </p>
 *  
 * <h1>输入/输出</h1>
 * 
 * <p>
 * 输入 html 文件所在的位置， 输出一个JEditorPanel。
 * </p>
 * 
 * <h1>使用方法</h1>
 * 
 * <blockquote>
 * <pre>
* String path = "demo/pages/introductionModule.html";
*		JEditorPane jEditorPanel = null;
*		try {
*			InputStream resourceAsStream = getClass().getResourceAsStream(path);
*			String contentFromInputStreamAsString = EGPSFileUtil.getContentFromInputStreamAsString(resourceAsStream);
*			jEditorPanel = new InformationPanelFactory().getInformationPanel(contentFromInputStreamAsString);
*		} catch (IOException e) {
*			e.printStackTrace();
*		}
*
*		JScrollPane buttomScorllPanel = new JScrollPane(jEditorPanel);
*		buttomScorllPanel.setBorder(null);
*
* </pre>
* 
* 或者
* 
* <pre>
* String path = "demo/pages/introductionModule.html";
		JEditorPane jEditorPanel = null;
		try {
			URL resource = getClass().getResource(path);
			jEditorPanel = new InformationPanelFactory().getInformationPanelFromResource(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}

		JScrollPane buttomScorllPanel = new JScrollPane(jEditorPanel);
		buttomScorllPanel.setBorder(null);
* </pre>
 * </blockquote>
 * 
 * <h1>注意点</h1>
 * <ol>
 * <li>
 * 现在没有什么要注意的点
 * </li>
 * <li>
 * 不要忘记有这个类能用就行了
 * </li>
 * </ol>
 * 
 * @implSpec
 * 见 {@link #getInformationPanel(String)}方法即可，实际就是封装了一层方法
 * 
 * @author yudal
 *
 */
/**
 * InformationPanelFactory is a reusable Swing panel or dialog within eGPS.
 */
public class InformationPanelFactory {

	private JEditorPane editorPane;
	private JPopupMenu jPopupMenu;
	
	private Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDocumentFont();

	public JEditorPane getInformationPanelFromResource(URL resource) throws IOException {
		editorPane = new JEditorPane(resource);
//		editorPane = new JXEditorPane();
		editorPane.setContentType("text/html;charset=utf-8");
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		editorPane.setFont(defaultFont);

		// 这些出现在右键点击菜单栏里面好了
		editorPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu getjPopupMenu = getjPopupMenu();
					getjPopupMenu.show(editorPane, e.getX(), e.getY());
					getjPopupMenu.setVisible(true);
				}
			}
		});
		editorPane.setCaretPosition(0);

		return editorPane;
		
//		InputStream resourceAsStream = resource.openStream();
//		String contentFromInputStreamAsString = EGPSFileUtil.getContentFromInputStreamAsString(resourceAsStream);
//		return getInformationPanel(contentFromInputStreamAsString);
	}
	
	public JEditorPane getInformationPanel(String content) throws IOException {
		editorPane = new JEditorPane();
//		editorPane = new JXEditorPane();
		editorPane.setContentType("text/html;charset=utf-8");
		editorPane.setText(content);
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
		editorPane.setFont(defaultFont);

		// 这些出现在右键点击菜单栏里面好了
		editorPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu getjPopupMenu = getjPopupMenu();
					getjPopupMenu.show(editorPane, e.getX(), e.getY());
					getjPopupMenu.setVisible(true);
				}
			}
		});
		editorPane.setCaretPosition(0);

		return editorPane;
	}

	public JEditorPane getUnimplementedInformationPanel() {
		try {
			editorPane = new JXEditorPane(getClass().getResource("unimplement.html"));
			editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
			editorPane.setFont(defaultFont);
		} catch (IOException e1) {
			throw new InternalError(e1);
		}

		// 这些出现在右键点击菜单栏里面好了
		editorPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					JPopupMenu getjPopupMenu = getjPopupMenu();
					getjPopupMenu.show(editorPane, e.getX(), e.getY());
					getjPopupMenu.setVisible(true);
				}
			}
		});

		return editorPane;
	}

	private JPopupMenu getjPopupMenu() {
		if (jPopupMenu == null) {
			jPopupMenu = new JPopupMenu();
			
			Font defaultFont2 = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
			JMenuItem comp = new JMenuItem("Copy");
			comp.setFont(defaultFont2);
			comp.addActionListener(e -> {
				editorPane.copy();
			});
			jPopupMenu.add(comp);

			JMenuItem comp2 = new JMenuItem("Paste");
			comp2.setFont(defaultFont2);
			comp2.addActionListener(e -> {
				editorPane.paste();
			});
			jPopupMenu.add(comp2);

			jPopupMenu.addSeparator();

			JMenuItem comp3 = new JMenuItem("Recover to default");
			comp3.setFont(defaultFont2);
			comp3.addActionListener(e -> {

			});
			jPopupMenu.add(comp3);

			jPopupMenu.addSeparator();

			JMenuItem comp4 = new JMenuItem("Save as text");
			comp4.setFont(defaultFont2);
			comp4.addActionListener(e -> {

			});
			jPopupMenu.add(comp4);
		}
		return jPopupMenu;
	}

	

}
