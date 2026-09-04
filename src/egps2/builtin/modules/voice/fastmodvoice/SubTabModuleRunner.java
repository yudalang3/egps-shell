package egps2.builtin.modules.voice.fastmodvoice;

import java.util.Collection;

import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import top.signature.IModuleSignature;

/**
 * 
 * <h1>目的</h1>
 * <p>
 * 
 * 这个目的是 为Voice小模块提供一个运行内核 <br>
 * 也就是说开发者只需要实现这个接口的方法，就能够直接使用三大方式运行
 * </p>
 * 
 * <h1>输入/输出</h1>
 * 
 * <p>
 * 没有
 * </p>
 * 
 * <h1>使用方法</h1>
 * 
 * <blockquote> 实现这个类即可;
 * 
 * worker.doIt(); </blockquote>
 * 
 * <h1>注意点</h1>
 * <ol>
 * <li>在调用主要方法 <code>doIt()</code>时不要忘记设置一些属性</li>
 * <li>现在只能当做脚本来用，如果要用的话注意设置 <code>setter</code>方法等</li>
 * </ol>
 * 
 * @implSpec 就是一个代理。
 * 
 * @author yudal
 *
 */
public interface SubTabModuleRunner extends IModuleSignature {

	@Override
	String getShortDescription();

	@Override
	String getTabName();

	/**
	 * Set the parameters hashmap producer. Most common usage:
	 * 
	 * <pre>
	 * 
	 * designer.addKeyValueEntryBean("parameter.name", "default.value", "tooltip/prompt");
	 * designer.addKeyValueEntryBean("%", "category.name", ""); 分类单元
	 * designer.addKeyValueEntryBean("@", "module.name", ""); 模块的名称，现实在脚本的最前方
	 * designer.addKeyValueEntryBean("^", "", "");高级参数的标志
	 * </pre>
	 * 
	 * @param designer
	 */
	void setParameterOnCommandLine(AbstractParamsAssignerAndParser4VOICE designer);

	/**
	 * 脚本执行过程
	 * 
	 * @param o get the users input parameters
	 * @return the words output to the console
	 * @throws Exception
	 */
	Collection<String> executeOnCommandLine(OrganizedParameterGetter o) throws Exception;

}
