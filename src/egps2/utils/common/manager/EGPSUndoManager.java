package egps2.utils.common.manager;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * EGPSUndoManager provides shared utility logic for eGPS modules and UI.
 */
public class EGPSUndoManager extends UndoManager {

	public int nextUndoIndex() {
		UndoableEdit first = editToBeUndone();
	    if (first != null) {
	    	
	    	return edits.indexOf(first);
	    } else {
	    	return -1;
	    }
	}

	public int nextRedoIndex() {
		UndoableEdit first = editToBeRedone();
	    if (first != null) {
	    	return edits.indexOf(first);
	    } else {
	    	return -1;
	    }
    }

	public int undoCount() {
		return nextUndoIndex() + 1;
	}

	public int redoCount() {
		int pos = nextRedoIndex();
	    if (pos == -1) {
	       return 0;
	    } else {
	    	return edits.size() - pos;
	    }
	}

	public UndoableEdit getEdit(int pos) {
		return edits.get(pos);
	}

	public UndoableEdit getUndoEdit(int pos) {
		int first = nextUndoIndex();
	    if (first == -1) {
	       throw new IndexOutOfBoundsException("There are no undoEdits.");
	    } else {
	    	return edits.get(first - pos);
	    }
	}

	public UndoableEdit getRedoEdit(int pos) {
	
		int first = nextRedoIndex();
	    if (first == -1) {
	       throw new IndexOutOfBoundsException("There are no redoEdits.");
	    } else {
	    	return edits.get(first + pos);
	    }
	}

	@Override
	public void redoTo(UndoableEdit edit) throws CannotRedoException {
		super.redoTo(edit);
	}

	@Override
	public void undoTo(UndoableEdit edit) throws CannotUndoException {
	    super.undoTo(edit);
	}

	public boolean contains(UndoableEdit edit) {
	     return edits.contains(edit);
	}
}
