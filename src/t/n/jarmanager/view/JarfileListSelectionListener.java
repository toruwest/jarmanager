package t.n.jarmanager.view;

import java.io.File;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JarfileListSelectionListener implements ListSelectionListener {

	IClassJarTabView view;
	private final JList<String> foundJarFileList;

	public JarfileListSelectionListener(IClassJarTabView view, JList<String> foundJarFileList) {
		this.view = view;
		this.foundJarFileList = foundJarFileList;
	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {

		if (evt.getValueIsAdjusting()){
			return;
		}

		String selectedFile = foundJarFileList.getSelectedValue();
		if (selectedFile != null) {
			String searchClass = view.getTextOnSearchClassnameTextfield();
			view.notifyListSelectionChanged(new File(selectedFile), searchClass);
		}
	}

}
