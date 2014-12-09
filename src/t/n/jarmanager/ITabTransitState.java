package t.n.jarmanager;

import java.io.File;


public interface ITabTransitState {

	void setFromCatalogTabPopupMenu(File targetJarFile);

	boolean isFromCatalogTabPopupMenu();

	void clearStatus();

	File getTargetJarFile();

	void setRegistCandidateJarFiles(String candidateJarFiles);

	boolean hasUnregisteredCandidateJarFiles();

	String getUnregisteredCandidateJarFiles();

}
