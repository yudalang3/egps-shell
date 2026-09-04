package egps2.modulei;

/**
 * The DataOperator interface provides methods for importing and exporting data.
 * Implementing classes should provide specific functionality for these
 * operations.
 */
public interface DataOperator {

	/**
	 * Checks if the current data source can be imported.
	 *
	 * @return {@code true} if the data source can be imported, {@code false}
	 *         otherwise.
	 */
	boolean canImport();

	/**
	 * Performs the action of importing data from a data source. This method should
	 * be implemented to handle the specifics of the data import process.
	 */
	void importData();

	/**
	 * Checks if the current data can be exported.
	 *
	 * @return {@code true} if the data can be exported, {@code false} otherwise.
	 */
	boolean canExport();

	/**
	 * Performs the action of exporting data. This method should be implemented to
	 * handle the specifics of the data export process.
	 */
	void exportData();
}