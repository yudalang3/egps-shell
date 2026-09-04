package egps2.builtin.modules.largetextedi.model;

/**
 * RevisedLineAction belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class RevisedLineAction extends TextEditAction {

	private LineObj revisedLineObj;
	
	private int index = 0;

	public RevisedLineAction(LineObj lineObj) {

		this.revisedLineObj = lineObj;

		setEditAction(REVISED);
	}

	public LineObj getRevisedLineObj() {

		return this.revisedLineObj;
	}

	@Override
	public int getLineNumberOnShow() {

		return revisedLineObj.getLineNumberOnShow();
	}

	public int getIndex() {

		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
