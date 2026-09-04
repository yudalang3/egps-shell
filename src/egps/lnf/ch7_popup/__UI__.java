package egps.lnf.ch7_popup;

import javax.swing.PopupFactory;

/**
 * The Class __UI__.
 */
public class __UI__
{
	
	/** The popup factory diy. */
	public static PopupFactory popupFactoryDIY = new TranslucentPopupFactory();
	
	/**
	 * Ui impl.
	 */
	public static void uiImpl()
	{
		PopupFactory.setSharedInstance(popupFactoryDIY);
	}
}
