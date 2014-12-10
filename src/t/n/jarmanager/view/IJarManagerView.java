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
package t.n.jarmanager.view;

import java.awt.Container;
import java.util.List;

import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.JarInfo;


public interface IJarManagerView {

	public final int JAR_CLASS_TAB = 0;
	public final int CLASS_JAR_TAB = 1;
	public final int CATALOG_TAB = 2;

	static final String MSG_GLOBAL        = "global";
	static final String MSG_JAR_CLASS_TAB = "jarClassTab";
	static final String MSG_CLASS_JAR_TAB = "classJarTab";
	static final String MSG_CATALOG_TAB   = "catalog";

	public abstract void setValueProgressBar(final int count);
	public abstract void setValueProgressBar(int totalCount, int count);
	public abstract void setMessage(String message);
	public abstract void enableOperation();
	public abstract void disableOperation();
	public abstract void selectTab(int tabIndex);
	public abstract void startProgressBar(boolean isIndeterminate);
	public abstract void finishProgressBar();
	public abstract Container getContainer();

	public abstract void setTextOnJarContentOfClassJarTab(String string);
	public abstract void setTextOnJarContentOfJarClassTab(String content);
	public abstract void appendTextOnJarContentOfJarClassTab(String string);

	public abstract void setupButtonOnAddJarCompleted();
	public abstract void setAddToCatalogButtonEnabled(boolean b);
	public abstract void selectJarContentOfJarClassTab(int i, int j);
	public abstract void setupButtonOnScanCompleted();
	public abstract void setupButtonOnVerifyCompleted();
	public abstract void setCatalogUpdateButtonEnabled(boolean b);

	public abstract void setupButtonOnUpdateCompleted();
	public abstract void setupButtonOnFindClassCompleted();


	public abstract void addRowCatalogModel(JarInfo jarInfo);
	public abstract void updateDataCatalogModel(String path, CatalogEntryStatus replaced);
	public abstract void removeRowCatalogModel(JarInfo jarInfo);
	public abstract void updateCatalogTable(List<JarInfo> entryList);

	public abstract void addJarFileListModel(List<String> result);
	public abstract void clearJarFileListModel();
	public abstract void setHilightFoundClass(List<Integer> indexList, int length);


}
