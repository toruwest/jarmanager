package t.n.jarmanager;

import java.io.File;
import java.util.List;
import java.util.Map;

import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.CatalogRegistStatus;
import t.n.jarmanager.dto.JarFileContent;
import t.n.jarmanager.dto.JarInfo;

public class EventDispatcher {
	protected ICatalogEventListener listener;

	public EventDispatcher(ICatalogEventListener catalogEventListener) {
		this.listener= catalogEventListener;
	}

	public void fireScanInProgressEvent(boolean b, File file) {
		listener.notifyScanInProgress(b, file);
	}

	public void fireScanProgressStatusEvent(File file, CatalogRegistResult status) {
		listener.notifyScanProgressStatus(file, status);
	}

	public void fireScanCompleteEvent(boolean completedOrCanceled, Map<File, CatalogRegistResult> statusMap) {
		listener.notifyScanComplete(completedOrCanceled, statusMap);
	}

	public void fireAddJarCompleteEvent(Map<File, CatalogRegistResult> statusMap) {
		listener.notifyAddJarComplete(statusMap);
	}

	public void fireFindClassCompleteEvent(List<String> result) {
		listener.notifyFindClassComplete(result);
	}

	public void fireVerifyCompleteEvent(Map<File, CatalogEntryStatus> statusMap, boolean isCanceled) {
		listener.notifyVerifyComplete(statusMap, isCanceled);
	}

	public void fireUpdateCompleteEvent(Map<JarInfo, CatalogRegistResult> status) {
		listener.notifyUpdateComplete(status);
	}

	public void fireCheckCatalogStatusCompleteEvent(Map<File, CatalogRegistStatus> statusMap) {
		listener.notifyCheckCatalogStatusComplete(statusMap);
	}

	public void fireJarContentsFetchCompleteEvent(String content) {
		listener.notifyJarContentsFetchComplete(content);
	}

	public void fireJarEntryListFetchCompleteEvent(List<JarInfo> result) {
		listener.notifyJarEntryListFetchComplete(result);
	}

	public void fireIndexedJarContentFetchCompleteEvent(JarFileContent jarFileContent, int length) {
		listener.notifyIndexedJarContentsFetchComplete(jarFileContent, length);
	}

	public void fireVerifyInProgressEvent(int totalCount, int count, String jarFilename) {
		listener.notifyVerifyInProgress(totalCount, count, jarFilename);
	}
}
