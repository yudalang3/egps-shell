# Understanding Module Interfaces & Contracts
更详细列出该类别下的类，含路径与角色说明。

- ## Module definition (developer view)
  - Any class implementing `IModuleSignature` is considered a module. `IModuleLoader` extends `IModuleSignature`, so GUI loaders are modules too.
  - Modules can be CLI-only or GUI modules.
  - GUI modules fall into three buckets: **Mainframe core modules** (fixed modules provided by the main frame), **iTools/independent tools** (built-in tools), and **plug-in modules** (external JARs).
  - Mainframe core + iTools together are treated as **built-in modules**.
  - `ModuleInspector` and Module Gallery can load all GUI module types.
  - Menu mapping: Mainframe core menu for core modules, iTools menu for independent tools, Plug-ins menu for plugin modules (GUI coverage for all three).

- **AdjusterFillAndLine** (`src/egps2/modulei/AdjusterFillAndLine.java`): Component within the eGPS desktop application.
- **AdjusterSizeAndPosition** (`src/egps2/modulei/AdjusterSizeAndPosition.java`): Component within the eGPS desktop application.
- **BaseInformationImp** (`src/egps2/modulei/BaseInformationImp.java`): Component within the eGPS desktop application.
- **Credit** (`src/egps2/modulei/Credit.java`): Component within the eGPS desktop application.
- **CreditBean** (`src/egps2/modulei/CreditBean.java`): Component within the eGPS desktop application.
- **DataOperator** (`src/egps2/modulei/DataOperator.java`): Component within the eGPS desktop application.
- **ICategory** (`src/egps2/modulei/ICategory.java`): Component within the eGPS desktop application.
- **IconBean** (`src/egps2/modulei/IconBean.java`): Component within the eGPS desktop application.
- **IHelp** (`src/egps2/modulei/IHelp.java`): Component within the eGPS desktop application.
- **IInformation** (`src/egps2/modulei/IInformation.java`): Component within the eGPS desktop application.
- **IModuleFace** (`src/egps2/modulei/IModuleFace.java`): Base class or contract for module UI panels; handles lifecycle hooks and tab behaviors.
- **IModuleLoader** (`src/egps2/modulei/IModuleLoader.java`): Component within the eGPS desktop application.
- **IStatistics** (`src/egps2/modulei/IStatistics.java`): Component within the eGPS desktop application.
- **IThreadOperator** (`src/egps2/modulei/IThreadOperator.java`): Component within the eGPS desktop application.
- **ModuleClassification** (`src/egps2/modulei/ModuleClassification.java`): Component within the eGPS desktop application.
- **RunningTask** (`src/egps2/modulei/RunningTask.java`): Component within the eGPS desktop application.
