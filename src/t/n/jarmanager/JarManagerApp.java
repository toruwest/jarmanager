/*
 * JarManagerApp.java
 */

package t.n.jarmanager;

import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import t.n.jarmanager.util.MessageUtil;
import t.n.jarmanager.view.JarManagerViewImpl;

public class JarManagerApp extends SingleFrameApplication {

	private static final Logger logger = Logger.getLogger(JarManagerApp.class);

	private static File appDataDir;
	private static FileLock lock;
	private static File lockFile;
	private static FileOutputStream fs = null;
	private static FileChannel fc = null;

	//If you want to change the log level etc, then use the file 'JarManagerConfig.properties' in app directory.
	//http://db.apache.org/derby/docs/10.4/tuning/rtunproper26985.html
	private static Level level = Level.INFO;//default
	private static final String DEFAULT_DERBY_LOG_LEVEL = "20000";
	private static final String DEFAULT_DERBY_LOG_SQL = "false";
	private static String derbyLogSql = DEFAULT_DERBY_LOG_SQL;
	private static String derbyLogLevel = DEFAULT_DERBY_LOG_LEVEL;
	private static String locale;

    @Override
    protected void startup() {
    	JarManagerViewImpl view = new JarManagerViewImpl(this, appDataDir);
        show(view);
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of JarManagerApp
     */
    public static JarManagerApp getApplication() {
        return Application.getInstance(JarManagerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
    	appDataDir = getApplication().getContext().getLocalStorage().getDirectory();
    	makeAppDir();
    	readConfigFile();
    	setupLogging();
    	setupDerbyLog();

    	if(isExecuting()) {
    		String msg = MessageUtil.getMessage(MSG_GLOBAL, "fileLockDetected", appDataDir);
    		String title = MessageUtil.getMessage(MSG_GLOBAL, "alreadyRunning");
    		JOptionPane pane = new JOptionPane(msg, JOptionPane.OK_OPTION);
    		JDialog dialog = pane.createDialog(getApplication().getMainFrame(), title);
    		dialog.setVisible(true);
    		System.exit(1);
    	} else {
    		Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					//release lock (remove lock file)
					releaseLock();
				}
    		});

    		launch(JarManagerApp.class, args);
    	}
    }

	private static void releaseLock() {
		if(lock != null) {
			try {
				lock.release();
			} catch (IOException e) {
				//empty
			} finally {
				try {
					fc.close();
				} catch (IOException e) {
					//empty
				} finally {
					try {
						fs.close();
						//System.out.println("fs is closed normally");
					} catch (IOException e) {
						//empty
					} finally {
						if(lockFile.exists()) {
							lockFile.delete();
							//System.out.println("State of the lock file" + lockFile.exists());
						}
					}
				}
			}
		}
	}

    private static void readConfigFile() {
    	Properties prop = new Properties();
        FileInputStream fis;
        String levelKey = null;

		try {
			File f = new File(appDataDir, "JarManagerConfig.properties");
			if(f.exists()) {
				fis = new FileInputStream(f);
				prop.load(fis);

				levelKey = (String)prop.get("application.loglevel");

				if("DEBUG".equals(levelKey)) {
					level = Level.DEBUG;
				} else if("ALL".equals(levelKey)){
					level = Level.ALL;
				} else if("ERROR".equals(levelKey)) {
					level = Level.ERROR;
				} else if("FATAL".equals(levelKey)){
					level = Level.FATAL;
				} else if("INFO".equals(levelKey)) {
					level = Level.INFO;
				} else if("OFF".equals(levelKey)){
					level = Level.OFF;
				} else if("TRACE".equals(levelKey)){
					level = Level.TRACE;
				} else if("WARN".equals(levelKey)){
					level = Level.WARN;
				}

				//20000, 30000, 40000 or 50000
				derbyLogLevel = (String)prop.get("derby.logLevel");
				if(derbyLogLevel.isEmpty())
					derbyLogLevel= DEFAULT_DERBY_LOG_LEVEL;
				//true or false
				derbyLogSql = (String)prop.get("derby.logSql");
				if(derbyLogSql.isEmpty()){
					derbyLogSql = DEFAULT_DERBY_LOG_SQL;
				}
				locale = (String)prop.get("locale");
				if(locale != null) {
					boolean status = MessageUtil.changeLocale(new Locale(locale));
					if(!status) {
						//if failed to switch to the specified Locale, switch to ROOT Locale.
						MessageUtil.changeLocale(Locale.ROOT);
					}
				}
			} else { //JarManagerConfig.properties not found
			}
		} catch (IOException e) {
			//ignore
		}
    }

	/**
	 * Specifies Derby's log file destination and log level.
	 *  @See http://db.apache.org/derby/docs/10.5/ref/ref-single.html#rrefproper18151
	 */
	private static void setupDerbyLog() {
		System.setProperty( "derby.stream.error.file", appDataDir + File.separator + "derby.log");
		System.setProperty( "derby.stream.error.logSeverityLevel", derbyLogLevel);
		System.setProperty( "derby.language.logStatementText", derbyLogSql);
		System.setProperty( "derby.infolog.append", "true");
	}

	private static void setupLogging() {
		//We do not use Log4J's config file at all, except level (reflects the config file 'JarManagerConfig.properties')
		// How to config Log4J, See :http://d.hatena.ne.jp/y_hasegawa/20060821/1156174466 (Japanese only)
        FileAppender appender= new FileAppender();
        appender.setName("appender");
        appender.setFile(appDataDir + File.separator + "jarmanager.log");
        Layout layout = new PatternLayout("%d %-5p %C[%t](%F:%L) - %m %n");
        appender.setLayout(layout);
        appender.activateOptions();

        Logger root = Logger.getRootLogger();
		root.setLevel(level);
		root.removeAllAppenders();
        root.addAppender(appender);
        root.setAdditivity(false);
	}

    private static void makeAppDir() {
		if(!appDataDir.exists()) {
			appDataDir.mkdirs();
			if(!appDataDir.exists()) { //mkdir failed
				logger.fatal("Unable to generate the data directory. Please check the perimission. directory:" + appDataDir );
				System.exit(1);
			}
		}
	}

	private static boolean isExecuting() {
		boolean isExecuting = false;

		lockFile = new File(appDataDir, ".jarManager.lock");

		try {
			// lock file is genereated here.
			fs = new FileOutputStream(lockFile);
			fc = fs.getChannel();
			lock = fc.tryLock();

			if (lock == null) {
				isExecuting = true;
			} else {
				isExecuting = false;
			}
			//Note : we must NOT close fs and fc here. (If you close them, we always succeed to get lock)
			//We will close them at addShutdownHook() in main().
		} catch (IOException e) {
			e.printStackTrace();
		}
		return isExecuting;
	}
}
