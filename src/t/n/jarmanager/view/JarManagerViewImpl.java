package t.n.jarmanager.view;

import java.awt.Color;
import java.awt.Container;
import java.io.File;
import java.util.EventObject;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.Logger;
import org.fuin.aspects4swing.InvokeLater;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application.ExitListener;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;

import t.n.jarmanager.IMainController;
import t.n.jarmanager.ITabTransitState;
import t.n.jarmanager.JarManagerApp;
import t.n.jarmanager.JarManagerController;
import t.n.jarmanager.TabTransitStateImpl;
import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.JarInfo;
import t.n.jarmanager.util.MessageUtil;

public final class JarManagerViewImpl extends FrameView implements IJarManagerView, ExitListener {
	private static final Logger logger = Logger.getLogger(JarManagerViewImpl.class);

	private final IMainController  mainController;
	private final ITabTransitState tabTransitState;

	private final SimpleAttributeSet classSearchResultFoundAttr = new SimpleAttributeSet();
	private final SimpleAttributeSet classSearchResultNormalAttr = new SimpleAttributeSet();

	private JMenuItem exitMenuItem;

	private JPanel mainPanel;
	private JTabbedPane jTabbedPane;

	private JLabel statusAnimationLabel;
	private JLabel statusMessageLabel;
	private JPanel statusPanel;

	private final JarClassTabPanel jarClassTabPanel;
	private final ClassJarTabPanel classJarTabPanel;
	private final CatalogTabPanel catalogTabPanel;

	private JDialog aboutBox;
	private JProgressBar progressBar;

	public JarManagerViewImpl(SingleFrameApplication app, File appDataDir) {
		super(app);
		getApplication().addExitListener(this);
		//To avoid creating new directory.
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);

		mainController = new JarManagerController(this, appDataDir);
		tabTransitState = new TabTransitStateImpl();
		jarClassTabPanel = new JarClassTabPanel(mainController, tabTransitState, this);
		classJarTabPanel = new ClassJarTabPanel(mainController, this);
		catalogTabPanel  = new CatalogTabPanel(mainController, tabTransitState, this);

		initComponents();

		jTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int tabIndex = jTabbedPane.getModel().getSelectedIndex();

				switch (tabIndex) {
				case JAR_CLASS_TAB:
					setMessage(""); //This will be overwritten soon...
					if (tabTransitState.isFromCatalogTabPopupMenu()) {
						//If transitted by popup menu from 'catalog' tab, the JAR file is already registered, so, disable the register button.
						File targetJarFile = tabTransitState.getTargetJarFile();
						tabTransitState.clearStatus();

						//dump the contents of JAR file.
						//The result will be notified with another receiver method. (JarClassTabViewImpl#setTextOnJarContentTextArea())
						jarClassTabPanel.setTextOnJarFilenameTextfield("");
						mainController.requestJarFileContentsInCatalog(targetJarFile);
						jarClassTabPanel.setJarFileClearButtonEnabled(true);
						jarClassTabPanel.setTextfieldCompletedButtonEnabled(false);
						jarClassTabPanel.setAddToCatalogButtonEnabled(false);
						setMessage(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "registeredJar.text"));
					} else if (tabTransitState.hasUnregisteredCandidateJarFiles()) {
						// When the tab is switched here -> another -> here, then restore unregistered JAR files.
						String candidate = tabTransitState.getUnregisteredCandidateJarFiles();

						jarClassTabPanel.setTextOnJarFilenameTextfield(candidate);
						setMessage(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "all_new.text"));
						tabTransitState.clearStatus();
					}
					break;
				case CLASS_JAR_TAB:
					statusMessageLabel.setText("");
					break;
				case CATALOG_TAB:
					statusMessageLabel.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "wait_reading_catalog"));
					//The result will be notified with another receiver method. (updateCatalogTable())
					mainController.requestJarEntryList();
					break;
				}
			}
		});
		StyleConstants.setForeground(classSearchResultFoundAttr, Color.RED);
		StyleConstants.setForeground(classSearchResultNormalAttr, Color.BLACK);
	}

	@Action
	public void showAboutBox() {
		if (aboutBox == null) {
			JFrame mainFrame = JarManagerApp.getApplication().getMainFrame();
			aboutBox = new JarManagerAboutBox(mainFrame);
			aboutBox.setLocationRelativeTo(mainFrame);
		}
		JarManagerApp.getApplication().show(aboutBox);
	}

	@Override
	@InvokeLater
	public void startProgressBar(boolean isIndeterminate) {
		progressBar.setVisible(true);
		progressBar.setIndeterminate(isIndeterminate);
	}

	@Override
	@InvokeLater
	public void setValueProgressBar(final int count) {
		progressBar.setValue(count);
	}

	@Override
	@InvokeLater
	public void setValueProgressBar(int totalCount, int count) {
		progressBar.setMaximum(totalCount);
		progressBar.setValue(count);
	}

	@Override
	@InvokeLater
	public void finishProgressBar() {
		progressBar.setIndeterminate(false);
		progressBar.setValue(100);
		progressBar.setVisible(false);
	}

	@Override
	@InvokeLater
	public void setMessage(String message) {
		statusMessageLabel.setText(message);
	}

	@Override
	@InvokeLater
	public void setTextOnJarContentOfClassJarTab(String string) {
		classJarTabPanel.setTextOnJarContentOfClassJarTab(string);
	}

	@Override
	@InvokeLater
	public void setTextOnJarContentOfJarClassTab(String string) {
		jarClassTabPanel.setTextOnJarContentOfJarClassTab(string);
	}

	@Override
	@InvokeLater
	public void appendTextOnJarContentOfJarClassTab(String string) {
		jarClassTabPanel.appendTextOnJarContentOfJarClassTab(string);
	}

	@Override
	@InvokeLater
	public void setupButtonOnAddJarCompleted() {
		jarClassTabPanel.setupButtonOnAddJarCompleted();
	}

	@Override
	@InvokeLater
	public void setAddToCatalogButtonEnabled(boolean b) {
		jarClassTabPanel.setAddToCatalogButtonEnabled(b);
	}

	@Override
	@InvokeLater
	public void selectJarContentOfJarClassTab(int i, int j) {
		jarClassTabPanel.selectJarContentOfJarClassTab(i, j);
	}

	@Override
	@InvokeLater
	public void setupButtonOnScanCompleted() {
		jarClassTabPanel.setupButtonOnScanCompleted();
	}

	@Override
	@InvokeLater
	public void setupButtonOnVerifyCompleted() {
		catalogTabPanel.setupButtonOnVerifyCompleted();
	}

	@Override
	@InvokeLater
	public void setCatalogUpdateButtonEnabled(boolean b) {
		catalogTabPanel.setCatalogUpdateButtonEnabled(b);
	}

	@Override
	@InvokeLater
	public void setupButtonOnUpdateCompleted() {
		catalogTabPanel.setupButtonOnUpdateCompleted();
	}

	@Override
	@InvokeLater
	public void setupButtonOnFindClassCompleted() {
		classJarTabPanel.setupButtonOnFindClassCompleted();
	}

	@Override
	@InvokeLater
	public void addRowCatalogModel(JarInfo jarInfo) {
		catalogTabPanel.addRowCatalogModel(jarInfo);
	}

	@Override
	@InvokeLater
	public void updateDataCatalogModel(String path, CatalogEntryStatus replaced) {
		catalogTabPanel.updateDataCatalogModel(path, replaced);
	}

	@Override
	@InvokeLater
	public void removeRowCatalogModel(JarInfo jarInfo) {
		catalogTabPanel.removeRowCatalogModel(jarInfo);
	}

	@Override
	@InvokeLater
	public void updateCatalogTable(List<JarInfo> entryList) {
		catalogTabPanel.updateCatalogTable(entryList);
	}

	@Override
	@InvokeLater
	public void addJarFileListModel(List<String> itemList) {
		classJarTabPanel.addJarFileListModel(itemList);
	}

	@Override
	@InvokeLater
	public void clearJarFileListModel() {
		classJarTabPanel.clearJarFileListModel();
	}

	@Override
	@InvokeLater
	public void setHilightFoundClass(List<Integer> indexList, int length) {
		classJarTabPanel.setHilightFoundClass(indexList, length);
	}

	@Override
	@InvokeLater
	public void enableOperation() {
		//Enables the close button of title bar.
		//getFrame().setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		exitMenuItem.setEnabled(true);
		jTabbedPane.setEnabled(true);
	}

	@Override
	@InvokeLater
	public void disableOperation() {
		//Temporarily disables the close button of title bar.
		//The following way doesn't work (the app terminates immediately), so I use SwingFramework#ExitListener.
		//getFrame().setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		exitMenuItem.setEnabled(false);
		jTabbedPane.setEnabled(false);
	}

	@Override
	@InvokeLater
	public void selectTab(int tabIndex) {
		jTabbedPane.setSelectedIndex(tabIndex);
	}

	@Override
	public Container getContainer() {
		return super.getContext().getFocusOwner().getParent();
	}

	@Override
	public boolean canExit(EventObject arg0) {
		//If this method return true, the application terminates immediately without any repaint.
		//So, return false and do the following shutdown procedure.

		if(mainController.isDoingSomethig()) {
			String msg = MessageUtil.getMessage(MSG_GLOBAL, "confirmExit");
			String title = MessageUtil.getMessage(MSG_GLOBAL, "exit");

			switch(JOptionPane.showConfirmDialog(getFrame(), msg, title, JOptionPane.YES_NO_OPTION)) {
			case JOptionPane.YES_OPTION:
				mainController.cancelAll();
				//All cancellable worker thread is safely terminated.
				//Shutdown of the database and Executor service will be shutdown separately.
				requestShutdownAfterDelay(2000, 500);
				break;

			case JOptionPane.NO_OPTION:
				break;
			}
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void willExit(EventObject arg0) {
		mainController.shutdown();
	}

	private void requestShutdownAfterDelay(final int delay, final int holdTime) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
					statusMessageLabel.setText(MessageUtil.getMessage(MSG_GLOBAL, "exitMessage"));
					Thread.sleep(holdTime);
					getFrame().dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu();
		exitMenuItem = new JMenuItem();

		JMenu helpMenu = new JMenu();
		JMenuItem aboutMenuItem = new JMenuItem();

		JSeparator statusPanelSeparator = new JSeparator();
		statusPanel = new JPanel();
		statusMessageLabel = new JLabel();
		statusAnimationLabel = new JLabel();
		progressBar = new JProgressBar();

		menuBar.setName("menuBar");
		mainPanel = new JPanel();
		mainPanel.setAlignmentX(10.0F);
		mainPanel.setAlignmentY(10.0F);
		mainPanel.setName("mainPanel");

		fileMenu.setText(MessageUtil.getMessage(MSG_GLOBAL, "fileMenu.text"));
		fileMenu.setName("fileMenu");

		ActionMap actionMap = org.jdesktop.application.Application
				.getInstance(t.n.jarmanager.JarManagerApp.class).getContext()
				.getActionMap(JarManagerViewImpl.class, this);
		exitMenuItem.setAction(actionMap.get("quit"));
		exitMenuItem.setName("exitMenuItem");
		exitMenuItem.setText(MessageUtil.getMessage(MSG_GLOBAL, "exitMenuItem.text"));
		fileMenu.add(exitMenuItem);

		menuBar.add(fileMenu);

		helpMenu.setText(MessageUtil.getMessage(MSG_GLOBAL, "helpMenu.text"));
		helpMenu.setName("helpMenu");

		aboutMenuItem.setAction(actionMap.get("showAboutBox"));
		aboutMenuItem.setName("aboutMenuItem");
		aboutMenuItem.setText(MessageUtil.getMessage(MSG_GLOBAL, "aboutMenuItem.text"));
		helpMenu.add(aboutMenuItem);

		menuBar.add(helpMenu);

		statusPanel.setName("statusPanel");
		statusMessageLabel.setSize(30, 5);
		statusMessageLabel.setName("statusMessageLabel");

		statusAnimationLabel
				.setHorizontalAlignment(SwingConstants.LEFT);
		statusAnimationLabel.setSize(30, 5);
		statusAnimationLabel.setName("statusAnimationLabel");

		progressBar.setName("progressBar");
		progressBar.setSize(10, 5);
		progressBar.setVisible(false);

		jTabbedPane = new JTabbedPane();
		jTabbedPane.setName("jTabbedPane");

		jTabbedPane.addTab(MessageUtil.getMessage(MSG_GLOBAL, "jPanel1.TabConstraints.tabTitle"), jarClassTabPanel);
		jTabbedPane.addTab(MessageUtil.getMessage(MSG_GLOBAL, "jPanel2.TabConstraints.tabTitle"), classJarTabPanel);
		jTabbedPane.addTab(MessageUtil.getMessage(MSG_GLOBAL, "jPanel3.TabConstraints.tabTitle"), catalogTabPanel);

		GroupLayout statusPanelLayout = new GroupLayout(statusPanel);
		statusPanel.setLayout(statusPanelLayout);

		statusPanelLayout
		.setVerticalGroup(statusPanelLayout
				.createParallelGroup(
						GroupLayout.Alignment.LEADING)
						.addGroup(
								statusPanelLayout
								.createSequentialGroup()
								.addComponent(
										statusPanelSeparator,
										GroupLayout.PREFERRED_SIZE,
										2,
										GroupLayout.PREFERRED_SIZE)
										.addGroup(
												statusPanelLayout
												.createParallelGroup(
														GroupLayout.Alignment.LEADING)
														.addGroup(
																statusPanelLayout
																.createSequentialGroup()
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED,
																		GroupLayout.DEFAULT_SIZE,
																		Short.MAX_VALUE)
																		.addGroup(
																				statusPanelLayout
																				.createParallelGroup(
																						GroupLayout.Alignment.BASELINE)
																						.addComponent(
																								statusMessageLabel,
																								GroupLayout.PREFERRED_SIZE,
																								22,
																								GroupLayout.PREFERRED_SIZE)
																								.addComponent(
																										statusAnimationLabel,
																										GroupLayout.PREFERRED_SIZE,
																										17,
																										GroupLayout.PREFERRED_SIZE))
																										.addGap(25,
																												25,
																												25))
																												.addGroup(
																														statusPanelLayout
																														.createSequentialGroup()
																														.addPreferredGap(
																																LayoutStyle.ComponentPlacement.RELATED)
																																.addComponent(
																																		progressBar,
																																		GroupLayout.PREFERRED_SIZE,
																																		GroupLayout.DEFAULT_SIZE,
																																		GroupLayout.PREFERRED_SIZE)
																																		.addContainerGap()))));



		statusPanelLayout
		.setHorizontalGroup(statusPanelLayout
				.createParallelGroup(
						GroupLayout.Alignment.LEADING)
						.addGroup(
								statusPanelLayout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										statusPanelLayout
										.createParallelGroup(
												GroupLayout.Alignment.LEADING)
												.addComponent(
														statusPanelSeparator,
														GroupLayout.DEFAULT_SIZE,
														608,
														Short.MAX_VALUE)
														.addGroup(
																GroupLayout.Alignment.TRAILING,
																statusPanelLayout
																.createSequentialGroup()
																.addComponent(
																		statusMessageLabel,
																		GroupLayout.DEFAULT_SIZE,
																		408,
																		Short.MAX_VALUE)
																		.addPreferredGap(
																				LayoutStyle.ComponentPlacement.UNRELATED)
																				.addComponent(
																						progressBar,
																						GroupLayout.PREFERRED_SIZE,
																						GroupLayout.DEFAULT_SIZE,
																						GroupLayout.PREFERRED_SIZE)
																						.addGap(18,
																								18,
																								18)
																								.addComponent(
																										statusAnimationLabel,
																										GroupLayout.PREFERRED_SIZE,
																										12,
																										GroupLayout.PREFERRED_SIZE)
																										.addContainerGap()))));
//	}

		GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainPanelLayout);

		mainPanelLayout.setHorizontalGroup(mainPanelLayout.createParallelGroup(
				GroupLayout.Alignment.LEADING).addGroup(
				mainPanelLayout.createSequentialGroup().addContainerGap()
						.addComponent(jTabbedPane).addContainerGap()
						.addContainerGap()));
		mainPanelLayout.setVerticalGroup(mainPanelLayout
				.createSequentialGroup().addGroup(
						mainPanelLayout.createSequentialGroup()
								.addComponent(jTabbedPane).addContainerGap()));

		setMenuBar(menuBar);
		setComponent(mainPanel);
		setStatusBar(statusPanel);

		statusAnimationLabel.setVisible(true);
		statusMessageLabel.setText("");
		statusMessageLabel.setVisible(true);

		statusMessageLabel.setVisible(true);
		statusMessageLabel.setBackground(Color.RED);
	}

}
