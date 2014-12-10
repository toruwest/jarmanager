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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fuin.aspects4swing.StartNewThread;

import t.n.jarmanager.dto.CatalogRegistStatus;

public final class RegistCheckWorker {
	private static final Logger logger = Logger.getLogger(RegistCheckWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<?> future;
	private boolean isExecuting = false;

	private RegistCheckWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static RegistCheckWorker getInstance(WorkerUtil workerUtil) {
		return new RegistCheckWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void doCheck(final List<File> candidateJarFileList) {
		synchronized(this) {
			if(isExecuting) {
				if(logger.isDebugEnabled()){
					logger.debug(getClass().getName() + ": failed to lock");
				}
				return;
			} else {
				isExecuting = true;
			}
		}

		//start another thread
		future = es.submit(new Callable<Map<File, CatalogRegistStatus>>() {
			@Override
			public Map<File, CatalogRegistStatus> call() {
				Map<File, CatalogRegistStatus> statusMap;
				statusMap = new HashMap<File, CatalogRegistStatus>();

				for (final File jarFile : candidateJarFileList) {
					statusMap.put(jarFile, workerUtil.checkJarFileStatusInCatalog(jarFile));
				}
				return statusMap;
			}
		});

		try {
			// block and wait for finish.
			workerUtil.getDispatcher().fireCheckCatalogStatusCompleteEvent((Map<File, CatalogRegistStatus>) future.get());
		} catch (InterruptedException e) {
			logger.log(Level.FATAL, e);
		} catch (ExecutionException e) {
			logger.log(Level.FATAL, e);
		} finally {
			synchronized(this) {
				isExecuting = false;
			}
		}
	}
}
