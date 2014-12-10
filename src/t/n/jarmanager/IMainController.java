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
