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
package t.n.jarmanager.worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;

import t.n.jarmanager.EventDispatcher;
import t.n.jarmanager.ICatalogEventListener;
import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.CatalogRegistStatus;
import t.n.jarmanager.dto.JarFileViewProperty;
import t.n.jarmanager.dto.JarInfo;
import t.n.jarmanager.util.DBUtilImpl;
import t.n.jarmanager.util.IDBUtil;
import t.n.jarmanager.util.JarFileUtil;

public final class WorkerUtil {
	private static final Logger logger = Logger.getLogger(WorkerUtil.class);
	private final EventDispatcher dispatcher;
	IDBUtil dbUtil;

	private WorkerUtil(File dataDir, ICatalogEventListener catalogEventListener) throws Exception {
		dispatcher = new EventDispatcher(catalogEventListener);
		dbUtil = DBUtilImpl.getInstance(dataDir);
	}

	private WorkerUtil(File dataDir, String dbName, ICatalogEventListener catalogEventListener) throws Exception {
		dispatcher = new EventDispatcher(catalogEventListener);
		dbUtil = DBUtilImpl.getInstance(dataDir, dbName);
	}

	public static WorkerUtil getInstance(File dataDir, ICatalogEventListener catalogEventListener) throws Exception {
		return new WorkerUtil(dataDir, catalogEventListener);
	}

	public static WorkerUtil getInstance(File dataDir, String dbName, ICatalogEventListener catalogEventListener) throws Exception {
		return new WorkerUtil(dataDir, dbName, catalogEventListener);
	}

	//@StartNewThread
	public void shutdown() {
		dbUtil.shutdown();
	}

	public CatalogRegistStatus checkJarFileStatusInCatalog(File jarFile) {
		CatalogRegistStatus result = CatalogRegistStatus.UNKNOWN;
		if (checkCandidateJarFile(jarFile)) {
			if (isAlreadyRegisterd(jarFile)) {
				// We don't rely on file's timestamp. We comfirm if the JAR file exists in catalog and compare the MD5 checksum of JAR file.
				// We won't do anything if the checksum is same.
				String catalogSum = getChecksumFromCatalog(jarFile);
				try {
					String fileSum = JarFileUtil.calculateMD5sum(jarFile);
					if(fileSum != null) {
						if(fileSum.equals(catalogSum)) {
							result = CatalogRegistStatus.REGISTERED_JAR;
						} else {
							result = CatalogRegistStatus.UPDATED_JAR;
						}
					} else {
						result = CatalogRegistStatus.IO_ERROR;
					}
				} catch (FileNotFoundException e) {
					//Never happens.
				} catch (IOException e) {
					result = CatalogRegistStatus.IO_ERROR;
				}
			} else {
				result = CatalogRegistStatus.NEW_JAR;
			}
		} else {
			result = CatalogRegistStatus.NOT_JAR;
		}
		return result;
	}

	/**
	 * We get the content of manifest file and list of files except for class file such as ".xml", ".png"
	 */
	public String getJarManifestAndOtherFileContentInCatalog(File targetJarFile) {
		StringBuilder contents = new StringBuilder();
		JarFileViewProperty prop = dbUtil.getJarContentInCatalog(targetJarFile);
		contents = JarFileUtil.wrapManifestContent(prop.getManifestContent());
		contents.append(prop.getOtherFilesContent());

		return contents.toString();
	}

	public List<String> getClassListInCatalog(File jarFile) {
		return dbUtil.getClassListInCatalog(jarFile.getParent(), jarFile.getName());
	}

	public EventDispatcher getDispatcher() {
		return dispatcher;
	}

	public CatalogRegistResult tryToAddOrReplace(File jarFile, String checksum) {
		CatalogRegistResult status = CatalogRegistResult.UNDEFINED;
		//
		JarFileViewProperty prop = JarFileUtil.getJarFileProperty(jarFile, checksum);
		if(!prop.hasError()) {
			status = dbUtil.addOrReplaceCatalog(prop, System.currentTimeMillis(), CatalogEntryStatus.NORMAL);
		} else {
			status = encode(prop.getErrorCause());
		}
		return status;
	}

	public boolean isAlreadyRegisterd(File candidateJarFile) {
		return dbUtil.isAlreadyRegisterd(candidateJarFile.getParent(), candidateJarFile.getName());
	}

	/**
	 * @param targetJarFile
	 * @return list of all files in JAR file.
	 */
	public String getAllJarContentInCatalog(File targetJarFile) {
		String contents = null;
		JarFileViewProperty prop = dbUtil.getJarContentInCatalog(targetJarFile);
		try {
			contents = prop.getAllContent();
		} catch (ZipException e) {
			logger.error("", e);
		} catch (IOException e) {
			logger.error("", e);
		}

		return contents;
	}

	/**
	 * Remove the specified JAR file from catalog.
	 * @param target JAR file
	 * @return {@link CatalogRegistResult}
	 */
	protected CatalogRegistResult removeJarFromCatalog(File jarFile) {
		return dbUtil.removeJar(jarFile);
	}

	/**
	 * 指定されたクラスを検索する。
	 * Find the specified class. Both of FQCN and Simple class name is accepted. Package name is also accepted.
	 * Use "*" as wildcard and "." as package name separator. If targetName contains "*" or ".", then index of database is not used.
	 * @param targetName
	 * @return List<String> found classes
	 */
	protected List<String> findClass(String targetName) {
		if(targetName.contains(".") || targetName.contains("*") ) {
			return dbUtil.findClassLoose(targetName);
		} else {
			if(targetName.endsWith(".class")) {
				return dbUtil.findClassStrict(targetName);
			} else {
				return dbUtil.findClassStrict(targetName + ".class");
			}
		}
	}

	protected List<JarInfo> requestJarEntryList() {
		return dbUtil.getJarEntryList();
	}

	/**
	 * Returns the checksum (MD5) of JAR file in catalog.
	 * @param jarFile
	 * @return String Checksum of specified JAR file
	 */
	protected String getChecksumFromCatalog(File jarFile) {
		return dbUtil.getChecksumFromCatalog(jarFile);
	}

	private boolean checkCandidateJarFile(File jarFile) {
		return(jarFile != null && jarFile.exists() &&
			jarFile.isFile() && jarFile.getName().endsWith(".jar"));

	}

	private CatalogRegistResult encode(int errorCause) {
		CatalogRegistResult result = null;
		switch(errorCause) {
		case JarFileViewProperty.IO_ERROR:
			result = CatalogRegistResult.IO_ERROR;
			break;
		case JarFileViewProperty.IS_DIRECTORY:
			result = CatalogRegistResult.NOT_JAR;
			break;
		case JarFileViewProperty.NOT_EXIST:
			result = CatalogRegistResult.JAR_NOT_EXIST;
			break;
		case JarFileViewProperty.NOT_JAR:
			result = CatalogRegistResult.NOT_JAR;
			break;
		case JarFileViewProperty.ZIP_ERROR:
			result = CatalogRegistResult.IO_ERROR;
			break;
		default:
			//empty
		}

		return result;
	}
}
