package t.n.jarmanager.dto;

import java.io.File;
import java.util.Date;


public final class JarInfo {
	private final int id;
	private final String jarShortFileName; //JAR file name exclude parent dirs
	private final String folder;
	private final long jarFileLastModified;
	private final String checksum;
	private final boolean hasMainClass;
	private final boolean isSigned;
	private final long registDate;
	private CatalogEntryStatus entryStatus;

	public JarInfo(int id, String jarShortFileName, String folder, long jarFileLastModified, String checksum,
			boolean hasMainClass, boolean isSigned, long registDate, CatalogEntryStatus entryStatus) {
		this.id = id;
		this.jarShortFileName = jarShortFileName;
		this.folder = folder;
		this.jarFileLastModified = jarFileLastModified;
		this.checksum = checksum;
		this.hasMainClass = hasMainClass;
		this.isSigned = isSigned;
		this.registDate = registDate;
		this.entryStatus = entryStatus;
	}

	public Date getJarFileLastModified() {
		return new Date(jarFileLastModified);
	}

	public String getChecksum() {
		return checksum;
	}

	public boolean hasMainClass() {
		return hasMainClass;
	}

	public boolean isSigned() {
		return isSigned;
	}

	public int getId() {
		return id;
	}

	public String getJarFullpathname() {
		return folder + File.separator + jarShortFileName;
	}

	public String getJarShortFileName() {
		return jarShortFileName;
	}

	public String getFolder() {
		return folder;
	}

	public Date getRegistDate() {
		return new Date(registDate);
	}

	public CatalogEntryStatus getEntryStatus() {
		return entryStatus;
	}

	public void setEntryStatus(CatalogEntryStatus entryStatus) {
		this.entryStatus = entryStatus;
	}

	public File getJarFullpathnameAsFile() {
		return new File(getJarFullpathname());
	}
}
