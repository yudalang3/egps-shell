package egps2.frame;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.border.CompoundBorder;

import egps2.builtin.modules.filemanager.IndependentModuleLoader;
import egps2.builtin.modules.itoolmanager.IModuleElement;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.Strings;

import egps2.panels.dialog.SwingDialog;
import egps2.EGPSProperties;
import egps2.utils.EGPSIconUtil;
import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;
import egps2.frame.features.EGPS2ServiceLoader;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.modulei.ModuleClassification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 主框架属性和工具方法类，为 {@link MyFrame} 提供模块加载、菜单构建等辅助功能。
 * Main frame properties and utility method class providing auxiliary functionality such as module loading and menu building for {@link MyFrame}.
 *
 * <p>此类的存在意义是将某些具体的方法细节从 {@link MyFrame} 中分离出来，从而减少核心类的体积和复杂度。
 * 它作为MyFrame的辅助类，封装了模块加载、菜单项创建、资源管理等通用功能。
 * This class exists to separate certain method details from {@link MyFrame}, thereby reducing the volume and complexity of the core class.
 * As an auxiliary class for MyFrame, it encapsulates general functionality such as module loading, menu item creation, and resource management.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>模块发现和加载 - Module discovery and loading</li>
 *   <li>动态构建模块菜单 - Dynamically build module menus</li>
 *   <li>按功能分类组织模块 - Organize modules by functionality classification</li>
 *   <li>创建内置核心模块菜单项 - Create built-in core module menu items</li>
 *   <li>管理开发团队信息 - Manage development team information</li>
 *   <li>提供通用UI工具方法 - Provide common UI utility methods</li>
 * </ul>
 *
 * <p><strong>模块加载机制：</strong>
 * Module loading mechanism:
 * <ol>
 *   <li>通过 {@link egps2.frame.features.EGPS2ServiceLoader} 合并模块配置文件与自动发现结果</li>
 *   <li>基于 {@link ModuleClassification} 对模块进行多维度分类</li>
 *   <li>按分类构建层级菜单结构（一级分类 → 二级模块列表）</li>
 *   <li>为每个模块创建带图标的菜单项</li>
 *   <li>When loading modules via {@link egps2.frame.features.EGPS2ServiceLoader}, merge configured modules with automatically discovered modules</li>
 *   <li>Classify modules in multiple dimensions based on {@link ModuleClassification}</li>
 *   <li>Build hierarchical menu structure by classification (Level 1 category → Level 2 module list)</li>
 *   <li>Create menu items with icons for each module</li>
 * </ol>
 *
 * <p><strong>内置核心模块（固定顺序）：</strong>
 * Built-in core modules (fixed order):
 * <ol>
 *   <li><b>The Resource Manager</b> - 文件管理器 (Ctrl+1)</li>
 *   <li><b>Module gallery</b> - 模块画廊 (Ctrl+2)</li>
 *   <li><b>Low volume text editor</b> - 轻量级文本编辑器 (Ctrl+3)</li>
 *   <li><b>Large volume text view</b> - 大文件文本查看器 (Ctrl+4)</li>
 *   <li><b>Handy tool for biologist</b> - 生物学家工具集 (Ctrl+5)</li>
 *   <li><b>VOICE demo: Dockable</b> - VOICE可停靠演示 (Ctrl+6)</li>
 *   <li><b>VOICE demo: Floating</b> - VOICE浮动演示 (Ctrl+7)</li>
 *   <li><b>ITools Manager</b> - 工具管理器 (Ctrl+8)</li>
 * </ol>
 *
 * <p><strong>工具方法：</strong>
 * Utility methods:
 * <ul>
 *   <li>{@link #wrapStringArraysAsString(String[])} - 拼接字符串数组</li>
 *   <li>{@link #autoWrapComponentWithScollPanel(JComponent)} - 自动包装滚动面板</li>
 *   <li>{@link #loadTheModuleFromIModuleLoader(IModuleLoader)} - 加载模块实例</li>
 *   <li>{@link #getDevTeam()} - 获取默认开发团队信息</li>
 * </ul>
 *
 * @see MyFrame
 * @see IModuleLoader
 * @see ModuleClassification
 * @see egps2.frame.features.EGPS2ServiceLoader
 * @author eGPS Dev Team
 * @since 2.0
 */
public class MainFrameProperties {

    private static final Logger log = LoggerFactory.getLogger(MainFrameProperties.class);
    private static StringBuilder stringBuilder = new StringBuilder();
    private static IModuleLoader[] existingIModuleLoaders;
    private static Triple<String, String, String> defaultDevTeam;
    private static List<IModuleElement> allProviders;

    public static IModuleLoader[] getExistedLoaders() {

        if (existingIModuleLoaders == null) {
            EGPS2ServiceLoader<IModuleLoader> loader = new EGPS2ServiceLoader<>(IModuleLoader.class);

            // Use new discovery system (v2.1+) to automatically find all modules
            egps2.frame.features.ModuleDiscoveryService discoveryService =
                new egps2.frame.features.ModuleDiscoveryService();
            List<IModuleLoader> ret = loader.loadWithDiscovery(
                EGPSProperties.EGPS_MODULE_CONFIG_PATH, discoveryService);
            existingIModuleLoaders = ret.toArray(new IModuleLoader[0]);

            allProviders = loader.getAllProviders();

            log.info("Module loading complete. Loaded {} modules, tracking {} total providers",
                ret.size(), allProviders.size());
        }
        return existingIModuleLoaders;
    }

    public static List<IModuleElement> getAllProviders() {
        return allProviders;
    }

    /**
     * Reset module loader cache to force re-scanning
     * Useful when refreshing module list in ITools Manager
     */
    public static void resetModuleCache() {
        existingIModuleLoaders = null;
        allProviders = null;
        log.info("Module cache has been reset");
    }

    static void loadIndependentTools(IModuleLoader[] loaders, JMenu jmenu, MyFrame myFrame) {

        ModuleClassification moduleClassification = ModuleClassification.ByFunctionality;
        int ordinal = moduleClassification.ordinal();
        int sizeOfCategory = moduleClassification.getSize();
        Map<Integer, List<IModuleLoader>> ret = new HashMap<>();

        for (int i = 0; i < sizeOfCategory; i++) {
            ret.put(i, new ArrayList<>());
        }

        for (IModuleLoader iModuleLoader : getExistedLoaders()) {
            int[] category = iModuleLoader.getCategory();
            if (category == null) {
                System.err.println(iModuleLoader.toString() + "\t do not have category information.");
            }

            if (ordinal >= category.length) {
                continue;
            }
            int typeIndex = category[ordinal];
            List<IModuleLoader> list = ret.get(typeIndex);

            list.add(iModuleLoader);
        }

        String[] nameStrings = moduleClassification.getNameStrings();
        Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getMenuSecondLevelFont();
        for (Entry<Integer, List<IModuleLoader>> entry : ret.entrySet()) {
            Integer i = entry.getKey();
            String string = nameStrings[i];
            JMenu subJmenu = new JMenu(string);
            subJmenu.setFont(defaultFont);
            List<IModuleLoader> value = entry.getValue();
            for (IModuleLoader loader : value) {
                JMenuItem jMenuItem = createJMenuItem(loader, myFrame);
                jMenuItem.setFont(defaultFont);
                subJmenu.add(jMenuItem);
            }
            jmenu.add(subJmenu);

        }

    }

    static void loadInternalCoreModules(IModuleLoader[] loaders, JMenu jmenu, MyFrame myFrame) throws IOException {
        Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getMenuSecondLevelFont();
        Color bg = new Color(240, 240, 240);
//        CompoundBorder compoundBorder = BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
//                BorderFactory.createEmptyBorder(5, 10, 5, 10)
//        );
        CompoundBorder compoundBorder = null;

        {
            IndependentModuleLoader iModuleLoader = new IndependentModuleLoader();

            InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/The Resource Manager.svg");
            ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
            JMenuItem jMenuItem = createJMenuItem(iModuleLoader, myFrame);
            jMenuItem.setIcon(imageIcon);
            jMenuItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            jMenuItem.setFont(defaultFont);
            jMenuItem.setBackground(bg); // 添加背景色
            jMenuItem.setOpaque(true);
            jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
            jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_DOWN_MASK));
            jmenu.add(jMenuItem);
        }
        {
            JMenuItem jMenuItem = new JMenuItem("Module gallery");
            InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/Module gallery.svg");
            ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
            jMenuItem.setIcon(imageIcon);
            jMenuItem.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            jMenuItem.setFont(defaultFont);
            jMenuItem.setOpaque(true);
            jMenuItem.setBackground(bg); // 添加背景色
            jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
            jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_DOWN_MASK));
            jmenu.add(jMenuItem);
            jMenuItem.addActionListener(e -> {
                myFrame.launchIntroductionPanel();
            });
        }
        jmenu.addSeparator();
        {
            egps2.builtin.modules.lowtextedi.IndependentModuleLoader iModuleLoader = new egps2.builtin.modules.lowtextedi.IndependentModuleLoader();
            JMenuItem jMenuItem = createJMenuItem(iModuleLoader, myFrame);
            jMenuItem.setFont(defaultFont);
            InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/Low volume text editor.svg");
            ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
            jMenuItem.setIcon(imageIcon);
            jMenuItem.setBackground(bg); // 添加背景色
            jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
            jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_DOWN_MASK));
            jmenu.add(jMenuItem);
        }
        {

            egps2.builtin.modules.largetextedi.IndependentModuleLoader iModuleLoader = new egps2.builtin.modules.largetextedi.IndependentModuleLoader();
            JMenuItem jMenuItem = createJMenuItem(iModuleLoader, myFrame);
            jMenuItem.setFont(defaultFont);
            InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/Large volume text view.svg");
            ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
            jMenuItem.setIcon(imageIcon);
            jMenuItem.setBackground(bg); // 添加背景色
            jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
            jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, InputEvent.CTRL_DOWN_MASK));
            jmenu.add(jMenuItem);
        }

        jmenu.addSeparator();
        {
            final String clz = "demo.handytools.HandyToolExampleMain";
            IModuleLoader handyToolExampleMain;
            try {
                Object o = Class.forName(clz).getConstructor().newInstance();
                handyToolExampleMain = (IModuleLoader) o;

                JMenuItem jMenuItem = createJMenuItem(handyToolExampleMain, myFrame);
                jMenuItem.setFont(defaultFont);
                InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/Handy tool for biologist.svg");
                ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
                jMenuItem.setIcon(imageIcon);
                jMenuItem.setBackground(bg); // 添加背景色
                jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
                jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, InputEvent.CTRL_DOWN_MASK));
                jmenu.add(jMenuItem);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }

        {
            final String clz = "demo.dockable.IndependentModuleLoader";
            IModuleLoader independentModuleLoader;
            try {
                Object o = Class.forName(clz).getConstructor().newInstance();
                independentModuleLoader = (IModuleLoader) o;

                JMenuItem jMenuItem = createJMenuItem(independentModuleLoader, myFrame);
                InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/VOICE demo：Dockable.svg");
                ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
                jMenuItem.setIcon(imageIcon);

                jMenuItem.setFont(defaultFont);
                jMenuItem.setBackground(bg); // 添加背景色
                jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
                jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, InputEvent.CTRL_DOWN_MASK));
                jmenu.add(jMenuItem);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        {
            final String clz = "demo.floating.IndependentModuleLoader";
            IModuleLoader independentModuleLoader;
            try {
                Object o = Class.forName(clz).getConstructor().newInstance();
                independentModuleLoader = (IModuleLoader) o;

                JMenuItem jMenuItem = createJMenuItem(independentModuleLoader, myFrame);
                InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/VOICE demo：floating.svg");
                ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
                jMenuItem.setIcon(imageIcon);

                jMenuItem.setFont(defaultFont);
                jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, InputEvent.CTRL_DOWN_MASK));
                jMenuItem.setBackground(bg); // 添加背景色
                jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
                jmenu.add(jMenuItem);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

        }
        jmenu.addSeparator();
        {
            egps2.builtin.modules.itoolmanager.IndependentModuleLoader independentModuleLoader = new egps2.builtin.modules.itoolmanager.IndependentModuleLoader();
            JMenuItem jMenuItem = createJMenuItem(independentModuleLoader, myFrame);
            InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/maincore/ITools Manager.svg");
            ImageIcon imageIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
            jMenuItem.setIcon(imageIcon);

            jMenuItem.setFont(defaultFont);
            jMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, InputEvent.CTRL_DOWN_MASK));
            jMenuItem.setBackground(bg); // 添加背景色
            jMenuItem.setBorder(compoundBorder); // 添加边框和内边距
            jmenu.add(jMenuItem);
        }

    }

    private static JMenuItem createJMenuItem(IModuleLoader next, MyFrame myFrame) {
        JMenuItem jMenuItem = new JMenuItem();

        String name = next.getTabName();
        jMenuItem.setText(name);
        jMenuItem.setToolTipText(next.getShortDescription());

        IconBean iconBean = next.getIcon();
        if (iconBean != null && iconBean.hasResource()) {
            Icon icon = null;

            try {
                icon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(iconBean.getInputStream(), iconBean.isSVG());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (icon != null) {
                jMenuItem.setIcon(icon);
            }
        }

        jMenuItem.addActionListener(e -> {
            loadTheModuleFromIModuleLoader(next);
        });

        return jMenuItem;
    }

    /**
     * 这是载入一个模块的快捷方法，需要放入一个IModuleLoader实例
     *
     * @param next
     * @return the moduleFace
     */
    public static ModuleFace loadTheModuleFromIModuleLoader(IModuleLoader next) {
        ModuleFace face = UnifiedAccessPoint.loadTheModuleFromIModuleLoader(next);
        return face;
    }

    public static String wrapStringArraysAsString(String[] strs) {
        stringBuilder.setLength(0);

        for (String string : strs) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }


    public static Triple<String, String, String> getDevTeam() {
        if (defaultDevTeam == null) {

            String team = "EvolGene";
            String developers = "Dalang Yu, Xiao Yang, Bixia Tang, Yi-Hsuan Pan, Jianing Yang, Guangya Duan, Junwei Zhu, Zi-Qian Hao,<br> Hailong Mu, Long Dai, Wangjie Hu, Mochen Zhang, Ying Cui, Tong Jin, Cui-Ping Li, Lina Ma,<br> Language translation team, Xiao Su, Guoqing Zhang, Wenming Zhao, Haipeng Li";
            String webSite = EGPSProperties.EVOLGEN_LAB_WEBSITE;

            defaultDevTeam = Triple.of(team, developers, webSite);
        }
        return defaultDevTeam;
    }

    /**
     * 给一个组件配置一个 ScollPanel，如果这个组件是 null ，就返回一个空的JComponent
     *
     * @param comp
     * @return
     */
    public static JComponent autoWrapComponentWithScollPanel(JComponent comp) {
        JComponent ret = null;
        if (comp == null) {
            ret = new JLabel("The developers has not implement this.");
        } else {
            if (comp instanceof JScrollPane) {
                ret = comp;
            } else {
                JScrollPane jscrollPanel = new JScrollPane(comp);
                jscrollPanel.getViewport().setOpaque(false);
                jscrollPanel.setBorder(null);
                ret = jscrollPanel;
            }
        }

        return ret;
    }

    public static void configAdditionalITools(JToolBar jtoolBar) {

        JButton lastOpenModule = new JButton();
        lastOpenModule.setToolTipText("Last launched module (Ctrl+R)");

        ImageIcon lastOpenModuleIcon = null;
        try {
            InputStream resource = MainFrameProperties.class.getResourceAsStream("/images/toolbar/recover.svg");
            lastOpenModuleIcon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        lastOpenModule.setIcon(lastOpenModuleIcon);
        lastOpenModule.addActionListener(e -> {
            launchLastOpenedModule();
        });

        lastOpenModule.setFocusable(false);
        jtoolBar.add(lastOpenModule);
    }

    /**
     * 启动最后打开的模块，供按钮和快捷键共同调用。
     * Launch the last opened module, called by both button and keyboard shortcut.
     */
    public static void launchLastOpenedModule() {
        LaunchProperty launchProperty = UnifiedAccessPoint.getLaunchProperty();
        String className = launchProperty.getLastLaunchedModuleClz();
        if (Strings.isEmpty(className)) {
            SwingDialog.showInfoMSGDialog("No previous module",
                    "No module launched record here, please choose a module first.");
            return;
        }
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            MainFrameProperties.loadTheModuleFromIModuleLoader((IModuleLoader) instance);
        } catch (Exception e2) {
            SwingDialog.showErrorMSGDialog("Error", "Error when loading the module: " + className);
        }
    }

}
