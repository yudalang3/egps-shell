package egps2.builtin.modules.voice;

import java.awt.Color;
import java.awt.Component;
import java.io.InputStream;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import egps2.utils.EGPSIconUtil;
import egps2.utils.common.util.EGPSShellIcons;

/**
 * 
 * Tooltip 会在这里显示！Flag也是！
 * 
 * @author mhl
 * 
 * @Date Created on: 2019-03-29 15:45
 * @ModifyDate 2020-09-16
 * 
 */
public class HistoryNodeTreeRenderer extends DefaultTreeCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2301443896179138179L;
	private final VersatileOpenInputClickAbstractGuiBase voiceImportHandler;

	private ImageIcon flagIcon = EGPSShellIcons.get("flag_16x16.png");
	private ImageIcon unflagIcon = EGPSShellIcons.get("unflag_16x16.png");
	final private ImageIcon categoryIcon;
	final private ImageIcon editingIcon;

	private Color defaultBackground = getBackground();

	private final Border compoundBorder = BorderFactory.createDashedBorder(Color.black, 1,4,4, true);

	public HistoryNodeTreeRenderer(VersatileOpenInputClickAbstractGuiBase voiceImportHandler) {
		this.voiceImportHandler = voiceImportHandler;
		setOpaque(false);

		InputStream tutorialIcon = getClass().getResourceAsStream("images/treasureChest.svg");
		categoryIcon = EGPSIconUtil.getIconFromSVGByStream(tutorialIcon, 20, 20);
		InputStream inputStream = getClass().getResourceAsStream("images/doEditing.svg");
		editingIcon = EGPSIconUtil.getIconFromSVGByStream(inputStream, 16, 16);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {

		Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

		// setBackgroundSelectionColor(new Color(205, 255, 205));
		setBackgroundSelectionColor(new Color(51, 153, 255));
		setTextSelectionColor(Color.WHITE);
		setTextNonSelectionColor(Color.BLACK);

		setBackgroundNonSelectionColor(defaultBackground);

		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) value;
		BookMarkNode bookMarkNode = (BookMarkNode) treeNode.getUserObject();

		TreePath path = tree.getPathForRow(row);

		if (path != null && row != -1) {
			if (bookMarkNode.isDesignAsLeaf()) {
				if (bookMarkNode.isFlag()) {
					setIcon(flagIcon);
				} else {
					setIcon(unflagIcon);
				}
			}
		}

		if (bookMarkNode.isCategoryDirectory()){
			setIcon(categoryIcon);
		}
		if (bookMarkNode == voiceImportHandler.getLinkedBookMarkNode()){
			/**
			 * 判断内存地址是否相同
			 * voiceImportHandler.getLinkedBookMarkNode() 如果为null也是返回 false，也符合要求
			 */
			setBorder(compoundBorder);
			setIcon(editingIcon);
		}else{
			setBorder(BorderFactory.createEmptyBorder());
		}

		return c;
	}

}
