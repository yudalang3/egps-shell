package egps.preferences.gui;

import java.awt.Component;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
* @author YFQ
* @date Nov 9, 2018 10:12:38 AM  
*/
public class PreferenceTree extends DefaultTreeCellRenderer {

	private ImageIcon preferenceIcon;
	
	public PreferenceTree() {
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	   
		URL resource = getClass().getResource("preference.png");
		preferenceIcon = new ImageIcon(resource);
		
	    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
	    
	    super.getTreeCellRendererComponent(tree, value, selected, expanded,
	      leaf, row, hasFocus);

//		setBackgroundNonSelectionColor(defaultBackground);
		setIcon(preferenceIcon);

		return this;
   }
}
