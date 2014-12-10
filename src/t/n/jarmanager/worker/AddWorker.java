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

import t.n.jarmanager.dto.CatalogRegistResult;

public final class AddWorker {
	private static final Logger logger = Logger.getLogger(AddWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<Map<File, CatalogRegistResult>> future;
	private boolean isExecuting = false;

	private AddWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static AddWorker getInstance(WorkerUtil workerUtil) {
		return new AddWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void addJars(final List<File> jarFiles) {
		synchronized(this) {
			if(isExecuting) {
				if(logger.isDebugEnabled()){
					logger.debug(getClass().getName() + ": failed to execute. Already executing.");
				}
				return;
			} else {
				isExecuting = true;
			}
		}

		future = es.submit(new Callable<Map<File, CatalogRegistResult>>() {

			@Override
			public Map<File, CatalogRegistResult> call() {
				Map<File, CatalogRegistResult> statusMap = new HashMap<File, CatalogRegistResult>();
				if(logger.isDebugEnabled()){
					logger.debug("AddWorker: thread:" + Thread.currentThread().getName());
				}
				for (File file : jarFiles) {
					statusMap.put(file, workerUtil.tryToAddOrReplace(file, null));
				}
				return statusMap;
			}
		});

		try {
			//block and wait.
			workerUtil.getDispatcher().fireAddJarCompleteEvent(future.get());
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
