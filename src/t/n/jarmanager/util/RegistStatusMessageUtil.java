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
