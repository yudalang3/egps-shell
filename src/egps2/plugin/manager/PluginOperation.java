package egps2.plugin.manager;

import egps2.panels.dialog.SwingDialog;
import egps2.EGPSProperties;
import egps2.utils.EGPSIconUtil;
import egps2.Launcher;
import egps2.UnifiedAccessPoint;
import egps2.frame.MainFrameProperties;
import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * PluginOperation supports the plugin/template system for extending eGPS.
 */
public class PluginOperation {

    private final File PLUGIN_DIR = new File(EGPSProperties.PROPERTIES_DIR + "/plugin");
    private static final Logger logger = LoggerFactory.getLogger(PluginOperation.class);

    public void configMenu(JMenu pluginsMenus) {
        // Add "Open folder" button as the first menu item
        addOpenFolderMenuItem(pluginsMenus);

        // http://www.java2s.com/Tutorial/Java/0240__Swing/whatmenuslooklike.htm
        if (!PLUGIN_DIR.isDirectory()) {
            logger.trace("The plugin dir is not a file directory.");
            return;
        }
        File[] list = PLUGIN_DIR.listFiles();
        logger.trace("Rdebug   PluginOperation 38...");
        if (list != null) {
            for (File filtIte : list) {
                configOneJMenu(pluginsMenus, filtIte);
            }
        }
        logger.trace("Rdebug   PluginOperation 42...");

        // write scripts for the eGPS for data analysis

        if (!Launcher.isDev) {
            return;
        }
        installAnalysisMenus(pluginsMenus);
    }

    /**
     * Add "Open folder" menu item at the top of Plugins menu
     * Opens the plugin directory (~/.egps2/config/plugin) in system file manager
     */
    private void addOpenFolderMenuItem(JMenu pluginsMenus) {
        JMenuItem openFolderItem = new JMenuItem("Open folder");
        Font menuSecondLevelFont = UnifiedAccessPoint.getLaunchProperty().getMenuSecondLevelFont();
        openFolderItem.setFont(menuSecondLevelFont);
        openFolderItem.setToolTipText("Open plugin directory in file manager");

        openFolderItem.addActionListener(e -> {
            try {
                // Create plugin directory if it doesn't exist
                if (!PLUGIN_DIR.exists()) {
                    PLUGIN_DIR.mkdirs();
                    logger.info("Created plugin directory: {}", PLUGIN_DIR.getAbsolutePath());
                }

                // Open in system file manager
                if (java.awt.Desktop.isDesktopSupported()) {
                    java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                    if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                        desktop.open(PLUGIN_DIR);
                        logger.info("Opened plugin directory: {}", PLUGIN_DIR.getAbsolutePath());
                    } else {
                        SwingDialog.showErrorMSGDialog("Not Supported",
                            "Opening folders is not supported on this system.");
                    }
                } else {
                    SwingDialog.showErrorMSGDialog("Not Supported",
                        "Desktop operations are not supported on this system.");
                }
            } catch (IOException ex) {
                logger.error("Failed to open plugin directory", ex);
                SwingDialog.showErrorMSGDialog("Error",
                    "Failed to open plugin directory: " + ex.getMessage());
            }
        });

        pluginsMenus.add(openFolderItem);
        pluginsMenus.addSeparator();
    }

    protected void installAnalysisMenus(JMenu pluginsMenus) {
        try {
            List<String> myLists = Arrays.asList(
                    "wating.module.evol.timeline.IndependentModuleLoader",
                    "wating.module.chrom.pating.IndependentModuleLoader");

            pluginsMenus.addSeparator();

            for (String string : myLists) {
                loadOneAnalysisPanel(string, pluginsMenus);
            }

        } catch (Exception e) {
            // e.printStackTrace();
            // do not need this
        }

    }

    private void loadOneAnalysisPanel(String string, JMenu pluginsMenus) throws Exception {
        Object newInstance = Class.forName(string).getDeclaredConstructor().newInstance();

        IModuleLoader independentModuleLoader = (IModuleLoader) newInstance;


        JMenuItem newMenuItem = new JMenuItem(independentModuleLoader.getTabName());

        Font menuSecondLevelFont = UnifiedAccessPoint.getLaunchProperty().getMenuSecondLevelFont();
        newMenuItem.setFont(menuSecondLevelFont);
        newMenuItem.setToolTipText(independentModuleLoader.getShortDescription());

        pluginsMenus.add(newMenuItem);

        newMenuItem.addActionListener(e -> UnifiedAccessPoint.loadTheModuleFromIModuleLoader(independentModuleLoader));

    }

    private void configOneJMenu(JMenu pluginsMenus, File filtIte) {
        String fileNameWithsuffix = filtIte.getName();
        if (!fileNameWithsuffix.endsWith(".jar")) {
            return;
        }
        List<String> lines;
        try {
            lines = JarFileUtil.getEGPSPluginProperties(filtIte.getAbsolutePath());
        } catch (IOException e2) {
            logger.error("Error to get the plugin properties in file. Please check the eGPS 2 plug-in format.", e2);
            return;
        }
        if (lines.isEmpty()) {
            // not contains the config file
            return;
        }
        logger.trace("Rdebug   PluginOperation 61...");
        PluginProperty pluginProperty = new PluginProperty(lines);
        pluginProperty.setJarFile(filtIte);
        logger.trace("Rdebug   PluginOperation 64...");
        IModuleLoader onePluginJarFileLoader = null;
        URLClassLoader URLClassLoader = null;
        try {
            Pair<IModuleLoader, URLClassLoader> ret = loadOnePluginJarFileWorkhorse(pluginProperty);
            logger.trace("Rdebug   PluginOperation 68...");
            onePluginJarFileLoader = ret.getLeft();
            URLClassLoader = ret.getRight();
        } catch (Exception e) {
            logger.error("Error to load plug-in", e);
            return;
        }
        JMenuItem newMenuItem = new JMenuItem(onePluginJarFileLoader.getTabName());

        logger.trace("Rdebug   PluginOperation 77...");

        Font menuSecondLevelFont = UnifiedAccessPoint.getLaunchProperty().getMenuSecondLevelFont();
        newMenuItem.setFont(menuSecondLevelFont);
        newMenuItem.setToolTipText(onePluginJarFileLoader.getShortDescription());

        handleIcon(onePluginJarFileLoader, newMenuItem);

        // Add separator before adding new item (if menu is not empty and last item is not a separator)
        if (pluginsMenus.getMenuComponentCount() > 0) {
            Component lastComponent = pluginsMenus.getMenuComponent(pluginsMenus.getMenuComponentCount() - 1);
            if (!(lastComponent instanceof JSeparator)) {
                pluginsMenus.addSeparator();
            }
        }

        pluginsMenus.add(newMenuItem);

        final IModuleLoader loader = onePluginJarFileLoader;
        final URLClassLoader finalLoder = URLClassLoader;
        newMenuItem.addActionListener(e -> loadingModuleAction(loader, finalLoder));

    }

    public Pair<IModuleLoader, URLClassLoader> loadOnePluginJarFileWorkhorse(PluginProperty pluginProperty)
            throws Exception {

        File file = pluginProperty.getJarFile();
        /*
          https://blog.csdn.net/zsllxbb/article/details/49902661

          @author yudalang
         * @date 2018-12-23
         */

        File[] filePathStrings = null;
        Optional<String[]> dependentJars = pluginProperty.getDependentJars();

        if (dependentJars.isPresent()) {
            String[] strings = dependentJars.get();

            filePathStrings = new File[1 + strings.length];
            filePathStrings[0] = file;
            for (int i = 0; i < strings.length; i++) {
                filePathStrings[i + 1] = new File(PLUGIN_DIR, strings[i]);
            }
        } else {
            filePathStrings = new File[]{file};
        }

        java.net.URL[] urls = new java.net.URL[filePathStrings.length];

        try {
            for (int i = 0; i < urls.length; i++) {
                // 将File类型转为URL类型，file为jar包路径
                urls[i] = filePathStrings[i].toURI().toURL();
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
        CustomURLClassLoader urlClassLoader = new CustomURLClassLoader(urls, ClassLoader.getSystemClassLoader());
        logger.trace("Rdebug   PluginOperation 133...");

        // Check if launchClass is valid (new format required)
        String launchClass = pluginProperty.getLaunchClass();
        if (launchClass == null || launchClass.isEmpty()) {
            urlClassLoader.close();
            throw new Exception("Invalid plugin configuration: launchClass not found in eGPS2.plugin.properties. " +
                    "Please use new format: launchClass=your.package.ClassName");
        }

        Class<?> clazz = null;
        try {
            logger.trace("Rdebug   PluginOperation 136...");
            clazz = urlClassLoader.loadClass(launchClass);
            logger.trace("Rdebug   PluginOperation 138...");
        } catch (ClassNotFoundException e1) {
            String msg = "Your plugin entrance class is not as statement : " + launchClass
                    + "\nPlease check the package name and class name!";
            urlClassLoader.close();
            throw new Exception(msg);
        }
        logger.trace("Rdebug   PluginOperation 143...");
        Object instance = null;

        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException e1) {
            String msg = "Initailize entry class error. Please contact developers of this plug-in.";
            urlClassLoader.close();
            throw new Exception(msg);
        }

        /**
         * 这里的不要把 urlClassLoader关掉，不然类就加载不进来了。 但是这也有一个 垃圾不能回收的问题，暂时还没找到解决方法。
         *
         * <pre>
         * try {
         * 	urlClassLoader.close();
         * } catch (IOException e) {
         * 	e.printStackTrace();
         * }
         *
         * </pre>
         */

        if (!(instance instanceof IModuleLoader)) {
            urlClassLoader.close();
            throw new Exception("Your plug-in's TestExample class has not extend from IModuleLoader!");
        }

        IModuleLoader loader = (IModuleLoader) instance;
        return Pair.of(loader, urlClassLoader);
    }

    protected void loadingModuleAction(final IModuleLoader loader, final URLClassLoader finalLoder) {
        Thread currentThread = Thread.currentThread();
        currentThread.setContextClassLoader(finalLoder);

        try {
            MainFrameProperties.loadTheModuleFromIModuleLoader(loader);
        } catch (Exception e2) {
            e2.printStackTrace();
            logger.error("Loading plugin error", e2);
            SwingDialog.showErrorMSGDialog("Loading plugin error", e2.getMessage());

        }
    }

    protected IconBean handleIcon(IModuleLoader onePluginJarFileLoader, JMenuItem newMenuItem) {
        IconBean iconBean = onePluginJarFileLoader.getIcon();
        if (iconBean == null || !iconBean.hasResource()) {
            return null;
        }
        Icon icon = null;

        try {
            icon = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(iconBean.getInputStream(), iconBean.isSVG());
        } catch (IOException e) {
            logger.error("Can not get Icon in the plug-in {}", onePluginJarFileLoader.getTabName(), e);
        }
        if (icon != null) {
            newMenuItem.setIcon(icon);
        }
        return iconBean;
    }
}
