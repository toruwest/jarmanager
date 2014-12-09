package t.n.jarmanager;

import java.io.File;
import java.util.List;

public interface IJarCatalog {

	/**
	 * Start checking the consistency of the catalog with actual JAR files
	 * The result is notified with CatalogEventListener interface.
	 */
	public void startVerify();

	/**
	 * Returns whether the veryfy action is performing or not.
	 */
	public boolean isVerifying();

	/**
	 * Cancel the startVerify()をキャンセルする。
	 */
	public void cancelVerifying();

	/**
	 * Start scanning the JAR files under the specified path recursively.
	 * The result is notified with CatalogEventListener interface.
	 *
	 * @param path
	 */
	public void startScan(File path);

	/**
	 * Returns whether the scan action is performing or not.
	 */
	public boolean isScanning();

	/**
	 * Cancel the scan action.
	 */
	public void cancelScanning();


	/**
	 * Update the catalog and remove/replace entries if they are invalid.
	 * The result is notified with CatalogEventListener interface.
	 * @return
	 */
	public void doUpdate();

	/**
	 * Add the specified JAR file to catalog. Checksum (MD5) is calculated.
	 * The result is notified with CatalogEventListener interface.
	 * @param File[] jarFiles
	 */
	public void addJars(List<File> jarFiles);

	/**
	 * Find the specified class from catalog.
	 * The result is notified with CatalogEventListener interface.
	 * @param className
	 */
	public void doFindClass(String className);

	/**
	 * Get the list of JAR files in the catalog.
	 * The result is notified with CatalogEventListener interface.
	 */
	public void requestJarEntryList();

	/**
	 * Get the status of specified JAR files in the catalog.
	 * The result is notified with CatalogEventListener interface.
	 */
	public void checkCatalogRegistStatus(List<File> candidateJarFileList);

	/**
	 * Get the content of specified JAR file in the catalog.
	 * The result is notified with CatalogEventListener interface.
	 */
	public void requestJarContent(File targetJarFile);

	/**
	 * Get the content of specified JAR file in the catalog with found position of the searchClass
	 * The result is notified with CatalogEventListener interface.
	 */
	void requestJarContentWithFoundPosition(File selectedJarFile, String searchClass);

	/**
	 * Shutdown the database of catalog.
	 */
	void shutdown();

}