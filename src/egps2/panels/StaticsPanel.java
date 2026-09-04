package egps2.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.gallerymod.IndependentModuleLoader;
import egps2.frame.MainFrameProperties;
import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * StaticsPanel is a reusable Swing panel or dialog within eGPS.
 */
public class StaticsPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(StaticsPanel.class);
	private JTable table;
	private JLabel lbl_launchedTimes;
	private JLabel lbl_launchJLabel;
	private JLabel lbl_levelRank;
	
	private boolean isSoftware = false;
	
	private Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
	private Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();
	private JCheckBox chckBox_showLocked;

	/**
	 * Create the panel.
	 */
	public StaticsPanel() {
		
		setBorder(new EmptyBorder(40, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		lbl_launchJLabel = new JLabel("  Module has been launched :        ");
		lbl_launchJLabel.setFont(defaultTitleFont);
		GridBagConstraints gbc_lbl_launchJLabel = new GridBagConstraints();
		gbc_lbl_launchJLabel.anchor = GridBagConstraints.EAST;
		gbc_lbl_launchJLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_launchJLabel.gridx = 0;
		gbc_lbl_launchJLabel.gridy = 0;
		panel.add(lbl_launchJLabel, gbc_lbl_launchJLabel);

		lbl_launchedTimes = new JLabel("");
		lbl_launchedTimes.setFont(defaultFont);
		GridBagConstraints gbc_lbl_launchedTimes = new GridBagConstraints();
		gbc_lbl_launchedTimes.anchor = GridBagConstraints.WEST;
		gbc_lbl_launchedTimes.insets = new Insets(0, 0, 5, 35);
		gbc_lbl_launchedTimes.gridx = 1;
		gbc_lbl_launchedTimes.gridy = 0;
		panel.add(lbl_launchedTimes, gbc_lbl_launchedTimes);
		
		JLabel lblNewLabel_2 = new JLabel("  Ranking :        ");
		lblNewLabel_2.setFont(defaultTitleFont);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 1;
		panel.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		
		lbl_levelRank = new JLabel();
		GridBagConstraints gbc_lbl_levelRank = new GridBagConstraints();
		gbc_lbl_levelRank.anchor = GridBagConstraints.WEST;
		gbc_lbl_levelRank.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_levelRank.gridx = 1;
		gbc_lbl_levelRank.gridy = 1;
		panel.add(lbl_levelRank, gbc_lbl_levelRank);
		
		JLabel lblNewLabel_4 = new JLabel("                 ");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel_4.gridx = 2;
		gbc_lblNewLabel_4.gridy = 1;
		panel.add(lblNewLabel_4, gbc_lblNewLabel_4);

		chckBox_showLocked = new JCheckBox("Only show locked features");
		chckBox_showLocked.setFont(defaultTitleFont);
		chckBox_showLocked.setFocusable(false);
		GridBagConstraints gbc_chckBox_showLocked = new GridBagConstraints();
		gbc_chckBox_showLocked.gridwidth = 2;
		gbc_chckBox_showLocked.insets = new Insets(0, 0, 0, 5);
		gbc_chckBox_showLocked.gridx = 1;
		gbc_chckBox_showLocked.gridy = 2;
		panel.add(chckBox_showLocked, gbc_chckBox_showLocked);
		
		

	}

	public void initializeThePanel(ModuleFace selectedModule) {
		
		int launchTimes = 0;
		LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();

		boolean noModuleSelectedOrIsGalleryModule = true;
		if (selectedModule != null) {
			Optional<IModuleLoader> moduleLoaderOpt = selectedModule.getModuleLoader();
			// 在这种情况下 DIYTools小模块一般是没有ModuleLoader的。
			IModuleLoader moduleLoader = null;
			if (moduleLoaderOpt.isPresent()) {
				moduleLoader = moduleLoaderOpt.get();
			} else {
				if (selectedModule instanceof IModuleLoader) {
					moduleLoader = (IModuleLoader) selectedModule;
				} else {
					log.error("A new situation happens, please tell the developers.");
				}
			}

			// 这里是区分是否是导航模块，导航模块就是把所有模块的启动字数统计一下。
			if (Objects.equals(IndependentModuleLoader.INTRO_MODULE_NAME,
					moduleLoader.getTabName())) {

			}else {
				noModuleSelectedOrIsGalleryModule = false;
				Map<String, Integer> moduleName2times = launchProperty.getModuleName2times();
				// 注意，这里的key不是TabName，而是ClassName
				Integer times = moduleName2times.get(moduleLoader.getClass().getName());
				launchTimes = times == null ? 0 : times;
			}
		}
		
		if (noModuleSelectedOrIsGalleryModule){
			launchTimes = launchProperty.getLaunchTimes();
			// Default is module has been haunched
			lbl_launchJLabel.setText("  Software has been launched :        ");
			isSoftware = true;
		}
		String timeStr = String.valueOf(launchTimes).concat("   times");
		lbl_launchedTimes.setText(timeStr);


		String level = "0";
		if (launchTimes > 1500) {
			level = "7";
		}else if (launchTimes > 800) {
			level = "6";
		}else if (launchTimes > 400) {
			level = "5";
		}else if (launchTimes > 160) {
			level = "4";
		}else if (launchTimes > 80) {
			level = "3";
		}else if (launchTimes > 40) {
			level = "2";
		}else if (launchTimes > 10) {
			level = "1";
		}
		URL resource = getClass().getResource("/images/miscellaneous/ic_user level_"+ level + ".png");
		ImageIcon imageIcon = new ImageIcon(resource);
		lbl_levelRank.setIcon(imageIcon);

		table = getJtable(selectedModule);
		table.setFont(defaultFont);
		JScrollPane scrollable = new JScrollPane(table);
		add(scrollable, BorderLayout.CENTER);

	}

	private JTable getJtable(ModuleFace selectedModule) {
		String[] columnNames = { "Feature", "States", "Times" };

		URL lock = UnifiedAccessPoint.getImageResource("module/24gf-lock2.png");
		URL unlock = UnifiedAccessPoint.getImageResource("module/24gf-unlock4.png");

		ImageIcon lockImageIcon = new ImageIcon(lock);
		ImageIcon unlockImageIcon = new ImageIcon(unlock);
		
		
		Map<String, Integer> hashMap = new HashMap<>();
		if (isSoftware) {
			IModuleLoader[] existedLoaders = MainFrameProperties.getExistedLoaders();
			
			Map<String, Integer> moduleName2times = UnifiedAccessPoint.getLaunchProperty().getModuleName2times();
			
			hashMap = new HashMap<>();
			for (IModuleLoader iModuleLoader : existedLoaders) {
				// 注意，这里的key不是TabName，而是ClassName
				String name = iModuleLoader.getClass().getName();
				String newKeyname = iModuleLoader.getTabName();
				Integer integer = moduleName2times.get(name);
				if (integer == null) {
					integer = Integer.valueOf(0);
				}
				
				hashMap.put(newKeyname, integer);
			}
		}else {
			String[] featureNames = selectedModule.getFeatureNames();
			if (featureNames != null) {
				hashMap = selectedModule.getFeatureUsedCountMap(featureNames);
			}
		}
		
		int size = hashMap.size();
		Object[][] rowData = new Object[size][];
		
		int index = 0;
		for (Entry<String, Integer> entry : hashMap.entrySet()) {
			Integer value = entry.getValue();
			
			Object[] array = null;
			if (value == 0) {
				array =  new Object[] { entry.getKey(), lockImageIcon, value }; 
			}else {
				array =  new Object[] { entry.getKey(), unlockImageIcon, value }; 
			}
			
			rowData[index] = array;
			index ++;
		}
		
		
		//https://stackoverflow.com/questions/6592192/why-does-my-jtable-sort-an-integer-column-incorrectly
		// 排序会出现问题，需要对String 和Integer做出改变
		DefaultTableModel model = new DefaultTableModel(rowData,columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return ImageIcon.class;
                    case 2:
                        return Integer.class;
                    default:
                        return String.class;
                }
            }
        };
		
		table = new JTable(model);
		
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();   
		r.setHorizontalAlignment(JLabel.CENTER);   
		table.setDefaultRenderer(Object.class, r);
		table.setDefaultRenderer(Integer.class, r);
//		table.setDefaultRenderer(Double.class, r);

		table.setRowHeight(50);
		table.getColumnModel().getColumn(1).setCellRenderer(table.getDefaultRenderer(ImageIcon.class));

		table.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		// 设置表数据居中显示

		DefaultTableCellRenderer cr = new DefaultTableCellRenderer();
		cr.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, cr);

		// 设置表头居中显示
//		DefaultTableCellRenderer hr = new DefaultTableCellRenderer();
//		hr.setHorizontalAlignment(JLabel.CENTER);
		JTableHeader tableHeader = table.getTableHeader();
		tableHeader.setFont(defaultTitleFont);
		TableCellRenderer hr = tableHeader.getDefaultRenderer();
		
		
		//设置过滤显示的信息
		
		RowSorter<? extends TableModel> rs = table.getRowSorter();
		if (rs == null) {
            table.setAutoCreateRowSorter(true);
            rs = table.getRowSorter();
        }
		TableRowSorter<? extends TableModel> rowSorter =
                (rs instanceof TableRowSorter) ? (TableRowSorter<? extends TableModel>) rs : null;

        if (rowSorter == null) {
            throw new RuntimeException("Cannot find appropriate rowSorter: " + rs);
        }
        
        
		RowFilter<Object, Object> simbpleFilter = new RowFilter<Object, Object>() {
			public boolean include(Entry<? extends Object, ? extends Object> entry) {
				for (int i = entry.getValueCount() - 1; i >= 0; i--) {
					Object value = entry.getValue(1);
					
					if (lockImageIcon == value) {
						return true;
					}
					
				}
				return false;
			}
		};
        	 
        chckBox_showLocked.addItemListener(e -> {
        	if( e.getStateChange() == ItemEvent.SELECTED) {
        		rowSorter.setRowFilter(simbpleFilter );
        	}else {
        		 rowSorter.setRowFilter(null);
			}
        });
        
		
		return table;
	}

}
