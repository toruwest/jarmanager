package t.n.jarmanager.view;

import static t.n.jarmanager.view.IJarManagerView.MSG_CATALOG_TAB;
import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableColumn;

import org.apache.log4j.Logger;
import org.fuin.aspects4swing.InvokeLater;

import t.n.jarmanager.IMainController;
import t.n.jarmanager.ITabTransitState;
import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.JarInfo;
import t.n.jarmanager.util.MessageUtil;

public class CatalogTabPanel extends JPanel implements ICatalogTabView {
	private static final Logger logger = Logger.getLogger(CatalogTabPanel.class);

	private final ITabTransitState tabTransitState;
	private final IJarManagerView mainView;

	private final JarSearchActionAdaptor jarSearchActionAdaptor;
	private JTable catalogTable;
	private JScrollPane catalogScrollPane;
	private CatalogTableRenderer jarEntryTableRenderer;
	private CatalogModel catalogModel;

	private JButton validCheckStartButton;
	private JButton validCheckCancelButton;
	private JButton catalogUpdateButton;
	private JTextField searchJarNameTextfield;
	private JButton jarSearchStartButton;
	private JButton jarSearchNextButton;
	private JButton jarSearchPrevButton;
	private JButton jarSearchClearButton;
	private JMenuItem changeToJarContentsMenuItem;
	private JMenuItem copyJarFileFullpathMenuItem;
	private JLabel jarNameLabel;

	public CatalogTabPanel(final IMainController mainController, final ITabTransitState tabTransitState, final IJarManagerView mainView) {
		this.tabTransitState = tabTransitState;
		this.mainView = mainView;

		initComponent();

		validCheckStartButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				validCheckStartButton.setEnabled(false);
				validCheckCancelButton.setEnabled(true);
				catalogUpdateButton.setEnabled(false);
				mainView.disableOperation();
				mainView.startProgressBar(false);
				mainController.startVerify();
			}
		});

		validCheckCancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				validCheckCancelButton.setEnabled(false);
				mainController.cancelVerifying();
			}
		});

		catalogUpdateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				validCheckStartButton.setEnabled(false);
				catalogUpdateButton.setEnabled(false);
				mainView.disableOperation();
				mainView.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "now_updating"));
				mainController.doUpdate();
			}
		});

		jarSearchActionAdaptor = new JarSearchActionAdaptor(mainView, this, catalogModel);
		jarSearchStartButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				jarSearchStartButton.setEnabled(false);
				jarSearchActionAdaptor.startSearch(searchJarNameTextfield.getText());
			}
		});

		jarSearchNextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				jarSearchStartButton.setEnabled(false);
				jarSearchActionAdaptor.showNext();
			}
		});
		jarSearchPrevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				jarSearchStartButton.setEnabled(false);
				jarSearchActionAdaptor.showPrev();
			}
		});

		jarSearchClearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				jarSearchActionAdaptor.clearSearch();
			}
		});

		searchJarNameTextfield.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				onCheckSearchJarNameFieldUpdated();
			}
		});

		searchJarNameTextfield.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent e) {
				onCheckSearchJarNameFieldUpdated();
			}

			@Override
			public void focusLost(FocusEvent e) {
				onCheckSearchJarNameFieldUpdated();
			}
		});

		catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		catalogTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed( MouseEvent e ){
				if ( SwingUtilities.isRightMouseButton( e )) {
					Point p = e.getPoint();
					int rowNumber = catalogTable.rowAtPoint( p );
					ListSelectionModel model = catalogTable.getSelectionModel();
					model.setSelectionInterval( rowNumber, rowNumber );
					changeToJarContentsMenuItem.setEnabled(true);
					copyJarFileFullpathMenuItem.setEnabled(true);
				}
			}
		});
	}

	@Override
	public void setEnabledJarSearchNextButton(final boolean b) {
		jarSearchNextButton.setEnabled(b);
	}

	@Override
	public void setEnabledJarSearchPrevButton(final boolean b) {
		jarSearchPrevButton.setEnabled(b);
	}

	@Override
	public void clearJarFilenameSearch() {
		searchJarNameTextfield.setText("");
		jarSearchClearButton.setEnabled(false);
		jarSearchNextButton.setEnabled(false);
		jarSearchPrevButton.setEnabled(false);
		mainView.setMessage("");
	}

	@Override
	public List<Integer> convertToViewIndexInCatalog(List<Integer> foundJarnameList) {
		List<Integer> result = new ArrayList<Integer>();
		if(foundJarnameList != null) {
			for(Integer index : foundJarnameList) {
				int viewIndex = catalogTable.convertRowIndexToView(index);
				result.add(viewIndex);
			}
		}
		Collections.sort(result);
		return result;
	}

	@Override
	@InvokeLater
	public void changeSelectionCatalogTable(final Integer index,
			final int colJarFilename, final boolean b1, final boolean b2) {
		catalogTable.changeSelection(index, colJarFilename, b1, b2);
	}

	protected void addRowCatalogModel(final JarInfo replacedEntry) {
		catalogModel.addRow(replacedEntry);
	}

	protected void updateDataCatalogModel(final String path, final CatalogEntryStatus replaced) {
		catalogModel.updateData(path, replaced);
	}

	protected void removeRowCatalogModel(final JarInfo replacedEntry) {
		catalogModel.removeRow(replacedEntry);
	}

	protected void updateCatalogTable(List<JarInfo> entryList) {
		catalogModel.setData(entryList);

		// indicate the total number of items for messageLabel.
		int catalogSize = entryList.size();
		if (catalogSize == 0) {
			mainView.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "no_jars_in_catalog"));
		} else {
			mainView.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "no_of_jar_files_are_registered", catalogSize));
		}
	}

	protected void setupButtonOnVerifyCompleted() {
		validCheckStartButton.setEnabled(true);
		validCheckCancelButton.setEnabled(false);
		mainView.enableOperation();
	}

	protected void setupButtonOnUpdateCompleted() {
		catalogUpdateButton.setEnabled(false);
		validCheckStartButton.setEnabled(true);
		mainView.enableOperation();
	}

	protected void setCatalogUpdateButtonEnabled(final boolean b) {
		catalogUpdateButton.setEnabled(b);
	}

	private void onCheckSearchJarNameFieldUpdated() {
		if (searchJarNameTextfield.getText().length() > 0) {
			jarSearchStartButton.setEnabled(true);
			jarSearchClearButton.setEnabled(true);
		} else {
			jarSearchStartButton.setEnabled(false);
			jarSearchClearButton.setEnabled(false);
		}
	}

	private File getJarfilePathFromTableModel(int selectedRow) {
		String folder = catalogTable.getValueAt(selectedRow,
				CatalogModel.COL_JAR_FOLDER).toString();
		String file = catalogTable.getValueAt(selectedRow,
				CatalogModel.COL_JAR_FILENAME).toString();

		File targetJarFile = new File(folder, file);
		return targetJarFile;
	}

	private void initComponent() {
		setName("catalogPanel");

		validCheckStartButton = new JButton();
		validCheckCancelButton = new JButton();
		validCheckCancelButton.setEnabled(false);
		catalogUpdateButton = new JButton();
		catalogUpdateButton.setEnabled(false);
		jarNameLabel = new JLabel();
		searchJarNameTextfield = new JTextField();
		jarSearchStartButton = new JButton();
		jarSearchStartButton.setEnabled(false);
		jarSearchNextButton = new JButton();
		jarSearchNextButton.setEnabled(false);
		jarSearchPrevButton = new JButton();
		jarSearchPrevButton.setEnabled(false);
		jarSearchClearButton = new JButton();
		jarSearchClearButton.setEnabled(false);

		catalogScrollPane = new JScrollPane();
		catalogScrollPane.setName("catalogScrollPane");
		String[] columnNames = new String[CatalogModel.getColumnSize()];
		for (int i = 0; i < CatalogModel.getColumnSize(); i++) {
			columnNames[i] = MessageUtil.getMessage(MSG_CATALOG_TAB, "catalogTable.columnModel.title" + i);
		}

		catalogModel = new CatalogModel(columnNames);
		catalogTable = new JTable(catalogModel);
		jarEntryTableRenderer = new CatalogTableRenderer();
		catalogTable.setDefaultRenderer(Boolean.class, jarEntryTableRenderer);

		catalogTable.setAutoCreateRowSorter(true);
		catalogTable.setComponentPopupMenu(createPopupMenu());
		catalogTable.setName("jTable1");

		catalogScrollPane.setViewportView(catalogTable);

		TableColumn column = null;
		for (int i = 0; i < catalogModel.getColumnCount(); i++) {
			column = catalogTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(catalogModel.getColumnPrefferedSize(i));
		}
		catalogTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt){
				jarSearchActionAdaptor.notifyTableHeaderClicked();
			}
		});

		validCheckStartButton.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "validCheckStartButton.text"));
		validCheckStartButton.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "validCheckStartButton.toolTipText"));
		validCheckStartButton.setName("validCheckStartButton");
		validCheckCancelButton.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "validCheckCancelButton.text"));
		validCheckCancelButton.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "validCheckCancelButton.toolTipText"));
		validCheckCancelButton.setName("validCheckCancelButton");

		catalogUpdateButton.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "repositUpdateButton.text"));
		catalogUpdateButton.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB,"repositUpdateButton.toolTipText"));
		catalogUpdateButton.setName("repositUpdateButton");
		jarNameLabel.setText(MessageUtil.getMessage(MSG_GLOBAL,  "jarNameLabel.text"));
		jarNameLabel.setName("jarNameLabel");
		searchJarNameTextfield.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "tfSearchJarName.toolTipText"));
		searchJarNameTextfield.setName("tfSearchJarName");
		searchJarNameTextfield.setComponentPopupMenu(new TextComponentPopupMenu(searchJarNameTextfield));
		jarSearchStartButton.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchStartButton.text"));
		jarSearchStartButton.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchStartButton.toolTipText"));
		jarSearchStartButton.setName("jarSearchStartButton");

		jarSearchNextButton.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchNextButton.text"));
		jarSearchNextButton.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchNextButton.toolTipText"));
		jarSearchNextButton.setName("jarSearchNextButton");

		jarSearchPrevButton.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchPrevButton.text"));
		jarSearchPrevButton.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchPrevButton.toolTipText"));
		jarSearchPrevButton.setName("jarSearchPrevButton");

		jarSearchClearButton.setText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchClearButton.text"));
		jarSearchClearButton.setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarSearchClearButton.toolTipText"));
		jarSearchClearButton.setName("jarSearchClearButton");

		GroupLayout catalogPanelLayout = new GroupLayout(this);
		setLayout(catalogPanelLayout);

		catalogPanelLayout.setHorizontalGroup(catalogPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(catalogPanelLayout.createSequentialGroup()
				.addGroup(catalogPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(catalogScrollPane, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
				.addGroup(catalogPanelLayout.createSequentialGroup().addContainerGap()
							.addComponent(jarNameLabel)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(searchJarNameTextfield,GroupLayout.PREFERRED_SIZE, 89, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jarSearchStartButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jarSearchNextButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jarSearchPrevButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(jarSearchClearButton))
				.addGroup(catalogPanelLayout.createSequentialGroup()
							.addComponent(validCheckStartButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(validCheckCancelButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(catalogUpdateButton)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 82, Short.MAX_VALUE)))
				.addContainerGap()));


		catalogPanelLayout.setVerticalGroup(catalogPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(catalogPanelLayout.createSequentialGroup()
				.addGroup(catalogPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(jarNameLabel)
					.addComponent(searchJarNameTextfield,GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addComponent(jarSearchStartButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(jarSearchNextButton)
					.addComponent(jarSearchPrevButton)
					.addComponent(jarSearchClearButton))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(catalogPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(validCheckStartButton)
					.addComponent(validCheckCancelButton)
					.addComponent(catalogUpdateButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addComponent(catalogScrollPane, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)));
	}

	private JPopupMenu createPopupMenu() {
		JPopupMenu menu = new JPopupMenu();

		changeToJarContentsMenuItem = new JMenuItem(
				MessageUtil.getMessage(MSG_CATALOG_TAB, "content_of_jar_file"));
		changeToJarContentsMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
					int[] selectedRows = catalogTable.getSelectedRows();
				if (selectedRows.length >= 1) {
					File targetJarFile = getJarfilePathFromTableModel(selectedRows[0]);
					tabTransitState.setFromCatalogTabPopupMenu(targetJarFile);
					mainView.selectTab(mainView.JAR_CLASS_TAB);
				}
			}

		});
		copyJarFileFullpathMenuItem = new JMenuItem(
				MessageUtil.getMessage(MSG_CATALOG_TAB, "copy_jarfile_fullpath"));
		copyJarFileFullpathMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				int[] selectedRows = catalogTable.getSelectedRows();
				if (selectedRows.length >= 1) {
					File targetJarFile = getJarfilePathFromTableModel(selectedRows[0]);
					Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
					StringSelection ss = new StringSelection(targetJarFile.toString());
					clip.setContents(ss, ss);
				}
			}
		});

		changeToJarContentsMenuItem.setEnabled(false);
		copyJarFileFullpathMenuItem.setEnabled(false);

		menu.add(changeToJarContentsMenuItem);
		menu.add(copyJarFileFullpathMenuItem);
		return menu;
	}

}
