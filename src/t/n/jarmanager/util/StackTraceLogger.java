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
