package egps2.utils.common.model.datatransfer;

import java.util.Vector;


/**
 * EGPSSelection provides shared utility logic for eGPS modules and UI.
 */
public class EGPSSelection {

	private Vector<Object> oneElement = null;

	public EGPSSelection() {
		oneElement = new Vector<Object>(8);
	}

	public void addSelection(Object currentSelection) {
		oneElement.clear();
		oneElement.add(currentSelection);
	}

	public void removeSelection() {
		oneElement.clear();
	}

	public final Vector<Object> getOneElement() {
		return oneElement;
	}

	public Object getSelected() {
		return getOneElement().get(0);
	}
}
