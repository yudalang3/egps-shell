package egps2.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTextField;
import org.jdesktop.swingx.prompt.BuddySupport;

import egps2.UnifiedAccessPoint;
import egps2.frame.MainFrameProperties;
import egps2.frame.MyFrame;
import egps2.modulei.IModuleLoader;

/**
 * ActionSearch 这个按钮点下起作用
 * 是一个全局搜索的按钮
 * 
 * @author yudal
 *
 */
public class ModuleInspector {

	JWindow jWindow;

	private final JFrame jFrame;

	private DefaultTableModel defaultTableModel;
	private Preferences pref = Preferences.userNodeForPackage(getClass());

	private JXTextField filterField;

	private TableRowSorter<DefaultTableModel> sorter;

	private HashMap<String, IModuleLoader> name2moduleLoader;

	@SuppressWarnings("serial")
	public ModuleInspector(MyFrame jFrame) {
		this.jFrame = jFrame;

		// Use getExistedLoaders() to respect user configuration
		// Only show modules that user has configured to load (consistent with iTools menu)
		IModuleLoader[] existedLoaders = MainFrameProperties.getExistedLoaders();

		Vector<String> colonmNames = new Vector<>();
		Vector<Vector<String>> data = new Vector<>();
		colonmNames.add("Module name");
		colonmNames.add("Short description");


		name2moduleLoader = new HashMap<>();
		for (IModuleLoader loader : existedLoaders) {

			Vector<String> row = new Vector<>();

			// Add module name (with plugin badge if applicable)
			String moduleName = loader.getTabName();
			if (isPluginModule(loader)) {
				moduleName = moduleName + " [Plugin]";
			}
			row.add(moduleName);

			String shortDescription = loader.getShortDescription();
			row.add(shortDescription);

			data.add(row);
			name2moduleLoader.put(moduleName, loader);
		}
		defaultTableModel = new DefaultTableModel(data, colonmNames) {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				return String.class;
			}
		};
		initComponents();

	}

	@SuppressWarnings("serial")
	private void initComponents() {
		JPanel jPanel = new JPanel(new BorderLayout(5, 5));
		
		Border emptyBorder = BorderFactory.createEmptyBorder(13,13, 15, 13);
		Border lineBorder = BorderFactory.createLineBorder(Color.decode("#00a0da"));
		CompoundBorder compoundBorder = new CompoundBorder(lineBorder,emptyBorder);
		jPanel.setBorder(compoundBorder);

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
		Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
		JTable jTable = new JTable(defaultTableModel);
		jTable.setFont(defaultFont);
		jTable.getTableHeader().setFont(defaultTitleFont);
		jTable.setShowGrid(true);
		jTable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		MouseAdapter adapter = new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int selectedRow = jTable.getSelectedRow();
				Object valueAt = jTable.getValueAt(selectedRow, 0);

				String moduleName = valueAt.toString();
				int int1 = pref.getInt(moduleName, 0);
				pref.putInt(moduleName, ++int1);
				jWindow.dispose();
				
				IModuleLoader iModuleLoader = name2moduleLoader.get(moduleName);
				if (moduleName == null) {
					throw new IllegalArgumentException("Sorry, this is the interval error, please tell the developers.");
				}else {
					MainFrameProperties.loadTheModuleFromIModuleLoader(iModuleLoader);
				}
				
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Rectangle r = new Rectangle(e.getX() - 50, e.getY() - 50, e.getX(), e.getY());

				jTable.scrollRectToVisible(r);

				int rowAtPoint = jTable.rowAtPoint(e.getPoint());
				jTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
				jTable.repaint();
			}

		};

		jTable.addMouseListener(adapter);
		jTable.addMouseMotionListener(adapter);

		sorter = new TableRowSorter<>(defaultTableModel);
		Comparator<String> firstColCompa = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int a = pref.getInt(o1, 0);
				int b = pref.getInt(o2, 0);

				int ret = b - a;
				if (ret == 0) {
					return o1.compareTo(o2);
				} else {
					return ret;
				}
			}
		};
		List<RowSorter.SortKey> sortKeys = new ArrayList<>();
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		sorter.setComparator(0, firstColCompa);

		jTable.setRowSorter(sorter);

		TableColumn column = jTable.getColumnModel().getColumn(0);
		int sizeOfFirstColumn = 180;
		column.setPreferredWidth(sizeOfFirstColumn);
		column.setMaxWidth(sizeOfFirstColumn);

		// 自定义单元格渲染器以添加四周留白
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				// 调用父类方法以获取默认组件
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				// 设置边距（留白）
				if (c instanceof JLabel) {
					JLabel label = (JLabel) c;
					label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 上、左、下、右的留白
				}

				return c;
			}
		};

		jTable.setRowHeight(25);
		for (int i = 0; i < jTable.getColumnModel().getColumnCount(); i++) {
			jTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
		}

		filterField = new JXTextField("Enter one or more filter strings, separated by space characters", Color.gray);
		filterField.setFont(defaultFont);
		JButton deleteAllButton = new JButton("x");
		deleteAllButton.setBorderPainted(false);
		deleteAllButton.addActionListener(e -> {
			filterField.setText("");
		});
		deleteAllButton.setVisible(false);
		filterField.addBuddy(deleteAllButton, BuddySupport.Position.RIGHT);
		filterField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if (filterField.getText().isEmpty()) {
					deleteAllButton.setVisible(false);
				}
				newFilter();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				deleteAllButton.setVisible(true);
				newFilter();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}
		});

		jPanel.add(filterField, BorderLayout.NORTH);
		JScrollPane comp = new JScrollPane(jTable);
		comp.setBorder(null);
		jPanel.add(comp, BorderLayout.CENTER);

		jWindow = new JWindow(jFrame);
		jWindow.add(jPanel);

		jWindow.addWindowFocusListener(new WindowFocusListener() {

			@Override
			public void windowLostFocus(WindowEvent e) {
				jWindow.dispose();
			}

			@Override
			public void windowGainedFocus(WindowEvent e) {
			}
		});

		InputMap inputMap = filterField.getInputMap(JComponent.WHEN_FOCUSED);

		AbstractAction close = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					pref.flush();
				} catch (BackingStoreException e1) {
					e1.printStackTrace();
				}

				jWindow.dispose();
			}

		};

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), close);

		SwingUtilities.invokeLater(() -> {
			sorter.sort();
		});

	}

	private void newFilter() {
		RowFilter<DefaultTableModel, Object> rf = null;
		// If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter(filterField.getText());
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	public void display() {
		jWindow.setSize(800, 600);
		jWindow.setLocationRelativeTo(jFrame);
		jWindow.setVisible(true);
	}

	/**
	 * Checks if a module is from plugin directory (~/.egps2/config/plugin/)
	 *
	 * @param loader Module loader to check
	 * @return true if the module is from plugin directory, false otherwise
	 */
	private boolean isPluginModule(IModuleLoader loader) {
		try {
			// Get the class location
			java.security.ProtectionDomain protectionDomain = loader.getClass().getProtectionDomain();
			java.security.CodeSource codeSource = protectionDomain.getCodeSource();

			if (codeSource == null) {
				return false;
			}

			java.net.URL location = codeSource.getLocation();
			if (location == null) {
				return false;
			}

			String locationPath = location.getPath();

			// Normalize path separators and check if it's in plugin directory
			// Plugin directory: ~/.egps2/config/plugin/ or %USERPROFILE%\.egps2\config\plugin\
			String normalizedPath = locationPath.replace('\\', '/');
			boolean isFromPluginDir = normalizedPath.contains("/.egps2/config/plugin/") ||
			                          normalizedPath.contains(".egps2/config/plugin/");

			return isFromPluginDir;
		} catch (Exception e) {
			return false;
		}
	}

}
