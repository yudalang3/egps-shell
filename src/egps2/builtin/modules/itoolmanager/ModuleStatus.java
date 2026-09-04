package egps2.builtin.modules.itoolmanager;

/**
 * Module status enumeration for tracking the availability and loading state of modules.
 *
 * <p>This enum provides detailed status information for each module in the ITools Manager:
 * <ul>
 *   <li>AVAILABLE - Module is available and loaded</li>
 *   <li>AVAILABLE_NOT_LOADED - Module is available but not loaded</li>
 *   <li>NEWLY_DISCOVERED - Module found via scanning but not in config file</li>
 *   <li>UNAVAILABLE - Module in config but cannot be loaded</li>
 *   <li>DEPRECATED - Module marked as deprecated</li>
 * </ul>
 *
 * @author eGPS Dev Team
 * @since 2.1
 */
public enum ModuleStatus {
    /**
     * 模块可用且已加载
     * Module is available and loaded
     */
    AVAILABLE("Available", "模块可用且已加载"),

    /**
     * 模块可用但未加载
     * Module is available but not loaded
     */
    AVAILABLE_NOT_LOADED("Available (Not Loaded)", "模块可用但未加载"),

    /**
     * 新发现的模块（配置文件中不存在）
     * Newly discovered module (not in config file)
     */
    NEWLY_DISCOVERED("Newly Discovered", "新发现的模块（配置文件中不存在）"),

    /**
     * 配置文件中存在但无法加载
     * Module in config but cannot be loaded
     */
    UNAVAILABLE("Unavailable", "配置文件中存在但无法加载"),

    /**
     * 已标记为过时的模块
     * Module marked as deprecated
     */
    DEPRECATED("Deprecated", "已标记为过时的模块");

    private final String displayName;
    private final String description;

    ModuleStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
