package egps2.modulei;

import java.awt.Color;
import java.awt.Font;
import java.util.Optional;

/**
 * 模块实现这个接口即可调用右侧的Shape formatting面板。
 * 注：模块开发者需要自己在合适的时候调用更新方法，以免造成界面的不一致：当用户选中一个图形元素时，可进行调整，但是按钮缺仍然不可用。 如果更新：
 * <code>
 * MyFrame instanceFrame = UniSoftInstance.getInstanceFrame();
 * instanceFrame.refreshRightGraphicPropertiesPanel();
 * </code>
 * 
 * The AdjusterFillAndLine interface defines methods for adjusting the fill
 * color, font, line color, and line thickness of a component. It provides
 * methods to check if these adjustments are possible and to perform the
 * adjustments.
 * 
 */
public interface AdjusterFillAndLine {

	/**
	 * Checks if the fill color of the component can be set.
	 * 
	 * @return an Optional containing the old Color if the fill color can be set,
	 *         otherwise an empty Optional.
	 */
	Optional<Color> couldSetFillColor();

	/**
	 * Adjusts the fill color of the component to the specified color.
	 * 
	 * @param newCol the new fill color of the component.
	 */
	void adjustFillColor(Color newCol);

	/**
	 * Checks if the font of the component can be set.
	 * 
	 * @return an Optional containing the old Font if the font can be set, otherwise
	 *         an empty Optional.
	 */
	Optional<Font> couldSetFont();

	/**
	 * Adjusts the font of the component to the specified font.
	 * 
	 * @param newFont the new font of the component.
	 */
	void adjustFillFont(Font newFont);

	/**
	 * Checks if the line color of the component can be set.
	 * 
	 * @return an Optional containing the old Color if the line color can be set,
	 *         otherwise an empty Optional.
	 */
	Optional<Color> couldSetLineColor();

	/**
	 * Adjusts the line color of the component to the specified color.
	 * 
	 * @param newCol the new line color of the component.
	 */
	void adjustLineColor(Color newCol);

	/**
	 * Checks if the line thickness of the component can be set.
	 * 
	 * @return an Optional containing the old line thickness (in pixels) if the line
	 *         thickness can be set, otherwise an empty Optional.
	 */
	Optional<Integer> couldSetLineThickness();

	/**
	 * Adjusts the line thickness of the component to the specified thickness.
	 * 
	 * @param newThickness the new line thickness (in pixels) of the component.
	 */
	void adjustLineThickness(int newThickness);
}
