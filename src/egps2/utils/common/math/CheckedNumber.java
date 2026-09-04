package egps2.utils.common.math;

import java.util.regex.Pattern;

/**
 * 数字字符串验证工具类，用于验证字符串是否为特定类型的整数。
 * Number string validation utility class for validating whether strings are specific types of integers.
 *
 * <p>此工具类使用预编译的正则表达式模式来高效验证字符串是否符合整数格式要求。
 * 支持正整数和负整数的验证，并可选择是否包含零。
 * This utility class uses pre-compiled regex patterns to efficiently validate whether strings conform to integer format requirements.
 * Supports validation of positive and negative integers, with optional inclusion of zero.
 *
 * <p><strong>支持的验证类型：</strong>
 * Supported validation types:
 * <ul>
 *   <li><b>正整数（含零）：</b>匹配 0, 1, 2, 3, ... - Pattern: ^[0-9]\\d*$</li>
 *   <li><b>正整数（不含零）：</b>匹配 1, 2, 3, ... - Pattern: ^[1-9]\\d*$</li>
 *   <li><b>负整数（含零）：</b>匹配 -0, -1, -2, -3, ... - Pattern: ^-[0-9]\\d*$</li>
 *   <li><b>负整数（不含零）：</b>匹配 -1, -2, -3, ... - Pattern: ^-[1-9]\\d*$</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage examples:
 * <pre>{@code
 * // Check positive integer (including zero)
 * boolean result1 = CheckedNumber.isPositiveInteger("0", true);    // true
 * boolean result2 = CheckedNumber.isPositiveInteger("123", true);  // true
 * boolean result3 = CheckedNumber.isPositiveInteger("0", false);   // false
 *
 * // Check negative integer (excluding zero)
 * boolean result4 = CheckedNumber.isNegativeInteger("-5", false);  // true
 * boolean result5 = CheckedNumber.isNegativeInteger("0", false);   // false
 * }</pre>
 *
 * <p><strong>性能考虑：</strong>
 * Performance considerations:
 * <br>所有正则表达式模式在类加载时预编译并缓存，确保验证操作的高性能。
 * All regex patterns are pre-compiled and cached during class loading, ensuring high-performance validation operations.
 *
 * @author YFQ
 * @version 1.0
 * @since 2018-05-10
 */
public class CheckedNumber {
	
	private static Pattern CHECK_PositiveInteger = Pattern.compile("^[0-9]\\d*$");
	
	private static Pattern CHECK_PositiveInteger_NoZero = Pattern.compile("^[1-9]\\d*$");
	
	private static Pattern CHECK_NegativeInteger = Pattern.compile("^-[0-9]\\d*$");
	
	private static Pattern CHECK_NegativeInteger_NoZero = Pattern.compile("^-[1-9]\\d*$");
	
	public static boolean isPositiveInteger(String str, boolean isContainZero) {

		boolean result;

		if (isContainZero) {
			result = CHECK_PositiveInteger.matcher(str).matches();
		} else {
			result = CHECK_PositiveInteger_NoZero.matcher(str).matches();
		}
		
		return result;
	}
	
	public static boolean isNegativeInteger(String str, boolean isContainZero) {
		
		boolean result;

		if (isContainZero) {
			result = CHECK_NegativeInteger.matcher(str).matches();
		} else {
			result = CHECK_NegativeInteger_NoZero.matcher(str).matches();
		}

		return result;
	}
}
