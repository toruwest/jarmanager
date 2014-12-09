package t.n.jarmanager.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileFilter;

public class JarFileFilter extends FileFilter {

	List<String> extension = null;

	public JarFileFilter() {
		extension = new ArrayList<String>();
	}

	public JarFileFilter(String string) {
		this();
		extension.add(string);
	}

	@Override
	public boolean accept(File file) {
		if (file != null) {
			if (file.isDirectory()) {
				return true;
			}

			for (String ext : extension) {
				String s = file.toString();
				if(s != null && !s.isEmpty()) {
					s = s.toLowerCase();
					if (s.endsWith(ext)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected String getExt(File f) {
		String name = f.getName();
		return name.substring(name.indexOf('.'), name.length());
	}

	@Override
	public String getDescription() {
		return "Just JAR files";

	}

	public void addExtension(String string) {
		extension.add(string);
	}
}
