#!/bin/bash

# eGPS 插件自动生成脚本
# 功能：创建两种类型的测试插件，并将生成的 JAR 文件放到 plug_dist 目录
# 类型1: 继承 FastBaseTemplate（简单快速）
# 类型2: 直接实现 IModuleLoader（灵活强大）

set -e

echo "=========================================="
echo "eGPS 插件自动生成工具 v2.1"
echo "=========================================="
echo ""

# 获取脚本所在目录的父目录（即项目根目录）
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"

cd "$PROJECT_ROOT"

WORK_DIR="temp_plugin_build"
DIST_DIR="plug_dist"

# 清理并创建目录
rm -rf "$WORK_DIR"
rm -rf "$DIST_DIR"
mkdir -p "$WORK_DIR"
mkdir -p "$DIST_DIR"

echo "工作目录: $WORK_DIR"
echo "输出目录: $DIST_DIR"
echo ""

# ============================================================
# Plugin 1: 继承 FastBaseTemplate（简单工具型插件）
# ============================================================
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦 创建插件 1: FastBase Plugin (简单型)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "  实现方式: 继承 FastBaseTemplate"
echo "  适用场景: 简单工具、快速开发"
echo "  代码量:   约 70 行"
echo ""

mkdir -p "$WORK_DIR/plugin1/test/fastbase"

cat > "$WORK_DIR/plugin1/test/fastbase/FastBasePlugin.java" << 'EOF'
package test.fastbase;

import egps2.plugin.fastmodtem.FastBaseTemplate;
import javax.swing.*;
import java.awt.*;

/**
 * 示例插件 - 继承 FastBaseTemplate
 *
 * 优点：
 * - 代码量少（约70行）
 * - 快速开发
 * - 自动继承 ModuleFace 所有功能
 *
 * 适用场景：
 * - 简单工具型插件
 * - 快速原型开发
 * - 单一功能模块
 */
public class FastBasePlugin extends FastBaseTemplate {

    public FastBasePlugin() {
        super();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 创建信息面板
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("插件信息"));

        infoPanel.add(new JLabel("✓ 类型：继承 FastBaseTemplate"));
        infoPanel.add(new JLabel("✓ 包名：test.fastbase"));
        infoPanel.add(new JLabel("✓ 特点：代码简洁，快速开发"));
        infoPanel.add(new JLabel("✓ 位置：会自动出现在 Module Gallery"));

        add(infoPanel, BorderLayout.NORTH);

        // 创建功能面板
        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBorder(BorderFactory.createTitledBorder("功能演示"));

        JButton button = new JButton("测试功能");
        button.addActionListener(e ->
            JOptionPane.showMessageDialog(this,
                "FastBaseTemplate 插件正常工作！\n\n" +
                "这是一个简单但功能完整的插件示例。",
                "成功",
                JOptionPane.INFORMATION_MESSAGE)
        );
        actionPanel.add(button);

        add(actionPanel, BorderLayout.CENTER);

        // 添加说明
        JTextArea helpText = new JTextArea(
            "开发提示：\n" +
            "1. 继承 FastBaseTemplate 是最快速的插件开发方式\n" +
            "2. 只需实现 getTabName()、getShortDescription()、getCategory()\n" +
            "3. 在构造函数中构建 UI 即可\n" +
            "4. 适合工具型、单一功能的插件"
        );
        helpText.setEditable(false);
        helpText.setBackground(infoPanel.getBackground());
        helpText.setBorder(BorderFactory.createTitledBorder("开发说明"));
        add(new JScrollPane(helpText), BorderLayout.SOUTH);
    }

    @Override
    public String getTabName() {
        return "FastBase Plugin";
    }

    @Override
    public String getShortDescription() {
        return "示例插件 - 继承 FastBaseTemplate（简单快速型）";
    }

    @Override
    public int[] getCategory() {
        return new int[]{0, 0, 0, 0}; // 工具类, 通用, 简单, 无依赖
    }
}
EOF

# 编译 Plugin 1
echo "  ⚙️  编译中..."
javac -d "$WORK_DIR/plugin1" \
      -cp "dependency-egps/*:out/production/egps-main.gui" \
      "$WORK_DIR/plugin1/test/fastbase/FastBasePlugin.java"

# 创建配置文件
cat > "$WORK_DIR/plugin1/eGPS2.plugin.properties" << 'EOF'
launchClass=test.fastbase.FastBasePlugin
pluginName=FastBase Plugin
version=1.0.0
author=eGPS Dev Team
description=示例插件 - 继承 FastBaseTemplate
EOF

# 打包
echo "  📦 打包中..."
cd "$WORK_DIR/plugin1"
jar cvf fastbase-plugin.jar . > /dev/null 2>&1
cd ../..

# 复制到 dist 目录
cp "$WORK_DIR/plugin1/fastbase-plugin.jar" "$DIST_DIR/"
echo "  ✓ Plugin 1 创建完成 → $DIST_DIR/fastbase-plugin.jar"
echo ""

# ============================================================
# Plugin 2: 直接实现 IModuleLoader（复杂灵活型插件）
# ============================================================
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦 创建插件 2: Direct Plugin (复杂型)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "  实现方式: 直接实现 IModuleLoader"
echo "  适用场景: 复杂功能、团队协作"
echo "  代码量:   约 200 行"
echo ""

mkdir -p "$WORK_DIR/plugin2/test/direct"

cat > "$WORK_DIR/plugin2/test/direct/DirectPlugin.java" << 'EOF'
package test.direct;

import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.frame.ModuleFace;
import javax.swing.*;
import java.awt.*;

/**
 * 示例插件 - 直接实现 IModuleLoader
 *
 * 优点：
 * - 完全控制模块行为
 * - 架构清晰（加载器与面板分离）
 * - 便于团队协作
 * - 适合大型复杂插件
 *
 * 适用场景：
 * - 复杂业务逻辑
 * - 需要精确控制的插件
 * - 团队协作开发
 */
public class DirectPlugin implements IModuleLoader {

    private DirectPluginPanel panel;

    public DirectPlugin() {
        panel = new DirectPluginPanel(this);
    }

    @Override
    public String getTabName() {
        return "Direct IModuleLoader Plugin";
    }

    @Override
    public String getShortDescription() {
        return "示例插件 - 直接实现 IModuleLoader 接口（灵活强大型）";
    }

    @Override
    public ModuleFace getFace() {
        return panel;
    }

    @Override
    public int[] getCategory() {
        return new int[]{1, 1, 0, 0}; // 分析类, 生物信息, 简单, 无依赖
    }

    @Override
    public IconBean getIcon() {
        return null; // 可以返回自定义图标
    }

    @Override
    public JPanel getEnglishDocument() {
        return null; // 可以提供英文文档
    }

    @Override
    public JPanel getChineseDocument() {
        return null; // 可以提供中文文档
    }

    /**
     * 插件面板类 - 继承 ModuleFace
     * 这里是插件的实际 UI 实现
     */
    static class DirectPluginPanel extends ModuleFace {

        public DirectPluginPanel(IModuleLoader loader) {
            super(loader);
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // 创建信息面板
            JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            infoPanel.setBorder(BorderFactory.createTitledBorder("插件信息"));

            infoPanel.add(new JLabel("✓ 类型：直接实现 IModuleLoader"));
            infoPanel.add(new JLabel("✓ 包名：test.direct"));
            infoPanel.add(new JLabel("✓ 特点：架构清晰，灵活强大"));
            infoPanel.add(new JLabel("✓ 位置：会自动出现在 Module Gallery"));

            add(infoPanel, BorderLayout.NORTH);

            // 创建功能面板
            JPanel actionPanel = new JPanel(new FlowLayout());
            actionPanel.setBorder(BorderFactory.createTitledBorder("功能演示"));

            JButton button = new JButton("测试功能");
            button.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                    "IModuleLoader 插件正常工作！\n\n" +
                    "这种方式提供了最大的灵活性和控制力。",
                    "成功",
                    JOptionPane.INFORMATION_MESSAGE)
            );
            actionPanel.add(button);

            add(actionPanel, BorderLayout.CENTER);

            // 添加说明
            JTextArea helpText = new JTextArea(
                "开发提示：\n" +
                "1. IModuleLoader 方式适合复杂插件开发\n" +
                "2. 加载器（Loader）和面板（Panel）分离，架构清晰\n" +
                "3. 可以完全控制模块的各个方面\n" +
                "4. 便于团队协作，职责明确\n" +
                "5. 适合需要复杂业务逻辑的插件"
            );
            helpText.setEditable(false);
            helpText.setBackground(infoPanel.getBackground());
            helpText.setBorder(BorderFactory.createTitledBorder("开发说明"));
            add(new JScrollPane(helpText), BorderLayout.SOUTH);
        }

        @Override
        public boolean canImport() { return false; }

        @Override
        public void importData() {}

        @Override
        public boolean canExport() { return false; }

        @Override
        public void exportData() {}

        @Override
        public String[] getFeatureNames() { return null; }

        @Override
        protected void initializeGraphics() {}
    }
}
EOF

# 编译 Plugin 2
echo "  ⚙️  编译中..."
javac -d "$WORK_DIR/plugin2" \
      -cp "dependency-egps/*:out/production/egps-main.gui" \
      "$WORK_DIR/plugin2/test/direct/DirectPlugin.java"

# 创建配置文件
cat > "$WORK_DIR/plugin2/eGPS2.plugin.properties" << 'EOF'
launchClass=test.direct.DirectPlugin
pluginName=Direct Plugin
version=1.0.0
author=eGPS Dev Team
description=示例插件 - 直接实现 IModuleLoader
EOF

# 打包
echo "  📦 打包中..."
cd "$WORK_DIR/plugin2"
jar cvf direct-plugin.jar . > /dev/null 2>&1
cd ../..

# 复制到 dist 目录
cp "$WORK_DIR/plugin2/direct-plugin.jar" "$DIST_DIR/"
echo "  ✓ Plugin 2 创建完成 → $DIST_DIR/direct-plugin.jar"
echo ""

# ============================================================
# Plugin 3: Simple Calculator（实用工具 - 计算器）
# ============================================================
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦 创建插件 3: Simple Calculator (实用型)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "  实现方式: 实现 IModuleLoader + 中英文文档"
echo "  适用场景: 实用工具、带文档的插件"
echo "  代码量:   约 400 行"
echo ""

mkdir -p "$WORK_DIR/plugin3/test/calculator"

cat > "$WORK_DIR/plugin3/test/calculator/SimpleCalculator.java" << 'EOF'
package test.calculator;

import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.frame.ModuleFace;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Simple Calculator Plugin - Practical utility tool
 *
 * Features:
 * - Basic arithmetic operations (+, -, *, /)
 * - Clear and backspace functions
 * - Keyboard support
 * - English and Chinese documentation
 */
public class SimpleCalculator implements IModuleLoader {

    private CalculatorPanel panel;

    public SimpleCalculator() {
        panel = new CalculatorPanel(this);
    }

    @Override
    public String getTabName() {
        return "Simple Calculator";
    }

    @Override
    public String getShortDescription() {
        return "A simple calculator for basic arithmetic operations";
    }

    @Override
    public ModuleFace getFace() {
        return panel;
    }

    @Override
    public int[] getCategory() {
        return new int[]{0, 0, 0, 0}; // Tool, General, Simple, No dependencies
    }

    @Override
    public IconBean getIcon() {
        return null;
    }

    @Override
    public JPanel getEnglishDocument() {
        return createDocumentPanel(
            "Simple Calculator - English Manual",
            "<html><body style='padding: 10px; font-family: Arial, sans-serif;'>" +
            "<h2>Simple Calculator</h2>" +
            "<p>A basic calculator for arithmetic operations.</p>" +
            "<h3>Features:</h3>" +
            "<ul>" +
            "<li><b>Basic Operations:</b> Addition (+), Subtraction (-), Multiplication (*), Division (/)</li>" +
            "<li><b>Clear Function:</b> C button clears the display</li>" +
            "<li><b>Backspace:</b> ← button deletes the last digit</li>" +
            "<li><b>Keyboard Support:</b> Use number keys and operators</li>" +
            "</ul>" +
            "<h3>Usage:</h3>" +
            "<ol>" +
            "<li>Enter first number using buttons or keyboard</li>" +
            "<li>Click or type an operator (+, -, *, /)</li>" +
            "<li>Enter second number</li>" +
            "<li>Press = or Enter to see the result</li>" +
            "</ol>" +
            "<h3>Keyboard Shortcuts:</h3>" +
            "<ul>" +
            "<li><b>0-9:</b> Number input</li>" +
            "<li><b>+, -, *, /:</b> Operators</li>" +
            "<li><b>Enter or =:</b> Calculate result</li>" +
            "<li><b>Escape or C:</b> Clear</li>" +
            "<li><b>Backspace:</b> Delete last digit</li>" +
            "</ul>" +
            "</body></html>"
        );
    }

    @Override
    public JPanel getChineseDocument() {
        return createDocumentPanel(
            "简易计算器 - 中文手册",
            "<html><body style='padding: 10px; font-family: 微软雅黑, Arial, sans-serif;'>" +
            "<h2>简易计算器</h2>" +
            "<p>一个用于基本算术运算的计算器。</p>" +
            "<h3>功能特性：</h3>" +
            "<ul>" +
            "<li><b>基本运算：</b>加法(+)、减法(-)、乘法(*)、除法(/)</li>" +
            "<li><b>清除功能：</b>C 按钮清空显示</li>" +
            "<li><b>退格功能：</b>← 按钮删除最后一位数字</li>" +
            "<li><b>键盘支持：</b>可以使用数字键和运算符键</li>" +
            "</ul>" +
            "<h3>使用方法：</h3>" +
            "<ol>" +
            "<li>使用按钮或键盘输入第一个数字</li>" +
            "<li>点击或输入运算符 (+, -, *, /)</li>" +
            "<li>输入第二个数字</li>" +
            "<li>按 = 或回车查看结果</li>" +
            "</ol>" +
            "<h3>键盘快捷键：</h3>" +
            "<ul>" +
            "<li><b>0-9:</b> 数字输入</li>" +
            "<li><b>+, -, *, /:</b> 运算符</li>" +
            "<li><b>回车或 =:</b> 计算结果</li>" +
            "<li><b>Esc 或 C:</b> 清除</li>" +
            "<li><b>退格键:</b> 删除最后一位</li>" +
            "</ul>" +
            "</body></html>"
        );
    }

    private JPanel createDocumentPanel(String title, String htmlContent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Calculator Panel Implementation
     */
    static class CalculatorPanel extends ModuleFace {
        private JTextField display;
        private double firstNumber = 0;
        private String operator = "";
        private boolean startNewNumber = true;

        public CalculatorPanel(IModuleLoader loader) {
            super(loader);
            initUI();
            setupKeyboardListener();
        }

        private void initUI() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // Display
            display = new JTextField("0");
            display.setFont(new Font("Monospaced", Font.BOLD, 24));
            display.setHorizontalAlignment(JTextField.RIGHT);
            display.setEditable(false);
            display.setBackground(Color.WHITE);
            display.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            add(display, BorderLayout.NORTH);

            // Button panel
            JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));

            String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "C", "←", "", ""
            };

            for (String text : buttons) {
                if (text.isEmpty()) {
                    buttonPanel.add(new JLabel()); // Empty cell
                } else {
                    JButton btn = createButton(text);
                    buttonPanel.add(btn);
                }
            }

            add(buttonPanel, BorderLayout.CENTER);
        }

        private JButton createButton(String text) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setFocusPainted(false);

            // Color coding
            if (text.matches("[0-9.]")) {
                button.setBackground(new Color(240, 240, 240));
            } else if (text.matches("[+\\-*/=]")) {
                button.setBackground(new Color(255, 200, 100));
            } else {
                button.setBackground(new Color(200, 200, 200));
            }

            button.addActionListener(e -> handleButtonClick(text));
            return button;
        }

        private void setupKeyboardListener() {
            KeyListener keyListener = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    char key = e.getKeyChar();
                    int code = e.getKeyCode();

                    if (Character.isDigit(key) || key == '.') {
                        handleButtonClick(String.valueOf(key));
                    } else if (key == '+' || key == '-' || key == '*' || key == '/') {
                        handleButtonClick(String.valueOf(key));
                    } else if (key == '\n' || key == '=') {
                        handleButtonClick("=");
                    } else if (code == KeyEvent.VK_ESCAPE || key == 'c' || key == 'C') {
                        handleButtonClick("C");
                    } else if (code == KeyEvent.VK_BACK_SPACE) {
                        handleButtonClick("←");
                    }
                }
            };

            addKeyListener(keyListener);
            setFocusable(true);
        }

        private void handleButtonClick(String text) {
            if (text.matches("[0-9.]")) {
                handleNumber(text);
            } else if (text.matches("[+\\-*/]")) {
                handleOperator(text);
            } else if (text.equals("=")) {
                handleEquals();
            } else if (text.equals("C")) {
                handleClear();
            } else if (text.equals("←")) {
                handleBackspace();
            }
            requestFocusInWindow();
        }

        private void handleNumber(String num) {
            if (startNewNumber) {
                display.setText(num.equals(".") ? "0." : num);
                startNewNumber = false;
            } else {
                String current = display.getText();
                if (num.equals(".") && current.contains(".")) {
                    return; // Only one decimal point
                }
                display.setText(current + num);
            }
        }

        private void handleOperator(String op) {
            if (!operator.isEmpty()) {
                handleEquals();
            }
            firstNumber = Double.parseDouble(display.getText());
            operator = op;
            startNewNumber = true;
        }

        private void handleEquals() {
            if (operator.isEmpty()) return;

            double secondNumber = Double.parseDouble(display.getText());
            double result = 0;

            switch (operator) {
                case "+": result = firstNumber + secondNumber; break;
                case "-": result = firstNumber - secondNumber; break;
                case "*": result = firstNumber * secondNumber; break;
                case "/":
                    if (secondNumber == 0) {
                        display.setText("Error");
                        operator = "";
                        startNewNumber = true;
                        return;
                    }
                    result = firstNumber / secondNumber;
                    break;
            }

            display.setText(formatResult(result));
            operator = "";
            startNewNumber = true;
        }

        private void handleClear() {
            display.setText("0");
            firstNumber = 0;
            operator = "";
            startNewNumber = true;
        }

        private void handleBackspace() {
            String current = display.getText();
            if (current.length() > 1) {
                display.setText(current.substring(0, current.length() - 1));
            } else {
                display.setText("0");
                startNewNumber = true;
            }
        }

        private String formatResult(double result) {
            if (result == (long) result) {
                return String.valueOf((long) result);
            } else {
                return String.valueOf(result);
            }
        }

        @Override
        public boolean canImport() { return false; }

        @Override
        public void importData() {}

        @Override
        public boolean canExport() { return false; }

        @Override
        public void exportData() {}

        @Override
        public String[] getFeatureNames() {
            return new String[]{"Basic Calculator", "Keyboard Support"};
        }

        @Override
        protected void initializeGraphics() {}
    }
}
EOF

# 编译 Plugin 3
echo "  ⚙️  编译中..."
javac -d "$WORK_DIR/plugin3" \
      -cp "dependency-egps/*:out/production/egps-main.gui" \
      "$WORK_DIR/plugin3/test/calculator/SimpleCalculator.java"

# 创建配置文件
cat > "$WORK_DIR/plugin3/eGPS2.plugin.properties" << 'EOF'
launchClass=test.calculator.SimpleCalculator
pluginName=Simple Calculator
version=1.0.0
author=eGPS Dev Team
description=A simple calculator for basic arithmetic operations
EOF

# 打包
echo "  📦 打包中..."
cd "$WORK_DIR/plugin3"
jar cvf calculator-plugin.jar . > /dev/null 2>&1
cd ../..

# 复制到 dist 目录
cp "$WORK_DIR/plugin3/calculator-plugin.jar" "$DIST_DIR/"
echo "  ✓ Plugin 3 创建完成 → $DIST_DIR/calculator-plugin.jar"
echo ""

# ============================================================
# Plugin 4: ClipboardPaste（剪贴板路径工具）
# ============================================================
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📦 创建插件 4: ClipboardPaste (路径工具)"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "  实现方式: 实现 IModuleLoader + 实用功能"
echo "  适用场景: 开发工具、路径转换"
echo "  代码量:   约 500 行"
echo ""

mkdir -p "$WORK_DIR/plugin4/test/clipboard"

cat > "$WORK_DIR/plugin4/test/clipboard/ClipboardPaste.java" << 'EOF'
package test.clipboard;

import egps2.modulei.IModuleLoader;
import egps2.modulei.IconBean;
import egps2.frame.ModuleFace;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;

/**
 * ClipboardPaste Plugin - Path conversion and normalization tool
 *
 * Features:
 * - Windows path to WSL path conversion (C:\path → /mnt/c/path)
 * - Path normalization (forward/backward slashes)
 * - Auto-paste from clipboard
 * - Copy result to clipboard
 */
public class ClipboardPaste implements IModuleLoader {

    private ClipboardPanel panel;

    public ClipboardPaste() {
        panel = new ClipboardPanel(this);
    }

    @Override
    public String getTabName() {
        return "ClipboardPaste";
    }

    @Override
    public String getShortDescription() {
        return "Path conversion tool - Windows to WSL and normalization";
    }

    @Override
    public ModuleFace getFace() {
        return panel;
    }

    @Override
    public int[] getCategory() {
        return new int[]{0, 0, 0, 0}; // Tool, General, Simple, No dependencies
    }

    @Override
    public IconBean getIcon() {
        return null;
    }

    @Override
    public JPanel getEnglishDocument() {
        return createDocumentPanel(
            "ClipboardPaste - English Manual",
            "<html><body style='padding: 10px; font-family: Arial, sans-serif;'>" +
            "<h2>ClipboardPaste</h2>" +
            "<p>A utility for converting and normalizing file paths.</p>" +
            "<h3>Features:</h3>" +
            "<ul>" +
            "<li><b>Windows to WSL:</b> Convert Windows paths to WSL format<br>" +
            "Example: C:\\Users\\name\\file.txt → /mnt/c/Users/name/file.txt</li>" +
            "<li><b>Path Normalization:</b> Convert backslashes to forward slashes<br>" +
            "Example: path\\to\\file → path/to/file</li>" +
            "<li><b>Clipboard Integration:</b> Paste from and copy to clipboard</li>" +
            "</ul>" +
            "<h3>Usage:</h3>" +
            "<ol>" +
            "<li>Copy a path to clipboard (Ctrl+C)</li>" +
            "<li>Click 'Paste from Clipboard' or press Ctrl+V</li>" +
            "<li>Choose conversion method</li>" +
            "<li>Click 'Copy to Clipboard' to copy result</li>" +
            "</ol>" +
            "<h3>Conversion Methods:</h3>" +
            "<ul>" +
            "<li><b>Win → WSL:</b> Converts Windows drive letters (C:, D:) to /mnt/c, /mnt/d</li>" +
            "<li><b>Normalize:</b> Replaces all backslashes with forward slashes</li>" +
            "<li><b>Reverse:</b> Converts forward slashes back to backslashes</li>" +
            "</ul>" +
            "</body></html>"
        );
    }

    @Override
    public JPanel getChineseDocument() {
        return createDocumentPanel(
            "ClipboardPaste - 中文手册",
            "<html><body style='padding: 10px; font-family: 微软雅黑, Arial, sans-serif;'>" +
            "<h2>ClipboardPaste</h2>" +
            "<p>文件路径转换和标准化工具。</p>" +
            "<h3>功能特性：</h3>" +
            "<ul>" +
            "<li><b>Windows 转 WSL：</b>将 Windows 路径转换为 WSL 格式<br>" +
            "示例：C:\\Users\\name\\file.txt → /mnt/c/Users/name/file.txt</li>" +
            "<li><b>路径标准化：</b>将反斜杠转换为正斜杠<br>" +
            "示例：path\\to\\file → path/to/file</li>" +
            "<li><b>剪贴板集成：</b>从剪贴板粘贴和复制到剪贴板</li>" +
            "</ul>" +
            "<h3>使用方法：</h3>" +
            "<ol>" +
            "<li>复制路径到剪贴板 (Ctrl+C)</li>" +
            "<li>点击 '从剪贴板粘贴' 或按 Ctrl+V</li>" +
            "<li>选择转换方法</li>" +
            "<li>点击 '复制到剪贴板' 以复制结果</li>" +
            "</ol>" +
            "<h3>转换方法：</h3>" +
            "<ul>" +
            "<li><b>Win → WSL:</b> 将 Windows 盘符 (C:, D:) 转换为 /mnt/c, /mnt/d</li>" +
            "<li><b>标准化:</b> 将所有反斜杠替换为正斜杠</li>" +
            "<li><b>反向:</b> 将正斜杠转换回反斜杠</li>" +
            "</ul>" +
            "</body></html>"
        );
    }

    private JPanel createDocumentPanel(String title, String htmlContent) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JEditorPane editorPane = new JEditorPane("text/html", htmlContent);
        editorPane.setEditable(false);
        editorPane.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(editorPane);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Clipboard Panel Implementation
     */
    static class ClipboardPanel extends ModuleFace {
        private JTextArea inputArea;
        private JTextArea outputArea;
        private Clipboard clipboard;

        public ClipboardPanel(IModuleLoader loader) {
            super(loader);
            clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            initUI();
        }

        private void initUI() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            // Title
            JLabel titleLabel = new JLabel("Path Conversion Tool", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            add(titleLabel, BorderLayout.NORTH);

            // Main panel with input and output
            JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));

            // Input panel
            JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
            inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Input Path",
                TitledBorder.LEFT,
                TitledBorder.TOP
            ));

            inputArea = new JTextArea(5, 40);
            inputArea.setLineWrap(true);
            inputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            JScrollPane inputScroll = new JScrollPane(inputArea);

            JButton pasteBtn = new JButton("Paste from Clipboard (Ctrl+V)");
            pasteBtn.addActionListener(e -> pasteFromClipboard());

            inputPanel.add(inputScroll, BorderLayout.CENTER);
            inputPanel.add(pasteBtn, BorderLayout.SOUTH);

            // Output panel
            JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
            outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Output Path",
                TitledBorder.LEFT,
                TitledBorder.TOP
            ));

            outputArea = new JTextArea(5, 40);
            outputArea.setLineWrap(true);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            outputArea.setEditable(false);
            outputArea.setBackground(new Color(245, 245, 245));
            JScrollPane outputScroll = new JScrollPane(outputArea);

            JButton copyBtn = new JButton("Copy to Clipboard");
            copyBtn.addActionListener(e -> copyToClipboard());

            outputPanel.add(outputScroll, BorderLayout.CENTER);
            outputPanel.add(copyBtn, BorderLayout.SOUTH);

            mainPanel.add(inputPanel);
            mainPanel.add(outputPanel);
            add(mainPanel, BorderLayout.CENTER);

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

            JButton win2wslBtn = new JButton("Windows → WSL");
            win2wslBtn.setBackground(new Color(100, 200, 100));
            win2wslBtn.addActionListener(e -> convertWinToWSL());

            JButton normalizeBtn = new JButton("Normalize Path");
            normalizeBtn.setBackground(new Color(100, 150, 250));
            normalizeBtn.addActionListener(e -> normalizePath());

            JButton reverseBtn = new JButton("Reverse Slashes");
            reverseBtn.setBackground(new Color(250, 200, 100));
            reverseBtn.addActionListener(e -> reverseSlashes());

            JButton clearBtn = new JButton("Clear");
            clearBtn.addActionListener(e -> clearAll());

            buttonPanel.add(win2wslBtn);
            buttonPanel.add(normalizeBtn);
            buttonPanel.add(reverseBtn);
            buttonPanel.add(clearBtn);

            add(buttonPanel, BorderLayout.SOUTH);

            // Keyboard shortcuts
            setupKeyboardShortcuts();
        }

        private void setupKeyboardShortcuts() {
            InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
            ActionMap actionMap = getActionMap();

            inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK), "paste");
            actionMap.put("paste", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pasteFromClipboard();
                }
            });
        }

        private void pasteFromClipboard() {
            try {
                Transferable contents = clipboard.getContents(null);
                if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    String text = (String) contents.getTransferData(DataFlavor.stringFlavor);
                    inputArea.setText(text);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Failed to paste from clipboard: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }

        private void copyToClipboard() {
            String text = outputArea.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Output is empty. Please convert a path first.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);
            JOptionPane.showMessageDialog(this,
                "Copied to clipboard successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }

        private void convertWinToWSL() {
            String input = inputArea.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter or paste a path first.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String result = input;

            // Convert drive letter (C:\, D:\, etc.) to /mnt/c/, /mnt/d/
            if (result.matches("^[A-Za-z]:[/\\\\].*")) {
                char drive = Character.toLowerCase(result.charAt(0));
                result = "/mnt/" + drive + "/" + result.substring(3);
            }

            // Convert all backslashes to forward slashes
            result = result.replace('\\', '/');

            // Remove duplicate slashes
            result = result.replaceAll("/+", "/");

            outputArea.setText(result);
        }

        private void normalizePath() {
            String input = inputArea.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter or paste a path first.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String result = input.replace('\\', '/');
            outputArea.setText(result);
        }

        private void reverseSlashes() {
            String input = inputArea.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter or paste a path first.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String result = input.replace('/', '\\');
            outputArea.setText(result);
        }

        private void clearAll() {
            inputArea.setText("");
            outputArea.setText("");
        }

        @Override
        public boolean canImport() { return false; }

        @Override
        public void importData() {}

        @Override
        public boolean canExport() { return false; }

        @Override
        public void exportData() {}

        @Override
        public String[] getFeatureNames() {
            return new String[]{"Windows to WSL", "Path Normalization", "Clipboard Support"};
        }

        @Override
        protected void initializeGraphics() {}
    }
}
EOF

# 编译 Plugin 4
echo "  ⚙️  编译中..."
javac -d "$WORK_DIR/plugin4" \
      -cp "dependency-egps/*:out/production/egps-main.gui" \
      "$WORK_DIR/plugin4/test/clipboard/ClipboardPaste.java"

# 创建配置文件
cat > "$WORK_DIR/plugin4/eGPS2.plugin.properties" << 'EOF'
launchClass=test.clipboard.ClipboardPaste
pluginName=ClipboardPaste
version=1.0.0
author=eGPS Dev Team
description=Path conversion tool - Windows to WSL and normalization
EOF

# 打包
echo "  📦 打包中..."
cd "$WORK_DIR/plugin4"
jar cvf clipboardpaste-plugin.jar . > /dev/null 2>&1
cd ../..

# 复制到 dist 目录
cp "$WORK_DIR/plugin4/clipboardpaste-plugin.jar" "$DIST_DIR/"
echo "  ✓ Plugin 4 创建完成 → $DIST_DIR/clipboardpaste-plugin.jar"
echo ""

# ============================================================
# 清理临时文件
# ============================================================
echo "🧹 清理临时文件..."
rm -rf "$WORK_DIR"
echo ""

# ============================================================
# 生成安装说明
# ============================================================
cat > "$DIST_DIR/README.txt" << 'EOF'
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                    eGPS Plugin Installation Guide
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

This directory contains 4 example plugins:

1. fastbase-plugin.jar
   Type: Extends FastBaseTemplate
   Purpose: Demonstrates simple plugin development
   Features: Basic UI, minimal code (~70 lines)
   Use case: Simple tools, rapid prototyping

2. direct-plugin.jar
   Type: Implements IModuleLoader directly
   Purpose: Demonstrates complex plugin architecture
   Features: Loader/Panel separation, full control
   Use case: Complex features, team collaboration

3. calculator-plugin.jar
   Type: Implements IModuleLoader with documentation
   Purpose: Practical utility tool - basic calculator
   Features: Arithmetic operations, keyboard support, EN/CN docs
   Use case: Real-world utility tools with documentation

4. clipboardpaste-plugin.jar
   Type: Implements IModuleLoader with practical features
   Purpose: Path conversion tool (Windows ↔ WSL)
   Features: Win→WSL conversion, path normalization, clipboard integration
   Use case: Development tools, cross-platform path handling

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                        Installation Methods
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Method 1: Install as External Plugins (Recommended)
---------------------------------
Copy JAR files to plugin directory:

    Linux/Mac:
    cp *.jar ~/.egps2/config/plugin/

    Windows:
    copy *.jar %USERPROFILE%\.egps2\config\plugin\

After installation:
  • Restart eGPS
  • Plugins appear in "Plugins" menu
  • Plugins appear in "Module Gallery" (Ctrl+2)
  • Plugins have [Plug] badge in Module Gallery

Method 2: Install as Built-in Modules (Advanced)
---------------------------------
Copy JAR files to dependency-egps directory:

    cp *.jar dependency-egps/

After installation:
  • Restart eGPS
  • Modules appear as built-in modules
  • NOT shown in "Plugins" menu
  • Appear in "iTools" or category menus
  • No [Plug] badge in Module Gallery

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                        Usage Instructions
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Start eGPS:
    java -cp "./out/production/egps-main.gui:dependency-egps/*" egps2.Launcher

Open plugins:
    Method 1: Menu Bar → Plugins → Select plugin
    Method 2: Press Ctrl+2 to open Module Gallery → Select plugin

View documentation (for plugins with docs):
    Right-click on plugin tab → View English/Chinese Manual

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                      Develop Your Own Plugins
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

View detailed tutorials:
    docs/module_plugin_course/

Contents:
  • 01_QUICK_START_zh.md           - Quick start guide
  • 02_PLUGIN_DEVELOPMENT_zh.md    - Plugin development (external JAR)
  • 03_BUILTIN_DEVELOPMENT_zh.md   - Built-in module development
  • 04_ARCHITECTURE_zh.md          - Architecture principles
  • eGPS2.plugin.properties_zh.md  - Configuration file specification

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                      Plugin Feature Summary
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

FastBase Plugin:
  ✓ Simplest implementation (extends FastBaseTemplate)
  ✓ Perfect for learning plugin basics
  ✓ Minimal code required

Direct Plugin:
  ✓ Demonstrates best practices architecture
  ✓ Loader and Panel separation
  ✓ Suitable for complex plugins

Simple Calculator:
  ✓ Functional utility tool
  ✓ Includes English and Chinese documentation
  ✓ Keyboard shortcuts support
  ✓ Error handling (division by zero)
  ✓ Shows how to implement getEnglishDocument() and getChineseDocument()

ClipboardPaste:
  ✓ Practical development tool
  ✓ Windows path to WSL conversion (C:\ → /mnt/c/)
  ✓ Path normalization (backslash ↔ forward slash)
  ✓ Clipboard integration (Ctrl+V to paste)
  ✓ English and Chinese documentation
  ✓ Demonstrates real-world utility plugin

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Generated: $(date)
eGPS Version: 2.1+

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
EOF

# ============================================================
# 完成总结
# ============================================================
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "✅ 所有插件创建完成！"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "📦 生成的文件:"
echo ""
ls -lh "$DIST_DIR"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "📝 安装说明："
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "方法 1️⃣: 安装为外部插件（Plugin）"
echo "  cp $DIST_DIR/*.jar ~/.egps2/config/plugin/"
echo ""
echo "方法 2️⃣: 安装为内置模块（Built-in）"
echo "  cp $DIST_DIR/*.jar dependency-egps/"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo "🚀 启动 eGPS："
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
echo "  java -cp \"./out/production/egps-main.gui:dependency-egps/*\" egps2.Launcher"
echo ""
echo "然后按 Ctrl+2 打开 Module Gallery 查看插件！"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""
