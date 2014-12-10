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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.dto.JarInfo;
import t.n.jarmanager.util.JarFilenameUtil;

public class CatalogModel extends AbstractTableModel {
	protected static final int COL_JAR_VALID = 0;
	protected static final int COL_JAR_FILENAME = 1;
	protected static final int COL_JAR_FOLDER = 2;
	protected static final int COL_LAST_MODIFIED = 3;
	protected static final int COL_IS_SIGNED = 4;
	protected static final int COL_HAS_MAIN_CLASS = 5;
	protected static final int COL_REGIST_DATE = 6;

	private final Class[] types = new Class[] {
			java.lang.Boolean.class,//jar_valid
			java.lang.Object.class, //filename
			java.lang.Object.class,//folder
			java.lang.Object.class,//lastModified
			java.lang.Boolean.class,//isSigned
			java.lang.Boolean.class,//hasMainClass
			java.lang.Object.class //registDate
	};

	private static int[] columnWidth = {40, 210, 190, 63, 40, 40, 63};

	private List<JarInfo> objList;
	private final Map<String, JarInfo> objMap;
	private final String[] columnNames;

	public CatalogModel(String[] columnNames){
		super();
		this.columnNames = columnNames;
		objList = new ArrayList<JarInfo>();
		objMap  = new HashMap<String, JarInfo>();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return types[columnIndex];
	}

	public static int getColumnSize(){
		return columnWidth.length;
	}

	@Override
	public int getColumnCount() {
		return types.length;
	}

	@Override
	public int getRowCount() {
		if(objList != null) {
			return objList.size();
		} else {
			return 0;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public void setValueAt(Object obj, int row, int colum) {
		//
	}

	public int getColumnPrefferedSize(int i) {

		return columnWidth[i];
	}

	public void setData(List<JarInfo> objList) {

		this.objList = objList;
		for(JarInfo entry : objList) {
			objMap.put(entry.getJarFullpathname(), entry);
		}

		fireTableDataChanged();
	}

	public void addRow(JarInfo newEntry) {
		objList.add(newEntry);
		objMap.put(newEntry.getJarFullpathname(), newEntry);
		fireTableDataChanged();
	}

	public void removeRow(JarInfo removedJarInfo) {
		removeRow(removedJarInfo.getJarFullpathname());
	}

	public void removeRow(String removedJarFullPathFileName) {
		JarInfo entry = objMap.get(removedJarFullPathFileName);
		if(entry != null) {
			objList.remove(entry);
			objMap.remove(removedJarFullPathFileName);
			fireTableDataChanged();
		}
	}

	public void updateRow(String replacedFilename) {
//		throw new NotImplementedException();
	}

	public void updateData(String jarFullPathFileName, CatalogEntryStatus entryStatus) {
		if(objList != null){
			JarInfo entry = objMap.get(jarFullPathFileName);
			if(entry != null) {
				entry.setEntryStatus(entryStatus);
				int rowPos = objList.indexOf(entry);
				if(rowPos != -1) {
					objList.set(rowPos, entry);
					fireTableCellUpdated(rowPos, COL_JAR_VALID);
				}
			}
		}else{
			throw new IllegalStateException("obj not set yet.");
		}

	}

	@Override
	public Object getValueAt(int row, int col) {
		Object obj = null;
		JarInfo entry = null;
		if(row <= objList.size() && col <= getColumnCount()) {

			entry = objList.get(row);
			switch(col) {
			case COL_JAR_VALID:
				obj = entry.getEntryStatus();
				break;
			case COL_JAR_FILENAME:
				obj = entry.getJarShortFileName();
				break;
			case COL_JAR_FOLDER:
				obj = entry.getFolder();
				break;
			case COL_LAST_MODIFIED:
				obj = JarFilenameUtil.format(entry.getJarFileLastModified());
				break;
			case COL_IS_SIGNED:
				obj = entry.isSigned();
				break;
			case COL_HAS_MAIN_CLASS:
				obj = entry.hasMainClass();
				break;
			case COL_REGIST_DATE:
				obj = JarFilenameUtil.format(entry.getRegistDate());
				break;
			default:
				break;
			}

		}
		return obj;
	}
}
