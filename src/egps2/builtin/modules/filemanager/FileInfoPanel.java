package egps2.builtin.modules.filemanager;

import egps2.UnifiedAccessPoint;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件信息显示面板，模仿Windows Explorer的设计风格
 * 显示文件或目录的详细信息，包括图标、名称、大小、修改时间等
 */
public class FileInfoPanel extends JPanel {
    // 核心组件字段
    private Path currentPath;           // 当前显示的文件/目录路径
    private JLabel iconLabel;           // 显示文件/目录图标
    private JLabel nameLabel;           // 显示文件/目录名称
    private JLabel typeLabel;           // 显示文件类型
    private JLabel sizeLabel;           // 显示文件大小或目录项目数
    private JLabel modifiedLabel;       // 显示修改时间
    private JLabel createdLabel;        // 显示创建时间
    private JTextArea locationLabel;       // 显示文件位置
    private JPanel detailsPanel;        // 详细信息面板

    // 字体设置
    private Font titleFont;             // 标题字体（文件名）
    private Font detailsFont;           // 详细信息字体（标签和值）

    // 静态常量
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat SIZE_FORMAT = new DecimalFormat("#,##0.#");
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();

    /**
     * 构造函数 - 创建文件信息显示面板
     * @param file 要显示的文件或目录路径
     * @param titleFont 标题字体（用于文件名显示）
     * @param detailsFont 详细信息字体（用于标签和内容显示）
     */
    public FileInfoPanel(Path file, Font titleFont, Font detailsFont) {
        this.titleFont = titleFont;
        this.detailsFont = detailsFont;
        initializeComponents();
        changeFile(file);
    }

    /**
     * 初始化界面组件 - 设置布局和样式
     */
    private void initializeComponents() {
        // === 主面板设置 ===
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(200, getHeight())); // 新增：设置Preference size宽度为200

        // === 顶部区域：图标 + 文件名 ===
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);

        // 文件图标
        iconLabel = new JLabel();
        iconLabel.setPreferredSize(new Dimension(48, 48));
        headerPanel.add(iconLabel);

        // 文件名（使用标题字体）
        nameLabel = new JLabel();
        nameLabel.setFont(titleFont);
        nameLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        headerPanel.add(nameLabel);

        add(headerPanel, BorderLayout.NORTH);

        // === 详细信息区域 ===
        detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 0, 8, 30); // 增加间距让布局更清晰

        // 创建信息行：标签 || 值
        createInfoRow("Type:", typeLabel = new JLabel(), gbc, 0);
        createInfoRow("Size:", sizeLabel = new JLabel(), gbc, 2);
        createInfoRow("Modified:", modifiedLabel = new JLabel(), gbc, 4);
        createInfoRow("Created:", createdLabel = new JLabel(), gbc, 6);

        locationLabel = new JTextArea();
        locationLabel.setLineWrap(true);
        locationLabel.setWrapStyleWord(true);
        locationLabel.setOpaque(false);
        locationLabel.setEditable(true);
        locationLabel.setFocusable(false);
        locationLabel.setBorder(null);
        createInfoRow4TextArea("Location:", locationLabel, gbc, 8);

        add(detailsPanel, BorderLayout.CENTER);
    }

    /**
     * 创建信息显示行 - 标准格式：标签 || 值
     * @param labelText 标签文本
     * @param valueLabel 值显示的JLabel
     * @param gbc GridBag约束
     * @param row 行号
     */
    private void createInfoRow(String labelText, JLabel valueLabel, GridBagConstraints gbc, int row) {
        // 左侧标签
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(detailsFont);
        label.setForeground(new Color(100, 100, 100)); // 深灰色标签
        detailsPanel.add(label, gbc);

        // 右侧值
        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        valueLabel.setFont(detailsFont);
        valueLabel.setForeground(Color.BLACK);
        detailsPanel.add(valueLabel, gbc);
        gbc.weightx = 0;
    }
    private void createInfoRow4TextArea(String labelText, JTextArea valueLabel, GridBagConstraints gbc, int row) {
        // 左侧标签
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(detailsFont);
        label.setForeground(new Color(100, 100, 100)); // 深灰色标签
        detailsPanel.add(label, gbc);

        // 右侧值
        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        valueLabel.setFont(detailsFont);
        valueLabel.setForeground(Color.BLACK);
        detailsPanel.add(valueLabel, gbc);
        gbc.weightx = 0;
    }

    /**
     * 可以动态更新显示的路径，效果同构造函数
     * @param path 要显示信息的文件或目录路径
     */
    public void changeFile(Path path) {
        if (path == null || !Files.exists(path)) {
            clearInfo();
            return;
        }

        this.currentPath = path;
        updateFileInfo();
    }

    /**
     * 更新文件信息显示 - 读取文件属性并更新界面
     */
    private void updateFileInfo() {
        try {
            // 读取文件基础属性
            BasicFileAttributes attrs = Files.readAttributes(currentPath, BasicFileAttributes.class);

            // === 设置文件图标 ===
            setFileIcon();

            // === 设置文件名 ===
            String fileName = currentPath.getFileName() != null ?
                    currentPath.getFileName().toString() : currentPath.toString();
            nameLabel.setText(fileName);

            // === 设置文件类型 ===
            if (Files.isDirectory(currentPath)) {
                typeLabel.setText("File folder");
            } else {
                String extension = getFileExtension(fileName);
                if (extension.isEmpty()) {
                    typeLabel.setText("File");
                } else {
                    // 尝试获取系统文件类型描述
                    String description = getFileTypeDescription(fileName);
                    typeLabel.setText(description != null ? description : extension.toUpperCase() + " file");
                }
            }

            // === 设置大小信息 ===
            if (Files.isDirectory(currentPath)) {
                // 目录显示包含的项目数量
                long itemCount = getDirectoryItemCount(currentPath);
                if (itemCount == 1) {
                    sizeLabel.setText("1 item");
                } else {
                    sizeLabel.setText(SIZE_FORMAT.format(itemCount) + " items");
                }
            } else {
                // 文件显示字节大小
                long size = attrs.size();
                sizeLabel.setText(formatFileSize(size));
            }

            // === 设置时间信息 ===
            FileTime modifiedTime = attrs.lastModifiedTime();
            modifiedLabel.setText(DATE_FORMAT.format(new Date(modifiedTime.toMillis())));

            FileTime createdTime = attrs.creationTime();
            createdLabel.setText(DATE_FORMAT.format(new Date(createdTime.toMillis())));

            // === 设置位置信息 ===
            locationLabel.setText(currentPath.toUri().getPath() );


        } catch (IOException e) {
            showError("Cannot read file information: " + e.getMessage());
        }
    }

    /**
     * 设置文件图标 - 使用系统原生图标
     */
    private void setFileIcon() {
        try {
            File file = currentPath.toFile();
            Icon icon = null;

            if (Files.isDirectory(currentPath)) {
                // 获取系统文件夹图标
                icon = FILE_SYSTEM_VIEW.getSystemIcon(file);
                if (icon == null) {
                    // 备用方案：UIManager的文件夹图标
                    icon = UIManager.getIcon("FileView.directoryIcon");
                }
            } else {
                // 获取系统文件图标
                icon = FILE_SYSTEM_VIEW.getSystemIcon(file);
                if (icon == null) {
                    // 备用方案：UIManager的文件图标
                    icon = UIManager.getIcon("FileView.fileIcon");
                }
            }

            // 图标缩放和设置
            if (icon != null) {
                if (icon instanceof ImageIcon) {
                    ImageIcon imageIcon = (ImageIcon) icon;
                    Image img = imageIcon.getImage();
                    Image scaledImg = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                    iconLabel.setIcon(new ImageIcon(scaledImg));
                } else {
                    iconLabel.setIcon(icon);
                }
            } else {
                // 最终备用方案：Tree图标
                if (Files.isDirectory(currentPath)) {
                    iconLabel.setIcon(UIManager.getIcon("Tree.closedIcon"));
                } else {
                    iconLabel.setIcon(UIManager.getIcon("Tree.leafIcon"));
                }
            }

        } catch (Exception e) {
            // 异常处理：使用默认图标
            if (Files.isDirectory(currentPath)) {
                iconLabel.setIcon(UIManager.getIcon("Tree.closedIcon"));
            } else {
                iconLabel.setIcon(UIManager.getIcon("Tree.leafIcon"));
            }
        }
    }

    /**
     * 获取文件类型描述 - 从系统获取文件类型信息
     * @param fileName 文件名
     * @return 文件类型描述，失败返回null
     */
    private String getFileTypeDescription(String fileName) {
        try {
            File file = new File(fileName);
            return FILE_SYSTEM_VIEW.getSystemTypeDescription(file);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取文件扩展名
     * @param fileName 文件名
     * @return 扩展名（不含点），无扩展名返回空字符串
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1) : "";
    }

    /**
     * 统计目录中的项目数量
     * @param directory 目录路径
     * @return 项目数量，出错返回0
     */
    private long getDirectoryItemCount(Path directory) {
        try {
            return Files.list(directory).count();
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * 格式化文件大小显示 - 使用二进制单位（1024为基数）
     * @param bytes 文件大小（字节）
     * @return 格式化的大小字符串，如 "1.5 MiB (1,572,864 bytes)"
     */
    private String formatFileSize(long bytes) {
        if (bytes < 0) {
            return "Unknown";
        }

        if (bytes == 0) {
            return "0 bytes";
        } else if (bytes == 1) {
            return "1 byte";
        } else if (bytes < 1024) {
            return bytes + " bytes";
        }

        // 使用二进制单位 (1024为基数)，显示为 KiB, MiB, GiB, TiB
        double size = bytes;
        String[] units = {"KiB", "MiB", "GiB", "TiB", "PiB"};

        for (int i = 0; i < units.length; i++) {
            size /= 1024.0;
            if (size < 1024.0 || i == units.length - 1) {
                return SIZE_FORMAT.format(size) + " " + units[i] +
                        " (" + SIZE_FORMAT.format(bytes) + " bytes)";
            }
        }

        return SIZE_FORMAT.format(bytes) + " bytes";
    }

    /**
     * 清除显示信息 - 当路径无效时调用
     */
    private void clearInfo() {
        iconLabel.setIcon(null);
        nameLabel.setText("Invalid path");
        typeLabel.setText("-");
        sizeLabel.setText("-");
        modifiedLabel.setText("-");
        createdLabel.setText("-");
        locationLabel.setText("-");
    }

    /**
     * 显示错误信息对话框
     * @param message 错误信息
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("File Info Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);

            // 创建文件选择器用于测试
            JPanel mainPanel = new JPanel(new BorderLayout());

            // 默认显示当前目录
            Path defaultPath = Paths.get(System.getProperty("user.dir"));

            Font defaultFont = UnifiedAccessPoint.getLaunchProperty().getDefaultFont();
            Font defaultTitleFont = UnifiedAccessPoint.getLaunchProperty().getDefaultTitleFont();

            if (defaultFont == null) defaultFont = new Font("SansSerif", Font.PLAIN, 12);
            if (defaultTitleFont == null) defaultTitleFont = new Font("SansSerif", Font.BOLD, 14);

            FileInfoPanel fileInfoPanel = new FileInfoPanel(defaultPath, defaultTitleFont, defaultFont);

            // 添加文件选择按钮
            JPanel buttonPanel = new JPanel(new FlowLayout());
            JButton selectFileButton = new JButton("Select File");
            JButton selectDirButton = new JButton("Select Directory");

            selectFileButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        fileInfoPanel.changeFile(chooser.getSelectedFile().toPath());
                    }
                }
            });

            selectDirButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                        fileInfoPanel.changeFile(chooser.getSelectedFile().toPath());
                    }
                }
            });

            buttonPanel.add(selectFileButton);
            buttonPanel.add(selectDirButton);

            mainPanel.add(buttonPanel, BorderLayout.NORTH);
            mainPanel.add(fileInfoPanel, BorderLayout.CENTER);

            frame.add(mainPanel);
            frame.setVisible(true);
        });
    }
}