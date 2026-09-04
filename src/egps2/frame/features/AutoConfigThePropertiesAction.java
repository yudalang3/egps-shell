package egps2.frame.features;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.InputMismatchException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import egps2.utils.common.util.EGPSShellIcons;
import egps2.EGPSProperties;

/**
 * 自动配置属性功能类，负责首次启动时或配置导入时的自动配置初始化。
 * Auto-configuration properties feature class responsible for automatic configuration initialization on first launch or configuration import.
 *
 * <p>此类实现{@link Runnable}接口，提供配置文件的自动安装和管理功能。
 * This class implements {@link Runnable} interface, providing automatic installation and management functionality for configuration files.
 *
 * <p><strong>核心功能：</strong>
 * Core functionality:
 * <ul>
 *   <li>首次启动时创建配置目录 - Create configuration directory on first launch</li>
 *   <li>导入配置文件到用户目录 - Import configuration files to user directory</li>
 *   <li>处理配置重置和覆盖 - Handle configuration reset and override</li>
 *   <li>自动创建必要的工作目录 - Automatically create necessary working directories</li>
 * </ul>
 *
 * <p><strong>配置流程：</strong>
 * Configuration flow:
 * <ol>
 *   <li>检查用户配置目录是否存在 - Check if user configuration directory exists</li>
 *   <li>若不存在，直接创建并导入配置 - If not exists, create and import configuration directly</li>
 *   <li>若已存在，询问用户是否重置配置 - If exists, ask user whether to reset configuration</li>
 *   <li>用户确认后移动或删除配置 - Move or delete configuration after user confirmation</li>
 *   <li>创建必要的工作目录 - Create necessary working directories</li>
 * </ol>
 *
 * @see Runnable
 * @see EGPSProperties
 * @author eGPS Dev Team
 * @since 2.1
 */
public class AutoConfigThePropertiesAction implements Runnable {

	private final File softwareConfig;

	public AutoConfigThePropertiesAction(File file) {
		this.softwareConfig = file;
	}

	@Override
	public void run() {
		File configUserHome = new File(EGPSProperties.PROPERTIES_DIR);
		try {
			if (configUserHome.exists()) {
				boolean yes = showConfigResetDialog();
				if (yes) {
					// 先删除再移动
					FileUtils.deleteQuietly(configUserHome);
					FileUtils.moveDirectory(softwareConfig, configUserHome);
				} else {
					// do nothing
					FileUtils.deleteQuietly(softwareConfig);
				}
			} else {
				FileUtils.moveDirectory(softwareConfig, configUserHome);
			}
			Path path = Paths.get(EGPSProperties.JSON_DIR);
			if (Files.notExists(path)) {
				Files.createDirectories(path);
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new InputMismatchException();
		}


	}

	/**
	 * Displays a dialog asking the user whether to reset the config directory.
	 * 
	 * @return true if the user chooses "Yes", false if the user chooses "No".
	 */
	private boolean showConfigResetDialog() {
		// Dialog title
		String title = "Existing Configuration Found";
		// Dialog message
		String message = "Do you want to reset the config directory?";
		// Options for the buttons
		String[] options = { "Yes", "No" };

		// Show the dialog and get the user's choice
		int choice = JOptionPane.showOptionDialog(null, // Parent component, null for centered dialog
				message, // Dialog message
				title, // Dialog title
				JOptionPane.DEFAULT_OPTION, // Dialog type
				JOptionPane.QUESTION_MESSAGE, // Icon type (question mark)
				EGPSShellIcons.get("eGPS_logo16x16.png"), // Custom icon (null for default)
				options, // Button text options
				options[0] // Default selected option
		);

		// Return true if "Yes" is selected, otherwise false
		return choice == 0;
	}

}
