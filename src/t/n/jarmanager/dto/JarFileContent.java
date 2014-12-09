package t.n.jarmanager.dto;

import java.util.List;

public class JarFileContent {
	String contents;
	List<Integer> foundClassList;
	
	public JarFileContent(String contents, List<Integer> foundClassList) {
		this.contents = contents;
		this.foundClassList = foundClassList;
	}

	public String getContents() {
		return contents;
	}

	public List<Integer> getFoundClassIndexList() {
		return foundClassList;
	}

}
