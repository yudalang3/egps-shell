package egps2.builtin.modules.largetextedi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * OneBlockShownContent belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class OneBlockShownContent {

	private static int numOfLinesStored = 100;

	private long block_offset_Start = -1;
	private long block_offset_End = -1;

	private int trueNumberOfOneBlocks = 0;

	private List<LineObj> lineObjs = new ArrayList<>(numOfLinesStored);

	public OneBlockShownContent() {

	}

	public long getBlock_offset_Start() {
		return block_offset_Start;
	}

	public void setBlock_offset_Start(long block_offset_Start) {
		this.block_offset_Start = block_offset_Start;
	}

	public long getBlock_offset_End() {
		return block_offset_End;
	}

	public void setBlock_offset_End(long block_offset_End) {
		this.block_offset_End = block_offset_End;
	}

	public List<String> getAllLines() {

		List<String> allLines = new ArrayList<String>(numOfLinesStored);

		for (int i = 0; i < lineObjs.size(); i++) {
			LineObj lineObj = lineObjs.get(i);
			allLines.add(lineObj.getLine());
		}

		return allLines;
	}

	public void addLine(LineObj lineObj) {
		lineObjs.add(lineObj);
	}

	public int getFirstLineNumber() {

		if (lineObjs.get(0) != null) {
			return lineObjs.get(0).getLineNumber();
		} else {
			return -1;
		}

	}

	public int getLastLineNumber() {

		if (lineObjs.get(lineObjs.size() - 1) != null) {
			return lineObjs.get(lineObjs.size() - 1).getLineNumber();
		} else {
			return -1;
		}

	}

	public List<LineObj> getLineObj() {
		return lineObjs;
	}

	public int getTrueNumberOfOneBlocks() {
		return trueNumberOfOneBlocks;
	}

	public void setTrueNumberOfOneBlocks(int numOfStoredBlocks) {
		this.trueNumberOfOneBlocks = numOfStoredBlocks;
	}
}
