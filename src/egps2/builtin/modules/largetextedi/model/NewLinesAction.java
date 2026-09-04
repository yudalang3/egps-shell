package egps2.builtin.modules.largetextedi.model;

/**
 * NewLinesAction belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class NewLinesAction extends TextEditAction {

	private int afterlineNumber = -1; // 在某一行后面插入的新行，记录其行号。如果在文件起始位置插入新行，则为0

	// 这是新的行，在文件里面不存在，所以无须考虑偏移量offset
	private LineObj newLineObj;

	public NewLinesAction(int afterlineNumber, LineObj lineObj) {

		this.afterlineNumber = afterlineNumber;

		this.newLineObj = lineObj;

		setEditAction(NEW_LINE);
	}

	private int index = 0;

	public int getIndex() {

		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getAfterlineNumber() {
		return afterlineNumber;
	}

	public void setAfterlineNumber(int afterlineNumber) {
		this.afterlineNumber = afterlineNumber;
	}

	public LineObj getNewLineObj() {
		return newLineObj;
	}

	@Override
	public int getLineNumberOnShow() {

		return newLineObj.getLineNumberOnShow();
	}
}
