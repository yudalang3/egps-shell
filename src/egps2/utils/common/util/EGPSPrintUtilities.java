package egps2.utils.common.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import egps2.panels.dialog.SwingDialog;
import egps2.utils.common.util.poi.pptx.SLGraphics;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFGroupShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * EGPSPrintUtilities provides shared utility logic for eGPS modules and UI.
 */
public class EGPSPrintUtilities {

    /**
     * print the 'component'
     *
     * @param component:
     *            the component to be printed
     */
    public static void printComponent(JComponent component) {
        if ((component instanceof Printable)) {
            PrinterJob pj = PrinterJob.getPrinterJob();
            pj.setPrintable((Printable) component);
            if (pj.printDialog()) {
                try {
                    pj.print();
                } catch (PrinterException pe) {
                    SwingDialog.showErrorMSGDialog("Print error", pe.getMessage());
                }
            }
        }
    }


    /**
     * 将JComponent组件保存为指定格式的图像文件
     * 特别处理png格式，使其支持透明背景
     * @param format 图像文件格式，如"png", "jpg"等
     * @param component 需要保存的JComponent组件
     * @param selectedF 选定的图像文件，包含路径和文件名
     * @throws IOException 如果读写文件过程中发生错误
     */
    public static void saveAsBitGraphics(String format, JComponent component, File selectedF)
            throws IOException {
        // 根据图像格式确定图像类型，png支持透明，其他格式默认不透明
        int imageType = BufferedImage.TYPE_INT_RGB;
        if (format.equals("png")) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        }

        // 创建BufferedImage实例，用于绘制组件
        BufferedImage bi = new BufferedImage(component.getSize().width, component.getSize().height, imageType);
        Graphics g = bi.createGraphics();

        // 如果不是png格式，先填充背景为白色
        if (!format.equals("png")) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
        }
        // 将组件绘制到BufferedImage上
        component.paint(g);
        // 释放Graphics资源
        g.dispose();
        // 将BufferedImage写入指定格式的文件
        ImageIO.write(bi, format, selectedF);
    }


    public static void saveAsPDF(JComponent component, File outFile) throws DocumentException, IOException {
        saveAsPDF(component, component.getSize(), outFile);
    }


    public static void saveAsPDF(JComponent component, Dimension dim, File outFile) throws DocumentException, IOException {
        OutputStream stream = Files.newOutputStream(outFile.toPath());
        float width = (float) dim.getWidth();
        float height = (float) dim.getHeight();


        Document document = new Document(new Rectangle(width, height));
        PdfWriter writer = PdfWriter.getInstance(document, stream);
        document.open();
        PdfContentByte cb = writer.getDirectContent();
        PdfTemplate tp = cb.createTemplate(width, height);
        Graphics2D g2d = tp.createGraphics(width, height,
                new DefaultFontMapper());
        component.print(g2d);
        g2d.dispose();
        cb.addTemplate(tp, 0, 0);
        document.close();

        stream.close();

    }


    public static void saveAsPptx(JComponent chart, File outFile) throws IOException {
        try (XMLSlideShow ppt = new XMLSlideShow();
             FileOutputStream out = new FileOutputStream(outFile)) {

            Dimension size = chart.getSize();
            // Set the size of the slide to match JPanel size
            ppt.setPageSize(size);

            // bar chart data. The first value is the bar color, the second is the width
            XSLFSlide slide = ppt.createSlide();

            XSLFGroupShape group = slide.createGroup();
            // define position of the drawing in the slide

            Rectangle2D bounds = new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight());
            group.setAnchor(bounds);
            group.setInteriorAnchor(bounds);

            Graphics2D graphics = new SLGraphics(group, ppt);


            chart.paint(graphics);

            //这是用来辅助定位的一个Anchor 矩形，先不绘制了
            //graphics.draw(group.getInteriorAnchor());

            graphics.dispose();


            ppt.write(out);
        }
    }

}
