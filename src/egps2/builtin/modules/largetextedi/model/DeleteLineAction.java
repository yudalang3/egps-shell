package egps2.builtin.modules.largetextedi.model;

/**
 * DeleteLineAction belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class DeleteLineAction extends TextEditAction {

	private LineObj lineObj;

	private int lineNumber;

	private long lineHeadOffset;

	private String lineTerminator;

	private int index = 0; // 当undo,redo时,根据index找到返回的对象;如:当将第二行数据删除时,显示行号变成-2行;继续删除原来的第三行数据,显示行号也变为-2;因此根据index可以知道进行undo时找到指定的放回对象

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {

		return index;
	}

	public DeleteLineAction(LineObj lineObjBefore) {
		setLineObjBefore(lineObjBefore);

		setEditAction(DELETE_LINE);
	}

	public LineObj getLineObj() {
		return lineObj;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getLineTerminator() {
		return lineTerminator;
	}

	public long getLineHeadOffset() {
		return lineHeadOffset;
	}

	private void setLineObjBefore(LineObj lineObj) {
		this.lineObj = lineObj;
		this.lineHeadOffset = lineObj.getHeadOffset();
		this.lineTerminator = lineObj.getLineTerminator();
		this.lineNumber = lineObj.getLineNumber();
	}

	@Override
	public int getLineNumberOnShow() {

		return lineObj.getLineNumberOnShow();
	}

}
