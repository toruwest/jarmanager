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
