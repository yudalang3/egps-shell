package egps2.utils.common.model.datatransfer;

/**
 * EGPSClipboard provides shared utility logic for eGPS modules and UI.
 */
public class EGPSClipboard {

	private Object oldValue = null;
	
	/** It denote current operation is cut or copy ? */
	private String cutOrcopy = null;
	
	public void setContents(Object newValue) {
		oldValue = newValue;
	}
	
	public Object getContents() {
		return oldValue;
	}

	public final String getCutOrcopy() {
		return cutOrcopy;
	}

	public final void setCutOrcopy(String cutOrcopy) {
		this.cutOrcopy = cutOrcopy;
	}
}
