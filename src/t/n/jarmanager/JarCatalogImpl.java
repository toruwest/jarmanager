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
package t.n.jarmanager;

import java.io.File;
import java.util.List;

import t.n.jarmanager.worker.AddWorker;
import t.n.jarmanager.worker.FindClassWorker;
import t.n.jarmanager.worker.JarContentsWorker;
import t.n.jarmanager.worker.JarEntryListWorker;
import t.n.jarmanager.worker.JarFileContentWorker;
import t.n.jarmanager.worker.RegistCheckWorker;
import t.n.jarmanager.worker.ScanWorker;
import t.n.jarmanager.worker.UpdateWorker;
import t.n.jarmanager.worker.VerifyWorker;
import t.n.jarmanager.worker.WorkerUtil;

public final class JarCatalogImpl implements IJarCatalog {
	private final ICatalogEventListener catalogEventListener;
	private final VerifyWorker verifyWorker;
	private final UpdateWorker updateWorker;
	private final AddWorker addJarsWorker;
	private final FindClassWorker findClassWorker;
	private final ScanWorker scannerWorker;
	private final RegistCheckWorker registCheckWorker;
	private final JarContentsWorker jarContentsWorker;
	private final JarEntryListWorker jarEntryListWorker;
	private final JarFileContentWorker jarFileContentWorker;
	private final WorkerUtil workerUtil;

	private JarCatalogImpl(File dataDir, ICatalogEventListener catalogEventListener) throws Exception {
		this.catalogEventListener = catalogEventListener;
		workerUtil = WorkerUtil.getInstance(dataDir, catalogEventListener);
		verifyWorker = VerifyWorker.getInstance(workerUtil);
		updateWorker = UpdateWorker.getInstance(workerUtil);
		addJarsWorker = AddWorker.getInstance(workerUtil);
		findClassWorker = FindClassWorker.getInstance(workerUtil);
		scannerWorker = ScanWorker.getInstance(workerUtil);
		registCheckWorker = RegistCheckWorker.getInstance(workerUtil);
		jarContentsWorker = JarContentsWorker.getInstance(workerUtil);
		jarEntryListWorker = JarEntryListWorker.getInstance(workerUtil);
		jarFileContentWorker = JarFileContentWorker.getInstance(workerUtil);
	}

	public static IJarCatalog getInstance(File dataDir, ICatalogEventListener catalogEventListener) throws Exception {
		return new JarCatalogImpl(dataDir, catalogEventListener);
	}

	@Override
	public void shutdown() {

		verifyWorker.cleanup();
		updateWorker.cleanup();
		addJarsWorker.cleanup();
		findClassWorker.cleanup();
		scannerWorker.cleanup();
		registCheckWorker.cleanup();
		jarContentsWorker.cleanup();
		jarEntryListWorker.cleanup();
		jarFileContentWorker.cleanup();
		workerUtil.shutdown();
	}

	@Override
	public void requestJarEntryList() {
		jarEntryListWorker.requestJarEntryList();
	}

	@Override
	public void checkCatalogRegistStatus(List<File> candidateJarFileList) {
		registCheckWorker.doCheck(candidateJarFileList);
	}

	@Override
	public void startVerify() {
		verifyWorker.startVerify();
	}

	@Override
	public boolean isVerifying() {
		return verifyWorker.isVerifying();
	}

	@Override
	public void cancelVerifying() {
		verifyWorker.cancelVerifying();
	}

	@Override
	public void doUpdate() {
		updateWorker.update();
	}

	@Override
	public void addJars(List<File> jarFiles) {
		addJarsWorker.addJars(jarFiles);
	}

	@Override
	public void doFindClass(String className) {
		findClassWorker.doFind(className);
	}

	@Override
	public void startScan(File path) {
		scannerWorker.startScan(path);
	}

	@Override
	public boolean isScanning() {
		return scannerWorker.isScanning();
	}

	@Override
	public void cancelScanning() {
		scannerWorker.cancelScan();
	}

	@Override
	public void requestJarContent(File targetJarFile) {
		jarContentsWorker.requestJarContent(targetJarFile);
	}

	@Override
	public void requestJarContentWithFoundPosition(File selectedJarFile, String searchClass) {
		jarFileContentWorker.requestJarContentWithFoundPosition(selectedJarFile, searchClass);
	}
}
