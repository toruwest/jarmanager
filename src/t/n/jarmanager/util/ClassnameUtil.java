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
