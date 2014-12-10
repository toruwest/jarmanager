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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

import t.n.jarmanager.util.JarFileUtil;

//JarInfo.java is a DTO class used to insert to database.
//This class is intend to use for view.
public class JarFileViewProperty {
	private static final String MAIN_CLASS_IN_MANIFEST = "Main-Class";
	private static final String NEW_LINE = "\n";

	//Regular expression for MANIFEST.MF of signed jar file. They are alphabet, number, underscore, hyphon, or '$' (for inner class)
	//To detect the following pattern in MANIFEST.MF
	//com/kitfox/volume/transfer/TransferFnEditPanel$BrushType.class
	public static final String CLASS_IN_MANIFEST_PATTERN = "([a-zA-Z0-9\\-_]+/)*+[a-zA-Z0-9\\-_]+(\\$([a-zA-Z0-9\\-_])+)?\\.class";

	private final File jarFile;

	private String manifestContent;
	private StringBuilder classFilesContent;
	private StringBuilder otherFilesContent;
	private String checksum;

	private boolean hasMainClass;
	private boolean isSigned;
	private List<String> classesList;
	private List<String> otherFilesList;

	private enum MainClassCheck {unknown, yes, no};
	private MainClassCheck mainClassCheck = MainClassCheck.unknown;

	private enum JarSignerCheck {unknown, yes, no};
	private JarSignerCheck jarSignerCheck = JarSignerCheck.unknown;
	private int notSignedCount;
	private int signedCount;
	private boolean hasError = false;
	private int errorCause = 0;

	//errorCause can contain multiple reason
	public static final int NOT_EXIST = 0x01; //LSB
	public static final int NOT_JAR   = 0x02;
	public static final int IO_ERROR  = 0x04;
	public static final int ZIP_ERROR  = 0x08;
	public static final int IS_DIRECTORY = 0x10;

	public JarFileViewProperty(File jarFile) {
		if(jarFile != null && jarFile.exists()) {
			if(jarFile.isFile()) {
				if(jarFile.getName().endsWith(".jar")) {
					this.jarFile = jarFile;
					generateManifestContent();
				} else {
					this.jarFile = null;
					hasError = true;
					errorCause = NOT_JAR;
				}
			} else {
				this.jarFile = null;
				hasError = true;
				errorCause = IS_DIRECTORY;
			}
		} else {
			this.jarFile = null;

			hasError = true;
			errorCause = NOT_EXIST;
		}
	}
	public JarFileViewProperty(File jarFile, String newChecksum) {
		this(jarFile);
		if(newChecksum != null)checksum = newChecksum;
	}

	public JarFileViewProperty(File jarFile, JarInfo jarInfo, String manifestContent, List<String> classesList,
			String otherFilesContent) {
		this(jarFile);

		this.hasMainClass = jarInfo.hasMainClass();
		this.mainClassCheck = hasMainClass?MainClassCheck.yes:MainClassCheck.no;
		this.isSigned = jarInfo.isSigned();
		this.jarSignerCheck = isSigned?JarSignerCheck.yes:JarSignerCheck.no;
		this.checksum = jarInfo.getChecksum();
		this.manifestContent = manifestContent;
		this.classesList = classesList;
		this.otherFilesContent = new StringBuilder(otherFilesContent);
	}

	public final boolean hasError() {
		return hasError;
	}

	public final int getErrorCause() {
		return errorCause;
	}

	public String getMD5Sum() throws FileNotFoundException, IOException {
		if(checksum == null) {
			checksum = JarFileUtil.calculateMD5sum(jarFile);
		}
		return checksum;
	}

	public List<String> getJarFileList() throws ZipException, IOException {
		if(classesList == null) {
			listFilesInJarFile();
		}
		return classesList;
	}

	public File getJarFile() {
		return jarFile;
	}

	public String getManifestContent() {
		return manifestContent;
	}

	public String getAllContent() throws ZipException, IOException {
		//At first, list up normal files. (not class file, .xml, .png, .jpg etc)
		StringBuilder content = new StringBuilder(manifestContent);
		content = JarFileUtil.wrapManifestContent(content.toString());
		content.append(getOtherFilesContent());
		content.append(NEW_LINE);
		content.append(getClassFilesContent());
		return content.toString();
	}

	private String getClassFilesContent() throws ZipException, IOException {
		if(classFilesContent == null) {
			generateClassFilesContent();
		}
		return classFilesContent.toString();
	}

	private void generateClassFilesContent() throws ZipException, IOException {
		classFilesContent = new StringBuilder();
		if(classesList == null) {
			listFilesInJarFile();
		}
		for(String classname : classesList){
			classFilesContent.append(classname);
			classFilesContent.append(NEW_LINE);
		}
	}

	public StringBuilder getOtherFilesContent() {
		if(otherFilesContent == null) {
			generateOtherFilesContent();
		}

		return otherFilesContent;
	}

	private void generateOtherFilesContent() {
		otherFilesContent = new StringBuilder();

		if(otherFilesList == null) {
			try {
				listFilesInJarFile();
			} catch (ZipException e) {
				hasError = true;
				errorCause = ZIP_ERROR;
			} catch (IOException e) {
				hasError = true;
				errorCause = IO_ERROR;
			}
		}
		for(String file : otherFilesList) {
			otherFilesContent.append(file);
			otherFilesContent.append(NEW_LINE);
		}
	}

	public boolean hasMainClass() throws ZipException, IOException {
		if(mainClassCheck == MainClassCheck.unknown) {
			checkHasMainClass();
		}
		return hasMainClass;
	}

	public boolean isSigned() throws ZipException, IOException {
		if(jarSignerCheck == JarSignerCheck.unknown) {
			listFilesInJarFile();
		}
		return isSigned;
	}

	private void generateManifestContent() {
		StringBuilder manifestSB = new StringBuilder();
		Manifest manifest;
		JarFile jar = null;
		try {
			jar = new JarFile(jarFile, false);

			manifest = jar.getManifest();
			if (manifest != null) {
				Attributes attr1 = manifest.getMainAttributes();
				//We don't show the checksum of each class if JAR file is signed.
				for (Object o1 : attr1.keySet()) {
					manifestSB.append(o1.toString());
					manifestSB.append(" : ");
					manifestSB.append(attr1.get(o1));
					manifestSB.append(NEW_LINE);
				}
				manifestContent = manifestSB.toString();

			} else { //not a JAR file
				hasError = true;
				errorCause = NOT_JAR;
			}
		} catch (IOException e) {
			hasError = true;
			errorCause = IO_ERROR;
		} finally {
			try {
				if(jar != null) {
					jar.close();
				}
			} catch (IOException e) {
				hasError = true;
				errorCause = IO_ERROR;
			}
		}
	}

	/**
	 * Get the list of classes and other files contained in JAR file.
	 *
	 * @param jarFile target JAR file
	 * @param includeNormalFiles includes normal files (images, xml file etc).  Includes them if true.
	 * @return List<String> list of files in JAR file.
	 * @throws ZipException, IOException
	 */
	private void listFilesInJarFile() throws ZipException, IOException {
		classesList = new ArrayList<String>();
		otherFilesList = new ArrayList<String>();
		String fileNameInEntry;
		JarFile jar = new JarFile(jarFile, true);
		Enumeration<JarEntry> entries = jar.entries();

		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();

			//if the entry is directory, ignore it.
			if (entry.isDirectory()) continue;

			fileNameInEntry = entry.getName();
			if (fileNameInEntry.endsWith(".class")) {
				checkSigner(jar, entry);
				//replace '/' with '.'
				fileNameInEntry = fileNameInEntry.replaceAll("/", ".");
				classesList.add(fileNameInEntry);
			} else {
				//we will show other files at the beginning of text field.
				otherFilesList.add(fileNameInEntry);
			}
		}

		if(0 < signedCount && notSignedCount == 0) {
			jarSignerCheck = JarSignerCheck.yes;
			isSigned = true;
		} else if(signedCount == 0 && 0 < notSignedCount) {
			jarSignerCheck = JarSignerCheck.no;
			isSigned = false;
		}

		Collections.sort(otherFilesList);
		Collections.sort(classesList);

		jar.close();

	}

	private void checkSigner(JarFile jar, JarEntry entry) throws IOException {
		//http://docs.oracle.com/javase/7/docs/technotes/guides/security/crypto/HowToImplAProvider.html#CheckJARFile
		//http://docs.oracle.com/javase/7/docs/technotes/guides/jar/jar.html#API%20Details
		//JavaDoc says: "This method can only be called once the JarEntry has been completely verified by reading from the entry input stream until the end of the stream has been reached."
		byte[] buffer = new byte[8192];
		InputStream is = jar.getInputStream(entry);
		int n;
		while ((n = is.read(buffer, 0, buffer.length)) != -1) {
		    // Don't care
		}

		CodeSigner[] signers = entry.getCodeSigners();
		if(signers != null) {
			signedCount++;
		} else {
			notSignedCount++;
		}

		is.close();
	}

	private void checkHasMainClass() throws ZipException, IOException {

		boolean result = false;
		JarFile jar = new JarFile(jarFile);
		Manifest manifest = jar.getManifest();

		String mainClassInManifest = null;

		if (manifest != null) {
			Attributes attr1 = manifest.getMainAttributes();
			for (Object o1 : attr1.keySet()) {
				if(o1.toString().equals(MAIN_CLASS_IN_MANIFEST)) {
					mainClassInManifest = (String) attr1.get(o1);
					//found the MAIN_CLASS_IN_MANIFEST
					break;
				}
			}

			//Now we found the Main-class from MANIFEST.MF, we will confirm if this class exists.
			if(mainClassInManifest != null) {
				if(classesList == null) {
					listFilesInJarFile();
				}
				//note that ".class" string is not found at the end of mainClassInManifest.
				mainClassInManifest = mainClassInManifest + ".class";
				result = classesList.contains(mainClassInManifest);
			}
		}

		jar.close();

		hasMainClass = result;
		if(result) {
			mainClassCheck = MainClassCheck.yes;
		} else {
			mainClassCheck = MainClassCheck.no;
		}
	}
}
