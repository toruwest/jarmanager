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

import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.JarInfo;
import t.n.jarmanager.util.JarFileUtil;

public final class UpdateWorker {
	private static final Logger logger = Logger.getLogger(UpdateWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<?> future;
	private boolean isExecuting = false;

	private Map<JarInfo, CatalogRegistResult> statusMap;

	private UpdateWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static UpdateWorker getInstance(WorkerUtil workerUtil) {
		return new UpdateWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void update() {
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


		statusMap = new HashMap<JarInfo, CatalogRegistResult>();

		future = es.submit(new Runnable() {
			@Override
			public void run() {
				List<JarInfo> jarList = workerUtil.requestJarEntryList();

				for (JarInfo jarInfo : jarList) {
					File jarFile = new File(jarInfo.getJarFullpathname());
					if (jarFile.exists()) {
						String catalogSum = jarInfo.getChecksum();
						String fileSum = null;
						try {
							fileSum = JarFileUtil.calculateMD5sum(jarFile);
						} catch (IOException e) {
							logger.info("I/O error: " + jarFile.getAbsolutePath(), e);
						}
						if (!catalogSum.equals(fileSum)) {
							statusMap.put(jarInfo, workerUtil.tryToAddOrReplace(jarFile, fileSum));
						}
					} else {
						statusMap.put(jarInfo, workerUtil.removeJarFromCatalog(jarFile));
					}
				}
				logger.log(Level.DEBUG, "loop finished");
			}
		});

		try {
			future.get(); // block and wait
			workerUtil.getDispatcher().fireUpdateCompleteEvent(statusMap);
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

