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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class JarManagerPreference {
	private static final Logger logger = Logger.getLogger(JarManagerPreference.class);

	private static final String PREV_DIR_KEY = "prevDir";
	private static final String DIRECT_BROWSER_OPEN_KEY = "directBrowserOpen";
	private static final String DEFAULT_PREF_FILENAME ="jarmanager.pref";

	private String prefFilename = null;

	private final Properties prop = new Properties();

	private final File dataDir;

	public JarManagerPreference(File dataDir){
		this.dataDir = dataDir;
		this.prefFilename = DEFAULT_PREF_FILENAME;
	}

	//For test with JUnit
	public JarManagerPreference(File dataDir, String prefFilename){
		this(dataDir);
		if(DEFAULT_PREF_FILENAME.equals(prefFilename)) {
			throw new IllegalArgumentException("Please do not specify the default property file name. Try another one in order to protect default preference file that is used for application");
		} else {
			this.prefFilename = prefFilename;
		}
	}

	public File loadDir() {
		FileReader reader = null;
		File result = null;
		try {
			File prefFile = new File(dataDir, prefFilename);
			if(prefFile.exists()) {
				reader = new FileReader(prefFile);
				prop.load(reader);
				result = new File((String)prop.get(PREV_DIR_KEY));
				reader.close();
			}
		} catch (IOException e) {
			logger.log(Level.FATAL, e);
		}
		return result;
	}

	public void saveDir(File dir) {
		FileWriter writer = null;
		try {
			File prefFile = new File(dataDir, prefFilename);
			writer = new FileWriter(prefFile);
			prop.setProperty(PREV_DIR_KEY, dir.toString());
			prop.store(writer, "");
			writer.close();
		} catch (IOException e) {
			logger.log(Level.FATAL, e);
		}
	}

	public boolean exists() {
		File f = new File(dataDir, prefFilename);
		return f.exists();
	}

	public boolean loadIsBrowserDirectOpen() {
		FileReader reader = null;
		boolean result = false;
		String s = null;
		try {
			File prefFile = new File(dataDir, prefFilename);
			if(prefFile.exists()) {
				reader = new FileReader(prefFile);
				prop.load(reader);
				s = (String)prop.get(DIRECT_BROWSER_OPEN_KEY);
				reader.close();
			}
		} catch (IOException e) {
			logger.log(Level.FATAL, e);
		}
		if("true".equals(s)) {
			result = true;
		} else if("false".equals(s)) {
			result = false;
		}
		return result;
	}

	public void saveIsBrowserDirectOpen(boolean isBrowserDirectOpen) {
		FileWriter writer = null;
		try {
			File prefFile = new File(dataDir, prefFilename);
			writer = new FileWriter(prefFile);
			prop.setProperty(DIRECT_BROWSER_OPEN_KEY, isBrowserDirectOpen+"");
			prop.store(writer, "");
			writer.close();
		} catch (IOException e) {
			logger.log(Level.FATAL, e);
		}
	}

}
