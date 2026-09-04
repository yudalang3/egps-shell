package egps2.builtin.modules.filemanager;

import com.jidesoft.swing.JideSplitPane;
import egps2.UnifiedAccessPoint;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * ExplorerPanel belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class ExplorerPanel extends JPanel {

    private JScrollPane scrollPanelOfSplit;
    private final GuiMain guiMain;
    private JideSplitPane splitPane; // 主分栏容器

    Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
    Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();

    /**
     * The Path is from (the root path) to (the selected target path)
     */
    private List<Path> currentPathSegments;
    private BookmarkElement bookmarkElement;

    public ExplorerPanel(GuiMain guiMain, BookmarkElement bookmarkElement) {
        this.guiMain = guiMain;
        this.bookmarkElement = bookmarkElement;

        setLayout(new BorderLayout());
        // 初始化路径分段
        currentPathSegments = new ArrayList<>();
        updatePathSegmentsList();

        // 初始化 JideSplitPane
        splitPane = new JideSplitPane(JideSplitPane.HORIZONTAL_SPLIT);
        splitPane.setProportionalLayout(false);
        splitPane.setDividerSize(1);  // Thinner divider for modern macOS look
        splitPane.setContinuousLayout(true);  // Smooth resizing
        splitPane.setBackground(new Color(245, 245, 247));

        // 根据路径动态生成面板
        updatePanels();

        scrollPanelOfSplit = new JScrollPane(splitPane);
        scrollPanelOfSplit.setBorder(null);
        scrollPanelOfSplit.getHorizontalScrollBar().setUnitIncrement(16);  // Smooth horizontal scrolling
        add(scrollPanelOfSplit, BorderLayout.CENTER);
    }

    private void updatePathSegmentsList() {
        currentPathSegments.clear();

        Path rootPathGlobal = bookmarkElement.getRootPath();
        // 将路径添加到路径段列表
        Path parent = bookmarkElement.getPath();

        while (true) {
            currentPathSegments.addFirst(parent);
            if (parent.equals(rootPathGlobal)) {
                break;
            } else {
                parent = parent.getParent();
            }
        }

    }

    /**
     * 根据当前路径分段动态更新 JideSplitPane 的面板
     */
    private void updatePanels() {
        splitPane.removeAll(); // 清空所有面板

        int size = currentPathSegments.size();
        for (int i = 0; i <= size - 1; i++) {
            Path path = currentPathSegments.get(i);
            Path childPath = i < size - 1 ? currentPathSegments.get(i + 1) : null;
            DirNavigatorPanelElement dirNavigatorPanelElement = new DirNavigatorPanelElement(path, Optional.ofNullable(childPath));
            JPanel panel = createDirNavigatorPanel(dirNavigatorPanelElement); // 是否为最后一个面板
            splitPane.add(panel);
        }

        turnToRightmostView();
    }

    private JPanel createDirNavigatorPanel(DirNavigatorPanelElement dirNavigatorPanelElement) {
        Path path = dirNavigatorPanelElement.getCurrDirPath();
        JPanel panel = new JPanel(new BorderLayout());

        // Modern macOS Finder-style title
        String title = path.getFileName() != null ? path.getFileName().toString() : path.toString();
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)),
                title
        );
        border.setTitleFont(defaultTitleFont);
        border.setTitleColor(new Color(80, 80, 80));
        panel.setBorder(border);
        panel.setBackground(new Color(255, 255, 255));

        if (!Files.isDirectory(path)) {
            JLabel errorLabel = new JLabel("Already the last file, not Dir.\nThis should not happen, please contact developers", SwingConstants.CENTER);
            errorLabel.setForeground(new Color(150, 150, 150));
            panel.add(errorLabel, BorderLayout.CENTER);
            return panel;
        }

        DirNavigatorPanel dirNavigatorPanel = new DirNavigatorPanel(dirNavigatorPanelElement);
        dirNavigatorPanel.setExplorerPanel(this);
        Objects.requireNonNull(guiMain);
        dirNavigatorPanel.setModuleFace(guiMain);

        JScrollPane scrollPane = new JScrollPane(dirNavigatorPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }


    protected void updateFilePath4bookmarkJListClick(BookmarkElement bookmarkElement) {
        this.bookmarkElement = bookmarkElement;
        updatePathSegmentsList();
        updatePanels();
        scrollPanelOfSplit.revalidate();
    }


    /**
     * DirNavigatorPanel 点击双击事件回调，rootPath是不变的，但是，最后的Path会变
     * @param dirNavigatorPanel
     * @param newSelectedFilePath
     */
    public void reOpenTheDirPath(DirNavigatorPanel dirNavigatorPanel, Path newSelectedFilePath) {
        bookmarkElement.setPath(newSelectedFilePath);
        updatePathSegmentsList();
        updatePanels();
        //scrollPanelOfSplit.revalidate();
        // 更新 ViewPort 并移动到最右边
        turnToRightmostView();
    }

    private void turnToRightmostView() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar horizontalBar = scrollPanelOfSplit.getHorizontalScrollBar();
            int targetValue = horizontalBar.getMaximum();
            int currentValue = horizontalBar.getValue();

            // Smooth scrolling animation
            if (targetValue != currentValue) {
                animateScrollTo(horizontalBar, targetValue);
            }
        });
    }

    /**
     * Smooth scroll animation to target value
     * @param scrollBar The scroll bar to animate
     * @param targetValue The target scroll position
     */
    private void animateScrollTo(JScrollBar scrollBar, int targetValue) {
        int currentValue = scrollBar.getValue();
        int distance = targetValue - currentValue;

        // If distance is small, just set it directly
        if (Math.abs(distance) < 10) {
            scrollBar.setValue(targetValue);
            return;
        }

        // Animation parameters
        final int duration = 300; // milliseconds
        final int steps = 20;
        final int delay = duration / steps;
        final int step = distance / steps;

        Timer timer = new Timer(delay, null);
        final int[] currentStep = {0};

        timer.addActionListener(e -> {
            currentStep[0]++;
            if (currentStep[0] >= steps) {
                scrollBar.setValue(targetValue);
                timer.stop();
            } else {
                // Ease-out effect
                float progress = (float) currentStep[0] / steps;
                float easedProgress = 1 - (float) Math.pow(1 - progress, 3);
                int newValue = currentValue + (int) (distance * easedProgress);
                scrollBar.setValue(newValue);
            }
        });

        timer.start();
    }


    /**
     * only 刷新最后一个面板
     */
    public void updateLastDirNavigatorPanelIfNeeded() {
        int size = currentPathSegments.size();
        if (size == 1){
            updateFilePath4bookmarkJListClick(this.bookmarkElement);
            return;
        }

        int paneCount = splitPane.getPaneCount();
        Component paneAt = splitPane.getPaneAt(paneCount - 1);
        splitPane.removePane(paneAt);
        {
            int i = size - 1;
            Path path = currentPathSegments.get(i);
            Path childPath = i < size - 1 ? currentPathSegments.get(i + 1) : null;
            DirNavigatorPanelElement dirNavigatorPanelElement = new DirNavigatorPanelElement(path, Optional.ofNullable(childPath));
            JPanel panel = createDirNavigatorPanel(dirNavigatorPanelElement); // 是否为最后一个面板
            splitPane.add(panel);
        }

        turnToRightmostView();
        scrollPanelOfSplit.revalidate();
    }
}
