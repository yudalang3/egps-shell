package egps2.builtin.modules.filemanager;

import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.largetextedi.MethodsForText2Editor;
import egps2.builtin.modules.lowtextedi.IndependentModuleLoader;
import egps2.frame.MyFrame;
import egps2.panels.dialog.SwingDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 我用Java Swing在写一个DirNavigatorPanel，它继承自JPanel，它的构造函数需要输入一个目录的路径，
 * 我需要根据这个路径动态生成一个面板，这个面板需要显示这个目录下的文件和文件夹，
 *
 * 请你按照我规划的技术路线来：
 * 1. 我在构造函数中已经写好了，我打算用JLabel直接放在BoxLayout中，不用JList了。
 * 2. 我这里写的一些事件方法可以重复使用，但是需要修改
 */
public class DirNavigatorPanel extends JPanel {

    private static final Logger log = LoggerFactory.getLogger(DirNavigatorPanel.class);
    private GuiMain guiMain;

    final Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
    final Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
    private ExplorerPanel explorerPanel;

    final private Border selectedBorder = BorderFactory.createLineBorder(new Color(2, 153, 236), 2, true);
    private final Border normalBorder = BorderFactory.createEmptyBorder();

    final private DirNavigatorPanelElement dirNavigatorPanelElement;

    public DirNavigatorPanel(DirNavigatorPanelElement dirNavigatorPanelElement) {
        Path currDirPath = dirNavigatorPanelElement.getCurrDirPath();
        Optional<Path> selectedPathOpt = dirNavigatorPanelElement.getSelectedPathOpt();
        this.dirNavigatorPanelElement = dirNavigatorPanelElement;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Modern flat design - no raised border
        setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        // macOS Finder-style white background
        setBackground(new Color(255, 255, 255));

        // 初始化路径分段
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        List<Path> children = null;
        try {
            children = Files.list(currDirPath).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Path path : children) {
            JLabel4NavigatorPanel label = createJLabel4NavigatorPanel(path, fileSystemView, selectedPathOpt);
            add(label);
        }

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    mouseClickEvent(e, currDirPath, null);
                } else {
                    UnifiedAccessPoint.getInstanceFrame().showTipsOnBottomStatusBar(currDirPath.toString());
                }
            }
        });
    }

    private JLabel4NavigatorPanel createJLabel4NavigatorPanel(Path path, FileSystemView fileSystemView, Optional<Path> selectedPathOpt) {
        JLabel4NavigatorPanel label = getJLabel4NavigatorPanel(path, fileSystemView);
        if (selectedPathOpt.isPresent() && Objects.equals(path, selectedPathOpt.get())) {
            label.setBorder(selectedBorder);
        } else {
            label.setBorder(normalBorder);
        }
        return label;
    }

    private JLabel4NavigatorPanel getJLabel4NavigatorPanel(Path path, FileSystemView fileSystemView) {
        JLabel4NavigatorPanel label = new JLabel4NavigatorPanel();
        label.setIcon(fileSystemView.getSystemIcon(path.toFile()));
        label.setText(path.getFileName().toString());

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel4NavigatorPanel source = (JLabel4NavigatorPanel) e.getSource();
                mouseClickEvent(e, path, source);
                Component[] components = DirNavigatorPanel.this.getComponents();
                for (Component component : components) {
                    if (!(component instanceof JLabel4NavigatorPanel)) {
                        continue;
                    }
                    JLabel4NavigatorPanel label = (JLabel4NavigatorPanel) component;
                    if (label != source) {
                        label.setSelected(false);
                    } else {
                        source.setSelected(true);
                    }
                }
            }
        });
        label.setFont(defaultFont);
        return label;
    }

    private void mouseClickEvent(MouseEvent e, Path selectedFilePath, JLabel4NavigatorPanel source) {
        boolean isBlankArea = selectedFilePath == null;
        if (e.getClickCount() == 2) { // 双击进入新目录
            if (source != null && Files.isDirectory(selectedFilePath)) {
                explorerPanel.reOpenTheDirPath(this, selectedFilePath);
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            JPopupMenu popupMenu = getPopupMenu(selectedFilePath, source);
            Point point = e.getPoint();
            popupMenu.show((Component) e.getSource(), point.x, point.y);
        } else {
            //  Single left  click
            if (selectedFilePath != null) {
                guiMain.fileInfoPanel.changeFile(selectedFilePath);
                UnifiedAccessPoint.getInstanceFrame().showTipsOnBottomStatusBar(selectedFilePath.toString());
            }
        }
    }


    private JPopupMenu getPopupMenu(Path path, JLabel4NavigatorPanel source) {
        JPopupMenu popupMenu = new JPopupMenu();

        boolean isBlankArea = source == null;
        if (Files.isRegularFile(path) && source != null) {
            JMenuItem openWithEditor = new JMenuItem("Open with editor");
            openWithEditor.setToolTipText("Open the text file with eGPS text editor");
            openWithEditor.addActionListener(e -> {
                final IndependentModuleLoader independentModuleLoader = new IndependentModuleLoader();
                SwingWorker<Void, Void> swingWorker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        independentModuleLoader.setImportDataInfo(null, path.toFile());
                        return null;
                    }

                    @Override
                    protected void done() {
                        UnifiedAccessPoint.loadTheModuleFromIModuleLoader(independentModuleLoader);
                    }
                };
                swingWorker.execute();

            });
            popupMenu.add(openWithEditor);
            JMenuItem openWithLargeTextEditor = new JMenuItem("Open with large text view");
            openWithLargeTextEditor.setToolTipText("Open the text file with eGPS text large volume text view");
            openWithLargeTextEditor.addActionListener(e -> {
                MethodsForText2Editor methodsForText2Editor = new MethodsForText2Editor();
                methodsForText2Editor.addNewTextEditorTab(path.toFile(), false);
                guiMain.invokeTheFeatureMethod(2);
            });
            popupMenu.add(openWithLargeTextEditor);
            popupMenu.addSeparator();
        }

        if (Files.isDirectory(path)) {
            JMenuItem addToBookmark = new JMenuItem("Add to bookmark");
            addToBookmark.setToolTipText("Add the path to the bookmark panel");
            popupMenu.add(addToBookmark);
            addToBookmark.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(UnifiedAccessPoint.getInstanceFrame(), "Enter bookmark name:");
                if (input == null || input.trim().isEmpty()) {
                    SwingDialog.showErrorMSGDialog("Input error", "Bookmark name cannot be empty");
                    return;
                }

                BookmarkPanel bookmarkPanel = this.guiMain.bookmarkPanel;
                BookmarkElement bookmarkElement = new BookmarkElement(input, path, path);
                bookmarkPanel.appendNewBookmark(bookmarkElement);

            });
        }

        {
            JMenuItem copyFilePathBookmark = new JMenuItem("Copy file path");
            copyFilePathBookmark.setToolTipText("Copy the absolute file path");
            popupMenu.add(copyFilePathBookmark);
            copyFilePathBookmark.addActionListener(e -> {
                String absolutePath = path.toAbsolutePath().toString();
                // 创建 StringSelection 对象，用于封装要复制到剪贴板的文本
                StringSelection stringSelection = new StringSelection(absolutePath);
                // 获取系统剪贴板
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                // 将字符串放到剪贴板上
                clipboard.setContents(stringSelection, null);
                // 可选：给用户一些反馈，比如弹出一个小提示框
                UnifiedAccessPoint.getInstanceFrame().prompt("Already copy to the clipboard...");
            });
        }

        {
            JMenuItem showDetails = new JMenuItem("Display details");
            showDetails.addActionListener(e -> {
                new FileManagerFileDetailsDialog().showCustomFileDetailsDialog(UnifiedAccessPoint.getInstanceFrame(),
                        path.toFile());
            });
            showDetails.setToolTipText("Show the details of the file");
            popupMenu.add(showDetails);
            popupMenu.addSeparator();
        }


        /**
         * 还不行，更改是不能做的，要从长计议，因为这个是JLabel，不能动态改变长宽，只能用Border
         */
        if (!isBlankArea) {
            JMenuItem rename = new JMenuItem("Rename");
            rename.addActionListener(e -> {
                Optional<String> showInputDialog = SwingDialog.showInputDialog(UnifiedAccessPoint.getInstanceFrame(),
                        path.getFileName().toString());
                if (showInputDialog.isPresent()) {
                    //Files.move(path, path.resolveSibling(showInputDialog.get()))
                }
            });

            popupMenu.add(rename);
        }
        {
            JMenuItem rename = new JMenuItem("Create file");
            rename.addActionListener(e -> {
                createFileOrDir(path, isBlankArea, false);
            });

            popupMenu.add(rename);
        }
        {
            JMenuItem rename = new JMenuItem("Create dir.");
            rename.addActionListener(e -> {
                createFileOrDir(path, isBlankArea, true);
            });

            popupMenu.add(rename);
        }
        if (isBlankArea) {
            JMenuItem rename = new JMenuItem("Open win File Explorer");
            rename.addActionListener(e -> {
                if (path == null) {
                    throw new IllegalStateException("Path cannot be null");
                }
                if (!Desktop.isDesktopSupported()) {
                    SwingDialog.showErrorMSGDialog("Error", "Desktop is not supported");
                    return;
                }

                String os = System.getProperty("os.name").toLowerCase();
                File file = path.toFile();
                try {
                    if (os.contains("win")) {
                        // Windows: explorer
                        Runtime.getRuntime().exec(new String[]{"explorer", file.getAbsolutePath()});
                    } else if (os.contains("mac")) {
                        // macOS: open
                        Runtime.getRuntime().exec(new String[]{"open", file.getAbsolutePath()});
                    } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
                        // Linux/Unix: xdg-open
                        Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
                    } else {
                        System.err.println("Unsupported OS: " + os);
                    }
                } catch (IOException e2) {
                    SwingDialog.showErrorMSGDialog("Error", "Failed to open file explorer: " + e2.getMessage());
                }
            });

            popupMenu.add(rename);
        }
        if (!isBlankArea) {
            JMenuItem rename = new JMenuItem("Delete");
            rename.addActionListener(e -> {
                MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
                if (path == null || !Files.exists(path)) {
                    SwingDialog.showErrorMSGDialog("Error", "File does not exist!");
                    return;
                }
                if (Files.isDirectory(path)) {
                    SwingDialog.showInfoMSGDialog("Directory not supported", "For security reasons, deletion of entire directories is currently disabled.");
                    return;
                }

                int response = JOptionPane.showConfirmDialog(instanceFrame,
                        "Are you sure you want to delete: " + path.toAbsolutePath() + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        Files.delete(path);
                        remove(source);
                        instanceFrame.showTipsOnBottomStatusBar("Successfully deleted: " + path.toAbsolutePath());
                    } catch (IOException ex) {
                        SwingDialog.showErrorMSGDialog("Error", "Failed to delete: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });

            popupMenu.add(rename);
        }

        return popupMenu;
    }

    private void createFileOrDir(Path path, boolean isBlankArea, boolean isDir) {
        Optional<String> showInputDialog = SwingDialog.showInputDialog(UnifiedAccessPoint.getInstanceFrame(),
                "");
        if (!showInputDialog.isPresent()) {
            return;
        }
        MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
        Path dirPath;
        if (isBlankArea) {
            dirPath = Path.of(path.toString(), showInputDialog.get());
        } else {
            dirPath = Path.of(path.getParent().toString(), showInputDialog.get());

        }
        try {
            // 创建目录
            if (isDir) {
                Files.createDirectory(dirPath);
            } else {
                Files.createFile(dirPath);
            }
            instanceFrame.showTipsOnBottomStatusBar("Successfully deleted: " + dirPath.toAbsolutePath());
            FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            JLabel4NavigatorPanel jLabel4NavigatorPanel = getJLabel4NavigatorPanel(dirPath, fileSystemView);
            add(jLabel4NavigatorPanel);
        } catch (IOException e1) {
            SwingDialog.showErrorMSGDialog("Error", "Failed to create directory: " + e1.getMessage());
        }
    }

    public void setModuleFace(GuiMain guiMain) {
        this.guiMain = guiMain;

    }

    public void setExplorerPanel(ExplorerPanel explorerPanel) {
        this.explorerPanel = explorerPanel;
    }

}