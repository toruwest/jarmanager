package t.n.jarmanager;

import java.io.File;
import java.util.List;

public interface IMainController {

	void doFindClass(String className);

	void prepareForRegistering(List<File> candidateJarFileList);

	void startVerify();
	void cancelVerifying();

	void doUpdate();

	void requestJarEntryList();

	void addJars();

	void cancelScanning();

	void startScan(File f);

	void requestJarFileContentsInCatalog(File targetJarFile);

	void dumpJarFileContentsInCatalogWithIndex(File targetJarFile, String searchClass);

	File getPrevDir();

	boolean isBrowserDirectOpen();
	void saveIsBrowserDirectOpen(boolean isBrowserDirectOpen);

	void saveDir(File prevDir);
//	void setDumpJarFileContensEnabled(boolean b);
	void dumpJarFileContents();

	void cancelAll();

	boolean isDoingSomethig();

	void shutdown();
}
