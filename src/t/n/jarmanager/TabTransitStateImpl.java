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
package t.n.jarmanager;

import java.io.File;


public class TabTransitStateImpl implements ITabTransitState {

	private boolean fromCatalogTabPopupMenu;
	private File targetJarFile;
	private String candidateJarFiles;

	public TabTransitStateImpl() {
		 fromCatalogTabPopupMenu = false;
		 candidateJarFiles = "";
	}

	@Override
	public void setRegistCandidateJarFiles(String candidateJarFiles) {
		fromCatalogTabPopupMenu = false;
		this.candidateJarFiles = candidateJarFiles;
	}

	@Override
	public void setFromCatalogTabPopupMenu(File targetJarFile) {
		fromCatalogTabPopupMenu = true;
		this.targetJarFile = targetJarFile;
		candidateJarFiles = "";
	}
	@Override
	public File getTargetJarFile() {
		return targetJarFile;
	}

	@Override
	public boolean isFromCatalogTabPopupMenu() {
		return fromCatalogTabPopupMenu;
	}

	@Override
	public void clearStatus() {
		fromCatalogTabPopupMenu = false;
		targetJarFile = null;
		candidateJarFiles = "";
	}

	@Override
	public boolean hasUnregisteredCandidateJarFiles() {
		return candidateJarFiles.length() > 0;
	}

	@Override
	public String getUnregisteredCandidateJarFiles() {
		return candidateJarFiles;
	}
}
