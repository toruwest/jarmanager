package t.n.jarmanager.worker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.fuin.aspects4swing.StartNewThread;

import t.n.jarmanager.dto.JarFileContent;

public final class JarFileContentWorker {
	private static final Logger logger = Logger.getLogger(FindClassWorker.class);

	private final WorkerUtil workerUtil;
	private final ExecutorService es = Executors.newSingleThreadExecutor();
	private Future<?> future;
	private boolean isExecuting = false;

	private static final String NEW_LINE = "\n";

	private int totalCharacterCount;
	private int searchTextLength;

	private String searchClassLowerCase = null;

	private List<String> classList;
	private final List<Integer> indexList = new ArrayList<Integer>();
	private final StringBuilder content = new StringBuilder();

	private JarFileContentWorker(WorkerUtil workerUtil) {
		this.workerUtil = workerUtil;
	}

	public static JarFileContentWorker getInstance(WorkerUtil workerUtil) {
		return new JarFileContentWorker(workerUtil);
	}

	public void cleanup() {
		es.shutdown();
	}

	/**
	 * Get the content of specified JAR file in the catalog with found position of the searchClass.
	 * The result is notified with CatalogEventListener interface.
	 * The contents of the JAR file may contain searchClass more than 1, so the result of type of List<Integer>.
	 *
	 * @param jarFile
	 * @param searchClass
	 */
	@StartNewThread
	public void requestJarContentWithFoundPosition(final File jarFile, final String searchClass) {
		//start new thead
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

		future = es.submit(new Runnable() {
			@Override
			public void run() {
				searchTextLength = searchClass.length();
				searchClassLowerCase = searchClass.toLowerCase();
				content.delete(0, content.length());
				content.append(workerUtil.getJarManifestAndOtherFileContentInCatalog(jarFile));
				totalCharacterCount = content.length();
				classList = workerUtil.getClassListInCatalog(jarFile);
			}
		});

		try {
			future.get(); // blocked
			generateIndexList();
			workerUtil.getDispatcher().fireIndexedJarContentFetchCompleteEvent(new JarFileContent(content.toString(), indexList), searchTextLength);
		} catch (InterruptedException e) {
			logger.log(Level.FATAL, "jarFile:" + jarFile + ", searchClass:" + searchClass, e);
		} catch (ExecutionException e) {
			logger.log(Level.FATAL, "jarFile:" + jarFile + ", searchClass:" + searchClass, e);
		} finally {
			synchronized(this) {
				isExecuting = false;
			}
		}
	}

	protected void generateIndexList() {
		String  filenameLowerCase;
		indexList.clear();
		for(String clazz : classList) {
			filenameLowerCase = clazz.toLowerCase();
			content.append(clazz);
			content.append(NEW_LINE);
			//if clazz's filename contains the target string, then memorize the positon of filename in JAR file.
			//new line should be considered.
			int index = filenameLowerCase.indexOf(searchClassLowerCase);
			if(index >= 0) {
				indexList.add(totalCharacterCount + index);
			}
			totalCharacterCount += clazz.length() + NEW_LINE.length();
		}
	}

}
