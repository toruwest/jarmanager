package t.n.jarmanager.util;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.JarFileViewProperty;
import t.n.jarmanager.dto.JarInfo;

public interface IDBUtil {
	public boolean isReady();

	public String getChecksumFromCatalog(File jarFile);

	public boolean isAlreadyRegisterd(String folder, String jarname);

	public CatalogRegistResult addOrReplaceCatalog(JarFileViewProperty prop, long registDate, CatalogEntryStatus status);

	public List<JarInfo> getJarEntryList();

	public List<String> findClassLoose(String targetName);
	public List<String> findClassStrict(String targetName);

	public CatalogRegistResult removeJar(File jarFile1);

	public void shutdown();

	public long getLastUpdatedDateFromCatalog(File candidateJarFile);

	public JarFileViewProperty getJarContentInCatalog(File jarfile);

	public List<String> getClassListInCatalog(String folder, String jarname);
	public List<String> getClassListInCatalog(JarInfo jarInfo);

	public void setAutoCommit(boolean b) throws SQLException;

	public void rollback() throws SQLException;




}