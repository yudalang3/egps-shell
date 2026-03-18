# 全局字体系统设计与边界说明

本文说明当前 `egps-shell` 中全局字体系统的设计目标、实际作用范围和当前边界。

## 1. 设计目标

当前全局字体系统的目标是：

- 让壳层为常见 Swing 组件提供统一的语义化字体入口
- 让用户可以在 Preference 中按类别调整字体
- 让当前设置能持久化并在后续启动时恢复
- 让大部分标准 Swing 组件通过 `UIManager` 自动继承这些设置

## 2. 当前字体分组

当前 `LaunchProperty` 中已经按用途维护多组字体，包括：

- 菜单字体
- Tab 字体
- 模块标题与正文字体
- 文档字体
- 对话框字体
- 基础组件字体
- 输入组件字体
- 数据展示组件字体
- 工具提示与工具栏字体
- 进度条、滑块、微调框、滚动容器等其它组件字体

这些分组不是为了追求“字体项越多越好”，而是为了让界面上的常见语义位置有稳定入口。

## 3. 当前应用路径

当前实现的应用路径是：

1. `LaunchProperty` 保存各类字体对象
2. `Launcher` 在主界面创建前调用 `applyFontsToUIManager()`
3. `PreferencePanel.applyAndClose()` 在用户应用设置时：
   - 读取各配置面板中的值
   - 更新 `LaunchProperty`
   - 再次调用 `applyFontsToUIManager()`
   - 通过 `SwingUtilities.updateComponentTreeUI(...)` 刷新当前主界面
   - 将设置写回 `defaultGlobalProperties.json`

## 4. 当前持久化方式

当前字体设置通过 `LaunchPropertyDTO` 序列化，并保存到：

- `~/.egps/json/defaultGlobalProperties.json`

当前实现仍然沿用这条历史路径，因此相关文档不应把它改写成新的配置位置。

## 5. 当前 Preference 入口

当前用户主要通过 `File -> Preference -> Appearance` 调整字体。

当前 `PreferencePanel` 中已经提供了这些面板或入口：

- 模块字体
- 菜单字体
- Tab 字体
- 文档字体
- 对话框字体
- 基础组件字体
- 输入组件字体
- 数据展示字体
- 外观与图标尺寸等相邻设置

## 6. 当前系统边界

当前全局字体系统应按下面的边界理解：

- 它优先覆盖标准 Swing 组件和壳层自己维护的通用界面
- 第三方组件、手工硬编码字体的界面、或自行绘制文本的代码，可能仍需要开发者额外处理
- `UIManager` 的统一设置能覆盖大量默认组件，但不能保证所有历史代码都自动无缝继承
- 文本区域默认使用等宽字体，这是当前实现的有意选择

## 7. 对模块开发者的影响

如果模块想和当前全局字体系统保持一致，推荐：

- 通过 `UnifiedAccessPoint.getLaunchProperty()` 取语义化字体
- 对标准 Swing 组件尽量不要再写死字体
- 对自绘文本或特殊组件，明确使用对应语义字体

## 8. 相关文档

- `README.md`：本目录入口与阅读顺序
- `global_font_system_statement.md`：当前系统能力与运行行为
- `global_font_system_code_map.md`：当前相关代码位置与集成点
- `how_to_use_global_preference.md`：模块和插件如何正确使用这些字体入口
- `global_font_system_code_map.md`：FAQ / History 双语帮助页与本系统的接入点和维护规则
