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

import java.awt.Component;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class HyperLinkUtil {

	public static Component genHyperLinkEditor(final String uri, final Logger logger) {
		JEditorPane editor = new JEditorPane("text/html", "<html><a href='"
				+ uri + "'>" + uri + "</a>");
		editor.setOpaque(false);
		editor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
				Boolean.TRUE);
		editor.setEditable(false);
		editor.addHyperlinkListener(new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (!Desktop.isDesktopSupported())
						return;
					try {
						Desktop.getDesktop().browse(new URI(uri));
					} catch (IOException e1) {
						if(logger != null) logger.log(Level.INFO, e);
					} catch (URISyntaxException e2) {
						if(logger != null) logger.log(Level.INFO, e);
					}
				}
			}
		});
		return editor;
	}

}
