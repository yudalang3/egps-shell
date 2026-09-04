package egps2.builtin.modules.itoolmanager;

import egps2.modulei.IModuleLoader;

/**
 * IModuleElement belongs to a built-in eGPS module (loader, panel, or helper).
 *
 * <p>Enhanced version includes status information for module discovery and management.
 * This allows tracking of module availability, loading state, and error conditions.
 */
public class IModuleElement {
    private IModuleLoader loader;           // Module loader instance (may be null for unavailable modules)
    private boolean load;                   // Whether to load this module
    private ModuleStatus status;            // Module status
    private String className;               // Full class name
    private String errorMessage;            // Error message (if unavailable)

    /**
     * Constructor for available modules (backward compatible)
     *
     * @param loader Module loader instance
     * @param load Whether to load this module
     */
    public IModuleElement(IModuleLoader loader, boolean load) {
        this.loader = loader;
        this.load = load;
        this.status = load ? ModuleStatus.AVAILABLE : ModuleStatus.AVAILABLE_NOT_LOADED;
        this.className = loader != null ? loader.getClass().getName() : null;
        this.errorMessage = null;
    }

    /**
     * Constructor for available modules with explicit status
     *
     * @param loader Module loader instance
     * @param load Whether to load this module
     * @param status Module status
     */
    public IModuleElement(IModuleLoader loader, boolean load, ModuleStatus status) {
        this.loader = loader;
        this.load = load;
        this.status = status;
        this.className = loader != null ? loader.getClass().getName() : null;
        this.errorMessage = null;
    }

    /**
     * Constructor for unavailable modules
     *
     * @param className Full class name
     * @param load Whether to load this module (from config)
     * @param errorMessage Error message describing why the module is unavailable
     */
    public IModuleElement(String className, boolean load, String errorMessage) {
        this.className = className;
        this.load = load;
        this.status = ModuleStatus.UNAVAILABLE;
        this.errorMessage = errorMessage;
        this.loader = null;
    }

    public IModuleLoader getLoader() {
        return loader;
    }

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public ModuleStatus getStatus() {
        return status;
    }

    public void setStatus(ModuleStatus status) {
        this.status = status;
    }

    public String getClassName() {
        return className;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Check if this module is available (can be loaded)
     *
     * @return true if the module loader instance exists
     */
    public boolean isAvailable() {
        return loader != null;
    }

    @Override
    public String toString() {
        return String.format("IModuleElement{class=%s, status=%s, load=%s, error=%s}",
                className, status, load, errorMessage);
    }
}
