package egps2.modulei;

import java.awt.Dimension;
import java.util.Optional;

/**
 * The AdjusterSizeAndPosition interface defines methods for adjusting the size,
 * rotation, and position of a component. It provides methods to check if these
 * adjustments are possible and to perform the adjustments.
 */
public interface AdjusterSizeAndPosition {

	/**
	 * Checks if the size of the component can be adjusted.
	 * 
	 * @return an Optional containing the old Dimension if the size can be adjusted,
	 *         otherwise an empty Optional.
	 */
	Optional<Dimension> couldAdjustSize();

	/**
	 * Checks if the rotation of the component can be adjusted.
	 * 
	 * @return an Optional containing the old rotation angle (in degrees) if the
	 *         rotation can be adjusted, otherwise an empty Optional.
	 */
	Optional<Integer> couldRotation();

	/**
	 * Checks if the position of the component can be adjusted.
	 * 
	 * @return an Optional containing the old Dimension representing the position if
	 *         the position can be adjusted, otherwise an empty Optional.
	 */
	Optional<Dimension> couldAdjustPosition();

	/**
	 * Adjusts the size of the component to the specified new width and height.
	 * 
	 * @param newWidth  the new width of the component.
	 * @param newHeight the new height of the component.
	 */
	void adjustSize(int newWidth, int newHeight);

	/**
	 * Adjusts the rotation of the component to the specified new angle.
	 * 
	 * @param newAngle the new rotation angle (in degrees) of the component.
	 */
	void adjustRotation(int newAngle);

	/**
	 * Adjusts the position of the component to the specified new horizontal and
	 * vertical coordinates.
	 * 
	 * @param hori the new horizontal position of the component.
	 * @param vert the new vertical position of the component.
	 */
	void adjustPosition(int hori, int vert);
}
