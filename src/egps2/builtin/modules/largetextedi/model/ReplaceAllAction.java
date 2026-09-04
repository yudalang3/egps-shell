package egps2.builtin.modules.largetextedi.model;

/**
 * ReplaceAllAction belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class ReplaceAllAction extends TextEditAction {
	protected String targetString = null;
	protected String replaceWith = null;
	private boolean isCaseSensitive;

	public ReplaceAllAction(String targetString, String replaceWith, boolean isCaseSensitive) {
		if (targetString == null || replaceWith == null)
			throw new IllegalArgumentException();

		this.targetString = targetString;
		this.replaceWith = replaceWith;

		this.isCaseSensitive = isCaseSensitive;

		setEditAction(REPLACE_ALL);
	}

	public String getTargetString() {
		return targetString;
	}

	public void setTargetString(String targetString) {
		this.targetString = targetString;
	}

	public String getReplaceWith() {
		return replaceWith;
	}

	public void setReplaceWith(String replaceWith) {
		this.replaceWith = replaceWith;
	}

	@Override
	public int getLineNumberOnShow() {

		return 0;
	}

	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}

	public void setCaseSensitive(boolean isCaseSensitive) {
		this.isCaseSensitive = isCaseSensitive;
	}

}
