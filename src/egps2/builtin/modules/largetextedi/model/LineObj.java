package egps2.builtin.modules.largetextedi.model;

/**
 * LineObj belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class LineObj implements Comparable<LineObj> {

	public String line;
	public String lineTerminator;

	public int lineNumber = -1;

	public int lineNumberOnShow = -1;

	public long headOffset = -1; // skip(headOffset), then readLine().equal(line)

	public LineObj() {

	}

	public LineObj(String line, int lineNumber, long headOffset, String lineTerminator) {
		this.line = line;
		this.lineNumber = lineNumber;
		this.headOffset = headOffset;
		this.lineTerminator = lineTerminator;
	}

	public int getLineNumberOnShow() {
		return lineNumberOnShow;
	}

	public void setLineNumberOnShow(int lineNumberOnShow) {
		this.lineNumberOnShow = lineNumberOnShow;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public long getHeadOffset() {
		return headOffset;
	}

	public void setHeadOffset(long headOffset) {
		this.headOffset = headOffset;
	}

	public String getLineTerminator() {
		return lineTerminator;
	}

	public void setLineTerminator(String lineTerminator) {
		this.lineTerminator = lineTerminator;
	}

	@Override
	public Object clone() {

		LineObj clonedObj = new LineObj();
		clonedObj.setHeadOffset(this.getHeadOffset());
		clonedObj.setLine(this.getLine());
		clonedObj.setLineNumber(this.getLineNumber());
		clonedObj.setLineTerminator(this.getLineTerminator());

		return clonedObj;
	}

	@Override
	public int compareTo(LineObj o) {
		
		return this.lineNumberOnShow - o.lineNumberOnShow;
	}

}
