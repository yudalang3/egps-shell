package egps2.modulei;

/**
 * Base implementation of the {@link IInformation} interface.
 * This class provides a simple JavaBean-style implementation for storing
 * and retrieving module information metadata.
 *
 * <p>The information includes:</p>
 * <ul>
 *   <li>How the module is launched</li>
 *   <li>What data is invoked/used</li>
 *   <li>How the user operates the module</li>
 *   <li>Summary of results produced</li>
 * </ul>
 *
 * @see IInformation
 * @author eGPS Development Team
 */
public class BaseInformationImp implements IInformation {
	
	private String howModuleLaunch;
	private String whatDataInvoked;
	private String howUserOperates;
	private String summaryOfResults;

    public BaseInformationImp() {

    }
	@Override
	public String getHowModuleLaunch() {
		return howModuleLaunch;
	}

	@Override
	public String getWhatDataInvoked() {
		return whatDataInvoked;
	}

	@Override
	public String getHowUserOperates() {
		return howUserOperates;
	}

	@Override
	public String getSummaryOfResults() {
		return summaryOfResults;
	}

	public void setHowModuleLaunch(String howModuleLaunch) {
		this.howModuleLaunch = howModuleLaunch;
	}

	public void setWhatDataInvoked(String whatDataInvoked) {
		this.whatDataInvoked = whatDataInvoked;
	}

	public void setHowUserOperates(String howUserOperates) {
		this.howUserOperates = howUserOperates;
	}

	public void setSummaryOfResults(String summaryOfResults) {
		this.summaryOfResults = summaryOfResults;
	}

}
