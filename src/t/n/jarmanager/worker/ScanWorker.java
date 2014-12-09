package t.n.jarmanager.worker;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fuin.aspects4swing.StartNewThread;

import t.n.jarmanager.dto.CatalogRegistResult;
import t.n.jarmanager.dto.CatalogRegistStatus;

public final class ScanWorker {
	private static final Logger logger = Logger.getLogger(ScanWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<?> future;
	private volatile boolean isExecuting = false;
	private volatile boolean isCancelled;

	Stack<File> stack;
	private final Map<File, CatalogRegistResult> statusMap;

	private ScanWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
		statusMap = new HashMap<File, CatalogRegistResult>();
	}

	public static ScanWorker getInstance(WorkerUtil workerUtil) {
		return new ScanWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void startScan(final File topDir) {
		//Return immediately if already executing.
		if(isExecuting) {
			if(logger.isDebugEnabled()){
				logger.debug(getClass().getName() + ": failed to execute. Already executing.");
			}
			return;
		} else {
			isExecuting = true;
			isCancelled = false;
		}

		statusMap.clear();

		if (topDir.exists() && topDir.isDirectory()) {
			future = es.submit(new Runnable() {
				@Override
				public void run() {
					stack = new Stack<File>();
					stack.push(topDir);
					scanJarsFromDir();
				}
			});

			try {
				future.get(); // block and wait for finish.
				workerUtil.getDispatcher().fireScanCompleteEvent(!isCancelled, statusMap);
			} catch (InterruptedException e) {
				logger.log(Level.FATAL, e);
			} catch (ExecutionException e) {
				logger.log(Level.FATAL, e);
			} finally {
				isExecuting = false;
			}
		}
	}

	public void cancelScan() {
		isCancelled = true;
	}

	public boolean isScanning() {
		return isExecuting;
	}

	// Do the scan of JAR files and regist it at the same time.
	private void scanJarsFromDir() {
		if (!stack.isEmpty()) {
			for (File file : stack.pop().listFiles()) {
				if (!isCancelled) {
					if (file.isDirectory()) {
						workerUtil.getDispatcher().fireScanInProgressEvent(true, file);
						stack.push(file);
						scanJarsFromDir();// recursive call
						if (isCancelled) {
							break;
						}
					} else if (file.getName().toLowerCase().endsWith(".jar")) {
						notifyFoundJarFileToView(file);
					}
				} else { //cancelled
					break;
				}
			}

			if(isCancelled && !stack.isEmpty()) {
				stack.clear();
			}
		}
	}

	private void notifyFoundJarFileToView(File file) {
		CatalogRegistStatus currentStatus = CatalogRegistStatus.UNKNOWN;
		currentStatus = workerUtil.checkJarFileStatusInCatalog(file);

		CatalogRegistResult resultStat;

		if(currentStatus == CatalogRegistStatus.NEW_JAR || currentStatus == CatalogRegistStatus.UPDATED_JAR){
			resultStat = workerUtil.tryToAddOrReplace(file, null);
		} else {
			resultStat = convertMessageStatus(currentStatus);
		}
		statusMap.put(file, resultStat);
		workerUtil.getDispatcher().fireScanProgressStatusEvent(file, resultStat);
	}

	private CatalogRegistResult convertMessageStatus(CatalogRegistStatus status) {
		switch (status) {
		case IO_ERROR:
			return CatalogRegistResult.IO_ERROR;
		case NOT_EXIST:
			return CatalogRegistResult.JAR_NOT_EXIST;
		case NOT_JAR:
			return CatalogRegistResult.NOT_JAR;
		case REGISTERED_JAR:
			return CatalogRegistResult.NOT_CHANGED;
		case UNKNOWN:
			return CatalogRegistResult.UNDEFINED;
		default:
			//empty
		}

		return CatalogRegistResult.UNDEFINED;
	}

	private void dumpStatus(File file, CatalogRegistStatus status) {
		logger.debug("file:" + file + ", status:" + status.name());
	}
}
