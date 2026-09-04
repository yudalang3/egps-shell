package egps2.utils.common.model.datatransfer;

/**
 * Result provides shared utility logic for eGPS modules and UI.
 */
public class Result<T> {
	
	T result;
	
	boolean success;
	
	String errorTitle;
	
	String errorMsg;
	
	/**
	 * 
	 */
	//int errorCode;
	
	
	public void setErrorInformation(String errorTitle,String errorMsg) {
		this.errorTitle = errorTitle;
		this.errorMsg = errorMsg;
	}

	public Result(T result, boolean success) {
		super();
		this.result = result;
		this.success = success;
	}

	public T getResult() {
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public String getErrorTitle() {
		return errorTitle;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setErrorTitle(String errorTitle) {
		this.errorTitle = errorTitle;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	


	
	
}
