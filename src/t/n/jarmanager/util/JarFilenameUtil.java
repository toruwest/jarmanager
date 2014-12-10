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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JarFilenameUtil {
	private static DateFormat fmt;
	private static SimpleDateFormat sdfDate;
	private static SimpleDateFormat sdfTime;
	public static final String JAR_SEPARATOR = ";";

	static {
		fmt = java.text.DateFormat.getTimeInstance();
		fmt.setTimeZone(java.util.TimeZone.getTimeZone("GMT"));
		sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdfTime = new SimpleDateFormat("mm:ss.SSS");
	}

	public static String format(Date date) {
		return sdfDate.format(date);
	}

	public static String format(long time) {
		return sdfTime.format(time);
	}

	public static Date parse(String arg) throws ParseException {
		return sdfDate.parse(arg);
	}

	/**
	 * Get the list of JAR files from text field. ";" is used as separater.
	 */
	public static List<String> splitFilenames(String text) {
		List<String> filenameList = new ArrayList<String>();
		if(text != null && 0 < text.length()) {
			text = text.trim();
			String[] tmp = text.split(JAR_SEPARATOR);
			for(String s : tmp) {
				filenameList.add(s.trim());
			}
		}

		return filenameList;
	}

	public static boolean isExistsPath(String filename) {
		if(filename == null || filename.isEmpty()) return false;
		File path = new File(filename);
		boolean result = path.exists();
		path = null;
		return result;
	}
}
