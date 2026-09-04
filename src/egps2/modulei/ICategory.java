package egps2.modulei;

/**
 * Interface defining module categorization in the eGPS framework.
 * This interface allows modules to declare their category classification,
 * which organizes modules by functionality, application domain, and complexity.
 *
 * <p>The category is represented as an integer array with a fixed order:
 * <ol>
 *   <li>By Functionality - The primary function type (e.g., visualization, analysis)</li>
 *   <li>By Application - The application domain (e.g., genomics, phylogenetics)</li>
 *   <li>By Complexity - The dependency level and complexity</li>
 * </ol>
 * </p>
 *
 * <p><strong>Important:</strong> Developers should not manually create the int array.
 * Instead, use {@link ModuleClassification#getOneModuleClassification(int, int, int)}
 * to generate the category value.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ModuleClassification.getOneModuleClassification(
 *     BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
 *     BYAPPLICATION_COMMON_MODULE_INDEX,
 *     BYCOMPLEXITY_STRONG_INDEPENDENT_INDEX
 * );
 * }</pre>
 *
 * @see ModuleClassification
 * @author eGPS Development Team
 */
public interface ICategory {
	/**
	 * Returns the category classification of this module as an integer array.
	 *
	 * 返回这个模块属于哪个类别。它的顺序是固定的。
	 * 那么怎么产生这个值呢？
	 *
	 * 这个返回值不应该由开发者自己 创建int数组来赋值，而应该通过调用 ModuleClassification.getOneModuleClassification
	 * 方法来产生结果。
	 *
	 * 使用的例子:
	 *
	 * <pre>
	 *  ModuleClassification.getOneModuleClassification(
	 *      BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
	 *      BYAPPLICATION_COMMON_MODULE_INDEX,
	 *      BYCOMPLEXITY_STRONG_INDEPENDENT_INDEX
	 *  );
	 * </pre>
	 *
	 * @return An integer array containing category indices in the order: [functionality, application, complexity]
	 */
	int[] getCategory();
}
