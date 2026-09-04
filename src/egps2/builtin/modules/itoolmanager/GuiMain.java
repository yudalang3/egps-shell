package egps2.builtin.modules.itoolmanager;

import egps2.EGPSProperties;
import egps2.UnifiedAccessPoint;
import egps2.frame.ComputationalModuleFace;
import egps2.modulei.IModuleLoader;
import egps2.panels.dialog.SwingDialog;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <pre>
 * 请帮我实现一个 JTable，主要的要求是美观，简洁和大方
 *
 * 列的信息如下：
 * Num. 是一个列，表示序号。内容是int
 * Icon是一个列，表示图标。内容是ImageIcon
 * Name是一个列，表示名称。内容是String
 * Cate1是一个列，表示类别。内容是String
 * Cate2是一个列，表示类别。内容是String
 * Cate3是一个列，表示类别。内容是String
 * Cate4是一个列，表示类别。内容是String
 * Tooltip是一个列，鼠标移动到上面是手势，出现tooltip。内容是ImageIcon
 * Choose 是一个列，表示选择。内容是 RadioButton
 *
 * 要求：点击列可以排序
 *
 * </pre>
 */
@SuppressWarnings("serial")
public class GuiMain extends ComputationalModuleFace {

    private static final Logger log = LoggerFactory.getLogger(GuiMain.class);
    private ElegantJTable elegantJTable;

    protected GuiMain(IModuleLoader moduleLoader) {
        super(moduleLoader);
    }


    @Override
    public boolean canImport() {
        return false;
    }

    @Override
    public void importData() {
    }

    @Override
    public boolean canExport() {
        return true;
    }

    @Override
    public void exportData() {
        saveConfiguration(true);
    }

    private boolean saveConfiguration(boolean showSuccessDialog) {
        // Save to the file
        List<String> strings = elegantJTable.exportData();
        List<IModuleElement> allProviders = elegantJTable.getAllProviders();

        List<String> exportData = Lists.newArrayList();

        // Enhanced header with timestamp and format description
        exportData.add("# eGPS2 Module Loading Configuration");
        exportData.add("# Auto-generated on: " + new java.util.Date());
        exportData.add("# Format: <FullClassName>\\t<true|false>");
        exportData.add("# Lines starting with # are comments");
        exportData.add("#");
        exportData.add("# Module.path    whether.load?");
        exportData.add("");

        // Statistics counters
        int availableCount = 0;
        int unavailableCount = 0;
        int newCount = 0;
        int selectionIndex = 0;

        for (IModuleElement provider : allProviders) {
            IModuleLoader loader = provider.getLoader();
            ModuleStatus status = provider.getStatus();

            // Handle unavailable modules (comment them out)
            if (status == ModuleStatus.UNAVAILABLE) {
                unavailableCount++;
                String savedLoadState = Boolean.toString(provider.isLoad());
                exportData.add("# [UNAVAILABLE] " + provider.getClassName() +
                              "\t" + savedLoadState + " # " + provider.getErrorMessage());
                continue;
            }

            // Handle modules with loader
            if (loader != null) {
                if (selectionIndex >= strings.size()) {
                    log.error("Loading selections are fewer than editable providers");
                    break;
                }
                String s = strings.get(selectionIndex++);
                String name = loader.getClass().getName();

                // Mark newly discovered modules
                if (status == ModuleStatus.NEWLY_DISCOVERED) {
                    newCount++;
                    exportData.add("# [NEW] Module discovered");
                    exportData.add(name + "\t" + s);
                } else {
                    availableCount++;
                    exportData.add(name + "\t" + s);
                }
            }
        }

        if (selectionIndex != strings.size()) {
            log.error("Loading selections are more than editable providers");
        }

        // Add statistics summary at the end
        exportData.add("");
        exportData.add("# === Statistics ===");
        exportData.add("# Available: " + availableCount);
        exportData.add("# Newly discovered: " + newCount);
        exportData.add("# Unavailable (commented out): " + unavailableCount);
        exportData.add("# Total tracked: " + allProviders.size());

        // Save to file
        File file = new File(EGPSProperties.EGPS_MODULE_CONFIG_PATH);
        try {
            FileUtils.writeLines(file, exportData);
            ModuleLoadingStateSynchronizer.applySelections(strings, allProviders);
            elegantJTable.markSaved();
            if (showSuccessDialog) {
                String str1 = UnifiedAccessPoint.getResourceString("dialog.info");
                String msg = String.format(
                    "Configuration saved successfully!\n\n" +
                    "Modules saved: %d\n" +
                    "New modules: %d\n" +
                    "Removed unavailable: %d\n\n" +
                    "Please close and launch software to make effects!",
                    availableCount, newCount, unavailableCount
                );
                SwingDialog.showInfoMSGDialog(str1, msg);
            }
            return true;
        } catch (IOException e) {
            log.error("Failed to save module loading configuration", e);
            SwingDialog.showErrorMSGDialog("Export Failed", e.getMessage());
            return false;
        }
    }


    @Override
    public boolean closeTab() {
        if (elegantJTable == null || !elegantJTable.hasUnsavedChanges()) {
            return super.closeTab();
        }

        String title = UnifiedAccessPoint.getResourceString("common.save.confirm.title");
        String message = "The module loading table has unsaved changes.\nSave them before closing?";

        while (true) {
            boolean shouldSave = SwingDialog.showConfirmDialog(title, message);
            if (!shouldSave) {
                return false;
            }
            if (saveConfiguration(false)) {
                return false;
            }
        }
    }

    @Override
    protected void initializeGraphics() {
        elegantJTable = new ElegantJTable();
        add(elegantJTable, BorderLayout.CENTER);
    }

}
