package egps2.builtin.modules.voice;

import egps2.UnifiedAccessPoint;
import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import egps2.utils.common.util.EGPSShellIcons;
import utils.EGPSFileUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * EventUniformlyProcessor belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class EventUniformlyProcessor {
    private final VersatileOpenInputClickAbstractGuiBase voiceHandler;
    private InputAreaPanel inputAreaPanel;
    private BookmarkOperationPanel bookmarkOperationPanel;
    private BookmarkDisplayPanel bookmarkDisplayPanel;

    private boolean isUserEditedInputArea = false;

    BookmarkDisplayPanelActions displayPanelOperation = new BookmarkDisplayPanelActions();
    BookmarkOperationPanelActions operationPanelOActions = new BookmarkOperationPanelActions();
    InputDataAreaPanelActions inputDataAreaPanelActions = new InputDataAreaPanelActions();

    public EventUniformlyProcessor(VersatileOpenInputClickAbstractGuiBase voiceHandler) {
        this.voiceHandler = voiceHandler;
        inputAreaPanel = voiceHandler.dataImportPanel;
        bookmarkOperationPanel = voiceHandler.bookmarkOperationPanel;
        bookmarkDisplayPanel = voiceHandler.bookmarkDisplayPanel;
    }

    /**
     * 第一次用VOICE GUI的时候调用这个方法
     */
    public void produceInitialBookmark() {
        // 每次打开新的 VOICE GUI，都需要清理上一次 Dialog 中残留的 linkedBookMarkNode 引用。
        // 否则该引用指向旧树节点，会导致后续逻辑（尤其是自动定位）走到“已有 linked 节点”的分支。
        voiceHandler.clearLinkedBookmarkNode();

        // 【新增】尝试复用上一次的书签（混合策略）
        String lastBookmarkName = voiceHandler.getLastUsedBookmarkName();
        String lastBookmarkHash = voiceHandler.getLastUsedBookmarkContentHash();

        if (lastBookmarkName != null && !lastBookmarkName.isEmpty()) {
            // 在新树中查找匹配的书签节点（混合策略：优先精确匹配，降级到名称匹配）
            Optional<DefaultMutableTreeNode> foundNode =
                bookmarkDisplayPanel.findBookmarkNode(lastBookmarkName, lastBookmarkHash);

            if (foundNode.isPresent()) {
                // 找到了，选中该节点
                DefaultMutableTreeNode targetNode = foundNode.get();
                TreePath path = new TreePath(targetNode.getPath());
                bookmarkDisplayPanel.getjTree().setSelectionPath(path);
                bookmarkDisplayPanel.getjTree().scrollPathToVisible(path);

                // 关键：仅设置 Selection 不会触发“左键单击”逻辑，因此输入区不会被加载。
                // 这里直接复用单击逻辑：加载内容 + 绑定 linkedBookMarkNode + 更新 Name 字段等状态。
                Object userObject = targetNode.getUserObject();
                if (userObject instanceof BookMarkNode bookMarkNode) {
                    action4singleLeftClick2anotherBookmark(bookMarkNode);
                    // 进入后，以实际命中的节点为准刷新“上一次使用”的标识
                    if (!bookMarkNode.isExample()) {
                        String contentHash = Integer.toHexString(bookMarkNode.getContent().orElse("").hashCode());
                        voiceHandler.setLastUsedBookmarkName(bookMarkNode.getName());
                        voiceHandler.setLastUsedBookmarkContentHash(contentHash);
                    }
                }

                return; // 复用成功，直接返回
            } else {
                // 没找到（可能被删除了），清除记忆
                voiceHandler.setLastUsedBookmarkName(null);
                voiceHandler.setLastUsedBookmarkContentHash(null);
            }
        }

        // 【原有逻辑】第一次打开或复用失败，走原有初始化流程
        try {
            bookmarkOperationPanel.action4toggleButtonSelected();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 当用户点击了执行按钮的时候，会调用这个方法：现在貌似有两个过程会调用这个方法：
     * 1. 点击了执行按钮
     * 2. 在 BookmarkDisplayPanel双击的时候
     * @throws IOException
     */
    public void actionWhenUserExecute() throws IOException {
        // 当 userSelectedBookMarkNode != null 时，会自动让 auto save的按钮关闭
        boolean shouldAutoSaveBookmark = bookmarkOperationPanel.shouldAutoSaveBookmark();
        String userInputText4bookmark = bookmarkOperationPanel.getUserInputText4bookmark();
        if (userInputText4bookmark.isBlank()) {
            userInputText4bookmark = voiceHandler.dateTimeOperator.getCurrentTime();
        }

        String inputContent = inputAreaPanel.getInputContent();
        if (shouldAutoSaveBookmark) {
            // 这里再新增一个判定，如果用户在输入框中输入了一个bookmark的标签，那么就用用户输入的标签。
            // 什么时候会 autoSave 同时又没有一个linkedBookMarkNode？
            // 使用局部变量快照，确保在检查和使用之间的原子性
            BookMarkNode linkedBookMarkNode = voiceHandler.getLinkedBookMarkNode();
            if (linkedBookMarkNode == null){
                throw new IllegalStateException("voiceHandler.getLinkedBookMarkNode() 存在的时候 shouldAutoSaveBookmark 一定为 true");
            }
            // 如果是linked，那么要更新内容
            // 因为 linkedBookMarkNode 已经是 AtomicReference，这里的 setContent 是安全的
            linkedBookMarkNode.setContent(inputContent);

            // 保存书签标识（名称 + 内容哈希，用于下次复用）
            if (!linkedBookMarkNode.isExample()) {
                String name = linkedBookMarkNode.getName();
                String content = linkedBookMarkNode.getContent().orElse("");
                String contentHash = Integer.toHexString(content.hashCode());

                voiceHandler.setLastUsedBookmarkName(name);
                voiceHandler.setLastUsedBookmarkContentHash(contentHash);
            }
        }
        // 保存输入对话框树结构的状态，保存要在这个地方，linkedBookMarkNode要更新内容
        bookmarkDisplayPanel.save();
        bookmarkOperationPanel.updateJTextFieldContentIfPossible(userInputText4bookmark);
        if (!shouldAutoSaveBookmark) {
            return;
        }

        clearUserEditedFlag();

        EditScriptState inputAreaState = bookmarkOperationPanel.getInputAreaState();
        EditScriptState retState = inputAreaState;
        switch (inputAreaState) {
            case COPY_ON_ENTRY, COPY_OF_EXISTED, USER_MODIFIED:
                retState = EditScriptState.IN_PREVIOUS_RECORD;
                break;
            default:
                //IN_PREVIOUS_RECORD
                // do nothing
                break;
        }
        bookmarkOperationPanel.setInputAreaState(retState);
    }
    public void createNovelLinkedBookmarkByInputAreaText(String name, String content) throws IOException {
        BookMarkNode markNode = bookmarkDisplayPanel.addLeafNodeWithUserInputName(name, content);
        voiceHandler.assignLinkedBookmarkNode(markNode);
        bookmarkDisplayPanel.getjTree().updateUI();

        // Update Current label for the new bookmark
        voiceHandler.setCurrentBookmarkName(name, false, false);
    }
    public void action4singleLeftClick2anotherBookmark(BookMarkNode userObject) {
        BookMarkNode linkedBookMarkNode = voiceHandler.getLinkedBookMarkNode();
        if (linkedBookMarkNode == null) {
            action4turn4ExistBookmark(userObject);
        } else {
            if (Objects.equals(userObject, linkedBookMarkNode)) {
                return;
            }
            Optional<String> content = userObject.getContent();
            if (content.isEmpty()){
                throw new IllegalStateException("No content, this should not happen, please tell developers");
            }

            boolean isExampleCat = userObject.isExample();
            EditScriptState inputAreaState = bookmarkOperationPanel.getInputAreaState();
            switch (inputAreaState) {
                case IN_PREVIOUS_RECORD:
                    // 直接走掉就行，因为此时用户没有改动过输入脚本，而且在原先的节点处
                    voiceHandler.clearLinkedBookmarkNode();
                    if (!isExampleCat) {
                        voiceHandler.assignLinkedBookmarkNode(userObject);
                        // 更新 Name 字段显示书签名称
                        bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
                    } else {
                        // For Example, show the Example's original name (not CopyOf)
                        bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
                    }
                    inputAreaPanel.updateJTextAreaDirectImport(content.get());
                    // Update Current label
                    voiceHandler.setCurrentBookmarkName(userObject.getName(), isExampleCat, false);
                    break;
                case USER_MODIFIED:
                    // 这时比较复杂
                    actionCurrNodeIsModifiedWhenTurn4ExistBookmark(userObject,linkedBookMarkNode);
                    break;
                case COPY_ON_ENTRY:
                    // COPY_ON_ENTRY: 临时节点，用户切换时删除（没有编辑过）
                    voiceHandler.clearLinkedBookmarkNode();
                    bookmarkDisplayPanel.removeBookmarkNode(linkedBookMarkNode);
                    bookmarkOperationPanel.setInputAreaState(EditScriptState.IN_PREVIOUS_RECORD);
                    if (!isExampleCat) {
                        voiceHandler.assignLinkedBookmarkNode(userObject);
                        bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
                    } else {
                        bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
                    }
                    inputAreaPanel.updateJTextAreaDirectImport(content.get());
                    voiceHandler.setCurrentBookmarkName(userObject.getName(), isExampleCat, false);
                    break;
                case COPY_OF_EXISTED:
                    // COPY_OF_EXISTED: 用户手动复制的，保留节点不删除
                    // 只是切换到新的书签，原来的复制节点保留
                    voiceHandler.clearLinkedBookmarkNode();
                    // 不删除节点！用户手动复制的应该保留
                    bookmarkOperationPanel.setInputAreaState(EditScriptState.IN_PREVIOUS_RECORD);
                    if (!isExampleCat) {
                        voiceHandler.assignLinkedBookmarkNode(userObject);
                        // 更新 Name 字段显示书签名称
                        bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
                    } else {
                        // For Example, show the Example's original name (not CopyOf)
                        bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
                    }
                    inputAreaPanel.updateJTextAreaDirectImport(content.get());
                    // Update Current label
                    voiceHandler.setCurrentBookmarkName(userObject.getName(), isExampleCat, false);
                    break;
            }

            if (isExampleCat) {
                this.bookmarkOperationPanel.setAutoSaveBookmark2false();
            } else {
                this.bookmarkOperationPanel.setAutoSaveBookmark2true();
            }
        }
        bookmarkDisplayPanel.getjTree().updateUI();
    }

    private void actionCurrNodeIsModifiedWhenTurn4ExistBookmark(BookMarkNode targetNode, BookMarkNode sourceNode) {
        int choice = JOptionPane.showConfirmDialog(UnifiedAccessPoint.getInstanceFrame(),
                "The current data input area is under editing, Do you want to overwrite the original content?", "Confirmation",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, EGPSShellIcons.get("eGPS_logo16x16.png"));

        if (choice != JOptionPane.YES_OPTION && choice != JOptionPane.NO_OPTION) {
            // Do nothing
            return;
        }
        voiceHandler.clearLinkedBookmarkNode();

        //============
        if (choice == JOptionPane.YES_OPTION) {
            try {
                String inputContent = inputAreaPanel.getInputContent();
                sourceNode.setContent(inputContent);
            } catch (Exception e) {
                SwingDialog.showErrorMSGDialog(UnifiedAccessPoint.getResourceString("dialog.error"), e.toString());
                e.printStackTrace();
                return;
            }
        }
        // 这个改动之前需要 把原来的内容重新赋值过去
        //=============
        action4turn4ExistBookmark(targetNode);

        if (choice == JOptionPane.YES_OPTION) {
            bookmarkDisplayPanel.save();
        }

    }

    private void action4turn4ExistBookmark(BookMarkNode userObject) {
        Optional<String> content = userObject.getContent();
        // 保护性措施，实际上是不可能发生的，因为能执行到这一步肯定有内容。
        String str = content.get();
        if (content.isEmpty()){
            throw new IllegalStateException("No content, please call developers.");
        }

        boolean isExampleCat = userObject.isExample();
        if (isExampleCat) {
            this.bookmarkOperationPanel.setAutoSaveBookmark2false();
            // For Examples, show the Example's original name (not CopyOf)
            this.bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
        } else {
            this.bookmarkOperationPanel.setAutoSaveBookmark2true();
            voiceHandler.assignLinkedBookmarkNode(userObject);
            // 非 Example才更新这个名字
            this.bookmarkOperationPanel.updateJTextFieldContent(userObject.getName());
        }
        inputAreaPanel.updateJTextAreaDirectImport(str);
        bookmarkOperationPanel.setInputAreaState(EditScriptState.IN_PREVIOUS_RECORD);

        // Update Current label
        voiceHandler.setCurrentBookmarkName(userObject.getName(), isExampleCat, false);
    }

    /**
     * 放置需要和其他面板交互的动作，自己面板就能处理，不需要交互的，可以去掉
     */
    class BookmarkDisplayPanelActions {
        private BookmarkDisplayPanelActions() {
        }

        public void importAllBookmark() {
            EGPSFileChooser egpsFileChooser = new EGPSFileChooser(getClass());
            int showOpenDialog = egpsFileChooser.showOpenDialog();
            if (showOpenDialog != EGPSFileChooser.APPROVE_OPTION) {
                return;
            }
            File selectedFile = egpsFileChooser.getSelectedFile();
            try {
                EGPSFileUtil.copyFileUsingStream(selectedFile, new File(voiceHandler.internalFilePath2storageConfigFiles4voice()));
                String str1 = UnifiedAccessPoint.getResourceString("dialog.info");
                String msg = "Successfully import. Close current module and re-open to see import bookmarks.";
                SwingDialog.showInfoMSGDialog(str1, msg);
                voiceHandler.closeDialogIfPossible();
            } catch (IOException e1) {
                SwingDialog.showErrorMSGDialog(UnifiedAccessPoint.getResourceString("dialog.error"), e1.getMessage());
            }
        }

        public void duplicate(BookMarkNode targetNode) {
            //1. 产生一个新的节点，2.直接复用 commonProcess4userTurn2anotherBookmark
            // 3. 注意这个name是targetNode的name

            Optional<String> content = targetNode.getContent();
            String sourceName = targetNode.getName();
            if (content.isEmpty()){
                throw new IllegalStateException("Cannot duplicate a bookmark without content");
            }

            // Keep the original name for Duplicate (no prefix)
            BookMarkNode markNode = bookmarkDisplayPanel.addLeafNodeWithUserInputName(sourceName, content.get());
            action4singleLeftClick2anotherBookmark(markNode);

            // Duplication之后，输入框状态要变为 IN_PREVIOUS_RECORD，因为现在是复制，用户肯定有作用
            bookmarkOperationPanel.setInputAreaState(EditScriptState.IN_PREVIOUS_RECORD);
            bookmarkOperationPanel.updateJTextFieldContentIfPossible(null);
        }

        public void deleteAllRecords() {
            voiceHandler.clearLinkedBookmarkNode();

            bookmarkDisplayPanel.clearAllRecords();
            bookmarkDisplayPanel.save();

            bookmarkOperationPanel.setAutoSaveBookmark2false();
            inputDataAreaPanelActions.userClickTurnToExamples();
        }

        public void doubleClick2run() {
            //DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) jTree.getLastSelectedPathComponent();
            // Dont need do this, because first click will select the node
            new Thread(voiceHandler).start();
        }

        /**
         * Reset all bookmarks to factory defaults.
         * This will delete all user bookmarks and restore original examples.
         */
        public void resetToFactoryDefaults() {
            // Clear linked bookmark reference
            voiceHandler.clearLinkedBookmarkNode();

            // Clear last used bookmark memory
            voiceHandler.setLastUsedBookmarkName(null);
            voiceHandler.setLastUsedBookmarkContentHash(null);

            // Delete the storage file
            File storageFile = new File(voiceHandler.internalFilePath2storageConfigFiles4voice());
            if (storageFile.exists()) {
                EGPSFileUtil.forceDelete(storageFile);
            }

            // Notify user and close dialog to reinitialize
            String str1 = UnifiedAccessPoint.getResourceString("dialog.info");
            String msg = "Factory defaults restored. Please re-open this module to see the changes.";
            SwingDialog.showInfoMSGDialog(str1, msg);
            voiceHandler.closeDialogIfPossible();
        }
    }

    /**
     * BookmarkOperationPanelActions belongs to a built-in eGPS module (loader, panel, or helper).
     */
    class BookmarkOperationPanelActions {
        private BookmarkOperationPanelActions() {
        }

        public void textFiledContentChanged(String newText) {
            BookMarkNode linkedBookMarkNode = voiceHandler.getLinkedBookMarkNode();
            if (linkedBookMarkNode == null) {
                throw new RuntimeException("It is impossible user will do this, linkedBookMarkNode is null. May be the textField listener is added when toggle button is unchecked.");
            } else {
                linkedBookMarkNode.setName(newText);
                // Sync Current label with Name field
                voiceHandler.setCurrentBookmarkName(newText, false, isUserEditedInputArea);
            }

            bookmarkDisplayPanel.getjTree().updateUI();
        }

        public void saveAndCreateButtonClicked(String newText) {

            // 这里要特别注意，这个新的inputContent也要赋值到原来的BookmarkNode中，否则出错
            try {
                BookMarkNode linkedBookMarkNode = voiceHandler.getLinkedBookMarkNode();
                String inputContent = inputAreaPanel.getInputContent();
                if (linkedBookMarkNode == null) {
                    // 此时没有 linked Node（用户当前查看 Example）
                    BookMarkNode markNode = bookmarkDisplayPanel.addLeafNodeWithUserInputName(newText, inputContent);
                    // 保存需要处理
                    bookmarkDisplayPanel.save();

                    // 设置 linked 节点
                    voiceHandler.assignLinkedBookmarkNode(markNode);

                    // 选中新节点
                    bookmarkDisplayPanel.selectBookmarkNode(markNode);

                    // 设置为 linked 状态
                    bookmarkOperationPanel.setAutoSaveBookmark2true();

                    // 记录"上一次使用"的书签，保证下次打开能回到这里（不依赖 execute）
                    if (!markNode.isExample()) {
                        voiceHandler.setLastUsedBookmarkName(markNode.getName());
                        voiceHandler.setLastUsedBookmarkContentHash(Integer.toHexString(inputContent.hashCode()));
                    }
                } else {
                    linkedBookMarkNode.setContent(inputContent);
                    boolean b = bookmarkOperationPanel.shouldAutoSaveBookmark();
                    bookmarkDisplayPanel.save();
                    if (!b){
                        throw new IllegalStateException("Impossible to get this, call developers.");
                    }
                    createNovelLinkedBookmarkByInputAreaText(newText, inputContent);
                    // createNovelLinkedBookmarkByInputAreaText 会把新节点设为 linkedBookMarkNode
                    voiceHandler.setLastUsedBookmarkName(newText);
                    voiceHandler.setLastUsedBookmarkContentHash(Integer.toHexString(inputContent.hashCode()));
                }
            } catch (IOException e) {
                SwingDialog.showErrorMSGDialog(UnifiedAccessPoint.getResourceString("dialog.error"), e.toString());
            }

            // 这些是状态的变化
            clearUserEditedFlag();
            bookmarkOperationPanel.updateJTextFieldContentIfPossible(newText);
            bookmarkOperationPanel.setInputAreaState(EditScriptState.COPY_OF_EXISTED);  // User explicitly duplicated

            // Update Current label
            voiceHandler.setCurrentBookmarkName(newText, false, false);

            SwingUtilities.invokeLater(() -> {
                bookmarkDisplayPanel.getjTree().updateUI();
            });
        }

        public void linkedButtonChanged(boolean upcomingState) throws Exception {
            if (upcomingState) {
                Optional<BookMarkNode> firstSelectedBookmarkNode = bookmarkDisplayPanel.getFirstSelectedBookmarkNode();
                if (firstSelectedBookmarkNode.isEmpty()) {
                    // No bookmark selected, use CopyOf_Example format
                    String name = voiceHandler.getBookmarkInitialName("Example");
                    String exampleText = voiceHandler.getExampleText();
                    createNovelLinkedBookmarkByInputAreaText(name, exampleText);
                    bookmarkOperationPanel.updateJTextFieldContent(name);  // Sync TextField with bookmark name
                    bookmarkOperationPanel.setInputAreaState(EditScriptState.COPY_ON_ENTRY);  // Temporary: auto-created on entry
                    bookmarkOperationPanel.setAutoSaveBookmark2true();  // Enable linking for Name field sync
                    // Update Current label to show the new linked bookmark (not Example)
                    voiceHandler.setCurrentBookmarkName(name, false, false);
                }else {
                    BookMarkNode markNode = firstSelectedBookmarkNode.get();
                    if (markNode.isExample()){
                        // Creating from Example, use CopyOf format
                        String name = voiceHandler.getBookmarkInitialName(markNode.getName());
                        createNovelLinkedBookmarkByInputAreaText(name, markNode.getContent().get());
                        bookmarkOperationPanel.updateJTextFieldContent(name);  // Sync TextField with bookmark name
                        bookmarkOperationPanel.setInputAreaState(EditScriptState.COPY_ON_ENTRY);  // Temporary: auto-created on entry
                        bookmarkOperationPanel.setAutoSaveBookmark2true();  // Enable linking for Name field sync
                        // Update Current label to show the new linked bookmark (not Example)
                        voiceHandler.setCurrentBookmarkName(name, false, false);
                    }else {
                        voiceHandler.assignLinkedBookmarkNode(markNode);
                        // 更新 Name 字段显示书签名称
                        bookmarkOperationPanel.updateJTextFieldContent(markNode.getName());
                        bookmarkOperationPanel.setInputAreaState(EditScriptState.IN_PREVIOUS_RECORD);
                        bookmarkOperationPanel.setAutoSaveBookmark2true();  // Enable linking for Name field sync
                        // Update Current label
                        voiceHandler.setCurrentBookmarkName(markNode.getName(), false, false);
                        bookmarkDisplayPanel.getjTree().updateUI();
                    }

                }
            } else {
                BookMarkNode linkedBookMarkNode = voiceHandler.getLinkedBookMarkNode();
                if (linkedBookMarkNode != null) {
                    if (linkedBookMarkNode.isExample()) {
                        throw new IllegalStateException("Its impossible for example bookmark to be linked bookmark to input data area.");
                    }
                    EditScriptState inputAreaState = bookmarkOperationPanel.getInputAreaState();
                    switch (inputAreaState) {
                        case IN_PREVIOUS_RECORD, USER_MODIFIED, COPY_OF_EXISTED:
                            // 这些状态下，节点应该保留
                            voiceHandler.clearLinkedBookmarkNode();
                            break;
                        case COPY_ON_ENTRY:
                            // COPY_ON_ENTRY: 临时节点，取消链接时删除
                            voiceHandler.clearLinkedBookmarkNode();
                            bookmarkDisplayPanel.removeBookmarkNode(linkedBookMarkNode);
                            break;
                        default:
                            throw new IllegalStateException("New EditScriptState need to handle.");
                    }

                } else {
                    throw new IllegalStateException("linkedBookMarkNode is null, this should not happened, please tell developers.");
                }
            }
        }
    }

    /**
     * 这里仅放置需要交互的代码，其它的一个面板就能处理的都放到自己的Panel处理即可
     */
    class InputDataAreaPanelActions {
        private InputDataAreaPanelActions() {
        }

        public void userEditContent() {
            isUserEditedInputArea = true;
            bookmarkOperationPanel.setInputAreaState(EditScriptState.USER_MODIFIED);

            BookMarkNode linkedBookMarkNode = voiceHandler.getLinkedBookMarkNode();
            if (linkedBookMarkNode == null) {
                bookmarkOperationPanel.setAutoSaveBookmark2true();
                try {
                    bookmarkOperationPanel.action4toggleButtonSelected();
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }else {
                // 已经有一个linked bookmark node了，更新 Current 标签显示修改状态
                voiceHandler.setCurrentBookmarkName(linkedBookMarkNode.getName(), false, true);
            }
        }

        public void userClickTurnToExamples() {
            BookMarkNode markNode = bookmarkDisplayPanel.getExampleBookmarkNode();
            action4singleLeftClick2anotherBookmark(markNode);
        }
    }

    private void clearUserEditedFlag() {
        isUserEditedInputArea = false;
    }

    void saveTheModificationWhenExit() {
        try {
            EditScriptState inputAreaState = bookmarkOperationPanel.getInputAreaState();
            BookMarkNode linkedBookMarkNode = voiceHandler.getLinkedBookMarkNode();

            // COPY_ON_ENTRY: 临时节点，如果没有修改过则删除不保存
            if (inputAreaState == EditScriptState.COPY_ON_ENTRY && !isUserEditedInputArea) {
                if (linkedBookMarkNode != null) {
                    bookmarkDisplayPanel.removeBookmarkNode(linkedBookMarkNode);
                    voiceHandler.clearLinkedBookmarkNode();
                }
                bookmarkDisplayPanel.save();
                return;
            }

            String inputContent = inputAreaPanel.getInputContent();
            if (linkedBookMarkNode == null) {
                String name = bookmarkOperationPanel.getUserInputText4bookmark();
                BookMarkNode created = bookmarkDisplayPanel.addLeafNodeWithUserInputName(name, inputContent);
                if (!created.isExample()) {
                    voiceHandler.setLastUsedBookmarkName(created.getName());
                    voiceHandler.setLastUsedBookmarkContentHash(Integer.toHexString(inputContent.hashCode()));
                }
            }else {
                linkedBookMarkNode.setContent(inputContent);
                if (!linkedBookMarkNode.isExample()) {
                    voiceHandler.setLastUsedBookmarkName(linkedBookMarkNode.getName());
                    voiceHandler.setLastUsedBookmarkContentHash(Integer.toHexString(inputContent.hashCode()));
                }
            }
            bookmarkDisplayPanel.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
