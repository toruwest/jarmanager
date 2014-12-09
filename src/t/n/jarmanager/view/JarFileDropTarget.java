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
