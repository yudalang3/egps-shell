package egps2.utils.common.tablelike;

/**
 * MatrixTableContentBean provides shared utility logic for eGPS modules and UI.
 */
public class MatrixTableContentBean {

	boolean hasRows;
	boolean hasColumn;
	String[] columnNames;
	String[][] tableElements;
	
	MatrixTablePurpose purpose = MatrixTablePurpose.GENERAL;
	
	public MatrixTableContentBean() {
	}
	
	int numOfNAs;
	int numOfRows;
	int numOfColumns;
	int lineSeparetorIndex;
	
	public boolean isHasRows() {
		return hasRows;
	}
	public void setHasRows(boolean hasRows) {
		this.hasRows = hasRows;
	}
	public boolean isHasColumn() {
		return hasColumn;
	}
	public void setHasColumn(boolean hasColumn) {
		this.hasColumn = hasColumn;
	}
	public String[] getColumnNames() {
		return columnNames;
	}
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	public String[][] getTableElements() {
		return tableElements;
	}
	
	public void setTableElements(String[][] tableElements) {
		this.tableElements = tableElements;
	}
	public MatrixTablePurpose getPurpose() {
		return purpose;
	}
	public void setPurpose(MatrixTablePurpose purpose) {
		this.purpose = purpose;
	}
	public int getNumOfNAs() {
		return numOfNAs;
	}
	public void setNumOfNAs(int numOfNAs) {
		this.numOfNAs = numOfNAs;
	}
	public int getNumOfRows() {
		return numOfRows;
	}
	public void setNumOfRows(int numOfRows) {
		this.numOfRows = numOfRows;
	}
	public int getNumOfColumns() {
		return numOfColumns;
	}
	public void setNumOfColumns(int numOfColumns) {
		this.numOfColumns = numOfColumns;
	}
	public int getLineSeparetorIndex() {
		return lineSeparetorIndex;
	}
	public void setLineSeparetorIndex(int lineSeparetorIndex) {
		this.lineSeparetorIndex = lineSeparetorIndex;
	}
	
	public LineSparator getLineSeparator() {
		return LineSparator.values()[lineSeparetorIndex];
	}
	
}
