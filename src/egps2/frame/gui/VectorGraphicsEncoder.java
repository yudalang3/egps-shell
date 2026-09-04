package egps2.frame.gui;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JComponent;

import org.apache.pdfbox.cos.COSDocument;

import com.itextpdf.text.DocumentException;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.eps.EPSProcessor;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import egps2.utils.common.util.EGPSPrintUtilities;

/**
 * A helper class with static methods for saving Charts as vectors
 *
 * 
 * @author timmolter
 *
 *         This class is use xchart library for reference.
 * 
 */
public final class VectorGraphicsEncoder {

	/** Constructor - Private constructor to prevent instantiation */
	private VectorGraphicsEncoder() {
	}

	/**
	 * Write a chart to a file.
	 * 
	 * 
	 * 
	 * @throws DocumentException
	 */
	public static void saveVectorGraphic(JComponent chart, String fileName, VectorGraphicsFormat vectorGraphicsFormat)
			throws Exception {

		if (vectorGraphicsFormat == VectorGraphicsFormat.PDF) {
			EGPSPrintUtilities.saveAsPDF(chart, new File(fileName));
			return;
		}

		if (vectorGraphicsFormat == VectorGraphicsFormat.PPTX) {
			EGPSPrintUtilities.saveAsPptx(chart, new File(fileName));
			return;
		}

		FileOutputStream file = new FileOutputStream(addFileExtension(fileName, vectorGraphicsFormat));
		Processor p = null;

		switch (vectorGraphicsFormat) {
		case EPS:
			p = new EPSProcessor();
			break;
//		case SVG:
//			p = new SVGProcessor();
//			break;
		default:
			p = new SVGProcessor();
			break;
		}

		VectorGraphics2D vg2d = new VectorGraphics2D();
		// vg2d.draw(new Rectangle2D.Double(0.0, 0.0, chart.getWidth(),
		// chart.getHeight()));
		CommandSequence commands = vg2d.getCommands();

		chart.paint(vg2d);

		PageSize pageSize = new PageSize(0.0, 0.0, chart.getWidth(), chart.getHeight());
		Document doc = p.getDocument(commands, pageSize);
		if (doc instanceof COSDocument) {
			COSDocument doc2 = (COSDocument) doc;
			doc2.close();
		}

		doc.writeTo(file);

		file.close();
	}

	/**
	 * Only adds the extension of the VectorGraphicsFormat to the filename if the
	 * filename doesn't already have it.
	 *
	 * @param fileName
	 * @param vectorGraphicsFormat
	 * @return filename (if extension already exists), otherwise;: filename + "." +
	 *         extension
	 */
	private static String addFileExtension(String fileName, VectorGraphicsFormat vectorGraphicsFormat) {

		String fileNameWithFileExtension = fileName;
		final String newFileExtension = "." + vectorGraphicsFormat.toString().toLowerCase();
		if (fileName.length() <= newFileExtension.length()
				|| !fileName.substring(fileName.length() - newFileExtension.length(), fileName.length())
						.equalsIgnoreCase(newFileExtension)) {
			fileNameWithFileExtension = fileName + newFileExtension;
		}
		return fileNameWithFileExtension;
	}

	/**
	 * VectorGraphicsFormat supports the main eGPS window, actions, or tab management.
	 */
	public enum VectorGraphicsFormat {
		EPS, PDF, SVG, PPTX
	}

}
