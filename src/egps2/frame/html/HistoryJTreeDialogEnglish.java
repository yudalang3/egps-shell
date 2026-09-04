package egps2.frame.html;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;

import egps2.UnifiedAccessPoint;
import egps2.panels.InformationPanelFactory;

/**
 * History dialog (English version) for displaying development history.
 */
@SuppressWarnings("serial")
public class HistoryJTreeDialogEnglish extends JPanel implements TreeSelectionListener {
	private JEditorPane htmlPane;
	private JXTree tree;
	private URL helpURL = getClass().getResource("History_English.html");
	private static boolean DEBUG = false;

	private static boolean playWithLineStyle = true;
	private static String lineStyle = "Horizontal";

	public HistoryJTreeDialogEnglish() {
		super(new GridLayout(1, 0));
		setBorder(null);

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode("Development History");
		createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JXTree(top);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setFont(defaultFont);

		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		if (playWithLineStyle) {
			tree.putClientProperty("JTree.lineStyle", lineStyle);
		}

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		// Create the HTML viewing pane - Load English version
		URL url = helpURL;
		try {
			htmlPane = new InformationPanelFactory().getInformationPanelFromResource(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		htmlPane.setEditable(false);
		JScrollPane htmlView = new JScrollPane(htmlPane);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(htmlView);
		splitPane.setBorder(null);

		Dimension minimumSize = new Dimension(100, 50);
		htmlView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(200);
		splitPane.setPreferredSize(new Dimension(650, 500));

		// Add the split pane to this panel.
		add(splitPane);

		tree.expandAll();
	}

	/** Required by TreeSelectionListener interface. */
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof BookInfo) {
			BookInfo book = (BookInfo) nodeInfo;
			displayURL(book.bookURL);
		} else {
			displayURL(helpURL);
		}
	}

	private class BookInfo {
		public String bookName;
		public URL bookURL;

		public BookInfo(String book, String filename) {
			bookName = book;
			bookURL = getClass().getResource(filename);
			if (bookURL == null) {
				System.err.println("Couldn't find file: " + filename);
			}
		}

		public String toString() {
			return bookName;
		}
	}

	private void displayURL(URL url) {
		try {
			if (url != null) {
				htmlPane.setPage(url);
			} else { // null url
				htmlPane.setText("File Not Found");
				if (DEBUG) {
					System.out.println("Attempted to display a null URL.");
				}
			}
		} catch (IOException e) {
			System.err.println("Attempted to read a bad URL: " + url);
		}
	}

	private void createNodes(DefaultMutableTreeNode top) {
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode book = null;

		category = new DefaultMutableTreeNode(new BookInfo("Major Features", "majorFeatures.html"));
		top.add(category);

		{
			book = new DefaultMutableTreeNode(new BookInfo("Voice Module", "feature_voicm.html"));
			category.add(book);
			book = new DefaultMutableTreeNode(new BookInfo("Features 1.0 to 2.0", "feature_details_from1to2.html"));
			category.add(book);
		}

		category = new DefaultMutableTreeNode(new BookInfo("History Details", "history.html"));
		top.add(category);

		{
			book = new DefaultMutableTreeNode(new BookInfo("Version 2.0.0", "historyDetails2.0.0.html"));
			category.add(book);
			book = new DefaultMutableTreeNode(new BookInfo("Version 2.0.1", "historyDetails2.0.1.html"));
			category.add(book);
			book = new DefaultMutableTreeNode(new BookInfo("Version 2.0.2", "historyDetails2.0.2.html"));
			category.add(book);
			book = new DefaultMutableTreeNode(new BookInfo("Version 2.0.3", "historyDetails2.0.3.html"));
			category.add(book);
			book = new DefaultMutableTreeNode(new BookInfo("Version 2.0.4", "historyDetails2.0.4.html"));
			category.add(book);
			book = new DefaultMutableTreeNode(new BookInfo("Version 2.0.5", "historyDetails2.0.5.html"));
			category.add(book);
			book = new DefaultMutableTreeNode(new BookInfo("Version 2.1.1", "historyDetails2.1.0.html"));
			category.add(book);
		}
	}

}
