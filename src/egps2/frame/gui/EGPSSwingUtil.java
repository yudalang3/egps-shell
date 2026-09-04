package egps2.frame.gui;


import egps2.UnifiedAccessPoint;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * EGPSSwingUtil supports the main eGPS window, actions, or tab management.
 */
public class EGPSSwingUtil {

    /**
     * 从剪切板获得图片。
     */
    public static Image getImageFromClipboard() throws Exception {
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);
        if (cc == null)
            return null;
        else if (cc.isDataFlavorSupported(DataFlavor.imageFlavor))
            return (Image) cc.getTransferData(DataFlavor.imageFlavor);
        return null;
    }

    public static void copyToClipboard(String str) {
        SwingUtilities.invokeLater(() ->{
            //获取系统剪切板
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            //构建String数据类型
            StringSelection selection = new StringSelection(str);
            //添加文本到系统剪切板
            clipboard.setContents(selection, null);
        });

    }

    /**
     *
     * @param time4exist 秒，不是毫秒
     */
    public static void promote(int time4exist, String title, String msg, Component parentComponent) {
        JOptionPane pane = new JOptionPane(msg.concat("\n\nThis dialog will close automatically a few seconds later!"));
        if (parentComponent == null) {
            final JDialog dialog = pane.createDialog(title);
            Timer timer = new Timer(time4exist * 1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });

            timer.setRepeats(false);
            timer.start();

            dialog.setVisible(true);
        }else {
            final JDialog dialog = pane.createDialog(parentComponent,title);
            Timer timer = new Timer(time4exist * 1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();

                }
            });

            timer.setRepeats(false);
            timer.start();

            dialog.setVisible(true);
        }
    }


    public static void setupHighQualityRendering(Graphics2D graphics2D) {
        // 设置抗锯齿
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        setTextAntiAliasing(graphics2D);
        // 设置颜色/纹理平滑度
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        // 设置 stroke 控制
        graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // 设置图形渐变和形状填充的质量
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

    }

    private static void setTextAntiAliasing(Graphics2D g2d) {
        Object antialiasOption = UnifiedAccessPoint.getLaunchProperty().getTextAntiAliasString();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antialiasOption);

    }
}
