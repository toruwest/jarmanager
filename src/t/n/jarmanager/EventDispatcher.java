/*
* Copyright 2008 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 * * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
