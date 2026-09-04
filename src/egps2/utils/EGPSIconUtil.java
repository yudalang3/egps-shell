package egps2.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.geometry.size.FloatSize;
import com.github.weisj.jsvg.parser.SVGLoader;

import egps2.LaunchProperty;
import egps2.UnifiedAccessPoint;

/**
 * EGPSIconUtil provides shared utility logic for eGPS modules and UI.
 */
public class EGPSIconUtil {

	public static ImageIcon getIconFromSVGByPath(URL svgUrl, int targetHeight, int targetWidth) {
		SVGLoader loader = new SVGLoader();
		SVGDocument svgDocument = loader.load(svgUrl);

		return getImageIcon(targetHeight, targetWidth, svgDocument);
	}

	private static ImageIcon getImageIcon(int targetHeight, int targetWidth, SVGDocument svgDocument) {
		FloatSize size = svgDocument.size();
		float originalHeight = size.height;
		float originalWidth = size.width;

		BufferedImage image = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

		// scale from base size to passed size
		double sx = (targetWidth > 0) ? (float) targetWidth / originalWidth : 1;
		double sy = (targetHeight > 0) ? targetHeight / originalHeight : 1;
		if (sx != 1 || sy != 1)
			g.scale(sx, sy);

		svgDocument.render(null, g);
		g.dispose();
		ImageIcon imageIcon = new ImageIcon(image);
		return imageIcon;
	}

	public static ImageIcon getIconFromSVGByStream(InputStream svgUrl, int targetHeight, int targetWidth) {
		SVGLoader loader = new SVGLoader();

		SVGDocument svgDocument = loader.load(svgUrl);

		return getImageIcon(targetHeight, targetWidth, svgDocument);
	}

	public static ImageIcon getIconFromSVGByStreamSoftwaresize(InputStream svgUrl, boolean isSVG) throws IOException {
		Objects.requireNonNull(svgUrl, "Sorry the svgUrl is null");
		LaunchProperty lauchProperty = UnifiedAccessPoint.getLaunchProperty();

		if (isSVG) {
			SVGLoader loader = new SVGLoader();
			SVGDocument svgDocument = loader.load(svgUrl);

			return getImageIcon(lauchProperty.getIconHeight(), lauchProperty.getIconWidth(), svgDocument);
		} else {
			ImageIcon imageIcon = new ImageIcon(IOUtils.toByteArray(svgUrl));
			if (imageIcon.getIconHeight() == lauchProperty.getIconHeight()
					&& imageIcon.getIconWidth() == lauchProperty.getIconWidth()) {
				return imageIcon;
			}

			Image scaledImage = imageIcon.getImage().getScaledInstance(lauchProperty.getIconWidth(),
                    lauchProperty.getIconHeight(), Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(scaledImage);

			return imageIcon;
		}
	}
	
	
	public static ImageIcon getIconFromSVGByStreamSoftwaresizeWithTabIcon(InputStream svgUrl, boolean isSVG) throws IOException {
		LaunchProperty lauchProperty = UnifiedAccessPoint.getLaunchProperty();

		if (isSVG) {
			SVGLoader loader = new SVGLoader();
			SVGDocument svgDocument = loader.load(svgUrl);

			return getImageIcon(lauchProperty.getTabIconHeight(), lauchProperty.getTabIconWidth(), svgDocument);
		} else {
			ImageIcon imageIcon = new ImageIcon(IOUtils.toByteArray(svgUrl));
			if (imageIcon.getIconHeight() == lauchProperty.getTabIconHeight()
					&& imageIcon.getIconWidth() == lauchProperty.getTabIconWidth()) {
				return imageIcon;
			}

			Image scaledImage = imageIcon.getImage().getScaledInstance(lauchProperty.getTabIconWidth(),
                    lauchProperty.getTabIconHeight(), Image.SCALE_SMOOTH);
			imageIcon = new ImageIcon(scaledImage);

			return imageIcon;
		}
	}


}
