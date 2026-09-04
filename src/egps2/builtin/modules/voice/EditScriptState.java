package egps2.builtin.modules.voice;

/**
 * EditScriptState belongs to a built-in eGPS module (loader, panel, or helper).
 *
 * States:
 * - IN_PREVIOUS_RECORD: Viewing/editing an existing saved bookmark
 * - COPY_ON_ENTRY: Auto-created on module entry from Example (temporary, will be deleted if user switches without editing)
 * - COPY_OF_EXISTED: User explicitly duplicated a bookmark (permanent, will be saved)
 * - USER_MODIFIED: User has modified the content
 */
public enum EditScriptState {
    IN_PREVIOUS_RECORD("In previous record", 0),
    COPY_ON_ENTRY("Copy on entry", 1),      // Temporary: auto-created on entry, deleted if user switches without editing
    COPY_OF_EXISTED("Copy of existed", 2),  // Permanent: user explicitly duplicated
    USER_MODIFIED("User modified", 3);
    
    private final String description;
    private final int index;
    
    EditScriptState(String description, int index) {
        this.description = description;
        this.index = index;
    }
    
    public String getDescription() {
        return description;
    }
    
    public int getIndex() {
        return index;
    }
    
    // 根据索引获取枚举值的静态方法
    public static EditScriptState fromIndex(int index) {
        for (EditScriptState state : values()) {
            if (state.index == index) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid index: " + index);
    }
    
    // 根据描述获取枚举值的静态方法
    public static EditScriptState fromDescription(String description) {
        for (EditScriptState state : values()) {
            if (state.description.equals(description)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Invalid description: " + description);
    }
    

    // 按索引顺序获取描述数组（更安全的方式）
    public static String[] getDescriptionsByIndex() {
        String[] descriptions = new String[values().length];
        for (EditScriptState state : values()) {
            descriptions[state.index] = state.description;
        }
        return descriptions;
    }
    
    @Override
    public String toString() {
        return description;
    }
}
