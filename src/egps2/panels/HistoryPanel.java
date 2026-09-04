package egps2.panels;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTree;

/**
 * HistoryPanel is a reusable Swing panel or dialog within eGPS.
 */
public class HistoryPanel extends JXTree {

	public HistoryPanel() {
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Create the nodes.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		createNodes(root);
		
		DefaultTreeModel defaultTreeModel = new DefaultTreeModel(root, false);
		
		setModel(defaultTreeModel);
		
		expandAll();
	}

	private void createNodes(DefaultMutableTreeNode top) {

		{
			DefaultMutableTreeNode book = null;

			DefaultMutableTreeNode category = new DefaultMutableTreeNode("2021-02-21-20:09:04");
			top.add(category);

			book = new DefaultMutableTreeNode("EHeatmap");
			category.add(book);
			book = new DefaultMutableTreeNode("Venn plot");
			category.add(book);
			book = new DefaultMutableTreeNode("Sanky plot");
			category.add(book);

		}
		{
			DefaultMutableTreeNode book = null;

			DefaultMutableTreeNode category = new DefaultMutableTreeNode("2021-02-22-10:09:04");
			top.add(category);

			book = new DefaultMutableTreeNode("Genetic diversity");
			category.add(book);
			book = new DefaultMutableTreeNode("Phylogenetic tree");
			category.add(book);

		}
		{

			DefaultMutableTreeNode book = null;
			DefaultMutableTreeNode category = new DefaultMutableTreeNode("2019-02-22-15:50:04");
			top.add(category);
			
			book = new DefaultMutableTreeNode("Phylogenetic tree");
			category.add(book);

		}

	}
}
