# 模块接口与契约理解
本节详细列出该类别的类、所在路径和职责描述。

- ## 模块定义（开发者视角）
  - 实现 `IModuleSignature` 的派生类都被视为模块，`IModuleLoader` 继承了 `IModuleSignature`，因此 GUI 加载器也是模块。
  - 模块可以是命令行模块，也可以是 GUI 模块。
  - GUI 模块分三类：**Mainframe core modules**（主框架内置、固定的核心模块）、**iTools/independent tools**（内置独立工具），以及 **plug-in modules**（外部插件）。
  - Mainframe core + iTools 统称为 **built-in 模块**。
  - `ModuleInspector` 与 Module Gallery 都能加载以上所有 GUI 模块。
  - 菜单对应关系：主框架核心模块在 Mainframe core 菜单，独立工具在 iTools 菜单，插件在 Plug-ins 菜单（GUI 三类模块均可从菜单进入）。

- **AdjusterFillAndLine**（`src/egps2/modulei/AdjusterFillAndLine.java`）：eGPS 桌面应用的组成部分。
- **AdjusterSizeAndPosition**（`src/egps2/modulei/AdjusterSizeAndPosition.java`）：eGPS 桌面应用的组成部分。
- **BaseInformationImp**（`src/egps2/modulei/BaseInformationImp.java`）：eGPS 桌面应用的组成部分。
- **Credit**（`src/egps2/modulei/Credit.java`）：eGPS 桌面应用的组成部分。
- **CreditBean**（`src/egps2/modulei/CreditBean.java`）：eGPS 桌面应用的组成部分。
- **DataOperator**（`src/egps2/modulei/DataOperator.java`）：eGPS 桌面应用的组成部分。
- **ICategory**（`src/egps2/modulei/ICategory.java`）：eGPS 桌面应用的组成部分。
- **IconBean**（`src/egps2/modulei/IconBean.java`）：eGPS 桌面应用的组成部分。
- **IHelp**（`src/egps2/modulei/IHelp.java`）：eGPS 桌面应用的组成部分。
- **IInformation**（`src/egps2/modulei/IInformation.java`）：eGPS 桌面应用的组成部分。
- **IModuleFace**（`src/egps2/modulei/IModuleFace.java`）：模块 UI 面板的基类/契约，负责生命周期钩子与标签行为。
- **IModuleLoader**（`src/egps2/modulei/IModuleLoader.java`）：eGPS 桌面应用的组成部分。
- **IStatistics**（`src/egps2/modulei/IStatistics.java`）：eGPS 桌面应用的组成部分。
- **IThreadOperator**（`src/egps2/modulei/IThreadOperator.java`）：eGPS 桌面应用的组成部分。
- **ModuleClassification**（`src/egps2/modulei/ModuleClassification.java`）：eGPS 桌面应用的组成部分。
- **RunningTask**（`src/egps2/modulei/RunningTask.java`）：eGPS 桌面应用的组成部分。
