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

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JarfileListSelectionListener implements ListSelectionListener {

	IClassJarTabView view;
	private final JList<String> foundJarFileList;

	public JarfileListSelectionListener(IClassJarTabView view, JList<String> foundJarFileList) {
		this.view = view;
		this.foundJarFileList = foundJarFileList;
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {

		if (evt.getValueIsAdjusting()){
			return;
		}

		String selectedFile = foundJarFileList.getSelectedValue();
		if (selectedFile != null) {
			String searchClass = view.getTextOnSearchClassnameTextfield();
			view.notifyListSelectionChanged(new File(selectedFile), searchClass);
		}
	}

}
