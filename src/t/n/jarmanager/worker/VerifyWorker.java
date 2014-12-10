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
package t.n.jarmanager.worker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fuin.aspects4swing.StartNewThread;

import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.JarInfo;
import t.n.jarmanager.util.JarFileUtil;

public final class VerifyWorker {
	private static final Logger logger = Logger.getLogger(VerifyWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<?> future;
	private volatile boolean isExecuting = false;
	private volatile boolean isCancelled = false;

	private Map<File, CatalogEntryStatus> statusMap;

	private VerifyWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static VerifyWorker getInstance(WorkerUtil workerUtil) {
		return new VerifyWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void startVerify() {
		if(isExecuting) {
			if(logger.isDebugEnabled()) {
				logger.debug(getClass().getName() + ": failed to execute. Already executing.");
			}
			return;
		} else {
			isExecuting = true;
		}

		statusMap = new HashMap<File, CatalogEntryStatus>();

		future = es.submit(new Runnable() {
			@Override
			public void run() {
				List<JarInfo> jarList = workerUtil.requestJarEntryList();
				int totalCount = jarList.size();
				//int count = 0;
				for (JarInfo jarInfo : jarList) {
					if(!isCancelled) {
						checkJarInfo(totalCount, jarInfo);
					} else {
						if(logger.isDebugEnabled()) {
							logger.debug("scanning calcelled");
						}
						break;//exit from for-loop
					}
				}
			}

			private int checkJarInfo(int totalCount, JarInfo jarInfo) {
				File jarFile = new File(jarInfo.getJarFullpathname());
				int count = 0;
				if (jarFile.exists()) {
					String catalogSum = jarInfo.getChecksum();
					String fileSum;
					try {
						fileSum = JarFileUtil.calculateMD5sum(jarFile);
						if (catalogSum.equals(fileSum)) {
							statusMap.put(jarFile, CatalogEntryStatus.NORMAL);
							workerUtil.getDispatcher().fireVerifyInProgressEvent(totalCount, count++, jarFile.getAbsolutePath());
						} else {
							statusMap.put(jarFile, CatalogEntryStatus.REPLACED);
							workerUtil.getDispatcher().fireVerifyInProgressEvent(totalCount, count++, jarFile.getAbsolutePath());
						}
					} catch (IOException e) {
						statusMap.put(jarFile, CatalogEntryStatus.IO_ERROR);
						workerUtil.getDispatcher().fireVerifyInProgressEvent(totalCount, count++, jarFile.getAbsolutePath());
					}
				} else {
					statusMap.put(jarFile, CatalogEntryStatus.DELETED);
					workerUtil.getDispatcher().fireVerifyInProgressEvent(totalCount, count++, jarFile.getAbsolutePath());
				}
				return count;
			}
		});

		try {
			future.get(); // block and wait
			workerUtil.getDispatcher().fireVerifyCompleteEvent(statusMap, isCancelled);
		} catch (InterruptedException e) {
			logger.log(Level.FATAL, e);
		} catch (ExecutionException e) {
			logger.log(Level.FATAL, e);
		} finally {
			isExecuting = false;
			isCancelled = false;
		}
	}

	public void cancelVerifying() {
		isCancelled = true;
	}

	public boolean isVerifying() {
		return isExecuting;
	}
}

