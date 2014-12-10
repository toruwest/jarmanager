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
