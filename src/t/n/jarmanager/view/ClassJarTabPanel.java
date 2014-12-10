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

import static t.n.jarmanager.view.IJarManagerView.MSG_CLASS_JAR_TAB;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

import org.apache.log4j.Logger;

import t.n.jarmanager.IMainController;
import t.n.jarmanager.util.MessageUtil;

// 「Class > JAR」tab
public class ClassJarTabPanel extends JPanel implements ItemListener, IClassJarTabView {
	private static final Logger logger = Logger.getLogger(ClassJarTabPanel.class);

	private final IMainController mainController;
	private GroupLayout classToJarPanelLayout;

	private JTextField searchClassnameTextfield;
	private JButton classSearchStartButton;
	private JButton webSearchButton;
	private JButton classSearchClearButton;

	// Left side (list of JAR files)
	private JScrollPane scrollPaneForJarFileList;
	private JList<String> foundJarFileList;
	private DefaultListModel<String> jarFileListModel;

	//  Right side
	private JTextPane jarContentTextPane2;//class list
	private JScrollPane scrollPaneForJarContents;

	public ClassJarTabPanel(final IMainController mainController, final IJarManagerView mainView) {
		this.mainController = mainController;
		initComponents();

		searchClassnameTextfield.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				onSearchClassnameFieldUpdated();
			}
		});

		searchClassnameTextfield.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				onSearchClassnameFieldUpdated();
			}

			@Override
			public void focusLost(FocusEvent e) {
				onSearchClassnameFieldUpdated();
			}
		});


		classSearchClearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				classSearchClearButton.setEnabled(false);
				classSearchStartButton.setEnabled(false);
				webSearchButton.setEnabled(false);
				searchClassnameTextfield.setText("");
				jarContentTextPane2.setText("");
				jarFileListModel.clear();
				mainView.setMessage("");
			}
		});

		classSearchStartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				classSearchStartButton.setEnabled(false);
				mainController.doFindClass(searchClassnameTextfield.getText().trim());
			}
		});


		foundJarFileList.addListSelectionListener(new JarfileListSelectionListener(this, foundJarFileList));

		webSearchButton.addActionListener(new WebSearchButtonActionListener(this, mainController.isBrowserDirectOpen(), this));
	}

	private final Highlighter.HighlightPainter highlightPainter =
			new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);

	protected void setHilightFoundClass(final List<Integer> indexList, final int length) {
		Document doc = jarContentTextPane2.getEditorKit().createDefaultDocument();
		Highlighter hilite = jarContentTextPane2.getHighlighter();

		int lastIndex = 0;
		int anotherIndex = 0;
		try {
			for(int index : indexList) {
				anotherIndex = index;
				//Only the last variable survives.
				lastIndex = index;
				hilite.addHighlight(index, index + length, highlightPainter);
			}

			if (lastIndex > 0) {
				jarContentTextPane2.setCaretPosition(lastIndex);
			}
		} catch (BadLocationException e) {
			logger.fatal("searchClassname:" + searchClassnameTextfield.getText() + ", document length:" + doc.getLength() + ", begin:" + anotherIndex + ", end:" + anotherIndex + length, e);
		}
	}

	protected void clearJarFileListModel() {
		jarFileListModel.clear();
	}

	protected void addJarFileListModel(final List<String> itemList) {
		for (String item : itemList) {
			jarFileListModel.addElement(item);
		}
	}

	protected void setTextOnJarContentOfClassJarTab(final String string) {
		jarContentTextPane2.setText(string);
	}

	private void onSearchClassnameFieldUpdated() {
		String tmp = searchClassnameTextfield.getText();
		if (tmp.length() > 0) {
			classSearchStartButton.setEnabled(true);
			webSearchButton.setEnabled(true);
			classSearchClearButton.setEnabled(true);
		} else {
			classSearchStartButton.setEnabled(false);
			webSearchButton.setEnabled(false);
			classSearchClearButton.setEnabled(false);
		}
	}

	protected void setupButtonOnFindClassCompleted() {
		classSearchStartButton.setEnabled(true);
		classSearchClearButton.setEnabled(true);
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		boolean isBrowserDirectOpen = (evt.getStateChange() == ItemEvent.SELECTED);
		mainController.saveIsBrowserDirectOpen(isBrowserDirectOpen);
	}

	@Override
	public String getTextOnSearchClassnameTextfield() {
		if(searchClassnameTextfield != null) {
			return searchClassnameTextfield.getText();
		} else {
			return "";
		}
	}

	@Override
	public void notifyListSelectionChanged(File selectedJarFile, String searchClass) {
		mainController.dumpJarFileContentsInCatalogWithIndex(selectedJarFile, searchClass);
	}

	private void initComponents() {
		setName("classToJarPanel");
		jarFileListModel = new DefaultListModel<>();
		foundJarFileList = new JList<>(jarFileListModel);
		foundJarFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneForJarFileList = new JScrollPane(foundJarFileList);
		scrollPaneForJarFileList
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneForJarFileList.setPreferredSize(new Dimension(250, 100));
		scrollPaneForJarFileList.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(""),
						BorderFactory.createEmptyBorder(0, 0, 0, 0)),
				scrollPaneForJarFileList.getBorder()));

		JLabel jLabel2 = new JLabel();
		searchClassnameTextfield = new JTextField();
		classSearchStartButton = new JButton();
		classSearchClearButton = new JButton();
		classSearchClearButton.setEnabled(false);
		webSearchButton = new JButton();

		jarContentTextPane2 = new JTextPane();
		jarContentTextPane2.setName("jarContentTextArea2");
		jarContentTextPane2.setEditable(false);
		jarContentTextPane2.setText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "jarContentText2.text"));
		jarContentTextPane2.setToolTipText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "jarContentText2.text"));

		jarContentTextPane2.setComponentPopupMenu(new TextComponentPopupMenu(jarContentTextPane2));
		jarContentTextPane2.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed( MouseEvent e ){
				if ( SwingUtilities.isRightMouseButton( e )) {
					jarContentTextPane2.requestFocus();
				}
			}
		});
		scrollPaneForJarContents = new JScrollPane(jarContentTextPane2);
		scrollPaneForJarContents.setViewportView(jarContentTextPane2);

		scrollPaneForJarContents.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		scrollPaneForJarContents.setPreferredSize(new Dimension(250, 250));
		scrollPaneForJarContents.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createTitledBorder(""),
						BorderFactory.createEmptyBorder(0, 0, 0, 0)),
				scrollPaneForJarContents.getBorder()));

		foundJarFileList.setToolTipText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "jarFileList.toolTipText"));
		foundJarFileList.setName("jarFileList");

		jLabel2.setText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "classNameLabel.text"));
		jLabel2.setName("classNameLabel");

		searchClassnameTextfield.setToolTipText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "tfSearchClassname.toolTipText"));
		searchClassnameTextfield.setName("tfSearchClassname");
		searchClassnameTextfield.setComponentPopupMenu(new TextComponentPopupMenu(searchClassnameTextfield));
		classSearchStartButton.setText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "classSearchStartButton.text"));
		classSearchStartButton.setToolTipText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "classSearchStartButton.toolTipText"));
		classSearchStartButton.setName("classSearchStartButton");

		webSearchButton.setText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "webSearchButton.text"));
		webSearchButton.setToolTipText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "webSearchButton.toolTipText"));
		webSearchButton.setName("webSearchButton");
		classSearchClearButton.setText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "classSearchClearButton.text"));
		classSearchClearButton.setToolTipText(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "classSearchClearButton.toolTipText"));
		classSearchClearButton.setName("classSearchClearButton");

		classToJarPanelLayout = new GroupLayout(this);
		setLayout(classToJarPanelLayout);

		JSplitPane jarContentPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneForJarFileList, scrollPaneForJarContents);

		classToJarPanelLayout
				.setHorizontalGroup(classToJarPanelLayout
						.createParallelGroup(
								GroupLayout.Alignment.LEADING)
						.addGroup(
								classToJarPanelLayout
										.createSequentialGroup()
										.addComponent(jarContentPane))
						.addGroup(
								classToJarPanelLayout
										.createSequentialGroup()
										.addGap(21, 21, 21)
										.addComponent(jLabel2)
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												classToJarPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.LEADING)
														.addGroup(
																classToJarPanelLayout
																		.createSequentialGroup()
																		.addComponent(
																				classSearchStartButton)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				webSearchButton)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.RELATED)
																		.addComponent(
																				classSearchClearButton))
														.addComponent(
																searchClassnameTextfield,
																GroupLayout.PREFERRED_SIZE,
																310,
																GroupLayout.PREFERRED_SIZE))
										.addContainerGap(27, Short.MAX_VALUE)));
		classToJarPanelLayout
				.setVerticalGroup(classToJarPanelLayout
						.createParallelGroup(
								GroupLayout.Alignment.LEADING)
						.addGroup(
								GroupLayout.Alignment.TRAILING,
								classToJarPanelLayout
										.createSequentialGroup()
										.addGroup(
												classToJarPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(jLabel2)
														.addComponent(
																searchClassnameTextfield,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												classToJarPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.BASELINE)
														.addComponent(
																classSearchStartButton,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																webSearchButton,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE)
														.addComponent(
																classSearchClearButton,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.DEFAULT_SIZE,
																Short.MAX_VALUE))
										.addPreferredGap(
												LayoutStyle.ComponentPlacement.RELATED)
										.addGroup(
												classToJarPanelLayout
														.createParallelGroup(
																GroupLayout.Alignment.TRAILING)
														.addComponent(jarContentPane))));

		classSearchStartButton.setEnabled(false);
		webSearchButton.setEnabled(false);
	}
}
