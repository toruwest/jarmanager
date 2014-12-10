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

import static t.n.jarmanager.view.IJarManagerView.MSG_CATALOG_TAB;
import static t.n.jarmanager.view.IJarManagerView.MSG_CLASS_JAR_TAB;
import static t.n.jarmanager.view.IJarManagerView.MSG_GLOBAL;
import static t.n.jarmanager.view.IJarManagerView.MSG_JAR_CLASS_TAB;

import java.text.ChoiceFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import t.n.jarmanager.resources.XMLResourceBundleControl;

public class MessageUtil {

	private static final String GLOBAL_BUNDLE_NAME        = "t.n.jarmanager.resources.messages";
	private static final String JAR_CLASS_TAB_BUNDLE_NAME = "t.n.jarmanager.resources.jar_class_tab_messages";
	private static final String CLASS_JAR_TAB_BUNDLE_NAME = "t.n.jarmanager.resources.class_jar_tab_messages";
	private static final String CATALOG_TAB_BUNDLE_NAME   = "t.n.jarmanager.resources.catalog_tab_messages";
	private final static XMLResourceBundleControl control;

	private static BundleHolder globalBundle = null;
	private static BundleHolder jarClassTabBundle = null;
	private static BundleHolder classJarTabBundle = null;
	private static BundleHolder catalogTabBundle = null;

	private static final List<String> formats;

	static {
		control = new XMLResourceBundleControl();
		formats = control.getFormats("t.n.jarmanager.resources");
		globalBundle      = new BundleHolder(GLOBAL_BUNDLE_NAME, control, formats);
		jarClassTabBundle = new BundleHolder(JAR_CLASS_TAB_BUNDLE_NAME, control, formats);
		classJarTabBundle = new BundleHolder(CLASS_JAR_TAB_BUNDLE_NAME, control, formats);
		catalogTabBundle  = new BundleHolder(CATALOG_TAB_BUNDLE_NAME, control, formats);
	}

	/**
	 * Change the Locale.
	 * @param newLocale
	 * @return true if failed.
	 */
	public static boolean changeLocale(Locale newLocale) {
		boolean b1 = globalBundle.changeLocale(newLocale);
		boolean b2 = jarClassTabBundle.changeLocale(newLocale);
		boolean b3 = classJarTabBundle.changeLocale(newLocale);
		boolean b4 = catalogTabBundle.changeLocale(newLocale);

		return(b1 || b2 || b3 || b4);
	}

	/**
	 * Get the message text.
	 * @param tabName
	 * @param key
	 * @return message string coressponding to the key. We use another files for each tab.
	 * If the key or tabName (or both) is invalid, log it and returns empty string ("").
	 *
	 */
	public static String getMessage(String tabName, String key) {
		String msg = null;
		if(MSG_GLOBAL.equals(tabName)) {
			msg = globalBundle.getString(key);
		} else if(MSG_JAR_CLASS_TAB.equals(tabName)) {
			msg = jarClassTabBundle.getString(key);
		} else if(MSG_CLASS_JAR_TAB.equals(tabName)) {
			msg = classJarTabBundle.getString(key);
		} else if(MSG_CATALOG_TAB.equals(tabName)) {
			msg = catalogTabBundle.getString(key);
		}

		return msg;
	}

	public static String getMessage(String tabName, String key, Object... args) {
		String msg = getMessage(tabName, key);

		//http://otn.oracle.co.jp/technology/global/jp/sdn/java/private/techtips/2003/tt0926.html (Japanese only)
		//original site is dead.
		if(msg.contains("|")) {
			ChoiceFormat cf = new ChoiceFormat(msg);
			MessageFormat mf = new MessageFormat("{0}");
			mf.setFormats(new Format[]{cf});
			return mf.format(args);
		}
		return MessageFormat.format(msg, args) + '\n';
	}

}
