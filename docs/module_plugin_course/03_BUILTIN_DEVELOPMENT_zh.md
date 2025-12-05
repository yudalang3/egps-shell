# eGPS 内置模块开发教程（Built-in 模式）

## 📌 什么是 Built-in 模式

Built-in 模式是指将模块打包成 JAR 文件，放置到 `dependency-egps` 目录中，作为**内置模块**的方式。

**与 Plugin 模式的关系**：
- ✅ **本质完全相同**：两种方式的 JAR 结构、代码实现完全一样
- 📍 **唯一区别**：存放位置不同
  - Plugin 模式：`~/.egps2/config/plugin/`
  - Built-in 模式：`dependency-egps/`
- 🔄 **可以互换**：同一个 JAR 文件可以在两个位置之间移动

**适用场景**：
- 🏠 核心功能模块
- 📦 默认工具集
- 🔧 与应用一起发布的模块
- ⚡ 需要更快加载的模块

---

## 🎯 核心理念

**重要**：Built-in 模式和 Plugin 模式在开发上没有任何区别！

### 相同点：
- ✅ 都实现 `IModuleLoader` 接口或继承 `FastBaseTemplate`
- ✅ 都需要 `eGPS2.plugin.properties` 配置文件
- ✅ JAR 文件结构完全相同
- ✅ 编译、打包方式完全相同
- ✅ 都会被 `ModuleDiscoveryService` 自动发现
- ✅ 都会出现在 Module Gallery 中

### 不同点：
| 特性 | Plugin 模式 | Built-in 模式 |
|------|------------|--------------|
| **存放位置** | `~/.egps2/config/plugin/` | `dependency-egps/` |
| **菜单位置** | "Plugins" 菜单 | "iTools" 或其他分类菜单 |
| **加载方式** | CustomURLClassLoader | 应用 ClassLoader |
| **发布方式** | 用户自行安装 | 随应用一起发布 |

---

## 📚 开发流程

### 方法 1: 先开发为 Plugin，后转为 Built-in

这是**推荐的方式**，因为：
- 开发阶段可以快速迭代（修改 JAR 后重启即可）
- 测试完成后直接移动 JAR 文件即可

#### 步骤 1: 按照 Plugin 模式开发

参考 [02_PLUGIN_DEVELOPMENT_zh.md](02_PLUGIN_DEVELOPMENT_zh.md)，完整开发你的模块。

#### 步骤 2: 测试

```bash
# 将 JAR 安装到插件目录测试
cp dist/mymodule.jar ~/.egps2/config/plugin/

# 启动 eGPS 测试
java -cp "./out/production/egps-main.gui:dependency-egps/*" egps2.Launcher
```

#### 步骤 3: 转为 Built-in

测试通过后，直接移动 JAR 文件：

```bash
# 从插件目录移除
rm ~/.egps2/config/plugin/mymodule.jar

# 复制到 dependency-egps
cp dist/mymodule.jar dependency-egps/

# 重启 eGPS
java -cp "./out/production/egps-main.gui:dependency-egps/*" egps2.Launcher
```

完成！现在你的模块就是内置模块了。

---

### 方法 2: 直接开发为 Built-in

如果你确定要开发内置模块，可以直接将 JAR 放到 `dependency-egps` 目录。

#### 完整示例：一个内置的"系统信息"工具

**文件结构**：
```
system-info-tool/
├── src/
│   └── com/mycompany/sysinfo/
│       └── SystemInfoTool.java
├── build.sh
└── dist/
```

**SystemInfoTool.java**：

```java
package com.mycompany.sysinfo;

import egps2.plugin.fastmodtem.FastBaseTemplate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * 系统信息工具 - 内置模块示例
 *
 * 显示 Java 运行时环境的系统信息
 */
public class SystemInfoTool extends FastBaseTemplate {

    public SystemInfoTool() {
        super();
        initUI();
        loadSystemInfo();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("系统信息查看器", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // 创建表格
        String[] columnNames = {"属性", "值"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 保存表格引用
        putClientProperty("table", table);

        // 底部按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> loadSystemInfo());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadSystemInfo() {
        JTable table = (JTable) getClientProperty("table");
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // 清空

        // 添加系统属性
        addRow(model, "Java 版本", System.getProperty("java.version"));
        addRow(model, "Java 供应商", System.getProperty("java.vendor"));
        addRow(model, "Java Home", System.getProperty("java.home"));
        addRow(model, "操作系统", System.getProperty("os.name"));
        addRow(model, "操作系统版本", System.getProperty("os.version"));
        addRow(model, "系统架构", System.getProperty("os.arch"));
        addRow(model, "用户名", System.getProperty("user.name"));
        addRow(model, "用户主目录", System.getProperty("user.home"));
        addRow(model, "当前工作目录", System.getProperty("user.dir"));

        // 内存信息
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory() / (1024 * 1024);
        long freeMemory = runtime.freeMemory() / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);

        addRow(model, "总内存", totalMemory + " MB");
        addRow(model, "空闲内存", freeMemory + " MB");
        addRow(model, "最大内存", maxMemory + " MB");
        addRow(model, "CPU 核心数", String.valueOf(runtime.availableProcessors()));
    }

    private void addRow(DefaultTableModel model, String key, String value) {
        model.addRow(new Object[]{key, value});
    }

    @Override
    public String getTabName() {
        return "系统信息";
    }

    @Override
    public String getShortDescription() {
        return "查看 Java 运行环境的系统信息";
    }

    @Override
    public int[] getCategory() {
        // 工具类, 通用, 简单, 无依赖
        return new int[]{0, 0, 0, 0};
    }
}
```

**build.sh**：

```bash
#!/bin/bash
set -e

EGPS_HOME="/path/to/egps-main.gui"
MODULE_NAME="system-info-tool"
VERSION="1.0.0"

echo "🔨 构建内置模块: $MODULE_NAME"

# 清理
rm -rf build dist
mkdir -p build dist

# 编译
echo "⚙️  编译..."
javac -d build \
      -cp "$EGPS_HOME/dependency-egps/*:$EGPS_HOME/out/production/egps-main.gui" \
      -encoding UTF-8 \
      src/com/mycompany/sysinfo/SystemInfoTool.java

# 配置文件（内置模块也需要！）
cat > build/eGPS2.plugin.properties << EOF
launchClass=com.mycompany.sysinfo.SystemInfoTool
pluginName=System Info Tool
version=$VERSION
author=Your Name
description=View Java runtime system information
EOF

# 打包
echo "📦 打包..."
cd build
jar cvf $MODULE_NAME-$VERSION.jar . > /dev/null 2>&1
cd ..

# 移动到 dist
mv build/$MODULE_NAME-$VERSION.jar dist/

# 直接安装到 dependency-egps
echo "📥 安装到 dependency-egps..."
cp dist/$MODULE_NAME-$VERSION.jar $EGPS_HOME/dependency-egps/

echo "✅ 完成！"
echo "重启 eGPS 后，模块将作为内置工具出现。"
```

运行构建：

```bash
chmod +x build.sh
./build.sh
```

---

## 🎨 Built-in 模块的优势

### 1. 更快的加载速度

Built-in 模块使用应用的 ClassLoader，不需要创建新的 URLClassLoader，加载稍快。

### 2. 作为默认工具集

用户安装 eGPS 后，内置模块已经可用，无需额外安装。

### 3. 版本控制

内置模块随应用一起发布，版本同步更容易管理。

### 4. 依赖简化

如果多个模块需要相同的依赖，放在 `dependency-egps` 目录下统一管理更方便。

---

## 🔄 Plugin 与 Built-in 的转换

### 从 Plugin 转为 Built-in

```bash
# 简单移动即可
mv ~/.egps2/config/plugin/mymodule.jar dependency-egps/
```

### 从 Built-in 转为 Plugin

```bash
# 简单移动即可
mv dependency-egps/mymodule.jar ~/.egps2/config/plugin/
```

### 同时支持两种模式

如果你想让用户自由选择，可以：

1. 默认发布到 `dependency-egps`（作为内置）
2. 在文档中说明用户可以移动到 `~/.egps2/config/plugin/`
3. 或者提供安装脚本让用户选择

示例安装脚本：

```bash
#!/bin/bash

JAR_FILE="mymodule-1.0.0.jar"
EGPS_HOME="/path/to/egps-main.gui"

echo "选择安装方式:"
echo "1) 内置模块 (Built-in) - 放到 dependency-egps/"
echo "2) 外部插件 (Plugin) - 放到 ~/.egps2/config/plugin/"
read -p "请选择 (1/2): " choice

case $choice in
    1)
        cp $JAR_FILE $EGPS_HOME/dependency-egps/
        echo "✅ 已安装为内置模块"
        ;;
    2)
        mkdir -p ~/.egps2/config/plugin
        cp $JAR_FILE ~/.egps2/config/plugin/
        echo "✅ 已安装为外部插件"
        ;;
    *)
        echo "❌ 无效选择"
        exit 1
        ;;
esac
```

---

## 📊 Built-in vs Plugin 决策指南

### 选择 Built-in 模式，如果：

- ✅ 模块是核心功能
- ✅ 需要随应用一起发布
- ✅ 希望用户开箱即用
- ✅ 模块是团队内部使用
- ✅ 需要与应用版本同步

### 选择 Plugin 模式，如果：

- ✅ 模块是可选功能
- ✅ 需要独立发布和更新
- ✅ 用户可能不需要这个功能
- ✅ 第三方开发的扩展
- ✅ 实验性功能

### 两者都支持，如果：

- ✅ 让用户自由选择
- ✅ 提供灵活性
- ✅ 不确定最佳方式

---

## 🛠️ 开发建议

### 1. 开发阶段使用 Plugin 模式

```bash
# 开发时放在插件目录，方便测试
cp dist/mymodule.jar ~/.egps2/config/plugin/
```

好处：
- 快速迭代
- 不影响 dependency-egps 目录
- 容易回退

### 2. 发布时转为 Built-in

```bash
# 测试完成后，移动到 dependency-egps
cp dist/mymodule.jar dependency-egps/
```

### 3. 使用构建脚本自动化

在 `build.sh` 中添加：

```bash
# 询问安装方式
read -p "安装为 Built-in (b) 还是 Plugin (p)? " mode

if [ "$mode" = "b" ]; then
    cp dist/$JAR_FILE $EGPS_HOME/dependency-egps/
    echo "✅ 已安装为 Built-in"
elif [ "$mode" = "p" ]; then
    cp dist/$JAR_FILE ~/.egps2/config/plugin/
    echo "✅ 已安装为 Plugin"
fi
```

---

## 🔍 常见问题

### Q: Built-in 模块需要特殊的代码吗？

**A:** 不需要！代码完全一样，只是 JAR 文件的存放位置不同。

### Q: 可以同时在两个位置放同一个模块吗？

**A:** 技术上可以，但不推荐。会导致模块在列表中出现两次。

### Q: Built-in 模块可以卸载吗？

**A:** 可以，直接删除 `dependency-egps` 目录下的 JAR 文件即可。

### Q: 如何更新 Built-in 模块？

**A:** 替换 `dependency-egps` 目录下的 JAR 文件，重启 eGPS。

### Q: Built-in 和 Plugin 的性能差异大吗？

**A:** 几乎没有差异。Built-in 可能在首次加载时稍快（微秒级别），但用户感知不到。

---

## ✅ 总结

### 关键要点：

1. **本质相同**：Built-in 和 Plugin 模式的代码、JAR 结构完全一样
2. **位置决定**：只是存放位置不同，决定了在菜单中的显示位置
3. **可以转换**：两种模式可以自由转换，移动 JAR 文件即可
4. **开发方式**：推荐先作为 Plugin 开发测试，稳定后转为 Built-in
5. **用户选择**：可以让用户自己选择安装方式

### 最佳实践：

```bash
# 开发阶段
开发 → 编译 → 打包 → 安装到 ~/.egps2/config/plugin/ → 测试 → 修改 → 循环

# 发布阶段
测试通过 → 复制到 dependency-egps/ → 随应用一起发布
```

---

## 📚 下一步

- [eGPS2.plugin.properties_zh.md](eGPS2.plugin.properties_zh.md) - 配置文件详细说明
- [04_ARCHITECTURE_zh.md](04_ARCHITECTURE_zh.md) - 深入理解模块系统架构
- 研究现有内置模块的源代码: `src/egps2/builtin/modules/`
- 了解 `ModuleDiscoveryService` 的工作原理

---

**版本**: eGPS 2.1+
**最后更新**: 2025-12-05
**作者**: eGPS Dev Team
