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
