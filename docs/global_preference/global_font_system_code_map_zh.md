# 全局字体相关文件位置与集成点说明

本文只保留对读者有价值的稳定信息：当前全局字体系统主要涉及哪些代码位置、这些位置各自负责什么，以及维护时容易踩到哪些坑。

## 1. 主要代码位置

| 位置 | 当前职责 |
| --- | --- |
| `src/egps2/LaunchProperty.java` | 保存全局字体、提供默认值、把字体应用到 `UIManager` |
| `src/egps2/utils/LaunchPropertyDTO.java` | 把字体设置序列化到 JSON |
| `src/egps2/Launcher.java` | 在主界面创建前应用全局字体 |
| `src/egps2/PreferencePanel.java` | 提供字体设置入口，应用修改并刷新界面 |
| `src/egps2/panels/pref/` | 各类字体分组设置面板 |
| `src/egps2/frame/ActionFaq.java` | 根据语言设置加载对应 FAQ 页面 |
| `src/egps2/frame/html/HistoryJTreeDialogEnglish.java` | 英文 History 页面入口 |
| `src/egps2/frame/html/HistoryJTreeDialogChinese.java` | 中文 History 页面入口 |
| `src/egps2/frame/html/Faq_English.html` | 英文 FAQ 页面 |
| `src/egps2/frame/html/Faq_Chinese.html` | 中文 FAQ 页面 |
| `src/egps2/frame/html/History_English.html` | 英文 History 页面 |
| `src/egps2/frame/html/History_Chinese.html` | 中文 History 页面 |

## 2. 当前集成关系

当前全局字体系统不是单个类完成的，而是由几层共同组成：

- `LaunchProperty` 定义字体语义和默认值
- `Launcher` 保证主界面创建前就先把字体注入 `UIManager`
- `PreferencePanel` 提供用户调整入口，并负责把变更重新应用到界面
- FAQ / History 帮助页负责向用户解释这个专题

## 3. FAQ / History 帮助页入口

当前帮助页接入点如下：

- `ActionFaq` 根据 `UnifiedAccessPoint.getLaunchProperty().isEnglish()` 选择 `Faq_English.html` 或 `Faq_Chinese.html`
- `HistoryJTreeDialogEnglish` 加载 `History_English.html`
- `HistoryJTreeDialogChinese` 加载 `History_Chinese.html`

对外说明时，应以上述当前页面为准。

## 4. 维护时常见坑

### 4.1 只改了 `LaunchProperty`，但没有同步到 `UIManager`

如果新增了字体字段，却没有补到 `applyFontsToUIManager()`，那么用户能配置到，但标准 Swing 组件不会自动使用。

### 4.2 只改了 `PreferencePanel`，没有持久化

如果界面上能改，但没有正确写回 `LaunchProperty` 或 `LaunchPropertyDTO`，那么下一次启动就会丢失。

### 4.3 依赖 `UIManager` 处理一切

`UIManager` 对标准组件非常有用，但对下列场景不一定够：

- 自绘文本
- 历史遗留的硬编码字体
- 第三方组件
- HTML 页面中自行指定的样式

### 4.4 修改帮助页时忘了中英文同步

当前 FAQ 与 History 都有中英文版本。只改一边会导致两种语言说明不一致。

### 4.5 在 HTML 页面里继续引用过时的文档角色

FAQ / History 页面里如果要提到 `docs/` 下的文件，应当使用当前文档角色名称，不要再写“summary”“report”“plan”这类历史起草口径。

## 5. 当前建议的阅读顺序

如果要理解这一专题，推荐按下面顺序阅读：

1. `README.md`
2. `global_font_system_statement.md`
3. `global_font_system_design.md`
4. `global_font_system_code_map.md`
5. `how_to_use_global_preference.md`

## 6. 与 README 的分工

`README.md` 负责本目录入口、阅读顺序和维护提示；
本文则更偏“当前哪些文件承担了这套系统的关键职责”。
