package egps2.frame.gui.comp.search;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import utils.string.EGPSStringUtil;

/**
 * 一步一步增加需求吧：
 * 1. 需求是从字符串 line中是否包含一个 string
 * 2. 需求是从字符串 line中是否包含任意一个 strings。只要strings中任意一个在line中匹配就返回true
 * 3. 需求是 includeListOfString 列表中的任意一个字符串，我们还需将其分得更细。
 * 就是说不是直接search方法中 contains 返回 true就返回true
 * 而是要满足两个条件才行
 * 
 * 具体的应用例子：
 * 
 * search_str1;search_str2;search_str3;key1&key2&key3
 * 那么它首先根据;进行split，形成 includeListOfString 这个变量的元素
 * 然后根据子字符串的&符号形成新的字符串
 * 
 * 4. 增加了需求，我们要在满足 includeListOfString 的结果中，剔除一些结果
 * 所以需要增加 excludeListOfString 变量。
 *
 * 具体的应用例子：
 * 假如根据上面的结果我们得到了候选的gff3 feature，这个就是在include基础上，去剔除结果
 * 例如：我们输入的excludeListOfString是 ex_search_key1;ex_key2&ex_key3
 * 那么这样的结果就是会提出原来包含ex_search_key1的结果。也会提出同时包含 ex_key2和ex_key3的结果
 * 
 * @author yudal
 *
 */
public class SearchItem {

	private List<ImmutableList<String>> includeListOfString;
	private List<ImmutableList<String>> excludeListOfString = new ArrayList<>();
	
	
	
	public boolean search(String line) {
		if (includeListOfString == null) {
			throw new IllegalArgumentException("You do not have input the search criteria.");
		}
		boolean ret = false;
		for (ImmutableList<String> immuList : includeListOfString) {
			boolean contained = true;
			
			for (String string2 : immuList) {
				if(!line.contains(string2)) {
					contained = false;
					break;
				}
			}
			
			if (contained) {
				ret = true;
				break;
			}
			
		}
		
		if (ret) {
			//执行exclude
			for (ImmutableList<String> immuList : excludeListOfString) {
				boolean contained = true;
				
				for (String string2 : immuList) {
					if(!line.contains(string2)) {
						contained = false;
						break;
					}
				}
				//符合排除的其中一个entry
				if (contained) {
					ret = false;
					break;
				}
			}
		}
		
		
		return ret;
	}
	
	public List<ImmutableList<String>> getIncludeListOfString() {
		return includeListOfString;
	}
	public void setIncludeListOfString(List<String> includeListOfString) {
		
		List<ImmutableList<String>> list = new ArrayList<>();
		
		for (String str : includeListOfString) {
			String[] splits = EGPSStringUtil.split(str, '&');
			ImmutableList<String> copyOf = ImmutableList.copyOf(splits);
			list.add(copyOf);
		}
		
		
		this.includeListOfString = list;
	}
	public void setExcludeListOfString(List<String> includeListOfString) {
		
		List<ImmutableList<String>> list = new ArrayList<>();
		
		for (String str : includeListOfString) {
			String[] splits = EGPSStringUtil.split(str, '&');
			ImmutableList<String> copyOf = ImmutableList.copyOf(splits);
			list.add(copyOf);
		}
		
		
		this.excludeListOfString = list;
	}
	
	
	
}
