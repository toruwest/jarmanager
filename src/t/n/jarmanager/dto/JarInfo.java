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
