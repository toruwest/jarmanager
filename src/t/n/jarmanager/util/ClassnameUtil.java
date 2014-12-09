package t.n.jarmanager.util;

public class ClassnameUtil {

	public static String extractSimpleClassname(String fullQualifiedClassname) {
		int index1 = -1;
		int index2 = -1;
		if(fullQualifiedClassname != null && !fullQualifiedClassname.isEmpty()) {
			index1 = fullQualifiedClassname.indexOf('.');
			index2 = fullQualifiedClassname.lastIndexOf('.');

			if(index2 == index1) {
				return fullQualifiedClassname;
			} else {
				do {
					index2--;
				} while(0 < index2 && fullQualifiedClassname.charAt(index2) != '.');
			}
		}
		return fullQualifiedClassname.substring(index2 + 1);
	}
}
