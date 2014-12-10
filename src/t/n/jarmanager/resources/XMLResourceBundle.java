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

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class XMLResourceBundle extends ResourceBundle {
    private final Properties properties;
	private final Locale locale;

    public XMLResourceBundle(Locale locale, InputStream stream) throws IOException {
    	//Read XML file by using Properties class.
    	this.locale = locale;
        properties = new Properties();
        properties.loadFromXML(stream);
     }

    @Override
    public Object handleGetObject(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return properties.get(key);
    }
    @Override
    @SuppressWarnings("unchecked")
    public Enumeration<String> getKeys() {
        return (Enumeration<String>)properties.propertyNames();
    }

    @Override
    public Locale getLocale() {
    	return locale;
    }
}
