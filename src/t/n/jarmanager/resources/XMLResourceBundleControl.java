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
