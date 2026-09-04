package egps2.builtin.modules.largetextedi.model;

/**
 * SelectEditor belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class SelectEditor {

	private int selectionStart = 0;

	private int selectionEnd = 0;

	private int startLineNumber = -1;

	private int endLineNumber = -1;

	private boolean isThereAnyRemaining;

	private String remainingCharacters;

	private boolean isShiftDown;

	public boolean isShiftDown() {
		return isShiftDown;
	}

	public void setShiftDown(boolean isShiftDown) {
		this.isShiftDown = isShiftDown;
	}

	public String getRemainingCharacters() {
		return remainingCharacters;
	}

	public void setRemainingCharacters(String remainingCharacters) {
		this.remainingCharacters = remainingCharacters;
	}

	public boolean isThereAnyRemaining() {
		return isThereAnyRemaining;
	}

	public void setThereAnyRemaining(boolean isThereAnyRemaining) {
		this.isThereAnyRemaining = isThereAnyRemaining;
	}

	public int getSelectionStart() {
		return selectionStart;
	}

	public void setSelectionStart(int selectionStart) {
		this.selectionStart = selectionStart;
	}

	public int getSelectionEnd() {
		return selectionEnd;
	}

	public void setSelectionEnd(int selectionEnd) {
		this.selectionEnd = selectionEnd;
	}

	public int getStartLineNumber() {
		return startLineNumber;
	}

	public void setStartLineNumber(int startLineNumber) {
		this.startLineNumber = startLineNumber;
	}

	public int getEndLineNumber() {
		return endLineNumber;
	}

	public void setEndLineNumber(int endLineNumber) {
		this.endLineNumber = endLineNumber;
	}

	public void clearSelection() {
		selectionStart = -1;
		selectionEnd = -1;
		startLineNumber = -1;
		endLineNumber = -1;
		isThereAnyRemaining = false;
		remainingCharacters = null;
	}

}
