# eGPS 全局偏好与字体文档

本目录保存的是 `egps-shell` 当前全局偏好与字体系统的读者向文档。

它不是整个仓库的 README。它的职责是说明这个子目录在讲什么、当前入口是什么、以及应该先读哪些文件。

## 本目录覆盖的主题

这里的文档主要聚焦于：

- 当前全局字体配置的运行行为
- 与字体相关的 Preference 面板入口
- 模块与插件如何正确使用这套字体系统
- 这个专题相关的双语 FAQ / History 帮助页
- 该子系统的关键代码位置与维护注意事项

## 文档地图

- `README_zh.md`：目录入口与阅读顺序
- `global_font_system_statement.md` / `global_font_system_statement_zh.md`：当前字体系统的运行行为与生效路径
- `global_font_system_design.md` / `global_font_system_design_zh.md`：当前设计目标与系统边界
- `global_font_system_code_map.md` / `global_font_system_code_map_zh.md`：关键代码位置、集成点与维护坑位
- `how_to_use_global_preference.md` / `how_to_use_global_preference_zh.md`：模块和插件如何正确使用当前全局偏好与字体系统

## 推荐阅读顺序

如果想最快进入这个专题，建议按下面顺序阅读：

1. `global_font_system_statement_zh.md`
2. `global_font_system_design_zh.md`
3. `global_font_system_code_map_zh.md`
4. `how_to_use_global_preference_zh.md`

## 主要运行时依据

本目录描述的当前行为，主要在以下类中实现：

- `LaunchProperty`
- `LaunchPropertyDTO`
- `Launcher`
- `PreferencePanel`
- `ActionFaq`
- `HistoryJTreeDialogEnglish`
- `HistoryJTreeDialogChinese`

## 常见坑

- 新增字体字段时，没有同步到 `applyFontsToUIManager()`
- 改了 Preference 界面，但没有通过 `LaunchPropertyDTO` 正确保存
- 误以为 `UIManager` 会自动修复所有硬编码字体或自绘字体
- FAQ / History 只更新了一个语言版本

## FAQ / History 帮助页

这个专题也通过双语 Help 内容对外提供：

- `ActionFaq` 会根据当前语言加载 `Faq_English.html` 或 `Faq_Chinese.html`
- `HistoryJTreeDialogEnglish` 加载 `History_English.html`
- `HistoryJTreeDialogChinese` 加载 `History_Chinese.html`

维护这些页面时，应确保中英文始终描述同一套当前行为。

## 与 `docs/` 总索引的关系

本目录是 `docs/` 下的一个专题文档簇。

如果需要查看 `egps-shell` 更完整的文档地图，请回到顶层的 `docs/README_TableOfContents.md` 或 `docs/README_TableOfContents_zh.md`。
