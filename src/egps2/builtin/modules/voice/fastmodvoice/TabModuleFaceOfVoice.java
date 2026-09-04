package egps2.builtin.modules.voice.fastmodvoice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import egps2.UnifiedAccessPoint;
import egps2.frame.ComputationalModuleFace;
import egps2.frame.DefaultParamsAssignerAndParserHandler4VOICE;
import egps2.frame.ModuleFace;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.modulei.IModuleLoader;
import egps2.panels.InformationPanelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unified.output.UnifiedPrinter;
import utils.string.EGPSStringUtil;

/**
 * 这个类还是有用武之地的，因为有些人可能不想写一个 Module Loader，对吧。
 */
@SuppressWarnings("serial")
public abstract class TabModuleFaceOfVoice extends ComputationalModuleFace implements IModuleLoader,SubTabModuleRunner {

    private static final Logger log = LoggerFactory.getLogger(TabModuleFaceOfVoice.class);
    // 一些GUI等配置
	protected VoiceParameterHandler4DIYTabModuleFace voiceTools;
	// 专门处理输入参数的类，voiceTools里面也有一个，这个类是和VOICE模块一起的，所以可以共用一个实例
	protected DefaultParamsAssignerAndParserHandler4VOICE mapProducer;
	// 用于解析输入参数的类，和 mapProducer中的类沿用同一个实例
	protected JTextArea jTextArea4buttomConsole;
	protected UnifiedPrinter unifiedPrinter;

	private Class<? extends TabModuleFaceOfVoice> theImplementClass = getClass();

	protected TabModuleFaceOfVoice(IModuleLoader moduleLoader) {
		super(moduleLoader);
	}

	/**
	 * 它自己就是 IModuleLoader，没有模块加载器，所以需要重写这个方法。
	 */
	protected TabModuleFaceOfVoice() {
		this(null);
	}

	protected void initializeTheVoiceTools() {
		voiceTools = new VoiceParameterHandler4DIYTabModuleFace(this) {
			@Override
			protected String getFile4storage() {
				return TabModuleFaceOfVoice.this.getPersistingStorageString4Voice();
			}

			@Override
			public String getExampleText() {
				return TabModuleFaceOfVoice.this.getExampleText();
			}

			@Override
			protected int getNumberOfExamples() {
				return TabModuleFaceOfVoice.this.getNumberOfExamples();
			}
		};
		mapProducer = voiceTools.defaultVoiceInputParamHandler;
	}
	protected String getExampleText() {
		return mapProducer.getExampleString( this);
	}

	protected int getNumberOfExamples() {
		return 1;
	}

	protected String getPersistingStorageString4Voice() {
		String name = theImplementClass.getName();
		return name;
	}

	@Override
	public boolean canImport() {
		return false;
	}

	@Override
	public void importData() {

	}

	@Override
	public boolean canExport() {
		return false;
	}

	@Override
	public void exportData() {

	}

	@Override
	public boolean closeTab() {
		return super.closeTab();
	}

	public JComponent getEnglishDocument() {
		URL resource = getClass().getResource("manual_en.html");
		if (resource == null) {
			return null;
		}
		try {
			return new InformationPanelFactory().getInformationPanelFromResource(resource);
		} catch (IOException e) {
			return null;
		}

	}

	public JComponent getChineseDocument() {
		URL resource = getClass().getResource("manual_zh.html");
		if (resource == null) {
			return null;
		}
		try {
			return new InformationPanelFactory().getInformationPanelFromResource(resource);
		} catch (IOException e) {
			return null;
		}
	}

	@Override
	public ModuleFace getFace() {
		// 注意子类 DockableTabModuleFaceOfVoice 不会调用这个方法，完全是为了模块而创建的
		// 创建一个全新的对象，因为对于VOICE模块来说 ModuleLoader和ModuleFace是相同的，必须要这样操作才能实现不同的Tab有不能的实例
		// 你可能会问为什么要这样设计呢？
		// 看一眼
		// edu.sinh.beauty.unisoft.module.scountmerger就明白了，ModuleLoader和ModuleFace是相同的情况下就能超快开发模块。
		// 我们进行了一定的优化：目的是保证只有调用 getFace方法的时候生成的实例才能调用initialize初始化方法。否则不会生成成员变量。

        try {
            // 获取构造函数
            Constructor<? extends TabModuleFaceOfVoice> constructor = theImplementClass.getDeclaredConstructor();
            // 创建实例
            TabModuleFaceOfVoice newInstance = constructor.newInstance();
            return newInstance;
            // 现在可以使用 newInstance 了
        } catch (InstantiationException e) {
            log.error("无法创建实例: " + e.getMessage());
        } catch (IllegalAccessException e) {
            log.error("构造函数不可访问: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            log.error("未找到无参构造函数: " + e.getMessage());
        } catch (InvocationTargetException e) {
            log.error("构造函数执行时抛出异常: " + e.getCause().getMessage());
        }
		return this;
	}

	@Override
	public void initializeGraphics() {
		if (voiceTools == null) {
			initializeTheVoiceTools();
		}
		this.setParameter(mapProducer);

		JPanel importDataDialog = this.voiceTools.generateImportDataDialogGUI();
		importDataDialog.setBorder(null);

		Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();

		LineBorder lineBorder = new LineBorder(Color.gray, 1, true);
		TitledBorder border = new TitledBorder(lineBorder, "Console: ", TitledBorder.LEADING, TitledBorder.TOP,
				defaultFont.deriveFont(Font.BOLD), Color.black);

		jTextArea4buttomConsole = new JTextArea();
		jTextArea4buttomConsole.setFont(defaultFont);

		unifiedPrinter = new UnifiedPrinter() {
			@Override
			public void clear() {
				SwingUtilities.invokeLater(() -> {
					jTextArea4buttomConsole.append("");
				});
			}

			@Override
			public void print(String text) {
				appendText2Console(text);
			}

			@Override
			public void print(String format, Object... objs) {
				print(EGPSStringUtil.format(format, objs));
			}

			@Override
			public void printAll(Collection<String> texts) {
				StringBuilder sb = new StringBuilder();
				for (String string : texts) {
					sb.append("> ").append(string).append("\n");
				}
				appendText2Console(sb.toString());
			}
 		};

		JScrollPane jScrollPane = new JScrollPane(jTextArea4buttomConsole);

		jScrollPane.setBorder(border);

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false, importDataDialog, jScrollPane);
		sp.setOneTouchExpandable(true);
		sp.setDividerSize(8);

		int height2 = getHeight();
		if (height2 > 0) {
			sp.setDividerLocation((int) (height2 * 0.84));
		} else {
			sp.setDividerLocation(500);
		}

		sp.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		add(sp, BorderLayout.CENTER);

		List<String> list = Arrays.asList(
				"Click the Examples on the left Bookmark Display Panel to see how to use.",
				"Mouse hover on the ? label to see the format statement.",
				"Enter the \"Import and execute\" button to run the module.");
		setText4Console(list);

	}

	public void setText4Console(Collection<String> inputs) {
		StringBuilder sb = new StringBuilder();
		for (String string : inputs) {
			sb.append("> ").append(string).append("\n");
		}

		// 命令行使用的时候是 null
		if (jTextArea4buttomConsole == null) {
			System.out.println(sb);
			return;
		}
		SwingUtilities.invokeLater(() -> {
			jTextArea4buttomConsole.setText(sb.toString());
			jTextArea4buttomConsole.setCaretPosition(0);
		});
	}

	public void appendText2Console(String text) {
		if (jTextArea4buttomConsole == null) {
			System.out.println(text);
			return;
		}
		if (jTextArea4buttomConsole.getText().length() > 10000) {
			return;
		}
		SwingUtilities.invokeLater(() -> {
			jTextArea4buttomConsole.append("> ".concat(text));
		});
	}

	protected UnifiedPrinter getUnifiedPrinter() {
		return unifiedPrinter;
	}

	/**
	 * 配置模块需要什么参数
	 * 
	 * @param designer 框架自己会提供的参数解析器
	 */
	protected abstract void setParameter(AbstractParamsAssignerAndParser4VOICE designer);

	/**
	 * 执行该模块的功能.
	 *
	 * @param o 一个参数解析器，用于解析用户输入的参数.
	 * @throws Exception 如果执行过程中发生任何异常，则抛出该异常.
	 */
	protected abstract void execute(OrganizedParameterGetter o) throws Exception;

	@Override
	public void setParameterOnCommandLine(AbstractParamsAssignerAndParser4VOICE mapProducer){
		setParameter(mapProducer);
	};

	@Override
	public Collection<String> executeOnCommandLine(OrganizedParameterGetter o) throws Exception{
		execute(o);
		return Collections.emptySet();
	}

}
