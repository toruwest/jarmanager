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
package t.n.jarmanager.dto;


public final class JarFilesCatalogRegistStatus {

	private final int newJarfileCount;
	private final int replacedJarfileCount;
	private final int alreadyRegisteredJarFileCount;
	private final int notJarFileCount;
	private final int notExistFileCount;
	private final int ioErrorCount;

	public enum Status {allNew, allUpdated, allNewOrUpdated, allRegistered, partialRegistered, unknown, };

	public JarFilesCatalogRegistStatus(int newJarfileCount, int replacedJarfileCount,
			int alreadyRegisteredJarFileCount, int notJarFileCount, int notExistFileCount, int ioErrorCount) {

		this.newJarfileCount = newJarfileCount;
		this.replacedJarfileCount = replacedJarfileCount;
		this.alreadyRegisteredJarFileCount = alreadyRegisteredJarFileCount;
		this.notJarFileCount = notJarFileCount;
		this.notExistFileCount = notExistFileCount;
		this.ioErrorCount = ioErrorCount;
	}

	public Status getStatus(){
		Status status = Status.unknown;
		if (newJarfileCount > 0 && replacedJarfileCount == 0 && alreadyRegisteredJarFileCount == 0) {
			return Status.allNew;
		} else if (newJarfileCount == 0 && replacedJarfileCount > 0 && alreadyRegisteredJarFileCount == 0) {
			return Status.allUpdated;
		} else if (newJarfileCount + replacedJarfileCount > 0 && alreadyRegisteredJarFileCount == 0) {
			return Status.allNewOrUpdated;
		} else if (newJarfileCount + replacedJarfileCount > 0 && alreadyRegisteredJarFileCount > 0) {
			return Status.partialRegistered;
		} else if (newJarfileCount + replacedJarfileCount == 0 && alreadyRegisteredJarFileCount > 0) {
			return Status.allRegistered;
		}

		return status;
	}

	public int getNewJarfileCount() {
		return newJarfileCount;
	}

	public int getReplacedJarfileCount() {
		return replacedJarfileCount;
	}

	public int getAlreadyRegisteredJarFileCount() {
		return alreadyRegisteredJarFileCount;
	}

	public int getNotJarFileCount() {
		return notJarFileCount;
	}

	public int getNotExistFileCount() {
		return notExistFileCount;
	}

	public int getIoErrorCount() {
		return ioErrorCount;
	}

}
