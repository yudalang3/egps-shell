package egps2.utils.common.math;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 矩阵元素工具类，用于验证和识别矩阵元素的数据类型。
 * Matrix element utility class for validating and identifying data types of matrix elements.
 *
 * <p>此工具类提供高效的字符串类型检测方法，用于判断矩阵元素是否为数值型（double、int）或特殊值（NA）。
 * 主要用于处理生物信息学和统计学中的矩阵数据，其中经常出现缺失值（NA）和各种数值格式。
 * This utility class provides efficient string type detection methods for determining whether matrix elements are numeric (double, int) or special values (NA).
 * Primarily used for processing matrix data in bioinformatics and statistics, where missing values (NA) and various numeric formats frequently occur.
 *
 * <p><strong>支持的检测类型：</strong>
 * Supported detection types:
 * <ul>
 *   <li><b>Double 浮点数：</b>支持科学计数法、十六进制浮点数、带类型后缀（f/F/d/D）的数值</li>
 *   <li><b>Integer 整数：</b>匹配标准整数格式（包括负数）</li>
 *   <li><b>NA 缺失值：</b>不区分大小写地识别 "NA"（常用于统计学中表示缺失数据）</li>
 * </ul>
 *
 * <p><strong>正则表达式说明：</strong>
 * Regular expression explanation:
 * <ul>
 *   <li><b>DOUBLE_PATTERN：</b>来自Java规范的完整浮点数匹配模式，支持：
 *     <ul>
 *       <li>标准小数：123.456</li>
 *       <li>科学计数法：1.23e10, 1.23E-5</li>
 *       <li>十六进制浮点：0x1.8p3</li>
 *       <li>类型后缀：123f, 456.789d</li>
 *     </ul>
 *   </li>
 *   <li><b>INT_PATTERN：</b>匹配零或非零开头的整数（含负数）</li>
 * </ul>
 *
 * <p><strong>使用示例：</strong>
 * Usage examples:
 * <pre>{@code
 * // Check if element is double
 * boolean isDouble1 = MatrixElementUtil.isDoubleCompiledRegex("3.14");      // true
 * boolean isDouble2 = MatrixElementUtil.isDoubleCompiledRegex("1.23e-5");   // true
 * boolean isDouble3 = MatrixElementUtil.isDoubleCompiledRegex("0x1.8p3");   // true
 *
 * // Check if element is NA (missing value)
 * boolean isNA1 = MatrixElementUtil.isNAN("NA");    // true
 * boolean isNA2 = MatrixElementUtil.isNAN("na");    // true (case-insensitive)
 *
 * // Check if element is integer
 * boolean isInt1 = MatrixElementUtil.isInt("123");   // true
 * boolean isInt2 = MatrixElementUtil.isInt("-456");  // true
 * boolean isInt3 = MatrixElementUtil.isInt("0");     // true
 * }</pre>
 *
 * <p><strong>性能优化：</strong>
 * Performance optimization:
 * <br>正则表达式模式在类加载时编译并缓存为静态常量，避免重复编译开销。
 * Regex patterns are compiled and cached as static constants during class loading, avoiding repeated compilation overhead.
 *
 * <p><strong>数据来源：</strong>
 * Data source:
 * <br>Double类型正则表达式来自Stack Overflow:
 * <br><a href="https://stackoverflow.com/questions/3133770">How to find out if the value contained in a string is double or not</a>
 *
 * @see Pattern
 * @see Matcher
 * @author eGPS Dev Team
 * @since 2.0
 */
public class MatrixElementUtil{
	private static final String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
	private static final Pattern DOUBLE_PATTERN = Pattern.compile(regExp);
	private static final Pattern INT_PATTERN = Pattern.compile("0|([-]?[1-9][0-9]*)");
	
	/**
	 * 判断一个字符串是否是Double!
	 * 方法是从 stack over flow 上来的！
	 * https://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
	 * @return
	 */
	public static boolean isDoubleCompiledRegex(String s) {
	    Matcher m = DOUBLE_PATTERN.matcher(s);
	    return m.matches();
	}
	
	public static boolean isNAN(String s) {
	    return "NA".equalsIgnoreCase(s);
	}

	public static boolean isInt(String str) {
        return INT_PATTERN.matcher(str).matches();
    }
}
