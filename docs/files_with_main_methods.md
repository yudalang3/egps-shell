# Source Files That Define `main(...)`

- Scope: source files under `src/` that currently contain a real `public static void main(...)`
- Snapshot date: `2026-03-17`
- Command used: `rg --line-number "^\s*public\s+static\s+void\s+main\s*\(" .\src`
- Total: `30`
- Note: only real `main` methods are counted; commented-out legacy test entries are excluded

## 1. Production Entry Points (3)

1. `src/egps2/Launcher.java`: production launcher that initializes the UI, localization, and configuration, and can auto-load a module from arguments.
2. `src/egps2/Launcher4Dev.java`: development launcher that sets `isDev` and delegates to `Launcher`.
3. `src/egps2/builtin/modules/CLI.java`: command-line entry for loading and running VOICE computational modules without the GUI.

## 2. Demo and Example Entries (7)

1. `src/com/raven/main/Main.java`: Raven Dashboard template demo entry, not necessarily integrated into the main shell.
2. `src/demo/dockable/algo/ExpressionProfileRunner.java`: expression-profile simulator demo entry for generating grouped mock expression data.
3. `src/demo/dockable/sigtest/TestRunner.java`: demo entry for differential-expression statistics and basic test methods.
4. `src/demo/dockable/sigtest/DifferentialExpressionAnalyzer.java`: example differential-expression analyzer entry that reads a TSV matrix and writes analysis output.
5. `src/demo/dockable/sigtest/AdvancedTestRunner.java`: advanced differential-expression demo entry for chaining Limma-, DESeq2-, and edgeR-style flows.
6. `src/demo/dockable/sigtest/AdvancedDifferentialExpressionAnalyzer.java`: advanced analyzer demo entry for comparing multiple advanced methods.
7. `src/demo/dockable/richment/EnrichmentAnalysis.java`: enrichment-analysis demo entry supporting ORA and GSEA.

## 3. GUI / Tool Self-Test Entries (11)

1. `src/egps2/builtin/modules/filemanager/FileInfoPanel.java`: file-info panel test entry.
2. `src/egps2/frame/HintManager.java`: hint-bubble and help-positioning test entry.
3. `src/egps2/frame/gui/EGPSCustomTabbedPaneUI.java`: custom tabbed-pane UI test entry.
4. `src/egps2/panels/DialogUtil.java`: dialog utility test entry.
5. `src/egps2/plugin/manager/JarFileUtil.java`: JAR/ZIP access test entry for plugin resources.
6. `src/egps2/utils/common/io/WebReaderUtil.java`: URL content reading test entry.
7. `src/egps2/utils/common/io/ZipFilesWriter.java`: ZIP writing test entry.
8. `src/egps2/utils/common/math/DoubleRanksDecreasingOrder.java`: descending-rank algorithm test entry.
9. `src/egps2/utils/common/math/matrix/MatrixTriangleOp.java`: triangular-matrix conversion test entry.
10. `src/egps2/utils/common/util/topkfinder/TopElementFinder.java`: Top-K lookup algorithm test entry.
11. `src/egps2/panels/bugreporter/MailUtil.java`: mail utility test entry for tasks such as SMTP DNS lookup.

## 4. Module Discovery and Reflection Tests (5)

1. `src/test/egps2/frame/features/ReflectionsDebugTest.java`: Reflections debugging entry for plugin JAR scanning and class loading.
2. `src/test/egps2/frame/features/ModuleDiscoveryTest.java`: end-to-end module-discovery test entry.
3. `src/test/egps2/frame/features/ModuleDiscoveryServiceTest.java`: exclusion-rule test entry for SubTabs, core handling, and template-base filtering.
4. `src/test/egps2/frame/features/IntegratedModuleDiscoveryTest.java`: integrated discovery test entry covering built-in modules and external plugins.
5. `src/test/egps2/frame/features/FastBaseTemplateDiscoveryTest.java`: discovery-rule test entry for `FastBaseTemplate` subclasses.

## 5. Other Test Entries (4)

1. `src/test/java/egps2/builtin/modules/bonus/modules/BasicGraphicsUtilsTest.java`: Swing graphics API demo and test entry.
2. `src/test/java/egps2/panels/bugreporter/SendEmail_text.java`: plain-text mail sending test entry with hardcoded account data.
3. `src/test/java/egps2/panels/bugreporter/SendEmailSMTP.java`: SMTP mail sending test entry.
4. `src/test/java/egps2/panels/bugreporter/SendEmailAttachment.java`: attachment mail sending test entry with hardcoded paths.
