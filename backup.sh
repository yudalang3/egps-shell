#!/bin/bash

# 创建backup目录
mkdir -p backup

# 生成时间戳
timestamp=$(date +"%Y%m%d_%H%M%S")

# 打包除backup目录外的所有文件
tar --exclude='backup' -cJf "backup/backup_egps_main_gui_${timestamp}.tar.xz" .

echo "备份完成: backup/backup_egps_main_gui_${timestamp}.tar.xz"
