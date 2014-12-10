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
package t.n.jarmanager.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

public class JarFileFilter extends FileFilter {

	List<String> extension = null;

	public JarFileFilter() {
		extension = new ArrayList<String>();
	}

	public JarFileFilter(String string) {
		this();
		extension.add(string);
	}

	@Override
	public boolean accept(File file) {
		if (file != null) {
			if (file.isDirectory()) {
				return true;
			}

			for (String ext : extension) {
				String s = file.toString();
				if(s != null && !s.isEmpty()) {
					s = s.toLowerCase();
					if (s.endsWith(ext)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected String getExt(File f) {
		String name = f.getName();
		return name.substring(name.indexOf('.'), name.length());
	}

	@Override
	public String getDescription() {
		return "Just JAR files";

	}

	public void addExtension(String string) {
		extension.add(string);
	}
}
