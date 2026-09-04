package egps2.builtin.modules.voice.fastmodvoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;

import utils.string.EGPSStringUtil;

/**
 * <h1>目的</h1>
 * <p>
 * <p>
 * 快速解析字符串
 * </p>
 *
 * <h1>输入/输出</h1>
 *
 * <p>
 * 输入字符串，自动根据 $生成 Map
 * </p>
 *
 * <h1>使用方法</h1>
 *
 * <blockquote> 直接用 两个高级的封装方法是：parseLongSingleString4simplifiedMap() 和
 * getOrganizedParameterGetter()
 * 
 * </blockquote>
 *
 * <h1>注意点</h1>
 * <ol>
 * <li>要理解Map的value为什么是一个List，可以运行main方法。要注意key是包含$字符的。</li>
 * <li>输入的 List<String> 内容不要带有 # 注释</li>
 * </ol>
 *
 * @author yudal
 * @implSpec 直接遍历生成一个Map，没有太多黑魔法。拥有很高地提升空间，但是没必要，因为它已经得到了从零到一的突破。
 */
public class VoiceParameterParser {

    static final char PARAMETER_STRING_HEADER = '$';
    static final char EQUAL_CHAR = EGPSStringUtil.EQUAL_CHAR;

    /**
     * 根据输入的字符串生成一个组织化的参数获取器
     * 该方法首先将长单行字符串解析成字符串数组，然后将这些字符串组织到一个映射中，
     * 最后返回一个新的组织化参数获取器实例，该实例可以按参数类型获取相应的值
     *
     * @param str 需要被解析和组织的长单行字符串
     * @return 返回一个OrganizedParameterGetter实例，该实例包含组织化的参数
     */
    public OrganizedParameterGetter getOrganizedParameterGetter(String str) {
        Map<String, LinkedList<String>> map = parseInputString4organization(getStringsFromLongSingleLine(str));
        return new OrganizedParameterGetter(map);
    }

    /**
     * 将一个长字符串解析为简化版的映射表
     * 主要用于处理单行的、格式化的组织结构字符串，并将其转换为键值对的Map
     *
     * @param input 输入的长字符串，包含多个组织结构信息
     * @return 返回一个Map，键是组织结构的名称，值是名称对应的代码
     */
    public Map<String, String> parseLongSingleString4simplifiedMap(String input) {
        Map<String, String> ret = new HashMap<>();

        List<String> stringsFromLongSingleLine = getStringsFromLongSingleLine(input);
        Map<String, LinkedList<String>> inputString4organization = parseInputString4organization(stringsFromLongSingleLine);

        for (Map.Entry<String, LinkedList<String>> entry : inputString4organization.entrySet()) {
            LinkedList<String> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                String string = values.get(0);
                String stringAfterEqualStr = getStringAfterEqualStr(string);
                ret.put(entry.getKey(), stringAfterEqualStr);
            }else{
                throw new IllegalArgumentException("Input string is not valid, please check the key: ".concat(entry.getKey()));
            }
        }
        return ret;
    }

    /**
     * 解析输入字符串列表以获取组织结构
     * 该方法将输入的字符串列表解析成以字符串开头的参数为键，其后连续的字符串为值的映射关系
     * 有可能需要和 parseLongSingleString4simplifiedMap 方法一起使用。
     *
     * @param input 输入的字符串列表，其中以参数字符串开头的表示键，其后的连续字符串表示对应的值
     * @return 返回一个映射关系，键为参数字符串，值为参数字符串后连续的字符串列表
     */
    public Map<String, LinkedList<String>> parseInputString4organization(List<String> input) {

        Map<String, LinkedList<String>> ret = new HashMap<>();

        int size = input.size();

        int index;
        for (index = 0; index < size; index++) {
            String string = input.get(index);

            if (string.charAt(0) == PARAMETER_STRING_HEADER) {
                int indexOf = string.indexOf(EQUAL_CHAR);
                String keyString;
                if (indexOf < 0) {
                    keyString = string;
                } else {
                    keyString = string.substring(0, indexOf);
                }

                LinkedList<String> linkedList = new LinkedList<>();
                linkedList.add(string);

                int tempIndex = index + 1;
                while (tempIndex < size) {
                    String nextString = input.get(tempIndex);
                    if (nextString.charAt(0) == PARAMETER_STRING_HEADER) {
                        break;
                    } else {
                        linkedList.add(nextString);
                    }
                    tempIndex++;
                }

                index = tempIndex - 1;

                ret.put(keyString, linkedList);
            }
        }

        return ret;

    }
    /**
     * 功能：得到 = 字符后面的内容
     * <pre>
     * a=b ==>  b
     * =b  ==> b
     * g=apple ==> apple
     *
     * </pre>
     *
     * @param str 输入的字符串，包含=字符
     * @return 返回=字符后面的内容
     */
    public String getStringAfterEqualStr(String str) {
		return  EGPSStringUtil.getStringAfterEqualChar(str);
    }

    /**
     * 从一个可能包含多行的字符串中提取非空且非注释的字符串
     * 忽略空行和以'#'开头的注释行。
     * 需求来源：
     * 1. 从 JTextArea 组件中获取文本
     * 2. 从文件读取字符串，不过现在还没有碰到。主要是上面的来源。
     *
     * @param str 输入的字符串，可能包含多行
     * @return 返回一个字符串列表，其中不包含空行和注释行
     */
    public List<String> getStringsFromLongSingleLine(String str) {
        if (str == null || str.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> ret = new ArrayList<>();

        Splitter splitter = Splitter.on('\n').trimResults().omitEmptyStrings();
        for (String line : splitter.split(str)) {
            if (!line.isEmpty() && line.charAt(0) != '#') {
                ret.add(line);
            }
        }

        return ret;
    }
}
