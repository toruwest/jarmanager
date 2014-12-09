package t.n.jarmanager.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import t.n.jarmanager.resources.XMLResourceBundleControl;

public class BundleHolder {
	private static final Logger logger = Logger.getLogger(BundleHolder.class);
	private static final String GET_MESSAGE_FAILED = "Get message text from resource-bundle failed. resource bundle:";

	private final String bundleName;
	private Locale locale;
	//'bundle' will be updated if changeLocale() is invoked. This is optional (this should not be invoked).
	//'bundle' is to be fixed when getMessage() invoked.
	private ResourceBundle bundle;
	private final XMLResourceBundleControl control;
	private final List<String> formats;

	private final Set<String> allKeys;
	private boolean isReadBundlefailed;

	public BundleHolder(String bundleName, XMLResourceBundleControl control, List<String> formats) {
		this.bundleName = bundleName;
		this.control = control;
		this.formats = formats;
		allKeys = new HashSet<String>();

		//usedKeys = new HashSet<String>();
	}

	public String getString(String key) {
		if(bundle == null) {
			readResourceBundle(Locale.getDefault());
		}
		String msg = null;
		if(bundle.containsKey(key)) {
			msg = bundle.getString(key);
		} else {
			msg = "";
			logger.log(Level.FATAL, GET_MESSAGE_FAILED + bundleName + ", key:" + key);
			StackTraceLogger.dumpStackTrace(logger);
		}
		return msg;
	}

	/**
	 * @param newLocale
	 * @return true if failed to retrieve resource bundle
	 */
	public boolean changeLocale(Locale newLocale) {
		readResourceBundle(newLocale);
		return isReadBundlefailed;
	}

	private void readResourceBundle(Locale newLocale) {
		isReadBundlefailed = false;
		try {
			if(bundle == null) {
				bundle = control.newBundle(bundleName, newLocale, formats.get(0), BundleHolder.class.getClassLoader(), false);
				locale = newLocale;
			} else if(newLocale != null) {
				//change the locale, if the current locale and new locale is different.
				//if failed to retrieve resource bundle with new local, then switch to ROOT locale.
				if(!newLocale.equals(bundle.getLocale()) ) {
					bundle = control.newBundle(bundleName, newLocale, formats.get(0), BundleHolder.class.getClassLoader(), false);
					locale = newLocale;
				}
			}
			allKeys.clear();
			allKeys.addAll(bundle.keySet());
		} catch (IllegalAccessException e) {
			isReadBundlefailed = true;
			logger.error("Read resource bundle failed:" + bundleName + ":" + newLocale.getDisplayName(), e);
		} catch (InstantiationException e) {
			isReadBundlefailed = true;
			logger.error("Read resource bundle failed:" + bundleName + ":" + newLocale.getDisplayName(), e);
		} catch (IOException e) {
			isReadBundlefailed = true;
			logger.error("Read resource bundle failed:" + bundleName + ":" + newLocale.getDisplayName(), e);
		}
	}

}
