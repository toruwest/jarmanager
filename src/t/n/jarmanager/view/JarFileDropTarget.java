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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class JarFileDropTarget extends DropTargetAdapter {
	private static final Logger logger = Logger.getLogger(JarFileDropTarget.class);
	private List<File> jarFiles;
	IDropEventListener dropEventListener;
	public JarFileDropTarget(IDropEventListener dropEventListener) {
		this.dropEventListener = dropEventListener;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent dtde) {

		try {
			Transferable transfer = dtde.getTransferable();
			if (transfer
					.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				jarFiles = (List<File>) (transfer
						.getTransferData(DataFlavor.javaFileListFlavor));
				StringBuffer sb = new StringBuffer();
				for (File f : jarFiles) {
					if (f.toString().toLowerCase().endsWith(".jar")) { 
						sb.append(f.toString());
						sb.append(" "); 
					}
				}

				dropEventListener.dropFilenames(sb.toString());
			}
		} catch (Exception e) {
			logger.log(Level.INFO, "" , e);
		}	
	}

}
