package egps2.builtin.modules.largetextedi.model;

/**
 * EditorCaret belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class EditorCaret {

	private int markLineNumber = 1; // 记录开始选择的行号

	private int markOffset; // 记录开始选择时的偏移量(该偏移量为相对于当前行的偏移量)

	private int currentFocusOnShowLineNumber = 1; // 当前焦点行号

	private int currentFocusOnOffset; // 当前焦点偏移量

	private int startPosition; // 当前焦点所在界面的起始行号

	private boolean isCurrentFocusReassignment = true; // 是否重新计算当前焦点位置

	public int getMarkLineNumber() {
		return markLineNumber;
	}

	public void setMarkLineNumber(int markLineNumber) {
		this.markLineNumber = markLineNumber;
	}

	public int getMarkOffset() {
		return markOffset;
	}

	public void setMarkOffset(int markOffset) {
		this.markOffset = markOffset;
	}

	public int getCurrentFocusOnShowLineNumber() {
		return currentFocusOnShowLineNumber;
	}

	public void setCurrentFocusOnShowLineNumber(int currentFocusOnShowLineNumber) {
		this.currentFocusOnShowLineNumber = currentFocusOnShowLineNumber;
	}

	public int getCurrentFocusOnOffset() {
		return currentFocusOnOffset;
	}

	public void setCurrentFocusOnOffset(int currentFocusOnOffset) {
		this.currentFocusOnOffset = currentFocusOnOffset;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public boolean isCurrentFocusReassignment() {
		return isCurrentFocusReassignment;
	}

	public void setCurrentFocusReassignment(boolean isCurrentFocusReassignment) {
		this.isCurrentFocusReassignment = isCurrentFocusReassignment;
	}
	
	

}
