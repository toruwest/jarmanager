package t.n.jarmanager.view;

import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;
import static t.n.jarmanager.view.IJarManagerView.MSG_JAR_CLASS_TAB;

import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;
import org.fuin.aspects4swing.InvokeLater;

import t.n.jarmanager.IMainController;
import t.n.jarmanager.ITabTransitState;
import t.n.jarmanager.util.JarFilenameUtil;
import t.n.jarmanager.util.MessageUtil;

// 「JAR > class」tab
public class JarClassTabPanel extends JPanel implements IDropEventListener {
	private static final Logger logger = Logger.getLogger(JarClassTabPanel.class);

	private final IMainController mainController;
	private final IJarManagerView mainView;
	private JTextField jarFilenameTextfield;
	private JButton textfieldCompletedButton;
	private JButton addToCatalogButton;
	private JButton fileChoiceButton;
	private JButton clearButton;
	private JButton startScanButton;
	private JButton cancelScanButton;
	private JScrollPane scrollPaneForJarClassTab;
	private JTextArea jarContentTextArea1;
	private JLabel jarFilenameLabel;
	private JarFileFilter jarFileFilter;
	private final ITabTransitState tabTransitState;
	private boolean isRegistReady;

	public JarClassTabPanel(final IMainController mainController, ITabTransitState argTabTransitState, final IJarManagerView mainView) {
		this.mainController = mainController;
		this.tabTransitState = argTabTransitState;
		this.mainView = mainView;

		initComponent();

		jarFilenameTextfield.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent evt) {
			}

			@Override
			public void focusLost(FocusEvent evt) {
				if(!jarFilenameTextfield.getText().isEmpty()) {
					if(isRegistReady) {
						tabTransitState.setRegistCandidateJarFiles(jarFilenameTextfield.getText());
					}
				}
			}
		});

		jarFilenameTextfield.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {textFiledUpdate();}

			@Override
			public void insertUpdate(DocumentEvent e) {textFiledUpdate();}

			@Override
			public void changedUpdate(DocumentEvent e) {textFiledUpdate();}

			private void textFiledUpdate() {
				clearMessages();
				if(!jarFilenameTextfield.getText().isEmpty()) {
					clearButton.setEnabled(true);
					textfieldCompletedButton.setEnabled(true);
				}
			}
		});

		JarFileDropTarget jarFileDropTarget = new JarFileDropTarget(this);
		DropTarget dropTarget1 = new DropTarget(jarFilenameTextfield, jarFileDropTarget);
		jarFilenameTextfield.setDropTarget(dropTarget1);
		DropTarget dropTarget2 = new DropTarget(jarContentTextArea1,  jarFileDropTarget);
		jarContentTextArea1.setDropTarget(dropTarget2);

		textfieldCompletedButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				showJarContents();
			}
		});

		addToCatalogButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				jarFilenameTextfield.setEditable(false);
				mainController.addJars();
			}
		});

		fileChoiceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				showJarChoiceDialog();
			}
		});

		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				jarFilenameTextfield.setText("");
				jarContentTextArea1.setText("");
				mainView.setMessage("");
				addToCatalogButton.setEnabled(false);
				clearButton.setEnabled(false);
				textfieldCompletedButton.setEnabled(false);
				tabTransitState.clearStatus();

			}
		});
		startScanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				showScanTargetFolderChoiceDialog();
			}
		});
		cancelScanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {

				mainController.cancelScanning();
				mainView.setMessage(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "cancel_accepted.text"));
			}
		});
	}

	@Override
	@InvokeLater
	public void dropFilenames(String filename) {
		tabTransitState.clearStatus();
		if(filename != null && !filename.isEmpty()) {
			jarFilenameTextfield.setText(filename);
			clearButton.setEnabled(true);
			showJarContents();
		}
	}

	protected void setTextOnJarContentOfJarClassTab(final String string) {
		jarContentTextArea1.setText(string);
		jarContentTextArea1.select(0,0);
	}

	protected void appendTextOnJarContentOfJarClassTab(String string) {
		jarContentTextArea1.append(string);
	}

	protected void selectJarContentOfJarClassTab(final int beginIndex, final int endIndex) {
		jarContentTextArea1.select(beginIndex, endIndex);
	}

	protected void setTextOnJarFilenameTextfield(final String string) {
		jarFilenameTextfield.setText(string);
	}

	protected void setAddToCatalogButtonEnabled(final boolean b) {
		isRegistReady = b;
		addToCatalogButton.setEnabled(b);
	}

	protected void setupButtonOnScanCompleted() {
		fileChoiceButton.setEnabled(true);
		addToCatalogButton.setEnabled(false);
		startScanButton.setEnabled(true);
		cancelScanButton.setEnabled(false);
		clearButton.setEnabled(true);
		jarFilenameTextfield.setEditable(true);
		textfieldCompletedButton.setEnabled(false);
		mainView.enableOperation();
	}

	protected void setupButtonOnAddJarCompleted() {
		addToCatalogButton.setEnabled(false);
		fileChoiceButton.setFocusable(true);
		jarFilenameTextfield.setEditable(true);
	}

	protected void setTextfieldCompletedButtonEnabled(boolean b) {
		textfieldCompletedButton.setEnabled(b);
	}

	protected void setJarFileClearButtonEnabled(boolean b) {
		clearButton.setEnabled(b);
	}

	private void showJarContents() {
		mainController.prepareForRegistering(getCandidateJarFileList());
		mainController.dumpJarFileContents();
	}

	private List<File> getCandidateJarFileList() {
		List<String> fileListInTextfield = JarFilenameUtil.splitFilenames(jarFilenameTextfield.getText());
		List<File> candidateJarFileList = new ArrayList<File>();
		for (String t : fileListInTextfield) {
			if (t.trim().length() != 0) {
				candidateJarFileList.add(new File(t.trim()));
			}
		}
		return candidateJarFileList;
	}

	private void showJarChoiceDialog() {
		File prevDir = mainController.getPrevDir();
		JFileChooser fc;
		//Priority is (1)specified in tfJarFilename (text field), (2)previous choice
		String tf = jarFilenameTextfield.getText();
		if(!tf.isEmpty() && JarFilenameUtil.isExistsPath(tf)) {
			fc = new JFileChooser(tf);
		} else if(prevDir != null) {
			fc = new JFileChooser(prevDir);
		} else {
			fc = new JFileChooser();
		}

		jarFileFilter = new JarFileFilter("jar");
		fc.setFileFilter(jarFileFilter);
		fc.setMultiSelectionEnabled(true);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int selected = fc.showOpenDialog(this);
		switch (selected) {
		case JFileChooser.APPROVE_OPTION:
			File[] selectedFilesOnFileChooser = fc.getSelectedFiles();
			//Multiple JAR files are acceptable
			if (selectedFilesOnFileChooser != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < selectedFilesOnFileChooser.length; i++) {
					// Memorize the chosen directory in java.util.Preference and use it next time.
					if (i == 0) {
						prevDir = selectedFilesOnFileChooser[i].getParentFile();
						mainController.saveDir(prevDir);
					}
					sb.append(selectedFilesOnFileChooser[i].getAbsolutePath() + JarFilenameUtil.JAR_SEPARATOR);
				}
				tabTransitState.clearStatus();
				clearButton.setEnabled(true);
				clearMessages();

				jarFilenameTextfield.setText(sb.toString());
				showJarContents();
			}
			break;
		case JFileChooser.CANCEL_OPTION:
			break;
		case JFileChooser.ERROR_OPTION:
			break;
		}
	}

	private void clearMessages() {
		mainView.setMessage("");
		jarContentTextArea1.setText("");
	 }

	private void showScanTargetFolderChoiceDialog() {
		File prevDir = mainController.getPrevDir();
		JFileChooser fc;
		String tf = jarFilenameTextfield.getText();
		if(!tf.isEmpty() && JarFilenameUtil.isExistsPath(tf)) {
			fc = new JFileChooser(tf);
		} else if(prevDir != null) {
			fc = new JFileChooser(prevDir);
		} else {
			fc = new JFileChooser();
		}
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int selected = fc.showOpenDialog(this);
		switch (selected) {
		case JFileChooser.APPROVE_OPTION:
			tabTransitState.clearStatus();
			clearMessages();
			File f = fc.getSelectedFile();
			jarFilenameTextfield.setText(f.getAbsolutePath());
			if (f != null) {
				//Memorize the specified directory in java.util.Preference and use it as default next time.
				prevDir = f;
				mainController.saveDir(prevDir);
				mainView.startProgressBar(true);
				startScanButton.setEnabled(false);
				cancelScanButton.setEnabled(true);
				fileChoiceButton.setEnabled(false);
				clearButton.setEnabled(true);
				addToCatalogButton.setEnabled(false);
				jarFilenameTextfield.setEditable(false);
				textfieldCompletedButton.setEnabled(false);
				mainView.disableOperation();
				mainController.startScan(f);
			}
			break;
		case JFileChooser.CANCEL_OPTION:
			break;
		case JFileChooser.ERROR_OPTION:
			break;
		}
	}

	protected void initComponent() {
		setName("classToJarPanel");
		jarFilenameTextfield = new JTextField();
		textfieldCompletedButton = new JButton(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "textfieldCompletedButton.text"));
		textfieldCompletedButton.setName("textfieldCompletedButton");
		textfieldCompletedButton.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "textfieldCompletedButton.tooltiptext"));
		textfieldCompletedButton.setEnabled(false);

		scrollPaneForJarClassTab = new JScrollPane();

		jarContentTextArea1 = new JTextArea();
		jarContentTextArea1.setLineWrap(false);
		jarContentTextArea1.setEditable(false);

		jarContentTextArea1.setText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "jarContentText.text"));
		jarContentTextArea1.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "jarContentText.text"));
		jarContentTextArea1.setComponentPopupMenu(new TextComponentPopupMenu(jarContentTextArea1));
		jarContentTextArea1.setName("jarContentTextArea1");
		jarContentTextArea1.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed( MouseEvent e ){
				if ( SwingUtilities.isRightMouseButton( e )) {
					jarContentTextArea1.requestFocus();
				}
			}
		});

		scrollPaneForJarClassTab.setViewportView(jarContentTextArea1);
		fileChoiceButton = new JButton();
		jarFilenameLabel = new JLabel();
		addToCatalogButton = new JButton();
		startScanButton = new JButton();

		clearButton = new JButton();
		cancelScanButton = new JButton();

		jarFilenameTextfield.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "tfJarFilename.toolTipText"));
		jarFilenameTextfield.setName("tfJarFilename");
		jarFilenameTextfield.setComponentPopupMenu(new TextComponentPopupMenu(jarFilenameTextfield));
		scrollPaneForJarClassTab.setName("jScrollPane1");

		fileChoiceButton.setText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "fileChoiceButton.text"));
		fileChoiceButton.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "fileChoiceButton.toolTipText"));
		fileChoiceButton.setName("fileChoiceButton");

		jarFilenameLabel.setText(MessageUtil.getMessage(MSG_GLOBAL, "jarNameLabel.text"));
		jarFilenameLabel.setName("jarFilenameLabeljLabel1");

		addToCatalogButton.setText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "addToRepoButton.text"));
		addToCatalogButton.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "addToRepoButton.toolTipText"));
		addToCatalogButton.setName("addToRepoButton");

		startScanButton.setText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "startScanButton.text"));
		startScanButton.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "startScanButton.toolTipText"));
		startScanButton.setName("startScanButton");

		clearButton.setText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "fileClearButton.text"));
		clearButton.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "fileClearButton.toolTipText"));
		clearButton.setName("fileClearButton");
		clearButton.setEnabled(false);

		cancelScanButton.setText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "cancelScanButton.text"));
		cancelScanButton.setToolTipText(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "cancelScanButton.toolTipText"));
		cancelScanButton.setName("cancelScanButton");
		cancelScanButton.setEnabled(false);

		GroupLayout jarToClassPanelLayout = new GroupLayout(this);
		setLayout(jarToClassPanelLayout);

		jarToClassPanelLayout
				.setHorizontalGroup(jarToClassPanelLayout
						.createParallelGroup(
							GroupLayout.Alignment.LEADING)
						.addComponent(scrollPaneForJarClassTab,
							GroupLayout.DEFAULT_SIZE, 399,
							Short.MAX_VALUE)
						.addGroup(jarToClassPanelLayout.createSequentialGroup()
							.addContainerGap()
							.addComponent(jarFilenameLabel)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jarFilenameTextfield, GroupLayout.PREFERRED_SIZE,	404, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(textfieldCompletedButton, GroupLayout.PREFERRED_SIZE,	GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(jarToClassPanelLayout.createSequentialGroup()
							.addComponent(fileChoiceButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(startScanButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(cancelScanButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(addToCatalogButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(clearButton))
						);
		jarToClassPanelLayout
				.setVerticalGroup(jarToClassPanelLayout
						.createSequentialGroup()
						.addGroup(jarToClassPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(jarFilenameLabel)
							.addComponent(jarFilenameTextfield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(textfieldCompletedButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(jarToClassPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(fileChoiceButton)
							.addComponent(startScanButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(cancelScanButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(addToCatalogButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(clearButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(scrollPaneForJarClassTab, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE));


		addToCatalogButton.setEnabled(false);
	}
}
