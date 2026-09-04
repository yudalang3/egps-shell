package egps2.builtin.modules.largetextedi.model;

/**
 * TextEditAction belongs to a built-in eGPS module (loader, panel, or helper).
 */
public abstract class TextEditAction {

	public static int REVISED = 0;
	public static int NEW_LINE = 1;
	public static int DELETE_LINE = 2;
	public static int REPLACE_ALL = 3;

	public int editAction = -1;

	private int belongingGroupIndex;

	public void setBelongingGroupIndex(int belongingGroupIndex) {
		this.belongingGroupIndex = belongingGroupIndex;
	}

	public int getBelongingGroupIndex() {

		return belongingGroupIndex;
	}

	public int getEditAction() {
		return editAction;
	}

	public void setEditAction(int textEditAction) {
		this.editAction = textEditAction;
	}

	public abstract int getLineNumberOnShow();
}
