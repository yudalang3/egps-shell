package egps2.builtin.modules.voice.template;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JComponent;

import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.builtin.modules.voice.fastmodvoice.VoiceParameterParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

import egps2.utils.common.util.SaveUtil;

/**
 * The Base class for developers to implement the VOICE module develop model
 * command line launcher.
 */
public abstract class CommandLineArgsInterpreter4VOICE {

	protected int width;
	protected int height;
	protected String configFilePath;
	protected String outputFilePath = "output.pptx";

	protected abstract void process() throws Exception;

	public void parseOptions(String[] args) throws Exception {
		// 创建Options对象，用于存储定义的命令行选项
		Options options = new Options();

		// 添加短选项和对应的长选项以及参数描述
		options.addOption("h", "help", false, "Show help contents");
		options.addOption("w", "width", true, "Set the figure width");
		options.addOption("h", "height", true, "Set the figure height");
		options.addOption("c", "config", true,
				"Set configuration file, the file content is same as the VOICE Input in the GUI desktop.");
		options.addOption("o", "outputFile", true,
				"Set output file, the file suffix name will be used to deternimie the format type, default is pptx.");

		// 解析命令行参数
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println(e.getMessage());
			printHelpAndExit(options);
		}

		// 处理解析后的参数
		if (cmd.hasOption("help")) {
			printHelpAndExit(options);
		}

		// 获取width和height参数值
		if (cmd.hasOption("width")) {
			String widthStr = cmd.getOptionValue("width");
			try {
				width = Integer.parseInt(widthStr);
			} catch (NumberFormatException e) {
				System.err.println("Invalid width value.");
				printHelpAndExit(options);
			}
		}

		if (cmd.hasOption("height")) {
			String heightStr = cmd.getOptionValue("height");
			try {
				height = Integer.parseInt(heightStr);
				// 使用height值...
			} catch (NumberFormatException e) {
				System.err.println("Invalid height value.");
				printHelpAndExit(options);
			}
		}

		if (cmd.hasOption("config")) {
			configFilePath = cmd.getOptionValue("config");
		} else {
			System.err.println("The configuration option is not set.");
			printHelpAndExit(options);
		}

		if (cmd.hasOption("outputFile")) {
			outputFilePath = cmd.getOptionValue("outputFile");
		}

		process();

		System.out.println(" > Successfully executed ~");
	}

	protected String getConfigStringContentsInFile() throws IOException {
		String str = FileUtils.readFileToString(new File(configFilePath), StandardCharsets.UTF_8);

		return str;
	}

	protected OrganizedParameterGetter getConfigFileOrganizedParameterGetter() throws IOException {
		String str = getConfigStringContentsInFile();
		VoiceParameterParser voiceParameterParser = new VoiceParameterParser();
		OrganizedParameterGetter organizedParameterGetter = voiceParameterParser.getOrganizedParameterGetter(str);
		return organizedParameterGetter;
	}

	protected void performVoiceExecute(AbstractGuiBaseVoiceFeaturedPanel avfp) throws Exception {
		String str = FileUtils.readFileToString(new File(configFilePath), StandardCharsets.UTF_8);
		avfp.execute(str);
	}

	protected void savePaintingPanel2file(JComponent paintingPanel) throws Exception {
		new SaveUtil().directlyProduceFigureAccording2filePath(outputFilePath, paintingPanel);
	}

	private void printHelpAndExit(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		Class<? extends CommandLineArgsInterpreter4VOICE> class1 = getClass();
		String name = class1.getName();
		String errStr = "java -cp $egpsLibPath " + name + "";
		formatter.printHelp(errStr, options);

		System.exit(1);
	}
}