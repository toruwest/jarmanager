package t.n.jarmanager.worker;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fuin.aspects4swing.StartNewThread;

import t.n.jarmanager.dto.JarInfo;

public final class JarEntryListWorker {
	private static final Logger logger = Logger.getLogger(JarEntryListWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future< List<JarInfo>> future;
	private boolean isExecuting = false;

	private JarEntryListWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static JarEntryListWorker getInstance(WorkerUtil workerUtil) {
		return new JarEntryListWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void requestJarEntryList() {
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

		future = es.submit(new Callable<List<JarInfo>>() {
			@Override
			public List<JarInfo> call() {
				 return workerUtil.requestJarEntryList();
			}
		});

		try {
			// block and wait for finish.
			workerUtil.getDispatcher().fireJarEntryListFetchCompleteEvent(future.get());
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
