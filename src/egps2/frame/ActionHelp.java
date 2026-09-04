package egps2.frame;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import javax.swing.*;

import egps2.utils.EGPSIconUtil;
import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSONObject;
import com.jidesoft.swing.JideTabbedPane;

import egps2.frame.gui.StringKeySaver;
import org.apache.commons.lang3.tuple.Pair;
import utils.storage.MapPersistence;
import egps2.frame.gui.comp.toggle.toggle.ToggleButton;
import egps2.UnifiedAccessPoint;
import egps2.frame.gui.EGPSMainGuiUtil;

/**
 * "帮助"菜单动作，显示软件帮助文档和使用指南。
 * Help menu action displaying software help documentation and usage guide.
 *
 * <p>此动作响应用户点击"帮助"菜单项或按Ctrl/Cmd+H快捷键，显示包含中英文帮助文档的对话框。
 * This action responds to user clicking "Help" menu item or pressing Ctrl/Cmd+H shortcut key, displaying a dialog with Chinese and English help documentation.
 *
 * <p><strong>功能：</strong>
 * Functionality:
 * <ul>
 *   <li>显示帮助文档 - Display help documentation</li>
 *   <li>支持中英文切换 - Support Chinese/English language toggle</li>
 *   <li>提供使用指南 - Provide usage guide</li>
 *   <li>快捷键：Ctrl/Cmd+H - Shortcut: Ctrl/Cmd+H</li>
 * </ul>
 *
 * @see AdjustedSoft4HelpInfoStaticsAction
 * @author eGPS Dev Team
 * @since 2.1
 */
@SuppressWarnings("serial")
public class ActionHelp extends AdjustedSoft4HelpInfoStaticsAction {

    private String actionName = "Help";

    public ActionHelp() {

        putValue(NAME, actionName);
        putValue(SHORT_DESCRIPTION, getShortDescriptionString());
        putValue(MNEMONIC_KEY, KeyEvent.VK_H);
        putValue(ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

        setIcons("help.png");
    }

    private Pair<Icon, Icon> getPairIcon() throws IOException {
        Class<? extends ActionHelp> aClass = getClass();
        InputStream resource1 = aClass.getResourceAsStream("/images/maincore/Chinese  document  instruction  tutorial  manual.svg");
        ImageIcon imageIcon1 = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource1, true);
        InputStream resource2 = aClass.getResourceAsStream("/images/maincore/English  document  instruction  tutorial  manual.svg");
        ImageIcon imageIcon2 = EGPSIconUtil.getIconFromSVGByStreamSoftwaresize(resource2, true);
        return Pair.of(imageIcon1, imageIcon2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();
        ModuleFace selectedModule = instanceFrame.getSelectedModule();

        if (selectedModule == null) {
            instanceFrame.launchIntroductionPanel();
        } else {
            JideTabbedPane jideTabbedPane = new JideTabbedPane(JTabbedPane.TOP);
            jideTabbedPane.setTabShape(JideTabbedPane.SHAPE_ROUNDED_VSNET);
            jideTabbedPane.setOpaque(false);
            jideTabbedPane.setColorTheme(JideTabbedPane.COLOR_THEME_WINXP);
            jideTabbedPane.setFont(UnifiedAccessPoint.getLaunchProperty().getUnSelectedTabTitleFont());
            jideTabbedPane.setSelectedTabFont(UnifiedAccessPoint.getLaunchProperty().getSelectedTabTitleFont());
            jideTabbedPane.setFocusable(false);

            ToggleButton toggleButton = new ToggleButton();
            Dimension dimension = new Dimension(40, 25);
            toggleButton.setSelected(true);
            toggleButton.setForeground(new java.awt.Color(40, 139, 236));
            toggleButton.setToolTipText("Whether automatically save the bound.");
            toggleButton.setPreferredSize(dimension);
            toggleButton.setFont(UnifiedAccessPoint.getLaunchProperty().getDefaultFont());

            jideTabbedPane.setTabTrailingComponent(toggleButton);

            JComponent englishDocument = selectedModule.getEnglishDocument();

            Icon chineseIcon;
            Icon englishIcon;
            try {
                Pair<Icon, Icon> pairIcon = getPairIcon();
                chineseIcon = pairIcon.getLeft();
                englishIcon = pairIcon.getRight();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            if (englishDocument != null) {
                String str = UnifiedAccessPoint.getResourceString("introduction.module.GrapIll.name");
                JComponent autoWrapComponentWithScollPanel = MainFrameProperties
                        .autoWrapComponentWithScollPanel(englishDocument);
                jideTabbedPane.addTab(str, englishIcon, autoWrapComponentWithScollPanel);
            }

//			boolean isEnglish = UnifiedAccessPoint.getLaunchProperty().isEnglish;
//			if (!isEnglish) {
//				这个应该不受影响
//			}
            JComponent chineseDocument = selectedModule.getChineseDocument();
            if (chineseDocument != null) {
                String str = UnifiedAccessPoint.getResourceString("introduction.module.TextExp.name");
                JComponent autoWrapComponentWithScollPanel = MainFrameProperties
                        .autoWrapComponentWithScollPanel(chineseDocument);
                jideTabbedPane.addTab(str, chineseIcon, autoWrapComponentWithScollPanel);
            }


            JDialog jDialog = new JDialog(instanceFrame, actionName, false);
            String saveKey = "help.dialog.save.key";
            Map<String, String> str2strMap = MapPersistence.getStr2strMap(EGPSMainGuiUtil.DIALOG_SAVE_PATH);
            String string = str2strMap.get(saveKey);
            if (string == null) {
                jDialog.setSize(1000, 900);
                jDialog.setLocationRelativeTo(instanceFrame);
            } else {
                Rectangle object = JSONObject.parseObject(string, Rectangle.class);
                jDialog.setBounds(object);
            }

            StringKeySaver saver = new StringKeySaver() {
                @Override
                public Optional<String> getKeySaver() {
                    if (!toggleButton.isSelected()) {
                        return Optional.empty();
                    }
                    Optional<String> saveKeyOpt = Optional.ofNullable(saveKey);
                    return saveKeyOpt;
                }

            };

            EGPSMainGuiUtil.addEscapeListenerAndSaveBounds(jDialog, saver);
            jDialog.add(jideTabbedPane);

            jDialog.setVisible(true);
        }
    }

    @Override
    protected String getShortDescriptionString() {
        String wrapStringArraysAsString = null;
        InputStream resourceAsStream = getClass().getResourceAsStream("html/tooltipOfHelpAction.html");
        try {
            wrapStringArraysAsString = IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wrapStringArraysAsString;
    }

}
