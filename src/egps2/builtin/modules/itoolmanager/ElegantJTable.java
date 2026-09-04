package egps2.builtin.modules.itoolmanager;

import com.google.common.collect.Lists;
import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;
import egps2.frame.MainFrameProperties;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.modulei.ModuleClassification;
import egps2.utils.EGPSIconUtil;
import egps2.utils.common.util.EGPSShellIcons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.string.EGPSStringUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * ElegantJTable belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class ElegantJTable extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(ElegantJTable.class);
    private JTable table;
    private CustomTableModel tableModel;
    // Keep a strongly typed reference to avoid unchecked casts from JTable#getRowSorter().
    private TableRowSorter<CustomTableModel> rowSorter;

    LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
    Font defaultFont = launchProperty.getDefaultFont();
    Font defaultTitleFont = launchProperty.getDefaultTitleFont();

    private int currentAvailableCount = 0;
    JLabel displayJLabel4available = new JLabel();
    final String inforString = "Available / Total =  {} / {}";

    private final int colIndexNumber = 0;
    private final int colIndexIcon = 1;
    private final int colIndexName = 2;
    private final int colIndexTooltip = 3;
    private final int colIndexCatByFunction = 4;
    private final int colIndexCatByApp = 5;
    private final int colIndexCatByComplexity = 6;
    private final int colIndexCatByDep = 7;
    private final int colIndexLoadingStatus = 8;
    private final int colIndexStatus = 9;  // New status column

    List<IModuleElement> allProviders = MainFrameProperties.getAllProviders();
    private LoadingSelectionTracker loadingSelectionTracker;


    public ElegantJTable() {
        initializeUI();
        setupTable();

        populateTestData();
    }

    private void initializeUI() {
        setSize(900, 600);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 8));
        // Create top search panel
        createSearchPanel();
    }

    private void createSearchPanel() {

        JPanel searchPanel = new JPanel();
        BoxLayout boxLayout = new BoxLayout(searchPanel, BoxLayout.X_AXIS);
        searchPanel.setLayout(boxLayout);

        searchPanel.setBackground(Color.WHITE);
        Border matteBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230));
        Border emptyBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5); // 可根据需要调整空白边距
        Border compoundBorder = BorderFactory.createCompoundBorder(matteBorder, emptyBorder);
        searchPanel.setBorder(compoundBorder);

        JLabel searchLabel = new JLabel("Search : ");
        searchLabel.setFont(defaultTitleFont);

        JTextField searchField = new JTextField();
        searchField.setFont(defaultFont);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                performSearch(searchField.getText());
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                performSearch(searchField.getText());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                performSearch(searchField.getText());
            }
        });

        searchPanel.add(Box.createHorizontalStrut(10));
        displayJLabel4available.setFont(defaultTitleFont);

        searchPanel.add(displayJLabel4available);
        searchPanel.add(Box.createHorizontalStrut(20));

        JButton jButton = new JButton("Select all");
        jButton.setFont(defaultFont);
        jButton.addActionListener(e -> {
            int rowCount = tableModel.getRowCount();
            int columnIndex = 8;
            String buttonText = jButton.getText();
            if ("Select all".equals(buttonText)) {
                jButton.setText("Clear all");

                for (int row = 0; row < rowCount; row++) {
                    tableModel.setValueAt(true, row, columnIndex);
                }
                currentAvailableCount = rowCount;
                updateTextOfTableAvailableCount();
            } else {
                jButton.setText("Select all");
                for (int row = 0; row < rowCount; row++) {
                    tableModel.setValueAt(false, row, columnIndex);
                }
                currentAvailableCount = 0;
                updateTextOfTableAvailableCount();
            }
        });

        jButton.setFocusable(false);
        searchPanel.add(jButton);
        searchPanel.add(Box.createHorizontalStrut(10));

        // Add Refresh Scan button
        JButton refreshButton = new JButton("Refresh Scan");
        refreshButton.setFont(defaultFont);
        refreshButton.setToolTipText("Re-scan all available modules using Reflections");
        refreshButton.addActionListener(e -> {
            refreshModuleList();
        });
        refreshButton.setFocusable(false);
        searchPanel.add(refreshButton);
        searchPanel.add(Box.createHorizontalStrut(10));

        searchPanel.add(Box.createHorizontalStrut(500));

        searchPanel.add(searchLabel);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField);


        //上层留白了一下
        JPanel jPanel = new JPanel();
        jPanel.setBorder(compoundBorder);
        add(jPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.SOUTH);
    }

    private void performSearch(String searchText) {
        if (table == null) return;

        TableRowSorter<CustomTableModel> sorter = rowSorter;
        if (sorter == null) return;
        if (searchText.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Search Name column (index 2) and Tooltip column (index 7, by row number)
            sorter.setRowFilter(new RowFilter<CustomTableModel, Object>() {
                @Override
                public boolean include(Entry<? extends CustomTableModel, ? extends Object> entry) {
                    String searchLower = searchText.toLowerCase();

                    // Search Name column
                    String name = entry.getStringValue(2).toLowerCase();
                    if (name.contains(searchLower)) {
                        return true;
                    }

                    // Search Tooltip (get tooltip text by row number)
                    int rowIndex = (Integer) entry.getIdentifier();
                    String tooltipText = ("Detailed information for row " + (rowIndex + 1)).toLowerCase();
                    return tooltipText.contains(searchLower);
                }
            });
        }
    }

    private void setupTable() {
        // 创建自定义表格模型
        tableModel = new CustomTableModel();
        table = new JTable(tableModel);

        // Set table basic properties
        table.setRowHeight(35);
        table.setFont(defaultFont);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setSelectionForeground(Color.BLACK);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
        table.setFillsViewportHeight(true); // Make table fill the entire viewport

        // Set header style
        JTableHeader header = table.getTableHeader();
        header.setFont(defaultTitleFont);
        header.setBackground(new Color(245, 245, 245));
        header.setForeground(Color.BLACK);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        header.setPreferredSize(new Dimension(0, 40));

        // 设置列宽
        setupColumnWidths();

        // 允许拖动调整列宽
        table.getTableHeader().setResizingAllowed(true);
        table.getTableHeader().setReorderingAllowed(false); // 禁止列重排序，但允许调整宽度
        table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS); // 关闭自动调整，允许手动调整

        // Set custom renderers
        setupCustomRenderers();

        // Set custom editors
        setupCustomEditors();

        // Enable auto sorting
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Create scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    List<String> exportData() {
        commitActiveEditor();
        List<String> lines = Lists.newArrayList();

        Vector<Vector> dataVector = tableModel.getDataVector();

        for (Vector<?> vector : dataVector) {
            Object value = vector.get(colIndexLoadingStatus);
            lines.add(value.toString());
        }
        return lines;
    }

    List<IModuleElement> getAllProviders() {
        return allProviders;
    }

    boolean hasUnsavedChanges() {
        commitActiveEditor();
        if (loadingSelectionTracker == null) {
            return false;
        }
        return loadingSelectionTracker.isDirty(captureCurrentLoadingSelections());
    }

    void markSaved() {
        commitActiveEditor();
        if (loadingSelectionTracker == null) {
            loadingSelectionTracker = new LoadingSelectionTracker(captureCurrentLoadingSelections());
            return;
        }
        loadingSelectionTracker.markSaved(captureCurrentLoadingSelections());
    }

    private void setupColumnWidths() {
        TableColumnModel columnModel = table.getColumnModel();

        // Set preferred widths for each column
        columnModel.getColumn(0).setPreferredWidth(30);   // Num
        columnModel.getColumn(1).setPreferredWidth(50);   // Icon
        columnModel.getColumn(2).setPreferredWidth(120);  // Name
        columnModel.getColumn(3).setPreferredWidth(80);   // Cate1
        columnModel.getColumn(4).setPreferredWidth(80);   // Cate2
        columnModel.getColumn(5).setPreferredWidth(80);   // Cate3
        columnModel.getColumn(6).setPreferredWidth(80);   // Cate4
        columnModel.getColumn(7).setPreferredWidth(60);   // Tooltip
        columnModel.getColumn(8).setPreferredWidth(60);   // Choose
        columnModel.getColumn(9).setPreferredWidth(120);  // Status

        // Set minimum widths
        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setMinWidth(40);
        }
    }

    private void setupCustomRenderers() {
        // Tooltip column renderer
        table.getColumnModel().getColumn(colIndexTooltip).setCellRenderer(new TooltipRenderer());
        // Choose column renderer
        table.getColumnModel().getColumn(colIndexLoadingStatus).setCellRenderer(new RadioButtonRenderer());
        // Status column renderer (new)
        table.getColumnModel().getColumn(colIndexStatus).setCellRenderer(new StatusRenderer());

        // Set renderers with left padding for other columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(colIndexNumber).setCellRenderer(centerRenderer); // Num

        // Set renderers with left padding for text columns
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5)); // Left padding 10px, right padding 5px
                return c;
            }
        };

        table.getColumnModel().getColumn(colIndexName).setCellRenderer(leftRenderer); // Name
        table.getColumnModel().getColumn(colIndexCatByDep).setCellRenderer(leftRenderer); // Cate1
        table.getColumnModel().getColumn(colIndexCatByApp).setCellRenderer(leftRenderer); // Cate2
        table.getColumnModel().getColumn(colIndexCatByComplexity).setCellRenderer(leftRenderer); // Cate3
        table.getColumnModel().getColumn(colIndexCatByFunction).setCellRenderer(leftRenderer); // Cate4
    }

    private void setupCustomEditors() {
        // Choose column editor
        table.getColumnModel().getColumn(colIndexLoadingStatus).setCellEditor(new RadioButtonEditor());
    }

    private void populateTestData() {

        int index = 1;
        int availableCount = 0;

        for (IModuleElement provider : allProviders) {
            IModuleElement provider1 = provider;
            IModuleLoader loader = provider1.getLoader();
            if (loader == null) {
                log.info("Provider is {}， error message: {}.", provider1.getClassName(), provider1.getErrorMessage());
                continue;
            }
            String shortDescription = loader.getShortDescription();

            ImageIcon icon = null;
            IconBean iconBean = loader.getIcon();
            if (iconBean == null || !iconBean.hasResource()) {

            } else {
                try {
                    icon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(iconBean.getInputStream(), iconBean.isSVG());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String[] nameStrings1 = ModuleClassification.ByFunctionality.getNameStrings();
            String[] nameStrings2 = ModuleClassification.ByApplication.getNameStrings();
            String[] nameStrings3 = ModuleClassification.ByComplexity.getNameStrings();
            String[] nameStrings4 = ModuleClassification.ByDependency.getNameStrings();

            ImageIcon helpIcon = EGPSShellIcons.getHelpIcon();
            helpIcon.setDescription(shortDescription);
            int[] category = loader.getCategory();

            // Safely get category values with default fallback
            String cat1 = (category != null && category.length > 0) ? nameStrings1[category[0]] : "N/A";
            String cat2 = (category != null && category.length > 1) ? nameStrings2[category[1]] : "N/A";
            String cat3 = (category != null && category.length > 2) ? nameStrings3[category[2]] : "N/A";
            String cat4 = (category != null && category.length > 3) ? nameStrings4[category[3]] : "N/A";

            tableModel.addRow(new Object[]{
                    index,
                    icon,
                    loader.getTabName(),
                    helpIcon,
                    cat1,
                    cat2,
                    cat3,
                    cat4,
                    provider1.isLoad(),
                    provider1.getStatus()  // Add status column
            });
            if (provider1.isLoad()) {
                availableCount++;
            }
            index++;
        }

        currentAvailableCount = availableCount;
        updateTextOfTableAvailableCount();
        markSaved();
    }

    private List<Boolean> captureCurrentLoadingSelections() {
        List<Boolean> selections = new ArrayList<>();
        Vector<Vector> dataVector = tableModel.getDataVector();

        for (Vector<?> vector : dataVector) {
            Object value = vector.get(colIndexLoadingStatus);
            selections.add(Boolean.TRUE.equals(value));
        }

        return selections;
    }

    private void commitActiveEditor() {
        if (table == null || !table.isEditing()) {
            return;
        }

        TableCellEditor cellEditor = table.getCellEditor();
        if (cellEditor != null) {
            cellEditor.stopCellEditing();
        }
    }

    private void updateTextOfTableAvailableCount() {
        int rowCount = tableModel.getRowCount();
        String format = EGPSStringUtil.format(inforString, currentAvailableCount, rowCount);

        if (SwingUtilities.isEventDispatchThread()) {
            displayJLabel4available.setText(format);
        } else {
            SwingUtilities.invokeLater(() -> displayJLabel4available.setText(format));
        }

    }

    /**
     * Refresh module list by re-scanning all available modules
     * This method clears the cache and triggers module discovery again
     *
     * IMPORTANT: This method properly handles EDT threading:
     * - Time-consuming operations (module scanning, cache reset) run in background thread
     * - GUI updates (table updates, dialogs) are dispatched to EDT thread
     */
    private void refreshModuleList() {
        // Disable refresh button to prevent multiple clicks
        Component[] components = getComponents();
        JButton refreshButton = null;
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                for (Component subComp : ((JPanel) comp).getComponents()) {
                    if (subComp instanceof JButton && ((JButton) subComp).getText().equals("Refresh Scan")) {
                        refreshButton = (JButton) subComp;
                        break;
                    }
                }
            }
        }
        final JButton finalRefreshButton = refreshButton;
        if (finalRefreshButton != null) {
            finalRefreshButton.setEnabled(false);
            finalRefreshButton.setText("Scanning...");
        }

        // Run time-consuming operations in background thread
        new Thread(() -> {
            try {
                // TIME-CONSUMING OPERATIONS - NOT ON EDT
                MainFrameProperties.resetModuleCache();
                MainFrameProperties.getExistedLoaders();
                List<IModuleElement> updatedProviders = MainFrameProperties.getAllProviders();

                // Calculate statistics in background thread
                long available = updatedProviders.stream()
                    .filter(e -> e.getStatus() == ModuleStatus.AVAILABLE ||
                                e.getStatus() == ModuleStatus.AVAILABLE_NOT_LOADED)
                    .count();
                long newlyDiscovered = updatedProviders.stream()
                    .filter(e -> e.getStatus() == ModuleStatus.NEWLY_DISCOVERED)
                    .count();
                long unavailable = updatedProviders.stream()
                    .filter(e -> e.getStatus() == ModuleStatus.UNAVAILABLE)
                    .count();

                String message = String.format(
                    "Refresh Complete!\n\n" +
                    "Total modules: %d\n" +
                    "Available: %d\n" +
                    "Newly discovered: %d\n" +
                    "Unavailable: %d",
                    updatedProviders.size(), available, newlyDiscovered, unavailable
                );

                // GUI UPDATES - DISPATCH TO EDT
                SwingUtilities.invokeLater(() -> {
                    try {
                        // Clear and update table
                        tableModel.setRowCount(0);
                        allProviders = updatedProviders;
                        populateTestData();

                        // Show result dialog
                        javax.swing.JOptionPane.showMessageDialog(
                            ElegantJTable.this,
                            message,
                            "Module Discovery Statistics",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE
                        );
                    } finally {
                        // Re-enable refresh button
                        if (finalRefreshButton != null) {
                            finalRefreshButton.setEnabled(true);
                            finalRefreshButton.setText("Refresh Scan");
                        }
                    }
                });

            } catch (Exception ex) {
                log.error("Error during module refresh", ex);
                // Show error dialog on EDT
                SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(
                        ElegantJTable.this,
                        "Error during module refresh: " + ex.getMessage(),
                        "Refresh Error",
                        javax.swing.JOptionPane.ERROR_MESSAGE
                    );
                    if (finalRefreshButton != null) {
                        finalRefreshButton.setEnabled(true);
                        finalRefreshButton.setText("Refresh Scan");
                    }
                });
            }
        }, "ModuleRefreshThread").start();
    }


    // Custom table model
    /**
     * CustomTableModel belongs to a built-in eGPS module (loader, panel, or helper).
     */
    private class CustomTableModel extends DefaultTableModel {

        String nameStrings1 = ModuleClassification.ByFunctionality.getCategory();
        String nameStrings2 = ModuleClassification.ByApplication.getCategory();
        String nameStrings3 = ModuleClassification.ByComplexity.getCategory();
        String nameStrings4 = ModuleClassification.ByDependency.getCategory();

        private final String[] columnNames = {
                "Number", "Icon", "Name", "Tooltip", nameStrings1, nameStrings2, nameStrings3, nameStrings4, "Loading status", "Status"
        };

        private final Class<?>[] columnTypes = {
                Integer.class, ImageIcon.class, String.class, ImageIcon.class, String.class,
                String.class, String.class, String.class, Boolean.class, ModuleStatus.class
        };

        public CustomTableModel() {
            super();
            setColumnIdentifiers(columnNames);
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            // Only Choose column is editable
            return column == colIndexLoadingStatus;
        }
    }


    // Tooltip column renderer
    /**
     * TooltipRenderer belongs to a built-in eGPS module (loader, panel, or helper).
     */
    private class TooltipRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, "", isSelected, hasFocus, row, column);


            if (value instanceof ImageIcon imageIcon) {
                label.setIcon(imageIcon);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setCursor(new Cursor(Cursor.HAND_CURSOR));
                String description = imageIcon.getDescription();
                label.setToolTipText(description);
            }

            return label;
        }
    }

    // RadioButton column renderer
    /**
     * RadioButtonRenderer belongs to a built-in eGPS module (loader, panel, or helper).
     */
    private class RadioButtonRenderer extends JRadioButton implements TableCellRenderer {
        public RadioButtonRenderer() {
            setHorizontalAlignment(JRadioButton.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(table.getForeground());
                setBackground(table.getBackground());
            }

            setSelected(value != null && (Boolean) value);

            return this;
        }
    }

    // RadioButton column editor
    /**
     * RadioButtonEditor belongs to a built-in eGPS module (loader, panel, or helper).
     */
    private class RadioButtonEditor extends DefaultCellEditor {
        private JRadioButton radioButton;

        public RadioButtonEditor() {
            super(new JCheckBox());
            radioButton = new JRadioButton();
            radioButton.setHorizontalAlignment(JRadioButton.CENTER);
            radioButton.setOpaque(true);

            radioButton.addActionListener(e -> {
                if (radioButton.isSelected()) {
                    currentAvailableCount++;
                } else {
                    currentAvailableCount--;
                }
                updateTextOfTableAvailableCount();
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            radioButton.setSelected(value != null && (Boolean) value);
            return radioButton;
        }

        @Override
        public Object getCellEditorValue() {
            return radioButton.isSelected();
        }
    }

    /**
     * Status column renderer with color coding
     * Renders ModuleStatus enum with different colors based on status
     */
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

            if (value instanceof ModuleStatus) {
                ModuleStatus status = (ModuleStatus) value;
                label.setText(status.getDisplayName());

                // Set different colors based on status
                if (!isSelected) {
                    switch (status) {
                        case AVAILABLE:
                            label.setForeground(new Color(34, 139, 34)); // Forest Green
                            label.setFont(defaultFont);
                            break;
                        case AVAILABLE_NOT_LOADED:
                            label.setForeground(new Color(70, 130, 180)); // Steel Blue
                            label.setFont(defaultFont);
                            break;
                        case NEWLY_DISCOVERED:
                            label.setForeground(new Color(255, 140, 0)); // Dark Orange
                            label.setFont(defaultFont.deriveFont(Font.BOLD));
                            break;
                        case UNAVAILABLE:
                            label.setForeground(new Color(220, 20, 60)); // Crimson
                            label.setFont(defaultFont.deriveFont(Font.ITALIC));
                            break;
                        case DEPRECATED:
                            label.setForeground(Color.GRAY);
                            label.setFont(defaultFont.deriveFont(Font.ITALIC));
                            break;
                    }
                }

                label.setToolTipText(status.getDescription());
                label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
            }

            return label;
        }
    }

}
