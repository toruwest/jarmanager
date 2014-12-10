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

import static t.n.jarmanager.view.IJarManagerView.MSG_CATALOG_TAB;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import t.n.jarmanager.util.MessageUtil;

public class JarSearchActionAdaptor {
	private static final Logger logger = Logger.getLogger(JarSearchActionAdaptor.class);

	private final IJarManagerView mainView;
	private List<Integer> foundJarnameList;
	private int foundJarnameListIndex;
	private final CatalogModel catalogModel;
	private List<Integer> foundJarnameViewList;

	private final ICatalogTabView catalogView;

	public JarSearchActionAdaptor(IJarManagerView mainView, ICatalogTabView catalogView, CatalogModel catalogModel) {
		this.mainView = mainView;
		this.catalogView = catalogView;
		this.catalogModel = catalogModel;
	}

	public void notifyTableHeaderClicked() {
		foundJarnameViewList = catalogView.convertToViewIndexInCatalog(foundJarnameList);
		dumpIndex();
	}

	public void startSearch(String jarToSearch) {
		foundJarnameList = new ArrayList<Integer>();
		jarToSearch = jarToSearch.trim().toLowerCase();
		for (int i = 0; i < catalogModel.getRowCount(); i++) {
			String jarName = (String) catalogModel.getValueAt(i, CatalogModel.COL_JAR_FILENAME);
			if (jarName.toLowerCase().contains(jarToSearch)) {
				foundJarnameList.add(i);
			}
		}
		foundJarnameViewList = catalogView.convertToViewIndexInCatalog(foundJarnameList);
		dumpIndex();
		int count = foundJarnameViewList.size();
		if (count > 0) {
			mainView.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarname_found", count));

			catalogView.changeSelectionCatalogTable(foundJarnameViewList.get(0),
					CatalogModel.COL_JAR_FILENAME, false, false);
			foundJarnameListIndex = 0;

			if (count == 1) {
				catalogView.setEnabledJarSearchNextButton(false);
				catalogView.setEnabledJarSearchPrevButton(false);
			} else {
				updateJarSearchButtonState(foundJarnameListIndex);
			}
		} else {
			mainView.setMessage(MessageUtil.getMessage(MSG_CATALOG_TAB, "jarname_not_found"));
		}
	}

	private void dumpIndex() {
		if(foundJarnameList != null) {
			for(int i : foundJarnameList){
				logger.debug("row pos(table model):" + i);
			}
		}
		if(foundJarnameViewList != null){
			for(int i : foundJarnameViewList){
				logger.debug("row pos(view):" + i);
			}
		}
	}

	public void showNext(){
		if (foundJarnameListIndex + 1 <= foundJarnameViewList.size()) {
		foundJarnameListIndex++;
		int rowPos = foundJarnameViewList.get(foundJarnameListIndex);
		logger.debug("move to row pos:" + rowPos);
		catalogView.changeSelectionCatalogTable(rowPos, CatalogModel.COL_JAR_FILENAME, false, false);

		updateJarSearchButtonState(foundJarnameListIndex);
	}
}
	public void showPrev(){
		if (foundJarnameListIndex - 1 >= 0) {
			foundJarnameListIndex--;
			int rowPos = foundJarnameViewList.get(foundJarnameListIndex);
			logger.debug("move to row pos:" + rowPos);
			catalogView.changeSelectionCatalogTable(rowPos, CatalogModel.COL_JAR_FILENAME, false, false);

			updateJarSearchButtonState(foundJarnameListIndex);
		}
	}

	public void clearSearch() {
		catalogView.clearJarFilenameSearch();
	}

	private void updateJarSearchButtonState(int foundJarnameListIndex) {
		int count = foundJarnameList.size();
		if (foundJarnameListIndex == 0) {
			catalogView.setEnabledJarSearchPrevButton(false);
			if (count > 0) {
				catalogView.setEnabledJarSearchNextButton(true);
			} else {
				catalogView.setEnabledJarSearchNextButton(false);
			}
		} else if (0 < foundJarnameListIndex
				&& foundJarnameListIndex < count - 1) {
			catalogView.setEnabledJarSearchNextButton(true);
			catalogView.setEnabledJarSearchPrevButton(true);
		} else if (foundJarnameListIndex == count - 1) {
			catalogView.setEnabledJarSearchNextButton(false);
			catalogView.setEnabledJarSearchPrevButton(true);
		}
	}
}
