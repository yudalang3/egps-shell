package egps2.builtin.modules.voice;

import com.google.common.collect.Lists;
import egps2.EGPSProperties;
import egps2.UnifiedAccessPoint;
import egps2.frame.MyFrame;
import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import egps2.utils.common.model.filefilter.FileFilterTxt;
import egps2.utils.common.util.EGPSShellIcons;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.EGPSFileUtil;
import utils.storage.ObjectPersistence;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * BookmarkDisplayPanel belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class BookmarkDisplayPanel extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(BookmarkDisplayPanel.class);
    public static final String DEFAULT_CATEGORY = EGPSProperties.BOOKMARK_DEFAULT_CATEGORY;
    public static final String EXAMPLES = EGPSProperties.BOOKMARK_EXAMPLES_CATEGORY;
    private final JTree jTree;
    private final VersatileOpenInputClickAbstractGuiBase voiceImportHandler;
    private final Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

    private DefaultMutableTreeNode defaultCategoryNode;
    private DefaultMutableTreeNode treeRootNode;
    private DefaultMutableTreeNode exampleNode;
    private JPopupMenu popup4blinkArea;
    JPopupMenu jPopupMenu;

    private JPopupMenu popupMenu4Example;

    private boolean isUnderExamplesCategory(DefaultMutableTreeNode node) {
        TreeNode[] path = node.getPath();
        for (TreeNode n : path) {
            if (n instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode mutableNode = (DefaultMutableTreeNode) n;
                if (mutableNode.getUserObject() instanceof BookMarkNode) {
                    BookMarkNode bmNode = (BookMarkNode) mutableNode.getUserObject();
                    if (bmNode.getName().equals(EXAMPLES)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private JPopupMenu getPopupMenu4ExampleNode() {
        if (popupMenu4Example == null) {
            popupMenu4Example = new JPopupMenu();
            {
                JMenuItem duplicate = new JMenuItem("Duplicate");
                duplicate.setFont(defaultFont);
                duplicate.addActionListener(e -> {
                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
                    if (selected == treeRootNode) {
                        return;
                    }
                    BookMarkNode userObject = (BookMarkNode) selected.getUserObject();
                    voiceImportHandler.getEventUniformlyProcessor().displayPanelOperation.duplicate(userObject);
                    save();
                    jTree.updateUI();
                });
                popupMenu4Example.add(duplicate);
            }
            {
                JMenuItem export = new JMenuItem("Export");
                export.setFont(defaultFont);
                export.addActionListener(e -> {
                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
                    if (selected == treeRootNode) {
                        return;
                    }
                    BookMarkNode userObject = (BookMarkNode) selected.getUserObject();
                    exportSelectedNodeBookMark(userObject);
                });
                popupMenu4Example.add(export);
            }
        }
        return popupMenu4Example;
    }

    private JPopupMenu getPopupMenu4markNode() {
        DefaultMutableTreeNode selectedForCheck = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
        if (selectedForCheck != null && selectedForCheck.getUserObject() instanceof BookMarkNode) {
            BookMarkNode userObject = (BookMarkNode) selectedForCheck.getUserObject();
            if (userObject.isExample() || isUnderExamplesCategory(selectedForCheck)) {
                return getPopupMenu4ExampleNode();
            }
        }

        if (jPopupMenu == null) {
            jPopupMenu = new JPopupMenu();
            {
                JMenuItem flag = new JMenuItem("Flag");
                flag.setToolTipText("Un-flag bookmark if already flag.");
                flag.setFont(defaultFont);
                flag.addActionListener(e -> {

                    TreePath[] selectionPaths = jTree.getSelectionPaths();
                    if (selectionPaths == null) {
                        return;
                    }

                    for (TreePath treePath : selectionPaths) {
                        DefaultMutableTreeNode selected = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        if (selected.isLeaf()) {
                            BookMarkNode userObject = (BookMarkNode) selected.getUserObject();
                            userObject.setFlag(!userObject.isFlag());

                        }
                    }

                    jTree.updateUI();
                    save();
                });
                jPopupMenu.add(flag);
            }
            {
                JMenuItem flag = new JMenuItem("Duplicate");
                flag.setFont(defaultFont);
                flag.addActionListener(e -> {
                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
                    if (selected == treeRootNode) {
                        return;
                    }

                    BookMarkNode userObject = (BookMarkNode) selected.getUserObject();
                    voiceImportHandler.getEventUniformlyProcessor().displayPanelOperation.duplicate(userObject);
                    save();
                    jTree.updateUI();
                });
                jPopupMenu.add(flag);
            }
            {
                JMenuItem flag = new JMenuItem("Rename");
                flag.setFont(defaultFont);
                flag.addActionListener(e -> {
                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

                    if (selected == treeRootNode) {
                        return;
                    }

                    BookMarkNode userObject = (BookMarkNode) selected.getUserObject();

                    MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
                    Optional<String> showInputDialog = SwingDialog.showInputDialog(instanceFrame, userObject.getName());
                    if (showInputDialog.isPresent()) {
                        userObject.setName(showInputDialog.get());
                        jTree.updateUI();
                        save();
                    }

                });
                jPopupMenu.add(flag);
            }
            {
                JMenuItem delete = new JMenuItem("Delete");
                delete.setFont(defaultFont);
                delete.addActionListener(e -> {
                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

                    if (treeRootNode == selected) {
                        SwingDialog.showErrorMSGDialog("Remove error", "You can not remove the root node.");
                        return;
                    }

                    DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
                    TreePath[] selectionPaths = jTree.getSelectionPaths();
                    if (selectionPaths == null) {
                        return;
                    }
                    List<DefaultMutableTreeNode> toRemove = Lists.newArrayList();
                    boolean hasLinkedNode = false;
                    BookMarkNode linkedBookMarkNode = voiceImportHandler.getLinkedBookMarkNode();
                    for (TreePath treePath : selectionPaths) {
                        DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
                        BookMarkNode userObject = (BookMarkNode) treeNode.getUserObject();
                        if (linkedBookMarkNode != null && userObject == linkedBookMarkNode) {
                            hasLinkedNode = true;
                        }
                        toRemove.add(treeNode);

                    }

                    if (hasLinkedNode) {
                        // Get Optional Panel to ask user to confirm deletion
                        String[] options = {"Confirm", "Cancel"};
                        int result = JOptionPane.showOptionDialog(
                                UnifiedAccessPoint.getInstanceFrame(),
                                "The bookmarks to remove contain the linked node with input area.\nConfirm to delete?",
                                "Delete Confirmation",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                EGPSShellIcons.getHelpIcon(),
                                options,
                                options[0]
                        );
                        if (result == JOptionPane.YES_OPTION) {
                            for (DefaultMutableTreeNode treeNode : toRemove) {
                                model.removeNodeFromParent(treeNode);
                            }
                            voiceImportHandler.getEventUniformlyProcessor().inputDataAreaPanelActions.userClickTurnToExamples();
                            save();
                        }
                    } else {
                        for (DefaultMutableTreeNode treeNode : toRemove) {
                            model.removeNodeFromParent(treeNode);
                        }
                        save();
                        jTree.updateUI();
                    }

                });
                jPopupMenu.add(delete);
            }
            {
                JMenuItem flag = new JMenuItem("Export");
                flag.setFont(defaultFont);
                flag.addActionListener(e -> {
                    DefaultMutableTreeNode selected = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();

                    if (selected == treeRootNode) {
                        return;
                    }
                    BookMarkNode userObject = (BookMarkNode) selected.getUserObject();

                    exportSelectedNodeBookMark(userObject);

                });
                jPopupMenu.add(flag);
                jPopupMenu.addSeparator();
                jPopupMenu.add(getAddBookmarkDirMenuItem());
            }
        }
        return jPopupMenu;
    }

    private JPopupMenu getPopup4blinkArea() {
        if (popup4blinkArea == null) {
            popup4blinkArea = new JPopupMenu();

            JMenuItem jButtonImport = new JMenuItem("Import all records");
            jButtonImport.setToolTipText(UnifiedAccessPoint.getResourceString("voice.module.tip.import"));
            jButtonImport.addActionListener(e -> voiceImportHandler.getEventUniformlyProcessor().displayPanelOperation.importAllBookmark());
            popup4blinkArea.add(jButtonImport);

            JMenuItem jButtonExport = new JMenuItem("Export all records");
            jButtonExport.setToolTipText(UnifiedAccessPoint.getResourceString("voice.module.tip.export"));
            jButtonExport.addActionListener(e -> voiceImportHandler.bookmarkNodeOperation.exportAllBookmark());
            popup4blinkArea.add(jButtonExport);

            popup4blinkArea.addSeparator();
            popup4blinkArea.add(getAddBookmarkDirMenuItem());

            popup4blinkArea.addSeparator();
            {
                JMenuItem jButton = new JMenuItem("Delete all");
                jButton.setToolTipText("Delete all records");
                jButton.addActionListener(e -> voiceImportHandler.getEventUniformlyProcessor().displayPanelOperation.deleteAllRecords());
                popup4blinkArea.add(jButton);
            }
            
            popup4blinkArea.addSeparator();
            
            {
                JMenuItem resetToDefault = new JMenuItem("Reset to Factory Defaults");
                resetToDefault.setToolTipText("Clear all user bookmarks and restore original examples");
                resetToDefault.addActionListener(e -> {
                    int choice = JOptionPane.showConfirmDialog(
                        UnifiedAccessPoint.getInstanceFrame(),
                        "This will PERMANENTLY delete all your bookmarks and restore the original examples.\n\nThis action cannot be undone.\n\nAre you sure you want to reset?",
                        "Reset to Factory Defaults",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    if (choice == JOptionPane.YES_OPTION) {
                        voiceImportHandler.getEventUniformlyProcessor().displayPanelOperation.resetToFactoryDefaults();
                    }
                });
                popup4blinkArea.add(resetToDefault);
            }
        }

        return popup4blinkArea;
    }


    /**
     * DisplayMouseAdapter belongs to a built-in eGPS module (loader, panel, or helper).
     */
    private class DisplayMouseAdapter extends MouseAdapter {
        private void action4leftSingleClick() {
            DefaultMutableTreeNode selected = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
            if (selected == null || !selected.isLeaf()) {
                return;
            }
            final BookMarkNode userObject = (BookMarkNode) selected.getUserObject();
            voiceImportHandler.getEventUniformlyProcessor().action4singleLeftClick2anotherBookmark(userObject);

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            JComponent sourceComp = (JComponent) e.getSource();
            int selRow = jTree.getRowForLocation(x, y);
            boolean hasTreeNode = selRow > -1;
            // Double click
            if (e.getClickCount() == 2 && hasTreeNode) {
                voiceImportHandler.getEventUniformlyProcessor().displayPanelOperation.doubleClick2run();
                return;
            }
            // Single click
            if (SwingUtilities.isRightMouseButton(e)) {
                int[] selectionRows = jTree.getSelectionRows();
                if (selRow > -1) {
                    // if selRow in selectionRows, help me write code
                    boolean isInPreviousSelected = false;
                    if (selectionRows != null && selectionRows.length > 0) {
                        for (int selectionRow : selectionRows) {
                            if (selectionRow == selRow) {
                                isInPreviousSelected = true;
                                break;
                            }
                        }
                    }

                    if (isInPreviousSelected) {
                        // nothing to do
                    } else {
                        jTree.clearSelection();
                        jTree.setSelectionRow(selRow);
                    }

                    // 功能需求：
                    getPopupMenu4markNode().show(sourceComp, x + 15, y);
                } else {
                    getPopup4blinkArea().show(sourceComp, x, y);
                }

            } else {
                // left click
                if (hasTreeNode) {
                    if (e.isControlDown() || e.isShiftDown()) {
                        // This is for multi-selection
                    } else {
                        action4leftSingleClick();
                    }

                } else {
                     jTree.clearSelection();
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            JTree jTree = (JTree) e.getSource();
            TreePath tp = jTree.getPathForLocation(e.getX(), e.getY());
            Cursor predefinedCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
            if (tp != null) {
                DefaultMutableTreeNode lastPathComponent = (DefaultMutableTreeNode) tp.getLastPathComponent();
                if (lastPathComponent.isLeaf()) {
                    predefinedCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
                }
            }
            jTree.setCursor(predefinedCursor);
        }
    }


    public BookmarkDisplayPanel(VersatileOpenInputClickAbstractGuiBase voiceImportHandler) {
        this.voiceImportHandler = voiceImportHandler;
        setLayout(new BorderLayout());

        final boolean DEBUG = false;

        boolean shouldInit = false;

        if (DEBUG) {
            shouldInit = true;// For develop
        } else {
            boolean exists = Files.exists(Path.of(voiceImportHandler.internalFilePath2storageConfigFiles4voice()));
            if (exists) {
                treeRootNode = recoverJTreeFromStorage();
                if (treeRootNode == null || treeRootNode.getChildCount() == 0) {
                    shouldInit = true;
                }
            } else {
                shouldInit = true;
            }
        }

        if (shouldInit) {
            initRootNode();
        } else {
            int childCount = treeRootNode.getChildCount();
            for (int i = 0; i < childCount; i++) {
                TreeNode child = treeRootNode.getChildAt(i);
                if (child instanceof DefaultMutableTreeNode) {

                } else {
                    continue;
                }
                BookMarkNode userObject = (BookMarkNode) ((DefaultMutableTreeNode) child).getUserObject();
                if (userObject.getName().equals(DEFAULT_CATEGORY)) {
                    defaultCategoryNode = (DefaultMutableTreeNode) child;
                    //debug for the Bookmark node
//                    int childCount1 = defaultCategoryNode.getChildCount();
//                    for (int z = 0; z < childCount1; z++) {
//                        DefaultMutableTreeNode child1 = (DefaultMutableTreeNode) defaultCategoryNode.getChildAt(z);
//                        BookMarkNode userObject1 = (BookMarkNode) child1.getUserObject();
//                        if (Objects.equals("20kGenes", userObject1.getName())){
//                            System.out.println("");
//                        }
//                    }
                }
                if (userObject.getName().equals(EXAMPLES)) {
                    exampleNode = (DefaultMutableTreeNode) child;
                }
            }

            if (defaultCategoryNode == null || exampleNode == null) {
                log.info("DefaultCategory or Examples not found.");
                initRootNode();
            }
        }

        // create the tree by passing in the root node
        jTree = new JTree(treeRootNode);
        jTree.setFont(defaultFont);
        jTree.setCellRenderer(new HistoryNodeTreeRenderer(voiceImportHandler));
        jTree.setRootVisible(true);

        // 全部展开
        for (int i = 0; i < jTree.getRowCount(); i++) {
            jTree.expandRow(i);
        }

        // 获取树模型


        add(new JScrollPane(jTree), BorderLayout.CENTER);
        //先不加了，徒增bug
        //addDragActions();
        addMouseListener();
    }

    void makeLastTreeNodeToSelected() {
        DefaultMutableTreeNode lastLeaf = findLastLeafNode(treeRootNode);
        if (lastLeaf != null) {
            TreePath pathToLastLeaf = new TreePath(lastLeaf.getPath());
            jTree.setSelectionPath(pathToLastLeaf);
            jTree.scrollPathToVisible(pathToLastLeaf); // 确保可见
        }

    }

    private DefaultMutableTreeNode findLastLeafNode(DefaultMutableTreeNode node) {
        if (node.getChildCount() == 0) {
            return node; // 是叶子节点
        }

        DefaultMutableTreeNode lastChild = null;
        for (int i = 0; i < node.getChildCount(); i++) {
            lastChild = (DefaultMutableTreeNode) node.getChildAt(i);
        }

        // 递归进入最后一个子节点
        return findLastLeafNode(lastChild);
    }

    private void initRootNode() {
        BookMarkNode rootEntry = new BookMarkNode();
        rootEntry.setName("Bookmarks");
        treeRootNode = new DefaultMutableTreeNode(rootEntry);
        {
            BookMarkNode example = new BookMarkNode();
            example.setCategoryDirectory(true);
            example.setName(EXAMPLES);
            exampleNode = new DefaultMutableTreeNode(example);
            treeRootNode.add(exampleNode);
        }
        // configure the examples
        int numberOfExamples = voiceImportHandler.getNumberOfExamples();
        for (int i = 0; i < numberOfExamples; i++) {
            String name = "Example-" + (i + 1);
            addLeafNodeSelfExamples(name, voiceImportHandler.getExampleText());
        }


        {
            BookMarkNode defaultDir = new BookMarkNode();
            defaultDir.setName(DEFAULT_CATEGORY);
            defaultDir.setCategoryDirectory(true);
            defaultCategoryNode = new DefaultMutableTreeNode(defaultDir);
            treeRootNode.add(defaultCategoryNode);
        }
    }

    public JTree getjTree() {
        return jTree;
    }

    void exportSelectedNodeBookMark(BookMarkNode userObject) {
        Optional<String> content = userObject.getContent();
        if (content.isEmpty()) {
            return;
        }
        EGPSFileChooser egpsFileChooser = new EGPSFileChooser(getClass());
        FileFilterTxt filter = new FileFilterTxt();
        egpsFileChooser.setFileFilter(filter);
        String defaultOutputName = "runEGPS_".concat(userObject.getName());
        egpsFileChooser.setSelectedFile(new File(defaultOutputName));
        int showOpenDialog = egpsFileChooser.showSaveDialog();

        if (showOpenDialog != EGPSFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedFile = egpsFileChooser.getSelectedFile();
        if (egpsFileChooser.getFileFilter().getDescription().equals(filter.getDescription())) {
            String concat = selectedFile.getAbsolutePath().concat(".txt");
            selectedFile = new File(concat);
        }
        try {
            FileUtils.writeStringToFile(selectedFile, content.get(), StandardCharsets.UTF_8);
            String str1 = UnifiedAccessPoint.getResourceString("dialog.info");
            String msg = UnifiedAccessPoint.getResourceString("dialog.msg.export.finish");
            SwingDialog.showInfoMSGDialog(str1, msg);
        } catch (IOException e) {
            SwingDialog.showErrorMSGDialog("Export Error", "An error occurred while exporting the file.");
            log.error("An error occurred while exporting the file.", e);
        }
    }


    private JMenuItem getAddBookmarkDirMenuItem() {
        JMenuItem jMenuItem = new JMenuItem("Add directory");
        jMenuItem.setToolTipText("Add directory");

        jMenuItem.addActionListener(e -> {
            String currentTime = voiceImportHandler.dateTimeOperator.getCurrentTime();
            MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
            Optional<String> showInputDialog = SwingDialog.showInputDialog(instanceFrame, currentTime);
            if (showInputDialog.isPresent()) {
                String str = showInputDialog.get();
                addCategoryDirNode(str);
                jTree.updateUI();
                save();
            }
        });
        return jMenuItem;
    }

    private void addMouseListener() {
        DisplayMouseAdapter displayMouseAdapter = new DisplayMouseAdapter();
        jTree.addMouseListener(displayMouseAdapter);
        jTree.addMouseMotionListener(displayMouseAdapter);
    }

    public BookMarkNode addLeafNodeWithUserInputName(String name, String value) {
        BookMarkNode markNode = new BookMarkNode();
        markNode.setDesignAsLeaf(true);

        markNode.setName(name);
        markNode.setContent(value);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(markNode);

        DefaultMutableTreeNode lastChild = (DefaultMutableTreeNode) treeRootNode.getLastChild();
        lastChild.add(node);

        TreeNode[] path = lastChild.getPath();
        TreePath treePath = new TreePath(path);
        jTree.expandPath(treePath);

        TreePath path1 = new TreePath(node.getPath());
        //jTree.setSelectionPath(path1);
        jTree.scrollPathToVisible(path1);

        jTree.updateUI();

        return markNode;
    }

    private void addLeafNodeSelfExamples(String name, String value) {
        BookMarkNode markNode = new BookMarkNode();
        markNode.setDesignAsLeaf(true);
        markNode.setExample(true);
        markNode.setName(name);
        markNode.setContent(value);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(markNode);
        exampleNode.add(node);
    }

    /**
     * 这个内节点不需要内容，只需要一个名字即可
     * @param name the dir node name
     */
    public void addCategoryDirNode(String name) {
        BookMarkNode markNode = new BookMarkNode();
        markNode.setName(name);
        markNode.setCategoryDirectory(true);
        // 不需要设置内容 rootEntry.setContent(value);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(markNode);

        treeRootNode.add(node);
        jTree.updateUI();
    }

    public void clearAllRecords() {
        treeRootNode.removeAllChildren();
        treeRootNode.add(exampleNode);
        treeRootNode.add(defaultCategoryNode);
        defaultCategoryNode.removeAllChildren();
        jTree.updateUI();
    }

    /**
     * 去掉了 SwingWorker的封装 ，这样可以保证save在一些操作的前面
     */
    public synchronized void save() {
        try {
            ObjectPersistence.saveObjectByObjectOutputWithGZIP(treeRootNode,
                    voiceImportHandler.internalFilePath2storageConfigFiles4voice());
        } catch (Exception e2) {
            String resourceString = UnifiedAccessPoint.getResourceString("common.save.error.title");
            SwingDialog.showErrorMSGDialog(resourceString, e2.getMessage());
        }

    }

    private DefaultMutableTreeNode recoverJTreeFromStorage() {
        File file = new File(voiceImportHandler.internalFilePath2storageConfigFiles4voice());
        DefaultMutableTreeNode rootNode = ObjectPersistence.getObjectByObjectInputWithGZIP(file.getAbsolutePath());
        if (rootNode == null) {
            // 显示确认对话框
            int choice = JOptionPane.showConfirmDialog(this,
                    "It seems the stored data is broken, do you want to delete?", "Confirmation",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, EGPSShellIcons.get("eGPS_logo16x16.png"));

            // 根据用户选择执行操作
            if (choice == JOptionPane.YES_OPTION) {
                // 用户选择了 Yes，执行后续代码
                EGPSFileUtil.forceDelete(file);
                return null;
            } else if (choice == JOptionPane.NO_OPTION) {
                // 用户选择了 No
                return null;
            } else {
                // 用户关闭了对话框
                return null;
            }
        }

        return rootNode;
    }

    public void removeBookmarkNode(BookMarkNode targetUserObject) {
        Enumeration<TreeNode> enumeration = treeRootNode.depthFirstEnumeration();

        DefaultMutableTreeNode targetTreeNode = null;
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (node.isLeaf() &&
                    node.getUserObject() != null &&
                    node.getUserObject().equals(targetUserObject)) {
                targetTreeNode = node;
                break;
            }
        }

        if (targetTreeNode == null) {
            return;
        }
        DefaultTreeModel model = (DefaultTreeModel) jTree.getModel();
        model.removeNodeFromParent(targetTreeNode);
        jTree.updateUI();
    }

    /**
     * Select the given BookMarkNode in the tree and scroll to make it visible.
     * @param targetUserObject the BookMarkNode to select
     */
    public void selectBookmarkNode(BookMarkNode targetUserObject) {
        Enumeration<TreeNode> enumeration = treeRootNode.depthFirstEnumeration();

        DefaultMutableTreeNode targetTreeNode = null;
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumeration.nextElement();
            if (node.getUserObject() != null &&
                    node.getUserObject().equals(targetUserObject)) {
                targetTreeNode = node;
                break;
            }
        }

        if (targetTreeNode == null) {
            return;
        }

        TreePath treePath = new TreePath(targetTreeNode.getPath());
        jTree.setSelectionPath(treePath);
        jTree.scrollPathToVisible(treePath);
    }

    Optional<BookMarkNode> getFirstSelectedBookmarkNode() {
        TreePath[] paths = jTree.getSelectionPaths();
        if (paths != null && paths.length > 0) {
            Object node = paths[0].getLastPathComponent();
            if (node instanceof DefaultMutableTreeNode dmNode &&
                    dmNode.getUserObject() instanceof BookMarkNode bookmarkNode) {
                return Optional.of(bookmarkNode);
            }
        }
        return Optional.empty();
    }

    BookMarkNode getExampleBookmarkNode() {
        BookMarkNode userObject;
        int childCount = exampleNode.getChildCount();
        if (childCount == 0) {
            throw new IllegalArgumentException("no example, Please tell the developers, it is impossible.");
        } else if (childCount == 1) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) exampleNode.getFirstChild();
            userObject = (BookMarkNode) child.getUserObject();
            TreePath treePath = new TreePath(child.getPath());
            jTree.setSelectionPath(treePath);
            jTree.scrollPathToVisible(treePath);
        } else {
            TreePath selectionPath = jTree.getSelectionPath();
            int selectedPathIndex = -1;
            for (int i = 0; i < childCount; i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) exampleNode.getChildAt(i);
                TreePath treePath = new TreePath(child.getPath());
                if (Objects.equals(treePath, selectionPath)) {
                    selectedPathIndex = i;
                    break;
                }
            }
            TreePath treePath;
            if (selectedPathIndex == -1) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) exampleNode.getFirstChild();
                treePath = new TreePath(child.getPath());
                userObject = (BookMarkNode) child.getUserObject();
            } else {
                selectedPathIndex++;
                if (selectedPathIndex >= childCount) {
                    selectedPathIndex = 0;
                }
                DefaultMutableTreeNode child = (DefaultMutableTreeNode) exampleNode.getChildAt(selectedPathIndex);
                treePath = new TreePath(child.getPath());
                userObject = (BookMarkNode) child.getUserObject();
            }
            jTree.setSelectionPath(treePath);
            jTree.scrollPathToVisible(treePath);
        }
        return userObject;
    }

    /**
     * 在书签树中查找节点（混合策略）
     * 优先精确匹配（名称 + 内容哈希），如果找不到则降级为名称匹配
     *
     * @param name        书签名称
     * @param contentHash 书签内容哈希（可以为 null）
     * @return 找到的节点，如果不存在则返回 Optional.empty()
     */
    public Optional<DefaultMutableTreeNode> findBookmarkNode(String name, String contentHash) {
        if (name == null || name.isEmpty()) {
            return Optional.empty();
        }

        // 1. 收集所有同名节点
        List<DefaultMutableTreeNode> candidates = Lists.newArrayList();
        findAllByNameRecursive(treeRootNode, name, candidates);

        if (candidates.isEmpty()) {
            return Optional.empty(); // 没有同名节点
        }

        // 2. 如果有内容哈希，优先精确匹配
        if (contentHash != null && !contentHash.isEmpty()) {
            for (DefaultMutableTreeNode node : candidates) {
                BookMarkNode bmNode = (BookMarkNode) node.getUserObject();
                String actualHash = Integer.toHexString(
                    bmNode.getContent().orElse("").hashCode()
                );
                if (actualHash.equals(contentHash)) {
                    return Optional.of(node); // 精确匹配成功
                }
            }
        }

        // 3. 降级匹配：只匹配名称（取第一个）
        return Optional.of(candidates.get(0));
    }

    /**
     * 递归查找所有同名节点
     */
    private void findAllByNameRecursive(
        DefaultMutableTreeNode node,
        String name,
        List<DefaultMutableTreeNode> results
    ) {
        // 检查当前节点
        Object userObj = node.getUserObject();
        if (userObj instanceof BookMarkNode) {
            BookMarkNode bookMarkNode = (BookMarkNode) userObj;
            if (bookMarkNode.isDesignAsLeaf() && !bookMarkNode.isExample()) {
                if (name.equals(bookMarkNode.getName())) {
                    results.add(node);
                }
            }
        }

        // 递归检查子节点
        for (int i = 0; i < node.getChildCount(); i++) {
            TreeNode child = node.getChildAt(i);
            if (child instanceof DefaultMutableTreeNode) {
                findAllByNameRecursive((DefaultMutableTreeNode) child, name, results);
            }
        }
    }
}
