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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;

public class WebSearchButtonActionListener implements ActionListener, ItemListener {
	private static final Logger logger = Logger.getLogger(WebSearchButtonActionListener.class);
	protected static final String JAR_FINDER_BASE = "http://www.findjar.com/";
	protected static final String JAR_FINDER = JAR_FINDER_BASE + "index.x?query=";
//	protected static final String JAR_FINDER_TAIL = "~";
	private boolean isBrowserDirectOpen;
	private final ItemListener listener;
	private final IClassJarTabView view;

	public WebSearchButtonActionListener(IClassJarTabView view, boolean isBrowserDirectOpen, ItemListener listener) {
		this.isBrowserDirectOpen = isBrowserDirectOpen;
		this.view = view;
		this.listener = listener;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String keyword = view.getTextOnSearchClassnameTextfield();
		String uri = null;
		if(keyword != null && !keyword.isEmpty()) {
			String[] keywordList = keyword.split("\\s+");
			if(keywordList.length > 0) {
//				uri = JAR_FINDER + keywordList[0] + JAR_FINDER_TAIL;
				uri = JAR_FINDER + keywordList[0];
			} else {
				uri = JAR_FINDER_BASE;
			}
		}

		//Initial value is false, true if "direct open next time" checkbox is selected.
		//This state is notified by itemStateChanged() method.
		//This state is saved with java.util.Preferences, so this selection is valid when the app is terminated.
		if(isBrowserDirectOpen) {
			try {
				Desktop.getDesktop().browse(new URI(uri));
			} catch (IOException e1) {
				logger.error(e1);
			} catch (URISyntaxException e1) {
				logger.error(e1);
			}
		} else {
			final BrowserOpenDialog dialog = new BrowserOpenDialog(null, this, uri);
			dialog.setVisible(true);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		isBrowserDirectOpen = (evt.getStateChange() == ItemEvent.SELECTED);
		if(listener != null) {
			listener.itemStateChanged(evt);
		}
	}
}
