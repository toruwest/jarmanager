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
package t.n.jarmanager.resources;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class XMLResourceBundleControl extends ResourceBundle.Control {
    public static final String XML = "xml";
    public static final List<String> FORMAT_XML
	    = Collections.unmodifiableList(Arrays.asList(XML));

    @Override
    public List<String> getFormats(String baseName) {
        if (baseName == null)
            throw new NullPointerException();
        // We use XML format only.
        return FORMAT_XML;
    }

    /**
     * Returns ROOT locale.
     */
    @Override
    public Locale getFallbackLocale(String baseName, Locale locale) {
    	if(locale == null)return Locale.ROOT;
    	String loc = locale.toString().toLowerCase();
    	if("en_us".equals(loc) || "en".equals(loc)) {
    		return Locale.ROOT;
    	} else {
    		return super.getFallbackLocale(baseName, locale);
    	}
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format,
    		ClassLoader loader, boolean reload)
    		throws IllegalAccessException, InstantiationException, IOException {

        if (baseName == null || locale == null || format == null || loader == null) {
            throw new NullPointerException();
        }

        ResourceBundle bundle = null;
        if (format.equals(XML)) {
        	while(true) {
        		String bundleName = toBundleName(baseName, locale);
        		String resourceName = toResourceName(bundleName, format);
        		InputStream stream = null;
        		if (reload) {
        			URL url = loader.getResource(resourceName);
        			if (url != null) {
        				URLConnection connection = url.openConnection();
        				if (connection != null) {
        					connection.setUseCaches(false);
        					stream = connection.getInputStream();
        				}
        			}
        		} else {
        			stream = loader.getResourceAsStream(resourceName);
        		}
        		if (stream != null) {
        			BufferedInputStream bis = new BufferedInputStream(stream);
        			bundle = new XMLResourceBundle(locale, bis);
        			bis.close();
        			break;
        		} else {
        			locale = getFallbackLocale(baseName, locale);
        			if(locale != null) {
        				continue;
        			} else {
        				break;
        			}
        		}
        	}
        }
        return bundle;
    }
}
