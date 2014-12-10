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

import org.apache.log4j.Logger;

import t.n.jarmanager.dto.CatalogRegistStatus;
import t.n.jarmanager.dto.JarFilesCatalogRegistStatus;
import t.n.jarmanager.view.IJarManagerView;

public class RegistStatusMessageUtil {
	private static final Logger logger = Logger.getLogger(RegistStatusMessageUtil.class);

	public static boolean showMessage(IJarManagerView mainView, Map<File, CatalogRegistStatus> statusMap) {
		JarFilesCatalogRegistStatus status = checkResult(statusMap);
		switch(status.getStatus()) {
		case allNew:
			mainView.setAddToCatalogButtonEnabled(true);
			mainView.setMessage(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "all_new.text"));
			break;
		case allUpdated: //no break
		case allNewOrUpdated:
				mainView.setAddToCatalogButtonEnabled(true);
				mainView.setMessage(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "all_new_or_updated.text"));
				break;
		case partialRegistered:
				mainView.setAddToCatalogButtonEnabled(true);
				mainView.setMessage(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "catalogStatus.text", status.getNewJarfileCount(),
						status.getReplacedJarfileCount(), status.getAlreadyRegisteredJarFileCount(), status.getNotJarFileCount(), status.getIoErrorCount()));
				break;
		case allRegistered:
				mainView.setAddToCatalogButtonEnabled(false);
				mainView.setMessage(MessageUtil.getMessage(MSG_JAR_CLASS_TAB, "registeredJar.text"));
				break;
		case unknown:
			//bug, no break;
		default:
			//bug
			logger.fatal("MultiJarFilesCatalogRegistStatu.getStatus() is " + status.getStatus().name(), new Throwable());
		}
		return false;
	}

	private static JarFilesCatalogRegistStatus checkResult(Map<File, CatalogRegistStatus> statusMap) {
		int newJarfileCount = 0;
		int replaceJarFileCount = 0;
		int alreadyRegisteredJarFileCount = 0;
		int notJarFileCount = 0;
		int notExistFileCount = 0;
		int ioErrorCount = 0;

		for(File f : statusMap.keySet()) {
			CatalogRegistStatus status = statusMap.get(f);

			switch(status){
			case NEW_JAR:
				newJarfileCount++;
				break;
			case REGISTERED_JAR:
				alreadyRegisteredJarFileCount++;
				break;
			case UPDATED_JAR:
				replaceJarFileCount++;
				break;
			case NOT_JAR:
				notJarFileCount++;
				break;
			case NOT_EXIST:
				notExistFileCount++;
				break;
			case IO_ERROR:
				ioErrorCount++;
				break;
			case UNKNOWN:
				//no break
			default:
			}
		}

		JarFilesCatalogRegistStatus result = new JarFilesCatalogRegistStatus(newJarfileCount, replaceJarFileCount, alreadyRegisteredJarFileCount,
			notJarFileCount, notExistFileCount, ioErrorCount);
		return result;

	}
}
