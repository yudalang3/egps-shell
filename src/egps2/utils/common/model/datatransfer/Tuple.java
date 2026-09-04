package egps2.utils.common.model.datatransfer;

/**
 * Tuple provides shared utility logic for eGPS modules and UI.
 */
public class Tuple <E>{
	E element;
	
	
	public Tuple(E element) {
		this.element = element;
	}

	public void setElement(E element) {
		this.element = element;
	}
	
	public E getElement() {
		return element;
	}
}
