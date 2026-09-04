package egps2.builtin.modules.voice;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.*;

import com.raven.swing.ButtonBadges;
import javax.swing.JLabel;
import egps2.utils.EGPSIconUtil;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;

import com.alibaba.fastjson.JSONObject;
import com.jidesoft.swing.JideBorderLayout;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.panels.dialog.SwingDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.EGPSFileUtil;
import egps2.EGPSProperties;
import utils.EGPSFormatUtil;
import utils.datetime.DateTimeOperator;
import utils.storage.MapPersistence;
import egps2.UnifiedAccessPoint;
import egps2.frame.MyFrame;
import egps2.frame.gui.EGPSMainGuiUtil;
import utils.string.EGPSStringUtil;

/**
 * In fact, this is a GUI class. GUI的基类。
 *
 * <pre>
 * |-------------------------------------------------------------------|
 * |content : ImportDataDialog                                         |
 * |                            |                                      |
 * |Tree dialog                 |  right JPanel                        |
 * |                            |   |----------------------------------|
 * |                            |   |                             |    |
 * |                            |   | Input area                  |    |
 * |                            |   |                             |    |
 * |                            |   | ----------------------------|    |
 * |                            |   |                             |    |
 * |                            |   | Bookmark operation          |    |
 * |                            |   |                             |    |
 * |                            |   |-----------------------------|    |
 * |                            |   |                             |    |
 * |                            |   | Button                      |    |
 * |                            |   |                             |    |
 * |-------------------------------------------------------------------|
 *
 * </pre>
 *
 * Handy Tools或者一些嵌入的模块，比如Handy Tools for biologist。
 * 他们下面还有一个Console的功能，这个是额外在下面又包了一层
 */
public abstract class VersatileOpenInputClickAbstractGuiBase implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(VersatileOpenInputClickAbstractGuiBase.class);
    private JDialog inputDialog;

    private String fileName4store;
    private static final String SAVE_KEY = "voice.general.dialog.save.key";

    InputAreaPanel dataImportPanel;
    BookmarkOperationPanel bookmarkOperationPanel;
    BookmarkDisplayPanel bookmarkDisplayPanel;
    private JButton doExecuteActionButton;

    private final AtomicReference<BookMarkNode> linkedBookMarkNode = new AtomicReference<>();

    BookmarkNodeOperation bookmarkNodeOperation = new BookmarkNodeOperation();

    final DateTimeOperator dateTimeOperator = new DateTimeOperator();
    EventUniformlyProcessor eventUniformlyProcessor;

    private long lastExecuteTime = System.currentTimeMillis();

    // Current bookmark label (displayed in JXTitledPanel right decoration)
    private JLabel currentBookmarkLabel;
    private static final Color EXAMPLE_COLOR = new Color(0x888888);  // Gray for examples
    private static final Color LINKED_COLOR = new Color(0x4A90D9);   // Blue for linked bookmarks

    /**
     * 上一次使用的书签名称（会话级记忆）
     * null = 用户未使用过任何书签，应该创建新节点
     */
    private String lastUsedBookmarkName = null;

    /**
     * 上一次使用的书签内容哈希（会话级记忆）
     * 用于精确匹配同名书签
     */
    private String lastUsedBookmarkContentHash = null;

    /**
     * 对于VOICE的一个需求是 作为一个Floating的数据导入Dialog，但是这个Dialog如果一个模块已经有了
     * 就不能同时打开两个以免引起混乱
     */
    private static final AtomicBoolean alreadyHasOneImportDialog = new AtomicBoolean(false);

    /**
     * Widget belongs to a built-in eGPS module (loader, panel, or helper).
     */
    public record Widget(JComponent component, String name, Integer width) {
    }

    public VersatileOpenInputClickAbstractGuiBase() {
        // 初始化的过程在 generateImportDataDialogGUI()方法中，很多变了也是在那里
    }


    String internalFilePath2storageConfigFiles4voice() {
        if (fileName4store == null) {
            fileName4store = EGPSStringUtil.format("{}/voice/{}.voice.store.gz", EGPSProperties.JSON_DIR, getFile4storage());
        }

        return fileName4store;
    }

    /**
     * 来源于一个需求，我们写了DIYTools（也就是Handy Tools for biologist）之后，发现每个地方的书签不独立了，这里提供一个复写的方法
     * 这样操作之后可以保证模块之间不会相互影响，因为每个保存的文件都不会重名。
     *
     * @return the file name for persistent storage
     */
    protected String getFile4storage() {
        return getClass().getName();
    }

    /**
     * 该方法来源于一个需求：
     * 每次调用VOICE界面之后，示例数据总是不能从头开始。（调用VOICE有两种：一种是弹出独立Dialog形式还有一种是内嵌在模块中）
     * 所以这里新增一个方法，它会在出现VOICE之前被调用，开发者可以在这里实现一些功能。例如重置exampleIndex
     */
    protected void actionBeforeVoiceGuiAppear() {

    }

    /**
     * BookmarkNodeOperation belongs to a built-in eGPS module (loader, panel, or helper).
     */
    class BookmarkNodeOperation {
        public void exportAllBookmark() {
            bookmarkDisplayPanel.save();
            EGPSFileChooser egpsFileChooser = new EGPSFileChooser(getClass());
            int showOpenDialog = egpsFileChooser.showSaveDialog();
            if (showOpenDialog == EGPSFileChooser.APPROVE_OPTION) {
                File selectedFile = egpsFileChooser.getSelectedFile();
                try {
                    EGPSFileUtil.copyFileUsingFileChannels(internalFilePath2storageConfigFiles4voice(),
                            selectedFile.getAbsolutePath());
                    SwingDialog.successExportDataDialog();
                } catch (IOException e1) {
                    SwingDialog.showErrorMSGDialog(UnifiedAccessPoint.getResourceString("dialog.error"), e1.getMessage());
                }
            }

        }

    }

    /**
     * This for the HOW TO USE guides
     * @return JComponent, tooltip and how to Display the string.
     */
    public List<Widget> getImportantWidgets() {
        List<Widget> list = new ArrayList<>();

        int fontSize = 5;


        {
            String resourceString = UnifiedAccessPoint.getResourceString("voice.demo.bookmark.display");
            String htmlStr = EGPSFormatUtil.html32Concat(fontSize, resourceString);
            Widget widget = new Widget(bookmarkDisplayPanel.getjTree(), htmlStr, SwingConstants.RIGHT);
            list.add(widget);
        }

        {
            JTextArea jTextAreaDirectImport = dataImportPanel.getJTextAreaDirectImport();
            String resourceString = UnifiedAccessPoint.getResourceString("voice.demo.input.area");
            String htmlStr = EGPSFormatUtil.html32Concat(fontSize, resourceString);
            Widget widget = new Widget(jTextAreaDirectImport, htmlStr, SwingConstants.BOTTOM);
            list.add(widget);
        }

        {
            ButtonBadges exampleButtonBadges = dataImportPanel.getExampleButtonBadges();
            String resourceString = UnifiedAccessPoint.getResourceString("voice.demo.example.bottom");
            String htmlStr = EGPSFormatUtil.html32Concat(fontSize, resourceString);
            Widget widget = new Widget(exampleButtonBadges, htmlStr, SwingConstants.LEFT);
            list.add(widget);
        }
        {
            JTextField textField = bookmarkOperationPanel.getUserInputTextField();
            String resourceString = UnifiedAccessPoint.getResourceString("voice.demo.bookmark.name");
            String htmlStr = EGPSFormatUtil.html32Concat(fontSize, resourceString);
            Widget widget = new Widget(textField, htmlStr, SwingConstants.TOP);
            list.add(widget);
        }

        {
            JButton buttonCreateBook = bookmarkOperationPanel.getRecordJButtonCreateBook();
            String resourceString = UnifiedAccessPoint.getResourceString("voice.demo.record.button");
            String htmlStr = EGPSFormatUtil.html32Concat(fontSize, resourceString);
            Widget widget = new Widget(buttonCreateBook, htmlStr, SwingConstants.LEFT);
            list.add(widget);
        }

        {
            JLabel linkIndicator = bookmarkOperationPanel.getLinkIndicator();
            String resourceString = UnifiedAccessPoint.getResourceString("voice.demo.linking.button");

            String htmlStr = EGPSFormatUtil.html32Concat(fontSize, resourceString);
            Widget widget = new Widget(linkIndicator, htmlStr, SwingConstants.LEFT);
            list.add(widget);
        }

        {
            String resourceString = UnifiedAccessPoint.getResourceString("voice.demo.run.button");
            String htmlStr = EGPSFormatUtil.html32Concat(fontSize, resourceString);
            Widget widget = new Widget(doExecuteActionButton, htmlStr, SwingConstants.TOP);
            list.add(widget);
        }
        return list;
    }

    /**
     * 创建并返回一个对话框面板，用于导入数据。 该对话框包括一个树视图、数据导入面板、书签输入面板和一个执行按钮。
     * 当用户点击执行按钮时，将启动一个新线程来处理导入和执行的操作。
     *
     * 用途： 1. 在AlignmentView Heatmap等模块中，和JTabbedPane等组件一起使用。 2. 在DIY小工具模块中，下面可以再放个
     * Console控制台。 3. 可以放一个按钮，点击之后弹出一个对话框，这是这个类的主要设计。 开发者调用doUserImportAction方法即可。
     *
     * @doUserImportAction
     *
     * @return JPanel 导入数据对话框的面板。
     */
    public JPanel generateImportDataDialogGUI() {
        // 初始化树视图组件，用于导入对话框的一部分界面
        bookmarkDisplayPanel = new BookmarkDisplayPanel(this);

        Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
        Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

        String str = UnifiedAccessPoint.getResourceString("voice.module.button.importAr");

        // 创建执行按钮，并设置字体、焦点属性以及点击事件的处理
        doExecuteActionButton = new JButton(str);
        doExecuteActionButton.setFont(defaultFont);
        doExecuteActionButton.setFocusable(false);
        doExecuteActionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        InputStream tutorialIcon = VersatileOpenInputClickAbstractGuiBase.class.getResourceAsStream("images/execute.svg");
        ImageIcon runIcon = EGPSIconUtil.getIconFromSVGByStream(tutorialIcon, 20, 20);
        doExecuteActionButton.setIcon(runIcon);

        doExecuteActionButton.addActionListener(e -> {
            // 当按钮被点击时，启动一个新线程来执行操作
            // 这个语句是执行 Import and execute 按钮点击之后去执行的过程。

            if (System.currentTimeMillis() - lastExecuteTime < 1200) {
                SwingDialog.showInfoMSGDialog("Warning", "Are you a robot?\nPlease wait for a moment.");
                return;
            }
            new Thread(this).start();
            lastExecuteTime = System.currentTimeMillis();
        });

        // 创建并初始化按钮面板，设置其布局和边框
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(0, 6));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        // 创建底部面板，用于容纳书签输入面板和执行按钮
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 2));

        // 用方法以处理任何外观相关的初始化，需在获取示例文本前调用
        // 这个方法的调用要在 getExampleText()方法之前
        actionBeforeVoiceGuiAppear();
        {
            {
                // 创建数据导入面板，并设置其内容
                dataImportPanel = new InputAreaPanel(this);
                String exampleText = getExampleText();
                dataImportPanel.updateJTextAreaDirectImport(exampleText);

                // 初始化并配置一个带标题的面板，用于容纳数据导入面板
                JXTitledPanel tmpJxTaskPane = new JXTitledPanel();
//				tmpJxTaskPane.setBorder(BorderFactory.createRaisedSoftBevelBorder());
                Color fillColor = new Color(214, 223, 247);

                String str1 = UnifiedAccessPoint.getResourceString("voice.module.title.inputArea");
                tmpJxTaskPane.setTitle(str1);

                tmpJxTaskPane.setTitleFont(defaultTitleFont);
                Color decode = Color.decode("#2D65F7");
                tmpJxTaskPane.setTitleForeground(decode);
                MattePainter p = new MattePainter(fillColor);
                tmpJxTaskPane.setTitlePainter(p);

                // Add current bookmark label to the right side of the title
                currentBookmarkLabel = new JLabel("Current: ");
                currentBookmarkLabel.setFont(defaultTitleFont);
                currentBookmarkLabel.setForeground(EXAMPLE_COLOR);
                currentBookmarkLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                tmpJxTaskPane.setRightDecoration(currentBookmarkLabel);

                tmpJxTaskPane.add(dataImportPanel);

                rightPanel.add(tmpJxTaskPane, BorderLayout.CENTER);
            }
            // ======================> 加入 Bookmark面板 <================== //
            {
                JXTaskPane bookmarkOperationPanel = new JXTaskPane();
                bookmarkOperationPanel.setFont(defaultTitleFont);

                String str1 = "Interactive operation among Input Area, Bookmark Display Tree and Bookmark Operations";
                bookmarkOperationPanel.setTitle(str1);

                this.bookmarkOperationPanel = new BookmarkOperationPanel(this);
                bookmarkOperationPanel.add(this.bookmarkOperationPanel);

                bottomPanel.add(bookmarkOperationPanel, BorderLayout.NORTH);
            }

        }
        // 将执行按钮添加到底部面板的南边，并将其对齐方式设置为居中
        doExecuteActionButton.setAlignmentX(0.5f);
        bottomPanel.add(doExecuteActionButton, BorderLayout.SOUTH);
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        bottomPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // 创建主面板，并设置其布局为边框布局
        JPanel retJPanel = new JPanel(new BorderLayout());
        retJPanel.add(bookmarkDisplayPanel, JideBorderLayout.WEST);
        retJPanel.add(rightPanel, JideBorderLayout.CENTER);
        // 为主面板设置边
        retJPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        eventUniformlyProcessor = new EventUniformlyProcessor(this);

        SwingUtilities.invokeLater(() -> {
            eventUniformlyProcessor.produceInitialBookmark();
        });
        return retJPanel;
    }

    /**
     * 对于内嵌在模块中的VOICE，当关闭模块的时候，需要调用这个方法
     * 这里需要注意，对于小工具模块。点击了运行按钮之后不会。对话框不会关闭，而是会存在。但是也需要执行 对话框关闭时一样的动作。 具体做的事的解释：
     * 它负责保存对话框位置、关闭对话框、保存树结构状态，并重置已导入对话框的标志
     */
    public void actionOfExistOrRunButton() {
        // 如果输入对话框存在，则获取其位置和大小，并将其保存
        if (inputDialog != null) {
            Rectangle bounds = inputDialog.getBounds();

            // 从持久化存储中加载对话框位置的映射
            Map<String, String> str2strMap = MapPersistence.getStr2strMap(EGPSMainGuiUtil.DIALOG_SAVE_PATH);
            // 将对话框的边界转换为JSON字符串，以便存储
            String jsonString = JSONObject.toJSONString(bounds, false);
            // 使用特定的键保存对话框的位置和大小
            str2strMap.put(SAVE_KEY, jsonString);
            // 将更新后的映射持久化存储
            MapPersistence.storeStr2strMap(str2strMap, EGPSMainGuiUtil.DIALOG_SAVE_PATH);

            // 关闭并清理引用，以释放资源
            closeDialogIfPossible();
        }
        // 重置标志，表示没有已导入的对话框
        alreadyHasOneImportDialog.set(false);
    }

    void closeDialogIfPossible() {
        if (inputDialog != null) {
            inputDialog.dispose();
            inputDialog = null;
        }
    }

    /**
     * 这个方法是主要弹出窗口的方法，主要运行流程在这里面。
     */
    public void doUserImportAction() {
        // 原子性地检查并设置标志，避免竞态条件
        if (!alreadyHasOneImportDialog.compareAndSet(false, true)) {
            // 已经有对话框打开，聚焦到现有对话框
            if (inputDialog != null) {
                inputDialog.requestFocusInWindow();
            }
            return;
        }

        try {
            JPanel importDataDialog = generateImportDataDialogGUI();

            // 根据是否有上次使用的书签来决定对话框标题
            String dialogTitle;
            String baseTitle = UnifiedAccessPoint.getResourceString("dialog.import");
            String lastBookmarkName = getLastUsedBookmarkName();
            if (lastBookmarkName != null && !lastBookmarkName.isEmpty()) {
                // 复用模式：显示书签名称
                dialogTitle = baseTitle + ": Enter with Previous Bookmark";
            } else {
                // 新建模式
                dialogTitle = baseTitle + ": Create New Bookmark";
            }

            MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
            inputDialog = new JDialog(instanceFrame, dialogTitle);
            inputDialog.getContentPane().add(importDataDialog, BorderLayout.CENTER);

            // 关闭的动作
            ActionListener escListener = e -> {
                actionOfExistOrRunButton();
                EditScriptState inputAreaState = bookmarkOperationPanel.getInputAreaState();
                if (inputAreaState == EditScriptState.USER_MODIFIED) {
                    String title = UnifiedAccessPoint.getResourceString("common.save.confirm.title");
                    String msg = UnifiedAccessPoint.getResourceString("common.save.confirm.msg");
                    // 提示用户保存输入数据
                    int option = JOptionPane.showConfirmDialog(inputDialog,
                            msg, title,
                            JOptionPane.YES_NO_CANCEL_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        // 保存输入数据
                        getEventUniformlyProcessor().saveTheModificationWhenExit();
                    }
                }

            };

            inputDialog.getRootPane().registerKeyboardAction(escListener, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                    JComponent.WHEN_IN_FOCUSED_WINDOW);

            inputDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    escListener.actionPerformed(null);
                }
            });

            Optional<Rectangle> dialogSize = EGPSMainGuiUtil.getDialogSize(SAVE_KEY);
            if (dialogSize.isPresent()) {
                Rectangle object = dialogSize.get();
                inputDialog.setBounds(object);
            } else {
                inputDialog.setSize(1000, 900);
                inputDialog.setLocationRelativeTo(instanceFrame);
            }

            inputDialog.setVisible(true);
        } catch (Exception e) {
            // 如果初始化失败，重置标志
            alreadyHasOneImportDialog.set(false);
            throw e;
        }
    }

    @Override
    public void run() {
        /*
         * getImportDataDialog() 这个线程的调用方法在这里
         */
        if (!dataImportPanel.checkInput()) {
            // 没有内容则返回
            return;
        }

        boolean busyStateSet = false;
        try {
            setBusyState(true);
            busyStateSet = true;

            getEventUniformlyProcessor().actionWhenUserExecute();
            var theContent = dataImportPanel.getInputContent();
            execute(theContent);
            actionOfExistOrRunButton();

        } catch (Exception e) {
            log.error("Error during VOICE execution", e);

            String str = UnifiedAccessPoint.getResourceString("dialog.error");
            String message = e.getMessage() != null
                ? e.getMessage().concat(UnifiedAccessPoint.getResourceString("dialog.msg.check.content"))
                : UnifiedAccessPoint.getResourceString("dialog.msg.check.content");
            SwingDialog.showErrorMSGDialog(str, message);

        } finally {
            if (busyStateSet) {
                setBusyState(false);
            }
        }
    }

    /**
     * 获取一个例子，这个例子在默认打开VOICE面板的时候，会显示在VOICE面板里面。如果有多个Example则框架会再次调用这个方法。
     * 开发者自己返回不同的Example内容。
     *
     * @return String 放在JTextArea中的内容
     */
    protected abstract String getExampleText();

    /**
     * 根据输入的结果进行处理，每个子类有自己特异的处理方法。 不用再开启新的线程了，这本身就是在一个新的线程里面
     *
     * @param inputs : JTextArea中的内容
     */
    protected abstract void execute(String inputs) throws Exception;

    protected int getNumberOfExamples() {
        return 1;
    }


    public BookMarkNode getLinkedBookMarkNode() {
        return linkedBookMarkNode.get();
    }

    public void clearLinkedBookmarkNode() {
        BookMarkNode previous = linkedBookMarkNode.getAndSet(null);
        if (previous != null) {
            SwingUtilities.invokeLater(() -> {
                bookmarkDisplayPanel.getjTree().updateUI();
            });
        }
    }

    public void assignLinkedBookmarkNode(BookMarkNode markNode) {
        linkedBookMarkNode.set(markNode);
    }

    public void setLastUsedBookmarkName(String name) {
        this.lastUsedBookmarkName = name;
    }

    public String getLastUsedBookmarkName() {
        return lastUsedBookmarkName;
    }

    /**
     * Set the current bookmark name displayed in the title bar.
     * @param name The bookmark name to display
     * @param isExample true if viewing an example (gray color), false if linked to user bookmark (blue color)
     * @param isModified true if the content has been modified (adds asterisk)
     */
    public void setCurrentBookmarkName(String name, boolean isExample, boolean isModified) {
        if (currentBookmarkLabel == null) {
            return;
        }
        String displayText = "Current: " + name;
        if (isModified) {
            displayText += " *";
        }
        currentBookmarkLabel.setText(displayText);
        currentBookmarkLabel.setForeground(isExample ? EXAMPLE_COLOR : LINKED_COLOR);
    }

    public void setLastUsedBookmarkContentHash(String hash) {
        this.lastUsedBookmarkContentHash = hash;
    }

    public String getLastUsedBookmarkContentHash() {
        return lastUsedBookmarkContentHash;
    }

    /**
     * Global method for generating bookmark initial names.
     * Single source of truth for CopyOf naming format.
     * @param sourceName The source bookmark/example name to copy from
     * @return Formatted name like "CopyOf_Example-1_0108_1845"
     */
    public String getBookmarkInitialName(String sourceName) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd_HHmm");
        String timestamp = sdf.format(new Date());
        return "CopyOf_" + sourceName + "_" + timestamp;
    }

    public EventUniformlyProcessor getEventUniformlyProcessor() {
        return eventUniformlyProcessor;
    }

    /**
     * Update busy UI states in a Swing-safe way.
     *
     * <p>This is called from a background execution thread; all Swing UI changes must be on EDT.</p>
     */
    private void setBusyState(boolean busy) {
        SwingUtilities.invokeLater(() -> {
            UnifiedAccessPoint.getInstanceFrame().becomeBusy(busy);

            JDialog dialog = inputDialog;
            if (dialog == null) {
                return;
            }

            Component glassPane = dialog.getGlassPane();
            if (glassPane instanceof JComponent jComponent) {
                jComponent.setOpaque(false);
            }
            glassPane.setCursor(Cursor.getPredefinedCursor(busy ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
            glassPane.setVisible(busy);
        });
    }
}
