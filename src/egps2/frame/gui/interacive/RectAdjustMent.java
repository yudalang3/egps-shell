package egps2.frame.gui.interacive;

/**
 * RectAdjustMent supports the main eGPS window, actions, or tab management.
 */
public interface RectAdjustMent {
	
	boolean contains(double x, double y);
	
	boolean isAvailable();
	
	/**
	 * This method should be called when the contains method return true!
	 * @return The Cursor type index!
	 */
	int getCursorType();

	void adjustPaintings(double d, double e);
}
