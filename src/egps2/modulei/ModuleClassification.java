package egps2.modulei;

/**
 * 模块分类枚举，定义了eGPS框架中模块的多维分类体系。
 * Module classification enum that defines the multi-dimensional classification system for modules in the eGPS framework.
 *
 * <p>此枚举提供了四个维度的模块分类，帮助组织和管理不同类型的模块：
 * This enum provides four dimensions of module classification to help organize and manage different types of modules:
 *
 * <p><strong>分类维度：</strong>
 * Classification dimensions:
 * <ol>
 *   <li><b>按功能 ({@link #ByFunctionality})</b> - 模块的主要功能类型
 *       <ul>
 *         <li>复杂可视化 (Complicated visualization)</li>
 *         <li>基础可视化 (Primitive visualization)</li>
 *         <li>简单工具 (Simple tools)</li>
 *         <li>专业计算 (Professional computation)</li>
 *         <li>操作工作台 (Operational workbench)</li>
 *         <li>流程组织器 (Pipeline organizer)</li>
 *       </ul>
 *   </li>
 *   <li><b>按应用领域 ({@link #ByApplication})</b> - 模块的应用场景
 *       <ul>
 *         <li>通用模块 (Common module)</li>
 *         <li>进化分析 (Evolution)</li>
 *         <li>群体遗传学 (Population Genetics)</li>
 *         <li>统计分析 (Statistics)</li>
 *         <li>可视化 (Visualization)</li>
 *         <li>基因组学 (Genomics)</li>
 *         <li>转录组学 (Transcriptomics)</li>
 *         <li>蛋白质组学 (Proteomics)</li>
 *       </ul>
 *   </li>
 *   <li><b>按复杂度 ({@link #ByComplexity})</b> - 模块的实现复杂度（Level 1-6）</li>
 *   <li><b>按依赖 ({@link #ByDependency})</b> - 模块对框架功能的依赖程度
 *       <ul>
 *         <li>仅使用容器 (Only employ container)</li>
 *         <li>调用voice导入框架 (voice import frame invoked)</li>
 *         <li>利用余量/反馈 (Remainder/feedback utilized)</li>
 *         <li>使用计算机制 (Computational mechanism employed)</li>
 *         <li>跨模块引用 (Cross module referenced)</li>
 *         <li>调用全部功能 (Full features invoked)</li>
 *       </ul>
 *   </li>
 * </ol>
 *
 * <p><strong>使用方法：</strong>
 * Usage:
 * <pre>{@code
 * // 在模块的 getCategory() 方法中使用
 * int[] category = ModuleClassification.getOneModuleClassification(
 *     ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
 *     ModuleClassification.BYAPPLICATION_COMMON_MODULE_INDEX,
 *     ModuleClassification.BYCOMPLEXITY_LEVEL_1_INDEX,
 *     ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER
 * );
 * return category;
 * }</pre>
 *
 * <p><strong>重要提示：</strong>
 * Important notes:
 * <ul>
 *   <li>不要手动创建分类数组，必须使用 {@link #getOneModuleClassification(int...)} 方法</li>
 *   <li>该方法会验证索引的有效性，防止越界错误</li>
 *   <li>Do not manually create classification arrays, must use {@link #getOneModuleClassification(int...)} method</li>
 *   <li>This method validates index validity to prevent out-of-bounds errors</li>
 * </ul>
 *
 * <p>线程安全：此枚举是线程安全的，可以在多线程环境中安全使用。
 * Thread safety: This enum is thread-safe and can be safely used in multi-threaded environments.
 *
 * @see ICategory
 * @author eGPS Dev Team
 * @since 2.1
 */
public enum ModuleClassification {

	ByFunctionality {
		final String[] NAMES_CATEGORY1 = { "Complicated visualization ", "Primitive visualization", "Simple tools ",
				"Professional computation ", "Operational workbench", "Pipeline organizer" };

		@Override
		public String[] getNameStrings() {
			return NAMES_CATEGORY1;
		}

		@Override
		public int getSize() {
			return getNameStrings().length;
		}

		@Override
		public String getCategory() {
			return "By functionality";
		}

	},

	ByApplication {
		final String[] NAMES_CATEGORY2 = { "Common Utils ", "Evolution ", "Population Genetics ", "Statistics ",
				"Visualization ", "Genomics ", "Transcriptomics ", "Proteomics " };

		@Override
		public String[] getNameStrings() {
			return NAMES_CATEGORY2;
		}

		@Override
		public int getSize() {
			return getNameStrings().length;
		}

		@Override
		public String getCategory() {
			return "By application";
		}
	},

	ByComplexity {
		final String[] NAMES_CATEGORY3 = { "Level 1 ", "Level 2 ", "Level 3 ","Level 4 ","Level 5 ",
				"Level 6 " };

		@Override
		public String[] getNameStrings() {
			return NAMES_CATEGORY3;
		}

		@Override
		public int getSize() {
			return getNameStrings().length;
		}

		@Override
		public String getCategory() {
			return "By complexity";
		}
	},
	ByDependency {
		final String[] NAMES_CATEGORY4 = { "Only employ container ", "voice import frame invoked ",
				 "Remainder/feedback utilized ", "Computational mechanism employed ", "Cross module referenced ",
				"Full features invoked " };
		
		@Override
		public String[] getNameStrings() {
			return NAMES_CATEGORY4;
		}
		
		@Override
		public int getSize() {
			return getNameStrings().length;
		}
		
		@Override
		public String getCategory() {
			return "By dependency";
		}
	};

	public abstract String[] getNameStrings();

	public abstract int getSize();
	
	public abstract String getCategory();

	public static ModuleClassification getClassificationAccordingToIndex(int index) {
		switch (index) {
		case 0:
			return ByFunctionality;
		case 1:
			return ByApplication;
		case 2:
			return ByComplexity;
		case 3:
			return ByDependency;
		default:
			throw new IllegalArgumentException("Only three categories.");
		}
	}

	public static int[] getOneModuleClassification(int... cal) {
		
		int length = cal.length;
		for (int i = 0; i < length; i++) {
			ModuleClassification classificationAccordingToIndex = getClassificationAccordingToIndex(i);
			if (cal[i] >= classificationAccordingToIndex.getSize()) {
				throw new IllegalArgumentException(classificationAccordingToIndex.getCategory().concat(" Your index of category exceed the predefined values."));
			}
			
		}
		return cal;

	}

	public static final int BYFUNCTIONALITY_COMPLICATED_VISUALIZATION_INDEX = 0;
	public static final int BYFUNCTIONALITY_PRIMITIVE_VISUALIZATION_INDEX = 1;
	public static final int BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX = 2;
	public static final int BYFUNCTIONALITY_PROFESSIONAL_COMPUTATION_INDEX = 3;
	public static final int BYFUNCTIONALITY_OPERATIONAL_WORKBENCH_INDEX = 4;
	public static final int BYFUNCTIONALITY_PIPELINE_ORGANIZER_INDEX = 5;

	public static final int BYAPPLICATION_COMMON_MODULE_INDEX = 0;
	public static final int BYAPPLICATION_EVOLUTION_INDEX = 1;
	public static final int BYAPPLICATION_POPULATION_GENETICS_INDEX = 2;
	public static final int BYAPPLICATION_STATISTICS_INDEX = 3;
	public static final int BYAPPLICATION_VISUALIZATION_INDEX = 4;
	public static final int BYAPPLICATION_GENOMICS_INDEX = 5;
	public static final int BYAPPLICATION_TRANSCRIPTOMICS_INDEX = 6;
	public static final int BYAPPLICATION_PROTEOMICS_INDEX = 7;

	public static final int BYCOMPLEXITY_LEVEL_1_INDEX = 0;
	public static final int BYCOMPLEXITY_LEVEL_2_INDEX = 1;
	public static final int BYCOMPLEXITY_LEVEL_3_INDEX = 2;
	public static final int BYCOMPLEXITY_LEVEL_4_INDEX = 3;
	public static final int BYCOMPLEXITY_LEVEL_5_INDEX = 4;
	public static final int BYCOMPLEXITY_LEVEL_6_INDEX = 5;
	
	public static final int BYDEPENDENCY_ONLY_EMPLOY_CONTAINER = 0;
	public static final int BYDEPENDENCY_voice_INVOKED = 1;
	public static final int BYDEPENDENCY_MAINFRAME_FEEDBACK_REMAINDER_INVOKED = 2;
	public static final int BYDEPENDENCY_COMPUTATIONAL_MECHANISM_EMPLOYED = 3;
	public static final int BYDEPENDENCY_CROSS_MODULE_REFERENCED  = 4;
	public static final int BYDEPENDENCY_FULL_FEATURES_INVOKED = 5;
}
