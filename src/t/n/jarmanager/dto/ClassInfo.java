package t.n.jarmanager.dto;

public class ClassInfo {
	private String classname;
	private String jarFilename;

	public ClassInfo(String classname, String jarFilename) {
		this.classname = classname;
		this.jarFilename = jarFilename;
	}
	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getJarFilename() {
		return jarFilename;
	}

	public void setJarFilename(String jarFilename) {
		this.jarFilename = jarFilename;
	}
}
