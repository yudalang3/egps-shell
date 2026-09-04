package egps.lnf.ch3_button;

import egps.lnf.ninepatch4j.NinePatch;
import egps.lnf.utils.NinePatchHelper;
import egps.lnf.utils.RawCache;

/**
 * NinePatch图片（*.9.png）工厂类.
 * 
 * @author Jack Jiang
 * @version 1.0
 */
public class __Icon9Factory__ extends RawCache<NinePatch> {

	/** 相对路径根（默认是相对于本类的相对物理路径）. */
	public final static String IMGS_ROOT = "imgs/np";

	/** The instance. */
	private static __Icon9Factory__ instance = null;

	/**
	 * Gets the single instance of __Icon9Factory__.
	 *
	 * @return single instance of __Icon9Factory__
	 */
	public static __Icon9Factory__ getInstance() {
		if (instance == null)
			instance = new __Icon9Factory__();
		return instance;
	}

	@Override
	protected NinePatch getResource(String relativePath, Class<?> baseClass) {
		return NinePatchHelper.createNinePatch(baseClass.getResource(relativePath), false);
	}

	/**
	 * Gets the raw.
	 *
	 * @param relativePath the relative path
	 * @return the raw
	 */
	public NinePatch getRaw(String relativePath) {
		return getRaw(relativePath, this.getClass());
	}

	/**
	 * Gets the button icon_ normal green.
	 *
	 * @return the button icon_ normal green
	 */
	public NinePatch getButtonIcon_NormalGreen() {
		return getRaw(IMGS_ROOT + "/btn_special_default.9.png");
	}

	/**
	 * Gets the button icon_ normal gray.
	 *
	 * @return the button icon_ normal gray
	 */
	public NinePatch getButtonIcon_NormalGray() {
		return getRaw(IMGS_ROOT + "/btn_general_default.9.png");
	}

	/**
	 * Gets the button icon_ disable gray.
	 *
	 * @return the button icon_ disable gray
	 */
	public NinePatch getButtonIcon_DisableGray() {
		return getRaw(IMGS_ROOT + "/btn_special_disabled.9.png");
	}

	/**
	 * Gets the button icon_ pressed orange.
	 *
	 * @return the button icon_ pressed orange
	 */
	public NinePatch getButtonIcon_PressedOrange() {
		return getRaw(IMGS_ROOT + "/btn_general_pressed.9.png");
	}

	/**
	 * Gets the button icon_rover.
	 *
	 * @return the button icon_rover
	 */
	public NinePatch getButtonIcon_rover() {
		return getRaw(IMGS_ROOT + "/btn_general_rover.9.png");
	}

	/**
	 * Gets the button icon_ normal light blue.
	 *
	 * @return the button icon_ normal light blue
	 */
	public NinePatch getButtonIcon_NormalLightBlue() {
		return getRaw(IMGS_ROOT + "/btn_special_lightblue.9.png");
	}

	/**
	 * Gets the button icon_ normal red.
	 *
	 * @return the button icon_ normal red
	 */
	public NinePatch getButtonIcon_NormalRed() {
		return getRaw(IMGS_ROOT + "/btn_special_red.9.png");
	}

	/**
	 * Gets the button icon_ normal blue.
	 *
	 * @return the button icon_ normal blue
	 */
	public NinePatch getButtonIcon_NormalBlue() {
		return getRaw(IMGS_ROOT + "/btn_special_blue.9.png");
	}

}