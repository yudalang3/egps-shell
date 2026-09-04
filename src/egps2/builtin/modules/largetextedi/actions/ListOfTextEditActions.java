package egps2.builtin.modules.largetextedi.actions;

import java.util.ArrayList;
import java.util.List;

import egps2.builtin.modules.largetextedi.model.DeleteLineAction;
import egps2.builtin.modules.largetextedi.model.LineObj;
import egps2.builtin.modules.largetextedi.model.NewLinesAction;
import egps2.builtin.modules.largetextedi.model.RevisedLineAction;
import egps2.builtin.modules.largetextedi.model.TextEditAction;


/**
 * ListOfTextEditActions belongs to a built-in eGPS module (loader, panel, or helper).
 */
public class ListOfTextEditActions {

	protected List<TextEditAction> listOfTextEditActions = new ArrayList<TextEditAction>(100);
	protected int currentEndPos = 0; // exclusive

	private int lineNumberOnShow;

	public TextEditAction getEditedLineObj(int index) throws IndexOutOfBoundsException {

		if (index < 0 || index >= currentEndPos)
			// if (index < 0 || index >= listOfTextEditActions.size())
			throw new IndexOutOfBoundsException("index = " + index);

		return listOfTextEditActions.get(index);
	}

	public int getNewBelongingGroupIndexAssigningValue() {

		return listOfTextEditActions.size();
	}

	public boolean canUndo() {

		boolean ok = false;

		if (currentEndPos > 0) {
			ok = true;
		}

		return ok;
	}

	public void undo() {

		int tempCurrentEndPos = currentEndPos - 1;

		TextEditAction textEditAction = getEditedLineObj(tempCurrentEndPos);

		lineNumberOnShow = textEditAction.getLineNumberOnShow();

		int belongingGroupIndex = textEditAction.getBelongingGroupIndex();

		currentEndPos--;

		recursivelyRecoverUndo(textEditAction);

		recursivelyRecoverUndo(belongingGroupIndex, tempCurrentEndPos);

	}

	public void recursivelyRecoverUndo(TextEditAction textEditAction) {

		int editAction = textEditAction.getEditAction();

		int lineNumberOnShow = textEditAction.getLineNumberOnShow();

		if (editAction == TextEditAction.NEW_LINE) {

			for (int i = 0; i < currentEndPos; i++) {

				TextEditAction editedLineObj = getEditedLineObj(i);

				int tempLineNumberOnShow = editedLineObj.getLineNumberOnShow();

				if (tempLineNumberOnShow <= lineNumberOnShow) {

					continue;
				}

				int editAction2 = editedLineObj.getEditAction();

				if (editAction2 == TextEditAction.NEW_LINE) {

					NewLinesAction newLinesAction = (NewLinesAction) editedLineObj;

					newLinesAction.getNewLineObj().setLineNumberOnShow(tempLineNumberOnShow - 1);

				} else if (editAction2 == TextEditAction.REVISED) {
					RevisedLineAction revisedAction = (RevisedLineAction) editedLineObj;

					revisedAction.getRevisedLineObj().setLineNumberOnShow(tempLineNumberOnShow - 1);

				}

			}

		} else if (editAction == TextEditAction.REVISED) {

			for (int i = currentEndPos - 1; i >= 0; i--) {

				TextEditAction editedLineObj = getEditedLineObj(i);

				int tempLineNumberOnShow = editedLineObj.getLineNumberOnShow();

				if (Math.abs(tempLineNumberOnShow) != lineNumberOnShow) {

					continue;
				}

				int editAction2 = editedLineObj.getEditAction();

				if (editAction2 == TextEditAction.NEW_LINE) {

					NewLinesAction newLinesAction = (NewLinesAction) editedLineObj;

					newLinesAction.getNewLineObj().setLineNumberOnShow(lineNumberOnShow);

					break;

				} else if (editAction2 == TextEditAction.REVISED) {

					RevisedLineAction revisedAction = (RevisedLineAction) editedLineObj;

					revisedAction.getRevisedLineObj().setLineNumberOnShow(lineNumberOnShow);
					break;
				}

			}
		} else if (editAction == TextEditAction.DELETE_LINE) {

			for (int i = currentEndPos - 1; i >= 0; i--) {

				TextEditAction editedLineObj = getEditedLineObj(i);

				int tempLineNumberOnShow = editedLineObj.getLineNumberOnShow();

				if (Math.abs(tempLineNumberOnShow) < lineNumberOnShow) {

					continue;
				}

				DeleteLineAction deleteLineAction = (DeleteLineAction) textEditAction;

				int editAction2 = editedLineObj.getEditAction();

				if (editAction2 == TextEditAction.NEW_LINE) {

					NewLinesAction newLinesAction = (NewLinesAction) editedLineObj;

					if (-tempLineNumberOnShow == lineNumberOnShow
							&& deleteLineAction.getIndex() == newLinesAction.getIndex()) {

						newLinesAction.getNewLineObj().setLineNumberOnShow(lineNumberOnShow);

					} else if (tempLineNumberOnShow >= lineNumberOnShow) {
						newLinesAction.getNewLineObj().setLineNumberOnShow(tempLineNumberOnShow + 1);
					}

				} else if (editAction2 == TextEditAction.REVISED) {

					RevisedLineAction revisedAction = (RevisedLineAction) editedLineObj;

					if (-tempLineNumberOnShow == lineNumberOnShow
							&& deleteLineAction.getIndex() == revisedAction.getIndex()) {

						revisedAction.getRevisedLineObj().setLineNumberOnShow(lineNumberOnShow);

						// isRevisedLineActionAlreadyUndo = true;

					} else if (tempLineNumberOnShow >= lineNumberOnShow) {
						revisedAction.getRevisedLineObj().setLineNumberOnShow(tempLineNumberOnShow + 1);
					}
				}

			}
		}
	}

	public void recursivelyRecoverUndo(int belongingGroupIndex, int tempCurrentEndPos) {

		for (int i = tempCurrentEndPos - 1; i >= 0; i--) {

			TextEditAction textEditAction = getEditedLineObj(i);

			int belongingGroupIndex2 = textEditAction.getBelongingGroupIndex();

			if (belongingGroupIndex == belongingGroupIndex2) {
				currentEndPos--;
				recursivelyRecoverUndo(textEditAction);
				lineNumberOnShow = textEditAction.getLineNumberOnShow();

			} else {
				break;
			}

		}

	}

	public boolean canRedo() {

		boolean ok = false;

		if (currentEndPos <= listOfTextEditActions.size() - 1) {
			ok = true;
		}
		return ok;
	}

	public void redo() {

		TextEditAction textEditAction = listOfTextEditActions.get(currentEndPos);

		lineNumberOnShow = textEditAction.getLineNumberOnShow();

		lineNumberPlusOneAfterShowLineNumber(textEditAction);

		int belongingGroupIndex = textEditAction.getBelongingGroupIndex();

		currentEndPos++;

		recursivelyRecoverRedo(belongingGroupIndex);

	}

	public void recursivelyRecoverRedo(TextEditAction textEditAction) {

		int editAction = textEditAction.getEditAction();

		int lineNumberOnShow = textEditAction.getLineNumberOnShow();

		if (editAction == TextEditAction.NEW_LINE) {

			for (int i = 0; i < currentEndPos; i++) {

				TextEditAction editedLineObj = getEditedLineObj(i);

				int tempLineNumberOnShow = editedLineObj.getLineNumberOnShow();

				if (tempLineNumberOnShow <= lineNumberOnShow) {
					continue;
				}

				int editAction2 = editedLineObj.getEditAction();

				if (editAction2 == TextEditAction.NEW_LINE) {

					NewLinesAction newLinesAction = (NewLinesAction) editedLineObj;

					newLinesAction.getNewLineObj().setLineNumberOnShow(tempLineNumberOnShow - 1);

				} else if (editAction2 == TextEditAction.REVISED) {
					RevisedLineAction revisedAction = (RevisedLineAction) editedLineObj;

					revisedAction.getRevisedLineObj().setLineNumberOnShow(tempLineNumberOnShow - 1);

				}

			}

		} else if (editAction == TextEditAction.REVISED) {

			for (int i = currentEndPos - 1; i >= 0; i--) {

				TextEditAction editedLineObj = getEditedLineObj(i);

				int tempLineNumberOnShow = editedLineObj.getLineNumberOnShow();

				if (Math.abs(tempLineNumberOnShow) != lineNumberOnShow) {

					continue;
				}

				int editAction2 = editedLineObj.getEditAction();

				if (editAction2 == TextEditAction.NEW_LINE) {

					NewLinesAction newLinesAction = (NewLinesAction) editedLineObj;

					newLinesAction.getNewLineObj().setLineNumberOnShow(lineNumberOnShow);

					break;

				} else if (editAction2 == TextEditAction.REVISED) {

					RevisedLineAction revisedAction = (RevisedLineAction) editedLineObj;

					revisedAction.getRevisedLineObj().setLineNumberOnShow(lineNumberOnShow);
					break;
				}

			}
		} else if (editAction == TextEditAction.DELETE_LINE) {

			boolean isRevisedLineActionAlreadyUndo = false;

			// boolean isNewLinesActionAlreadyUndo = false;

			for (int i = currentEndPos - 1; i >= 0; i--) {

				TextEditAction editedLineObj = getEditedLineObj(i);

				int tempLineNumberOnShow = editedLineObj.getLineNumberOnShow();

				if (Math.abs(tempLineNumberOnShow) < lineNumberOnShow) {

					continue;
				}

				int editAction2 = editedLineObj.getEditAction();

				if (editAction2 == TextEditAction.NEW_LINE) {

					NewLinesAction newLinesAction = (NewLinesAction) editedLineObj;

					if (-tempLineNumberOnShow == lineNumberOnShow) {

						newLinesAction.getNewLineObj().setLineNumberOnShow(lineNumberOnShow);
						// isNewLinesActionAlreadyUndo = true;

					} else if (tempLineNumberOnShow >= lineNumberOnShow) {
						newLinesAction.getNewLineObj().setLineNumberOnShow(tempLineNumberOnShow + 1);
					}

				} else if (editAction2 == TextEditAction.REVISED) {

					RevisedLineAction revisedAction = (RevisedLineAction) editedLineObj;

					if (-tempLineNumberOnShow == lineNumberOnShow && !isRevisedLineActionAlreadyUndo) {

						revisedAction.getRevisedLineObj().setLineNumberOnShow(lineNumberOnShow);
						isRevisedLineActionAlreadyUndo = true;

					} else if (tempLineNumberOnShow >= lineNumberOnShow) {
						revisedAction.getRevisedLineObj().setLineNumberOnShow(tempLineNumberOnShow + 1);
					}
				}

			}
		}

	}

	public void recursivelyRecoverRedo(int belongingGroupIndex) {

		int tempCurrentEndPos = currentEndPos;

		int size = listOfTextEditActions.size();

		if (tempCurrentEndPos > size) {

			return;
		}

		for (int i = tempCurrentEndPos; i < size; i++) {

			TextEditAction textEditAction = listOfTextEditActions.get(i);

			int tempBelongingGroupIndex = textEditAction.getBelongingGroupIndex();

			if (belongingGroupIndex == tempBelongingGroupIndex) {

				lineNumberPlusOneAfterShowLineNumber(textEditAction);
				currentEndPos++;

				lineNumberOnShow = textEditAction.getLineNumberOnShow();
			} else {

				break;
			}
		}

	}

	public void addTextEditActions(TextEditAction editedLineObj) {

		for (int i = listOfTextEditActions.size() - 1; i >= currentEndPos; i--) {
			listOfTextEditActions.remove(i);
		}

		listOfTextEditActions.add(editedLineObj);
		currentEndPos++;
	}

	public void lineNumberPlusOneAfterShowLineNumber(TextEditAction textEditAction) {

		int newEditAction = textEditAction.getEditAction();

		if (newEditAction == TextEditAction.NEW_LINE) {

			NewLinesAction newLinesAction = (NewLinesAction) textEditAction;

			int showLineNumber = newLinesAction.getNewLineObj().getLineNumberOnShow();

			for (int i = 0; i < currentEndPos; i++) {

				TextEditAction editActionObj = getEditedLineObj(i);

				int editAction = editActionObj.getEditAction();

				if (editAction == TextEditAction.NEW_LINE) {

					NewLinesAction linesAction = (NewLinesAction) editActionObj;

					LineObj newLineObj = linesAction.getNewLineObj();

					int lineNumberOnShow = newLineObj.getLineNumberOnShow();

					if (lineNumberOnShow >= showLineNumber) {

						newLineObj.setLineNumberOnShow(lineNumberOnShow + 1);
					}
				} else if (editAction == TextEditAction.REVISED) {

					RevisedLineAction linesAction = (RevisedLineAction) editActionObj;

					LineObj lineObj = linesAction.getRevisedLineObj();

					int lineNumberOnShow = lineObj.getLineNumberOnShow();

					if (lineNumberOnShow >= showLineNumber) {

						lineObj.setLineNumberOnShow(lineNumberOnShow + 1);

					}

				}

			}
		} else if (newEditAction == TextEditAction.DELETE_LINE) {

			DeleteLineAction deleteLineAction = (DeleteLineAction) textEditAction;

			int index = deleteLineAction.getIndex();

			LineObj lineObjBefore = deleteLineAction.getLineObj();

			int showLineNumber = lineObjBefore.getLineNumberOnShow();

			for (int i = 0; i < currentEndPos; i++) {

				TextEditAction editActionObj = getEditedLineObj(i);

				int editAction = editActionObj.getEditAction();

				if (editAction == TextEditAction.NEW_LINE) {

					NewLinesAction linesAction = (NewLinesAction) editActionObj;

					LineObj newLineObj = linesAction.getNewLineObj();

					int lineNumberOnShow = newLineObj.getLineNumberOnShow();

					if (lineNumberOnShow > showLineNumber) {

						newLineObj.setLineNumberOnShow(lineNumberOnShow - 1);
					} else if (lineNumberOnShow == showLineNumber) {

						linesAction.setIndex(index);

						newLineObj.setLineNumberOnShow(-lineNumberOnShow);
					}
				} else if (editAction == TextEditAction.REVISED) {

					RevisedLineAction revisedLineAction = (RevisedLineAction) editActionObj;

					LineObj lineObj = revisedLineAction.getRevisedLineObj();

					int lineNumberOnShow = lineObj.getLineNumberOnShow();

					if (lineNumberOnShow > showLineNumber) {

						lineObj.setLineNumberOnShow(lineNumberOnShow - 1);

					} else if (lineNumberOnShow == showLineNumber) {
						revisedLineAction.setIndex(index);

						lineObj.setLineNumberOnShow(-lineNumberOnShow);
					}

				}

			}

		} else if (newEditAction == TextEditAction.REVISED) {

			RevisedLineAction revisedLineAction = (RevisedLineAction) textEditAction;

			LineObj revisedLineObj = revisedLineAction.getRevisedLineObj();

			int showLineNumber = revisedLineObj.getLineNumberOnShow();

			for (int i = 0; i < currentEndPos; i++) {

				TextEditAction editActionObj = getEditedLineObj(i);

				int editAction = editActionObj.getEditAction();

				if (editAction == TextEditAction.REVISED) {

					RevisedLineAction linesAction = (RevisedLineAction) editActionObj;

					LineObj lineObj = linesAction.getRevisedLineObj();

					int lineNumberOnShow = lineObj.getLineNumberOnShow();

					if (showLineNumber == lineNumberOnShow) {
						lineObj.setLineNumberOnShow(-lineNumberOnShow);
					}
				}

			}

		}

	}

	public int getLineNumberOnShow() {

		return lineNumberOnShow;
	}

	public int size() {
		return currentEndPos;
	}

	public void clearEditActions() {
		listOfTextEditActions.clear();
	}
}
