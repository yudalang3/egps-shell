package egps2.builtin.modules;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import egps2.frame.DefaultParamsAssignerAndParserHandler4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.builtin.modules.voice.fastmodvoice.SubTabModuleRunner;
import egps2.builtin.modules.voice.fastmodvoice.VoiceParameterParser;

/**
 * 这是使用了 VOICE 开发模型 的启动类，具体的计算类是实现了 SubTabModuleRunner 接口的实例。
 */
public class CLI {

	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			printHelp();
		}
		
		String moduleClassPath = args[0];
		String configFilePath = args[1];
		
		SubTabModuleRunner runner = getClassInstance(moduleClassPath);
		

		DefaultParamsAssignerAndParserHandler4VOICE defaultVoiceInputParamHandler = new DefaultParamsAssignerAndParserHandler4VOICE();
		VoiceParameterParser parser = defaultVoiceInputParamHandler.getParameterParser();

		String inputs = FileUtils.readFileToString(new File(configFilePath), StandardCharsets.UTF_8);
		OrganizedParameterGetter organizedParameterGetter = parser.getOrganizedParameterGetter(inputs);
		System.out.println("This is module: " + runner.getTabName());
		Collection<String> results = runner.executeOnCommandLine(organizedParameterGetter);

		for (String string : results) {
			System.out.println(string);
		}
	}

	private static SubTabModuleRunner getClassInstance(String moduleClassPath) throws Exception {
		// 使用反射加载类
		Class<?> clazz = Class.forName(moduleClassPath);

		// 创建 SubTabModuleRunner 实例
		Object instance = clazz.getDeclaredConstructor().newInstance();

		// 如果你确定它是 SubTabModuleRunner 类型，进行强制类型转换
		SubTabModuleRunner runner = (SubTabModuleRunner) instance;
		return runner;
	}

	private static void printHelp() {
		System.err.println("Usage:");
		System.err.println("java -cp \"needed.jars\" egps2.modul.CLI class.path.of.module config.file");
		System.err.println("class.path.of.module is the module that implements the SubTabModuleRunner interface");
		System.err.println("config.file is the file that used in the VOICE GUI");

		System.exit(1);
	}

}
