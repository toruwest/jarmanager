package t.n.jarmanager.view;

import java.io.File;

public interface IClassJarTabView {
	public abstract String getTextOnSearchClassnameTextfield();
	public abstract void notifyListSelectionChanged(File file, String searchClass);

}
