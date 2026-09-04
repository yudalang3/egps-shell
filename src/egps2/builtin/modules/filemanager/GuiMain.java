package egps2.builtin.modules.filemanager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

import egps2.utils.common.model.filefilter.FileFilterTxt;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;

import egps2.panels.dialog.EGPSFileChooser;
import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;
import egps2.frame.ComputationalModuleFace;
import egps2.modulei.IModuleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * GuiMain belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class GuiMain extends ComputationalModuleFace {

    private static final Logger log = LoggerFactory.getLogger(GuiMain.class);
    ExplorerPanel explorerPanel;
    FileInfoPanel fileInfoPanel;
    BookmarkPanel bookmarkPanel;

    final PropertiesSetter propertiesSetter = new PropertiesSetter();
    final StorageIO storageIO = new StorageIO();

    public DefaultListModel<BookmarkElement> bookmarkListModel = new DefaultListModel<>();

    LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
    Font defaultFont = launchProperty.getDefaultFont();
    Font defaultTitleFont = launchProperty.getDefaultTitleFont();

    protected GuiMain(IModuleLoader moduleLoader) {
        super(moduleLoader);
    }


    @Override
    public boolean canImport() {
        return false;
    }

    @Override
    public void importData() {
        EGPSFileChooser dialog = new EGPSFileChooser(getClass());
        dialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
        try {
            int result = dialog.showOpenDialog(UnifiedAccessPoint.getInstanceFrame());
            if (result == JFileChooser.CANCEL_OPTION)
                return;
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = dialog.getSelectedFile();
                //TODO
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public boolean canExport() {
        return true;
    }

    @Override
    public void changeToThisTab() {
        if (explorerPanel == null){
            return;
        }
        explorerPanel.updateLastDirNavigatorPanelIfNeeded();

    }

    @Override
    public void exportData() {
        Optional<File> importFile = importFile(this.getClass());
        if (importFile.isEmpty()) {
            return;
        }

        List<String> lines = Lists.newArrayList();
        int size = bookmarkListModel.size();
        for (int i = 0; i < size; i++) {
            BookmarkElement line = bookmarkListModel.getElementAt(i);
            lines.add(line.getStorageString());
        }
        try {
            FileUtils.writeLines(importFile.get(), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }

        recordFeatureUsed4user(featureNames[3]);
    }
    private final String[] featureNames = {"Create Bookmark", "Delete Bookmark", "Open large text view", "Export records"};
    @Override
    public String[] getFeatureNames() {
        return featureNames;
    }

    /**
     * 用来快速实现一个导入的过程，相当于帮你写了JFileChooser的调用过程。 一些场景下会很好用.
     * 问题在于，需要一个回调的函数，然后把路径保存。
     *
     * @param clz
     * @return
     */
    private Optional<File> importFile(Class<?> clz) {
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle("Quick import file ... ");

        jfc.setDialogType(JFileChooser.OPEN_DIALOG);

        FileFilterTxt fileFilterTxt = new FileFilterTxt();
        jfc.addChoosableFileFilter(fileFilterTxt);

        if (jfc.showSaveDialog(UnifiedAccessPoint.getInstanceFrame()) == JFileChooser.APPROVE_OPTION) {
            File selectedF = jfc.getSelectedFile();
            return Optional.ofNullable(selectedF);
        }
        return Optional.empty();
    }

    @Override
    public boolean closeTab() {
        BookmarkElement selectedBookmarkElement = bookmarkPanel.getSelectedBookmarkElement();
        try {
            storageIO.saveBookmarkList(bookmarkListModel,selectedBookmarkElement);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return super.closeTab();
    }


    @Override
    protected void initializeGraphics() {
        bookmarkPanel = new BookmarkPanel(this);
        BookmarkElement selectedBookmarkElement = bookmarkPanel.getSelectedBookmarkElement();
        explorerPanel = new ExplorerPanel(this, selectedBookmarkElement);

        // Modern macOS Finder-style split pane
        JSplitPane jSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, bookmarkPanel, explorerPanel);
        jSplitPane.setDividerSize(1);  // Thinner divider for modern look
        jSplitPane.setDividerLocation(250);
        jSplitPane.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        jSplitPane.setContinuousLayout(true);  // Smooth resizing
        jSplitPane.setBackground(new Color(245, 245, 247));

        add(jSplitPane, BorderLayout.CENTER);

        fileInfoPanel = new FileInfoPanel(selectedBookmarkElement.getPath(), defaultTitleFont, defaultFont);
        add(fileInfoPanel, BorderLayout.EAST);
    }

}
