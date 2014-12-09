package t.n.jarmanager;

import java.io.File;


public class TabTransitStateImpl implements ITabTransitState {

	private boolean fromCatalogTabPopupMenu;
	private File targetJarFile;
	private String candidateJarFiles;

	public TabTransitStateImpl() {
		 fromCatalogTabPopupMenu = false;
		 candidateJarFiles = "";
	}

	@Override
	public void setRegistCandidateJarFiles(String candidateJarFiles) {
		fromCatalogTabPopupMenu = false;
		this.candidateJarFiles = candidateJarFiles;
	}

	@Override
	public void setFromCatalogTabPopupMenu(File targetJarFile) {
		fromCatalogTabPopupMenu = true;
		this.targetJarFile = targetJarFile;
		candidateJarFiles = "";
	}
	@Override
	public File getTargetJarFile() {
		return targetJarFile;
	}

	@Override
	public boolean isFromCatalogTabPopupMenu() {
		return fromCatalogTabPopupMenu;
	}

	@Override
	public void clearStatus() {
		fromCatalogTabPopupMenu = false;
		targetJarFile = null;
		candidateJarFiles = "";
	}

	@Override
	public boolean hasUnregisteredCandidateJarFiles() {
		return candidateJarFiles.length() > 0;
	}

	@Override
	public String getUnregisteredCandidateJarFiles() {
		return candidateJarFiles;
	}
}
