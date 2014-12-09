package t.n.jarmanager;

import static t.n.jarmanager.view.IJarManagerView.MSG_CATALOG_TAB;
import static t.n.jarmanager.view.IJarManagerView.MSG_CLASS_JAR_TAB;
import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;
import static t.n.jarmanager.view.IJarManagerView.MSG_JAR_CLASS_TAB;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.zip.ZipException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.CatalogRegistStatus;
import t.n.jarmanager.dto.JarFileContent;
import t.n.jarmanager.dto.JarFileViewProperty;
import t.n.jarmanager.dto.JarInfo;
import t.n.jarmanager.util.JarFileUtil;
import t.n.jarmanager.util.JarFilenameUtil;
import t.n.jarmanager.util.MessageUtil;
import t.n.jarmanager.util.RegistStatusMessageUtil;
import t.n.jarmanager.util.ScanResultMessageUtil;
import t.n.jarmanager.view.IJarManagerView;

public final class JarManagerController implements IMainController, ICatalogEventListener {
	private static final Logger logger = Logger.getLogger(JarManagerController.class);
	private final long beginTime = 0;
	private IJarCatalog catalog;
	private final IJarManagerView view;

	private int foundJarCount;
	private int scannedFolderCount;
	private final JarManagerPreference pref;
	private List<File> candidateJarFileList;

	public JarManagerController(IJarManagerView view, File dataDir) {
		this.view = view;

		pref = new JarManagerPreference(dataDir);
		try {
			catalog = JarCatalogImpl.getInstance(dataDir, this);
		} catch (Exception e) {
			view.setMessage(MessageUtil.getMessage(MSG_GLOBAL, "init_error"));
		}
	}

	@Override
	public void saveIsBrowserDirectOpen(boolean isBrowserDirectOpen) {
		pref.saveIsBrowserDirectOpen(isBrowserDirectOpen);
	}

	@Override
	public File getPrevDir() {
		if(pref.exists()) {
			return pref.loadDir();
		} else {
			return null;
		}
	}

	@Override
	public void saveDir(File prevDir) {
		pref.saveDir(prevDir);
	}

	@Override
	public boolean isBrowserDirectOpen() {
		if(pref.exists()) {
			return  pref.loadIsBrowserDirectOpen();
		} else {
			return false;
		}
	}

	@Override
	public void prepareForRegistering(List<File> candidateJarFileList){
		this.candidateJarFileList = candidateJarFileList;
		if(candidateJarFileList.size() > 0) {
			//複数のJARファイルが指定された場合、どれかひとつでも未登録あるいはchecksumが変わっていれば登録可能とし、有効にする。(タイムスタンプは使わない）
			//JARファイルが登録済みで、checksumが同じなら、既に登録されていて登録する必要はないので、ボタンは無効にする。
			//結果はこのクラスのnotifyCheckCatalogStatusComplete()へ通知される。
			catalog.checkCatalogRegistStatus(candidateJarFileList);
		}
	}

	@Override
	public void doFindClass(String className) {
		catalog.doFindClass(className);
	}

	@Override
	public void dumpJarFileContentsInCatalogWithIndex(File selectedJarFile, String searchClass) {
		catalog.requestJarContentWithFoundPosition(selectedJarFile, searchClass);
	}

	// JARファイルの内容を表示する。
	//TODO JARファイルの登録有無だけでなく、状況に応じたメッセージを表示する。
	@Override
	public void dumpJarFileContents() {
		StringBuilder sb = new StringBuilder();
		if(candidateJarFileList != null) {
			if(candidateJarFileList.size() == 1 && candidateJarFileList.get(0).isDirectory()) {
				//ディレクトリが指定されていたら無視。スキャンが終わった後にありえる状況。
			} else {
				for (File f : candidateJarFileList) {
					if (f.toString().toLowerCase().endsWith(".jar")) {
						if(f.exists()) {
							JarFileViewProperty prop = JarFileUtil.getJarFileProperty(f);
							if(prop != null) {
								 if(!prop.hasError()) {
									 try {
										 sb.append(f.toString());
										 sb.append("\n");
										 sb.append(prop.getAllContent());
									 } catch (ZipException e) {
										 logger.log(Level.INFO, e);
									 } catch (IOException e) {
										 logger.log(Level.INFO, e);
									 }
								 } else {
									 if(prop.getErrorCause() == JarFileViewProperty.IO_ERROR) {
										 sb.append(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "failedJarIOError.text"));
										 sb.append(f.toString());
										 sb.append("\n");
									 }
								 }
							} else {
								sb.append(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "failedJarNotJar.text"));
								sb.append(f.toString());
								sb.append("\n");
							}
						} else {
							sb.append(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "failedJarNotExist.text"));
							sb.append(f.toString());
							sb.append("\n");
						}
					}
				}
			}
		} else {
			//以下のログファイルにスタックトレースを含める。http://d.hatena.ne.jp/fumokmm/20070724/1185294796
			logger.log(Level.FATAL, "candidateJarFileChecksum is null", new Throwable());
		}
		// textAreaのいちばん上に移動する。moveCaretPosition()だと表示したテキストが選択状態になり、不都合。
		view.setTextOnJarContentOfJarClassTab(sb.toString());
		view.selectJarContentOfJarClassTab(0,0);

	}

	@Override
	public boolean isDoingSomethig() {
		return catalog.isScanning()|| catalog.isVerifying();
	}

	private CountDownLatch cancelScanningLatch;
	private CountDownLatch cancelVerifyingLatch;

	@Override
	public void cancelAll() {
		cancelVerifyingLatch = new CountDownLatch(1);
		if(catalog.isVerifying()) {
			catalog.cancelVerifying();
			//workerからの終了イベントを待つ。
			try {
				cancelVerifyingLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		cancelScanningLatch = new CountDownLatch(1);
		if(catalog.isScanning()) {
			catalog.cancelScanning();
			//workerからのイベントを待つ。EDTだけど大丈夫?
			try {
				cancelScanningLatch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void shutdown() {
		catalog.shutdown();
	}

	@Override
	public void requestJarEntryList() {
		catalog.requestJarEntryList();
	}

	@Override

	public void addJars() {
		catalog.addJars(candidateJarFileList);
	}

	@Override
	public void cancelScanning() {
		catalog.cancelScanning();
	}

	// scan開始。Threadを使い中断可能に。中断してもそれまでに抽出したのは追加する。
	// 状況をプログレスバーに表示（進捗の基準はないので、適当に）
	// scanしたフォルダとJARファイルの数と、現在スキャンしているフォルダも表示する。
	//スキャン完了後（中断も）にはＪＡＲファイル名欄はクリアする。
	//スキャン開始時はＪＡＲファイル名に入力されているのが存在するフォルダだったらそこをファイル選択ダイアログで出す。
	//存在するＪＡＲファイルだったらその親のフォルダを出す。
	//存在していなければデフォルトのフォルダを出す。
	@Override
	public void startScan(File f) {
		catalog.startScan(f);
	}

	@Override
	public void startVerify() {
		catalog.startVerify();
	}

	@Override
	public void cancelVerifying() {
		catalog.cancelVerifying();
	}

	@Override
	public void doUpdate() {
		catalog.doUpdate();
	}

	@Override
	public void requestJarFileContentsInCatalog(File targetJarFile) {
		catalog.requestJarContent(targetJarFile);
	}

	@Override
	public void notifyScanInProgress(boolean isFolder, File file) {
		if (isFolder) {
			view.setMessage(foundJarCount + " / " + scannedFolderCount
					+ " :" + file
					+ MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "scanTargetFolder.text"));
			scannedFolderCount++;
		}
		view.setValueProgressBar(scannedFolderCount);
	}

	@Override
	public void notifyScanProgressStatus(File file, CatalogRegistResult status) {
		StringBuilder sb = new StringBuilder();
		sb.append(file.getAbsolutePath());
		sb.append(":");
		sb.append(ScanResultMessageUtil.decodeMessage(status));
		sb.append("\n");
		view.appendTextOnJarContentOfJarClassTab(sb.toString());
		foundJarCount++;

		view.setMessage(foundJarCount + " / " + scannedFolderCount
				+ ":" + file + MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "scanFoundJar.text", file.getAbsolutePath()));

	}

	@Override
	public void notifyScanComplete(boolean isCompleted, Map<File, CatalogRegistResult> statusMap) {

		long endTime = System.currentTimeMillis();
		view.finishProgressBar();

		StringBuilder sb = new StringBuilder();
		if(isCompleted) {
			sb.append(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "scanComplete.text"));
		} else {
			sb.append(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "scanCancelled.text"));
		}
		sb.append(ScanResultMessageUtil.genMessage(statusMap));
		view.setMessage(sb.toString());
		view.selectJarContentOfJarClassTab(0,0);
		view.setupButtonOnScanCompleted();

		statusMap.clear();

		if(logger.isDebugEnabled()) {
			logger.debug("スキャン件数と、掛かった時間:" + foundJarCount + ", "+ JarFilenameUtil.format(endTime - beginTime));
		}

		//スキャン中に閉じるボタンを使い、ダイアログでシャットダウンが選択されたときだけ使われる。
		if(cancelScanningLatch != null)cancelScanningLatch.countDown();
	}

	@Override
	public void notifyVerifyInProgress(int totalCount, int count, String jarFilename) {
		view.setValueProgressBar(totalCount, count);
		view.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "now_verifing") + jarFilename);
	}

	@Override
	public void notifyVerifyComplete(Map<File, CatalogEntryStatus> statusMap, boolean isCanceled) {
		int invalidatedCount = 0;

		for (File f : statusMap.keySet()) {
			CatalogEntryStatus status = statusMap.get(f);

			if (status == CatalogEntryStatus.DELETED) {
				view.updateDataCatalogModel(f.getPath(), CatalogEntryStatus.DELETED);
				invalidatedCount++;
			} else if (status == CatalogEntryStatus.REPLACED) {
				view.updateDataCatalogModel(f.getPath(), CatalogEntryStatus.REPLACED);
				invalidatedCount++;
			}
		}

		if(isCanceled) {
			view.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "verify_canceled"));
		} else {
			if (invalidatedCount > 0) {
				view.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "verify_completed")
						+ MessageUtil.getMessage(MSG_CATALOG_TAB, "some_items_become_invalid", invalidatedCount));
				view.setCatalogUpdateButtonEnabled(true);
			} else {
				view.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "verify_completed") + MessageUtil.getMessage(MSG_CATALOG_TAB, "all_items_are_ok"));
			}
		}

		view.setupButtonOnVerifyCompleted();
		view.finishProgressBar();

		if(cancelVerifyingLatch != null)cancelVerifyingLatch.countDown();
	}

	@Override
	public void notifyUpdateComplete(Map<JarInfo, CatalogRegistResult> statusMap) {
		view.setupButtonOnUpdateCompleted();
		view.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "update_completed"));
		// tableModelの更新
		for(JarInfo jarInfo : statusMap.keySet()) {
			CatalogRegistResult result = statusMap.get(jarInfo);
			switch(result) {
			case REMOVED:
				view.removeRowCatalogModel(jarInfo);
				break;
			case REPLACED:
				view.removeRowCatalogModel(jarInfo);
				view.addRowCatalogModel(jarInfo);
				break;
			default:
				//無視
			}
		}
	}

	@Override
	public void notifyFindClassComplete(List<String> result) {
		view.setupButtonOnFindClassCompleted();
		view.clearJarFileListModel();
		view.setTextOnJarContentOfClassJarTab("");

		if (result != null && !result.isEmpty()) {
			view.addJarFileListModel(result);
			view.setMessage(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "jarname.found.text", result.size()));
		} else {
			view.setMessage(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "jarname.not.found.text"));
		}
	}

	@Override
	public void notifyIndexedJarContentsFetchComplete(JarFileContent content, int length) {
		int classFoundCount = 0;
		view.setTextOnJarContentOfClassJarTab(content.getContents());
		List<Integer> indexList = content.getFoundClassIndexList();
		classFoundCount = indexList.size();
		view.setHilightFoundClass(indexList, length);
		view.setMessage(MessageUtil.getMessage(MSG_CLASS_JAR_TAB, "containsNItemsInThisJarFile", classFoundCount));
	}

	@Override
	public void notifyJarContentsFetchComplete(String content) {
		view.setTextOnJarContentOfJarClassTab(content);
	}

	@Override
	public void notifyCheckCatalogStatusComplete(Map<File, CatalogRegistStatus> statusMap) {

		if(RegistStatusMessageUtil.showMessage(view, statusMap)) {
			logger.log(Level.FATAL, new Throwable());
		}
	}

	@Override
	public void notifyJarEntryListFetchComplete(List<JarInfo> entryList) {
		view.updateCatalogTable(entryList);
	}

	@Override
	public void notifyAddJarComplete(Map<File, CatalogRegistResult> statusMap) {
		StringBuilder sb = new StringBuilder();
		for (File file : statusMap.keySet()) {
			CatalogRegistResult status = statusMap.get(file);
			sb.append(file.getAbsolutePath());
			sb.append(":");
			sb.append(ScanResultMessageUtil.decodeMessage(status));
			sb.append("\n");
		}
		view.setTextOnJarContentOfJarClassTab(sb.toString());
		sb = new StringBuilder();
		sb.append(ScanResultMessageUtil.genMessage(statusMap));
		view.setMessage(sb.toString());
	}

}