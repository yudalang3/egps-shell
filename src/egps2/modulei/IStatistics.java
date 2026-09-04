package egps2.modulei;

/**
 * Interface for modules that provide statistical functionality and feature tracking.
 * This interface allows visualization modules to declare their specific features
 * and capabilities, enabling proper tracking of feature usage by users.
 *
 * <p>For properly developed modules, features should be discussed and defined
 * before implementation begins. This interface provides a mechanism to annotate
 * which features a module possesses.</p>
 *
 * <p>For information on how to record user usage of these features,
 * refer to the {@link ModuleFace} class.</p>
 *
 * @see ModuleFace
 * @author eGPS Development Team
 */
public interface IStatistics {

	/**
	 * Returns the names of features supported by this module.
	 *
	 * 可视化模块具有哪些特性，一般而言对于正规流程开发的模块，需要在开发之前就讨论好具体要实现什么功能。
	 * 因此这里提供了方法，用来标注该模块拥有哪些特性。
	 *
	 * 如何记录用户使用了这些特性？请参考 ModuleFace类。
	 *
	 * @return An array of feature names that this module supports, or null if no features are defined
	 */
	String[] getFeatureNames();

}
