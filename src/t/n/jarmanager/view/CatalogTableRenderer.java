package t.n.jarmanager.view;

import static t.n.jarmanager.view.IJarManagerView.MSG_CATALOG_TAB;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import t.n.jarmanager.JarManagerApp;
import t.n.jarmanager.dto.CatalogEntryStatus;
import t.n.jarmanager.util.MessageUtil;

public class CatalogTableRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1145484877262909591L;

	private final JCheckBox checked = new JCheckBox("", true);
	private final Icon redIcon ;
	private final JCheckBox redCheck;

	private final Icon yellowIcon ;
	private final JCheckBox yellowCheck;

	private final JLabel label = new JLabel();

	private final ResourceMap resourceMap;

	public CatalogTableRenderer() {
		super();

		resourceMap = Application.getInstance(t.n.jarmanager.JarManagerApp.class).getContext().getResourceMap(JarManagerApp.class);
		redIcon = resourceMap.getIcon("redIcon.icon");
		yellowIcon = resourceMap.getIcon("yellowIcon.icon");

		redCheck = new JCheckBox(redIcon);
		yellowCheck = new JCheckBox(yellowIcon);

	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component comp = null;
		Color selectedBackgroundColor = table.getSelectionBackground();
		Color defaultBackgroundColor = table.getBackground();

		if(column == 0) {
			switch((CatalogEntryStatus)value) {
			case NORMAL:
				comp = checked;
				((JComponent) comp).setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jar.normal"));
				break;
			case DELETED:
				comp = redCheck;
				((JComponent) comp).setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jar.deleted"));
				break;
			case REPLACED:
				comp = yellowCheck;
				((JComponent) comp).setToolTipText(MessageUtil.getMessage(MSG_CATALOG_TAB, "jar.replaced"));
				break;
			}
		} else if(column == 4) {
			label.setText((Boolean)value?"Yes":"No");
			label.setOpaque(true);
			comp = label;
		} else if(column == 5) {
			label.setText((Boolean)value?"Yes":"No");
			label.setOpaque(true);
			comp = label;
		} else {
			label.setText(value.toString());
			comp = label;
			((JComponent) comp).setToolTipText(resourceMap.getString("jar.normal", ""));
		}
		if(isSelected) {
			comp.setBackground(selectedBackgroundColor);
		} else {
			comp.setBackground(defaultBackgroundColor);
		}
		return comp;
	}

}
