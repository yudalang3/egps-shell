# eGPS 插件开发教程（Plugin 模式）

## 📌 什么是 Plugin 模式

Plugin 模式是指将模块打包成 JAR 文件，放置到用户配置目录 `~/.egps2/config/plugin/` 中，作为**外部扩展**的方式。

**适用场景**：
- 🔌 第三方开发者发布的工具
- 👥 需要分发给其他用户的功能
- 🧪 实验性功能和原型
- 🎨 自定义工具和扩展

## 🎯 两种开发方式选择

### 方式 1: 继承 FastBaseTemplate（推荐新手）

**适合**：
- 简单的工具型插件
- 快速原型开发
- 单一功能模块
- UI 为主的插件

**代码量**: 约 50-100 行

### 方式 2: 实现 IModuleLoader（推荐进阶）

**适合**：
- 复杂的业务逻辑
- 需要精确控制的功能
- 团队协作开发
- 大型插件项目

**代码量**: 约 150-300 行

---

## 📚 方式 1: 继承 FastBaseTemplate

### 第一步：创建项目结构

```
my-plugin/
├── src/
│   └── com/mycompany/myplugin/
│       └── MySimplePlugin.java
├── build/
└── dist/
```

### 第二步：编写插件类

创建 `src/com/mycompany/myplugin/MySimplePlugin.java`：

```java
package com.mycompany.myplugin;

import egps2.plugin.fastmodtem.FastBaseTemplate;
import javax.swing.*;
import java.awt.*;

/**
 * 我的第一个 eGPS 插件
 *
 * 这是一个简单的文本处理工具示例
 */
public class MySimplePlugin extends FastBaseTemplate {

    private JTextArea inputArea;
    private JTextArea outputArea;

    public MySimplePlugin() {
        super();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部：标题
        JLabel titleLabel = new JLabel("文本处理工具", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel, BorderLayout.NORTH);

        // 中间：输入输出区域
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // 输入面板
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("输入文本"));
        inputArea = new JTextArea();
        inputArea.setLineWrap(true);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);

        // 输出面板
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("处理结果"));
        outputArea = new JTextArea();
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        centerPanel.add(inputPanel);
        centerPanel.add(outputPanel);
        add(centerPanel, BorderLayout.CENTER);

        // 底部：操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton upperCaseBtn = new JButton("转大写");
        upperCaseBtn.addActionListener(e -> processText(String::toUpperCase));

        JButton lowerCaseBtn = new JButton("转小写");
        lowerCaseBtn.addActionListener(e -> processText(String::toLowerCase));

        JButton reverseBtn = new JButton("反转");
        reverseBtn.addActionListener(e -> processText(this::reverseString));

        JButton clearBtn = new JButton("清空");
        clearBtn.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });

        buttonPanel.add(upperCaseBtn);
        buttonPanel.add(lowerCaseBtn);
        buttonPanel.add(reverseBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 处理文本
     */
    private void processText(java.util.function.Function<String, String> processor) {
        String input = inputArea.getText();
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先输入文本！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String output = processor.apply(input);
        outputArea.setText(output);
    }

    /**
     * 反转字符串
     */
    private String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    // ===== 必须实现的方法 =====

    @Override
    public String getTabName() {
        return "文本工具";
    }

    @Override
    public String getShortDescription() {
        return "简单的文本处理工具 - 支持大小写转换和反转";
    }

    @Override
    public int[] getCategory() {
        return ModuleClassification.getOneModuleClassification(
            ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,   // 功能类型
            ModuleClassification.BYAPPLICATION_COMMON_MODULE_INDEX,    // 应用领域
            ModuleClassification.BYCOMPLEXITY_LEVEL_1_INDEX,           // 复杂度
            ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER    // 依赖性
        );
    }
}
```

### 第三步：编译插件

```bash
# 设置变量
EGPS_HOME="/path/to/egps-main.gui"
SRC_DIR="src"
BUILD_DIR="build"

# 创建构建目录
mkdir -p $BUILD_DIR

# 编译
javac -d $BUILD_DIR \
      -cp "$EGPS_HOME/dependency-egps/*:$EGPS_HOME/out/production/egps-main.gui" \
      -encoding UTF-8 \
      $SRC_DIR/com/mycompany/myplugin/MySimplePlugin.java
```

### 第四步：创建配置文件

在 `build` 目录下创建 `eGPS2.plugin.properties`：

```properties
# 插件主类（必需）
launchClass=com.mycompany.myplugin.MySimplePlugin

# 插件名称（必需）
pluginName=My Simple Plugin

# 版本号（必需）
version=1.0.0

# 作者（可选）
author=Your Name

# 描述（可选）
description=A simple text processing tool
```

### 第五步：打包 JAR

```bash
# 进入 build 目录
cd build

# 创建 JAR 文件
jar cvf my-simple-plugin.jar .

# 返回上级目录
cd ..

# 移动到 dist 目录
mkdir -p dist
mv build/my-simple-plugin.jar dist/
```

### 第六步：安装插件

```bash
# 复制到插件目录
cp dist/my-simple-plugin.jar ~/.egps2/config/plugin/
```

### 第七步：测试插件

```bash
# 启动 eGPS
cd $EGPS_HOME
java -cp "./out/production/egps-main.gui:dependency-egps/*" egps2.Launcher

# 按 Ctrl+2 打开 Module Gallery
# 或者从菜单 Plugins → My Simple Plugin
```

---

## 📚 方式 2: 实现 IModuleLoader

### 第一步：创建项目结构

```
my-complex-plugin/
├── src/
│   └── com/mycompany/complexpl​ugin/
│       ├── MyComplexPlugin.java        # 加载器
│       └── MyComplexPluginPanel.java   # 面板
├── build/
└── dist/
```

### 第二步：编写加载器类

创建 `src/com/mycompany/complexpl​ugin/MyComplexPlugin.java`：

```java
package com.mycompany.complexpl​ugin;

import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.frame.ModuleFace;
import javax.swing.*;

/**
 * 插件加载器类
 *
 * 职责：
 * - 实现 IModuleLoader 接口
 * - 提供模块元信息（名称、描述、分类等）
 * - 创建并返回面板实例
 */
public class MyComplexPlugin implements IModuleLoader {

    private MyComplexPluginPanel panel;

    public MyComplexPlugin() {
        // 在构造函数中创建面板
        panel = new MyComplexPluginPanel(this);
    }

    @Override
    public String getTabName() {
        return "复杂数据分析工具";
    }

    @Override
    public String getShortDescription() {
        return "演示复杂插件的开发 - 数据分析、可视化、导入导出";
    }

    @Override
    public ModuleFace getFace() {
        // 返回面板实例
        return panel;
    }

    @Override
    public int[] getCategory() {
        return ModuleClassification.getOneModuleClassification(
            ModuleClassification.BYFUNCTIONALITY_PROFESSIONAL_COMPUTATION_INDEX,  // 功能类型
            ModuleClassification.BYAPPLICATION_GENOMICS_INDEX,                    // 应用领域
            ModuleClassification.BYCOMPLEXITY_LEVEL_3_INDEX,                      // 复杂度
            ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER               // 依赖性
        );
    }

    @Override
    public IconBean getIcon() {
        // 可以返回自定义图标
        // 这里返回 null 使用默认图标
        return null;
    }

    @Override
    public JPanel getEnglishDocument() {
        // 可以提供英文帮助文档
        return createDocumentPanel("English", "This is a complex plugin example...");
    }

    @Override
    public JPanel getChineseDocument() {
        // 可以提供中文帮助文档
        return createDocumentPanel("中文", "这是一个复杂插件示例...");
    }

    /**
     * 创建帮助文档面板
     */
    private JPanel createDocumentPanel(String title, String content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        return panel;
    }
}
```

### 第三步：编写面板类

创建 `src/com/mycompany/complexpl​ugin/MyComplexPluginPanel.java`：

```java
package com.mycompany.complexpl​ugin;

import egps2.frame.ModuleFace;
import egps2.modulei.IModuleLoader;
import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * 插件面板类
 *
 * 职责：
 * - 继承 ModuleFace
 * - 实现具体的 UI 界面
 * - 实现业务逻辑
 * - 处理数据导入导出
 */
public class MyComplexPluginPanel extends ModuleFace {

    private JTextArea dataArea;
    private JTextArea resultArea;
    private boolean hasData = false;

    public MyComplexPluginPanel(IModuleLoader loader) {
        super(loader);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 顶部工具栏
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton importBtn = new JButton("导入数据");
        importBtn.addActionListener(e -> importData());

        JButton exportBtn = new JButton("导出结果");
        exportBtn.addActionListener(e -> exportData());

        JButton analyzeBtn = new JButton("开始分析");
        analyzeBtn.addActionListener(e -> analyzeData());

        toolBar.add(importBtn);
        toolBar.add(exportBtn);
        toolBar.addSeparator();
        toolBar.add(analyzeBtn);

        add(toolBar, BorderLayout.NORTH);

        // 中间分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 左侧：原始数据
        JPanel dataPanel = new JPanel(new BorderLayout());
        dataPanel.setBorder(BorderFactory.createTitledBorder("原始数据"));
        dataArea = new JTextArea();
        dataArea.setLineWrap(true);
        dataPanel.add(new JScrollPane(dataArea), BorderLayout.CENTER);

        // 右侧：分析结果
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("分析结果"));
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        splitPane.setLeftComponent(dataPanel);
        splitPane.setRightComponent(resultPanel);
        splitPane.setDividerLocation(0.5);

        add(splitPane, BorderLayout.CENTER);

        // 底部状态栏
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel statusLabel = new JLabel("就绪");
        statusPanel.add(statusLabel);

        add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     * 分析数据
     */
    private void analyzeData() {
        String data = dataArea.getText();
        if (data.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先输入或导入数据！", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 简单的数据分析示例
        String[] lines = data.split("\n");
        int lineCount = lines.length;
        int wordCount = 0;
        int charCount = data.length();

        for (String line : lines) {
            wordCount += line.split("\\s+").length;
        }

        StringBuilder result = new StringBuilder();
        result.append("=== 数据分析结果 ===\n\n");
        result.append("行数: ").append(lineCount).append("\n");
        result.append("词数: ").append(wordCount).append("\n");
        result.append("字符数: ").append(charCount).append("\n");
        result.append("\n=== 详细信息 ===\n\n");

        for (int i = 0; i < lines.length; i++) {
            result.append("第 ").append(i + 1).append(" 行: ")
                  .append(lines[i].length()).append(" 字符\n");
        }

        resultArea.setText(result.toString());
        hasData = true;
    }

    // ===== 实现 ModuleFace 的抽象方法 =====

    @Override
    public boolean canImport() {
        // 表示这个模块支持导入数据
        return true;
    }

    @Override
    public void importData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择要导入的文本文件");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                dataArea.setText(content.toString());
                hasData = true;
                JOptionPane.showMessageDialog(this, "数据导入成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public boolean canExport() {
        // 只有在有结果时才能导出
        return hasData && !resultArea.getText().isEmpty();
    }

    @Override
    public void exportData() {
        if (!canExport()) {
            JOptionPane.showMessageDialog(this, "没有可导出的数据！", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("选择导出位置");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(resultArea.getText());
                JOptionPane.showMessageDialog(this, "结果导出成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public String[] getFeatureNames() {
        // 返回这个模块提供的功能列表
        return new String[]{
            "文本数据导入",
            "数据统计分析",
            "结果导出"
        };
    }

    @Override
    protected void initializeGraphics() {
        // 如果有图形元素需要初始化，在这里实现
        // 本示例不需要图形功能
    }

    @Override
    public boolean moduleExisted() {
        // 判断模块是否有未保存的数据
        // 如果返回 true，关闭标签页时会弹出确认对话框
        return hasData;
    }
}
```

### 第四步：编译、打包、安装

编译脚本同方式 1，只需要编译多个文件：

```bash
# 编译
javac -d $BUILD_DIR \
      -cp "$EGPS_HOME/dependency-egps/*:$EGPS_HOME/out/production/egps-main.gui" \
      -encoding UTF-8 \
      $SRC_DIR/com/mycompany/complexpl​ugin/*.java
```

配置文件和打包方式相同。

---

## 🎨 进阶技巧

### 1. 使用自定义图标

自定义图标需要在Loader接口类（即插件主类）中实现 `getIcon()` 接口，在这个接口中需要返回一个 `IconBean` 类型的对象。

为此，还需要在文件头引入 `egps2.modulei.IconBean` 。 

```
import egps2.modulei.IconBean;
```

`getIcon()` 是一个虚函数类，在插件主类中实现时需要加上 `@Override` 修饰词。

```java
@Override
public IconBean getIcon() {
    // 从插件 JAR 内部读取图标
    InputStream is = getClass().getResourceAsStream("/asset/icons8-octahedron-96.png");
    if (is == null) {
        // 建议加空指针检查，避免后续 NPE
        throw new RuntimeException("Icon resource not found!");
    }
    // 创建一个IconBean()对象。这一对象不支持在创建时传参，参数传递依赖于后续的方法调用
    IconBean icon = new IconBean();
    icon.setInputStream(is); // 传入InputStream对象，作为icon内容
    icon.setSVG(false); // 如果icon是一个SVG文件，此处设置为true；如果是PNG，则设置为false
    return icon;
}
```

记得将图标文件放到 `build/icons/my-icon.png`。

### 2. 添加第三方依赖

如果你的插件需要第三方库：

```bash
# 方法 1: 将依赖 JAR 解压后一起打包
cd build
jar xf /path/to/dependency.jar
rm -rf META-INF
cd ..

# 方法 2: 使用 Maven/Gradle 打包成 fat JAR
```

### 3. 多语言支持

```java
private String getString(String key) {
    Locale locale = Locale.getDefault();
    if (locale.getLanguage().equals("zh")) {
        return getChineseString(key);
    } else {
        return getEnglishString(key);
    }
}
```

### 4. 保存用户配置

```java
// 配置会保存到 ~/.egps2/config/
Properties props = new Properties();
props.setProperty("lastFile", "/path/to/file");

String configDir = System.getProperty("user.home") + "/.egps2/config/";
File configFile = new File(configDir, "myplugin.properties");
props.store(new FileWriter(configFile), "My Plugin Config");
```

---

## 📦 完整构建脚本示例

创建 `build.sh`：

```bash
#!/bin/bash
set -e

EGPS_HOME="/path/to/egps-main.gui"
PLUGIN_NAME="my-simple-plugin"
VERSION="1.0.0"

echo "🔨 构建 $PLUGIN_NAME v$VERSION"

# 清理
rm -rf build dist
mkdir -p build dist

# 编译
echo "⚙️  编译中..."
javac -d build \
      -cp "$EGPS_HOME/dependency-egps/*:$EGPS_HOME/out/production/egps-main.gui" \
      -encoding UTF-8 \
      src/com/mycompany/myplugin/*.java

# 创建配置
echo "📝 生成配置文件..."
cat > build/eGPS2.plugin.properties << EOF
launchClass=com.mycompany.myplugin.MySimplePlugin
pluginName=$PLUGIN_NAME
version=$VERSION
author=Your Name
EOF

# 打包
echo "📦 打包 JAR..."
cd build
jar cvf $PLUGIN_NAME-$VERSION.jar . > /dev/null 2>&1
cd ..

# 移动到 dist
mv build/$PLUGIN_NAME-$VERSION.jar dist/

echo "✅ 构建完成: dist/$PLUGIN_NAME-$VERSION.jar"

# 可选：自动安装
read -p "是否安装到插件目录? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    cp dist/$PLUGIN_NAME-$VERSION.jar ~/.egps2/config/plugin/
    echo "✅ 已安装到 ~/.egps2/config/plugin/"
fi
```

---

## 🐛 调试技巧

### 1. 查看控制台输出

```java
System.out.println("调试信息: " + someValue);
```

启动 eGPS 时能在控制台看到输出。

### 2. 使用日志

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyPlugin extends FastBaseTemplate {
    private static final Logger logger = LoggerFactory.getLogger(MyPlugin.class);

    public MyPlugin() {
        super();
        logger.info("MyPlugin initialized");
        logger.debug("Debug info: {}", someValue);
    }
}
```

### 3. 异常处理

```java
try {
    // 你的代码
} catch (Exception e) {
    logger.error("Error occurred", e);
    JOptionPane.showMessageDialog(this,
        "错误: " + e.getMessage(),
        "错误",
        JOptionPane.ERROR_MESSAGE);
}
```

---

## ✅ 检查清单

在发布插件前，确保：

- [ ] 插件类正确实现了 `IModuleLoader` 或继承了 `FastBaseTemplate`
- [ ] `eGPS2.plugin.properties` 文件存在且配置正确
- [ ] `launchClass` 指向的类存在且包名正确
- [ ] 所有需要的类和资源文件都打包进 JAR
- [ ] 插件在 eGPS 中能正常加载和运行
- [ ] UI 界面正常显示
- [ ] 所有功能按钮都能正常工作
- [ ] 导入/导出功能正常（如果有）
- [ ] 没有明显的 Bug 或异常
- [ ] 编写了基本的使用说明

---

## 📚 下一步

- [eGPS2.plugin.properties_zh.md](eGPS2.plugin.properties_zh.md) - **配置文件详细说明（推荐阅读）**
- [03_BUILTIN_DEVELOPMENT_zh.md](03_BUILTIN_DEVELOPMENT_zh.md) - 学习如何开发内置模块
- [04_ARCHITECTURE_zh.md](04_ARCHITECTURE_zh.md) - 深入理解 eGPS 模块系统架构
- 参考内置模块源代码: `src/egps2/builtin/modules/`

---

**版本**: eGPS 2.1+
**最后更新**: 2025-12-05
**作者**: eGPS Dev Team
