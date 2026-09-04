package egps2.builtin.modules.voice.fastmodvoice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.BooleanUtils;

import com.google.common.base.Strings;

import utils.string.EGPSStringUtil;

/**
 * OrganizedParameterGetter belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class OrganizedParameterGetter {

    private final Map<String, LinkedList<String>> orgnizedMap;

    OrganizedParameterGetter(Map<String, LinkedList<String>> orgnizedMap) {
        this.orgnizedMap = orgnizedMap;
    }

    /**
     * 获取简化后的布尔值。
     * 从给定的字符串中提取键对应的第一个值，并尝试将其转换为布尔值。
     *
     * @param str 需要解析的字符串
     * @return 解析得到的布尔值
     * @throws IllegalArgumentException 如果值为空或无法解析为布尔值
     */
    public boolean getSimplifiedBool(String str) {
        String key = getKey(str);
        LinkedList<String> strings = orgnizedMap.get(key);
        String s = strings.get(0);
        String valueAfterEqualChar = EGPSStringUtil.getStringAfterEqualChar(s);

        if (Strings.isNullOrEmpty(valueAfterEqualChar)) {
            throw new IllegalArgumentException("Please input the parameter of ".concat(key));
        } else {
            return BooleanUtils.toBoolean(valueAfterEqualChar);
        }
    }

    /**
     * 获取简化后的整数值。
     * 从给定的字符串中提取键对应的第一个值，并尝试将其转换为整数。
     *
     * @param str 需要解析的字符串
     * @return 解析得到的整数值
     * @throws IllegalArgumentException 如果值为空或无法解析为整数
     */
    public int getSimplifiedInt(String str) {
        String key = getKey(str);
        LinkedList<String> strings = orgnizedMap.get(key);
        String s = strings.get(0);
        String valueAfterEqualChar = EGPSStringUtil.getStringAfterEqualChar(s);

        if (Strings.isNullOrEmpty(valueAfterEqualChar)) {
            throw new IllegalArgumentException("Please input the parameter of ".concat(key));
        } else {
            return Integer.parseInt(valueAfterEqualChar);
        }
    }

    /**
     * 获取简化后的双精度浮点数值。
     * 从给定的字符串中提取键对应的第一个值，并尝试将其转换为双精度浮点数。
     *
     * @param str 需要解析的字符串
     * @return 解析得到的双精度浮点数值
     * @throws IllegalArgumentException 如果值为空或无法解析为双精度浮点数
     */
    public double getSimplifiedDouble(String str) {
        String key = getKey(str);
        LinkedList<String> strings = orgnizedMap.get(key);
        String s = strings.get(0);
        String valueAfterEqualChar = EGPSStringUtil.getStringAfterEqualChar(s);

        if (Strings.isNullOrEmpty(valueAfterEqualChar)) {
            throw new IllegalArgumentException("Please input the parameter of ".concat(key));
        } else {
            return Double.parseDouble(valueAfterEqualChar);
        }
    }

    /**
     * 获取简化后的字符串值。
     * 从给定的字符串中提取键对应的第一个值。
     *
     * @param str 需要解析的字符串
     * @return 解析得到的字符串值
     */
    public String getSimplifiedString(String str) {
        String key = getKey(str);
        LinkedList<String> strings = orgnizedMap.get(key);
        if (strings == null || strings.isEmpty()){
            throw new IllegalArgumentException("Please input the parameter of ".concat(key));
        }
        String s = strings.getFirst();
        String valueAfterEqualChar = EGPSStringUtil.getStringAfterEqualChar(s);

        if (Strings.isNullOrEmpty(valueAfterEqualChar)) {
            throw new IllegalArgumentException("Please input the parameter of ".concat(key));
        } else {
            return valueAfterEqualChar;
        }
    }

	/**
	 * 相较于getSimplifiedString()方法，它不会抛出异常
	 */
	public String getSimplifiedStringWithDefault(String str) {
		if (isSimplifiedValueExist(str)) {
			return getSimplifiedString(str);
		} else {
			return "";
		}
	}

    /**
     * 判断简化值是否存在
     * 该方法用于检查给定字符串的简化值是否存在于组织好的映射中
     * 简化值是指字符串中等号后面的部分
     *
     * @param str 输入的字符串，用于查找其简化值
     * @return 如果简化值存在且不为空，则返回true；否则返回false
     */
    public boolean isSimplifiedValueExist(String str) {
        String key = getKey(str);
        LinkedList<String> strings = orgnizedMap.get(key);
		if (strings == null || strings.isEmpty()) {
			return false;
		}
        String s = strings.get(0);
        String valueAfterEqualChar = EGPSStringUtil.getStringAfterEqualChar(s);

        return !Strings.isNullOrEmpty(valueAfterEqualChar);
    }

    /**
     * 获取复杂情况下的值列表。
     * 根据提供的字符串获取对应键的所有值。
     *
     * @param str 需要解析的字符串
     * @return 包含所有值的可选列表，如果未找到则返回空可选对象
     */
    public Optional<List<String>> getComplicatedValues(String str) {
        String key = getKey(str);

        LinkedList<String> strings = orgnizedMap.get(key);
        return Optional.ofNullable(strings);
    }

    /**
     * 根据输入的字符串获取直接条目或文件内容
     *
     * @param str 输入的字符串，用于查找对应的条目或文件路径
     * @return 包含直接条目或文件内容的Optional列表如果输入的字符串没有对应的条目，则抛出IllegalArgumentException
     * @throws IOException 如果读取文件时发生错误
     */
    public List<String> getEntriesDirectlyOrFile(String str) throws IOException {
        // 根据输入字符串生成查找键
        String key = getKey(str);

        // 从组织化的映射中获取与键对应的字符串列表
        LinkedList<String> strings = orgnizedMap.get(key);

        // 根据字符串列表的长度决定返回直接条目还是从文件获取内容
        int size = strings.size();
        if (size == 0) {
            // 如果列表为空，抛出IllegalArgumentException提示缺少参数内容
            throw new IllegalArgumentException("Please input the contents of parameters: ".concat(key));
        } else if (size == 1) {
            // 如果列表只有一个元素，认为这是文件路径，尝试从文件读取内容
            String s = strings.get(0);
            // 获取字符串中等号后的部分作为文件路径
            String filePath = EGPSStringUtil.getStringAfterEqualChar(s);
            // 从文件路径读取所有内容
            // 返回文件内容
            return Files.readAllLines(Paths.get(filePath));
        } else {
            // 如果列表长度大于1，返回从索引1开始到结尾的子列表
            return strings.subList(1, strings.size());
        }
    }

    /**
     * 获取等于字符后的所有条目
     * 该方法旨在根据给定的字符串检索一个关联映射中对应的条目列表，
     * 并返回这些条目中从某个特定字符之后的内容
     * 分隔符是 ;
     *
     * @param str 输入的字符串，用于检索条目
     * @return 一个字符串列表，包含所有条目中从特定字符之后的内容
     */
    public List<String> getEntriesAfterEqualChar(String str) {
        // 根据输入字符串生成查找键
        String key = getKey(str);
        LinkedList<String> strings = orgnizedMap.get(key);
        if (strings == null) {
            return Collections.emptyList();
        }

        // 根据字符串列表的长度决定返回直接条目还是从文件获取内容
        int size = strings.size();
        if (size == 0) {
            return Collections.emptyList();
        } else {
            String s = strings.get(0);
            String string = EGPSStringUtil.getStringAfterEqualChar(s);
            String[] split = EGPSStringUtil.split(string, ';');
            return Arrays.asList(split);
        }
    }

    /**
     * 根据输入的字符串生成Key
     * 如果输入的字符串不以'$'开头，则在字符串前添加'$'作为前缀
     *
     * @param str 输入的字符串，用于生成键
     * @return 生成的键字符串
     * @throws IllegalArgumentException 如果输入的字符串为空或者为null
     */
    private String getKey(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("The input string is empty");
        }
        return str.charAt(0) == '$' ? str : "$".concat(str);
    }
}
