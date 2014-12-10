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

import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fuin.aspects4swing.StartNewThread;

import t.n.jarmanager.util.MessageUtil;

public final class JarContentsWorker {
	private static final Logger logger = Logger.getLogger(JarContentsWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<StringBuilder> future;
	private boolean isExecuting = false;

	private JarContentsWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static JarContentsWorker getInstance(WorkerUtil workerUtil) {
		return new JarContentsWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void requestJarContent(final File targetJarFile) {
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

		future = es.submit(new Callable<StringBuilder>() {
			@Override
			public StringBuilder call() {
				final StringBuilder result = new StringBuilder();
				result.append(MessageUtil.getMessage(MSG_GLOBAL, "jarNameLabel.text"));
				result.append(targetJarFile.getAbsolutePath());
				result.append("\n");

				result.append(workerUtil.getAllJarContentInCatalog(targetJarFile));
				return result;
			}
		});

		try {
			// block and wait for finish.
			workerUtil.getDispatcher().fireJarContentsFetchCompleteEvent(future.get().toString());
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
