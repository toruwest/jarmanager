package t.n.jarmanager.util;

import org.apache.log4j.Logger;

public class StackTraceLogger {
	public static void dumpStackTrace(Logger logger) {
		Throwable t = new Throwable();
		dumpStackTraceElement(logger, t);
	}

	private static void dumpStackTraceElement(Logger logger, Throwable t) {
		dumpStackTraceElement(logger, t.getStackTrace());
		Throwable t2 = t.getCause();
		if(t2 != null) {
			dumpStackTraceElement(logger, t2); //recursive call
		}
	}
	private static void dumpStackTraceElement(Logger logger, StackTraceElement[] ste) {
		for(StackTraceElement elem : ste ){
			logger.error(elem);
		}
	}
}
