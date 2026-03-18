# 含 `main` 方法的源文件清单

- 统计范围：`src/` 目录下当前真实包含 `public static void main(...)` 的源文件
- 统计时间：2026-03-17
- 统计命令：`rg --line-number "^\s*public\s+static\s+void\s+main\s*\(" .\src`
- 总数：30
- 说明：只统计真实存在的 `main` 方法；已注释掉的旧测试入口不计入

## 1. 生产入口（3）

1. `src/egps2/Launcher.java`：生产模式启动入口，初始化界面、本地化与配置，并可按参数自动加载模块。
2. `src/egps2/Launcher4Dev.java`：开发模式启动入口，设置 `isDev` 后委托给 `Launcher`，便于调试。
3. `src/egps2/builtin/modules/CLI.java`：命令行入口，用于加载和运行 VOICE 计算模块，无需 GUI。

## 2. 演示与样例入口（7）

1. `src/com/raven/main/Main.java`：Raven Dashboard 模板演示入口，未必被主程序集成。
2. `src/demo/dockable/algo/ExpressionProfileRunner.java`：表达谱模拟器示例入口，演示生成不同分组的模拟表达数据。
3. `src/demo/dockable/sigtest/TestRunner.java`：差异表达统计方法演示入口，展示多种基础统计检验的用法。
4. `src/demo/dockable/sigtest/DifferentialExpressionAnalyzer.java`：差异表达分析器示例入口，读取 TSV 表达矩阵并输出分析结果。
5. `src/demo/dockable/sigtest/AdvancedTestRunner.java`：高级差异表达分析演示入口，用于串联测试 Limma、DESeq2、edgeR 风格流程。
6. `src/demo/dockable/sigtest/AdvancedDifferentialExpressionAnalyzer.java`：高级差异表达分析器示例入口，演示多种高级方法的单独运行与比较输出。
7. `src/demo/dockable/richment/EnrichmentAnalysis.java`：富集分析示例入口，支持 ORA 与 GSEA，并输出富集分析结果。

## 3. GUI / 工具自测入口（11）

1. `src/egps2/builtin/modules/filemanager/FileInfoPanel.java`：文件信息面板测试入口。
2. `src/egps2/frame/HintManager.java`：提示气泡与帮助定位测试入口。
3. `src/egps2/frame/gui/EGPSCustomTabbedPaneUI.java`：自定义标签页 UI 测试入口。
4. `src/egps2/panels/DialogUtil.java`：对话框工具测试入口，演示字符串列表展示等功能。
5. `src/egps2/plugin/manager/JarFileUtil.java`：JAR/ZIP 读取测试入口，验证插件资源访问。
6. `src/egps2/utils/common/io/WebReaderUtil.java`：网络读取测试入口，演示 URL 内容抓取。
7. `src/egps2/utils/common/io/ZipFilesWriter.java`：ZIP 写入测试入口，记录压缩写入过程。
8. `src/egps2/utils/common/math/DoubleRanksDecreasingOrder.java`：降序排名算法测试入口。
9. `src/egps2/utils/common/math/matrix/MatrixTriangleOp.java`：三角矩阵转换测试入口。
10. `src/egps2/utils/common/util/topkfinder/TopElementFinder.java`：Top-K 查找算法测试入口。
11. `src/egps2/panels/bugreporter/MailUtil.java`：邮件工具测试入口，用于 DNS 查找 SMTP 等操作。

## 4. 模块发现与反射测试（5）

1. `src/test/egps2/frame/features/ReflectionsDebugTest.java`：Reflections 调试测试入口，检查插件 JAR 扫描与类加载结果。
2. `src/test/egps2/frame/features/ModuleDiscoveryTest.java`：模块发现系统综合测试入口，验证扫描、配置读取与 discovery 合并加载。
3. `src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java`：模块发现过滤规则测试入口，验证 SubTab、核心模块与模板基类排除逻辑。
4. `src/test/egps2/frame/features/IntegratedModuleDiscoveryTest.java`：内置模块与外部插件的一体化发现测试入口。
5. `src/test/egps2/frame/features/FastBaseTemplateDiscoveryTest.java`：FastBaseTemplate 发现规则测试入口，验证模板类排除与插件子类保留策略。

## 5. 其他测试入口（4）

1. `src/test/java/egps2/builtin/modules/bonus/modules/BasicGraphicsUtilsTest.java`：Swing 绘图 API 演示与测试入口。
2. `src/test/java/egps2/panels/bugreporter/SendEmail_text.java`：文本邮件发送测试入口，包含硬编码账号。
3. `src/test/java/egps2/panels/bugreporter/SendEmailSMTP.java`：SMTP 邮件发送测试入口。
4. `src/test/java/egps2/panels/bugreporter/SendEmailAttachment.java`：附件邮件发送测试入口，包含硬编码路径。
