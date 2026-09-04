package egps2.utils.common.util.poi.pptx;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.compress.utils.Lists;
import org.apache.poi.sl.usermodel.Placeholder;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decoder4pptx provides shared utility logic for eGPS modules and UI.
 */
public class Decoder4pptx {

	private int slideIndex = 0;
	private List<XSLFShape> listOfShapes = Lists.newArrayList();
	private XSLFSlide firstSlide;

	private static final Logger logger = LoggerFactory.getLogger(Decoder4pptx.class);
	private Dimension pageSize;

	public void decodeFile(File inputFile) throws Exception {
		decodeFile(new FileInputStream(inputFile));
	}

	public void decodeFile(String inputFile) throws Exception {
		decodeFile(new FileInputStream(inputFile));
	}

	public void decodeFile(InputStream inputStream) throws Exception {
		try (XMLSlideShow ppt = new XMLSlideShow(inputStream)) {
			List<XSLFSlide> slides = ppt.getSlides();
			XSLFSlide firstSlide = slides.get(slideIndex);
			this.firstSlide = firstSlide;

			pageSize = ppt.getPageSize();

			for (XSLFShape shape : firstSlide.getShapes()) {
				Placeholder placeholder = shape.getPlaceholder();
				if (placeholder != null) {
					continue;
				}

				String shapeName = shape.getShapeName();
				XmlObject xmlObject = shape.getXmlObject();
				Rectangle2D anchor = shape.getAnchor();
//				logger.info("Shape type is: {}, and this is {}. ", shape.getShapeName(), shape.getClass().getName());

				listOfShapes.add(shape);
				if (shape instanceof org.apache.poi.xslf.usermodel.XSLFTextShape) {
					org.apache.poi.xslf.usermodel.XSLFTextShape textShape = (org.apache.poi.xslf.usermodel.XSLFTextShape) shape;
				}

			}

		} catch (IOException e) {
			// 在再抛出
			throw e;
		}
	}

	public List<XSLFShape> getListOfShapes() {
		return listOfShapes;
	}

	public XSLFSlide getFirstSlide() {
		return firstSlide;
	}

	public Dimension getPageSize() {
		return pageSize;
	}

}
