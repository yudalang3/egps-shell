package egps2.builtin.modules.largetextedi.gui;

import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

/**
 * ExtendedStyledEditorKit belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class ExtendedStyledEditorKit extends StyledEditorKit {
	private static final long serialVersionUID = 1L;

	private static final ViewFactory styledEditorKitFactory = (new StyledEditorKit()).getViewFactory();

	private static final ViewFactory defaultFactory = new ExtendedStyledViewFactory();

	public Object clone() {
		return new ExtendedStyledEditorKit();
	}

	public ViewFactory getViewFactory() {
		return defaultFactory;
	}

	/* The extended view factory */
	/**
	 * ExtendedStyledViewFactory belongs to a built-in eGPS module (loader, panel, or helper).
	 */
	static class ExtendedStyledViewFactory implements ViewFactory {
		public View create(Element elem) {
			String elementName = elem.getName();
			if (elementName != null) {
				if (elementName.equals(AbstractDocument.ParagraphElementName)) {
					return new ExtendedParagraphView(elem);
				}
			}

			// Delegate others to StyledEditorKit
			return styledEditorKitFactory.create(elem);
		}
	}

}

/**
 * ExtendedParagraphView belongs to a built-in eGPS module (loader, panel, or helper).
 */
class ExtendedParagraphView extends ParagraphView {
	public ExtendedParagraphView(Element elem) {
		super(elem);
	}

	@Override
	public float getMinimumSpan(int axis) {
		return super.getPreferredSpan(axis);
	}
}
