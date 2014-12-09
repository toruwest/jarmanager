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

public final class FindClassWorker {
	private static final Logger logger = Logger.getLogger(FindClassWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<List<String>> future;
	private boolean isExecuting = false;

	private FindClassWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static FindClassWorker getInstance(WorkerUtil workerUtil) {
			return new FindClassWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	@StartNewThread
	public void doFind(final String targetClassName) {
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

		future = es.submit(new Callable<List<String>>() {
			@Override
			public List<String> call() {
				return workerUtil.findClass(targetClassName);
			}
		});

		try {
			//block and wait.
			workerUtil.getDispatcher().fireFindClassCompleteEvent(future.get());
		} catch (InterruptedException e) {
			logger.log(Level.FATAL, targetClassName, e);
		} catch (ExecutionException e) {
			logger.log(Level.FATAL, targetClassName, e);
		} finally {
			synchronized(this) {
				isExecuting = false;
			}
		}
	}
}