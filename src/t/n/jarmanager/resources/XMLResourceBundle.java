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
