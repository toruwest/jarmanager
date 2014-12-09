package t.n.jarmanager.util;

public class SliceUtil {

	/**
	 * Database column is limited to fixed length. We want to store more than that size,
	 * so we split the content into multi lines.
	 */
	public static String[] slice(String contents, int CONTENT_COLUMN_SIZE) {
		int columnCount = contents.length() / CONTENT_COLUMN_SIZE;
		String[] contentArray = new String[columnCount + 1];

		int beginIndex = 0;
		int endIndex = 0;
		for(int i = 0; i <= columnCount; i++) {
			if(beginIndex + CONTENT_COLUMN_SIZE < contents.length()) {
				endIndex = beginIndex + CONTENT_COLUMN_SIZE;
			}else {
				endIndex = beginIndex + contents.length() - (CONTENT_COLUMN_SIZE * i) ;
			}

			contentArray[i] = contents.substring(beginIndex, endIndex);
			beginIndex += CONTENT_COLUMN_SIZE;
		}
		return contentArray;
	}

}
