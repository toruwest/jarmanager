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

import static t.n.jarmanager.view.IJarManagerView.MSG_JAR_CLASS_TAB;

import java.io.File;
import java.util.Map;

import t.n.jarmanager.dto.CatalogRegistResult;

public class ScanResultMessageUtil {

	public static String decodeMessage(CatalogRegistResult registResult) {
		String result = null;
		switch(registResult) {
		case IO_ERROR:
		case FAILED:
			result = MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "failedJarIOError.text");
			break;
		case ADDED:
			result = MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "newlyaddJar.text");
			break;
		case JAR_NOT_EXIST:
			result = MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "failedJarNotExist.text");
			break;
		case NOT_JAR:
			result = MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "failedJarNotJar.text");
			break;
		case NOT_CHANGED:
			result = MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "registeredJar.text");
			break;
		case REPLACED:
			result = MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "updateJar.text");
			break;
		case REMOVED:
			//never happens
			break;
		case UNDEFINED:
			//bug
			result = "";
			break;

		}
		return result;
	}


	public static String genMessage(Map<File, CatalogRegistResult> statusMap) {
		int added = 0;
		int replaced = 0;
		int notChanged = 0;
		int notJar = 0;
		int failed = 0;

		String msg;
		for(File f : statusMap.keySet()) {
			CatalogRegistResult r = statusMap.get(f);
			switch(r) {
			case ADDED:
				added++;
				break;
			case NOT_CHANGED:
				notChanged++;
				break;
			case REPLACED:
				replaced++;
				break;
			case NOT_JAR:
				notJar++;
				break;
			case IO_ERROR:
				//no break
			case FAILED:
				failed++;
				break;
			default:

			}
		}
		msg = MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "addCatalogResult.text", new Object[]{added, replaced, notChanged, notJar, failed});
		return msg;
	}


}
