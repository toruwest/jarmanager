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
