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

import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultEditorKit.CopyAction;
import javax.swing.text.DefaultEditorKit.CutAction;
import javax.swing.text.DefaultEditorKit.PasteAction;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import t.n.jarmanager.util.MessageUtil;

@SuppressWarnings("serial")
class TextComponentPopupMenu extends JPopupMenu {
	private static final Logger logger = Logger.getLogger(TextComponentPopupMenu.class);

	// short cut keys (Ctrl+C,X,V) are availabe
	private CutAction cutAction;
	private final CopyAction copyAction;
	private PasteAction pasteAction;
	private final AbstractAction selectAllAction;

	public TextComponentPopupMenu(final JTextComponent textComponent) {
		if(textComponent instanceof JTextField) {
			cutAction = new DefaultEditorKit.CutAction();
			cutAction.putValue(AbstractAction.NAME, MessageUtil.getMessage(MSG_GLOBAL, "TextComponentPopupMenu.cut"));
		}
		copyAction = new DefaultEditorKit.CopyAction();
		copyAction.putValue(AbstractAction.NAME, MessageUtil.getMessage(MSG_GLOBAL, "TextComponentPopupMenu.copy"));
		add(copyAction);

		if(textComponent instanceof JTextField) {
			pasteAction = new DefaultEditorKit.PasteAction();
			pasteAction.putValue(AbstractAction.NAME, MessageUtil.getMessage(MSG_GLOBAL, "TextComponentPopupMenu.paste"));

			add(cutAction);
			add(pasteAction);
		}

		addSeparator();

		//set the textfield selected
		add(selectAllAction = new AbstractAction("select all") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				textComponent.selectAll();
			}
		});
		selectAllAction.putValue(AbstractAction.NAME, MessageUtil.getMessage(MSG_GLOBAL, "TextComponentPopupMenu.selectall"));
	}

	@Override
	public void show(Component c, int x, int y) {
		JTextComponent component = (JTextComponent) c;
		String selectText = component.getSelectedText();
		boolean isSelected = (selectText != null && !selectText.isEmpty());
		copyAction.setEnabled(isSelected);

		if(c instanceof JTextField) {
			cutAction.setEnabled(isSelected);
			pasteAction.setEnabled(hasClipboardText());
		}

		boolean isSelectAllActionValid = !(component.getText().isEmpty());
		selectAllAction.setEnabled(isSelectAllActionValid);

		if(logger.isDebugEnabled()) {
			if(selectText != null) {
				logger.debug(",text length:" + selectText.length() + ", caret:" + component.getCaretPosition() + ", isSelected:" + isSelected + ", isSelectAllActionValid:" + isSelectAllActionValid);
			} else {
				logger.debug(",text:NULL, caret:" + component.getCaretPosition() + ", isSelected:" + isSelected + ", isSelectAllActionValid:" + isSelectAllActionValid);
			}
		}
		super.show(c, x, y);
	}

	private boolean hasClipboardText() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		return (clipboard != null) && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
	}

}
