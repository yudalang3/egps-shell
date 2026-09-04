package egps2.frame;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.*;

import com.jidesoft.swing.JideTabbedPane;

import egps2.panels.dialog.SwingDialog;
import egps2.UnifiedAccessPoint;
import egps2.modulei.IModuleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 选项卡面板鼠标适配器，为模块选项卡提供右键菜单功能。
 * Tabbed pane mouse adapter providing right-click menu functionality for module tabs.
 *
 * <p>此适配器监听选项卡面板上的鼠标右键事件，弹出上下文菜单以提供批量关闭选项卡的功能。
 * 菜单项根据点击的选项卡位置和总选项卡数量动态调整。
 * This adapter listens for right-click mouse events on the tabbed pane, displaying context menus to provide
 * batch tab closing functionality. Menu items are dynamically adjusted based on the clicked tab position and total tab count.
 *
 * <p><strong>右键菜单项：</strong>
 * Right-click menu items:
 * <ul>
 *   <li><b>Close</b> - 关闭当前选项卡 (Close current tab) - 不能关闭第0个选项卡</li>
 *   <li><b>Close Tabs to the Left</b> - 关闭左侧所有选项卡 (except tab 0)</li>
 *   <li><b>Close Tabs to the Right</b> - 关闭右侧所有选项卡</li>
 *   <li><b>Close Others</b> - 关闭其他所有选项卡 (except current and tab 0)</li>
 *   <li><b>Close All</b> - 关闭所有选项卡 (except tab 0)</li>
 * </ul>
 *
 * <p><strong>特殊保护：</strong>
 * Special protection:
 * <br>第0个选项卡（通常是模块画廊或欢迎页）永远不会被关闭，所有关闭操作都会跳过索引0。
 * Tab 0 (usually Module Gallery or welcome page) is never closed, all close operations skip index 0.
 *
 * <p><strong>菜单动态逻辑：</strong>
 * Dynamic menu logic:
 * <ul>
 *   <li>点击选项卡0：仅显示 "Close Others", "Close Right", "Close All"</li>
 *   <li>点击其他选项卡：根据位置显示相应的关闭选项</li>
 *   <li>只有2个选项卡时：简化菜单项</li>
 *   <li>Right-click on tab 0: Only show "Close Others", "Close Right", "Close All"</li>
 *   <li>Right-click on other tabs: Show close options based on position</li>
 *   <li>When only 2 tabs exist: Simplified menu items</li>
 * </ul>
 *
 * <p><strong>关闭机制：</strong>
 * Closing mechanism:
 * <br>实际的选项卡关闭通过 {@link MyFrame#closeATabInTabbedPanel(ModuleFace)} 执行，
 * 确保正确的清理和生命周期管理。
 * Actual tab closing is performed via {@link MyFrame#closeATabInTabbedPanel(ModuleFace)},
 * ensuring proper cleanup and lifecycle management.
 *
 * @see JideTabbedPane
 * @see MyFrame
 * @see ModuleFace
 * @author mhl (original)
 * @author eGPS Dev Team
 * @since 2.0
 */
public class TabbedPaneMouseAdapter extends MouseAdapter {
    private final JideTabbedPane tabbedPane;
    private static final Logger log = LoggerFactory.getLogger(TabbedPaneMouseAdapter.class);

    public TabbedPaneMouseAdapter(JideTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            showPopupMenu(e);
        }
    }

    /**
     *
     * @Title: showPopupMenu
     * @author mhl
     * @Date Created on: 2018-07-14 15:25
     */
    private void showPopupMenu(final MouseEvent event) {
        final int index = tabbedPane.getUI().tabForCoordinate(tabbedPane, event.getX(), event.getY());
        final int count = tabbedPane.getTabCount();
        if (index < 0) {
            return;
        }

        JPopupMenu pop = new JPopupMenu();

        Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
        JMenuItem closeCurrent = new JMenuItem("Close");
        closeCurrent.setFont(defaultFont);
        JMenuItem closeLeft = new JMenuItem("Close Tabs to the Left");
        closeLeft.setFont(defaultFont);
        JMenuItem closeRight = new JMenuItem("Close Tabs to the Right");
        closeRight.setFont(defaultFont);
        JMenuItem closeOthers = new JMenuItem("Close Others");
        closeOthers.setFont(defaultFont);
        JMenuItem closeAll = new JMenuItem("Close All");
        closeAll.setFont(defaultFont);

        // 新增 Import 按钮
        JMenuItem importData = new JMenuItem("Import");
        importData.setFont(defaultFont);
        importData.setToolTipText("Import data if the module support, like features in toolbar");
        importData.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                Object source = e.getSource();
                if (source instanceof IModuleLoader loader) {
                    ModuleFace face = loader.getFace();
                    if (face.canImport()) {
                        face.importData();
                    } else {
                        SwingDialog.showWarningMSGDialog("Warning", "The module does not support import");
                    }
                } else {
                    log.error("Please contact the developers for the import error");
                }
            }
        });
        //pop.add(importData);

        // 新增 Export 按钮
        JMenuItem exportData = new JMenuItem("Export");
        exportData.setFont(defaultFont);
        exportData.setToolTipText("Export data if the module support, like features in toolbar");
        exportData.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                Object source = e.getSource();
                if (source instanceof IModuleLoader loader) {
                    ModuleFace face = loader.getFace();
                    if (face.canExport()) {
                        face.exportData();
                    } else {
                        SwingDialog.showWarningMSGDialog("Warning", "The module does not support export");
                    }
                } else {
                    log.error("Please contact the developers for the export error");
                }
            }
        });
        //pop.add(exportData);

        if (index == 0 && count > 1) {
            closeOthers.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    for (int j = (count - 1); j > index; j--) {
                        closeTabAction(j);
                    }
                    for (int j = (index - 1); j >= 0; j--) {
                        if (j == 0) {
                            continue;
                        }
                        closeTabAction(j);
                    }
                }
            });
            pop.add(closeOthers);

            closeRight.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    for (int j = (count - 1); j > index; j--) {
                        closeTabAction(j);
                    }

                }
            });
            pop.add(closeRight);

            pop.addSeparator();
            closeAll.addMouseListener(new MouseAdapter() {
                public void mouseReleased(MouseEvent e) {
                    for (int j = (count - 1); j > 0; j--) {
                        closeTabAction(j);
                    }
                }
            });
            pop.add(closeAll);

        } else if (index != 0) {
            closeCurrent.addMouseListener(new MouseAdapter() {

                public void mouseReleased(MouseEvent e) {
                    if (index == 0) {
                        return;
                    }
                    closeTabAction(index);
                }
            });
            pop.add(closeCurrent);
            if (count != 2) {
                if (index != 1) {
                    closeLeft.addMouseListener(new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            for (int j = (index - 1); j >= 0; j--) {
                                if (j == 0) {
                                    continue;
                                }
                                closeTabAction(j);
                            }
                        }
                    });
                    pop.add(closeLeft);
                }
                if (index != count - 1) {
                    closeRight.addMouseListener(new MouseAdapter() {
                        public void mouseReleased(MouseEvent e) {
                            for (int j = (count - 1); j > index; j--) {
                                closeTabAction(j);
                            }

                        }
                    });
                    pop.add(closeRight);
                }
                closeOthers.addMouseListener(new MouseAdapter() {

                    public void mouseReleased(MouseEvent e) {
                        for (int j = (count - 1); j > index; j--) {
                            closeTabAction(j);
                        }
                        for (int j = (index - 1); j >= 0; j--) {
                            if (j == 0) {
                                continue;
                            }
                            closeTabAction(j);
                        }
                    }
                });
                pop.add(closeOthers);

                pop.addSeparator();
                closeAll.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        for (int j = (count - 1); j > 0; j--) {
                            closeTabAction(j);
                        }
                    }
                });
                pop.add(closeAll);
            }

        }
        pop.show(event.getComponent(), event.getX(), event.getY());
    }

    private void closeTabAction(int index) {
//		tabbedPane.removeTabAt(index);
        Component tabComponentAt = tabbedPane.getComponentAt(index);
        Objects.requireNonNull(tabComponentAt);
//		System.out.println(tabComponentAt);
        ModuleFace face = (ModuleFace) tabComponentAt;

        MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
        instanceFrame.closeATabInTabbedPanel(face);
    }

}
