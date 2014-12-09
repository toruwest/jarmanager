package t.n.jarmanager;

import java.io.File;
import java.util.List;
import java.util.Map;

import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.CatalogRegistStatus;
import t.n.jarmanager.dto.JarFileContent;
import t.n.jarmanager.dto.JarInfo;

public interface ICatalogEventListener {

	public void notifyScanInProgress(boolean isFolder, File file);
	public void notifyScanProgressStatus(File file, CatalogRegistResult status);
	public void notifyScanComplete(boolean completedOrCanceled, Map<File, CatalogRegistResult> statusMap);
	public void notifyAddJarComplete(Map<File, CatalogRegistResult> statusMap);
	public void notifyCheckCatalogStatusComplete(Map<File, CatalogRegistStatus> statusMap);

	public void notifyFindClassComplete(List<String> result);
	public void notifyIndexedJarContentsFetchComplete(JarFileContent jarFileContent, int length);

	public void notifyUpdateComplete(Map<JarInfo, CatalogRegistResult> statusMap);

	public void notifyVerifyInProgress(int totalCount, int count, String jarFilename);
	public void notifyVerifyComplete(Map<File, CatalogEntryStatus> statusMap, boolean isCanceled);

	public void notifyJarEntryListFetchComplete(List<JarInfo> result);
	public void notifyJarContentsFetchComplete(String content);

}
